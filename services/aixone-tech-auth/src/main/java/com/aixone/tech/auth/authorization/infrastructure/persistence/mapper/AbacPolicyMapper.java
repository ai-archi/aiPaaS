package com.aixone.tech.auth.authorization.infrastructure.persistence.mapper;

import com.aixone.tech.auth.authorization.domain.model.AbacPolicy;
import com.aixone.tech.auth.authorization.infrastructure.persistence.entity.AbacPolicyEntity;
import org.mapstruct.Mapper;

import java.util.Map;

/**
 * AbacPolicy 映射器
 */
@Mapper(componentModel = "spring")
public interface AbacPolicyMapper {
    
    AbacPolicyEntity toEntity(AbacPolicy policy);
    
    AbacPolicy toDomain(AbacPolicyEntity entity);
    
    default Map<String, String> convertObjectMapToStringMap(Map<String, Object> objectMap) {
        if (objectMap == null) {
            return null;
        }
        return objectMap.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() != null ? entry.getValue().toString() : null
                ));
    }
    
    default Map<String, Object> convertStringMapToObjectMap(Map<String, String> stringMap) {
        if (stringMap == null) {
            return null;
        }
        return stringMap.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }
}
