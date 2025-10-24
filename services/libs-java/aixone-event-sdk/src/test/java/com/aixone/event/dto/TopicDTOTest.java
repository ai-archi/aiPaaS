package com.aixone.event.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

/**
 * TopicDTO 单元测试
 */
@DisplayName("TopicDTO 测试")
class TopicDTOTest {

    @Test
    @DisplayName("默认构造函数测试")
    void testDefaultConstructor() {
        TopicDTO topic = new TopicDTO();
        
        assertNull(topic.getTopicId());
        assertNull(topic.getName());
        assertNull(topic.getOwner());
        assertNull(topic.getDescription());
        assertNull(topic.getStatus());
        assertNull(topic.getTenantId());
        assertNull(topic.getCreateTime());
        assertNull(topic.getUpdateTime());
        assertEquals(Integer.valueOf(1), topic.getPartitionCount());
        assertEquals(Short.valueOf((short) 1), topic.getReplicationFactor());
    }

    @Test
    @DisplayName("业务构造函数测试")
    void testBusinessConstructor() {
        String name = "user-events";
        String owner = "auth-service";
        String description = "用户相关事件";
        String tenantId = "tenant-001";
        
        TopicDTO topic = new TopicDTO(name, owner, description, tenantId);
        
        assertNull(topic.getTopicId());
        assertEquals(name, topic.getName());
        assertEquals(owner, topic.getOwner());
        assertEquals(description, topic.getDescription());
        assertEquals("ACTIVE", topic.getStatus());
        assertEquals(tenantId, topic.getTenantId());
        assertNotNull(topic.getCreateTime());
        assertNotNull(topic.getUpdateTime());
        assertEquals(Integer.valueOf(1), topic.getPartitionCount());
        assertEquals(Short.valueOf((short) 1), topic.getReplicationFactor());
    }

    @Test
    @DisplayName("Getters 和 Setters 测试")
    void testGettersAndSetters() {
        TopicDTO topic = new TopicDTO();
        
        // 测试 topicId
        Long topicId = 123L;
        topic.setTopicId(topicId);
        assertEquals(topicId, topic.getTopicId());
        
        // 测试 name
        String name = "order-events";
        topic.setName(name);
        assertEquals(name, topic.getName());
        
        // 测试 owner
        String owner = "order-service";
        topic.setOwner(owner);
        assertEquals(owner, topic.getOwner());
        
        // 测试 description
        String description = "订单相关事件";
        topic.setDescription(description);
        assertEquals(description, topic.getDescription());
        
        // 测试 status
        String status = "INACTIVE";
        topic.setStatus(status);
        assertEquals(status, topic.getStatus());
        
        // 测试 tenantId
        String tenantId = "tenant-002";
        topic.setTenantId(tenantId);
        assertEquals(tenantId, topic.getTenantId());
        
        // 测试 createTime
        Instant createTime = Instant.now();
        topic.setCreateTime(createTime);
        assertEquals(createTime, topic.getCreateTime());
        
        // 测试 updateTime
        Instant updateTime = Instant.now();
        topic.setUpdateTime(updateTime);
        assertEquals(updateTime, topic.getUpdateTime());
        
        // 测试 partitionCount
        Integer partitionCount = 3;
        topic.setPartitionCount(partitionCount);
        assertEquals(partitionCount, topic.getPartitionCount());
        
        // 测试 replicationFactor
        Short replicationFactor = 2;
        topic.setReplicationFactor(replicationFactor);
        assertEquals(replicationFactor, topic.getReplicationFactor());
    }

    @Test
    @DisplayName("toString 方法测试")
    void testToString() {
        TopicDTO topic = new TopicDTO();
        topic.setTopicId(123L);
        topic.setName("user-events");
        topic.setOwner("auth-service");
        topic.setStatus("ACTIVE");
        topic.setTenantId("tenant-001");
        topic.setCreateTime(Instant.parse("2024-01-01T10:00:00Z"));
        
        String result = topic.toString();
        
        assertTrue(result.contains("topicId=123"));
        assertTrue(result.contains("name='user-events'"));
        assertTrue(result.contains("owner='auth-service'"));
        assertTrue(result.contains("status='ACTIVE'"));
        assertTrue(result.contains("tenantId='tenant-001'"));
    }

