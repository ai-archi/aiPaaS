from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from .interfaces.api import router as api_router

app = FastAPI(
    title="FastMCP Architect Assistant",
    description="架构师助手服务 - 基于FastMCP的架构设计辅助工具",
    version="0.1.0",
)

# 配置CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 在生产环境中应该设置具体的域名
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 包含API路由
app.include_router(api_router)

@app.get("/")
async def root():
    return {
        "message": "Welcome to FastMCP Architect Assistant",
        "status": "running",
        "version": "0.1.0"
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000) 