# Architecture Repository (架构景库)

## 1. 概述

本架构景库基于 **TOGAF 架构方法**建立，旨在提供企业级架构资产的集中管理和复用，包括：

- 企业架构全景（Architecture Landscape）  
- 标准信息库（Standards Information Base）  
- 参考库（Reference Library）  
- 治理日志（Governance Log）  

同时结合微服务演进实践，提供 **Netflix OSS → Spring Cloud → Spring Cloud Alibaba** 的技术参考和组件演进示例。

---

## 2. 目录结构

```
ArchitectureRepository/
├── ArchitectureLandscape/
│   ├── BusinessArchitecture/
│   │   ├── BusinessCapabilities/
│   │   ├── ValueStreams/
│   │   ├── Processes/
│   │   └── OrganizationStructure/
│   │
│   ├── ApplicationArchitecture/
│   │   ├── ApplicationPortfolio/         # 聚合服务模块
│   │   │   ├── AIServiceModules/         # AI应用能力
│   │   │   │   ├── NLPService/
│   │   │   │   ├── CVService/
│   │   │   │   ├── SpeechAndAudioService/
│   │   │   │   ├── MultiModalService/
│   │   │   │   └── RAGService/
│   │   │   │
│   │   │   ├── MicroservicesModules/     # 微服务能力
│   │   │   │   ├── ServiceDiscovery/    # e.g., Eureka
│   │   │   │   ├── LoadBalancing/       # e.g., Ribbon
│   │   │   │   ├── CircuitBreaker/      # e.g., Resilience4j
│   │   │   │   ├── API Gateway/         # e.g., Spring Cloud Gateway
│   │   │   │   └── Messaging/           # e.g., Kafka, RabbitMQ
│   │   │   │
│   │   │   └── JVMModules/               # JVM相关能力
│   │   │       ├── Overview.md           # JVM发展、设计理念
│   │   │       ├── JDK/
│   │   │       ├── JVMRuntime/
│   │   │       ├── ClassLoading/
│   │   │       └── Threading/
│   │   │
│   │   ├── ApplicationServices/          # 各服务接口、功能说明
│   │   └── ApplicationPatterns/          # 总览与模式，如 AIOverview.md, 微服务模式
│   │
│   ├── DataArchitecture/
│   │   ├── DataEntities/
│   │   ├── DataFlows/
│   │   └── DataModels/
│   │
│   └── TechnologyArchitecture/
│       ├── TechnologyPortfolio/          # 技术组件和平台能力
│       │   ├── AIFrameworks/
│       │   │   ├── TensorFlow/
│       │   │   ├── PyTorch/
│       │   │   ├── JAX/
│       │   │   └── MXNet/
│       │   │
│       │   ├── ModelServing/
│       │   │   ├── ONNX/
│       │   │   ├── Triton/
│       │   │   ├── MLflow/
│       │   │   └── BentoML/
│       │   │
│       │   ├── JVM/                       # JVM技术能力
│       │   │   ├── JDK/
│       │   │   ├── JVMRuntime/
│       │   │   ├── ClassLoading/
│       │   │   └── Threading/
│       │   │
│       │   └── MicroservicesTech/        # 微服务技术组件
│       │       ├── NetflixOSS/
│       │       ├── SpringCloud/
│       │       └── SpringCloudAlibaba/
│       │
│       ├── MiddlewareComponents/          # 中间件和辅助工具
│       │   ├── AI/                        # 向量数据库、检索库等
│       │   │   └── VectorDB/              # Milvus, FAISS等
│       │   ├── Messaging/                 # Kafka, RabbitMQ
│       │   └── JVMTools/                  # jconsole, jvisualvm, Flight Recorder
│       │
│       └── PlatformStandards/             # 性能、调优规范
│           ├── JVMPerformance/
│           └── MicroservicesStandards/
│
├── StandardsInformationBase/
│   ├── EnterpriseStandards/
│   ├── TechnologyStandards/
│   └── AIStandards/
│       ├── BiasAndFairness.md
│       ├── Explainability.md
│       ├── DataPrivacy.md
│       └── ResponsibleAI.md
│
├── ReferenceLibrary/
│   ├── ReferenceModels/
│   ├── BestPractices/
│   │   ├── AI/
│   │   ├── JVM/
│   │   └── Microservices/
│   └── SolutionBuildingBlocks/
│       ├── ResearchPapers/
│       ├── Datasets/
│       └── OpenSourceProjects/
│
└── GovernanceLog/
    ├── ArchitectureDecisions/
    ├── ComplianceReports/
    └── LessonsLearned/

```

---

## 3. 目录说明

### 3.1 ArchitectureLandscape（架构景观）

#### 3.1.1 BusinessArchitecture（业务架构）
- **BusinessCapabilities**：企业能力模型，定义核心业务能力
- **ValueStreams**：价值流分析，识别端到端业务流程
- **Processes**：业务流程定义，标准化业务操作流程
- **OrganizationStructure**：组织结构设计，支持业务能力实现

