from typing import List
from domain.ports import EmbeddingServicePort

class EmbeddingServiceClient(EmbeddingServicePort):
    async def embed(self, text: str) -> List[float]:
        # TODO: 调用实际嵌入服务API
        ...

    async def batch_embed(self, texts: List[str]) -> List[List[float]]:
        # TODO: 批量调用实际嵌入服务API
        ... 