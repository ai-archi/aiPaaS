from typing import List, Optional, Literal
from .base import Entity

class StepTemplate(Entity):
    """Step Template 领域模型"""
    name: str
    prompt: str
    input_vars: List[str]
    output_vars: List[str]
    tool_id: Optional[str] = None
    type: Literal["prompt", "tool", "chat"] 