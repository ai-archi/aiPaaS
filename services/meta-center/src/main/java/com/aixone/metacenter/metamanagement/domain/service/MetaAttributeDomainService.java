package com.aixone.metacenter.metamanagement.domain.service;

import com.aixone.metacenter.metamanagement.domain.MetaAttribute;
import com.aixone.metacenter.metamanagement.domain.MetaAttributeRepository;
import com.aixone.metacenter.common.exception.MetaValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.aixone.common.tools.ValidationUtils;
import com.aixone.common.tools.DataTypeUtils;
import com.aixone.common.tools.StringUtils;

import java.util.List;

/**
 * 元数据属性领域服务
 * 负责元数据属性的领域业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetaAttributeDomainService {

    private final MetaAttributeRepository metaAttributeRepository;

    /**
     * 验证元数据属性
     *
     * @param metaAttribute 元数据属性
     */
    public void validateMetaAttribute(MetaAttribute metaAttribute) {
        log.debug("验证元数据属性: {}", metaAttribute.getName());
        
        // 验证名称
        String nameError = ValidationUtils.validateIdentifier(metaAttribute.getName(), "属性名称");
        if (nameError != null) {
            throw new MetaValidationException(nameError);
        }
        
        // 验证显示名称
        if (StringUtils.isBlank(metaAttribute.getDisplayName())) {
            throw new MetaValidationException("属性显示名称不能为空");
        }
        
        // 验证数据类型
        String dataTypeError = DataTypeUtils.validateDataType(metaAttribute.getDataType());
        if (dataTypeError != null) {
            throw new MetaValidationException(dataTypeError);
        }
        
        // 验证数据类型是否支持
        validateDataType(metaAttribute.getDataType());
        
        // 验证长度
        if (metaAttribute.getLength() != null && metaAttribute.getLength() <= 0) {
            throw new MetaValidationException("属性长度必须大于0");
        }
        
        // 验证精度
        if (metaAttribute.getPrecision() != null && metaAttribute.getPrecision() <= 0) {
            throw new MetaValidationException("属性精度必须大于0");
        }
        
        // 验证小数位数
        if (metaAttribute.getScale() != null && metaAttribute.getScale() < 0) {
            throw new MetaValidationException("小数位数不能小于0");
        }
        
        // 验证精度和小数位数的关系
        if (metaAttribute.getPrecision() != null && metaAttribute.getScale() != null) {
            if (metaAttribute.getScale() > metaAttribute.getPrecision()) {
                throw new MetaValidationException("小数位数不能大于精度");
            }
        }
        
        log.debug("元数据属性验证通过: {}", metaAttribute.getName());
    }

    /**
     * 验证删除元数据属性
     *
     * @param metaAttribute 元数据属性
     */
    public void validateDeleteMetaAttribute(MetaAttribute metaAttribute) {
        log.debug("验证删除元数据属性: {}", metaAttribute.getName());
        
        // 检查属性是否被引用
        // TODO: 实现引用检查逻辑
        
        log.debug("元数据属性删除验证通过: {}", metaAttribute.getName());
    }

    /**
     * 验证数据类型
     *
     * @param dataType 数据类型
     */
    private void validateDataType(String dataType) {
        String error = DataTypeUtils.validateDataType(dataType);
        if (error != null) {
            throw new MetaValidationException(error);
        }
    }

    /**
     * 检查属性名称是否重复
     *
     * @param name 属性名称
     * @param metaObjectId 元数据对象ID
     * @param excludeId 排除的属性ID
     * @return 是否重复
     */
    public boolean isNameDuplicate(String name, Long metaObjectId, Long excludeId) {
        List<MetaAttribute> attributes = metaAttributeRepository.findByMetaObjectId(metaObjectId);
        
        return attributes.stream()
                .filter(attr -> !attr.getId().equals(excludeId))
                .anyMatch(attr -> attr.getName().equals(name));
    }

    /**
     * 获取属性的默认值
     *
     * @param dataType 数据类型
     * @return 默认值
     */
    public Object getDefaultValue(String dataType) {
        return DataTypeUtils.getDefaultValue(dataType);
    }

    /**
     * 验证属性值
     *
     * @param metaAttribute 元数据属性
     * @param value 属性值
     * @return 是否有效
     */
    public boolean validateAttributeValue(MetaAttribute metaAttribute, Object value) {
        if (value == null) {
            return !metaAttribute.getRequired();
        }
        
        return DataTypeUtils.isValidValue(metaAttribute.getDataType(), value, metaAttribute.getLength());
    }
} 