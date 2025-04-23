"""
Agent endpoints.
"""
from typing import List
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession

from src.domain.model import Agent
from src.infrastructure.database import get_db
from src.application.services.agent_service import AgentService

router = APIRouter()

@router.get("/", response_model=List[Agent])
async def list_agents(db: AsyncSession = Depends(get_db)):
    """获取智能体列表"""
    service = AgentService(db)
    return await service.list_agents()

@router.post("/", response_model=Agent)
async def create_agent(agent: Agent, db: AsyncSession = Depends(get_db)):
    """创建新智能体"""
    service = AgentService(db)
    return await service.create_agent(agent)

@router.get("/{agent_id}", response_model=Agent)
async def get_agent(agent_id: str):
    """
    Get agent by ID.
    """
    return {"id": agent_id} 