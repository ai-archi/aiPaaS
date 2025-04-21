from datetime import datetime
from typing import Any, Dict
from uuid import UUID, uuid4
from pydantic import BaseModel, Field

class DomainEvent(BaseModel):
    """基础领域事件类"""
    event_id: UUID = Field(default_factory=uuid4)
    event_type: str
    aggregate_id: UUID
    timestamp: datetime = Field(default_factory=datetime.utcnow)
    data: Dict[str, Any]
    
    class Config:
        from_attributes = True 