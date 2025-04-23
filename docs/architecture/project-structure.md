# 智能平台 项目目录结构

```
.
├── README.md                    # 项目主文档
├── LICENSE                     # 许可证文件
├── .gitignore                  # Git忽略文件
├── package.json                # Monorepo根目录包管理
├── yarn.lock                   # Yarn依赖锁定文件
├── lerna.json                  # Lerna配置文件
├── docker-compose.yml          # Docker编排配置
├── project-structure.md        # 项目结构说明文档
├── apps/                       # 应用程序目录
├── agents/                     # 智能体服务目录
│   └── task-agent/            # 任务智能体源码 (MetaGPT)
│       ├── domain/            # 领域模型
│       │   ├── base.py        # 基础实体类
│       │   ├── agent/        # Agent领域模型
│       │   ├── role/         # Role领域模型
│       │   ├── task/         # Task领域模型
│       │   ├── workspace/    # Workspace领域模型
│       │   ├── tool/         # Tool领域模型
│       │   ├── step/         # Step领域模型
│       │   └── message/      # Message领域模型
│       ├── .env              # 环境配置文件
│       ├── requirements.txt   # Python依赖
│       ├── run.py            # 服务启动脚本
│       ├── Dockerfile        # Docker构建文件
│       ├── docker-compose.yml # Docker编排配置
│       ├── Makefile          # 构建脚本
│       └── README.md         # 项目说明文档
├── services/                   # 微服务目录
├── libs/                       # 共享库目录
├── tools/                     # 开发工具和脚本
├── docs/                      # 项目文档
│   ├── CODE_OF_CONDUCT.md    # 行为准则
│   ├── CONTRIBUTING.md       # 贡献指南
│   ├── README.md             # 文档说明
│   ├── architecture/         # 架构文档
│   │   ├── overview.md      # 架构概览
│   │   └── template.md      # 架构模板
│   └── packages/            # 各包文档
│       └── task-agent/      # 任务智能体文档
│           ├── CHANGELOG.md  # 变更日志
│           ├── README.md     # 说明文档
│           ├── architecture/ # 架构文档
│           │   ├── README.md
│           │   └── architecture.md
│           └── quickstart/   # 快速开始指南
│               └── README.md
└── deploy/                    # 部署配置

## 目录说明

### 1. agents/
智能体服务目录，目前包含：
- task-agent: 基于 MetaGPT 的任务管理智能体，采用 DDD 架构设计

### 2. docs/
项目文档目录，包含：
- 架构文档
- 贡献指南
- 各个包的详细文档

### 3. libs/
共享库目录

### 4. tools/
开发工具和脚本目录

### 5. deploy/
部署相关的配置文件目录

## 技术栈说明

### task-agent 技术栈：
- **框架**: FastAPI
- **架构**: DDD (Domain-Driven Design)
- **模式**: CQRS (Command Query Responsibility Segregation)
- **数据库**: 支持 SQLite/PostgreSQL
- **依赖管理**: Poetry/pip
- **容器化**: Docker
- **文档**: OpenAPI/Swagger

## 架构模式说明

### 1. DDD分层架构
- **领域层**: 包含核心业务逻辑和领域模型
- **应用层**: 处理用例和业务流程编排
- **基础设施层**: 提供技术实现细节
- **接口层**: 处理外部请求和响应

### 2. CQRS模式
- 分离命令和查询职责
- 优化读写性能
- 支持事件溯源

### 3. 配置管理
- 使用环境变量和配置文件
- 支持多环境配置

### 4. 部署方案
- Docker容器化部署
- 支持单机和集群部署
- 提供完整的部署文档和脚本
