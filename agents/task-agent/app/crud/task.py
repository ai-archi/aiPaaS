from typing import List, Optional
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.models.task import Task
from app.schemas.task import TaskCreate, TaskUpdate


async def create_task(db: AsyncSession, *, obj_in: TaskCreate) -> Task:
    db_obj = Task(**obj_in.model_dump())
    db.add(db_obj)
    await db.commit()
    await db.refresh(db_obj)
    return db_obj


async def get_task(db: AsyncSession, task_id: int) -> Optional[Task]:
    result = await db.execute(select(Task).filter(Task.id == task_id))
    return result.scalar_one_or_none()


async def get_tasks(
    db: AsyncSession, *, skip: int = 0, limit: int = 100
) -> List[Task]:
    result = await db.execute(
        select(Task)
        .offset(skip)
        .limit(limit)
    )
    return result.scalars().all()


async def update_task(
    db: AsyncSession,
    *,
    db_obj: Task,
    obj_in: TaskUpdate
) -> Task:
    update_data = obj_in.model_dump(exclude_unset=True)
    for field, value in update_data.items():
        setattr(db_obj, field, value)
    db.add(db_obj)
    await db.commit()
    await db.refresh(db_obj)
    return db_obj


async def delete_task(db: AsyncSession, *, task_id: int) -> Optional[Task]:
    task = await get_task(db, task_id)
    if task:
        task.is_deleted = True
        db.add(task)
        await db.commit()
    return task 