package com.aixone.metacenter.metamanagement.application;

import com.aixone.metacenter.metamanagement.application.dto.MetaObjectDTO;
import com.aixone.metacenter.metamanagement.domain.MetaObject;
import com.aixone.metacenter.metamanagement.domain.MetaObjectRepository;
import com.aixone.metacenter.metamanagement.domain.service.MetaObjectDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 元数据对象应用服务测试
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@ExtendWith(MockitoExtension.class)
class MetaObjectApplicationServiceTest {

    @Mock
    private MetaObjectRepository metaObjectRepository;

    @Mock
    private MetaObjectDomainService metaObjectDomainService;

    @Mock
    private MetaObjectMapper metaObjectMapper;

    @InjectMocks
    private MetaObjectApplicationService metaObjectApplicationService;

    private MetaObjectDTO testMetaObjectDTO;
    private MetaObject testMetaObject;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testMetaObjectDTO = new MetaObjectDTO();
        testMetaObjectDTO.setName("测试元数据对象");
        testMetaObjectDTO.setType("business");
        testMetaObjectDTO.setObjectType("entity");
        testMetaObjectDTO.setTenantId("test-tenant");
        testMetaObjectDTO.setDescription("这是一个测试元数据对象");

        testMetaObject = new MetaObject();
        testMetaObject.setId(1L);
        testMetaObject.setName("测试元数据对象");
        testMetaObject.setType("business");
        testMetaObject.setObjectType("entity");
        testMetaObject.setTenantId("test-tenant");
        testMetaObject.setDescription("这是一个测试元数据对象");
    }

    @Test
    void testGetMetaObjectById_Success() {
        // 准备
        when(metaObjectRepository.findById(1L)).thenReturn(Optional.of(testMetaObject));
        when(metaObjectMapper.toDTO(testMetaObject)).thenReturn(testMetaObjectDTO);

        // 执行
        MetaObjectDTO result = metaObjectApplicationService.getMetaObjectById(1L);

        // 验证
        assertNotNull(result);
        assertEquals("测试元数据对象", result.getName());
        assertEquals("business", result.getType());
        verify(metaObjectRepository).findById(1L);
        verify(metaObjectMapper).toDTO(testMetaObject);
    }

    @Test
    void testExistsByName_Success() {
        // 准备
        when(metaObjectRepository.existsByTenantIdAndName("test-tenant", "测试元数据对象")).thenReturn(true);

        // 执行
        boolean result = metaObjectApplicationService.existsByName("test-tenant", "测试元数据对象");

        // 验证
        assertTrue(result);
        verify(metaObjectRepository).existsByTenantIdAndName("test-tenant", "测试元数据对象");
    }

    @Test
    void testCountByTenantId_Success() {
        // 准备
        when(metaObjectRepository.countByTenantId("test-tenant")).thenReturn(5L);

        // 执行
        long result = metaObjectApplicationService.countByTenantId("test-tenant");

        // 验证
        assertEquals(5L, result);
        verify(metaObjectRepository).countByTenantId("test-tenant");
    }

    @Test
    void testValidateMetaObject_Success() {
        // 准备
        when(metaObjectDomainService.validateMetaObject(testMetaObjectDTO)).thenReturn(true);

        // 执行
        boolean result = metaObjectApplicationService.validateMetaObject(testMetaObjectDTO);

        // 验证
        assertTrue(result);
        verify(metaObjectDomainService).validateMetaObject(testMetaObjectDTO);
    }
} 