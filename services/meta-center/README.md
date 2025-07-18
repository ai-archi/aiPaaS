# 元数据服务 (Meta Center)

## 项目简介

元数据服务是Aixone平台的核心组件，提供统一的元数据管理能力，支持动态定义数据结构、属性、关系及界面展示规则。通过元数据驱动的方式，实现低代码/无代码的数据管理和业务应用构建。

## 核心功能

### 1. 元数据管理 (Meta Management)
- **元数据定义**：支持业务元数据、技术元数据、管理元数据、参考元数据的统一管理
- **生命周期管理**：提供草稿、审核、发布、废弃、归档等完整生命周期管理
- **版本控制**：支持元数据多版本并行和回滚，保障数据变更安全
- **多租户隔离**：基于租户ID实现数据级隔离，确保租户间数据安全

### 2. 业务规则引擎 (Rule Engine)
- **规则配置**：支持校验规则、业务规则、自动化规则、流程规则、权限规则等
- **规则执行**：基于Aviator表达式引擎实现规则解析和执行
- **规则管理**：提供规则的启用/禁用、优先级设置、分组管理等功能

### 3. 流程引擎 (Process Engine)
- **流程建模**：支持可视化流程建模和流程定义管理
- **流程执行**：实现流程实例的创建、流转、节点执行、事件监听等
- **流程监控**：提供流程状态监控、性能分析、异常处理等功能

### 4. 权限服务 (Permission Service)
- **权限管理**：支持RBAC、ABAC等多种权限模型
- **数据脱敏**：提供字段级数据脱敏和访问控制
- **权限校验**：集成平台权限服务，实现统一的权限校验

### 5. 数据服务 (Data Service)
- **动态数据操作**：基于元数据自动生成数据增删改查操作
- **数据导入导出**：支持多种格式的数据导入导出功能
- **批量处理**：提供高效的数据批量处理能力

### 6. UI服务 (UI Service)
- **动态界面生成**：基于元数据自动生成表单、列表、详情等界面
- **主题定制**：支持多主题切换和租户级UI定制
- **组件管理**：提供丰富的UI组件库和组件配置管理

### 7. 集成编排 (Integration Orchestration)
- **能力聚合**：聚合各业务能力，统一对外输出API/服务
- **协议适配**：支持REST、gRPC、WebSocket等多种协议适配
- **事件驱动**：基于事件驱动的模块间解耦和异步处理

## 技术架构

### 技术栈
- **Java 21**：使用最新的Java LTS版本
- **Spring Boot 3.2.12**：应用框架
- **Spring Cloud 2023.0.5**：微服务框架
- **Spring Cloud Alibaba 2023.0.0.0-RC1**：阿里云微服务组件
- **PostgreSQL**：主数据库，支持JSONB字段存储动态数据结构
- **Redis**：分布式缓存，提供多级缓存策略
- **Nacos**：服务注册发现和配置中心
- **Aviator**：表达式引擎，支持规则解析和执行

### 架构设计
- **DDD分层架构**：采用领域驱动设计，清晰的分层边界
- **微服务架构**：支持独立部署和弹性扩展
- **事件驱动**：基于aixone-event-sdk实现模块间解耦
- **多租户隔离**：确保租户间数据安全和资源隔离

## 快速开始

### 环境要求
- JDK 21+
- Maven 3.8+
- PostgreSQL 12+
- Redis 6+
- Nacos 2.0+

### 本地开发

1. **克隆项目**
```bash
git clone <repository-url>
cd services/meta-center
```

2. **配置数据库**
```sql
-- 创建数据库
CREATE DATABASE meta_center;

-- 创建用户（可选）
CREATE USER meta_user WITH PASSWORD 'meta_password';
GRANT ALL PRIVILEGES ON DATABASE meta_center TO meta_user;
```

3. **配置Redis**
```bash
# 启动Redis服务
redis-server
```

4. **配置Nacos**
```bash
# 启动Nacos服务
sh startup.sh -m standalone
```

5. **修改配置**
编辑 `src/main/resources/application-dev.yml`，配置数据库、Redis、Nacos连接信息。

6. **启动应用**
```bash
mvn spring-boot:run
```

7. **访问服务**
- 应用地址：http://localhost:8080/meta-center
- API文档：http://localhost:8080/meta-center/swagger-ui.html
- 健康检查：http://localhost:8080/meta-center/actuator/health

## API文档

### 元数据管理API

#### 创建元数据对象
```http
POST /api/v1/meta-objects
Content-Type: application/json

{
  "name": "User",
  "objectType": "entity",
  "type": "business",
  "description": "用户实体",
  "attributes": [
    {
      "name": "username",
      "label": "用户名",
      "type": "string",
      "required": true
    }
  ]
}
```

#### 查询元数据对象
```http
GET /api/v1/meta-objects?page=0&size=10&name=User
```

#### 更新元数据对象
```http
PUT /api/v1/meta-objects/{id}
Content-Type: application/json

{
  "name": "User",
  "description": "用户实体（更新）"
}
```

#### 删除元数据对象
```http
DELETE /api/v1/meta-objects/{id}
```

### 规则引擎API

#### 创建规则
```http
POST /api/v1/rules
Content-Type: application/json

{
  "name": "username_unique",
  "type": "validation",
  "expression": "isUnique('User', 'username')",
  "description": "用户名唯一性校验"
}
```

