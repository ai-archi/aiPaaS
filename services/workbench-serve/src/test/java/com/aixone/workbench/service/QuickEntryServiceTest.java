package com.aixone.workbench.service;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * QuickEntryService 单元测试。
 */
public class QuickEntryServiceTest {
    private final QuickEntryService quickEntryService = new com.aixone.workbench.service.impl.QuickEntryServiceImpl();

    @Test
    void testGetQuickEntries() {
        assertNotNull(quickEntryService.getQuickEntries(UUID.randomUUID()));
    }

    @Test
    void testSaveQuickEntries() {
        assertDoesNotThrow(() -> quickEntryService.saveQuickEntries(UUID.randomUUID(), "[]"));
    }

    @Test
    void testGetDashboard() {
        assertNotNull(quickEntryService.getDashboard(UUID.randomUUID()));
    }

    @Test
    void testSaveDashboard() {
        assertDoesNotThrow(() -> quickEntryService.saveDashboard(UUID.randomUUID(), "{}"));
    }
} 