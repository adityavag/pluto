import os
import logging
from typing import Any, Optional
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
import httpx
from dotenv import load_dotenv

from parser import parse_excalidraw
from prompt import build_prompt

# Load environment configuration
load_dotenv()

OLLAMA_BASE_URL = os.getenv("OLLAMA_BASE_URL", "http://localhost:11434")
OLLAMA_MODEL = os.getenv("OLLAMA_MODEL", "llama3.1:8b")

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("evaluation-service")

app = FastAPI(
    title="Pluto Evaluation Service",
    description="Stateless service to evaluate system design solutions using local LLM",
    version="1.0.0"
)

# Enable CORS for all origins in development
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


class EvaluationRequest(BaseModel):
    problem_title: str = Field(..., description="Title of the design problem")
    problem_description: str = Field(..., description="Description of the design problem")
    excalidraw_json: dict[str, Any] = Field(..., description="Raw Excalidraw JSON diagram export")
    writeup: str = Field(..., description="Text explanation of the solution")
    rubric: Optional[str] = Field(None, description="Optional evaluation rubric string")


class EvaluationResponse(BaseModel):
    parsed_diagram: str
    feedback: str


@app.post("/evaluate", response_model=EvaluationResponse)
async def evaluate_solution(request: EvaluationRequest):
    """Parses a system design diagram and writeup, and evaluates it using local Ollama."""
    try:
        # 1. Parse Excalidraw JSON diagram
        logger.info("Parsing Excalidraw diagram...")
        parsed_diagram = parse_excalidraw(request.excalidraw_json)

        # 2. Build system evaluation prompt
        logger.info("Building evaluation prompt...")
        prompt = build_prompt(
            problem_title=request.problem_title,
            problem_description=request.problem_description,
            parsed_diagram=parsed_diagram,
            writeup=request.writeup,
            rubric=request.rubric,
        )

        # 3. Call local Ollama LLM endpoint
        logger.info(f"Calling local Ollama LLM ({OLLAMA_MODEL})...")
        ollama_url = f"{OLLAMA_BASE_URL}/api/generate"
        
        async with httpx.AsyncClient(timeout=httpx.Timeout(180.0)) as client:
            response = await client.post(
                ollama_url,
                json={
                    "model": OLLAMA_MODEL,
                    "prompt": prompt,
                    "stream": False,
                    "options": {
                        "temperature": 0.3,
                        "num_predict": 2048,
                    }
                }
            )
            response.raise_for_status()
            feedback = response.json().get("response", "").strip()

        if not feedback:
            raise HTTPException(
                status_code=500, 
                detail="Ollama LLM returned an empty response."
            )

        logger.info("Evaluation completed successfully.")
        return EvaluationResponse(
            parsed_diagram=parsed_diagram,
            feedback=feedback
        )

    except httpx.HTTPError as exc:
        logger.error(f"HTTP error calling local Ollama service: {exc}")
        raise HTTPException(
            status_code=502, 
            detail=f"Ollama server connection error: {str(exc)}"
        )
    except Exception as exc:
        logger.error(f"Unexpected error during evaluation: {exc}")
        raise HTTPException(
            status_code=500, 
            detail=f"Failed to process evaluation request: {str(exc)}"
        )


@app.get("/health")
def health_check():
    return {"status": "ok", "service": "evaluation-service"}


if __name__ == "__main__":
    import uvicorn
    port = int(os.getenv("PORT", "8084"))
    uvicorn.run("main:app", host="0.0.0.0", port=port, reload=True)