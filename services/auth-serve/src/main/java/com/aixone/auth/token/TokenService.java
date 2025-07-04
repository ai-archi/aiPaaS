package com.aixone.auth.token;

import java.util.List;
import java.util.Optional;

/**
 * 令牌服务接口
 * 定义Token相关的业务操作
 */
public interface TokenService {
    Optional<Token> findById(String token);
    List<Token> findAll();
    Token save(Token token);
    void deleteById(String token);
} 