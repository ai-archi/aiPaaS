package com.aixone.metacenter.metamanagement.application;

import com.aixone.metacenter.metamanagement.application.dto.MetaAttributeDTO;
import com.aixone.metacenter.metamanagement.domain.MetaAttribute;
import com.aixone.metacenter.metamanagement.domain.MetaAttributeRepository;
import com.aixone.metacenter.metamanagement.domain.MetaObject;
import com.aixone.metacenter.metamanagement.domain.MetaObjectRepository;
import com.aixone.metacenter.metamanagement.domain.service.MetaAttributeDomainService;
import com.aixone.metacenter.common.exception.MetaNotFoundException;
import com.aixone.metacenter.common.exception.MetaValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 元数据属性应用服务测试
 */
@ExtendWith(MockitoExtension.class)
class MetaAttributeApplicationServiceTest {

    @Mock
    private MetaAttributeRepository metaAttributeRepository;

    @Mock
    private MetaObjectRepository metaObjectRepository;

    @Mock
    private MetaAttributeDomainService metaAttributeDomainService;

    @Mock
    private MetaAttributeMapper metaAttributeMapper;

    @InjectMocks
    private MetaAttributeApplicationService metaAttributeApplicationService;

    private MetaObject testMetaObject;
    private MetaAttribute testMetaAttribute;
    private MetaAttributeDTO testMetaAttributeDTO;

    @BeforeEach
    void setUp() {
        // 创建测试元数据对象
        testMetaObject = new MetaObject();
        testMetaObject.setId(1L);
        testMetaObject.setName("TestObject");

        // 创建测试元数据属性
        testMetaAttribute = new MetaAttribute();
        testMetaAttribute.setId(1L);
        testMetaAttribute.setName("testAttribute");
        testMetaAttribute.setDisplayName("测试属性");
        testMetaAttribute.setDataType("STRING");
        testMetaAttribute.setLength(100);
        testMetaAttribute.setRequired(false);
        testMetaAttribute.setMetaObject(testMetaObject);
        testMetaAttribute.setCreatedTime(LocalDateTime.now());
        testMetaAttribute.setUpdatedTime(LocalDateTime.now());

        // 创建测试DTO
        testMetaAttributeDTO = new MetaAttributeDTO();
        testMetaAttributeDTO.setName("testAttribute");
        testMetaAttributeDTO.setDisplayName("测试属性");
        testMetaAttributeDTO.setDataType("STRING");
        testMetaAttributeDTO.setLength(100);
        testMetaAttributeDTO.setRequired(false);
        testMetaAttributeDTO.setMetaObjectId(1L);
    }

