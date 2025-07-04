package com.aixone.auth.user;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务接口
 * 定义用户相关的业务操作
 */
public interface UserService {
    /** 根据ID查找用户 */
    Optional<User> findById(String userId);
    /** 查询所有用户 */
    List<User> findAll();
    /** 新增用户 */
    User save(User user);
    /** 删除用户 */
    void deleteById(String userId);
    /** 根据用户名查找用户 */
    Optional<User> findByUsername(String username);
} 