def build_prompt(
    problem_title: str,
    problem_description: str,
    parsed_diagram: str,
    writeup: str,
    rubric: str | None = None,
) -> str:
    """Construct a clean, structured system design evaluation prompt."""
    rubric_section = ""
    if rubric:
        rubric_section = f"""
### Rubric Criteria
The submission should be evaluated against these guidelines:
{rubric}
"""

    return f"""You are a senior system design interviewer. Evaluate the user's High-Level Design (HLD) submission.

## Problem Statement
**Title:** {problem_title}
**Description:** {problem_description}
{rubric_section}
## User's Submission

### 1. HLD Diagram Structure (Parsed Nodes & Paths)
{parsed_diagram}

### 2. Design Explanation Writeup
{writeup}

## Evaluation Request
Analyze the design choice, components, flow, and completeness.
Produce a structured, brief, and actionable evaluation.
Your response MUST strictly use the following markdown format:

### ✅ What You Did Well
- (2-3 brief bullet points detailing strengths)

### ⚠️ Areas for Improvement
- (2-3 brief bullet points detailing weaknesses or gaps)

### 💡 Suggestions & Recommendations
- (2-3 brief bullet points of concrete steps for improvement)

### 📝 Final Summary
(A brief 2-sentence summary of the design's overall quality and viability.)
"""
