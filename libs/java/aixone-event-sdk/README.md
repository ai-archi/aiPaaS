# aixone-event-sdk

本模块为事件中心统一SDK，提供所有微服务/业务模块通用的：
- 事件发布、订阅、查询等接口协议
- 事件、订阅、审计日志等DTO
- 事件类型、常量、错误码等
- 便于各服务统一集成事件中心能力，实现事件驱动、审计、异步解耦

## 主要内容
- `com.aixone.event.dto.EventDTO` 事件数据结构
- `com.aixone.event.dto.SubscriptionDTO` 订阅数据结构
- `com.aixone.event.dto.AuditLogDTO` 审计日志数据结构
- `com.aixone.event.api.EventApi` 事件接口协议
- `com.aixone.event.api.SubscriptionApi` 订阅接口协议
- `com.aixone.event.api.AuditApi` 审计接口协议
- `com.aixone.event.client.EventCenterClient` 事件中心HTTP客户端
- `com.aixone.event.client.KafkaEventClient` Kafka事件发布/订阅客户端
- `com.aixone.event.listener.EventListener` 事件监听器接口
- `com.aixone.event.constant.EventType` 事件类型枚举

## 适用场景
- 作为所有微服务/业务模块的基础依赖，统一事件流转、审计、调度等能力

## Kafka事件发布/订阅示例

```java
// 配置Kafka参数
Properties producerProps = new Properties();
// ...设置bootstrap.servers等
KafkaEventClient client = new KafkaEventClient(producerProps, null);

// 发布事件
EventDTO event = new EventDTO();
// ...设置event属性
client.publishEvent("event-topic", event);

// 订阅事件
client.subscribe("event-topic", new EventListener() {
    @Override
    public void onEvent(EventDTO event) {
        // 处理事件
    }
});
```

## 用法
在业务模块的 pom.xml 中添加依赖：
```xml
<dependency>
    <groupId>com.aixone</groupId>
    <artifactId>aixone-event-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 目录结构建议
- dto/      事件、订阅、审计DTO
- api/      事件、订阅、审计接口协议
- client/   事件中心HTTP客户端
- constant/ 事件类型、常量

## 贡献说明
如需扩展通用事件能力，请遵循分层和命名规范，避免引入业务逻辑。 