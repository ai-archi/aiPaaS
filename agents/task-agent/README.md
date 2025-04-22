# Task Management Service

基于 FastAPI 和 DDD 架构的任务管理服务。

## 功能特点

- 任务的 CRUD 操作
- 异步数据库操作
- 基于 FastAPI 的 RESTful API
- 使用 Pydantic 进行数据验证
- 遵循 DDD 架构设计

## 安装

1. 克隆仓库：
```bash
git clone <repository-url>
cd task-agent
```

2. 创建虚拟环境：
```bash
python -m venv venv
source venv/bin/activate  # Linux/Mac
# 或
.\venv\Scripts\activate  # Windows
```

3. 安装依赖：
```bash
pip install -r requirements.txt
```

4. 配置环境变量：
创建 `.env` 文件并设置以下变量：
```
POSTGRES_SERVER=localhost
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=task_management
```

## 运行服务

```bash
python run.py
```

服务将在 http://localhost:8000 启动。

API 文档可在以下地址访问：
- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc

## API 端点

- `GET /api/v1/tasks/`: 获取任务列表
- `POST /api/v1/tasks/`: 创建新任务
- `GET /api/v1/tasks/{task_id}`: 获取特定任务
- `PUT /api/v1/tasks/{task_id}`: 更新任务
- `DELETE /api/v1/tasks/{task_id}`: 删除任务

## 项目结构

```
task-agent/
├── app/
│   ├── api/
│   │   ├── v1/
│   │   │   ├── endpoints/
│   │   │   │   └── tasks.py
│   │   │   └── api.py
│   │   └── deps.py
│   ├── core/
│   │   └── config.py
│   ├── crud/
│   │   └── task.py
│   ├── db/
│   │   ├── base_class.py
│   │   └── session.py
│   ├── models/
│   │   └── task.py
│   ├── schemas/
│   │   └── task.py
│   └── main.py
├── requirements.txt
├── run.py
└── README.md
``` 