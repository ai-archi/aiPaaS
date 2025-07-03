package com.aixone.directory.organization.domain.aggregate;

import com.aixone.directory.test.AbstractExcelDrivenTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.aixone.directory.user.domain.aggregate.Profile;
import com.aixone.directory.user.domain.aggregate.UserStatus;

public class PositionTest extends AbstractExcelDrivenTest {
    @BeforeAll
    static void setUp() {
        loadTestCases("usecases/position_cases.xlsx");
    }

    @Test
    void testAddUser() {
        execute(new String[]{"正常添加用户", "重复添加用户"}, getMethod(Position.class, "addUser", String.class));
    }

    @Test
    void testRemoveUser() {
        execute(new String[]{"正常移除用户", "移除不存在用户"}, getMethod(Position.class, "removeUser", String.class));
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