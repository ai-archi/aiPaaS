# aixone-common-tools

本模块为通用工具类能力包，提供所有微服务/业务模块通用的：
- 字符串工具类（StringUtils）
- 日期工具类（DateUtils）
- 集合工具类（CollectionUtils）
- Bean 拷贝工具类（BeanUtils）
- 其他常用工具

## 主要内容
- `com.aixone.common.tools.StringUtils` 字符串处理
- `com.aixone.common.tools.DateUtils` 日期处理
- `com.aixone.common.tools.CollectionUtils` 集合处理
- `com.aixone.common.tools.BeanUtils` Bean拷贝

## 适用场景
- 作为所有微服务/业务模块的基础依赖，统一常用工具能力

## 用法
在业务模块的 pom.xml 中添加依赖：
```xml
<dependency>
    <groupId>com.aixone</groupId>
    <artifactId>aixone-common-tools</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 目录结构建议
- tools/    通用工具类

## 贡献说明
如需扩展通用工具能力，请遵循分层和命名规范，避免引入业务逻辑。 