from datetime import datetime
from typing import Optional, Dict, Any
from pydantic import BaseModel, Field

class TaskBase(BaseModel):
    """任务基础模型"""
    title: str = Field(..., description="任务标题", min_length=1, max_length=255)
    description: Optional[str] = Field(None, description="任务描述")
    priority: str = Field("medium", description="任务优先级", pattern="^(low|medium|high|urgent)$")
    assigned_to: Optional[str] = Field(None, description="任务指派对象")
    metadata: Optional[Dict[str, Any]] = Field(None, description="任务元数据")

class TaskCreate(TaskBase):
    """创建任务请求模型"""
    pass

class TaskUpdate(BaseModel):
    """更新任务请求模型"""
    title: Optional[str] = Field(None, description="任务标题", min_length=1, max_length=255)
    description: Optional[str] = Field(None, description="任务描述")
    status: Optional[str] = Field(None, description="任务状态", pattern="^(pending|in_progress|completed|failed)$")
    priority: Optional[str] = Field(None, description="任务优先级", pattern="^(low|medium|high|urgent)$")
    assigned_to: Optional[str] = Field(None, description="任务指派对象")
    metadata: Optional[Dict[str, Any]] = Field(None, description="任务元数据")

class TaskInDB(TaskBase):
    """数据库中的任务模型"""
    id: int
    status: str
    created_at: datetime
    updated_at: datetime
    completed_at: Optional[datetime]
    created_by: str

    class Config:
        from_attributes = True

class TaskResponse(TaskInDB):
    """任务响应模型"""
    pass 