from typing import List
from .base import Entity

class Role(Entity):
    """Role 领域模型"""
    name: str
    description: str
    steps: List[str] = []  # 对应的 Step 模板 ID 
    permissions: List[str] = [] 