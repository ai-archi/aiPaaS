from typing import List
from sqlalchemy.ext.asyncio import AsyncSession
from src.domain.model import Agent

class AgentService:
    """智能体服务"""
    def __init__(self, db: AsyncSession):
        self.db = db

    async def list_agents(self) -> List[Agent]:
        """获取智能体列表"""
        # TODO: 实现从数据库获取智能体列表
        return []

    async def create_agent(self, agent: Agent) -> Agent:
        """创建新智能体"""
        # TODO: 实现保存智能体到数据库
        return agent 