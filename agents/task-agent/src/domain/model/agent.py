from typing import List, Dict, Any, Optional
from .base import Entity

class Agent(Entity):
    """Agent 领域模型"""
    name: str
    description: Optional[str] = None
    role_id: str
    skills: List[str] = []
    tools: List[str] = []
    config: Dict[str, Any] = {} 