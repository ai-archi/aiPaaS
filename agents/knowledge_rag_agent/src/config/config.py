import os
from pathlib import Path
from typing import Optional, Dict, Any, Union, List
import re
import json

import yaml
from pydantic import BaseModel, Field, model_validator
from dotenv import load_dotenv
from functools import lru_cache
from loguru import logger
from pydantic_settings import BaseSettings, SettingsConfigDict

# 加载 .env 文件
load_dotenv()

# 全局YAML配置缓存
_yaml_config: Dict[str, Any] = {}

def parse_env_var(value: str) -> Any:
    """解析环境变量占位符"""
    if not isinstance(value, str):
        return value
        
    pattern = r'\${([^:}]+)(?::([^}]+))?}'
    match = re.match(pattern, value)
    if match:
        env_var = match.group(1)
        default = match.group(2)
        
        # 获取环境变量值
        env_value = os.getenv(env_var)
        if env_value is not None:
            # 尝试转换为适当的类型
            if env_value.lower() in ('true', 'false'):
                return env_value.lower() == 'true'
            try:
                if '.' in env_value:
                    return float(env_value)
                return int(env_value)
            except ValueError:
                return env_value
        elif default is not None:
            # 处理默认值
            if default.lower() in ('true', 'false'):
                return default.lower() == 'true'
            try:
                if '.' in default:
                    return float(default)
                return int(default)
            except ValueError:
                return default
        return None
    return value

def process_config_values(config: Dict[str, Any]) -> Dict[str, Any]:
    """递归处理配置字典中的环境变量占位符"""
    processed = {}
    for key, value in config.items():
        if isinstance(value, dict):
            processed[key] = process_config_values(value)
        elif isinstance(value, list):
            processed[key] = [parse_env_var(item) if isinstance(item, str) else item for item in value]
        else:
            processed[key] = parse_env_var(value)
    return processed

def load_yaml_config() -> Dict[str, Any]:
    """
    加载YAML配置文件，使用LRU缓存避免重复读取
    """
    global _yaml_config
    if not _yaml_config:
        config_path = Path(__file__).parent.parent.parent / "config.yaml"
        try:
            if config_path.exists():
                with open(config_path, "r", encoding="utf-8") as f:
                    raw_config = yaml.safe_load(f) or {}
                    _yaml_config = process_config_values(raw_config)
            else:
                logger.warning(f"配置文件 {config_path} 不存在，将使用默认值")
                _yaml_config = {}
        except Exception as e:
            logger.error(f"加载配置文件失败: {e}")
            _yaml_config = {}
    return _yaml_config

def get_db_url() -> str:
    """获取数据库URL"""
    db_type = os.getenv("DB_TYPE") or yaml_config.get("database", {}).get("type", "sqlite")
    if db_type == "postgresql":
        return (
            f"postgresql+asyncpg://"
            f"{os.getenv('PG_USER') or yaml_config.get('database', {}).get('postgresql', {}).get('user', 'postgres')}:"
            f"{os.getenv('PG_PASSWORD') or yaml_config.get('database', {}).get('postgresql', {}).get('password', '')}@"
            f"{os.getenv('PG_HOST') or yaml_config.get('database', {}).get('postgresql', {}).get('host', 'localhost')}:"
            f"{os.getenv('PG_PORT') or yaml_config.get('database', {}).get('postgresql', {}).get('port', '5432')}/"
            f"{os.getenv('PG_DATABASE') or yaml_config.get('database', {}).get('postgresql', {}).get('database', 'knowledge_rag')}"
        )
    return os.getenv("SQLITE_URL") or yaml_config.get("database", {}).get("sqlite", {}).get("url", "sqlite:///data/knowledge_rag.db")

def get_vector_db_url() -> str:
    """获取向量数据库URL"""
    vector_db_type = os.getenv("VECTOR_DB_TYPE") or yaml_config.get("database", {}).get("vector_db_type", "sqlite")
    if vector_db_type == "postgresql":
        return (
            f"postgresql+asyncpg://"
            f"{os.getenv('VECTOR_DB_USER') or yaml_config.get('database', {}).get('vector_db_postgresql', {}).get('user', 'postgres')}:"
            f"{os.getenv('VECTOR_DB_PASSWORD') or yaml_config.get('database', {}).get('vector_db_postgresql', {}).get('password', '')}@"
            f"{os.getenv('VECTOR_DB_HOST') or yaml_config.get('database', {}).get('vector_db_postgresql', {}).get('host', 'localhost')}:"
            f"{os.getenv('VECTOR_DB_PORT') or yaml_config.get('database', {}).get('vector_db_postgresql', {}).get('port', '5432')}/"
            f"{os.getenv('VECTOR_DB_NAME') or yaml_config.get('database', {}).get('vector_db_postgresql', {}).get('database', 'knowledge_rag_vector')}"
        )
    return os.getenv("VECTOR_DB_URL") or yaml_config.get("database", {}).get("vector_db_sqlite", {}).get("url", "sqlite:///data/knowledge_rag_vector.db")