#### 执行规则
```http
POST /api/v1/rules/execute
Content-Type: application/json

{
  "ruleType": "validation",
  "context": {
    "username": "test_user"
  }
}
```

### 数据服务API

#### 创建数据实例
```http
POST /api/v1/data-instances
Content-Type: application/json

{
  "metaObjectId": 1,
  "data": {
    "username": "test_user",
    "email": "test@example.com"
  }
}
```

#### 查询数据实例
```http
GET /api/v1/data-instances?metaObjectId=1&page=0&size=10
```

## 开发指南

### 项目结构
```
src/main/java/com/aixone/metacenter/
├── common/                    # 通用组件
│   ├── constant/             # 常量定义
│   ├── exception/            # 异常类
│   └── ...
├── config/                   # 配置类
├── metamanagement/           # 元数据管理模块
│   ├── domain/              # 领域层
│   ├── application/         # 应用层
│   ├── infrastructure/      # 基础设施层
│   └── interfaces/          # 接口层
├── ruleengine/              # 规则引擎模块
├── processengine/           # 流程引擎模块
├── permissionservice/       # 权限服务模块
├── dataservice/             # 数据服务模块
├── uiservice/               # UI服务模块
└── integrationorchestration/ # 集成编排模块
```

### 开发规范

#### 1. 代码规范
- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 使用MapStruct进行对象映射
- 统一异常处理和日志记录

#### 2. 数据库规范
- 使用PostgreSQL的JSONB字段存储动态数据
- 所有表包含租户ID字段，实现多租户隔离
- 使用乐观锁机制处理并发更新
- 建立合适的索引提升查询性能

#### 3. API规范
- 遵循RESTful API设计规范
- 使用Swagger注解生成API文档
- 统一响应格式和错误码
- 支持分页查询和条件过滤

### 测试指南

#### 单元测试
```bash
# 运行单元测试
mvn test

# 运行特定测试类
mvn test -Dtest=MetaObjectServiceTest
```

#### 集成测试
```bash
# 运行集成测试
mvn verify
```

#### 性能测试
```bash
# 使用JMeter进行性能测试
jmeter -n -t performance-test.jmx -l results.jtl
```

## 部署指南

### Docker部署

1. **构建镜像**
```bash
mvn clean package docker:build
```

2. **运行容器**
```bash
docker run -d \
  --name meta-center \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/meta_center \
  -e SPRING_REDIS_HOST=redis \
  meta-center:latest
```

### Kubernetes部署

1. **创建命名空间**
```bash
kubectl create namespace meta-center
```

2. **部署应用**
```bash
kubectl apply -f k8s/
```

3. **查看部署状态**
```bash
kubectl get pods -n meta-center
kubectl get services -n meta-center
```

### 生产环境配置

#### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:postgresql://prod-db:5432/meta_center
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10
```

#### Redis配置
```yaml
spring:
  data:
    redis:
      host: prod-redis
      port: 6379
      password: ${REDIS_PASSWORD}
      cluster:
        nodes: ${REDIS_CLUSTER_NODES}
```

#### 监控配置
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

## 监控告警

### 监控指标
- **应用指标**：JVM内存、CPU使用率、GC情况
- **业务指标**：API调用量、响应时间、错误率
- **数据库指标**：连接池状态、慢查询、锁等待
- **缓存指标**：命中率、内存使用、过期策略

### 告警规则
- CPU使用率 > 80%
- 内存使用率 > 85%
- API响应时间 > 2秒
- 错误率 > 5%
- 数据库连接池使用率 > 90%

### 日志管理
- 使用ELK Stack进行日志收集和分析
- 配置日志轮转和归档策略
- 设置关键操作的审计日志

## 常见问题

### Q1: 如何处理元数据变更？
A: 元数据变更采用版本控制机制，支持多版本并行和灰度发布。变更前需要进行影响分析，确保不影响现有业务。

### Q2: 如何保证数据一致性？
A: 使用SAGA模式处理跨模块操作，确保最终一致性。对于单模块操作，使用数据库事务保证强一致性。

### Q3: 如何提升查询性能？
A: 使用多级缓存策略（本地缓存+Redis缓存），建立合适的数据库索引，优化查询语句。

### Q4: 如何实现多租户隔离？
A: 基于租户ID实现数据级隔离，使用Row Level Security (RLS)确保数据库层面的隔离。

### Q5: 如何扩展新的元数据类型？
A: 通过扩展点机制和插件架构，支持自定义元数据类型和处理器。

## 贡献指南

### 提交规范
- 使用语义化的提交信息
- 每个提交只包含一个功能或修复
- 提交前运行完整的测试套件

### 代码审查
- 所有代码变更需要经过代码审查
- 审查重点：代码质量、安全性、性能、可维护性
- 使用Pull Request进行代码合并

### 文档更新
- 代码变更需要同步更新相关文档
- 新增API需要更新API文档
- 重要变更需要更新变更日志

## 联系方式

- **项目维护者**：aixone团队
- **邮箱**：support@aixone.com
- **文档地址**：https://docs.aixone.com/meta-center
- **问题反馈**：https://github.com/aixone/meta-center/issues

## 许可证

本项目采用 [MIT License](LICENSE) 许可证。 