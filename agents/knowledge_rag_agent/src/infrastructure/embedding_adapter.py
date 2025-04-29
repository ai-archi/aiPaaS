from typing import List
from domain.ports import EmbeddingServicePort
import httpx
from config.config import settings
from mf_nacos_service_registrar import get_nacos_client, get_one_healthy_instance_url

class EmbeddingServiceClient(EmbeddingServicePort):
    def __init__(self):
        self._service_url = None

    async def _get_service_url(self) -> str:
        if self._service_url:
            return self._service_url
        client = get_nacos_client(
            server_addresses=settings.nacos.server_addresses,
            namespace=settings.nacos.namespace,
            ak=settings.nacos.username,
            sk=settings.nacos.password
        )
        self._service_url = get_one_healthy_instance_url(
            client,
            service_name="embed_serves",
            group=settings.nacos.group_name,
            schema="http"
        )
        return self._service_url

    async def embed(self, text: str) -> List[float]:
        url = f"{await self._get_service_url()}/infer"
        async with httpx.AsyncClient() as client:
            resp = await client.post(url, json={"text": text})
            resp.raise_for_status()
            return resp.json()["embedding"]

    async def batch_embed(self, texts: List[str]) -> List[List[float]]:
        url = f"{await self._get_service_url()}/embed"
        async with httpx.AsyncClient() as client:
            resp = await client.post(url, json={"texts": texts})
            resp.raise_for_status()
            return resp.json()["embeddings"] 