class ConfigBase(BaseModel):
    """配置基类，提供通用的配置加载方法"""
    
    @classmethod
    def _get_yaml_value(cls, key: str, section: str) -> Any:
        """从YAML配置中获取值"""
        yaml_config = load_yaml_config()
        return yaml_config.get(section, {}).get(key)

    @classmethod
    def _get_env_value(cls, env_key: str) -> Optional[str]:
        """从环境变量中获取值"""
        return os.getenv(env_key)

class AppConfig(ConfigBase):
    name: str = "knowledge_rag_agent"
    version: str = "1.0.0"
    description: str = "Knowledge RAG Agent Service"
    debug: bool = True
    environment: str = "development"

    @model_validator(mode='before')
    @classmethod
    def validate_app_config(cls, values: Dict[str, Any]) -> Dict[str, Any]:
        for field in ["name", "version", "description", "debug", "environment"]:
            env_value = cls._get_env_value(f"APP_{field.upper()}")
            if env_value:
                values[field] = env_value
            else:
                yaml_value = cls._get_yaml_value(field, "app")
                if yaml_value:
                    values[field] = yaml_value
        return values

class ServerConfig(ConfigBase):
    host: str = "0.0.0.0"
    port: int = 8000
    reload: bool = True
    workers: int = 1

    @model_validator(mode='before')
    @classmethod
    def validate_server_config(cls, values: Dict[str, Any]) -> Dict[str, Any]:
        for field in ["host", "port", "reload", "workers"]:
            env_value = cls._get_env_value(f"SERVER_{field.upper()}")
            if env_value:
                values[field] = env_value
            else:
                yaml_value = cls._get_yaml_value(field, "server")
                if yaml_value:
                    values[field] = yaml_value
        return values

class DataConfig(ConfigBase):
    base_dir: str = "data"
    vector_db_dir: str = "data/vector_db"
    storage_dir: str = "data/storage"
    temp_dir: str = "data/temp"

    @model_validator(mode='before')
    @classmethod
    def validate_data_config(cls, values: Dict[str, Any]) -> Dict[str, Any]:
        for field in ["base_dir", "vector_db_dir", "storage_dir", "temp_dir"]:
            env_value = cls._get_env_value(f"DATA_{field.upper()}")
            if env_value:
                values[field] = env_value
            else:
                yaml_value = cls._get_yaml_value(field, "data")
                if yaml_value:
                    values[field] = yaml_value
        return values

    def __init__(self, **data):
        super().__init__(**data)
        # 确保目录存在
        for dir_path in [Path(self.base_dir), Path(self.vector_db_dir), Path(self.storage_dir), Path(self.temp_dir)]:
            dir_path.mkdir(parents=True, exist_ok=True)

class DatabaseConfig(ConfigBase):
    type: str = "sqlite"
    # SQLite配置
    sqlite: Dict[str, Any] = Field(default_factory=lambda: {
        "url": "sqlite:///./rag.db",
        "echo": False
    })
    # PostgreSQL配置
    postgresql: Dict[str, Any] = Field(default_factory=lambda: {
        "host": "localhost",
        "port": 5432,
        "database": "knowledge_rag",
        "user": "postgres",
        "password": "",
        "echo": False,
        "min_connections": 1,
        "max_connections": 10
    })

    @model_validator(mode='before')
    @classmethod
    def validate_database_config(cls, values: Dict[str, Any]) -> Dict[str, Any]:
        # 基本类型配置
        env_value = cls._get_env_value("DB_TYPE")
        if env_value:
            values["type"] = env_value
        else:
            yaml_value = cls._get_yaml_value("type", "database")
            if yaml_value:
                values["type"] = yaml_value

        # SQLite配置
        sqlite_config = {}
        for field in ["url", "echo"]:
            env_value = cls._get_env_value(f"SQLITE_{field.upper()}")
            if env_value:
                sqlite_config[field] = env_value if field != "echo" else env_value.lower() == "true"
            else:
                yaml_value = cls._get_yaml_value(field, "database.sqlite")
                if yaml_value is not None:
                    sqlite_config[field] = yaml_value
        if sqlite_config:
            values["sqlite"] = {**values.get("sqlite", {}), **sqlite_config}

        # PostgreSQL配置
        pg_config = {}
        for field in ["host", "port", "database", "user", "password", "echo", "min_connections", "max_connections"]:
            env_value = cls._get_env_value(f"PG_{field.upper()}")
            if env_value:
                pg_config[field] = env_value if field not in ["port", "min_connections", "max_connections"] else int(env_value)
                if field == "echo":
                    pg_config[field] = env_value.lower() == "true"
            else:
                yaml_value = cls._get_yaml_value(field, "database.postgresql")
                if yaml_value is not None:
                    pg_config[field] = yaml_value
        if pg_config:
            values["postgresql"] = {**values.get("postgresql", {}), **pg_config}

        return values

    def get_db_url(self) -> str:
        """获取数据库URL"""
        if self.type == "sqlite":
            return self.sqlite["url"]
        elif self.type == "postgresql":
            pg = self.postgresql
            return f"postgresql://{pg['user']}:{pg['password']}@{pg['host']}:{pg['port']}/{pg['database']}"
        else:
            raise ValueError(f"不支持的数据库类型: {self.type}")

