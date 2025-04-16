# 智能平台 项目目录结构

```
.
├── README.md                    # 项目主文档
├── .gitignore                  # Git忽略文件
├── package.json                # Monorepo根目录包管理
├── lerna.json                  # Lerna配置文件
├── docker-compose.yml          # Docker编排配置
├── apps/                       # 应用程序目录
│   ├── web/                    # Web前端 (Next.js)
│   │   ├── src/
│   │   ├── public/
│   │   └── package.json
│   └── mobile/                 # 移动端应用 (React Native)
│       ├── src/
│       ├── ios/
│       ├── android/
│       └── package.json
├── services/                   # 微服务目录
│   ├── api-gateway/           # API网关服务 (Spring Cloud Gateway)
│   │   ├── src/
│   │   └── pom.xml
│   ├── auth-service/          # 认证服务
│   │   ├── src/
│   │   └── pom.xml
│   └── user-service/          # 用户服务
│       ├── src/
│       └── pom.xml
├── agents/                     # 智能体服务目录
│   ├── java-agent/            # Java智能体 (LangChain4j)
│   │   ├── src/
│   │   └── pom.xml
│   └── python-agent/          # Python智能体 (MetaGPT)
│       ├── src/
│       ├── tests/
│       └── requirements.txt
├── libs/                       # 共享库目录
│   ├── common/                # 通用工具库
│   │   └── src/
│   └── proto/                 # Protobuf定义文件
│       ├── src/
│       └── build.gradle
├── tools/                     # 开发工具和脚本
│   ├── scripts/              # 构建和部署脚本
│   └── config/               # 配置文件模板
├── docs/                      # 项目文档
│   ├── architecture/         # 架构设计文档
│   ├── api/                  # API文档
│   └── development/          # 开发指南
└── deploy/                    # 部署配置
    ├── k8s/                  # Kubernetes配置
    ├── docker/               # Docker配置
    └── terraform/            # 基础设施即代码配置
```

## 目录说明

### 1. apps/
前端应用程序目录，包含Web端(Next.js)和移动端(React Native)应用。

### 2. services/
微服务目录，基于Spring Cloud + Alibaba的后端服务。

### 3. agents/
智能体服务目录，包含Java和Python实现的智能体服务。

### 4. libs/
共享库目录，包含通用代码和Protobuf定义。

### 5. tools/
开发工具和脚本目录。

### 6. docs/
项目文档目录。

### 7. deploy/
部署相关的配置文件目录。

## 技术栈说明

- **前端**: Next.js, React Native
- **后端**: Spring Cloud + Alibaba
- **智能体**: LangChain4j (Java), MetaGPT (Python)
- **通信**: gRPC + Protobuf
- **部署**: Docker + Kubernetes
- **监控**: Prometheus + Grafana
- **日志**: ELK Stack 

## 架构模式说明

### 1. 微服务架构
- 系统被拆分为多个独立的服务，每个服务可以独立部署和扩展。
- 服务之间通过API网关进行通信，实现服务解耦和灵活性。
- 使用Nacos作为服务注册与发现中心，实现服务动态管理。

### 2. 智能体架构
- 智能体服务分为Java和Python两个版本，分别使用LangChain4j和MetaGPT实现。
- 智能体服务通过gRPC进行通信，实现跨语言的智能体开发。
- 智能体服务使用Spring Cloud Gateway作为API网关，实现请求路由和过滤。

### 3. 共享库架构
- 共享库目录包含通用代码和Protobuf定义，实现跨服务共享。
- 使用Gradle构建共享库，实现跨语言的代码共享。

### 4. 配置管理
- 使用Spring Cloud Config作为配置中心，实现配置的集中管理。
- 使用Nacos作为配置中心，实现配置的动态管理。

### 5. 部署架构
- 使用Docker进行容器化部署，实现应用的快速部署和扩展。
- 使用Kubernetes进行容器编排，实现应用的自动化部署和管理。
- 使用Prometheus和Grafana进行监控和告警，实现应用的监控和告警。
- 使用ELK Stack进行日志管理，实现应用的日志管理。

### 6. 监控和告警
- 使用Prometheus和Grafana进行监控和告警，实现应用的监控和告警。
- 使用ELK Stack进行日志管理，实现应用的日志管理。

### 7. 日志管理
- 使用ELK Stack进行日志管理，实现应用的日志管理。

### 8. 基础设施即代码
- 使用Terraform进行基础设施即代码管理，实现基础设施的自动化部署和管理。

### 9. 持续集成和持续部署
- 使用Gitlab进行持续集成，采用argoCD 持续部署，实现应用的持续集成和持续部署。
