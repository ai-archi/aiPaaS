from typing import List, Optional
from datetime import datetime
from uuid import uuid4

from .entities import ArchitectureDesign, Component, ArchitecturalDecision

async def create_architecture_design(
    name: str,
    description: str,
    version: str = "0.1.0",
) -> ArchitectureDesign:
    """创建新的架构设计"""
    return ArchitectureDesign(
        id=str(uuid4()),
        name=name,
        description=description,
        version=version,
        status="草稿",
        components=[],
        decisions=[],
    )

async def add_component_to_design(
    design: ArchitectureDesign,
    name: str,
    type: str,
    description: str,
    tech_stack: List[str],
    dependencies: List[str] = None,
) -> Component:
    """向架构设计中添加新组件"""
    component = Component(
        id=str(uuid4()),
        name=name,
        type=type,
        description=description,
        tech_stack=tech_stack,
        dependencies=dependencies or [],
    )
    design.components.append(component)
    design.updated_at = datetime.now()
    return component

async def add_architectural_decision(
    design: ArchitectureDesign,
    title: str,
    context: str,
    decision: str,
    consequences: str,
) -> ArchitecturalDecision:
    """添加架构决策记录"""
    adr = ArchitecturalDecision(
        id=str(uuid4()),
        title=title,
        status="提议",
        context=context,
        decision=decision,
        consequences=consequences,
    )
    design.decisions.append(adr)
    design.updated_at = datetime.now()
    return adr

async def update_design_status(
    design: ArchitectureDesign,
    new_status: str,
) -> ArchitectureDesign:
    """更新架构设计状态"""
    valid_statuses = ["草稿", "评审中", "已批准", "已实施"]
    if new_status not in valid_statuses:
        raise ValueError(f"无效的状态值。有效值为: {', '.join(valid_statuses)}")
    
    design.status = new_status
    design.updated_at = datetime.now()
    return design 