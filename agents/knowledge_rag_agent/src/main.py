import sys
import os
from fastapi import FastAPI
from contextlib import asynccontextmanager

# 确保 src 目录在 sys.path 中
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from config import settings
from interfaces.rest_api import router as rest_router
from mf_nacos_service_registrar.registrar import get_nacos_client, register_instance, deregister_instance

_nacos_client = None

def get_or_create_nacos_client():
    global _nacos_client
    if _nacos_client is not None:
        return _nacos_client
    _nacos_client = get_nacos_client(
        server_addr=settings.nacos_server_addr,
        namespace=settings.nacos_namespace,
        ak=settings.nacos_access_key,
        sk=settings.nacos_secret_key
    )
    return _nacos_client

@asynccontextmanager
async def lifespan(app: FastAPI):
    client = get_or_create_nacos_client()
    register_instance(
        client=client,
        service_name=settings.nacos_service_name,
        port=settings.port,
        cluster_name=settings.nacos_cluster,
        weight=settings.nacos_weight,
        metadata={"env": settings.environment},
        enable=settings.nacos_enable,
        healthy=settings.nacos_healthy,
        ephemeral=settings.nacos_ephemeral,
        group_name=settings.nacos_group,
        heartbeat_interval=5
    )
    try:
        yield
    finally:
        deregister_instance(
            client=client,
            service_name=settings.nacos_service_name,
            port=settings.port,
            cluster_name=settings.nacos_cluster,
            ephemeral=settings.nacos_ephemeral,
            group_name=settings.nacos_group
        )

app = FastAPI(title=settings.app_name, lifespan=lifespan)

# 注册 REST API 路由
app.include_router(rest_router)

# 可选：添加中间件、事件处理等
# @app.on_event("startup")
# async def startup_event():
#     ...

# @app.on_event("shutdown")
# async def shutdown_event():
#     ...

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host=settings.host, port=settings.port, reload=True) 