from datetime import datetime
from typing import Optional
from uuid import UUID
from sqlalchemy import DateTime, String, Text
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column

class Base(DeclarativeBase):
    """SQLAlchemy 基类"""
    pass

class TaskModel(Base):
    """任务数据库模型"""
    __tablename__ = "tasks"
    
    id: Mapped[UUID] = mapped_column(primary_key=True)
    title: Mapped[str] = mapped_column(String(200))
    description: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    status: Mapped[str] = mapped_column(String(20))
    due_date: Mapped[Optional[datetime]] = mapped_column(DateTime, nullable=True)
    assigned_to: Mapped[Optional[UUID]] = mapped_column(nullable=True)
    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime, 
        default=datetime.utcnow, 
        onupdate=datetime.utcnow
    )
    deleted_at: Mapped[Optional[datetime]] = mapped_column(DateTime, nullable=True) 