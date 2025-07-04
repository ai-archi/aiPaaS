package com.aixone.auth.client;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 客户端Repository
 * 提供Client实体的基本CRUD操作
 */
public interface ClientRepository extends JpaRepository<Client, String> {
} 