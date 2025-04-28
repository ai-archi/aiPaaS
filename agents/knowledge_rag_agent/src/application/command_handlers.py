from typing import List
from domain.models import Document, Chunk
from domain.services import split_document, save_chunks, batch_generate_embeddings, save_chunk_embedding, DocumentService
from domain.ports import EmbeddingServicePort

async def ingest_document(
    document: Document,
    embedding_client: EmbeddingServicePort
) -> List[Chunk]:
    """
    文档入库：分片、嵌入生成，返回片段列表（含嵌入）。
    """
    chunks_text = split_document(document.content)
    chunks = save_chunks(document.id, chunks_text)
    embeddings = await batch_generate_embeddings([c.content for c in chunks], embedding_client)
    return [
        save_chunk_embedding(chunk, embedding)
        for chunk, embedding in zip(chunks, embeddings)
    ]

async def ingest_document_content(content: str, metadata: dict) -> str:
    return await DocumentService.ingest(content, metadata) 