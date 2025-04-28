from typing import List, Dict, Any, Optional
from domain.models import Chunk, ResourceAttribute, Document
from domain.services import retrieve_chunks, assemble_context, generate_answer, filter_chunks_by_permission, DocumentService
from domain.ports import VectorDBPort, LLMServicePort, PermissionServicePort

async def qa_query(
    user_id: str,
    question: str,
    query_embedding: List[float],
    filter_params: Dict[str, Any],
    vector_db_client: VectorDBPort,
    llm_client: LLMServicePort,
    permission_client: PermissionServicePort
) -> Dict[str, Any]:
    """
    用户问答主流程：检索、权限过滤、生成答案。
    """
    chunks = await retrieve_chunks(query_embedding, filter_params, vector_db_client)
    chunk_attributes: List[ResourceAttribute] = []  # 实际应由infra层查询
    filtered_chunks = await filter_chunks_by_permission(user_id, chunks, chunk_attributes, permission_client)
    context = assemble_context(filtered_chunks, question)
    answer = await generate_answer(context, question, llm_client)
    return {
        "answer": answer,
        "chunks": filtered_chunks
    }

async def get_document(document_id: str) -> Optional[Document]:
    return await DocumentService.get(document_id)

async def list_documents() -> List[Document]:
    return await DocumentService.list_all() 