from datetime import datetime
from typing import Optional
from sqlalchemy import Column, Integer, String, DateTime, Text, Enum
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class Task(Base):
    """任务数据模型"""
    __tablename__ = "tasks"

    id = Column(Integer, primary_key=True, index=True)
    title = Column(String(255), nullable=False)
    description = Column(Text)
    status = Column(
        Enum("pending", "in_progress", "completed", "failed", name="task_status"),
        default="pending"
    )
    priority = Column(
        Enum("low", "medium", "high", "urgent", name="task_priority"),
        default="medium"
    )
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(
        DateTime,
        default=datetime.utcnow,
        onupdate=datetime.utcnow
    )
    completed_at = Column(DateTime, nullable=True)
    created_by = Column(String(255), nullable=False)
    assigned_to = Column(String(255), nullable=True)
    metadata = Column(Text, nullable=True)  # 存储 JSON 格式的额外元数据 