    @Test
    void testCreateMetaAttribute_Success() {
        // 准备测试数据
        when(metaObjectRepository.findById(1L)).thenReturn(Optional.of(testMetaObject));
        when(metaAttributeRepository.existsByNameAndMetaObjectId("testAttribute", 1L)).thenReturn(false);
        when(metaAttributeMapper.toEntity(testMetaAttributeDTO)).thenReturn(testMetaAttribute);
        when(metaAttributeRepository.save(any(MetaAttribute.class))).thenReturn(testMetaAttribute);
        when(metaAttributeMapper.toDTO(testMetaAttribute)).thenReturn(testMetaAttributeDTO);

        // 执行测试
        MetaAttributeDTO result = metaAttributeApplicationService.createMetaAttribute(testMetaAttributeDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals("testAttribute", result.getName());
        verify(metaAttributeDomainService).validateMetaAttribute(any(MetaAttribute.class));
        verify(metaAttributeRepository).save(any(MetaAttribute.class));
    }

    @Test
    void testCreateMetaAttribute_MetaObjectNotFound() {
        // 准备测试数据
        when(metaObjectRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        MetaNotFoundException exception = assertThrows(MetaNotFoundException.class, () -> {
            metaAttributeApplicationService.createMetaAttribute(testMetaAttributeDTO);
        });

        assertEquals("元数据对象不存在: 1", exception.getMessage());
        verify(metaAttributeRepository, never()).save(any());
    }

    @Test
    void testCreateMetaAttribute_NameAlreadyExists() {
        // 准备测试数据
        when(metaObjectRepository.findById(1L)).thenReturn(Optional.of(testMetaObject));
        when(metaAttributeRepository.existsByNameAndMetaObjectId("testAttribute", 1L)).thenReturn(true);

        // 执行测试并验证异常
        MetaValidationException exception = assertThrows(MetaValidationException.class, () -> {
            metaAttributeApplicationService.createMetaAttribute(testMetaAttributeDTO);
        });

        assertEquals("属性名称已存在: testAttribute", exception.getMessage());
        verify(metaAttributeRepository, never()).save(any());
    }

    @Test
    void testUpdateMetaAttribute_Success() {
        // 准备测试数据
        when(metaAttributeRepository.findById(1L)).thenReturn(Optional.of(testMetaAttribute));
        when(metaAttributeRepository.existsByNameAndMetaObjectId("updatedAttribute", 1L)).thenReturn(false);
        when(metaAttributeRepository.save(any(MetaAttribute.class))).thenReturn(testMetaAttribute);
        when(metaAttributeMapper.toDTO(testMetaAttribute)).thenReturn(testMetaAttributeDTO);

        // 修改DTO
        testMetaAttributeDTO.setName("updatedAttribute");
        testMetaAttributeDTO.setDisplayName("更新后的属性");

        // 执行测试
        MetaAttributeDTO result = metaAttributeApplicationService.updateMetaAttribute(1L, testMetaAttributeDTO);

        // 验证结果
        assertNotNull(result);
        verify(metaAttributeDomainService).validateMetaAttribute(any(MetaAttribute.class));
        verify(metaAttributeRepository).save(any(MetaAttribute.class));
    }

    @Test
    void testUpdateMetaAttribute_NotFound() {
        // 准备测试数据
        when(metaAttributeRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        MetaNotFoundException exception = assertThrows(MetaNotFoundException.class, () -> {
            metaAttributeApplicationService.updateMetaAttribute(1L, testMetaAttributeDTO);
        });

        assertEquals("元数据属性不存在: 1", exception.getMessage());
        verify(metaAttributeRepository, never()).save(any());
    }

    @Test
    void testDeleteMetaAttribute_Success() {
        // 准备测试数据
        when(metaAttributeRepository.findById(1L)).thenReturn(Optional.of(testMetaAttribute));

        // 执行测试
        metaAttributeApplicationService.deleteMetaAttribute(1L);

        // 验证结果
        verify(metaAttributeDomainService).validateDeleteMetaAttribute(testMetaAttribute);
        verify(metaAttributeRepository).delete(testMetaAttribute);
    }

    @Test
    void testDeleteMetaAttribute_NotFound() {
        // 准备测试数据
        when(metaAttributeRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        MetaNotFoundException exception = assertThrows(MetaNotFoundException.class, () -> {
            metaAttributeApplicationService.deleteMetaAttribute(1L);
        });

        assertEquals("元数据属性不存在: 1", exception.getMessage());
        verify(metaAttributeRepository, never()).delete(any());
    }

    @Test
    void testGetMetaAttributeById_Success() {
        // 准备测试数据
        when(metaAttributeRepository.findById(1L)).thenReturn(Optional.of(testMetaAttribute));
        when(metaAttributeMapper.toDTO(testMetaAttribute)).thenReturn(testMetaAttributeDTO);

        // 执行测试
        MetaAttributeDTO result = metaAttributeApplicationService.getMetaAttributeById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals("testAttribute", result.getName());
    }

    @Test
    void testGetMetaAttributeById_NotFound() {
        // 准备测试数据
        when(metaAttributeRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        MetaNotFoundException exception = assertThrows(MetaNotFoundException.class, () -> {
            metaAttributeApplicationService.getMetaAttributeById(1L);
        });

        assertEquals("元数据属性不存在: 1", exception.getMessage());
    }

    @Test
    void testGetMetaAttributesByMetaObjectId_Success() {
        // 准备测试数据
        List<MetaAttribute> attributes = Arrays.asList(testMetaAttribute);
        when(metaAttributeRepository.findByMetaObjectId(1L)).thenReturn(attributes);
        when(metaAttributeMapper.toDTO(testMetaAttribute)).thenReturn(testMetaAttributeDTO);

        // 执行测试
        List<MetaAttributeDTO> result = metaAttributeApplicationService.getMetaAttributesByMetaObjectId(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testAttribute", result.get(0).getName());
    }

    @Test
    void testGetMetaAttributes_Success() {
        // 准备测试数据
        Pageable pageable = PageRequest.of(0, 10);
        Page<MetaAttribute> attributePage = new PageImpl<>(Arrays.asList(testMetaAttribute));
        when(metaAttributeRepository.findByConditions(any(), eq(pageable))).thenReturn(attributePage);
        when(metaAttributeMapper.toDTO(testMetaAttribute)).thenReturn(testMetaAttributeDTO);

        // 执行测试
        Page<MetaAttributeDTO> result = metaAttributeApplicationService.getMetaAttributes(null, pageable);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("testAttribute", result.getContent().get(0).getName());
    }

    @Test
    void testGetMetaAttributeByName_Success() {
        // 准备测试数据
        when(metaAttributeRepository.findByName("testAttribute")).thenReturn(Optional.of(testMetaAttribute));
        when(metaAttributeMapper.toDTO(testMetaAttribute)).thenReturn(testMetaAttributeDTO);

        // 执行测试
        Optional<MetaAttributeDTO> result = metaAttributeApplicationService.getMetaAttributeByName("testAttribute");

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals("testAttribute", result.get().getName());
    }

    @Test
    void testExistsByName_Success() {
        // 准备测试数据
        when(metaAttributeRepository.existsByNameAndMetaObjectId("testAttribute", 1L)).thenReturn(true);

        // 执行测试
        boolean result = metaAttributeApplicationService.existsByName("testAttribute", 1L);

        // 验证结果
        assertTrue(result);
    }
} 