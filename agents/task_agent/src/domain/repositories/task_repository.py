from typing import List, Optional, Protocol
from uuid import UUID

from ..aggregates.task import Task
from ..value_objects.task_status import TaskStatus

class TaskRepository(Protocol):
    """任务仓储接口"""
    async def get_by_id(self, task_id: UUID) -> Optional[Task]:
        """根据ID获取任务"""
        ...
    
    async def list_tasks(
        self,
        status: Optional[TaskStatus] = None,
        assigned_to: Optional[UUID] = None,
        page: int = 1,
        page_size: int = 20,
        sort_by: str = "created_at",
        sort_order: str = "desc"
    ) -> List[Task]:
        """获取任务列表"""
        ...
    
    async def save(self, task: Task) -> Task:
        """保存任务"""
        ...
    
    async def delete(self, task_id: UUID) -> None:
        """删除任务"""
        ... 