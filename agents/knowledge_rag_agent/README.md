# Knowledge RAG Agent

基于 FastAPI 和向量数据库实现的知识检索增强生成(RAG)服务。采用领域驱动设计(DDD)和命令查询职责分离(CQRS)架构模式。

## 功能特性

- 基于 FastAPI 的高性能异步 API 服务
- 使用向量数据库进行相似度检索
- 支持文档导入和知识库管理
- 采用 DDD 分层架构，实现关注点分离
- 使用 CQRS 模式处理命令和查询
- 支持配置热重载
- 提供健康检查接口

## 目录结构

```
knowledge_rag_agent/
├── config.yaml         # 主配置文件
├── data/              # 数据目录
│   ├── vector_db/     # 向量数据库文件
│   ├── storage/       # 文档存储
│   └── temp/          # 临时文件
├── docs/              # 文档
├── logs/              # 日志文件
├── src/               # 源代码
└── requirements.txt   # 依赖管理
```

## 安装

1. 创建并激活虚拟环境（推荐）：

```bash
python -m venv .venv
source .venv/bin/activate  # Linux/Mac
# 或
.venv\Scripts\activate  # Windows
```

2. 安装依赖：

```bash
pip install -r requirements.txt
```

## 配置

主要配置文件为 `config.yaml`，支持以下配置项：

```yaml
server:
  host: 0.0.0.0        # 服务监听地址
  port: 8002           # 服务端口
  reload: true         # 是否启用热重载
  workers: 1           # 工作进程数

data:
  base_dir: data       # 基础数据目录
  vector_db_dir: data/vector_db  # 向量数据库目录
  storage_dir: data/storage      # 存储目录
  temp_dir: data/temp           # 临时目录

logging:
  level: INFO          # 日志级别
  format: "{time:YYYY-MM-DD HH:mm:ss} | {level} | {message}"  # 日志格式
  file: logs/knowledge_rag.log   # 日志文件路径
```

## 启动服务

有两种方式启动服务：

1. 使用 Python 直接启动：

```bash
python src/main.py
```

2. 使用 uvicorn 启动（推荐，支持热重载）：

```bash
uvicorn src.main:app --host 0.0.0.0 --port 8002 --reload --log-level info
```

服务启动后，可以通过以下地址访问：

- API 服务：http://localhost:8002
- API 文档：http://localhost:8002/docs
- ReDoc 文档：http://localhost:8002/redoc

## API 接口

### 健康检查

```bash
curl http://localhost:8002/
```

## 开发指南

本项目采用 DDD 架构，代码组织如下：

- `src/application/`: 应用服务层，处理用例编排
- `src/domain/`: 领域层，包含核心业务逻辑
- `src/infrastructure/`: 基础设施层，实现技术细节
- `src/interfaces/`: 接口层，处理 HTTP 请求
- `src/bootstrap/`: 启动相关代码

## 许可证

MIT 