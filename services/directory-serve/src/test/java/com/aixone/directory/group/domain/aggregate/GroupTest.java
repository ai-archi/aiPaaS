package com.aixone.directory.group.domain.aggregate;

import com.aixone.directory.test.AbstractExcelDrivenTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.aixone.directory.user.domain.aggregate.UserStatus;
import com.aixone.directory.user.domain.aggregate.Profile;

public class GroupTest extends AbstractExcelDrivenTest {
    @BeforeAll
    static void setUp() {
        loadTestCases("usecases/group_cases.xlsx");
    }

    @Test
    void testAddMember() {
        execute(new String[]{"正常添加成员", "重复添加成员"}, getMethod(Group.class, "addMember", String.class));
    }

    @Test
    void testRemoveMember() {
        execute(new String[]{"正常移除成员", "移除不存在成员"}, getMethod(Group.class, "removeMember", String.class));
    }

    @Test
    void testAddRole() {
        execute(new String[]{"正常添加角色", "重复添加角色"}, getMethod(Group.class, "addRole", String.class));
    }

    @Test
    void testRemoveRole() {
        execute(new String[]{"正常移除角色", "移除不存在角色"}, getMethod(Group.class, "removeRole", String.class));
    }

    @Override
    protected Object getMethodParamValue(String paramName, java.util.Map<String, String> caseData) {
        System.out.println("[DEBUG][GroupTest.getMethodParamValue] paramName=" + paramName + ", caseData.keys=" + caseData.keySet() + ", value=" + caseData.get(paramName));
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