class VectorDBConfig(ConfigBase):
    type: str = "sqlite"
    # SQLite配置
    sqlite: Dict[str, Any] = Field(default_factory=lambda: {
        "url": "sqlite:///./vector_store.db",
        "echo": False,
        "dimension": 768,
        "distance_strategy": "cosine"
    })
    # PostgreSQL配置
    postgresql: Dict[str, Any] = Field(default_factory=lambda: {
        "host": "localhost",
        "port": 5432,
        "database": "vector_store",
        "user": "postgres",
        "password": "",
        "echo": False,
        "min_connections": 1,
        "max_connections": 10,
        "dimension": 768,
        "distance_strategy": "cosine",
        "index_type": "ivfflat",
        "index_lists": 100,
        "ef_construction": 100,
        "m_neighbors": 16
    })

    @model_validator(mode='before')
    @classmethod
    def validate_vector_db_config(cls, values: Dict[str, Any]) -> Dict[str, Any]:
        # 基本类型配置
        env_value = cls._get_env_value("VECTOR_DB_TYPE")
        if env_value:
            values["type"] = env_value
        else:
            yaml_value = cls._get_yaml_value("type", "vector_db")
            if yaml_value:
                values["type"] = yaml_value

        # SQLite配置
        sqlite_config = {}
        for field in ["url", "echo", "dimension", "distance_strategy"]:
            env_value = cls._get_env_value(f"VECTOR_DB_SQLITE_{field.upper()}")
            if env_value:
                sqlite_config[field] = env_value
                if field == "echo":
                    sqlite_config[field] = env_value.lower() == "true"
                elif field == "dimension":
                    sqlite_config[field] = int(env_value)
            else:
                yaml_value = cls._get_yaml_value(field, "vector_db.sqlite")
                if yaml_value is not None:
                    sqlite_config[field] = yaml_value
        if sqlite_config:
            values["sqlite"] = {**values.get("sqlite", {}), **sqlite_config}

        # PostgreSQL配置
        pg_config = {}
        pg_fields = [
            "host", "port", "database", "user", "password", "echo",
            "min_connections", "max_connections", "dimension",
            "distance_strategy", "index_type", "index_lists",
            "ef_construction", "m_neighbors"
        ]
        for field in pg_fields:
            env_value = cls._get_env_value(f"VECTOR_DB_PG_{field.upper()}")
            if env_value:
                if field in ["port", "min_connections", "max_connections", "dimension", "index_lists", "ef_construction", "m_neighbors"]:
                    pg_config[field] = int(env_value)
                elif field == "echo":
                    pg_config[field] = env_value.lower() == "true"
                else:
                    pg_config[field] = env_value
            else:
                yaml_value = cls._get_yaml_value(field, "vector_db.postgresql")
                if yaml_value is not None:
                    pg_config[field] = yaml_value
        if pg_config:
            values["postgresql"] = {**values.get("postgresql", {}), **pg_config}

        return values

    def get_vector_db_url(self) -> str:
        """获取向量数据库URL"""
        if self.type == "sqlite":
            return self.sqlite["url"]
        elif self.type == "postgresql":
            pg = self.postgresql
            return f"postgresql://{pg['user']}:{pg['password']}@{pg['host']}:{pg['port']}/{pg['database']}"
        else:
            raise ValueError(f"不支持的向量数据库类型: {self.type}")

