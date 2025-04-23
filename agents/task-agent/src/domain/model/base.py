from pydantic import BaseModel, Field
from typing import Optional
from datetime import datetime
from uuid import UUID, uuid4

class Entity(BaseModel):
    """所有领域实体的基类"""
    id: UUID = Field(default_factory=uuid4)
    created_at: datetime = Field(default_factory=datetime.utcnow)
    updated_at: datetime = Field(default_factory=datetime.utcnow)
    
    class Config:
        from_attributes = True 