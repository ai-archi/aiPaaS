package com.aixone.directory.user.domain.aggregate;

import com.aixone.directory.test.AbstractExcelDrivenTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.UUID;

public class UserCreateTest extends AbstractExcelDrivenTest {
    private static Method createUserMethod;
    private static PasswordEncoder passwordEncoder;

    @BeforeAll
    static void setUp() throws NoSuchMethodException {
        // 加载用户用例数据
        loadTestCases("usecases/user_cases.xlsx");
        // 获取要测试的方法
        createUserMethod = User.class.getMethod("createUser", 
            UUID.class, String.class, String.class, String.class, PasswordEncoder.class);
        // 初始化静态参数
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded_password");
    }

    @Test
    void testCreateUser() {
        // 测试所有用例
        execute(new String[]{"正常创建用户","邮箱为空异常","密码为空异常","邮箱格式错误","密码长度不足"}, createUserMethod);
    }

    @Override
    protected Object getMethodParamValue(String paramName, java.util.Map<String, String> caseData) {
        // 兼容参数名为索引（如0、1、2、3、4）和真实参数名
        switch (paramName) {
            case "tenantId":
            case "0": // 租户ID
                String tenantIdStr = caseData.get("tenantId");
                return tenantIdStr != null && !tenantIdStr.isEmpty() ? 
                    UUID.fromString(tenantIdStr) : null;
            case "email":
            case "1": // 邮箱
                return caseData.get("email");
            case "plainPassword":
            case "2": // 明文密码
                return caseData.get("password");
            case "username":
            case "3": // 用户名
                return caseData.get("username");
            case "passwordEncoder":
            case "4": // 密码编码器
                return passwordEncoder;
            default:
                throw new IllegalArgumentException("Unknown parameter: " + paramName);
        }
    }
} 