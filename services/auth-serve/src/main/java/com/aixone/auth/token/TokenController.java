package com.aixone.auth.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 令牌管理API
 */
@RestController
@RequestMapping("/api/v1/tokens")
public class TokenController {
    @Autowired
    private TokenService tokenService;

    /** 查询所有令牌 */
    @GetMapping
    public List<Token> listTokens() {
        return tokenService.findAll();
    }

    /** 根据ID查询令牌 */
    @GetMapping("/{id}")
    public Optional<Token> getToken(@PathVariable("id") String token) {
        return tokenService.findById(token);
    }

    /** 新增令牌 */
    @PostMapping
    public Token createToken(@RequestBody Token token) {
        return tokenService.save(token);
    }

    /** 删除令牌 */
    @DeleteMapping("/{id}")
    public void deleteToken(@PathVariable("id") String token) {
        tokenService.deleteById(token);
    }
} 