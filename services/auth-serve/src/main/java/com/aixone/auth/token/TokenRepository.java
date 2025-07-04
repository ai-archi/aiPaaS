package com.aixone.auth.token;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 令牌Repository
 * 提供Token实体的基本CRUD操作
 */
public interface TokenRepository extends JpaRepository<Token, String> {
} 