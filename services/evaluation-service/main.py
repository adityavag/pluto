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

{
    "parsed_diagram": "Components:\n  - [RECTANGLE] User Client App\n  - [RECTANGLE] API Gateway\n  - [RECTANGLE] Rate Limiter Service\n(Token Bucket)\n  - [ELLIPSE] Redis Cluster\n(Token Counts)\n\nNotes:\n  - Check Limit\n\nConnections:\n  - User Client App → API Gateway\n  - API Gateway → Rate Limiter Service\n(Token Bucket)\n  - Rate Limiter Service\n(Token Bucket) → Redis Cluster\n(Token Counts)",
    "feedback": "### ✅ What You Did Well\n\n1. **Component Design**: The HLD diagram clearly outlines the components involved in the rate limiting system, including the User Client App, API Gateway, Rate Limiter Service, and Redis Cluster.\n2. **Flow Diagram**: The flow diagram shows how user requests are routed through the API Gateway to the Rate Limiter Service, with the Token Bucket algorithm used for rate limiting.\n3. **Data Storage**: The use of a distributed Redis cluster ensures that the rate limit counts are stored efficiently across multiple nodes.\n\n### ⚠️ Areas for Improvement\n\n1. **Concurrency Handling**: While the diagram mentions handling concurrency and race conditions, it does not provide specific details on how these are managed in the Rate Limiter Service.\n2. **Error Handling**: The HLD does not specify error handling mechanisms for cases where the rate limit check fails or other issues occur.\n\n### 💡 Suggestions & Recommendations\n\n1. **Concurrency Management**: Implementing a locking mechanism within the Rate Limiter Service to handle concurrent requests effectively can prevent race conditions and ensure that each user's rate limits are accurately tracked.\n2. **Error Handling**: Enhance error handling in the Rate Limiter Service to provide meaningful feedback to the API Gateway when issues arise, such as insufficient tokens or invalid request parameters.\n\n### 📝 Final Summary\n\nThe HLD provides a clear and concise design for a distributed rate limiting system using the Token Bucket algorithm. The use of Redis ensures high availability and scalability. However, the current implementation does not address concurrency management and error handling effectively. By incorporating these improvements, the design can be made more robust and reliable."
}