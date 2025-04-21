from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.orm import Session

from ...db.session import get_db
from ...schemas.task import TaskCreate, TaskUpdate, TaskResponse
from ...services.task_service import TaskService
from ...core.auth import get_current_user

router = APIRouter()

@router.post("/", response_model=TaskResponse)
def create_task(
    task: TaskCreate,
    db: Session = Depends(get_db),
    current_user: str = Depends(get_current_user)
):
    """创建新任务"""
    task_service = TaskService(db)
    try:
        db_task = task_service.create_task(
            title=task.title,
            description=task.description,
            priority=task.priority,
            created_by=current_user,
            assigned_to=task.assigned_to,
            metadata=task.metadata
        )
        return db_task
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@router.get("/{task_id}", response_model=TaskResponse)
def get_task(
    task_id: int,
    db: Session = Depends(get_db),
    _: str = Depends(get_current_user)
):
    """获取单个任务"""
    task_service = TaskService(db)
    task = task_service.get_task(task_id)
    if not task:
        raise HTTPException(status_code=404, detail="任务不存在")
    return task

@router.get("/", response_model=List[TaskResponse])
def get_tasks(
    status: Optional[str] = Query(None, pattern="^(pending|in_progress|completed|failed)$"),
    priority: Optional[str] = Query(None, pattern="^(low|medium|high|urgent)$"),
    created_by: Optional[str] = None,
    assigned_to: Optional[str] = None,
    db: Session = Depends(get_db),
    _: str = Depends(get_current_user)
):
    """获取任务列表"""
    task_service = TaskService(db)
    return task_service.get_tasks(
        status=status,
        priority=priority,
        created_by=created_by,
        assigned_to=assigned_to
    )

@router.put("/{task_id}", response_model=TaskResponse)
def update_task(
    task_id: int,
    task_update: TaskUpdate,
    db: Session = Depends(get_db),
    _: str = Depends(get_current_user)
):
    """更新任务"""
    task_service = TaskService(db)
    updated_task = task_service.update_task(
        task_id,
        **task_update.model_dump(exclude_unset=True)
    )
    if not updated_task:
        raise HTTPException(status_code=404, detail="任务不存在")
    return updated_task

@router.delete("/{task_id}")
def delete_task(
    task_id: int,
    db: Session = Depends(get_db),
    _: str = Depends(get_current_user)
):
    """删除任务"""
    task_service = TaskService(db)
    if not task_service.delete_task(task_id):
        raise HTTPException(status_code=404, detail="任务不存在")
    return {"message": "任务已删除"}

@router.put("/{task_id}/status", response_model=TaskResponse)
def update_task_status(
    task_id: int,
    status: str = Query(..., pattern="^(pending|in_progress|completed|failed)$"),
    db: Session = Depends(get_db),
    _: str = Depends(get_current_user)
):
    """更新任务状态"""
    task_service = TaskService(db)
    updated_task = task_service.update_task_status(task_id, status)
    if not updated_task:
        raise HTTPException(status_code=404, detail="任务不存在")
    return updated_task 