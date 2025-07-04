package com.aixone.auth.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 令牌服务实现
 */
@Service
public class TokenServiceImpl implements TokenService {
    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public Optional<Token> findById(String token) {
        return tokenRepository.findById(token);
    }

    @Override
    public List<Token> findAll() {
        return tokenRepository.findAll();
    }

    @Override
    public Token save(Token token) {
        return tokenRepository.save(token);
    }

    @Override
    public void deleteById(String token) {
        tokenRepository.deleteById(token);
    }
} 