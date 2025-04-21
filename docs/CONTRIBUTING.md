# Monorepo 贡献指南

感谢你对我们的 AI Monorepo 项目的关注！本指南将帮助你了解如何为项目做出贡献。

## 目录

- [行为准则](#行为准则)
- [项目结构](#项目结构)
- [开发流程](#开发流程)
- [提交规范](#提交规范)
- [文档规范](#文档规范)
- [发布流程](#发布流程)

## 行为准则

本项目采用 [贡献者公约](./CODE_OF_CONDUCT.md) 作为行为准则。请确保你已阅读并同意遵守这些准则。

## 项目结构

本仓库采用 Monorepo 结构，使用 [Lerna](https://lerna.js.org/) 进行管理：

```
monorepo/
├── docs/                # 顶层文档
├── packages/           # 项目目录
│   ├── task-agent/    # Task Agent 项目
│   └── [其他项目]/   # 其他项目
└── scripts/           # 共享脚本
```

## 开发流程

### 1. 环境设置

```bash
# 克隆仓库
git clone https://github.com/your-org/ai-monorepo.git
cd ai-monorepo

# 安装依赖
npm run bootstrap
```

### 2. 分支管理

- `main`: 主分支，保持稳定
- `develop`: 开发分支
- `feature/*`: 新功能分支
- `fix/*`: 修复分支
- `release/*`: 发布分支

### 3. 开发步骤

1. 从 `develop` 创建新分支
```bash
git checkout develop
git pull
git checkout -b feature/your-feature
```

2. 开发并测试
```bash
# 运行特定项目
npm run dev --scope=project-name

# 运行测试
npm run test --scope=project-name
```

3. 提交代码
```bash
git add .
git commit -m "feat(scope): your message"
git push origin feature/your-feature
```

4. 创建 Pull Request

## 提交规范

我们使用 [约定式提交](https://www.conventionalcommits.org/) 规范：

```
<类型>(<作用域>): <描述>

[可选的正文]

[可选的脚注]
```

### 类型

- `feat`: 新功能
- `fix`: 修复
- `docs`: 文档更新
- `style`: 代码格式
- `refactor`: 重构
- `test`: 测试
- `chore`: 构建/工具

### 作用域

- `root`: 根目录变更
- `task-agent`: Task Agent 项目
- `docs`: 文档更新
- [其他项目名称]

### 示例

```
feat(task-agent): 添加用户认证功能

- 实现 JWT 认证
- 添加用户登录接口
- 添加用户注册接口

Closes #123
```

## 文档规范

### 1. 文档位置

- 通用文档: `/docs`
- 项目文档: `/packages/<project>/docs`
- API 文档: 使用 OpenAPI 规范

### 2. 文档格式

- 使用 Markdown 格式
- 包含清晰的标题层级
- 添加必要的代码示例
- 保持文档最新

### 3. 文档类型

- README.md: 项目说明
- API.md: API 文档
- CHANGELOG.md: 变更记录
- CONTRIBUTING.md: 贡献指南

## 发布流程

### 1. 版本管理

使用 [语义化版本](https://semver.org/):
- 主版本号: 不兼容的 API 修改
- 次版本号: 向下兼容的功能性新增
- 修订号: 向下兼容的问题修正

### 2. 发布步骤

```bash
# 更新版本
lerna version

# 发布包
lerna publish
```

### 3. 更新文档

- 更新 CHANGELOG.md
- 更新版本号
- 更新文档

## 获取帮助

- 查看 [文档中心](./README.md)
- 提交 [Issue](https://github.com/your-org/ai-monorepo/issues)
- 加入 [Discord](https://discord.gg/your-server)

## 许可证

通过贡献代码，你同意你的贡献将按照项目的 [MIT](../LICENSE) 许可证进行授权。 