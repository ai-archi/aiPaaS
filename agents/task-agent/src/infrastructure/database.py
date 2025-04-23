from typing import AsyncGenerator
from sqlalchemy.ext.asyncio import AsyncSession, create_async_engine, async_sessionmaker
from src.core.config import settings

# 创建异步引擎
if settings.DATABASE_TYPE == "sqlite":
    engine = create_async_engine(
        settings.SQLITE_URL.replace("sqlite://", "sqlite+aiosqlite://"),
        echo=settings.DEBUG
    )
else:
    DATABASE_URL = f"postgresql+asyncpg://{settings.POSTGRES_USER}:{settings.POSTGRES_PASSWORD}@{settings.POSTGRES_SERVER}:{settings.POSTGRES_PORT}/{settings.POSTGRES_DB}"
    engine = create_async_engine(DATABASE_URL, echo=settings.DEBUG)

# 创建异步会话工厂
async_session_maker = async_sessionmaker(engine, expire_on_commit=False)

async def get_db() -> AsyncGenerator[AsyncSession, None]:
    """获取数据库会话"""
    async with async_session_maker() as session:
        try:
            yield session
            await session.commit()
        except Exception:
            await session.rollback()
            raise
        finally:
            await session.close() 