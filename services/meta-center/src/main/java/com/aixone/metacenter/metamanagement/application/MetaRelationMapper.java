package com.aixone.metacenter.metamanagement.application;

import com.aixone.metacenter.metamanagement.application.dto.MetaRelationDTO;
import com.aixone.metacenter.metamanagement.domain.MetaRelation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 元数据关系映射器
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Mapper(componentModel = "spring")
public interface MetaRelationMapper {

    /**
     * 将元数据关系实体转换为DTO
     * 
     * @param metaRelation 元数据关系实体
     * @return 元数据关系DTO
     */
    @Mapping(target = "sourceMetaObjectId", source = "sourceObject.id")
    @Mapping(target = "targetMetaObjectId", source = "targetObject.id")
    @Mapping(target = "sourceObjectName", source = "sourceObject.name")
    @Mapping(target = "targetObjectName", source = "targetObject.name")
    MetaRelationDTO toDTO(MetaRelation metaRelation);

    /**
     * 将DTO转换为元数据关系实体
     * 
     * @param metaRelationDTO 元数据关系DTO
     * @return 元数据关系实体
     */
    @Mapping(target = "sourceObject", ignore = true)
    @Mapping(target = "targetObject", ignore = true)
    MetaRelation toEntity(MetaRelationDTO metaRelationDTO);
} 