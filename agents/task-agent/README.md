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

# Task Agent

基于 MetaGPT 的任务管理智能体，采用 DDD 架构设计。

## 项目结构

```
agents/task-agent/
├── application/          # 应用服务层
│   ├── commands/        # 命令处理器
│   ├── queries/         # 查询处理器
│   └── services/        # 应用服务
├── infrastructure/       # 基础设施层
│   ├── persistence/     # 数据持久化
│   ├── messaging/       # 消息传递
│   └── external/        # 外部服务集成
├── interfaces/          # 接口层
│   ├── api/            # API 接口
│   ├── events/         # 事件处理
│   └── consumers/      # 消息消费者
├── domain/             # 领域层
│   ├── model/          # 领域模型
│   ├── services/       # 领域服务
│   ├── repositories/   # 仓储接口
│   └── events/         # 领域事件
├── tests/              # 测试目录
│   ├── unit/          # 单元测试
│   ├── integration/   # 集成测试
│   └── e2e/           # 端到端测试
├── config/            # 配置目录
│   ├── development.py
│   └── production.py
├── data/             # 数据文件
├── scripts/          # 工具脚本
└── requirements.txt  # 依赖管理
```

## 架构说明

项目采用领域驱动设计（DDD）和命令查询职责分离（CQRS）模式：

1. **领域层（Domain）**
   - 包含核心业务逻辑和规则
   - 定义领域模型、聚合根和值对象
   - 定义领域服务和领域事件
   - 定义仓储接口

2. **应用层（Application）**
   - 实现用例和业务流程
   - 协调领域对象和服务
   - 实现命令和查询处理器
   - 不包含业务规则

3. **基础设施层（Infrastructure）**
   - 提供技术能力
   - 实现仓储接口
   - 处理数据持久化
   - 集成外部服务

4. **接口层（Interfaces）**
   - 提供 API 接口
   - 处理请求和响应
   - 实现事件处理
   - 处理消息队列

## 开发指南

1. 安装依赖：
```bash
pip install -r requirements.txt
```

2. 配置环境：
   - 复制 `.env.example` 到 `.env`
   - 根据需要修改配置

3. 运行开发服务器：
```bash
python run.py
```

4. 运行测试：
```bash
pytest tests/
```

## Docker 部署

1. 构建镜像：
```bash
docker-compose build
```

2. 运行服务：
```bash
docker-compose up -d
```

## 贡献指南

1. Fork 项目
2. 创建特性分支
3. 提交变更
4. 推送到分支
5. 创建 Pull Request

## 许可证

MIT 