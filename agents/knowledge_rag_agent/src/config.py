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
    # nacos 配置
    nacos_server_addr: str = os.getenv("NACOS_SERVER_ADDR", "127.0.0.1:8848")
    nacos_namespace: Optional[str] = os.getenv("NACOS_NAMESPACE", "public")
    nacos_username: Optional[str] = os.getenv("NACOS_USERNAME")
    nacos_password: Optional[str] = os.getenv("NACOS_PASSWORD")
    nacos_group: str = os.getenv("NACOS_GROUP", "DEFAULT_GROUP")
    nacos_cluster: str = os.getenv("NACOS_CLUSTER", "DEFAULT")
    nacos_service_name: str = os.getenv("NACOS_SERVICE_NAME", "knowledge-rag-agent")
    nacos_weight: float = float(os.getenv("NACOS_WEIGHT", 1.0))
    nacos_enable: bool = os.getenv("NACOS_ENABLE", "true").lower() == "true"
    nacos_healthy: bool = os.getenv("NACOS_HEALTHY", "true").lower() == "true"
    nacos_ephemeral: bool = os.getenv("NACOS_EPHEMERAL", "true").lower() == "true"
    nacos_access_key: Optional[str] = os.getenv("NACOS_ACCESS_KEY")
    nacos_secret_key: Optional[str] = os.getenv("NACOS_SECRET_KEY")
    nacos_context_path: Optional[str] = os.getenv("NACOS_CONTEXT_PATH")

settings = Settings() 