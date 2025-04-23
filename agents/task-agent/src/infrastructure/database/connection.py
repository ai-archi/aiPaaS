"""
Database connection management.
"""
from typing import AsyncGenerator
from sqlalchemy.ext.asyncio import AsyncSession, create_async_engine, async_sessionmaker
from sqlalchemy.pool import AsyncAdaptedQueuePool
from src.core.config import settings

# 构建数据库URL
def get_database_url() -> str:
    """Get database URL based on configuration."""
    if settings.DATABASE_TYPE == "sqlite":
        return settings.SQLITE_URL.replace("sqlite://", "sqlite+aiosqlite://")
    elif settings.DATABASE_TYPE == "postgresql":
        return f"postgresql+asyncpg://{settings.POSTGRES_USER}:{settings.POSTGRES_PASSWORD}@{settings.POSTGRES_SERVER}:{settings.POSTGRES_PORT}/{settings.POSTGRES_DB}"
    else:
        raise ValueError(f"Unsupported database type: {settings.DATABASE_TYPE}")

# 创建异步引擎
engine = create_async_engine(
    get_database_url(),
    echo=settings.DEBUG,
    future=True,
    pool_size=5,
    max_overflow=10,
    poolclass=AsyncAdaptedQueuePool,
)

# 创建异步会话工厂
AsyncSessionFactory = async_sessionmaker(
    engine,
    class_=AsyncSession,
    expire_on_commit=False,
    autocommit=False,
    autoflush=False,
)

async def get_db_session() -> AsyncGenerator[AsyncSession, None]:
    """
    Get database session.
    
    Yields:
        AsyncSession: Database session.
    """
    async with AsyncSessionFactory() as session:
        try:
            yield session
        finally:
            await session.close() 