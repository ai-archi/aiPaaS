from datetime import datetime
from typing import Optional
from uuid import UUID

from ..aggregates.base import AggregateRoot
from ..events.base import DomainEvent
from ..value_objects.task_status import TaskStatus, TaskStatusEnum

class Task(AggregateRoot):
    """任务聚合根"""
    title: str
    description: Optional[str] = None
    status: TaskStatus
    due_date: Optional[datetime] = None
    assigned_to: Optional[UUID] = None
    
    @classmethod
    def create(cls, title: str, description: Optional[str] = None, 
               due_date: Optional[datetime] = None) -> "Task":
        """创建新任务"""
        task = cls(
            title=title,
            description=description,
            status=TaskStatus(value=TaskStatusEnum.PENDING),
            due_date=due_date
        )
        
        # 添加任务创建事件
        task.add_domain_event(DomainEvent(
            event_type="task_created",
            aggregate_id=task.id,
            data={"title": title, "status": str(task.status)}
        ))
        
        return task
    
    def assign(self, user_id: UUID) -> None:
        """分配任务"""
        self.assigned_to = user_id
        self.add_domain_event(DomainEvent(
            event_type="task_assigned",
            aggregate_id=self.id,
            data={"assigned_to": str(user_id)}
        ))
    
    def start(self) -> None:
        """开始任务"""
        if self.status.value != TaskStatusEnum.PENDING:
            raise ValueError("Task can only be started from pending status")
        
        self.status = TaskStatus(value=TaskStatusEnum.IN_PROGRESS)
        self.add_domain_event(DomainEvent(
            event_type="task_started",
            aggregate_id=self.id,
            data={"status": str(self.status)}
        ))
    
    def complete(self) -> None:
        """完成任务"""
        if self.status.value != TaskStatusEnum.IN_PROGRESS:
            raise ValueError("Task can only be completed from in_progress status")
        
        self.status = TaskStatus(value=TaskStatusEnum.COMPLETED)
        self.add_domain_event(DomainEvent(
            event_type="task_completed",
            aggregate_id=self.id,
            data={"status": str(self.status)}
        ))
    
    def cancel(self) -> None:
        """取消任务"""
        if self.status.value == TaskStatusEnum.COMPLETED:
            raise ValueError("Completed task cannot be cancelled")
        
        self.status = TaskStatus(value=TaskStatusEnum.CANCELLED)
        self.add_domain_event(DomainEvent(
            event_type="task_cancelled",
            aggregate_id=self.id,
            data={"status": str(self.status)}
        )) 