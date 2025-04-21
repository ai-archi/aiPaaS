from datetime import datetime
from typing import Optional
from uuid import UUID
from pydantic import BaseModel

class Query(BaseModel):
    """查询基类"""
    query_id: UUID
    timestamp: datetime = datetime.utcnow()

class GetTaskQuery(Query):
    """获取单个任务查询"""
    task_id: UUID

class ListTasksQuery(Query):
    """获取任务列表查询"""
    status: Optional[str] = None
    assigned_to: Optional[UUID] = None
    page: int = 1
    page_size: int = 20
    sort_by: str = "created_at"
    sort_order: str = "desc" 