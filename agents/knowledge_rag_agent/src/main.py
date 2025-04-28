import sys
import os
from fastapi import FastAPI
from contextlib import asynccontextmanager

# 确保 src 目录在 sys.path 中
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from config import settings
from interfaces.rest_api import router as rest_router
from infrastructure.nacos_register import register_instance, deregister_instance

@asynccontextmanager
async def lifespan(app: FastAPI):
    await register_instance()
    try:
        yield
    finally:
        await deregister_instance()

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