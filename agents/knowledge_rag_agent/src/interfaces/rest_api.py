from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from typing import List, Dict, Any
from application.command_handlers import ingest_document
from application.query_handlers import qa_query, get_document, list_documents
from domain.models import Document
from infrastructure.embedding_adapter import EmbeddingServiceClient
from infrastructure.vector_db_adapter import VectorDBClient
from infrastructure.llm_adapter import LLMServiceClient
from infrastructure.permission_adapter import PermissionServiceClient

router = APIRouter()

class DocumentIn(BaseModel):
    id: int
    content: str

class IngestRequest(BaseModel):
    content: str
    metadata: dict = {}

class IngestResponse(BaseModel):
    document_id: str

@router.post("/doc/process")
async def process_document(
    doc: DocumentIn,
    embedding_client: EmbeddingServiceClient = Depends()
):
    chunks = await ingest_document(doc, embedding_client)
    return {"chunks": [c.model_dump() for c in chunks]}

class QARequest(BaseModel):
    user_id: str
    question: str
    query_embedding: List[float]
    filter_params: Dict[str, Any]

@router.post("/qa")
async def qa(
    req: QARequest,
    vector_db_client: VectorDBClient = Depends(),
    llm_client: LLMServiceClient = Depends(),
    permission_client: PermissionServiceClient = Depends()
):
    result = await qa_query(
        req.user_id,
        req.question,
        req.query_embedding,
        req.filter_params,
        vector_db_client,
        llm_client,
        permission_client
    )
    return result

@router.post("/documents/ingest", response_model=IngestResponse)
async def ingest(request: IngestRequest):
    document_id = await ingest_document(request.content, request.metadata)
    return IngestResponse(document_id=document_id)

@router.get("/documents/{document_id}", response_model=Document)
async def get(document_id: str):
    document = await get_document(document_id)
    if not document:
        raise HTTPException(status_code=404, detail="Document not found")
    return document

@router.get("/documents", response_model=List[Document])
async def list_all():
    return await list_documents() 