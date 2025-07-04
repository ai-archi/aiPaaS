package com.aixone.directory.user.domain.aggregate;

import com.aixone.common.test.AbstractExcelDrivenTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class UserTest extends AbstractExcelDrivenTest {

    private static final Logger log = LoggerFactory.getLogger(UserTest.class);

    private static PasswordEncoder passwordEncoder;

    @BeforeAll
    static void setUp() throws NoSuchMethodException {
        // 加载用户用例数据
        loadTestCases("usecases/user_cases.xlsx");
        // 初始化静态参数
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded_password");
    }

    @Test
    void testCreateUser() {
        log.debug("testCreateUser 用例参数: {}", allTestCases.stream().filter(c -> c.get("caseName").equals("正常创建用户")).findFirst().orElse(null));
        execute(new String[]{"正常创建用户","邮箱为空异常","密码为空异常","邮箱格式错误","密码长度不足"}, getMethod(User.class, "createUser", String.class, String.class, String.class, String.class, PasswordEncoder.class));
    }

    @Test
    void testUpdateProfile() {
        execute(new String[]{"正常更新Profile", "Profile字段为空异常"}, getMethod(User.class, "updateProfile", Profile.class));
    }

    @Test
    void testChangePassword() {
        execute(new String[]{"正常修改密码", "密码为空异常", "密码长度不足"}, getMethod(User.class, "changePassword", String.class, PasswordEncoder.class));
    }

    @Test
    void testSuspend() {
        execute(new String[]{"正常挂起用户"}, getMethod(User.class, "suspend"));
    }

    @Test
    void testActivate() {
        execute(new String[]{"正常激活用户"}, getMethod(User.class, "activate"));
    }

    @Test
    void testAssignToGroup() {
        execute(new String[]{"分配分组"}, getMethod(User.class, "assignToGroup", String.class));
    }

    @Test
    void testRemoveFromGroup() {
        execute(new String[]{"移除分组"}, getMethod(User.class, "removeFromGroup", String.class));
    }

    @Test
    void testGrantRole() {
        execute(new String[]{"授予角色"}, getMethod(User.class, "grantRole", String.class));
    }

    @Test
    void testRevokeRole() {
        execute(new String[]{"撤销角色"}, getMethod(User.class, "revokeRole", String.class));
    }

    @Test
    void testChangePasswordWhenSuspendedThrowsException() {
        execute(new String[]{"挂起状态下修改密码抛异常"}, getMethod(User.class, "changePassword", String.class, PasswordEncoder.class));
    }

    @Override
    protected Object getMethodParamValue(String paramName, java.util.Map<String, String> caseData) {
        Object value;
        switch (paramName) {
            case "profile":
            case "newProfile":
                value = Profile.builder()
                    .username(caseData.get("profile_username"))
                    .avatarUrl(caseData.get("profile_avatarUrl"))
                    .bio(caseData.get("profile_bio"))
                    .build();
                break;
            case "passwordEncoder":
                value = passwordEncoder;
                break;
            case "plainPassword":
                value = caseData.get("password");
                break;
            case "groupId":
            case "roleId":
            case "tenantId":
                String v = caseData.get(paramName);
                value = v != null && !v.isEmpty() && !"null".equalsIgnoreCase(v) ? v : "dummy-" + paramName;
                break;
            case "status":
                String statusStr = caseData.get("status");
                value = statusStr != null && !statusStr.isEmpty() ? UserStatus.valueOf(statusStr) : null;
                break;
            default:
                value = super.getMethodParamValue(paramName, caseData);
        }
        log.debug("getMethodParamValue paramName={}, value={}", paramName, value);
        return value;
    }
} 