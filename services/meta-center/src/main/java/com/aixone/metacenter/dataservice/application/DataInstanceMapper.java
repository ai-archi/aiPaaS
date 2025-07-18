package com.aixone.metacenter.dataservice.application;

import com.aixone.metacenter.dataservice.application.dto.DataInstanceDTO;
import com.aixone.metacenter.dataservice.domain.DataInstance;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * 数据实例映射器
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Mapper(componentModel = "spring")
public interface DataInstanceMapper {

    /**
     * 将数据实例实体转换为DTO
     * 
     * @param dataInstance 数据实例实体
     * @return 数据实例DTO
     */
    DataInstanceDTO toDTO(DataInstance dataInstance);

    /**
     * 将DTO转换为数据实例实体
     * 
     * @param dataInstanceDTO 数据实例DTO
     * @return 数据实例实体
     */
    DataInstance toEntity(DataInstanceDTO dataInstanceDTO);

    /**
     * 从DTO更新实体
     * 
     * @param dataInstanceDTO 数据实例DTO
     * @param dataInstance 数据实例实体
     */
    void updateEntityFromDTO(DataInstanceDTO dataInstanceDTO, @MappingTarget DataInstance dataInstance);
} 