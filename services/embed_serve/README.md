# 嵌入模型服务（embed_serve）

基于 FastAPI + fastembed，支持多嵌入模型和多推理后端的本地嵌入 API。

## 依赖环境
- Python 3.8+
- FastAPI
- Uvicorn
- fastembed

建议在项目根目录统一使用 `.venv` 虚拟环境：

```bash
cd /Users/jin/DEVELOP/AI
python3 -m venv .venv
source .venv/bin/activate
pip install fastapi uvicorn fastembed
```

## 启动服务

```bash
cd services/embed_serve
uvicorn main:app --host 0.0.0.0 --port 8000
```

## API 说明

### 1. 获取可用模型和推理后端
- `GET /models`
- 返回：
```json
{
  "models": ["BAAI/bge-base-en-v1.5", ...],
  "providers": ["onnx", "pytorch", "tensorrt"]
}
```

### 2. 获取文本嵌入
- `POST /embed`
- 请求体：
```json
{
  "texts": ["你好世界", "Hello world"],
  "model_name": "BAAI/bge-base-en-v1.5", // 可选
  "provider": "onnx",                   // 可选
  "normalize": true,                      // 可选
  "batch_size": 32                        // 可选
}
```
- 返回：
```json
{
  "embeddings": [[...], [...]]
}
```

---
本服务可供桌面端、移动端、其他微服务通过 HTTP API 调用。 