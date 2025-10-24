# aixone-common-sdk

## 概述

通用基础设施SDK，提供纯基础设施能力，包括DDD基础组件、API响应包装、异常处理、工具类等通用能力。

## 功能模块

### 1. DDD基础组件 (`com.aixone.common.ddd`)
- `Entity<ID>` - 实体基类
- `ValueObject` - 值对象基类  
- `DomainEvent` - 领域事件基类
- `Repository<T>` - 仓储接口基类

### 2. API相关 (`com.aixone.common.api`)
- `ApiResponse<T>` - 统一API响应包装器
- `PageRequest` - 分页请求对象
- `PageResult<T>` - 分页结果对象
- `BaseDTO` - 通用DTO基类

### 3. 异常处理 (`com.aixone.common.exception`)
- `BaseException` - 统一异常基类
- `GlobalExceptionHandler` - 全局异常处理器
- `BizException` - 业务异常
- `NotFoundException` - 资源未找到异常
- `ValidationException` - 数据校验异常
- `UnauthorizedException` - 未授权异常
- `ForbiddenException` - 禁止访问异常

### 4. 常量定义 (`com.aixone.common.constant`)
- `CommonConstants` - 公共常量（如分页大小、通用字符串等）
- `SystemConstants` - 系统级常量（如系统名称、环境标识等）
- `enums.StatusEnum` - 通用启用/禁用状态枚举
- `enums.YesNoEnum` - 通用布尔枚举

### 5. 工具类 (`com.aixone.common.util`)
- `DateUtils` - 日期时间工具类
- `StringUtils` - 字符串工具类
- `ValidationUtils` - 校验工具类
- `JsonUtils` - JSON工具类

### 6. 扩展工具类 (`com.aixone.common.tools`)
- `StringUtils` - 字符串处理工具（增强版）
- `DateUtils` - 日期处理工具（增强版）
- `CollectionUtils` - 集合处理工具
- `BeanUtils` - Bean拷贝工具
- `DataTypeUtils` - 数据类型工具
- `ValidationUtils` - 验证工具（增强版）

## 多租户支持

本SDK不直接提供租户能力，而是通过依赖 `aixone-session-sdk` 来获取租户上下文：

```java
// 在业务代码中获取租户ID
String tenantId = SessionContext.getTenantId();

// 在Entity基类中自动注入租户ID
public abstract class Entity<ID> {
    protected String tenantId;
    
    protected Entity(ID id) {
        this.id = Objects.requireNonNull(id, "Entity ID cannot be null");
        this.tenantId = SessionContext.getTenantId();
    }
}
```

## 使用方式

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.aixone</groupId>
    <artifactId>aixone-common-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 使用DDD基础组件

```java
// 实体类
public class User extends Entity<Long> {
    private String name;
    private String email;
    
    public User(Long id, String name, String email) {
        super(id);
        this.name = name;
        this.email = email;
    }
}

// 值对象
public class Email extends ValueObject {
    private final String value;
    
    public Email(String value) {
        this.value = validateEmail(value);
    }
    
    private String validateEmail(String email) {
        // 邮箱格式校验
        return email;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Email other = (Email) obj;
        return Objects.equals(value, other.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
```

### 3. 使用API响应包装

```java
@RestController
public class UserController {
    
    @GetMapping("/users/{id}")
    public ApiResponse<UserDTO> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        return ApiResponse.success(userMapper.toDTO(user));
    }
    
    @GetMapping("/users")
    public ApiResponse<PageResult<UserDTO>> getUsers(PageRequest pageRequest) {
        PageResult<UserDTO> result = userService.findUsers(pageRequest);
        return ApiResponse.success(result);
    }
}
```

### 4. 使用常量定义

```java
@Service
public class UserService {
    
    public PageResult<User> findUsers(PageRequest pageRequest) {
        // 使用常量定义分页参数
        int pageSize = pageRequest.getPageSize() > 0 ? 
            pageRequest.getPageSize() : CommonConstants.DEFAULT_PAGE_SIZE;
        int pageNum = pageRequest.getPageNum() > 0 ? 
            pageRequest.getPageNum() : CommonConstants.DEFAULT_PAGE_NUM;
        
        // 使用枚举定义状态
        List<User> users = userRepository.findByStatus(StatusEnum.ENABLED);
        return new PageResult<>(users, pageNum, pageSize);
    }
    
    public void updateUserStatus(Long id, int status) {
        if (status != StatusEnum.ENABLED.getCode() && 
            status != StatusEnum.DISABLED.getCode()) {
            throw new ValidationException("无效的状态值");
        }
        // 更新用户状态逻辑
    }
}
```

### 5. 使用扩展工具类

```java
@Service
public class UserService {
    
    public UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        // 使用Bean拷贝工具
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
    
    public List<User> filterActiveUsers(List<User> users) {
        // 使用集合工具
        return CollectionUtils.distinct(
            users.stream()
                .filter(user -> StatusEnum.ENABLED.getCode().equals(user.getStatus()))
                .collect(Collectors.toList())
        );
    }
    
    public boolean validateUserData(CreateUserRequest request) {
        // 使用验证工具
        if (!ValidationUtils.isValidEmail(request.getEmail())) {
            return false;
        }
        if (!ValidationUtils.isValidPhone(request.getPhone())) {
            return false;
        }
        return true;
    }
    
    public String formatUserInfo(User user) {
        // 使用字符串工具
        String name = StringUtils.capitalize(user.getName());
        String email = StringUtils.trim(user.getEmail());
        return String.format("用户: %s, 邮箱: %s", name, email);
    }
}
```

### 6. 使用异常处理

```java
@Service
public class UserService {
    
    public User findById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new NotFoundException("用户不存在: " + id);
        }
        return user;
    }
    
    public void createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BizException("邮箱已存在: " + request.getEmail());
        }
        // 创建用户逻辑
    }
}
```

## 版本历史

- 1.0.0 - 初始版本，提供DDD基础组件、API响应、异常处理、工具类等通用能力
