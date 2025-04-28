from typing import List, Dict, Any, Optional
from datetime import datetime
from domain.models import Document, Chunk, ResourceAttribute
from .ports import EmbeddingServicePort, VectorDBPort, LLMServicePort, PermissionServicePort

# 文档分片服务
def split_document(document_content: str, max_length: int = 500) -> List[str]:
    """
    将文档内容按 max_length 分片，返回片段列表。
    """
    if not document_content:
        return []
    return [document_content[i:i+max_length] for i in range(0, len(document_content), max_length)]

def save_chunks(document_id: int, chunks: List[str]) -> List[Chunk]:
    """
    构造 Chunk 对象列表（实际持久化由基础设施层实现）。
    """
    now = datetime.utcnow()
    return [
        Chunk(
            id=-1,  # 由数据库生成
            document_id=document_id,
            content=chunk,
            embedding=None,
            order=idx,
            created_at=now,
            status="active"
        )
        for idx, chunk in enumerate(chunks)
    ]

# 嵌入生成服务
async def generate_embedding(text: str, embedding_client: EmbeddingServicePort) -> List[float]:
    """
    调用外部嵌入服务生成单条文本的向量。
    """
    if not text:
        return []
    return await embedding_client.embed(text)

async def batch_generate_embeddings(text_list: List[str], embedding_client: EmbeddingServicePort) -> List[List[float]]:
    """
    批量生成文本向量。
    """
    if not text_list:
        return []
    return await embedding_client.batch_embed(text_list)

def save_chunk_embedding(chunk: Chunk, embedding: List[float]) -> Chunk:
    """
    返回带嵌入向量的新 Chunk 对象。
    """
    return chunk.copy(update={"embedding": embedding})

# 检索与生成服务
def assemble_context(chunks: List[Chunk], user_question: str) -> str:
    """
    拼接检索片段与用户问题，生成上下文字符串。
    """
    context = "\n".join(chunk.content for chunk in chunks)
    return f"{context}\n\nQuestion: {user_question}"

async def retrieve_chunks(
    query_embedding: List[float],
    filter_params: Dict[str, Any],
    vector_db_client: VectorDBPort
) -> List[Chunk]:
    """
    基于向量和属性过滤检索片段（实际检索由基础设施层实现）。
    """
    return await vector_db_client.search(query_embedding, filter_params)

async def generate_answer(
    context: str,
    user_question: str,
    llm_client: LLMServicePort
) -> str:
    """
    基于上下文和问题生成答案（调用外部 LLM 服务）。
    """
    return await llm_client.generate(context=context, question=user_question)

# ABAC 权限过滤服务
async def filter_chunks_by_permission(
    user_id: str,
    chunks: List[Chunk],
    chunk_attributes: List[ResourceAttribute],
    permission_client: PermissionServicePort
) -> List[Chunk]:
    """
    基于用户属性过滤可访问片段。
    """
    user_attributes = await permission_client.get_user_attributes(user_id)
    return await permission_client.filter_chunks(user_attributes, chunks, chunk_attributes)

class DocumentService:
    @staticmethod
    async def ingest(content: str, metadata: dict) -> str:
        # 这里应实现文档入库逻辑，返回文档ID
        # 示例：return await db.save_document(content, metadata)
        return "mock_document_id"

    @staticmethod
    async def get(document_id: str) -> Optional[Document]:
        # 这里应实现获取单个文档逻辑
        return None

    @staticmethod
    async def list_all() -> List[Document]:
        # 这里应实现获取所有文档逻辑
        return [] 