package com.aixone.metacenter.common.constant;

/**
 * 元数据服务常量定义
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public final class MetaConstants {

    private MetaConstants() {
        // 私有构造函数，防止实例化
    }

    /**
     * 元数据类型常量
     */
    public static final class MetaType {
        /** 业务元数据 */
        public static final String BUSINESS = "business";
        /** 技术元数据 */
        public static final String TECHNICAL = "technical";
        /** 管理元数据 */
        public static final String ADMINISTRATIVE = "administrative";
        /** 参考元数据 */
        public static final String REFERENCE = "reference";
    }

    /**
     * 元数据对象类型常量
     */
    public static final class ObjectType {
        /** 实体 */
        public static final String ENTITY = "entity";
        /** 字段 */
        public static final String FIELD = "field";
        /** 关系 */
        public static final String RELATION = "relation";
        /** 规则 */
        public static final String RULE = "rule";
        /** 字典 */
        public static final String DICTIONARY = "dictionary";
        /** 表单 */
        public static final String FORM = "form";
        /** 流程 */
        public static final String PROCESS = "process";
    }

    /**
     * 生命周期状态常量
     */
    public static final class LifecycleStatus {
        /** 草稿 */
        public static final String DRAFT = "draft";
        /** 审核中 */
        public static final String REVIEW = "review";
        /** 已发布 */
        public static final String PUBLISHED = "published";
        /** 已废弃 */
        public static final String DEPRECATED = "deprecated";
        /** 已归档 */
        public static final String ARCHIVED = "archived";
    }

    /**
     * 运行状态常量
     */
    public static final class RuntimeStatus {
        /** 启用 */
        public static final String ENABLED = "enabled";
        /** 禁用 */
        public static final String DISABLED = "disabled";
        /** 锁定 */
        public static final String LOCKED = "locked";
    }

    /**
     * 关系类型常量
     */
    public static final class RelationType {
        /** 一对多 */
        public static final String ONE_TO_MANY = "one_to_many";
        /** 多对多 */
        public static final String MANY_TO_MANY = "many_to_many";
        /** 一对一 */
        public static final String ONE_TO_ONE = "one_to_one";
        /** 引用 */
        public static final String REFERENCE = "reference";
        /** 聚合 */
        public static final String AGGREGATION = "aggregation";
    }

    /**
     * 规则类型常量
     */
    public static final class RuleType {
        /** 校验规则 */
        public static final String VALIDATION = "validation";
        /** 业务规则 */
        public static final String BUSINESS = "business";
        /** 自动化规则 */
        public static final String AUTOMATION = "automation";
        /** 流程规则 */
        public static final String PROCESS = "process";
        /** 权限规则 */
        public static final String PERMISSION = "permission";
    }

    /**
     * 缓存相关常量
     */
    public static final class Cache {
        /** 元数据缓存前缀 */
        public static final String META_PREFIX = "meta:";
        /** 规则缓存前缀 */
        public static final String RULE_PREFIX = "rule:";
        /** 流程缓存前缀 */
        public static final String PROCESS_PREFIX = "process:";
        /** UI缓存前缀 */
        public static final String UI_PREFIX = "ui:";
        /** 默认过期时间（秒） */
        public static final long DEFAULT_EXPIRE = 3600L;
    }

    /**
     * 事件类型常量
     */
    public static final class EventType {
        /** 元数据创建 */
        public static final String META_CREATED = "meta.created";
        /** 元数据更新 */
        public static final String META_UPDATED = "meta.updated";
        /** 元数据删除 */
        public static final String META_DELETED = "meta.deleted";
        /** 规则执行 */
        public static final String RULE_EXECUTED = "rule.executed";
        /** 流程启动 */
        public static final String PROCESS_STARTED = "process.started";
        /** 流程完成 */
        public static final String PROCESS_COMPLETED = "process.completed";
    }

    /**
     * 权限操作常量
     */
    public static final class PermissionAction {
        /** 读取 */
        public static final String READ = "read";
        /** 写入 */
        public static final String WRITE = "write";
        /** 删除 */
        public static final String DELETE = "delete";
        /** 执行 */
        public static final String EXECUTE = "execute";
        /** 管理 */
        public static final String MANAGE = "manage";
    }

    /**
     * 数据脱敏类型常量
     */
    public static final class MaskingType {
        /** 掩码 */
        public static final String MASK = "mask";
        /** 加密 */
        public static final String ENCRYPT = "encrypt";
        /** 哈希 */
        public static final String HASH = "hash";
        /** 替换 */
        public static final String REPLACE = "replace";
    }

    /**
     * API相关常量
     */
    public static final class Api {
        /** API前缀 */
        public static final String API_PREFIX = "/api/meta-center";
        /** 版本号 */
        public static final String VERSION = "v1";
        /** 完整API前缀 */
        public static final String FULL_API_PREFIX = API_PREFIX + "/" + VERSION;
    }

    /**
     * 分页相关常量
     */
    public static final class Pagination {
        /** 默认页码 */
        public static final int DEFAULT_PAGE = 0;
        /** 默认页大小 */
        public static final int DEFAULT_SIZE = 20;
        /** 最大页大小 */
        public static final int MAX_SIZE = 1000;
    }

    /**
     * 排序相关常量
     */
    public static final class Sort {
        /** 升序 */
        public static final String ASC = "asc";
        /** 降序 */
        public static final String DESC = "desc";
        /** 默认排序字段 */
        public static final String DEFAULT_SORT_FIELD = "createdTime";
        /** 默认排序方向 */
        public static final String DEFAULT_SORT_DIRECTION = DESC;
    }
} 