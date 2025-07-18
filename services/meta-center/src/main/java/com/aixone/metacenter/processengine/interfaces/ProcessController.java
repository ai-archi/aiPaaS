package com.aixone.metacenter.processengine.interfaces;

import com.aixone.metacenter.common.response.ApiResponse;
import com.aixone.metacenter.processengine.application.ProcessApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 流程引擎REST控制器
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Slf4j
@RestController
@RequestMapping("/processes")
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessApplicationService processApplicationService;

    /**
     * 启动流程实例
     * 
     * @param processId 流程定义ID
     * @param variables 流程变量
     * @return 流程实例ID
     */
    @PostMapping("/{processId}/start")
    public ResponseEntity<ApiResponse<String>> startProcess(
            @PathVariable Long processId,
            @RequestBody Map<String, Object> variables) {
        log.info("启动流程实例: processId={}", processId);
        String instanceId = processApplicationService.startProcess(processId, variables);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(instanceId, "流程实例启动成功"));
    }

    /**
     * 暂停流程实例
     * 
     * @param instanceId 流程实例ID
     * @return 暂停结果
     */
    @PostMapping("/instances/{instanceId}/suspend")
    public ResponseEntity<ApiResponse<Void>> suspendProcessInstance(@PathVariable String instanceId) {
        log.info("暂停流程实例: {}", instanceId);
        processApplicationService.suspendProcessInstance(instanceId);
        return ResponseEntity.ok(ApiResponse.success(null, "流程实例暂停成功"));
    }

    /**
     * 恢复流程实例
     * 
     * @param instanceId 流程实例ID
     * @return 恢复结果
     */
    @PostMapping("/instances/{instanceId}/resume")
    public ResponseEntity<ApiResponse<Void>> resumeProcessInstance(@PathVariable String instanceId) {
        log.info("恢复流程实例: {}", instanceId);
        processApplicationService.resumeProcessInstance(instanceId);
        return ResponseEntity.ok(ApiResponse.success(null, "流程实例恢复成功"));
    }

    /**
     * 终止流程实例
     * 
     * @param instanceId 流程实例ID
     * @param reason 终止原因
     * @return 终止结果
     */
    @PostMapping("/instances/{instanceId}/terminate")
    public ResponseEntity<ApiResponse<Void>> terminateProcessInstance(
            @PathVariable String instanceId,
            @RequestParam(required = false) String reason) {
        log.info("终止流程实例: instanceId={}, reason={}", instanceId, reason);
        processApplicationService.terminateProcessInstance(instanceId, reason);
        return ResponseEntity.ok(ApiResponse.success(null, "流程实例终止成功"));
    }

    /**
     * 完成当前任务
     * 
     * @param taskId 任务ID
     * @param variables 任务变量
     * @return 完成结果
     */
    @PostMapping("/tasks/{taskId}/complete")
    public ResponseEntity<ApiResponse<Void>> completeTask(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> variables) {
        log.info("完成任务: taskId={}", taskId);
        processApplicationService.completeTask(taskId, variables);
        return ResponseEntity.ok(ApiResponse.success(null, "任务完成成功"));
    }

    /**
     * 委派任务
     * 
     * @param taskId 任务ID
     * @param assignee 委派人
     * @return 委派结果
     */
    @PostMapping("/tasks/{taskId}/delegate")
    public ResponseEntity<ApiResponse<Void>> delegateTask(
            @PathVariable String taskId,
            @RequestParam String assignee) {
        log.info("委派任务: taskId={}, assignee={}", taskId, assignee);
        processApplicationService.delegateTask(taskId, assignee);
        return ResponseEntity.ok(ApiResponse.success(null, "任务委派成功"));
    }

    /**
     * 转办任务
     * 
     * @param taskId 任务ID
     * @param assignee 转办人
     * @return 转办结果
     */
    @PostMapping("/tasks/{taskId}/transfer")
    public ResponseEntity<ApiResponse<Void>> transferTask(
            @PathVariable String taskId,
            @RequestParam String assignee) {
        log.info("转办任务: taskId={}, assignee={}", taskId, assignee);
        processApplicationService.transferTask(taskId, assignee);
        return ResponseEntity.ok(ApiResponse.success(null, "任务转办成功"));
    }

    /**
     * 获取流程实例详情
     * 
     * @param instanceId 流程实例ID
     * @return 流程实例详情
     */
    @GetMapping("/instances/{instanceId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProcessInstance(@PathVariable String instanceId) {
        log.info("获取流程实例详情: {}", instanceId);
        Map<String, Object> instance = processApplicationService.getProcessInstance(instanceId);
        return ResponseEntity.ok(ApiResponse.success(instance, "获取流程实例详情成功"));
    }

    /**
     * 获取流程实例列表
     * 
     * @param processId 流程定义ID
     * @param status 状态
     * @param page 页码
     * @param size 页大小
     * @return 流程实例列表
     */
    @GetMapping("/instances")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getProcessInstances(
            @RequestParam(required = false) Long processId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("获取流程实例列表: processId={}, status={}, page={}, size={}", processId, status, page, size);
        List<Map<String, Object>> instances = processApplicationService.getProcessInstances(processId, status, page, size);
        return ResponseEntity.ok(ApiResponse.success(instances, "获取流程实例列表成功"));
    }

    /**
     * 获取待办任务列表
     * 
     * @param assignee 处理人
     * @param processId 流程定义ID
     * @param page 页码
     * @param size 页大小
     * @return 待办任务列表
     */
    @GetMapping("/tasks/todo")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTodoTasks(
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) Long processId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("获取待办任务列表: assignee={}, processId={}, page={}, size={}", assignee, processId, page, size);
        List<Map<String, Object>> tasks = processApplicationService.getTodoTasks(assignee, processId, page, size);
        return ResponseEntity.ok(ApiResponse.success(tasks, "获取待办任务列表成功"));
    }

    /**
     * 获取已办任务列表
     * 
     * @param assignee 处理人
     * @param processId 流程定义ID
     * @param page 页码
     * @param size 页大小
     * @return 已办任务列表
     */
    @GetMapping("/tasks/done")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDoneTasks(
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) Long processId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("获取已办任务列表: assignee={}, processId={}, page={}, size={}", assignee, processId, page, size);
        List<Map<String, Object>> tasks = processApplicationService.getDoneTasks(assignee, processId, page, size);
        return ResponseEntity.ok(ApiResponse.success(tasks, "获取已办任务列表成功"));
    }

    /**
     * 获取流程历史记录
     * 
     * @param instanceId 流程实例ID
     * @return 历史记录
     */
    @GetMapping("/instances/{instanceId}/history")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getProcessHistory(@PathVariable String instanceId) {
        log.info("获取流程历史记录: {}", instanceId);
        List<Map<String, Object>> history = processApplicationService.getProcessHistory(instanceId);
        return ResponseEntity.ok(ApiResponse.success(history, "获取流程历史记录成功"));
    }

    /**
     * 获取流程变量
     * 
     * @param instanceId 流程实例ID
     * @return 流程变量
     */
    @GetMapping("/instances/{instanceId}/variables")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProcessVariables(@PathVariable String instanceId) {
        log.info("获取流程变量: {}", instanceId);
        Map<String, Object> variables = processApplicationService.getProcessVariables(instanceId);
        return ResponseEntity.ok(ApiResponse.success(variables, "获取流程变量成功"));
    }

    /**
     * 设置流程变量
     * 
     * @param instanceId 流程实例ID
     * @param variables 流程变量
     * @return 设置结果
     */
    @PostMapping("/instances/{instanceId}/variables")
    public ResponseEntity<ApiResponse<Void>> setProcessVariables(
            @PathVariable String instanceId,
            @RequestBody Map<String, Object> variables) {
        log.info("设置流程变量: instanceId={}", instanceId);
        processApplicationService.setProcessVariables(instanceId, variables);
        return ResponseEntity.ok(ApiResponse.success(null, "设置流程变量成功"));
    }

    /**
     * 获取流程统计信息
     * 
     * @param processId 流程定义ID
     * @return 统计信息
     */
    @GetMapping("/{processId}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProcessStats(@PathVariable Long processId) {
        log.info("获取流程统计信息: {}", processId);
        Map<String, Object> stats = processApplicationService.getProcessStats(processId);
        return ResponseEntity.ok(ApiResponse.success(stats, "获取流程统计信息成功"));
    }
} 