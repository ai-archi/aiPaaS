from enum import Enum
from pydantic import BaseModel
from typing import List, Dict, Any, Optional

class WorkflowStatus(str, Enum):
    active = "active"         # 启用
    inactive = "inactive"     # 停用
    archived = "archived"     # 已归档/删除

class Step(BaseModel):
    id: str
    name: str
    action: str
    params: Optional[Dict[str, Any]] = None
    order: int

class Workflow(BaseModel):
    id: str
    name: str
    steps: List[Step]
    status: WorkflowStatus = WorkflowStatus.active 