from typing import List, Optional, Any
from datetime import datetime
from pydantic import BaseModel, Field

class Document(BaseModel):
    id: int
    title: str
    source: str
    created_by: str
    created_at: datetime
    updated_at: datetime
    status: str
    description: Optional[str] = None
    content_hash: str

class Chunk(BaseModel):
    id: int
    document_id: int
    content: str
    embedding: Optional[List[float]] = None
    order: int
    created_at: datetime
    status: str

class ResourceAttribute(BaseModel):
    id: int
    resource_id: int
    attribute_name: str
    value: str
    resource_type: str
    created_at: datetime

class AttributeDefinition(BaseModel):
    name: str
    display_name: Optional[str] = None
    type: Optional[str] = None
    options: Optional[List[Any]] = None
    required: Optional[bool] = None
    searchable: Optional[bool] = None
    description: Optional[str] = None 