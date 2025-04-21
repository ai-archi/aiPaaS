# MCP Server

基于 MetaGPT 实现的 MCP (Mission Control Protocol) Server，用于管理与 GPT 沟通时的任务。

## 功能特性

- 任务管理：创建、更新、删除和查询任务
- 任务状态追踪：实时监控任务执行状态
- 多 Agent 协作：支持多个 Agent 之间的任务分配和协作
- 任务历史记录：保存任务执行历史和结果
- 用户认证：支持用户认证和权限管理
- API 接口：提供 RESTful API 接口

## 技术栈

- MetaGPT：AI Agent 框架
- FastAPI：Web 框架
- SQLAlchemy：ORM
- Alembic：数据库迁移
- Poetry：依赖管理
- Pytest：测试框架

## 快速开始

1. 安装依赖：
```bash
poetry install
```

2. 设置环境变量：
```bash
cp .env.example .env
# 编辑 .env 文件，设置必要的环境变量
```

3. 运行数据库迁移：
```bash
poetry run alembic upgrade head
```

4. 启动服务：
```bash
poetry run python src/main.py
```

## 项目结构

```
src/
├── agents/         # Agent 实现
├── api/           # API 路由
├── core/          # 核心功能
├── db/            # 数据库模型和迁移
├── schemas/       # Pydantic 模型
└── utils/         # 工具函数
```

## 开发

1. 运行测试：
```bash
poetry run pytest
```

2. 代码格式化：
```bash
poetry run black .
poetry run isort .
```

3. 类型检查：
```bash
poetry run mypy .
```

## API 文档

启动服务后，访问：
- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc

## 许可证

MIT 