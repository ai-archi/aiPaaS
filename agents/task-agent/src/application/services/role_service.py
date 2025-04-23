from typing import List
from sqlalchemy.ext.asyncio import AsyncSession
from src.domain.model import Role

class RoleService:
    """角色服务"""
    def __init__(self, db: AsyncSession):
        self.db = db

    async def list_roles(self) -> List[Role]:
        """获取角色列表"""
        # TODO: 实现从数据库获取角色列表
        return []

    async def create_role(self, role: Role) -> Role:
        """创建新角色"""
        # TODO: 实现保存角色到数据库
        return role 