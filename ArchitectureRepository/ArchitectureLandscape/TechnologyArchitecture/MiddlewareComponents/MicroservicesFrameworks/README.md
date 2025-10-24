| 功能 / 阶段    | Netflix OSS (2010–2015) | Spring Cloud Netflix (2014–2020)     | Spring Cloud Alibaba (2016–至今)               | 现代 Cloud Native (2020–至今)   |
| ---------------- | -------------------------- | --------------------------------------- | ------------------------------------------------- | ---------------------------------- |
| 服务注册与发现 | Eureka                   | spring-cloud-starter-netflix-eureka   | Nacos (替代 Eureka + Archaius)                  | Nacos / Consul                   |
| 配置管理       | Archaius                 | spring-cloud-starter-netflix-archaius | Nacos (替代 Archaius / Spring Config)           | Nacos / Consul                   |
| 客户端负载均衡 | Ribbon                   | spring-cloud-starter-netflix-ribbon   | Dubbo (替代 Ribbon + Feign)                     | Service Mesh (Istio / Linkerd)   |
| 声明式服务调用 | Feign                    | spring-cloud-starter-openfeign        | Dubbo (替代 Feign)                              | Service Mesh / gRPC              |
| 熔断与容错     | Hystrix                  | spring-cloud-starter-netflix-hystrix  | Sentinel (替代 Hystrix)                         | Resilience4j                     |
| API 网关       | Zuul 1                   | spring-cloud-starter-netflix-zuul     | Spring Cloud Gateway / Nacos 网关 (替代 Zuul 1) | Spring Cloud Gateway             |
| 熔断监控       | Turbine                  | spring-cloud-starter-netflix-turbine  | 集成 Sentinel / Prometheus                      | Prometheus / Zipkin / Micrometer |
| 分布式事务     | 无                       | 无                                    | Seata                                           | Seata / Saga / Event Sourcing    |
| 消息中间件     | 无                       | 无                                    | RocketMQ / Kafka                                | RocketMQ / Kafka / Pulsar        |
| 容器调度与部署 | Titus                    | Spinnaker                             | Kubernetes + Helm                               | Kubernetes + Helm                |

说明：
表格主要展示了各阶段微服务组件的功能和演进关系，但无法体现背后的实践背景与理念差异。

Netflix 是微服务架构的实践者与创造者，其 OSS 组件是基于自身大规模分布式系统的工程经验而开源的，但对中国企业支持有限（社区更新慢、文档少）。

Spring Cloud 将 Netflix 的微服务理念进行标准化、框架化和工具化，使开发者能够在 Spring Boot 生态中快速集成微服务能力，降低使用门槛。

Spring Cloud Alibaba 基于 Spring Cloud，并结合阿里巴巴在电商和支付系统的微服务实践，提供本地化、企业级特性（如 Nacos、Sentinel、Seata、Dubbo 等），更适合中国企业的高并发、分布式事务等场景。
