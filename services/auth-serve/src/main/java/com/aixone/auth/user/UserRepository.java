package com.aixone.auth.user;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户Repository
 * 提供User实体的基本CRUD操作
 */
public interface UserRepository extends JpaRepository<User, String> {
} 