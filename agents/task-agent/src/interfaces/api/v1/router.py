"""
API router configuration.
"""
from fastapi import APIRouter
from src.interfaces.api.v1.endpoints import tasks, agents, roles

api_router = APIRouter()

api_router.include_router(tasks.router, prefix="/tasks", tags=["tasks"])
api_router.include_router(agents.router, prefix="/agents", tags=["agents"])
api_router.include_router(roles.router, prefix="/roles", tags=["roles"]) 