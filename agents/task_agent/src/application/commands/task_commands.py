from datetime import datetime
from typing import Optional
from uuid import UUID
from pydantic import BaseModel

class Command(BaseModel):
    """命令基类"""
    command_id: UUID
    timestamp: datetime = datetime.utcnow()

class CreateTaskCommand(Command):
    """创建任务命令"""
    title: str
    description: Optional[str] = None
    due_date: Optional[datetime] = None

class AssignTaskCommand(Command):
    """分配任务命令"""
    task_id: UUID
    user_id: UUID

class UpdateTaskStatusCommand(Command):
    """更新任务状态命令"""
    task_id: UUID
    action: str  # "start", "complete", "cancel" 