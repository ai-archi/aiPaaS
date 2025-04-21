# AI PaaS 文档中心

本目录包含 AI PaaS 平台的所有技术文档。

## 文档结构

```
docs/
├── architecture/        # 架构文档
│   ├── overview.md     # 系统架构概述
│   ├── design.md       # 详细设计文档
│   └── decisions/      # 架构决策记录
│
├── development/        # 开发指南
│   ├── getting-started.md    # 入门指南
│   ├── setup.md             # 环境配置
│   ├── coding-standards.md  # 编码规范
│   └── git-workflow.md      # Git 工作流程
│
├── api/               # API 文档
│   ├── README.md     # API 概述
│   ├── auth/         # 认证相关 API
│   ├── user/         # 用户相关 API
│   └── agent/        # 智能体相关 API
│
├── deployment/       # 部署文档
│   ├── README.md    # 部署概述
│   ├── docker.md    # Docker 部署指南
│   └── k8s/         # Kubernetes 部署指南
│
└── packages/        # 各个包的具体文档
    └── task-agent/  # Task Agent 文档
```

## 快速导航

### 新手入门
- [快速开始指南](development/getting-started.md)
- [开发环境设置](development/setup.md)
- [编码规范](development/coding-standards.md)

### 架构文档
- [系统架构概述](architecture/overview.md)
- [详细设计文档](architecture/design.md)
- [技术栈说明](architecture/tech-stack.md)

### API 文档
- [API 概述](api/README.md)
- [认证 API](api/auth/README.md)
- [用户 API](api/user/README.md)
- [智能体 API](api/agent/README.md)

### 部署指南
- [部署概述](deployment/README.md)
- [Docker 部署](deployment/docker.md)
- [Kubernetes 部署](deployment/k8s/README.md)

### 项目规范
- [贡献指南](CONTRIBUTING.md)
- [行为准则](CODE_OF_CONDUCT.md)

## 文档更新

- 所有文档均使用 Markdown 格式编写
- 欢迎通过 Pull Request 提交文档改进
- 如发现文档问题，请提交 Issue

## 文档本地预览

```bash
# 安装文档工具
npm install -g docsify-cli

# 启动文档服务
docsify serve docs
```

访问 http://localhost:3000 查看文档。 