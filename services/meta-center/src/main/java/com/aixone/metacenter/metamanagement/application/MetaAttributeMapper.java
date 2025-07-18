package com.aixone.metacenter.metamanagement.application;

import com.aixone.metacenter.metamanagement.application.dto.MetaAttributeDTO;
import com.aixone.metacenter.metamanagement.domain.MetaAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * 元数据属性映射器
 * 负责MetaAttribute实体和DTO之间的转换
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MetaAttributeMapper {

    /**
     * 将DTO转换为实体
     *
     * @param dto 元数据属性DTO
     * @return 元数据属性实体
     */
    @Mapping(target = "metaObject", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "visible", ignore = true)
    MetaAttribute toEntity(MetaAttributeDTO dto);

    /**
     * 将实体转换为DTO
     *
     * @param entity 元数据属性实体
     * @return 元数据属性DTO
     */
    MetaAttributeDTO toDTO(MetaAttribute entity);

    /**
     * 更新实体（忽略null值）
     *
     * @param dto 元数据属性DTO
     * @param entity 要更新的实体
     */
    @Mapping(target = "metaObject", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "visible", ignore = true)
    void updateEntityFromDTO(MetaAttributeDTO dto, @MappingTarget MetaAttribute entity);
} 