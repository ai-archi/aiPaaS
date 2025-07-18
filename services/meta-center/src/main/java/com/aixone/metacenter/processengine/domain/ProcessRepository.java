package com.aixone.metacenter.processengine.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 流程仓储接口
 * 负责流程的数据访问
 */
@Repository
public interface ProcessRepository extends JpaRepository<Process, Long> {

    /**
     * 根据名称查询流程
     *
     * @param name 流程名称
     * @return 流程
     */
    Optional<Process> findByName(String name);

    /**
     * 根据流程类型查询流程列表
     *
     * @param processType 流程类型
     * @return 流程列表
     */
    List<Process> findByProcessType(String processType);

    /**
     * 根据状态查询流程列表
     *
     * @param status 状态
     * @return 流程列表
     */
    List<Process> findByStatus(String status);

    /**
     * 根据流程类型和状态查询流程列表
     *
     * @param processType 流程类型
     * @param status 状态
     * @return 流程列表
     */
    List<Process> findByProcessTypeAndStatus(String processType, String status);

    /**
     * 检查流程名称是否存在
     *
     * @param name 流程名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 根据名称模糊查询流程
     *
     * @param name 流程名称（模糊匹配）
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Process> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * 根据显示名称模糊查询流程
     *
     * @param displayName 显示名称（模糊匹配）
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Process> findByDisplayNameContainingIgnoreCase(String displayName, Pageable pageable);

    /**
     * 根据流程类型分页查询流程
     *
     * @param processType 流程类型
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Process> findByProcessType(String processType, Pageable pageable);

    /**
     * 根据状态分页查询流程
     *
     * @param status 状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Process> findByStatus(String status, Pageable pageable);

    /**
     * 统计流程类型数量
     *
     * @param processType 流程类型
     * @return 数量
     */
    long countByProcessType(String processType);

    /**
     * 统计状态数量
     *
     * @param status 状态
     * @return 数量
     */
    long countByStatus(String status);

    /**
     * 根据流程类型删除流程
     *
     * @param processType 流程类型
     */
    void deleteByProcessType(String processType);

    /**
     * 根据状态删除流程
     *
     * @param status 状态
     */
    void deleteByStatus(String status);
} 