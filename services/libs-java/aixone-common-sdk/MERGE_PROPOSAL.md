# aixone-session-sdk 合并到 aixone-common-sdk 的提案

## 合并方案

### 方案一：完全合并（推荐）

将 `aixone-session-sdk` 的所有功能合并到 `aixone-common-sdk` 中，形成统一的基础设施 SDK。

#### 优势
1. **简化依赖管理**：减少 SDK 数量，降低维护成本
2. **消除循环依赖**：解决当前 `common-sdk` 依赖 `session-sdk` 的问题
3. **功能完整性**：提供完整的基础设施能力
4. **使用便利性**：业务应用只需引入一个 SDK

#### 包结构设计
```
com.aixone.common
├── ddd/                    // DDD 基础组件
├── api/                    // API 相关
├── exception/              // 异常处理
├── constant/               // 常量定义
├── util/                   // 工具类
├── tools/                  // 扩展工具类
└── session/                // 会话上下文管理（原 session-sdk）
    ├── SessionContext.java
    ├── TokenParser.java
    ├── SessionInterceptor.java
    ├── SessionException.java
    ├── AbacAttributes.java
    └── config/
        └── SessionConfig.java
```

### 方案二：模块化合并

保持 `aixone-common-sdk` 作为主 SDK，将会话功能作为可选模块。

#### 包结构设计
```
com.aixone.common
├── core/                   // 核心功能（不依赖会话）
│   ├── ddd/
│   ├── api/
│   ├── exception/
│   ├── constant/
│   ├── util/
│   └── tools/
└── session/                // 会话模块（可选）
    ├── SessionContext.java
    ├── TokenParser.java
    ├── SessionInterceptor.java
    ├── SessionException.java
    ├── AbacAttributes.java
    └── config/
        └── SessionConfig.java
```

## 实施步骤

### 1. 合并代码
1. 将 `aixone-session-sdk` 的所有代码移动到 `aixone-common-sdk/src/main/java/com/aixone/common/session/`
2. 更新包名从 `com.aixone.session` 到 `com.aixone.common.session`
3. 更新 `Entity` 基类中的导入

### 2. 更新依赖
1. 移除 `aixone-common-sdk` 对 `aixone-session-sdk` 的依赖
2. 添加 JWT 相关依赖到 `aixone-common-sdk`
3. 更新所有使用 `aixone-session-sdk` 的项目依赖

### 3. 更新文档
1. 更新 `aixone-common-sdk` 的 README
2. 提供迁移指南
3. 更新架构文档

## 迁移影响

### 对现有代码的影响
```java
// 原来的导入
import com.aixone.session.SessionContext;
import com.aixone.session.TokenParser;

// 合并后的导入
import com.aixone.common.session.SessionContext;
import com.aixone.common.session.TokenParser;
```

### 对依赖的影响
```xml
<!-- 原来的依赖 -->
<dependency>
    <groupId>com.aixone</groupId>
    <artifactId>aixone-common-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>com.aixone</groupId>
    <artifactId>aixone-session-sdk</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- 合并后的依赖 -->
<dependency>
    <groupId>com.aixone</groupId>
    <artifactId>aixone-common-sdk</artifactId>
    <version>2.0.0</version>
</dependency>
```

## 建议

**推荐采用方案一（完全合并）**，理由如下：

1. **架构简洁**：统一的基础设施 SDK 更符合微服务架构的最佳实践
2. **维护便利**：减少 SDK 数量，降低维护成本
3. **使用简单**：业务应用只需引入一个 SDK
4. **功能完整**：提供完整的基础设施能力

## 风险评估

### 低风险
- 代码迁移相对简单
- 包名变更影响范围可控
- 依赖关系清晰

### 缓解措施
- 提供详细的迁移指南
- 保持向后兼容的过渡期
- 提供自动化迁移工具
