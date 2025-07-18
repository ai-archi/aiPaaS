package com.aixone.metacenter.processengine.interfaces;

import com.aixone.metacenter.processengine.application.ProcessApplicationService;
import com.aixone.metacenter.processengine.domain.Process;
import com.aixone.metacenter.common.constant.MetaConstants;
import com.aixone.metacenter.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 流程引擎控制器
 * 提供流程管理的REST API接口
 */
@Slf4j
@RestController
@RequestMapping(MetaConstants.Api.API_PREFIX + "/processes")
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessApplicationService processApplicationService;

    /**
     * 创建流程
     *
     * @param process 流程对象
     * @return 创建的流程
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Process>> createProcess(@Valid @RequestBody Process process) {
        try {
            log.info("创建流程: {}", process.getName());
            Process createdProcess = processApplicationService.createProcess(process);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdProcess, "流程创建成功"));
        } catch (Exception e) {
            log.error("创建流程失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("PROCESS_CREATE_ERROR", "创建流程失败: " + e.getMessage()));
        }
    }

    /**
     * 更新流程
     *
     * @param id 流程ID
     * @param process 流程对象
     * @return 更新后的流程
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Process>> updateProcess(@PathVariable Long id, @Valid @RequestBody Process process) {
        try {
            log.info("更新流程: {}", id);
            Process updatedProcess = processApplicationService.updateProcess(id, process);
            return ResponseEntity.ok(ApiResponse.success(updatedProcess, "流程更新成功"));
        } catch (Exception e) {
            log.error("更新流程失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("PROCESS_UPDATE_ERROR", "更新流程失败: " + e.getMessage()));
        }
    }

    /**
     * 删除流程
     *
     * @param id 流程ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProcess(@PathVariable Long id) {
        try {
            log.info("删除流程: {}", id);
            processApplicationService.deleteProcess(id);
            return ResponseEntity.ok(ApiResponse.success(null, "流程删除成功"));
        } catch (Exception e) {
            log.error("删除流程失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("PROCESS_DELETE_ERROR", "删除流程失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID查询流程
     *
     * @param id 流程ID
     * @return 流程
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Process>> getProcessById(@PathVariable Long id) {
        try {
            log.debug("查询流程: {}", id);
            Process process = processApplicationService.getProcessById(id);
            return ResponseEntity.ok(ApiResponse.success(process, "流程查询成功"));
        } catch (Exception e) {
            log.error("查询流程失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("PROCESS_QUERY_ERROR", "查询流程失败: " + e.getMessage()));
        }
    }

    /**
     * 分页查询流程
     *
     * @param page 页码
     * @param size 页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页结果
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Process>>> getProcesses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            log.debug("分页查询流程: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
            
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Process> processes = processApplicationService.getProcesses(pageable);
            return ResponseEntity.ok(ApiResponse.success(processes, "流程分页查询成功"));
        } catch (Exception e) {
            log.error("分页查询流程失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("PROCESS_QUERY_ERROR", "分页查询流程失败: " + e.getMessage()));
        }
    }

    /**
     * 启动流程
     *
     * @param id 流程ID
     * @param data 流程启动数据
     * @return 启动结果
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<Object>> startProcess(@PathVariable Long id, @RequestBody Object data) {
        try {
            log.info("启动流程: {}", id);
            Object result = processApplicationService.startProcess(id, data);
            return ResponseEntity.ok(ApiResponse.success(result, "流程启动成功"));
        } catch (Exception e) {
            log.error("启动流程失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("PROCESS_START_ERROR", "启动流程失败: " + e.getMessage()));
        }
    }

    /**
     * 暂停流程
     *
     * @param id 流程ID
     * @return 暂停结果
     */
    @PutMapping("/{id}/pause")
    public ResponseEntity<ApiResponse<Process>> pauseProcess(@PathVariable Long id) {
        try {
            log.info("暂停流程: {}", id);
            Process process = processApplicationService.pauseProcess(id);
            return ResponseEntity.ok(ApiResponse.success(process, "流程暂停成功"));
        } catch (Exception e) {
            log.error("暂停流程失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("PROCESS_PAUSE_ERROR", "暂停流程失败: " + e.getMessage()));
        }
    }

    /**
     * 恢复流程
     *
     * @param id 流程ID
     * @return 恢复结果
     */
    @PutMapping("/{id}/resume")
    public ResponseEntity<ApiResponse<Process>> resumeProcess(@PathVariable Long id) {
        try {
            log.info("恢复流程: {}", id);
            Process process = processApplicationService.resumeProcess(id);
            return ResponseEntity.ok(ApiResponse.success(process, "流程恢复成功"));
        } catch (Exception e) {
            log.error("恢复流程失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("PROCESS_RESUME_ERROR", "恢复流程失败: " + e.getMessage()));
        }
    }

    /**
     * 终止流程
     *
     * @param id 流程ID
     * @return 终止结果
     */
    @PutMapping("/{id}/terminate")
    public ResponseEntity<ApiResponse<Process>> terminateProcess(@PathVariable Long id) {
        try {
            log.info("终止流程: {}", id);
            Process process = processApplicationService.terminateProcess(id);
            return ResponseEntity.ok(ApiResponse.success(process, "流程终止成功"));
        } catch (Exception e) {
            log.error("终止流程失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("PROCESS_TERMINATE_ERROR", "终止流程失败: " + e.getMessage()));
        }
    }

    /**
     * 根据流程类型查询流程列表
     *
     * @param processType 流程类型
     * @return 流程列表
     */
    @GetMapping("/by-type/{processType}")
    public ResponseEntity<ApiResponse<List<Process>>> getProcessesByType(@PathVariable String processType) {
        try {
            log.debug("根据流程类型查询流程列表: {}", processType);
            List<Process> processes = processApplicationService.getProcessesByType(processType);
            return ResponseEntity.ok(ApiResponse.success(processes, "流程类型查询成功"));
        } catch (Exception e) {
            log.error("根据流程类型查询流程列表失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("PROCESS_QUERY_ERROR", "根据流程类型查询流程列表失败: " + e.getMessage()));
        }
    }

    /**
     * 根据状态查询流程列表
     *
     * @param status 状态
     * @return 流程列表
     */
    @GetMapping("/by-status/{status}")
    public ResponseEntity<ApiResponse<List<Process>>> getProcessesByStatus(@PathVariable String status) {
        try {
            log.debug("根据状态查询流程列表: {}", status);
            List<Process> processes = processApplicationService.getProcessesByStatus(status);
            return ResponseEntity.ok(ApiResponse.success(processes, "流程状态查询成功"));
        } catch (Exception e) {
            log.error("根据状态查询流程列表失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("PROCESS_QUERY_ERROR", "根据状态查询流程列表失败: " + e.getMessage()));
        }
    }
} 