from typing import Any, Dict, Optional
from pydantic import PostgresDsn, field_validator
from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    """应用配置"""
    PROJECT_NAME: str = "Task Agent"
    VERSION: str = "0.1.0"
    API_V1_STR: str = "/api/v1"
    
    DEBUG: bool = False
    
    # 数据库配置
    POSTGRES_SERVER: str = "localhost"
    POSTGRES_USER: str = "postgres"
    POSTGRES_PASSWORD: str = "postgres"
    POSTGRES_DB: str = "task_agent"
    POSTGRES_PORT: str = "5432"
    DATABASE_URL: Optional[str] = None
    
    @field_validator("DATABASE_URL", mode="before")
    @classmethod
    def assemble_db_url(cls, v: Optional[str], info: Dict[str, Any]) -> Any:
        if isinstance(v, str):
            return v
        
        user = info.data.get("POSTGRES_USER")
        password = info.data.get("POSTGRES_PASSWORD")
        host = info.data.get("POSTGRES_SERVER")
        port = info.data.get("POSTGRES_PORT", "5432")
        db = info.data.get("POSTGRES_DB", "")
        
        return f"postgresql+asyncpg://{user}:{password}@{host}:{port}/{db}"
    
    # JWT配置
    SECRET_KEY: str = "your-super-secret-key"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 30
    
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=True
    )

settings = Settings() 