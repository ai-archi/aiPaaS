# Service Mesh

## 1. 概述
Service Mesh 是一种基础设施层设计，用于在微服务之间提供**服务间通信、流量管理、安全和可观测性**。它通过 **Sidecar Proxy** 或透明代理方式拦截服务流量，实现服务治理，而无需修改业务代码。

### 核心价值
- **流量管理**：路由、负载均衡、限流、熔断  
- **安全通信**：服务间 mTLS、访问控制  
- **弹性与容错**：熔断、重试、故障隔离  
- **统一治理**：策略统一下发，减少业务侵入  

---

## 2. Service Mesh 模式与架构

### 2.1 Sidecar 模式
- 每个服务实例旁边部署一个代理容器（Sidecar）  
- 代理负责流量拦截、策略执行和监控  
- **优点**：应用无需改造  
- **缺点**：增加资源开销  

### 2.2 Control Plane / Data Plane
- **Control Plane**：策略管理和下发（如 Istio Pilot）  
- **Data Plane**：实际处理流量的代理（如 Envoy、Linkerd Proxy）  
- Control Plane 下发策略给 Data Plane，实现统一治理

### 2.3 部署模式
- **单集群模式**：Sidecar 与 Control Plane 部署在同一集群  
- **多集群模式**：跨集群流量管理，支持灾备和全局负载均衡  
- **多租户模式**：隔离不同业务或团队流量  

---

## 3. 功能与应用场景
- **流量管理**：路由、灰度发布、蓝绿发布  
- **安全**：服务间加密通信、访问控制、认证与授权  
- **弹性**：熔断、限流、重试、故障注入  
- **可观测性**：指标采集、分布式追踪、日志收集  
- **适用场景**：
  - 微服务数量较多（>50 个服务）  
  - 需要统一安全策略和流量治理  
  - 跨集群、跨数据中心部署  

---

## 4. 常用工具与框架

| 工具 / 框架 | 功能 | 典型用法 | 状态与说明 |
|------------|------|-----------|------------|
| **Istio** | 服务网格，提供流量管理、安全、可观测性 | Kubernetes 微服务 | 推荐，成熟生态，支持多集群和全局策略 |
| **Linkerd** | 轻量级服务网格，注重性能和简化部署 | Kubernetes 微服务 | 推荐，中小型集群轻量化 |
| **Envoy** | 数据平面代理，流量拦截、路由与策略执行 | Sidecar Proxy | 核心组件，Service Mesh Data Plane |
| **Consul Connect** | 服务网格与服务发现结合 | 微服务通信加密与治理 | 可选，适合 HashiCorp 生态 |

---

## 5. 架构示意图（Mermaid）

```mermaid
graph TD
    Client[客户端请求] -->|HTTP/gRPC| ServiceA[服务A]
    ServiceA -->|Sidecar| EnvoyA[Envoy Proxy]
    EnvoyA --> ServiceB[服务B]
    ServiceB -->|Sidecar| EnvoyB[Envoy Proxy]
    ServiceB --> ServiceC[服务C]
    ControlPlane[Control Plane] -->|策略下发| EnvoyA
    ControlPlane --> EnvoyB
````

---

## 6. 行业最佳实践

1. **适度部署**：中小型系统可仅部署核心流量管理与监控
2. **资源隔离**：合理配置 Sidecar 容器的 CPU/内存，避免影响业务服务
3. **统一策略管理**：通过 Control Plane 管理路由、安全策略
4. **安全通信**：启用 mTLS，实现服务间加密和访问控制
5. **监控与告警**：结合 Prometheus / Grafana / Jaeger 监控流量、延迟、错误率
6. **灰度与弹性策略**：结合负载均衡、熔断器和重试策略提高系统稳定性

---

## 7. 参考资料

* [Istio 官方文档](https://istio.io/latest/docs/)
* [Linkerd 官方文档](https://linkerd.io/)
* [Envoy Proxy 文档](https://www.envoyproxy.io/docs)
* [Service Mesh 性能与实践指南](https://www.cncf.io/blog/2019/10/15/service-mesh-principles-and-practices/)