#### 3.1.2 ApplicationArchitecture（应用架构）
- **ApplicationPortfolio**：应用组合管理
  - **AIServiceModules**：AI应用能力模块
    - **NLPService**：自然语言处理服务
    - **CVService**：计算机视觉服务
    - **SpeechAndAudioService**：语音和音频服务
    - **MultiModalService**：多模态服务
    - **RAGService**：检索增强生成服务
  - **MicroservicesModules**：微服务能力模块
    - **ServiceDiscovery**：服务发现（如Eureka）
    - **LoadBalancing**：负载均衡（如Ribbon）
    - **CircuitBreaker**：熔断器（如Resilience4j）
    - **API Gateway**：API网关（如Spring Cloud Gateway）
    - **Messaging**：消息中间件（如Kafka、RabbitMQ）
  - **JVMModules**：JVM相关能力
    - **Overview.md**：JVM发展历程和设计理念
    - **JDK**：Java开发工具包
    - **JVMRuntime**：JVM运行时环境
    - **ClassLoading**：类加载机制
    - **Threading**：多线程编程
- **ApplicationServices**：各服务接口和功能说明
- **ApplicationPatterns**：应用模式总览（如AIOverview.md、微服务模式）

#### 3.1.3 DataArchitecture（数据架构）
- **DataEntities**：数据实体定义
- **DataFlows**：数据流设计
- **DataModels**：数据模型规范

#### 3.1.4 TechnologyArchitecture（技术架构）
- **TechnologyPortfolio**：技术组件和平台能力
  - **AIFrameworks**：AI框架
    - **TensorFlow**：Google开源机器学习框架
    - **PyTorch**：Facebook开源深度学习框架
    - **JAX**：Google高性能机器学习库
    - **MXNet**：Apache深度学习框架
  - **ModelServing**：模型服务化
    - **ONNX**：开放神经网络交换格式
    - **Triton**：NVIDIA推理服务器
    - **MLflow**：机器学习生命周期管理
    - **BentoML**：模型服务化框架
  - **JVM**：JVM技术能力
    - **JDK**：Java开发工具包
    - **JVMRuntime**：JVM运行时环境
    - **ClassLoading**：类加载机制
    - **Threading**：多线程编程
  - **MicroservicesTech**：微服务技术组件
    - **NetflixOSS**：Netflix开源组件
    - **SpringCloud**：Spring Cloud微服务框架
    - **SpringCloudAlibaba**：Spring Cloud Alibaba组件
- **MiddlewareComponents**：中间件和辅助工具
  - **AI**：AI相关中间件
    - **VectorDB**：向量数据库（Milvus、FAISS等）
  - **Messaging**：消息中间件（Kafka、RabbitMQ）
  - **JVMTools**：JVM工具（jconsole、jvisualvm、Flight Recorder）
- **PlatformStandards**：平台标准和规范
  - **JVMPerformance**：JVM性能调优规范
  - **MicroservicesStandards**：微服务开发规范

### 3.2 StandardsInformationBase（标准信息库）
- **EnterpriseStandards**：企业级标准规范
- **TechnologyStandards**：技术标准规范
- **AIStandards**：AI相关标准
  - **BiasAndFairness.md**：AI偏见和公平性标准
  - **Explainability.md**：AI可解释性标准
  - **DataPrivacy.md**：数据隐私保护标准
  - **ResponsibleAI.md**：负责任AI标准

### 3.3 ReferenceLibrary（参考库）
- **ReferenceModels**：架构参考模型
- **BestPractices**：最佳实践与案例
  - **AI**：AI相关最佳实践
  - **JVM**：JVM相关最佳实践
  - **Microservices**：微服务最佳实践
- **SolutionBuildingBlocks**：可复用解决方案组件
  - **ResearchPapers**：研究论文
  - **Datasets**：数据集
  - **OpenSourceProjects**：开源项目

### 3.4 GovernanceLog（治理日志）
- **ArchitectureDecisions**：架构决策记录
- **ComplianceReports**：合规性检查报告
- **LessonsLearned**：经验总结和改进建议

---

## 4. 微服务演进参考

- **Netflix OSS**：微服务架构实践者，提供原始组件（Eureka、Ribbon、Hystrix、Zuul 等）  
- **Spring Cloud**：将 Netflix OSS 标准化、框架化、工具化，降低微服务使用门槛  
- **Spring Cloud Alibaba**：结合阿里巴巴电商和支付系统微服务实践，本地化组件（Nacos、Sentinel、Seata、Dubbo 等），适合中国企业场景  

> 替代关系示例：
> - Eureka + Archaius → Nacos  
> - Hystrix → Sentinel / Resilience4j  
> - Ribbon + Feign → Dubbo / Service Mesh  
> - Zuul → Spring Cloud Gateway  

---

## 5. 使用说明

1. 使用 `ArchitectureRepository/ArchitectureLandscape` 组织业务、应用、数据和技术资产  
2. 技术选型参考 `MiddlewareComponents`，可在 `ReferenceLibrary/SolutionBuildingBlocks` 中复用  
3. 所有架构决策记录在 `GovernanceLog/ArchitectureDecisions`  
4. 遵循标准库中的企业与技术标准，保证统一性和可治理性  

---

## 6. 维护建议

- 定期更新 `ReferenceLibrary` 中的最佳实践和 SBB  
- 对中间件演进（Netflix → Spring Cloud → Alibaba）做版本记录  
- 架构决策和经验教训必须及时写入 `GovernanceLog`  
- 与业务架构和应用架构保持同步，保证技术与业务一致性  

