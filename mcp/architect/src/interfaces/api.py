from fastapi import APIRouter, HTTPException
from typing import List

from ..application import commands, queries
from ..domain.entities import ArchitectureDesign, Component, ArchitecturalDecision

router = APIRouter(prefix="/api/v1")

@router.post("/designs", response_model=ArchitectureDesign)
async def create_design(cmd: commands.CreateDesignCommand):
    """创建新的架构设计"""
    return await commands.handle_create_design(cmd)

@router.get("/designs", response_model=List[ArchitectureDesign])
async def list_designs(status: str = None, page: int = 1, page_size: int = 10):
    """获取架构设计列表"""
    query = queries.ListDesignsQuery(
        status=status,
        page=page,
        page_size=page_size
    )
    return await queries.handle_list_designs(query)

@router.get("/designs/{design_id}", response_model=ArchitectureDesign)
async def get_design(design_id: str):
    """获取特定架构设计"""
    query = queries.DesignQuery(design_id=design_id)
    design = await queries.handle_get_design(query)
    if not design:
        raise HTTPException(status_code=404, detail="设计不存在")
    return design

@router.post("/designs/{design_id}/components", response_model=Component)
async def add_component(design_id: str, cmd: commands.AddComponentCommand):
    """向架构设计添加组件"""
    design = await queries.handle_get_design(queries.DesignQuery(design_id=design_id))
    if not design:
        raise HTTPException(status_code=404, detail="设计不存在")
    return await commands.handle_add_component(design, cmd)

@router.get("/designs/{design_id}/components/{component_id}", response_model=Component)
async def get_component(design_id: str, component_id: str):
    """获取特定组件"""
    query = queries.ComponentQuery(design_id=design_id, component_id=component_id)
    component = await queries.handle_get_component(query)
    if not component:
        raise HTTPException(status_code=404, detail="组件不存在")
    return component

@router.post("/designs/{design_id}/decisions", response_model=ArchitecturalDecision)
async def add_decision(design_id: str, cmd: commands.AddDecisionCommand):
    """添加架构决策"""
    design = await queries.handle_get_design(queries.DesignQuery(design_id=design_id))
    if not design:
        raise HTTPException(status_code=404, detail="设计不存在")
    return await commands.handle_add_decision(design, cmd)

@router.get("/designs/{design_id}/decisions/{decision_id}", response_model=ArchitecturalDecision)
async def get_decision(design_id: str, decision_id: str):
    """获取特定决策"""
    query = queries.DecisionQuery(design_id=design_id, decision_id=decision_id)
    decision = await queries.handle_get_decision(query)
    if not decision:
        raise HTTPException(status_code=404, detail="决策不存在")
    return decision

@router.put("/designs/{design_id}/status", response_model=ArchitectureDesign)
async def update_design_status(design_id: str, cmd: commands.UpdateDesignStatusCommand):
    """更新架构设计状态"""
    design = await queries.handle_get_design(queries.DesignQuery(design_id=design_id))
    if not design:
        raise HTTPException(status_code=404, detail="设计不存在")
    return await commands.handle_update_status(design, cmd) 