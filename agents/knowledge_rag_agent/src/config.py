import os
from pydantic import BaseModel
from typing import Optional
from dotenv import load_dotenv

# 加载 .env 文件
load_dotenv()

def get_db_url():
    db_type = os.getenv("DB_TYPE", "sqlite")
    if db_type == "postgres":
        return f"postgresql+asyncpg://{os.getenv('DB_USER')}:{os.getenv('DB_PASSWORD')}@{os.getenv('DB_HOST')}:{os.getenv('DB_PORT')}/{os.getenv('DB_NAME')}"
    return os.getenv("DB_URL", "sqlite:///./rag.db")

def get_vector_db_url():
    vector_db_type = os.getenv("VECTOR_DB_TYPE", "sqlite")
    if vector_db_type == "postgres":
        return f"postgresql+asyncpg://{os.getenv('VECTOR_DB_USER')}:{os.getenv('VECTOR_DB_PASSWORD')}@{os.getenv('VECTOR_DB_HOST')}:{os.getenv('VECTOR_DB_PORT')}/{os.getenv('VECTOR_DB_NAME')}"
    return os.getenv("VECTOR_DB_URL", "sqlite:///./rag_vector.db")

class Settings(BaseModel):
    app_name: str = os.getenv("APP_NAME", "knowledge_rag_agent")
    environment: str = os.getenv("ENVIRONMENT", "dev")
    host: str = os.getenv("HOST", "0.0.0.0")
    port: int = int(os.getenv("PORT", 8000))
    db_url: str = get_db_url()
    embedding_api_url: Optional[str] = os.getenv("EMBEDDING_API_URL")
    llm_api_url: Optional[str] = os.getenv("LLM_API_URL")
    permission_api_url: Optional[str] = os.getenv("PERMISSION_API_URL")
    vector_db_url: str = get_vector_db_url()
    log_level: str = os.getenv("LOG_LEVEL", "info")

settings = Settings() 