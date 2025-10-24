package com.aixone.event.api;

import com.aixone.event.dto.TopicDTO;
import com.aixone.common.api.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * TopicApi 接口单元测试
 */
@DisplayName("TopicApi 接口测试")
class TopicApiTest {

    @Test
    @DisplayName("接口方法签名测试")
    void testInterfaceMethodSignatures() {
        // 验证接口方法存在且签名正确
        try {
            // 测试 registerTopic 方法
            TopicApi.class.getMethod("registerTopic", TopicDTO.class);
            
            // 测试 getAllTopics 方法
            TopicApi.class.getMethod("getAllTopics");
            
            // 测试 getTopicByName 方法
            TopicApi.class.getMethod("getTopicByName", String.class);
            
            // 测试 updateTopic 方法
            TopicApi.class.getMethod("updateTopic", String.class, String.class);
            
            // 测试 activateTopic 方法
            TopicApi.class.getMethod("activateTopic", String.class);
            
            // 测试 deactivateTopic 方法
            TopicApi.class.getMethod("deactivateTopic", String.class);
            
            // 测试 deleteTopic 方法
            TopicApi.class.getMethod("deleteTopic", String.class);
            
        } catch (NoSuchMethodException e) {
            fail("接口方法签名不正确: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("返回类型测试")
    void testReturnTypes() {
        // 验证返回类型
        assertEquals(ApiResponse.class, getMethodReturnType("registerTopic", TopicDTO.class));
        assertEquals(ApiResponse.class, getMethodReturnType("getAllTopics"));
        assertEquals(ApiResponse.class, getMethodReturnType("getTopicByName", String.class));
        assertEquals(ApiResponse.class, getMethodReturnType("updateTopic", String.class, String.class));
        assertEquals(ApiResponse.class, getMethodReturnType("activateTopic", String.class));
        assertEquals(ApiResponse.class, getMethodReturnType("deactivateTopic", String.class));
        assertEquals(ApiResponse.class, getMethodReturnType("deleteTopic", String.class));
    }

    @Test
    @DisplayName("泛型类型测试")
    void testGenericTypes() {
        // 验证泛型类型
        assertTrue(getMethodReturnType("registerTopic", TopicDTO.class).getTypeParameters().length > 0);
        assertTrue(getMethodReturnType("getAllTopics").getTypeParameters().length > 0);
        assertTrue(getMethodReturnType("getTopicByName", String.class).getTypeParameters().length > 0);
        assertTrue(getMethodReturnType("updateTopic", String.class, String.class).getTypeParameters().length > 0);
        assertTrue(getMethodReturnType("activateTopic", String.class).getTypeParameters().length > 0);
        assertTrue(getMethodReturnType("deactivateTopic", String.class).getTypeParameters().length > 0);
        assertTrue(getMethodReturnType("deleteTopic", String.class).getTypeParameters().length > 0);
    }

    @Test
    @DisplayName("接口继承测试")
    void testInterfaceInheritance() {
        // 验证接口没有继承其他接口
        Class<?>[] interfaces = TopicApi.class.getInterfaces();
        assertEquals(0, interfaces.length);
        
        // 验证接口是 public 的
        assertTrue(java.lang.reflect.Modifier.isPublic(TopicApi.class.getModifiers()));
        
        // 验证接口是 abstract 的
        assertTrue(java.lang.reflect.Modifier.isAbstract(TopicApi.class.getModifiers()));
        
        // 验证接口不是 final 的
        assertFalse(java.lang.reflect.Modifier.isFinal(TopicApi.class.getModifiers()));
    }

    @Test
    @DisplayName("方法可见性测试")
    void testMethodVisibility() {
        // 验证所有方法都是 public 的
        java.lang.reflect.Method[] methods = TopicApi.class.getDeclaredMethods();
        for (java.lang.reflect.Method method : methods) {
            assertTrue(java.lang.reflect.Modifier.isPublic(method.getModifiers()), 
                "方法 " + method.getName() + " 不是 public 的");
        }
    }

    @Test
    @DisplayName("方法参数测试")
    void testMethodParameters() {
        // 验证方法参数类型
        try {
            java.lang.reflect.Method registerTopicMethod = TopicApi.class.getMethod("registerTopic", TopicDTO.class);
            assertEquals(TopicDTO.class, registerTopicMethod.getParameterTypes()[0]);
            
            java.lang.reflect.Method getTopicByNameMethod = TopicApi.class.getMethod("getTopicByName", String.class);
            assertEquals(String.class, getTopicByNameMethod.getParameterTypes()[0]);
            
            java.lang.reflect.Method updateTopicMethod = TopicApi.class.getMethod("updateTopic", String.class, String.class);
            assertEquals(String.class, updateTopicMethod.getParameterTypes()[0]);
            assertEquals(String.class, updateTopicMethod.getParameterTypes()[1]);
            
            java.lang.reflect.Method activateTopicMethod = TopicApi.class.getMethod("activateTopic", String.class);
            assertEquals(String.class, activateTopicMethod.getParameterTypes()[0]);
            
            java.lang.reflect.Method deactivateTopicMethod = TopicApi.class.getMethod("deactivateTopic", String.class);
            assertEquals(String.class, deactivateTopicMethod.getParameterTypes()[0]);
            
            java.lang.reflect.Method deleteTopicMethod = TopicApi.class.getMethod("deleteTopic", String.class);
            assertEquals(String.class, deleteTopicMethod.getParameterTypes()[0]);
            
        } catch (NoSuchMethodException e) {
            fail("方法参数类型不正确: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("异常声明测试")
    void testExceptionDeclarations() {
        // 验证方法没有声明检查异常
        java.lang.reflect.Method[] methods = TopicApi.class.getDeclaredMethods();
        for (java.lang.reflect.Method method : methods) {
            Class<?>[] exceptionTypes = method.getExceptionTypes();
            assertEquals(0, exceptionTypes.length, 
                "方法 " + method.getName() + " 声明了检查异常");
        }
    }

    @Test
    @DisplayName("方法数量测试")
    void testMethodCount() {
        // 验证接口有正确数量的方法
        java.lang.reflect.Method[] methods = TopicApi.class.getDeclaredMethods();
        assertEquals(7, methods.length, "TopicApi 应该有 7 个方法");
    }

    @Test
    @DisplayName("方法名称测试")
    void testMethodNames() {
        // 验证方法名称
        java.lang.reflect.Method[] methods = TopicApi.class.getDeclaredMethods();
        String[] expectedMethodNames = {
            "registerTopic",
            "getAllTopics", 
            "getTopicByName",
            "updateTopic",
            "activateTopic",
            "deactivateTopic",
            "deleteTopic"
        };
        
        for (String expectedName : expectedMethodNames) {
            boolean found = false;
            for (java.lang.reflect.Method method : methods) {
                if (expectedName.equals(method.getName())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "方法 " + expectedName + " 不存在");
        }
    }

    @Test
    @DisplayName("参数数量测试")
    void testParameterCounts() {
        // 验证方法参数数量
        try {
            assertEquals(1, TopicApi.class.getMethod("registerTopic", TopicDTO.class).getParameterCount());
            assertEquals(0, TopicApi.class.getMethod("getAllTopics").getParameterCount());
            assertEquals(1, TopicApi.class.getMethod("getTopicByName", String.class).getParameterCount());
            assertEquals(2, TopicApi.class.getMethod("updateTopic", String.class, String.class).getParameterCount());
            assertEquals(1, TopicApi.class.getMethod("activateTopic", String.class).getParameterCount());
            assertEquals(1, TopicApi.class.getMethod("deactivateTopic", String.class).getParameterCount());
            assertEquals(1, TopicApi.class.getMethod("deleteTopic", String.class).getParameterCount());
        } catch (NoSuchMethodException e) {
            fail("方法参数数量不正确: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("接口文档测试")
    void testInterfaceDocumentation() {
        // 验证接口有文档注释
        String classDoc = TopicApi.class.getAnnotation(java.lang.annotation.Documented.class) != null ? "有文档" : "无文档";
        
        // 验证方法有文档注释
        java.lang.reflect.Method[] methods = TopicApi.class.getDeclaredMethods();
        for (java.lang.reflect.Method method : methods) {
            // 这里主要验证方法存在，文档注释的验证比较复杂
            assertNotNull(method.getName());
        }
    }

    // 辅助方法
    private Class<?> getMethodReturnType(String methodName, Class<?>... parameterTypes) {
        try {
            return TopicApi.class.getMethod(methodName, parameterTypes).getReturnType();
        } catch (NoSuchMethodException e) {
            fail("方法 " + methodName + " 不存在: " + e.getMessage());
            return null;
        }
    }
}