    @Test
    @DisplayName("序列化测试")
    void testSerialization() {
        TopicDTO original = new TopicDTO("user-events", "auth-service", "用户相关事件", "tenant-001");
        original.setTopicId(123L);
        
        // 验证实现了 Serializable 接口
        assertTrue(original instanceof java.io.Serializable);
        
        // 验证 serialVersionUID
        try {
            java.lang.reflect.Field field = TopicDTO.class.getDeclaredField("serialVersionUID");
            field.setAccessible(true);
            long serialVersionUID = field.getLong(null);
            assertEquals(1L, serialVersionUID);
        } catch (Exception e) {
            fail("serialVersionUID 字段不存在或访问失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("边界值测试")
    void testBoundaryValues() {
        TopicDTO topic = new TopicDTO();
        
        // 测试空字符串
        topic.setName("");
        assertEquals("", topic.getName());
        
        topic.setOwner("");
        assertEquals("", topic.getOwner());
        
        topic.setDescription("");
        assertEquals("", topic.getDescription());
        
        // 测试 null 值
        topic.setName(null);
        assertNull(topic.getName());
        
        topic.setOwner(null);
        assertNull(topic.getOwner());
        
        topic.setDescription(null);
        assertNull(topic.getDescription());
        
        // 测试长字符串
        String longString = "a".repeat(1000);
        topic.setDescription(longString);
        assertEquals(longString, topic.getDescription());
    }

    @Test
    @DisplayName("时间戳测试")
    void testTimestamps() {
        TopicDTO topic = new TopicDTO("user-events", "auth-service", "用户相关事件", "tenant-001");
        
        // 验证创建时间在合理范围内
        Instant now = Instant.now();
        Instant createTime = topic.getCreateTime();
        Instant updateTime = topic.getUpdateTime();
        
        assertTrue(createTime.isAfter(now.minusSeconds(1)));
        assertTrue(createTime.isBefore(now.plusSeconds(1)));
        
        assertTrue(updateTime.isAfter(now.minusSeconds(1)));
        assertTrue(updateTime.isBefore(now.plusSeconds(1)));
        
        // 创建时间和更新时间应该相同（在构造函数中设置）
        assertEquals(createTime, updateTime);
    }

    @Test
    @DisplayName("状态常量测试")
    void testStatusConstants() {
        TopicDTO topic = new TopicDTO("user-events", "auth-service", "用户相关事件", "tenant-001");
        
        // 验证默认状态
        assertEquals("ACTIVE", topic.getStatus());
        
        // 测试状态变更
        topic.setStatus("INACTIVE");
        assertEquals("INACTIVE", topic.getStatus());
        
        topic.setStatus("PENDING");
        assertEquals("PENDING", topic.getStatus());
    }

    @Test
    @DisplayName("分区和副本配置测试")
    void testPartitionAndReplicationConfig() {
        TopicDTO topic = new TopicDTO();
        
        // 测试默认值
        assertEquals(Integer.valueOf(1), topic.getPartitionCount());
        assertEquals(Short.valueOf((short) 1), topic.getReplicationFactor());
        
        // 测试设置值
        topic.setPartitionCount(5);
        assertEquals(Integer.valueOf(5), topic.getPartitionCount());
        
        topic.setReplicationFactor((short) 3);
        assertEquals(Short.valueOf((short) 3), topic.getReplicationFactor());
        
        // 测试边界值
        topic.setPartitionCount(0);
        assertEquals(Integer.valueOf(0), topic.getPartitionCount());
        
        topic.setReplicationFactor((short) 0);
        assertEquals(Short.valueOf((short) 0), topic.getReplicationFactor());
    }
}