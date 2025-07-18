package com.aixone.metacenter.metamanagement.domain.service;

import com.aixone.metacenter.metamanagement.domain.MetaAttribute;
import com.aixone.metacenter.metamanagement.domain.MetaAttributeRepository;
import com.aixone.metacenter.common.exception.MetaValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        if (!StringUtils.hasText(metaAttribute.getName())) {
            throw new MetaValidationException("属性名称不能为空");
        }
        
        // 验证名称格式（只能包含字母、数字、下划线）
        if (!metaAttribute.getName().matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
            throw new MetaValidationException("属性名称格式不正确，只能包含字母、数字、下划线，且必须以字母开头");
        }
        
        // 验证显示名称
        if (!StringUtils.hasText(metaAttribute.getDisplayName())) {
            throw new MetaValidationException("属性显示名称不能为空");
        }
        
        // 验证数据类型
        if (!StringUtils.hasText(metaAttribute.getDataType())) {
            throw new MetaValidationException("数据类型不能为空");
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
        String[] supportedTypes = {
            "STRING", "INTEGER", "LONG", "DOUBLE", "DECIMAL", "BOOLEAN", 
            "DATE", "DATETIME", "TIMESTAMP", "TEXT", "BLOB", "JSON"
        };
        
        boolean isValid = false;
        for (String type : supportedTypes) {
            if (type.equalsIgnoreCase(dataType)) {
                isValid = true;
                break;
            }
        }
        
        if (!isValid) {
            throw new MetaValidationException("不支持的数据类型: " + dataType);
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
        switch (dataType.toUpperCase()) {
            case "STRING":
            case "TEXT":
                return "";
            case "INTEGER":
            case "LONG":
                return 0;
            case "DOUBLE":
            case "DECIMAL":
                return 0.0;
            case "BOOLEAN":
                return false;
            case "DATE":
            case "DATETIME":
            case "TIMESTAMP":
                return null;
            case "JSON":
                return "{}";
            default:
                return null;
        }
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
        
        try {
            switch (metaAttribute.getDataType().toUpperCase()) {
                case "STRING":
                case "TEXT":
                    String strValue = String.valueOf(value);
                    if (metaAttribute.getLength() != null && strValue.length() > metaAttribute.getLength()) {
                        return false;
                    }
                    break;
                case "INTEGER":
                    Integer.parseInt(String.valueOf(value));
                    break;
                case "LONG":
                    Long.parseLong(String.valueOf(value));
                    break;
                case "DOUBLE":
                case "DECIMAL":
                    Double.parseDouble(String.valueOf(value));
                    break;
                case "BOOLEAN":
                    Boolean.parseBoolean(String.valueOf(value));
                    break;
                case "DATE":
                case "DATETIME":
                case "TIMESTAMP":
                    // TODO: 实现日期格式验证
                    break;
                case "JSON":
                    // TODO: 实现JSON格式验证
                    break;
            }
            return true;
        } catch (Exception e) {
            log.warn("属性值验证失败: {}, 值: {}, 错误: {}", metaAttribute.getName(), value, e.getMessage());
            return false;
        }
    }
} 