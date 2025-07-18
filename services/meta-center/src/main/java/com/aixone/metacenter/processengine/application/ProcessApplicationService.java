package com.aixone.metacenter.processengine.application;

import com.aixone.metacenter.processengine.domain.Process;
import com.aixone.metacenter.processengine.domain.ProcessRepository;
import com.aixone.metacenter.common.exception.MetaNotFoundException;
import com.aixone.metacenter.common.exception.MetaValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 流程应用服务
 * 负责流程管理的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProcessApplicationService {

    private final ProcessRepository processRepository;

    /**
     * 创建流程
     *
     * @param process 流程实体
     * @return 创建的流程
     */
    public Process createProcess(Process process) {
        log.info("创建流程: {}", process.getName());
        
        // 验证流程名称唯一性
        if (processRepository.existsByName(process.getName())) {
            throw new MetaValidationException("流程名称已存在: " + process.getName());
        }
        
        // 设置创建时间
        process.setCreatedTime(LocalDateTime.now());
        process.setUpdatedTime(LocalDateTime.now());
        
        // 保存流程
        Process savedProcess = processRepository.save(process);
        
        log.info("流程创建成功: {}", savedProcess.getId());
        return savedProcess;
    }

    /**
     * 更新流程
     *
     * @param id 流程ID
     * @param process 流程实体
     * @return 更新后的流程
     */
    public Process updateProcess(Long id, Process process) {
        log.info("更新流程: {}", id);
        
        Process existingProcess = processRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("流程不存在: " + id));
        
        // 验证流程名称唯一性（排除自身）
        if (!existingProcess.getName().equals(process.getName()) &&
            processRepository.existsByName(process.getName())) {
            throw new MetaValidationException("流程名称已存在: " + process.getName());
        }
        
        // 更新流程属性
        existingProcess.setName(process.getName());
        existingProcess.setDisplayName(process.getDisplayName());
        existingProcess.setDescription(process.getDescription());
        existingProcess.setProcessType(process.getProcessType());
        existingProcess.setStatus(process.getStatus());
        existingProcess.setUpdatedTime(LocalDateTime.now());
        
        // 保存流程
        Process savedProcess = processRepository.save(existingProcess);
        
        log.info("流程更新成功: {}", id);
        return savedProcess;
    }

    /**
     * 删除流程
     *
     * @param id 流程ID
     */
    public void deleteProcess(Long id) {
        log.info("删除流程: {}", id);
        
        Process process = processRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("流程不存在: " + id));
        
        processRepository.delete(process);
        log.info("流程删除成功: {}", id);
    }

    /**
     * 根据ID查询流程
     *
     * @param id 流程ID
     * @return 流程
     */
    @Transactional(readOnly = true)
    public Process getProcessById(Long id) {
        log.debug("查询流程: {}", id);
        
        return processRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("流程不存在: " + id));
    }

    /**
     * 根据名称查询流程
     *
     * @param name 流程名称
     * @return 流程
     */
    @Transactional(readOnly = true)
    public Optional<Process> getProcessByName(String name) {
        log.debug("根据名称查询流程: {}", name);
        
        return processRepository.findByName(name);
    }

    /**
     * 分页查询流程
     *
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Transactional(readOnly = true)
    public Page<Process> getProcesses(Pageable pageable) {
        log.debug("分页查询流程");
        
        return processRepository.findAll(pageable);
    }

    /**
     * 根据流程类型查询流程列表
     *
     * @param processType 流程类型
     * @return 流程列表
     */
    @Transactional(readOnly = true)
    public List<Process> getProcessesByType(String processType) {
        log.debug("根据流程类型查询: {}", processType);
        
        return processRepository.findByProcessType(processType);
    }

    /**
     * 根据状态查询流程列表
     *
     * @param status 状态
     * @return 流程列表
     */
    @Transactional(readOnly = true)
    public List<Process> getProcessesByStatus(String status) {
        log.debug("根据状态查询流程: {}", status);
        
        return processRepository.findByStatus(status);
    }

    /**
     * 启动流程
     *
     * @param id 流程ID
     * @return 更新后的流程
     */
    public Process startProcess(Long id) {
        log.info("启动流程: {}", id);
        
        Process process = processRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("流程不存在: " + id));
        
        process.setStatus("running");
        process.setUpdatedTime(LocalDateTime.now());
        
        return processRepository.save(process);
    }

    /**
     * 启动流程（带数据）
     *
     * @param id 流程ID
     * @param data 流程启动数据
     * @return 启动结果
     */
    public Object startProcess(Long id, Object data) {
        log.info("启动流程: {}, 数据: {}", id, data);
        
        Process process = processRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("流程不存在: " + id));
        
        process.setStatus("running");
        process.setUpdatedTime(LocalDateTime.now());
        processRepository.save(process);
        
        // 这里可以添加流程执行逻辑
        // 暂时返回流程实例ID
        return "process_instance_" + System.currentTimeMillis();
    }

    /**
     * 暂停流程
     *
     * @param id 流程ID
     * @return 更新后的流程
     */
    public Process pauseProcess(Long id) {
        log.info("暂停流程: {}", id);
        
        Process process = processRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("流程不存在: " + id));
        
        process.setStatus("paused");
        process.setUpdatedTime(LocalDateTime.now());
        
        return processRepository.save(process);
    }

    /**
     * 恢复流程
     *
     * @param id 流程ID
     * @return 更新后的流程
     */
    public Process resumeProcess(Long id) {
        log.info("恢复流程: {}", id);
        
        Process process = processRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("流程不存在: " + id));
        
        process.setStatus("running");
        process.setUpdatedTime(LocalDateTime.now());
        
        return processRepository.save(process);
    }

    /**
     * 终止流程
     *
     * @param id 流程ID
     * @return 更新后的流程
     */
    public Process terminateProcess(Long id) {
        log.info("终止流程: {}", id);
        
        Process process = processRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("流程不存在: " + id));
        
        process.setStatus("terminated");
        process.setUpdatedTime(LocalDateTime.now());
        
        return processRepository.save(process);
    }

    /**
     * 停止流程
     *
     * @param id 流程ID
     * @return 更新后的流程
     */
    public Process stopProcess(Long id) {
        log.info("停止流程: {}", id);
        
        Process process = processRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("流程不存在: " + id));
        
        process.setStatus("stopped");
        process.setUpdatedTime(LocalDateTime.now());
        
        return processRepository.save(process);
    }

    /**
     * 检查流程名称是否存在
     *
     * @param name 流程名称
     * @return 是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return processRepository.existsByName(name);
    }
} 