package com.aixone.directory.role.domain.aggregate;

import com.aixone.common.test.AbstractExcelDrivenTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import com.aixone.directory.user.domain.aggregate.UserStatus;
import com.aixone.directory.user.domain.aggregate.Profile;

public class RoleTest extends AbstractExcelDrivenTest {
    @BeforeAll
    static void setUp() {
        loadTestCases("usecases/role_cases.xlsx");
    }

    @Test
    void testGrantUser() {
        execute(new String[]{"授予用户角色", "重复授予用户"}, getMethod(Role.class, "grantUser", String.class));
    }

    @Test
    void testRevokeUser() {
        execute(new String[]{"撤销用户角色", "撤销不存在用户"}, getMethod(Role.class, "revokeUser", String.class));
    }

    @Test
    void testGrantGroup() {
        execute(new String[]{"授予群组角色", "重复授予群组"}, getMethod(Role.class, "grantGroup", String.class));
    }

    @Test
    void testRevokeGroup() {
        execute(new String[]{"撤销群组角色", "撤销不存在群组"}, getMethod(Role.class, "revokeGroup", String.class));
    }

    @Override
    protected Object getMethodParamValue(String paramName, java.util.Map<String, String> caseData) {
        Object value;
        String lower = paramName.toLowerCase();
        switch (paramName) {
            case "profile":
            case "newProfile":
                value = Profile.builder()
                    .username(caseData.get("profile_username"))
                    .avatarUrl(caseData.get("profile_avatarurl"))
                    .bio(caseData.get("profile_bio"))
                    .build();
                break;
            case "status":
                String statusStr = caseData.get("status");
                value = statusStr != null && !statusStr.isEmpty() ? UserStatus.valueOf(statusStr) : null;
                break;
            default:
                if (lower.contains("id") || lower.contains("user") || lower.contains("role") || lower.contains("group") || lower.contains("member") || lower.contains("position") || lower.contains("department")) {
                    String v = caseData.get(paramName);
                    value = v != null && !v.isEmpty() && !"null".equalsIgnoreCase(v) ? v : "dummy-" + paramName;
                } else {
                    value = caseData.get(paramName);
                }
        }
        return value;
    }
} 