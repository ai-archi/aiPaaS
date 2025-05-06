package com.aixone.llm.domain.services;

import com.aixone.llm.domain.services.impl.AssistantServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AssistantServiceImpl.class)
public class AssistantServiceTest {
    @Autowired
    private AssistantService assistantService;

    @Test
    void contextLoads() {
        assertThat(assistantService).isNotNull();
    }
} 