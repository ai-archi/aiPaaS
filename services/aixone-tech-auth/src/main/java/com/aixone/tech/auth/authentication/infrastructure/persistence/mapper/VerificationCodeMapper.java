package com.aixone.tech.auth.authentication.infrastructure.persistence.mapper;

import com.aixone.tech.auth.authentication.domain.model.VerificationCode;
import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.VerificationCodeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * VerificationCode 映射器
 */
@Mapper(componentModel = "spring")
public interface VerificationCodeMapper {
    
    @Mapping(source = "codeId", target = "id")
    VerificationCodeEntity toEntity(VerificationCode verificationCode);
    
    @Mapping(source = "id", target = "codeId")
    VerificationCode toDomain(VerificationCodeEntity entity);
}
