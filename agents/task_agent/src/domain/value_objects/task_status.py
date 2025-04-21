from enum import Enum
from pydantic import BaseModel

class TaskStatusEnum(str, Enum):
    """任务状态枚举"""
    PENDING = "pending"
    IN_PROGRESS = "in_progress"
    COMPLETED = "completed"
    CANCELLED = "cancelled"

class TaskStatus(BaseModel):
    """任务状态值对象"""
    value: TaskStatusEnum
    
    @classmethod
    def create(cls, value: str) -> "TaskStatus":
        """创建任务状态值对象"""
        try:
            status = TaskStatusEnum(value.lower())
            return cls(value=status)
        except ValueError:
            raise ValueError(f"Invalid task status: {value}")
    
    def __str__(self) -> str:
        return self.value.value
    
    class Config:
        frozen = True  # 确保值对象不可变 