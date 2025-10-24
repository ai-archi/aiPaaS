# aixone-event-sdk

本模块为事件中心统一SDK，提供所有微服务/业务模块通用的：
- 事件发布、订阅、查询等接口协议
- 事件、订阅、审计日志等DTO
- 事件类型、常量等
- 便于各服务统一集成事件中心能力，实现事件驱动、审计、异步解耦

## 设计原则

**协议层设计**：SDK只关注事件相关的协议定义，不关心事件中心的具体实现（Kafka、RocketMQ等）
**专注事件**：只提供事件发布、订阅、查询等核心功能，不包含调度等非事件相关功能
**最小依赖**：只依赖必要的通用SDK，保持轻量级

## 主要内容

### DTO 数据传输对象
- `com.aixone.event.dto.EventDTO` 事件数据结构
- `com.aixone.event.dto.TopicDTO` Topic数据结构
- `com.aixone.event.dto.SubscriptionDTO` 订阅数据结构

### API 接口协议
- `com.aixone.event.api.EventApi` 事件接口协议
- `com.aixone.event.api.TopicApi` Topic接口协议
- `com.aixone.event.api.SubscriptionApi` 订阅接口协议

### 客户端
- `com.aixone.event.client.EventCenterClient` 事件中心HTTP客户端（协议层）

### 常量和工具
- `com.aixone.event.constant.EventType` 事件类型常量
- `com.aixone.event.listener.EventListener` 事件监听器接口

## 使用示例

### 1. 发布事件

```java
// 创建事件DTO
EventDTO event = new EventDTO(
    EventType.USER_LOGIN,
    "user-service",
    "{\"userId\":\"12345\",\"loginTime\":\"2024-01-01T10:00:00Z\"}",
    "tenant-001"
);

// 通过事件中心客户端发布事件
EventCenterClient client = new EventCenterClient("http://event-center:8080", "tenant-001");
ApiResponse<EventDTO> response = client.publishEvent(event);

if (response.isSuccess()) {
    System.out.println("事件发布成功: " + response.getData().getEventId());
}
```

### 2. 发布事件到指定Topic

```java
// 发布事件到Kafka Topic
ApiResponse<EventDTO> response = client.publishEventToTopic("user-events", event);
```

### 3. 查询事件

```java
// 查询所有事件
ApiResponse<List<EventDTO>> events = client.getAllEvents();

// 根据类型查询事件
ApiResponse<List<EventDTO>> loginEvents = client.getEventsByType(EventType.USER_LOGIN);

// 根据时间范围查询事件
Instant startTime = Instant.now().minus(Duration.ofHours(1));
Instant endTime = Instant.now();
ApiResponse<List<EventDTO>> recentEvents = client.getEventsByTimeRange(startTime, endTime);
```

### 4. 事件监听

```java
@Component
public class UserEventHandler {
    
    /**
     * 监听用户登录事件
     */
    @EventListener(
        topics = "user-events", 
        eventTypes = "user.login",
        groupId = "user-service"
    )
    public void handleUserLogin(EventDTO event) {
        System.out.println("用户登录: " + event.getData());
    }
    
    /**
     * 监听所有订单事件
     */
    @EventListener(topics = "order-events")
    public void handleOrderEvents(EventDTO event) {
        System.out.println("订单事件: " + event.getEventType());
    }
}
```

### 5. Topic管理

```java
// 注册Topic
TopicDTO topic = new TopicDTO("user-events", "user-service", "用户相关事件", "tenant-001");
ApiResponse<TopicDTO> response = client.registerTopic(topic);

// 查询所有Topic
ApiResponse<List<TopicDTO>> topics = client.getAllTopics();

// 激活Topic
ApiResponse<Boolean> activateResult = client.activateTopic("user-events");
```

## 依赖配置

在业务模块的 pom.xml 中添加依赖：

```xml
<dependency>
    <groupId>com.aixone</groupId>
    <artifactId>aixone-event-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 配置说明

在 application.yml 中添加事件配置：

```yaml
aixone:
  event:
    enabled: true
    event-center:
      base-url: http://event-center:8080
      tenant-id: ${spring.application.name}
    kafka:
      bootstrap-servers: localhost:9092
      group-id: ${spring.application.name}-event-group
      auto-offset-reset: earliest
      concurrency: 1
    listener:
      auto-start: true
      thread-pool-size: 10
      enable-metrics: true
```

## 实现说明

### 协议层设计
- SDK只定义接口协议和DTO，不包含具体实现
- 具体实现由使用者提供（如RestTemplate、Feign等）
- 基于事件中心的REST接口设计，确保协议一致性

### 事件监听设计
- 提供@EventListener注解，简化事件监听开发
- 支持Topic和事件类型过滤，提高监听精度
- 自动配置Kafka消费者，无需手动配置
- 支持监听器优先级和动态启用/禁用
- 微服务无需关心具体消息中间件实现

### 事件类型常量
- 提供常用的事件类型常量定义
- 支持事件类型验证和分类
- 便于统一管理事件类型

### 多租户支持
- 所有接口都支持租户隔离
- 通过tenantId参数区分不同租户的数据

## 目录结构

```
src/main/java/com/aixone/event/
├── dto/           # 数据传输对象
│   ├── EventDTO.java
│   ├── TopicDTO.java
│   └── SubscriptionDTO.java
├── api/           # 接口协议
│   ├── EventApi.java
│   ├── TopicApi.java
│   └── SubscriptionApi.java
├── client/        # 客户端
│   └── EventCenterClient.java
├── config/        # 配置类
│   ├── EventAutoConfiguration.java
│   └── EventProperties.java
├── annotation/    # 注解
│   └── EventListener.java
├── listener/      # 监听器
│   ├── EventListener.java
│   └── EventListenerManager.java
├── constant/      # 常量
│   └── EventType.java
└── example/       # 使用示例
    └── EventListenerExample.java
```

## 贡献说明

如需扩展通用事件能力，请遵循以下原则：
1. 保持协议层设计，不引入具体实现
2. 只关注事件相关功能，避免引入调度等非事件功能
3. 遵循现有的命名规范和代码风格
4. 确保多租户支持 