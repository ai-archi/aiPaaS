from typing import Dict, Any, Literal
from .base import Entity

class Message(Entity):
    """Message 领域模型"""
    sender: str
    receiver: str
    type: Literal["task", "reply", "info", "log"]
    payload: Dict[str, Any] = {} 