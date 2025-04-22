from typing import List, Any
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession

from app import crud
from app.api import deps
from app.schemas.task import Task, TaskCreate, TaskUpdate

router = APIRouter()


@router.get("/", response_model=List[Task])
async def read_tasks(
    db: AsyncSession = Depends(deps.get_db),
    skip: int = 0,
    limit: int = 100,
) -> Any:
    """
    获取任务列表
    """
    tasks = await crud.task.get_tasks(db, skip=skip, limit=limit)
    return tasks


@router.post("/", response_model=Task)
async def create_task(
    *,
    db: AsyncSession = Depends(deps.get_db),
    task_in: TaskCreate,
) -> Any:
    """
    创建新任务
    """
    task = await crud.task.create_task(db, obj_in=task_in)
    return task


@router.get("/{task_id}", response_model=Task)
async def read_task(
    *,
    db: AsyncSession = Depends(deps.get_db),
    task_id: int,
) -> Any:
    """
    通过ID获取任务
    """
    task = await crud.task.get_task(db, task_id=task_id)
    if not task:
        raise HTTPException(status_code=404, detail="Task not found")
    return task


@router.put("/{task_id}", response_model=Task)
async def update_task(
    *,
    db: AsyncSession = Depends(deps.get_db),
    task_id: int,
    task_in: TaskUpdate,
) -> Any:
    """
    更新任务
    """
    task = await crud.task.get_task(db, task_id=task_id)
    if not task:
        raise HTTPException(status_code=404, detail="Task not found")
    task = await crud.task.update_task(db, db_obj=task, obj_in=task_in)
    return task


@router.delete("/{task_id}", response_model=Task)
async def delete_task(
    *,
    db: AsyncSession = Depends(deps.get_db),
    task_id: int,
) -> Any:
    """
    删除任务
    """
    task = await crud.task.delete_task(db, task_id=task_id)
    if not task:
        raise HTTPException(status_code=404, detail="Task not found")
    return task 