from datetime import datetime
from typing import List, Optional
from pydantic import BaseModel, Field

class ArchitecturalDecision(BaseModel):
    """架构决策记录(ADR)实体"""
    id: str = Field(..., description="决策ID")
    title: str = Field(..., description="决策标题")
    status: str = Field(..., description="决策状态：提议/接受/拒绝/废弃")
    context: str = Field(..., description="决策上下文")
    decision: str = Field(..., description="决策内容")
    consequences: str = Field(..., description="决策后果")
    created_at: datetime = Field(default_factory=datetime.now)
    updated_at: datetime = Field(default_factory=datetime.now)

class Component(BaseModel):
    """架构组件实体"""
    id: str = Field(..., description="组件ID")
    name: str = Field(..., description="组件名称")
    type: str = Field(..., description="组件类型：服务/库/工具等")
    description: str = Field(..., description="组件描述")
    dependencies: List[str] = Field(default_factory=list, description="依赖的其他组件ID列表")
    tech_stack: List[str] = Field(default_factory=list, description="技术栈")
    created_at: datetime = Field(default_factory=datetime.now)
    updated_at: datetime = Field(default_factory=datetime.now)

class ArchitectureDesign(BaseModel):
    """架构设计实体"""
    id: str = Field(..., description="设计ID")
    name: str = Field(..., description="设计名称")
    description: str = Field(..., description="设计描述")
    components: List[Component] = Field(default_factory=list, description="包含的组件列表")
    decisions: List[ArchitecturalDecision] = Field(default_factory=list, description="相关的架构决策")
    version: str = Field(..., description="版本号")
    status: str = Field(..., description="设计状态：草稿/评审中/已批准/已实施")
    created_at: datetime = Field(default_factory=datetime.now)
    updated_at: datetime = Field(default_factory=datetime.now) 