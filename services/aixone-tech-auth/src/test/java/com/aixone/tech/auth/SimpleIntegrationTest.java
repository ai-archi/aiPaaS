package com.aixone.tech.auth;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.constant.CommonConstants;
import com.aixone.common.exception.BizException;
import com.aixone.common.tools.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 简单集成测试 - 验证 aixone-common-sdk 集成是否正常
 * 不使用Spring上下文，避免复杂的依赖问题
 */
public class SimpleIntegrationTest {

    @Test
    public void testCommonSdkIntegration() {
        // 测试常量类
        assertNotNull(CommonConstants.EMPTY_STRING);
        assertEquals("", CommonConstants.EMPTY_STRING);
        assertEquals(20, CommonConstants.DEFAULT_PAGE_SIZE);
        
        // 测试工具类
        assertTrue(StringUtils.isBlank(null));
        assertTrue(StringUtils.isBlank(""));
        assertTrue(StringUtils.isBlank("   "));
        assertFalse(StringUtils.isBlank("hello"));
        
        // 测试异常类
        BizException exception = new BizException("TEST_ERROR", "测试异常");
        assertEquals("测试异常", exception.getMessage());
        
        // 测试API响应类
        ApiResponse<String> response = ApiResponse.success("测试数据");
        assertEquals(Integer.valueOf(200), response.getCode());
        assertEquals("测试数据", response.getData());
        
        System.out.println("✅ aixone-common-sdk 集成测试通过！");
        System.out.println("✅ 常量类: " + CommonConstants.EMPTY_STRING);
        System.out.println("✅ 工具类: StringUtils.isBlank('hello') = " + StringUtils.isBlank("hello"));
        System.out.println("✅ 异常类: " + exception.getMessage());
        System.out.println("✅ API响应: " + response.getData() + " (code: " + response.getCode() + ")");
    }
}
