from fastapi import FastAPI
from pydantic import BaseModel
app = FastAPI()

class ChapterRequest(BaseModel):
    brief: str

@app.post("/generate")
def generate_chapter(req: ChapterRequest):
    # Later: call LLM
    return {"text": f"Draft chapter based on brief: {req.brief}"}