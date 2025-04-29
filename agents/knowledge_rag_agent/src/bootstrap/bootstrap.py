from pathlib import Path
from typing import Optional, Dict, Any
import socket
import asyncio
import os
from datetime import datetime

from fastapi import FastAPI, APIRouter
from loguru import logger
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.orm import sessionmaker
from mf_nacos_service_registrar.registrar import get_nacos_client, register_instance, deregister_instance,get_local_ip

from config.config import settings
from interfaces.rest_api import router as api_router

async def setup_app(app: FastAPI) -> None:
    """
    配置阶段：初始化和配置所有组件
    
    Args:
        app (FastAPI): FastAPI 应用实例
    """
    try:
        logger.info(f"开始配置 {settings.app.name} 服务...")
        
        # 1. 基础设施配置
        _setup_logging()
        _create_data_directories()
        
        # 2. 数据库配置
        db_config = _setup_database()
        app.state.db = db_config
        
        # 3. 路由配置
        app.include_router(api_router)
        
        # 4. 健康检查路由
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
                    "error": app.state.startup_error if hasattr(app.state, "startup_error") else None,
                    "version": settings.app.version
                }
            }
        
        # 5. Nacos配置（如果启用）
        if settings.nacos.enable:
            _setup_nacos(app)
            
        # 6. 记录初始化状态
        app.state.initialized = True
        app.state.startup_time = datetime.now()
        
        logger.info(f"完成配置 {settings.app.name} 服务")
        
    except Exception as e:
        logger.error(f"服务配置失败: {e}")
        app.state.initialized = False
        app.state.startup_error = str(e)
        raise

async def start_app(app: FastAPI) -> None:
    """
    启动阶段：启动所有服务组件
    
    Args:
        app (FastAPI): FastAPI 应用实例
    """
    try:
        logger.info(f"正在启动 {settings.app.name} 服务...")
        
        # 1. 注册到Nacos（如果启用）
        if settings.nacos.enable and hasattr(app.state, 'nacos_client'):
            await _register_nacos(app)
            
        # 2. 初始化数据库连接
        if hasattr(app.state, 'db'):
            # 这里可以添加数据库连接池初始化等操作
            pass
            
        # 3. 启动后台任务（如果有）
        # TODO: 添加后台任务启动逻辑
        
        logger.info(f"{settings.app.name} 服务启动完成")
        
    except Exception as e:
        logger.error(f"服务启动失败: {e}")
        raise

async def stop_app(app: FastAPI) -> None:
    """
    停止阶段：清理资源和关闭服务
    
    Args:
        app (FastAPI): FastAPI 应用实例
    """
    try:
        logger.info(f"正在关闭 {settings.app.name} 服务...")
        
        # 1. 从Nacos注销（如果已注册）
        if settings.nacos.enable and hasattr(app.state, 'nacos_client'):
            await _deregister_nacos(app)
            
        # 2. 关闭数据库连接
        if hasattr(app.state, 'db'):
            for engine in [app.state.db.get('db_engine'), app.state.db.get('vector_db_engine')]:
                if engine:
                    await engine.dispose()
                    
        # 3. 停止后台任务（如果有）
        # TODO: 添加后台任务停止逻辑
        
        logger.info(f"{settings.app.name} 服务关闭完成")
        
    except Exception as e:
        logger.error(f"服务关闭失败: {e}")
        raise

def _setup_logging() -> None:
    """配置日志系统"""
    # 移除默认的处理器
    logger.remove()
    
    # 添加控制台处理器
    logger.add(
        sink=lambda msg: print(msg),
        level=settings.log.level,
        format=settings.log.format,
        colorize=True
    )
    
    # 添加文件处理器
    log_file = Path(settings.log.file)
    log_file.parent.mkdir(parents=True, exist_ok=True)
    logger.add(
        sink=str(log_file),
        level=settings.log.level,
        format=settings.log.format,
        rotation=settings.log.rotation,
        retention=settings.log.retention,
        compression=settings.log.compression
    )

def _create_data_directories() -> None:
    """创建必要的数据目录"""
    data_dirs = [
        Path(settings.data.base_dir),
        Path(settings.data.vector_db_dir),
        Path(settings.data.storage_dir),
        Path(settings.data.temp_dir)
    ]
    
    for dir_path in data_dirs:
        try:
            dir_path.mkdir(parents=True, exist_ok=True)
            logger.info(f"创建目录: {dir_path}")
        except Exception as e:
            logger.error(f"创建目录失败 {dir_path}: {e}")