class LogConfig(ConfigBase):
    level: str = "INFO"
    format: str = "<green>{time:YYYY-MM-DD HH:mm:ss.SSS}</green> | <level>{level: <8}</level> | <cyan>{name}</cyan>:<cyan>{function}</cyan>:<cyan>{line}</cyan> - <level>{message}</level>"
    file: str = "logs/app.log"
    rotation: str = "1 day"
    retention: str = "1 month"
    compression: str = "zip"

    @model_validator(mode='before')
    @classmethod
    def validate_log_config(cls, values: Dict[str, Any]) -> Dict[str, Any]:
        yaml_config = load_yaml_config()
        log_config = yaml_config.get("log", {})
        
        # 确保values字典包含所有默认值
        if not values:
            values = {
                "level": "INFO",
                "format": "<green>{time:YYYY-MM-DD HH:mm:ss.SSS}</green> | <level>{level: <8}</level> | <cyan>{name}</cyan>:<cyan>{function}</cyan>:<cyan>{line}</cyan> - <level>{message}</level>",
                "file": "logs/app.log",
                "rotation": "1 day",
                "retention": "1 month",
                "compression": "zip"
            }
        
        # 处理日志级别（确保大写）
        level = os.getenv("LOG_LEVEL") or log_config.get("level", values.get("level", "INFO"))
        values["level"] = level.upper()
        
        # 处理其他配置
        values["format"] = os.getenv("LOG_FORMAT") or log_config.get("format", values.get("format"))
        values["file"] = os.getenv("LOG_FILE") or log_config.get("file", values.get("file"))
        values["rotation"] = os.getenv("LOG_ROTATION") or log_config.get("rotation", values.get("rotation"))
        values["retention"] = os.getenv("LOG_RETENTION") or log_config.get("retention", values.get("retention"))
        values["compression"] = os.getenv("LOG_COMPRESSION") or log_config.get("compression", values.get("compression"))
        
        return values

class AuthConfig(ConfigBase):
    secret_key: str = "your-secret-key"
    algorithm: str = "HS256"
    access_token_expire_minutes: int = 30

    @model_validator(mode='before')
    @classmethod
    def validate_auth_config(cls, values: Dict[str, Any]) -> Dict[str, Any]:
        for field in ["secret_key", "algorithm", "access_token_expire_minutes"]:
            env_value = cls._get_env_value(f"AUTH_{field.upper()}")
            if env_value:
                values[field] = env_value
            else:
                yaml_value = cls._get_yaml_value(field, "auth")
                if yaml_value:
                    values[field] = yaml_value
        return values

class CORSConfig(ConfigBase):
    allow_origins: List[str] = ["*"]
    allow_credentials: bool = True
    allow_methods: List[str] = ["*"]
    allow_headers: List[str] = ["*"]

    @model_validator(mode='before')
    @classmethod
    def validate_cors_config(cls, values: Dict[str, Any]) -> Dict[str, Any]:
        yaml_config = load_yaml_config()
        cors_config = yaml_config.get("cors", {})
        
        # 处理 allow_origins
        env_origins = os.getenv("CORS_ALLOW_ORIGINS")
        if env_origins:
            values["allow_origins"] = env_origins.split(",")
        else:
            yaml_origins = cors_config.get("allow_origins")
            if isinstance(yaml_origins, str):
                values["allow_origins"] = yaml_origins.split(",")
            elif isinstance(yaml_origins, list):
                values["allow_origins"] = yaml_origins
            else:
                values["allow_origins"] = ["*"]

        # 处理 allow_credentials
        env_credentials = os.getenv("CORS_ALLOW_CREDENTIALS")
        if env_credentials is not None:
            values["allow_credentials"] = env_credentials.lower() == "true"
        else:
            yaml_credentials = cors_config.get("allow_credentials")
            if yaml_credentials is not None:
                values["allow_credentials"] = yaml_credentials
            else:
                values["allow_credentials"] = True

        # 处理 allow_methods
        env_methods = os.getenv("CORS_ALLOW_METHODS")
        if env_methods:
            values["allow_methods"] = env_methods.split(",")
        else:
            yaml_methods = cors_config.get("allow_methods")
            if isinstance(yaml_methods, str):
                values["allow_methods"] = yaml_methods.split(",")
            elif isinstance(yaml_methods, list):
                values["allow_methods"] = yaml_methods
            else:
                values["allow_methods"] = ["*"]

        # 处理 allow_headers
        env_headers = os.getenv("CORS_ALLOW_HEADERS")
        if env_headers:
            values["allow_headers"] = env_headers.split(",")
        else:
            yaml_headers = cors_config.get("allow_headers")
            if isinstance(yaml_headers, str):
                values["allow_headers"] = yaml_headers.split(",")
            elif isinstance(yaml_headers, list):
                values["allow_headers"] = yaml_headers
            else:
                values["allow_headers"] = ["*"]

        return values

