from typing import Dict, Any
from .base import Entity

class Workspace(Entity):
    """Workspace 领域模型"""
    name: str
    files: Dict[str, str] = {}         # 文件路径到内容
    variables: Dict[str, Any] = {}     # 上下文变量
    shared_context: Dict[str, Any] = {} 