package com.aixone.metacenter.metamanagement.domain.service;

import com.aixone.metacenter.metamanagement.domain.MetaRelation;
import com.aixone.metacenter.metamanagement.domain.MetaRelationRepository;
import com.aixone.metacenter.common.exception.MetaValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.aixone.common.tools.ValidationUtils;
import com.aixone.common.tools.StringUtils;

import java.util.List;

/**
 * 元数据关系领域服务
 * 负责元数据关系的领域业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetaRelationDomainService {

    private final MetaRelationRepository metaRelationRepository;

    /**
     * 验证元数据关系
     *
     * @param metaRelation 元数据关系
     */
    public void validateMetaRelation(MetaRelation metaRelation) {
        log.debug("验证元数据关系: {}", metaRelation.getName());
        
        // 验证名称
        String nameError = ValidationUtils.validateIdentifier(metaRelation.getName(), "关系名称");
        if (nameError != null) {
            throw new MetaValidationException(nameError);
        }
        
        // 验证显示名称
        if (StringUtils.isBlank(metaRelation.getDisplayName())) {
            throw new MetaValidationException("关系显示名称不能为空");
        }
        
        // 验证源对象
        if (metaRelation.getSourceObject() == null) {
            throw new MetaValidationException("源对象不能为空");
        }
        
        // 验证目标对象
        if (metaRelation.getTargetObject() == null) {
            throw new MetaValidationException("目标对象不能为空");
        }
        
        // 验证源对象和目标对象不能相同
        if (metaRelation.getSourceObject().getId().equals(metaRelation.getTargetObject().getId())) {
            throw new MetaValidationException("源对象和目标对象不能相同");
        }
        
        // 验证关系类型
        if (StringUtils.isBlank(metaRelation.getRelationType())) {
            throw new MetaValidationException("关系类型不能为空");
        }
        
        // 验证关系类型是否支持
        validateRelationType(metaRelation.getRelationType());
        
        // 验证基数
        if (StringUtils.isBlank(metaRelation.getCardinality())) {
            throw new MetaValidationException("关系基数不能为空");
        }
        
        // 验证基数格式
        validateCardinality(metaRelation.getCardinality());
        
        log.debug("元数据关系验证通过: {}", metaRelation.getName());
    }

    /**
     * 验证删除元数据关系
     *
     * @param metaRelation 元数据关系
     */
    public void validateDeleteMetaRelation(MetaRelation metaRelation) {
        log.debug("验证删除元数据关系: {}", metaRelation.getName());
        
        // 检查关系是否被引用
        // TODO: 实现引用检查逻辑
        
        log.debug("元数据关系删除验证通过: {}", metaRelation.getName());
    }

    /**
     * 验证关系类型
     *
     * @param relationType 关系类型
     */
    private void validateRelationType(String relationType) {
        String[] supportedTypes = {
            "ONE_TO_ONE", "ONE_TO_MANY", "MANY_TO_ONE", "MANY_TO_MANY",
            "INHERITANCE", "COMPOSITION", "AGGREGATION", "ASSOCIATION"
        };
        
        boolean isValid = false;
        for (String type : supportedTypes) {
            if (type.equalsIgnoreCase(relationType)) {
                isValid = true;
                break;
            }
        }
        
        if (!isValid) {
            throw new MetaValidationException("不支持的关系类型: " + relationType);
        }
    }

    /**
     * 验证基数格式
     *
     * @param cardinality 基数
     */
    private void validateCardinality(String cardinality) {
        // 基数格式：0..1, 1, 0..*, 1..*, *
        String[] validCardinalities = {
            "0..1", "1", "0..*", "1..*", "*"
        };
        
        boolean isValid = false;
        for (String card : validCardinalities) {
            if (card.equals(cardinality)) {
                isValid = true;
                break;
            }
        }
        
        if (!isValid) {
            throw new MetaValidationException("无效的基数格式: " + cardinality);
        }
    }

    /**
     * 检查关系名称是否重复
     *
     * @param name 关系名称
     * @param sourceObjectId 源对象ID
     * @param excludeId 排除的关系ID
     * @return 是否重复
     */
    public boolean isNameDuplicate(String name, Long sourceObjectId, Long excludeId) {
        List<MetaRelation> relations = metaRelationRepository.findBySourceObjectId(sourceObjectId);
        
        return relations.stream()
                .filter(rel -> !rel.getId().equals(excludeId))
                .anyMatch(rel -> rel.getName().equals(name));
    }

    /**
     * 检查是否存在循环关系
     *
     * @param sourceObjectId 源对象ID
     * @param targetObjectId 目标对象ID
     * @return 是否存在循环关系
     */
    public boolean hasCircularRelation(Long sourceObjectId, Long targetObjectId) {
        // 检查是否存在从目标对象到源对象的反向关系
        List<MetaRelation> reverseRelations = metaRelationRepository.findBySourceObjectIdAndTargetObjectId(
            targetObjectId, sourceObjectId);
        
        return !reverseRelations.isEmpty();
    }

    /**
     * 获取关系的反向关系
     *
     * @param relation 关系
     * @return 反向关系
     */
    public MetaRelation getReverseRelation(MetaRelation relation) {
        List<MetaRelation> reverseRelations = metaRelationRepository.findBySourceObjectIdAndTargetObjectId(
            relation.getTargetObject().getId(), relation.getSourceObject().getId());
        
        return reverseRelations.isEmpty() ? null : reverseRelations.get(0);
    }

    /**
     * 验证关系的一致性
     *
     * @param relation 关系
     */
    public void validateRelationConsistency(MetaRelation relation) {
        // 检查关系类型和基数的一致性
        String relationType = relation.getRelationType();
        String cardinality = relation.getCardinality();
        
        switch (relationType.toUpperCase()) {
            case "ONE_TO_ONE":
                if (!cardinality.equals("1")) {
                    throw new MetaValidationException("一对一关系的基数必须是1");
                }
                break;
            case "ONE_TO_MANY":
                if (!cardinality.equals("1..*") && !cardinality.equals("*")) {
                    throw new MetaValidationException("一对多关系的基数必须是1..*或*");
                }
                break;
            case "MANY_TO_ONE":
                if (!cardinality.equals("0..1") && !cardinality.equals("1")) {
                    throw new MetaValidationException("多对一关系的基数必须是0..1或1");
                }
                break;
            case "MANY_TO_MANY":
                if (!cardinality.equals("0..*") && !cardinality.equals("*")) {
                    throw new MetaValidationException("多对多关系的基数必须是0..*或*");
                }
                break;
        }
    }

    /**
     * 检查关系是否允许删除
     *
     * @param relation 关系
     * @return 是否允许删除
     */
    public boolean canDeleteRelation(MetaRelation relation) {
        // 检查关系类型
        if ("INHERITANCE".equalsIgnoreCase(relation.getRelationType()) ||
            "COMPOSITION".equalsIgnoreCase(relation.getRelationType())) {
            // 继承和组合关系通常不允许删除
            return false;
        }
        
        // TODO: 检查是否有数据依赖
        
        return true;
    }
} 