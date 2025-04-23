from typing import List, Optional
from pydantic import BaseModel

from ..domain import services
from ..domain.entities import ArchitectureDesign, Component, ArchitecturalDecision

class CreateDesignCommand(BaseModel):
    """创建架构设计命令"""
    name: str
    description: str
    version: Optional[str] = "0.1.0"

class AddComponentCommand(BaseModel):
    """添加组件命令"""
    design_id: str
    name: str
    type: str
    description: str
    tech_stack: List[str]
    dependencies: Optional[List[str]] = None

class AddDecisionCommand(BaseModel):
    """添加架构决策命令"""
    design_id: str
    title: str
    context: str
    decision: str
    consequences: str

class UpdateDesignStatusCommand(BaseModel):
    """更新设计状态命令"""
    design_id: str
    new_status: str

async def handle_create_design(cmd: CreateDesignCommand) -> ArchitectureDesign:
    """处理创建设计命令"""
    return await services.create_architecture_design(
        name=cmd.name,
        description=cmd.description,
        version=cmd.version,
    )

async def handle_add_component(
    design: ArchitectureDesign,
    cmd: AddComponentCommand
) -> Component:
    """处理添加组件命令"""
    return await services.add_component_to_design(
        design=design,
        name=cmd.name,
        type=cmd.type,
        description=cmd.description,
        tech_stack=cmd.tech_stack,
        dependencies=cmd.dependencies,
    )

async def handle_add_decision(
    design: ArchitectureDesign,
    cmd: AddDecisionCommand
) -> ArchitecturalDecision:
    """处理添加决策命令"""
    return await services.add_architectural_decision(
        design=design,
        title=cmd.title,
        context=cmd.context,
        decision=cmd.decision,
        consequences=cmd.consequences,
    )

async def handle_update_status(
    design: ArchitectureDesign,
    cmd: UpdateDesignStatusCommand
) -> ArchitectureDesign:
    """处理更新状态命令"""
    return await services.update_design_status(
        design=design,
        new_status=cmd.new_status,
    ) 