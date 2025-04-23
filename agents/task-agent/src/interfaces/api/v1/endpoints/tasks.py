"""
Task endpoints.
"""
from typing import List
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession

from src.domain.model import Task
from src.infrastructure.database import get_db
from src.application.services.task_service import TaskService

router = APIRouter()

@router.get("/", response_model=List[Task])
async def list_tasks(db: AsyncSession = Depends(get_db)):
    """获取任务列表"""
    service = TaskService(db)
    return await service.list_tasks()

@router.post("/", response_model=Task)
async def create_task(task: Task, db: AsyncSession = Depends(get_db)):
    """创建新任务"""
    service = TaskService(db)
    return await service.create_task(task)

@router.get("/{task_id}", response_model=Task)
async def get_task(task_id: str):
    """
    Get task by ID.
    """
    return {"id": task_id} 