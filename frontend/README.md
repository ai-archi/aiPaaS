# AixOne Frontend Architecture

AixOne企业平台前端架构，基于微前端架构设计，支持多端开发和统一管理。

## 项目结构

```
aixone-frontend/
├── workspace/                   # 统一工作台（主应用）
│   ├── web/                    # Web端工作台
│   ├── mobile/                 # 移动端工作台
│   ├── desktop/                # 桌面端工作台
│   └── miniprogram/            # 小程序工作台
├── applications/               # 所有前端应用
│   ├── domains/                # 业务领域应用
│   │   ├── finance/            # 财务管理应用
│   │   ├── hr/                 # 人力资源应用
│   │   ├── supply/             # 供应链应用
│   │   ├── marketing/          # 市场营销应用
│   │   ├── manufacturing/      # 生产制造应用
│   │   ├── project/            # 项目管理应用
│   │   ├── asset/              # 资产管理应用
│   │   ├── collab/             # 协作办公应用
│   │   └── compliance/         # 合规管理应用
│   └── platforms/              # 平台应用
│       ├── data/               # 数据平台应用
│       ├── integration/        # 集成平台应用
│       ├── ai/                 # 智能平台应用
│       ├── tech/               # 技术平台应用
│       └── ops/                # 运维平台应用
├── shared/                     # 共享层
│   ├── components/             # 基础组件库
│   ├── utils/                  # 工具库
│   ├── types/                  # 类型定义
│   ├── styles/                 # 样式系统
│   ├── constants/              # 常量定义
│   ├── config/                 # 配置文件
│   └── assets/                 # 共享资源
├── tools/                      # 开发工具
├── docs/                       # 项目文档
├── tests/                      # 集成测试
└── .github/                    # GitHub工作流配置
```

## 技术栈

- **前端框架**: Vue 3.x + TypeScript
- **构建工具**: Vite
- **UI组件库**: Element Plus / Ant Design Vue
- **状态管理**: Pinia
- **路由管理**: Vue Router
- **跨端开发**: uni-app
- **桌面端**: Electron
- **包管理**: Lerna + npm workspaces

## 开发指南

### 环境要求

- Node.js >= 18.0.0
- npm >= 8.0.0

### 安装依赖

```bash
npm install
npm run bootstrap
```

### 开发模式

```bash
npm run dev
```

### 构建项目

```bash
npm run build
```

### 运行测试

```bash
npm run test
```

## 架构特点

- **微前端架构**: 各应用独立开发、部署、运行
- **跨端复用**: 一套代码多端运行
- **统一管理**: 通过工作台统一对外开放
- **技术统一**: 统一的技术栈和开发模式

## 文档

详细的设计文档请参考 `docs/` 目录。
