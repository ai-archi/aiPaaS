package com.aixone.metacenter.metamanagement.application;

import com.aixone.metacenter.metamanagement.application.dto.MetaAttributeDTO;
import com.aixone.metacenter.metamanagement.application.dto.MetaExtensionDTO;
import com.aixone.metacenter.metamanagement.application.dto.MetaObjectDTO;
import com.aixone.metacenter.metamanagement.application.dto.MetaRelationDTO;
import com.aixone.metacenter.metamanagement.application.dto.MetaRuleDTO;
import com.aixone.metacenter.metamanagement.domain.MetaAttribute;
import com.aixone.metacenter.metamanagement.domain.MetaExtension;
import com.aixone.metacenter.metamanagement.domain.MetaObject;
import com.aixone.metacenter.metamanagement.domain.MetaRelation;
import com.aixone.metacenter.metamanagement.domain.MetaRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * 元数据对象Mapper接口
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Mapper(componentModel = "spring")
public interface MetaObjectMapper {

    /**
     * 实体转DTO
     * 
     * @param entity 实体
     * @return DTO
     */
    MetaObjectDTO toDTO(MetaObject entity);

    /**
     * DTO转实体
     * 
     * @param dto DTO
     * @return 实体
     */
    MetaObject toEntity(MetaObjectDTO dto);

    /**
     * 更新实体
     * 
     * @param dto DTO
     * @param entity 实体
     */
    void updateEntityFromDTO(MetaObjectDTO dto, @MappingTarget MetaObject entity);

    /**
     * 属性实体转DTO
     * 
     * @param entity 属性实体
     * @return 属性DTO
     */
    MetaAttributeDTO toDTO(MetaAttribute entity);

    /**
     * 属性DTO转实体
     * 
     * @param dto 属性DTO
     * @return 属性实体
     */
    @Mapping(target = "metaObject", ignore = true)
    MetaAttribute toEntity(MetaAttributeDTO dto);

    /**
     * 关系实体转DTO
     * 
     * @param entity 关系实体
     * @return 关系DTO
     */
    @Mapping(target = "sourceMetaObjectId", source = "sourceObject.id")
    @Mapping(target = "targetMetaObjectId", source = "targetObject.id")
    @Mapping(target = "sourceObjectName", source = "sourceObject.name")
    @Mapping(target = "targetObjectName", source = "targetObject.name")
    MetaRelationDTO toRelationDTO(MetaRelation entity);

    /**
     * 规则实体转DTO
     * 
     * @param entity 规则实体
     * @return 规则DTO
     */
    @Mapping(target = "metaObjectId", source = "metaObject.id")
    MetaRuleDTO toDTO(MetaRule entity);

    /**
     * 规则DTO转实体
     * 
     * @param dto 规则DTO
     * @return 规则实体
     */
    @Mapping(target = "metaObject", ignore = true)
    MetaRule toEntity(MetaRuleDTO dto);

    /**
     * 扩展点实体转DTO
     * 
     * @param entity 扩展点实体
     * @return 扩展点DTO
     */
    @Mapping(target = "metaObjectId", source = "metaObject.id")
    MetaExtensionDTO toDTO(MetaExtension entity);

    /**
     * 扩展点DTO转实体
     * 
     * @param dto 扩展点DTO
     * @return 扩展点实体
     */
    @Mapping(target = "metaObject", ignore = true)
    MetaExtension toEntity(MetaExtensionDTO dto);

    /**
     * 属性实体列表转DTO列表
     * 
     * @param entities 属性实体列表
     * @return 属性DTO列表
     */
    List<MetaAttributeDTO> toDTOList(List<MetaAttribute> entities);

    /**
     * 关系实体列表转DTO列表
     * 
     * @param entities 关系实体列表
     * @return 关系DTO列表
     */
    List<MetaRelationDTO> toRelationDTOList(List<MetaRelation> entities);

    /**
     * 规则实体列表转DTO列表
     * 
     * @param entities 规则实体列表
     * @return 规则DTO列表
     */
    List<MetaRuleDTO> toRuleDTOList(List<MetaRule> entities);

    /**
     * 扩展点实体列表转DTO列表
     * 
     * @param entities 扩展点实体列表
     * @return 扩展点DTO列表
     */
    List<MetaExtensionDTO> toExtensionDTOList(List<MetaExtension> entities);
} 