class NacosConfig(ConfigBase):
    # Nacos 服务器配置
    enable: bool = True
    server_addresses: str = "127.0.0.1:8848"
    namespace: str = "public"
    username: str = "nacos"
    password: str = "nacos"
    context_path: str = "/nacos"
    
    # 服务注册配置
    service_name: str = "knowledge-rag-agent"
    group_name: str = "DEFAULT_GROUP"
    cluster_name: str = "DEFAULT"
    
    # 服务实例配置
    port: int = 8002
    weight: float = 1.0
    metadata: dict = {}
    
    # 健康检查配置
    health_check_url: str = "/actuator/health"

    @model_validator(mode='before')
    @classmethod
    def validate_nacos_config(cls, values: Dict[str, Any]) -> Dict[str, Any]:
        yaml_config = load_yaml_config()
        nacos_config = yaml_config.get("nacos", {})
        
        # 处理所有字段
        fields = [
            "enable", "server_addresses", "namespace", "username", "password",
            "context_path", "service_name", "group_name",
            "port", "weight", "metadata", "health_check_url"
        ]
        
        for field in fields:
            env_value = os.getenv(f"NACOS_{field.upper()}")
            if env_value is not None:
                # 特殊处理布尔值和数值类型
                if field in ["enable"]:
                    values[field] = env_value.lower() == "true"
                elif field in ["port"]:
                    values[field] = int(env_value)
                elif field in ["weight"]:
                    values[field] = float(env_value)
                elif field == "metadata" and env_value:
                    try:
                        values[field] = json.loads(env_value)
                    except:
                        values[field] = {}
                else:
                    values[field] = env_value
            else:
                yaml_value = nacos_config.get(field)
                if yaml_value is not None:
                    values[field] = yaml_value
        
        return values

    def validate_config(self) -> Dict[str, Any]:
        """验证配置并返回有效的配置字典"""
        config = {}
        required_fields = ['server_addresses', 'service_name', 'port']
        for field in required_fields:
            value = getattr(self, field)
            if value is None:
                raise ValueError(f"Nacos config missing required field: {field}")
            config[field] = value

        optional_fields = [
            'enable', 'namespace', 'username', 'password', 'context_path',
            'group_name', 'weight', 'metadata', 'health_check_url'
        ]
        for field in optional_fields:
            value = getattr(self, field)
            if value is not None:
                config[field] = value

        return config

class APIConfig(BaseModel):
    embedding_api_url: Optional[str] = None
    llm_api_url: Optional[str] = None
    permission_api_url: Optional[str] = None

    @model_validator(mode='before')
    @classmethod
    def validate_from_env(cls, values: Dict[str, Any]) -> Dict[str, Any]:
        yaml_config = load_yaml_config()
        api_config = yaml_config.get("api", {})
        
        values["embedding_api_url"] = os.getenv("EMBEDDING_API_URL") or api_config.get("embedding_api_url", values.get("embedding_api_url"))
        values["llm_api_url"] = os.getenv("LLM_API_URL") or api_config.get("llm_api_url", values.get("llm_api_url"))
        values["permission_api_url"] = os.getenv("PERMISSION_API_URL") or api_config.get("permission_api_url", values.get("permission_api_url"))
        
        return values

class Settings(BaseSettings):
    """
    全局设置类，整合所有配置
    """
    model_config = SettingsConfigDict(
        env_file=".env", 
        env_file_encoding="utf-8", 
        extra="ignore",
        arbitrary_types_allowed=True
    )
    
    app: AppConfig = Field(default_factory=AppConfig)
    server: ServerConfig = Field(default_factory=ServerConfig)
    data: DataConfig = Field(default_factory=DataConfig)
    database: DatabaseConfig = Field(default_factory=DatabaseConfig)
    vector_db: VectorDBConfig = Field(default_factory=VectorDBConfig)
    log: LogConfig = Field(default_factory=LogConfig)
    auth: AuthConfig = Field(default_factory=AuthConfig)
    cors: CORSConfig = Field(default_factory=CORSConfig)
    nacos: NacosConfig = Field(default_factory=NacosConfig)
    api: APIConfig = Field(default_factory=APIConfig)

# 加载YAML配置（全局变量，用于所有配置类）
yaml_config = load_yaml_config()

# 创建全局设置实例
settings = Settings()

# 配置日志
logger.remove()  # 移除默认的处理器
logger.add(
    sink=lambda msg: print(msg),
    level=settings.log.level,
    format=settings.log.format,
    colorize=True,
) 