# 系统架构概述

## 架构模式：微服务架构

系统采用微服务架构，所有业务功能被拆分成独立的微服务，通过 API 网关和服务间通信协议（gRPC）进行交互。

### 系统组件

- **前端应用**
  - Web 端（Next.js）
  - 移动端（React Native）
- **智能体服务**
  - Java 智能体
  - Python 智能体
- **微服务核心**
  - API 网关：Spring Cloud Gateway
  - 认证服务：Spring Security + JWT
  - 用户服务：Spring Boot
  - 配置中心：Nacos
  - 服务发现：Nacos

### 技术栈

#### 后端技术栈
- **Spring Boot**: 3.2.3
- **Spring Cloud**: 2023.0.0
- **Spring Cloud Alibaba**: 2022.0.0.0
- **Nacos**: 2.2.3
- **Sentinel**: 1.8.6
- **LangChain4j**: 0.1.x
- **gRPC**: 1.62.x
- **Protobuf**: 3.25.x

#### 前端技术栈
- **Next.js**: 14.x
- **React**: 18.x
- **React Native**: 0.73.x
- **TypeScript**: 5.x
- **Material UI**: 5.x
- **Ant Design**: 5.x

#### Python 技术栈
- **FastAPI**: 0.109.x
- **MetaGPT**: 0.7.x
- **LangChain**: 0.1.x
- **gRPC-Python**: 1.62.x

#### 监控和日志
- **Prometheus**: 2.49.x
- **Grafana**: 10.3.x
- **ELK Stack**: 8.12.x
  - Elasticsearch
  - Logstash
  - Kibana

## 系统特性

### 高可用性
- 服务注册与发现
- 负载均衡
- 熔断降级
- 限流保护

### 安全性
- OAuth2 认证
- JWT 令牌
- 细粒度权限控制
- API 网关防护

### 可扩展性
- 微服务独立部署
- 容器化支持
- 水平扩展能力
- 插件化架构

### 可维护性
- 统一技术栈
- 模块化设计
- 完善的监控
- 自动化部署

## 部署架构

### 开发环境
- Docker Compose
- 本地开发工具链
- 热重载支持

### 测试环境
- Kubernetes 集群
- CI/CD 流水线
- 自动化测试

### 生产环境
- 多区域部署
- 高可用配置
- 灾备方案
- 监控告警

## 更多信息

- [详细设计文档](./design.md)
- [API 文档](../api/README.md)
- [部署指南](../deployment/README.md) 