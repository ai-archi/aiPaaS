package com.aixone.metacenter.metamanagement.domain.service;

import com.aixone.metacenter.common.exception.MetaValidationException;
import com.aixone.metacenter.metamanagement.application.dto.MetaObjectDTO;
import com.aixone.metacenter.metamanagement.domain.MetaObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 元数据对象领域服务
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetaObjectDomainService {

    /**
     * 校验元数据对象
     * 
     * @param dto 元数据对象DTO
     * @return 校验结果
     */
    public boolean validateMetaObject(MetaObjectDTO dto) {
        log.debug("校验元数据对象: {}", dto.getName());

        // 校验属性
        if (dto.getAttributes() != null) {
            for (var attribute : dto.getAttributes()) {
                validateAttribute(attribute);
            }
        }

        // 校验规则
        if (dto.getRules() != null) {
            for (var rule : dto.getRules()) {
                validateRule(rule);
            }
        }

        // 校验扩展点
        if (dto.getExtensions() != null) {
            for (var extension : dto.getExtensions()) {
                validateExtension(extension);
            }
        }

        return true;
    }

    /**
     * 分析依赖关系
     * 
     * @param metaObject 元数据对象
     * @return 依赖分析结果
     */
    public Map<String, Object> analyzeDependencies(MetaObject metaObject) {
        log.debug("分析元数据对象依赖: {}", metaObject.getName());

        Map<String, Object> result = new HashMap<>();
        result.put("hasDependencies", false);
        result.put("dependencies", new HashMap<>());

        // TODO: 实现依赖分析逻辑
        // 1. 检查是否有其他元数据对象引用此对象
        // 2. 检查是否有数据实例使用此元数据
        // 3. 检查是否有规则、流程等依赖此元数据

        return result;
    }

    /**
     * 预览元数据对象变更
     * 
     * @param dto 元数据对象DTO
     * @return 变更预览结果
     */
    public Map<String, Object> previewMetaObject(MetaObjectDTO dto) {
        log.debug("预览元数据对象变更: {}", dto.getName());

        Map<String, Object> result = new HashMap<>();
        result.put("changes", new HashMap<>());
        result.put("impact", new HashMap<>());

        // TODO: 实现变更预览逻辑
        // 1. 对比变更前后的差异
        // 2. 分析变更影响范围
        // 3. 评估变更风险

        return result;
    }

    /**
     * 校验属性
     * 
     * @param attribute 属性DTO
     */
    private void validateAttribute(com.aixone.metacenter.metamanagement.application.dto.MetaAttributeDTO attribute) {
        if (attribute.getName() == null || attribute.getName().trim().isEmpty()) {
            throw new MetaValidationException("属性名称不能为空");
        }
        if (attribute.getLabel() == null || attribute.getLabel().trim().isEmpty()) {
            throw new MetaValidationException("属性标签不能为空");
        }
        if (attribute.getType() == null || attribute.getType().trim().isEmpty()) {
            throw new MetaValidationException("属性类型不能为空");
        }
    }

    /**
     * 校验规则
     * 
     * @param rule 规则DTO
     */
    private void validateRule(com.aixone.metacenter.metamanagement.application.dto.MetaRuleDTO rule) {
        if (rule.getName() == null || rule.getName().trim().isEmpty()) {
            throw new MetaValidationException("规则名称不能为空");
        }
        if (rule.getRuleType() == null || rule.getRuleType().trim().isEmpty()) {
            throw new MetaValidationException("规则类型不能为空");
        }
        if (rule.getExpression() == null || rule.getExpression().trim().isEmpty()) {
            throw new MetaValidationException("规则表达式不能为空");
        }
    }

    /**
     * 校验扩展点
     * 
     * @param extension 扩展点DTO
     */
    private void validateExtension(com.aixone.metacenter.metamanagement.application.dto.MetaExtensionDTO extension) {
        if (extension.getExtensionKey() == null || extension.getExtensionKey().trim().isEmpty()) {
            throw new MetaValidationException("扩展点标识不能为空");
        }
        if (extension.getExtensionType() == null || extension.getExtensionType().trim().isEmpty()) {
            throw new MetaValidationException("扩展点类型不能为空");
        }
    }
} 