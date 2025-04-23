from typing import Dict, Any, Callable
from .base import Entity

class Tool(Entity):
    """Tool 领域模型"""
    name: str
    description: str
    input_schema: Dict[str, Any]
    output_schema: Dict[str, Any]
    execute: Callable[[Dict[str, Any]], Dict[str, Any]]  # 函数式执行器 