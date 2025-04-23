"""
Application configuration.
"""
import os
from typing import Any, Dict, Optional, List
from pydantic import PostgresDsn, validator, AnyHttpUrl
from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    # 基础配置
    ENV: str = os.getenv("ENV", "development")
    DEBUG: bool = ENV == "development"
    PORT: int = 8085
    
    # API配置
    API_V1_STR: str = "/api/v1"
    PROJECT_NAME: str = "Task Agent"
    
    # 数据库配置
    DATABASE_TYPE: str = "sqlite"  # sqlite 或 postgresql
    
    # SQLite配置
    SQLITE_URL: str = "sqlite:///./data/task_management.db"
    
    # PostgreSQL配置
    POSTGRES_SERVER: str = "localhost"
    POSTGRES_USER: str = "postgres"
    POSTGRES_PASSWORD: str = "postgres"
    POSTGRES_PORT: int = 5432
    POSTGRES_DB: str = "task_management"
    SQLALCHEMY_DATABASE_URI: Optional[str] = None
    
    # 日志配置
    LOG_LEVEL: str = "DEBUG" if DEBUG else "INFO"
    
    # 安全配置
    SECRET_KEY: str = os.getenv("SECRET_KEY", "your-secret-key-here")
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24 * 8  # 8 days
    
    # CORS配置
    CORS_ORIGINS: List[str] = ["*"]
    CORS_CREDENTIALS: bool = True
    CORS_METHODS: List[str] = ["*"]
    CORS_HEADERS: List[str] = ["*"]

    @validator("SQLALCHEMY_DATABASE_URI", pre=True)
    def assemble_db_connection(cls, v: Optional[str], values: Dict[str, Any]) -> str:
        if isinstance(v, str):
            return v
            
        if values.get("DATABASE_TYPE") == "sqlite":
            return values.get("SQLITE_URL", "sqlite:///./data/task_management.db")
            
        return PostgresDsn.build(
            scheme="postgresql",
            username=values.get("POSTGRES_USER"),
            password=values.get("POSTGRES_PASSWORD"),
            host=values.get("POSTGRES_SERVER"),
            port=str(values.get("POSTGRES_PORT", 5432)),
            path=f"/{values.get('POSTGRES_DB') or ''}",
        )

    class Config:
        case_sensitive = True
        env_file = ".env"

# 创建全局配置实例
settings = Settings() 