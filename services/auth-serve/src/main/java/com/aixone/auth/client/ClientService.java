package com.aixone.auth.client;

import java.util.List;
import java.util.Optional;

/**
 * 客户端服务接口
 */
public interface ClientService {
    List<Client> findAll();
    Optional<Client> findById(String clientId);
    Client save(Client client);
    void deleteById(String clientId);
} 