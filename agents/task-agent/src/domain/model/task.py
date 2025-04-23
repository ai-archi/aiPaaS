from typing import List, Dict, Any, Literal, Optional
from datetime import datetime
from .base import Entity

class Task(Entity):
    """Task 领域模型"""
    title: str
    description: str
    created_by: str
    context: Dict[str, Any] = {}
    agents: List[str] = []
    status: Literal["pending", "running", "completed", "failed"] = "pending"
    logs: List[str] = []
    completed_at: Optional[datetime] = None

    @classmethod
    def create(cls, title: str, description: str, created_by: str) -> "Task":
        """创建新任务"""
        return cls(
            title=title,
            description=description,
            created_by=created_by,
            status="pending"
        )

    def complete(self) -> None:
        """完成任务"""
        self.status = "completed"
        self.completed_at = datetime.utcnow()
        self.updated_at = datetime.utcnow()

    def update(self, title: Optional[str] = None, description: Optional[str] = None) -> None:
        """更新任务信息"""
        if title is not None:
            self.title = title
        if description is not None:
            self.description = description
        self.updated_at = datetime.utcnow() 