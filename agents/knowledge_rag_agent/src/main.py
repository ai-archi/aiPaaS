import sys
from contextlib import asynccontextmanager
from typing import Optional
from loguru import logger
import uvicorn
from fastapi import FastAPI, APIRouter
from fastapi.middleware.cors import CORSMiddleware
from datetime import datetime

from config.config import settings
from bootstrap.bootstrap import (
    setup_app,
    start_app,
    stop_app
)

def create_app() -> FastAPI:
    """创建并返回 FastAPI 应用实例"""
    app = FastAPI(
        title=settings.app.name,
        description=settings.app.description,
        version=settings.app.version,
        lifespan=app_lifespan
    )
    
    # 配置基础中间件
    app.add_middleware(
        CORSMiddleware,
        allow_origins=settings.cors.allow_origins,
        allow_credentials=settings.cors.allow_credentials,
        allow_methods=settings.cors.allow_methods,
        allow_headers=settings.cors.allow_headers,
    )
    
    # 添加健康检查路由
    @app.get("/actuator/health")
    async def health_check():
        """健康检查接口，返回服务状态和启动时间"""
        if not hasattr(app.state, "initialized"):
            return {
                "status": "DOWN",
                "details": {
                    "message": "服务未完成初始化"
                }
            }
            
        return {
            "status": "UP" if app.state.initialized else "DOWN",
            "details": {
                "startupTime": app.state.startup_time.isoformat() if hasattr(app.state, "startup_time") else None,
                "error": app.state.startup_error if hasattr(app.state, "startup_error") else None
            }
        }
    
    return app

@asynccontextmanager
async def app_lifespan(app: FastAPI):
    """应用生命周期管理器"""
    try:
        # 配置阶段：初始化所有必要的组件和服务
        await setup_app(app)
        
        # 启动阶段：启动所有服务
        await start_app(app)
        
        yield
    finally:
        # 停止阶段：清理资源和关闭服务
        await stop_app(app)

# 创建应用实例
app = create_app()

def run_server():
    """启动服务器"""
    try:
        logger.info(f"正在启动 {settings.app.name} 服务...")
        uvicorn.run(
            app="main:app",
            host=settings.server.host,
            port=settings.server.port,
            reload=settings.server.reload,
            log_level=settings.log.level.lower(),
            access_log=True
        )
    except Exception as e:
        logger.error(f"服务启动失败: {e}")
        sys.exit(1)

if __name__ == "__main__":
    run_server() 