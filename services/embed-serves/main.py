from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Dict, Any
from functools import lru_cache
import fastembed
import os
import psutil
from contextlib import asynccontextmanager
from mf_nacos_service_registrar.registrar import get_nacos_client, register_instance, deregister_instance
import yaml

app = FastAPI()

default_model_name = "BAAI/bge-base-en-v1.5"
default_provider = "onnx"

# 手动维护支持的模型和推理后端
SUPPORTED_MODELS = [
    "BAAI/bge-base-en-v1.5",
    "BAAI/bge-small-en-v1.5",
    "BAAI/bge-large-en-v1.5",
    # 可根据实际情况扩展
]
SUPPORTED_PROVIDERS = [
    "onnx",
    "pytorch",
    "tensorrt"
]

# 全局集合，记录已加载的模型-后端组合
CACHED_MODELS = set()

# 读取 nacos 配置
NACOS_SERVER_ADDR = os.getenv("NACOS_SERVER_ADDR", "127.0.0.1:8848")
NACOS_NAMESPACE = os.getenv("NACOS_NAMESPACE", "public")
NACOS_ACCESS_KEY = os.getenv("NACOS_ACCESS_KEY")
NACOS_SECRET_KEY = os.getenv("NACOS_SECRET_KEY")
NACOS_GROUP = os.getenv("NACOS_GROUP", "DEFAULT_GROUP")
NACOS_CLUSTER = os.getenv("NACOS_CLUSTER", "DEFAULT")
NACOS_SERVICE_NAME = os.getenv("NACOS_SERVICE_NAME", "embed-serves")
NACOS_WEIGHT = float(os.getenv("NACOS_WEIGHT", 1.0))
NACOS_ENABLE = os.getenv("NACOS_ENABLE", "true").lower() == "true"
NACOS_HEALTHY = os.getenv("NACOS_HEALTHY", "true").lower() == "true"
NACOS_EPHEMERAL = os.getenv("NACOS_EPHEMERAL", "true").lower() == "true"

def get_port_from_yaml():
    yaml_path = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))), 'tools', 'config', 'application.yaml')
    if os.path.exists(yaml_path):
        with open(yaml_path, 'r') as f:
            config = yaml.safe_load(f)
            return int(config.get('ports', {}).get('embed_serves', 8003))
    return 8003

# 端口优先级：.env > application.yaml > 默认
NACOS_PORT = int(os.getenv("PORT") or get_port_from_yaml())

_nacos_client = None

def get_or_create_nacos_client():
    global _nacos_client
    if _nacos_client is not None:
        return _nacos_client
    _nacos_client = get_nacos_client(
        server_addr=NACOS_SERVER_ADDR,
        namespace=NACOS_NAMESPACE,
        ak=NACOS_ACCESS_KEY,
        sk=NACOS_SECRET_KEY
    )
    return _nacos_client

@asynccontextmanager
async def lifespan(app: FastAPI):
    client = get_or_create_nacos_client()
    register_instance(
        client=client,
        service_name=NACOS_SERVICE_NAME,
        port=NACOS_PORT,
        cluster_name=NACOS_CLUSTER,
        weight=NACOS_WEIGHT,
        metadata={"env": os.getenv("ENVIRONMENT", "dev")},
        enable=NACOS_ENABLE,
        healthy=NACOS_HEALTHY,
        ephemeral=NACOS_EPHEMERAL,
        group_name=NACOS_GROUP,
        heartbeat_interval=5
    )
    try:
        yield
    finally:
        deregister_instance(
            client=client,
            service_name=NACOS_SERVICE_NAME,
            port=NACOS_PORT,
            cluster_name=NACOS_CLUSTER,
            ephemeral=NACOS_EPHEMERAL,
            group_name=NACOS_GROUP
        )

app = FastAPI(lifespan=lifespan)

class EmbedRequest(BaseModel):
    texts: List[str]
    model_name: str = default_model_name
    provider: str = default_provider
    normalize: bool = True
    batch_size: int = 32

class InferRequest(BaseModel):
    text: str
    model_name: str = default_model_name
    provider: str = default_provider
    normalize: bool = True

class InferBatchRequest(BaseModel):
    texts: List[str]
    model_name: str = default_model_name
    provider: str = default_provider
    normalize: bool = True
    batch_size: int = 32

class EmbedResponse(BaseModel):
    embeddings: List[List[float]]

class InferResponse(BaseModel):
    embedding: List[float]

class InferInfoResponse(BaseModel):
    cached_models: List[str]
    cache_info: Dict[str, Any]
    process_memory_mb: float
    process_pid: int

@lru_cache(maxsize=8)
def get_embedder(model_name: str, provider: str):
    try:
        embedder = fastembed.TextEmbedding(model_name=model_name, provider=provider)
        CACHED_MODELS.add(f"{model_name}:{provider}")
        return embedder
    except Exception as e:
        raise RuntimeError(f"模型加载失败: {e}")

@app.post("/embed", response_model=EmbedResponse)
async def embed_texts(req: EmbedRequest):
    try:
        model = get_embedder(req.model_name, req.provider)
        embeddings = list(model.embed(
            req.texts,
            normalize=req.normalize,
            batch_size=req.batch_size
        ))
        return {"embeddings": embeddings}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"嵌入失败: {e}")

@app.post("/infer", response_model=InferResponse)
async def infer_text(req: InferRequest):
    try:
        model = get_embedder(req.model_name, req.provider)
        embedding = next(model.embed([req.text], normalize=req.normalize, batch_size=1))
        return {"embedding": embedding}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"推理失败: {e}")

@app.post("/infer_batch", response_model=EmbedResponse)
async def infer_batch(req: InferBatchRequest):
    try:
        model = get_embedder(req.model_name, req.provider)
        embeddings = list(model.embed(
            req.texts,
            normalize=req.normalize,
            batch_size=req.batch_size
        ))
        return {"embeddings": embeddings}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"批量推理失败: {e}")

@app.get("/infer_info", response_model=InferInfoResponse)
async def infer_info():
    """返回推理服务状态监控信息"""
    cache_info = get_embedder.cache_info()
    process = psutil.Process(os.getpid())
    mem_mb = process.memory_info().rss / 1024 / 1024
    return {
        "cached_models": list(CACHED_MODELS),
        "cache_info": {
            "hits": getattr(cache_info, 'hits', None),
            "misses": getattr(cache_info, 'misses', None),
            "currsize": getattr(cache_info, 'currsize', None),
            "maxsize": getattr(cache_info, 'maxsize', None)
        },
        "process_memory_mb": round(mem_mb, 2),
        "process_pid": process.pid
    }

@app.get("/models")
async def list_models():
    """列出可用模型和推理后端（本地维护）"""
    return {"models": SUPPORTED_MODELS, "providers": SUPPORTED_PROVIDERS}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=NACOS_PORT, reload=True) 