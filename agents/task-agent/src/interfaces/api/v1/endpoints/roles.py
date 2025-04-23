"""
Role endpoints.
"""
from typing import List
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession

from src.domain.model import Role
from src.infrastructure.database import get_db
from src.application.services.role_service import RoleService

router = APIRouter()

@router.get("/", response_model=List[Role])
async def list_roles(db: AsyncSession = Depends(get_db)):
    """获取角色列表"""
    service = RoleService(db)
    return await service.list_roles()

@router.post("/", response_model=Role)
async def create_role(role: Role, db: AsyncSession = Depends(get_db)):
    """创建新角色"""
    service = RoleService(db)
    return await service.create_role(role)

@router.get("/{role_id}", response_model=Role)
async def get_role(role_id: str):
    """
    Get role by ID.
    """
    return {"id": role_id} 