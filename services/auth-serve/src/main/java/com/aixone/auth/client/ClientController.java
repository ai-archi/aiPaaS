package com.aixone.auth.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 客户端管理API
 */
@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {
    @Autowired
    private ClientService clientService;

    /** 查询所有客户端 */
    @GetMapping
    public List<Client> listClients() {
        return clientService.findAll();
    }

    /** 根据ID查询客户端 */
    @GetMapping("/{id}")
    public Optional<Client> getClient(@PathVariable("id") String clientId) {
        return clientService.findById(clientId);
    }

    /** 新增客户端 */
    @PostMapping
    public Client createClient(@RequestBody Client client) {
        return clientService.save(client);
    }

    /** 删除客户端 */
    @DeleteMapping("/{id}")
    public void deleteClient(@PathVariable("id") String clientId) {
        clientService.deleteById(clientId);
    }
} 