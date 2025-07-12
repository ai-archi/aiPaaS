# aixone-common-constant

本模块为统一常量与枚举能力包，提供所有微服务/业务模块通用的：
- 公共常量（如系统级常量、通用字符串、数字等）
- 枚举类型（如通用状态、类型、标识等）

## 主要内容
- `com.aixone.common.constant.CommonConstants` 公共常量
- `com.aixone.common.constant.SystemConstants` 系统常量
- `com.aixone.common.constant.enums.*` 通用枚举

## 适用场景
- 作为所有微服务/业务模块的基础依赖，统一常量和枚举定义，避免重复造轮子

## 用法
在业务模块的 pom.xml 中添加依赖：
```xml
<dependency>
    <groupId>com.aixone</groupId>
    <artifactId>aixone-common-constant</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 目录结构建议
- constant/    公共常量
- constant/enums/    通用枚举

## 贡献说明
如需扩展通用常量或枚举，请遵循分层和命名规范，避免引入业务逻辑。 