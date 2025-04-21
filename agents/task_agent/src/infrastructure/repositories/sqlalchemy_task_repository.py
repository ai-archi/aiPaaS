from typing import List, Optional
from uuid import UUID
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from ...domain.aggregates.task import Task
from ...domain.repositories.task_repository import TaskRepository
from ...domain.value_objects.task_status import TaskStatus
from ..persistence.models import TaskModel

class SQLAlchemyTaskRepository(TaskRepository):
    """SQLAlchemy任务仓储实现"""
    
    def __init__(self, session: AsyncSession):
        self.session = session
    
    async def get_by_id(self, task_id: UUID) -> Optional[Task]:
        """根据ID获取任务"""
        stmt = select(TaskModel).where(TaskModel.id == task_id)
        result = await self.session.execute(stmt)
        task_model = result.scalar_one_or_none()
        
        if task_model is None:
            return None
            
        return Task.model_validate(task_model)
    
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
        stmt = select(TaskModel)
        
        if status:
            stmt = stmt.where(TaskModel.status == str(status))
        if assigned_to:
            stmt = stmt.where(TaskModel.assigned_to == assigned_to)
            
        # 添加排序
        if sort_order == "desc":
            stmt = stmt.order_by(getattr(TaskModel, sort_by).desc())
        else:
            stmt = stmt.order_by(getattr(TaskModel, sort_by).asc())
            
        # 添加分页
        stmt = stmt.offset((page - 1) * page_size).limit(page_size)
        
        result = await self.session.execute(stmt)
        task_models = result.scalars().all()
        
        return [Task.model_validate(task_model) for task_model in task_models]
    
    async def save(self, task: Task) -> Task:
        """保存任务"""
        task_dict = task.model_dump()
        task_dict["status"] = str(task.status)  # 转换状态为字符串
        
        task_model = TaskModel(**task_dict)
        self.session.add(task_model)
        await self.session.commit()
        await self.session.refresh(task_model)
        
        return Task.model_validate(task_model)
    
    async def delete(self, task_id: UUID) -> None:
        """删除任务"""
        stmt = select(TaskModel).where(TaskModel.id == task_id)
        result = await self.session.execute(stmt)
        task_model = result.scalar_one_or_none()
        
        if task_model:
            await self.session.delete(task_model)
            await self.session.commit() 