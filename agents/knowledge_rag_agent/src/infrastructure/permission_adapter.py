from typing import Dict, Any, List
from domain.models import Chunk, ResourceAttribute
from domain.ports import PermissionServicePort

class PermissionServiceClient(PermissionServicePort):
    async def get_user_attributes(self, user_id: str) -> Dict[str, Any]:
        # TODO: 查询用户属性
        ...

    async def filter_chunks(
        self,
        user_attributes: Dict[str, Any],
        chunks: List[Chunk],
        chunk_attributes: List[ResourceAttribute]
    ) -> List[Chunk]:
        # TODO: 权限过滤逻辑
        ... 