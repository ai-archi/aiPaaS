# aixone-common-exception

本模块为统一异常体系能力包，提供所有微服务/业务模块通用的：
- 统一异常基类（如 BaseException）
- 业务异常（如 BizException）
- 全局异常处理器（如 GlobalExceptionHandler，适用于 Spring Boot）
- 常见异常类型与错误码扩展

## 主要内容
- `com.aixone.common.exception.BaseException` 统一异常基类
- `com.aixone.common.exception.BizException` 业务异常
- `com.aixone.common.exception.GlobalExceptionHandler` 全局异常处理器

## 适用场景
- 作为所有微服务/业务模块的基础依赖，统一异常体系和处理方式

## 用法
在业务模块的 pom.xml 中添加依赖：
```xml
<dependency>
    <groupId>com.aixone</groupId>
    <artifactId>aixone-common-exception</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 目录结构建议
- exception/    统一异常体系
- handler/      全局异常处理器

## 贡献说明
如需扩展通用异常能力，请遵循分层和命名规范，避免引入业务逻辑。 