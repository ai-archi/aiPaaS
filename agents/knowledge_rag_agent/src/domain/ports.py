from typing import List, Dict, Any, Protocol
from .models import Chunk, ResourceAttribute

class EmbeddingServicePort(Protocol):
    async def embed(self, text: str) -> List[float]:
        ...

    async def batch_embed(self, texts: List[str]) -> List[List[float]]:
        ...

class VectorDBPort(Protocol):
    async def search(self, query_embedding: List[float], filter_params: Dict[str, Any]) -> List[Chunk]:
        ...

class LLMServicePort(Protocol):
    async def generate(self, context: str, question: str) -> str:
        ...

class PermissionServicePort(Protocol):
    async def get_user_attributes(self, user_id: str) -> Dict[str, Any]:
        ...

    async def filter_chunks(
        self,
        user_attributes: Dict[str, Any],
        chunks: List[Chunk],
        chunk_attributes: List[ResourceAttribute]
    ) -> List[Chunk]:
        ... 