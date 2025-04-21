from datetime import datetime
from typing import List, Optional, Dict, Any
from sqlalchemy.orm import Session
from sqlalchemy.exc import SQLAlchemyError

from ..models.task import Task

class TaskService:
    def __init__(self, db_session: Session):
        self.db = db_session

    def create_task(
        self,
        title: str,
        created_by: str,
        description: Optional[str] = None,
        priority: str = "medium",
        assigned_to: Optional[str] = None,
        metadata: Optional[Dict[str, Any]] = None
    ) -> Task:
        """创建新任务"""
        try:
            task = Task(
                title=title,
                description=description,
                priority=priority,
                created_by=created_by,
                assigned_to=assigned_to,
                metadata=str(metadata) if metadata else None
            )
            self.db.add(task)
            self.db.commit()
            self.db.refresh(task)
            return task
        except SQLAlchemyError as e:
            self.db.rollback()
            raise Exception(f"创建任务失败: {str(e)}")

    def get_task(self, task_id: int) -> Optional[Task]:
        """获取单个任务"""
        return self.db.query(Task).filter(Task.id == task_id).first()

    def get_tasks(
        self,
        status: Optional[str] = None,
        priority: Optional[str] = None,
        created_by: Optional[str] = None,
        assigned_to: Optional[str] = None
    ) -> List[Task]:
        """获取任务列表，支持多种过滤条件"""
        query = self.db.query(Task)
        
        if status:
            query = query.filter(Task.status == status)
        if priority:
            query = query.filter(Task.priority == priority)
        if created_by:
            query = query.filter(Task.created_by == created_by)
        if assigned_to:
            query = query.filter(Task.assigned_to == assigned_to)
            
        return query.all()

    def update_task(
        self,
        task_id: int,
        **kwargs
    ) -> Optional[Task]:
        """更新任务信息"""
        try:
            task = self.get_task(task_id)
            if not task:
                return None

            for key, value in kwargs.items():
                if hasattr(task, key):
                    setattr(task, key, value)
                    
            if "status" in kwargs and kwargs["status"] == "completed":
                task.completed_at = datetime.utcnow()

            self.db.commit()
            self.db.refresh(task)
            return task
        except SQLAlchemyError as e:
            self.db.rollback()
            raise Exception(f"更新任务失败: {str(e)}")

    def delete_task(self, task_id: int) -> bool:
        """删除任务"""
        try:
            task = self.get_task(task_id)
            if not task:
                return False
                
            self.db.delete(task)
            self.db.commit()
            return True
        except SQLAlchemyError as e:
            self.db.rollback()
            raise Exception(f"删除任务失败: {str(e)}")

    def update_task_status(
        self,
        task_id: int,
        status: str
    ) -> Optional[Task]:
        """更新任务状态"""
        return self.update_task(task_id, status=status) 