def _setup_database() -> Dict[str, Any]:
    """配置数据库连接"""
    try:
        # 主数据库配置
        db_type = settings.database.type
        if db_type == "sqlite":
            db_url = settings.database.get_db_url().replace('sqlite:///', 'sqlite+aiosqlite:///')
            engine = create_async_engine(
                db_url,
                echo=settings.database.sqlite.get("echo", False),
                future=True
            )
        elif db_type == "postgresql":
            db_url = settings.database.get_db_url()
            pg_config = settings.database.postgresql
            engine = create_async_engine(
                db_url,
                echo=pg_config.get("echo", False),
                pool_size=pg_config.get("max_connections", 5),
                max_overflow=pg_config.get("max_connections", 5) - pg_config.get("min_connections", 1),
                future=True
            )
        else:
            raise ValueError(f"不支持的数据库类型: {db_type}")
            
        async_session = sessionmaker(
            engine,
            class_=AsyncSession,
            expire_on_commit=False
        )
        
        # 向量数据库配置
        vector_db_type = settings.vector_db.type
        vector_engine = None
        vector_async_session = None
        
        if vector_db_type == "sqlite":
            vector_db_url = settings.vector_db.get_vector_db_url().replace('sqlite:///', 'sqlite+aiosqlite:///')
            vector_engine = create_async_engine(
                vector_db_url,
                echo=settings.vector_db.sqlite.get("echo", False),
                future=True
            )
        elif vector_db_type == "postgresql":
            vector_db_url = settings.vector_db.get_vector_db_url()
            pg_config = settings.vector_db.postgresql
            vector_engine = create_async_engine(
                vector_db_url,
                echo=pg_config.get("echo", False),
                pool_size=pg_config.get("max_connections", 5),
                max_overflow=pg_config.get("max_connections", 5) - pg_config.get("min_connections", 1),
                future=True
            )
            
        if vector_engine:
            vector_async_session = sessionmaker(
                vector_engine,
                class_=AsyncSession,
                expire_on_commit=False
            )
            
        return {
            "db_engine": engine,
            "db_session": async_session,
            "vector_db_engine": vector_engine,
            "vector_db_session": vector_async_session
        }
        
    except Exception as e:
        logger.error(f"数据库初始化失败: {e}")
        raise



def _setup_nacos(app: FastAPI) -> None:
    """设置 Nacos 服务注册"""
    try:
        client = get_nacos_client(
            server_addresses=settings.nacos.server_addresses,
            namespace=settings.nacos.namespace,
            ak=settings.nacos.username,
            sk=settings.nacos.password
        )
        
        app.state.nacos_client = client
        logger.info("Nacos客户端配置完成")
    except Exception as e:
        app.state.nacos_client = None
        logger.warning(f"Nacos配置失败: {e}")

async def _register_nacos(app: FastAPI, max_retries: int = 5, retry_interval: int = 5) -> None:
    """注册服务到 Nacos"""
    if not hasattr(app.state, 'nacos_client') or app.state.nacos_client is None:
        logger.warning("Nacos未配置，跳过注册")
        return
        
    retries = 0
    while retries < max_retries:
        try:
            register_instance(
                client=app.state.nacos_client,
                service_name=settings.nacos.service_name,
                port=settings.nacos.port,
                cluster_name=settings.nacos.cluster_name,
                weight=settings.nacos.weight,
                metadata={"env": os.getenv("ENVIRONMENT", "dev")},
                enable=True,
                healthy=True,
                ephemeral=True,
                group_name=settings.nacos.group_name,
                heartbeat_interval=5
            )
            logger.info(f"Nacos服务注册成功: {settings.nacos.service_name}@{get_local_ip()}:{settings.nacos.port}")
            return
        except Exception as e:
            retries += 1
            if retries < max_retries:
                logger.warning(f"Nacos注册失败 (尝试 {retries}/{max_retries}): {str(e)}")
                await asyncio.sleep(retry_interval)
            else:
                logger.error(f"Nacos注册失败，已达到最大重试次数 {max_retries}: {str(e)}")
                raise

async def _deregister_nacos(app: FastAPI) -> None:
    """从Nacos注销服务"""
    if not hasattr(app.state, 'nacos_client') or app.state.nacos_client is None:
        logger.debug("Nacos未配置，无需注销")
        return
    
    try:
        client = app.state.nacos_client
        logger.debug(f"正在从Nacos注销服务: {settings.nacos.service_name}")
        deregister_instance(
            client=client,
            service_name=settings.nacos.service_name,
            port=settings.nacos.port,
            cluster_name=settings.nacos.cluster_name,
            ephemeral=True,
            group_name=settings.nacos.group_name
        )
        logger.info("Nacos服务注销成功")
    except Exception as e:
        logger.error(f"Nacos服务注销失败: {e}")
        logger.error("详细错误信息: ", exc_info=True) 