from typing import List, Optional
from pydantic import BaseModel

from ..domain.entities import ArchitectureDesign, Component, ArchitecturalDecision

class DesignQuery(BaseModel):
    """架构设计查询"""
    design_id: str

class ListDesignsQuery(BaseModel):
    """列出架构设计查询"""
    status: Optional[str] = None
    page: int = 1
    page_size: int = 10

class ComponentQuery(BaseModel):
    """组件查询"""
    design_id: str
    component_id: str

class DecisionQuery(BaseModel):
    """决策查询"""
    design_id: str
    decision_id: str

# 这里先模拟一个内存存储
_designs: List[ArchitectureDesign] = []

async def handle_get_design(query: DesignQuery) -> Optional[ArchitectureDesign]:
    """处理获取设计查询"""
    return next((d for d in _designs if d.id == query.design_id), None)

async def handle_list_designs(query: ListDesignsQuery) -> List[ArchitectureDesign]:
    """处理列出设计查询"""
    filtered = _designs
    if query.status:
        filtered = [d for d in filtered if d.status == query.status]
    
    start = (query.page - 1) * query.page_size
    end = start + query.page_size
    return filtered[start:end]

async def handle_get_component(query: ComponentQuery) -> Optional[Component]:
    """处理获取组件查询"""
    design = await handle_get_design(DesignQuery(design_id=query.design_id))
    if not design:
        return None
    return next((c for c in design.components if c.id == query.component_id), None)

async def handle_get_decision(query: DecisionQuery) -> Optional[ArchitecturalDecision]:
    """处理获取决策查询"""
    design = await handle_get_design(DesignQuery(design_id=query.design_id))
    if not design:
        return None
    return next((d for d in design.decisions if d.id == query.decision_id), None) 