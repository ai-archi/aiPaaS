from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Dict, Any
from functools import lru_cache
import fastembed
import os
import psutil

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