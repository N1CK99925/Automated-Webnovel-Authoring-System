from fastapi import FastAPI
from pydantic import BaseModel
import requests
import json

app = FastAPI()

class ChapterRequest(BaseModel):
    brief: str
    max_tokens: int = 4000
    MIN_WORDS: int = 1500

OLLAMA_URL = "http://localhost:11434/api/generate"

@app.post("/generate")
def generate_chapter(req: ChapterRequest):
   
    payload = {
        "model": "llama3",
        "prompt": f"""
You are writing a webnovel chapter. Write a coherent story based on this brief:
{req.brief}

The chapter must be at least {req.MIN_WORDS} words. If your output is too short, continue until reaching the minimum word count.
""",
        "num_predict": req.max_tokens
    }

    response = requests.post(OLLAMA_URL, json=payload, stream=True)
    story = ""

    for line in response.iter_lines():
        if line:
            try:
                data = line.decode("utf-8")
                obj = json.loads(data)  
                if "response" in obj:
                    story += obj["response"]
            except Exception as e:
                print("Error parsing line:", e)

    story = story.strip()

   
    extraction_payload = {
        "model": "llama3",
        "prompt": f""" 
Analyze the following chapter text. 

1. Extract all **characters** (names + short description).  
2. Extract all **lore elements** (clans, families, locations, artifacts, important concepts).  

Return ONLY valid JSON in this format:

{{
  "characters": [
    {{"name": "...", "clan":"...","role": "...", "status":"Alive/Deceased" ,"Last Appearance": "...","details":"..."}}
  ],
  "lore": [
    {{"name": "...", "type": "clan/location/artifact/etc", "details": "..."}}
  ]
}}

Chapter Text:
{story}
""",
        "num_predict": 500
    }

    extraction_response = requests.post(OLLAMA_URL, json=extraction_payload)
    try:
        metadata = extraction_response.json()
    except Exception as e:
        print("Error parsing metadata:", e)
        metadata = {}

    
    return {"text": story, "metadata": metadata}
