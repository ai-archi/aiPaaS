package com.aixone.workbench.quickentry.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.workbench.quickentry.application.dto.QuickEntryDTO;
import com.aixone.workbench.quickentry.domain.service.QuickEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 快捷入口控制器
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/workbench/quick-entries")
@Slf4j
@RequiredArgsConstructor
public class QuickEntryController {
    
    private final QuickEntryService quickEntryService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<QuickEntryDTO>>> getQuickEntries(
            @RequestParam UUID userId,
            @RequestParam UUID tenantId) {
        
        log.info("获取用户快捷入口: userId={}, tenantId={}", userId, tenantId);
        
        List<QuickEntryDTO> entries = quickEntryService.getQuickEntries(userId, tenantId);
        
        return ResponseEntity.ok(ApiResponse.success(entries));
    }
    
    @PutMapping
    public ResponseEntity<ApiResponse<Void>> saveQuickEntries(
            @RequestParam UUID userId,
            @RequestParam UUID tenantId,
            @RequestBody List<QuickEntryDTO> entries) {
        
        log.info("保存用户快捷入口: userId={}, tenantId={}", userId, tenantId);
        
        quickEntryService.saveQuickEntries(userId, tenantId, entries);
        
        return ResponseEntity.ok(ApiResponse.success());
    }
}

