package com.aixone.directory.test;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterAll;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Excel驱动的抽象测试基类
 * 负责执行方法调用和结果汇总
 */
public abstract class AbstractExcelDrivenTest {
    // 所有测试用例数据
    protected static List<Map<String, String>> allTestCases = new ArrayList<>();
    // 测试结果统计
    protected static Map<String, TestResult> testResults = new HashMap<>();
    // 当前测试用例数据
    protected Map<String, String> currentCase;

    /**
     * 加载Excel用例数据
     * @param excelPath Excel文件路径
     */
    protected static void loadTestCases(String excelPath) {
        try (InputStream is = AbstractExcelDrivenTest.class.getClassLoader().getResourceAsStream(excelPath)) {
            if (is == null) {
                throw new RuntimeException("Cannot find Excel file: " + excelPath);
            }
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            // 获取表头
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                headers.add(cell != null ? cell.getStringCellValue() : "");
            }
            System.out.println("DEBUG: 表头: " + headers);
            // 读取数据行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Map<String, String> caseData = new HashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    String value = "";
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING:
                                value = cell.getStringCellValue();
                                break;
                            case NUMERIC:
                                value = String.valueOf((long) cell.getNumericCellValue());
                                break;
                            case BOOLEAN:
                                value = String.valueOf(cell.getBooleanCellValue());
                                break;
                            default:
                                value = "";
                        }
                    }
                    caseData.put(headers.get(j), value);
                }
                allTestCases.add(caseData);
                System.out.println("DEBUG: 加载用例: " + caseData.get("caseName"));
            }
            System.out.println("DEBUG: 总共加载了 " + allTestCases.size() + " 个用例");
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Excel file: " + excelPath, e);
        }
    }

    /**
     * 获取指定用例数据
     * @param caseName 用例名称
     * @return 用例数据
     */
    protected Map<String, String> getUseCase(String caseName) {
        return allTestCases.stream()
                .filter(caseData -> caseName.equals(caseData.get("caseName")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Test case not found: " + caseName));
    }

    /**
     * 执行测试方法
     * @param caseNames 用例名称数组
     * @param method 要测试的方法
     */
    protected void execute(String[] caseNames, Method method) {
        for (String caseName : caseNames) {
            currentCase = getUseCase(caseName);
            String caseType = currentCase.get("caseType");
            String expectedResult = currentCase.get("expectedResult");
            String expectedException = currentCase.get("expectedException");
            try {
                // 构建方法参数
                Object[] parameters = buildMethodParameters(method, currentCase);
                // 执行方法
                Object result = invokeTestMethod(method, parameters);
                // 根据用例类型进行断言
                performAssertion(caseType, expectedResult, expectedException, result, null);
                // 记录成功结果
                recordTestResult(caseName, true, null);
            } catch (Exception e) {
                // 根据用例类型进行断言
                performAssertion(caseType, expectedResult, expectedException, null, e);
                // 记录结果
                boolean isExpectedException = expectedException != null && 
                    e.getCause() != null && 
                    e.getCause().getClass().getSimpleName().equals(expectedException);
                recordTestResult(caseName, isExpectedException, e.getCause());
            }
        }
    }

    /**
     * 调用测试方法 - 子类可以重写此方法来自定义调用逻辑
     * @param method 要调用的方法
     * @param params 方法参数
     * @return 方法执行结果
     * @throws Exception 执行异常
     */
    protected Object invokeTestMethod(Method method, Object... params) throws Exception {
        // 默认实现：静态调用方法
        return method.invoke(null, params);
    }

    /**
     * 构建方法参数
     * @param method 方法对象
     * @param caseData 用例数据
     * @return 参数数组
     */
    private Object[] buildMethodParameters(Method method, Map<String, String> caseData) {
        String[] paramNames = getParameterNames(method);
        Object[] parameters = new Object[paramNames.length];
        for (int i = 0; i < paramNames.length; i++) {
            parameters[i] = getMethodParamValue(paramNames[i], caseData);
        }
        return parameters;
    }

    /**
     * 通过反射获取方法参数名
     * @param method 方法对象
     * @return 参数名数组
     */
    private String[] getParameterNames(Method method) {
        java.lang.reflect.Parameter[] parameters = method.getParameters();
        String[] paramNames = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            java.lang.reflect.Parameter param = parameters[i];
            String paramName = param.getName();
            // 如果参数名是合成的（如arg0, arg1），使用索引作为参数名
            if (paramName.startsWith("arg") || paramName.equals("param" + i)) {
                paramName = String.valueOf(i);
            }
            paramNames[i] = paramName;
        }
        return paramNames;
    }

    /**
     * 获取方法参数值 - 子类必须实现
     * @param paramName 参数名
     * @param caseData 用例数据
     * @return 参数值
     */
    protected abstract Object getMethodParamValue(String paramName, Map<String, String> caseData);

    /**
     * 执行断言
     * @param caseType 用例类型
     * @param expectedResult 期望结果
     * @param expectedException 期望异常
     * @param result 实际结果
     * @param exception 实际异常
     */
    private void performAssertion(String caseType, String expectedResult, String expectedException, 
                                 Object result, Exception exception) {
        System.out.println("[DEBUG] 用例类型: " + caseType + ", 期望结果: " + expectedResult + ", 实际结果: " + result + ", 异常: " + exception + ", 当前用例: " + (currentCase != null ? currentCase.get("caseName") : "null"));
        switch (caseType) {
            case "assertTrue":
                // 对于assertTrue，如果返回非null对象且不是Boolean.FALSE，就认为是成功的
                boolean isSuccess = (result != null && !Boolean.FALSE.equals(result));
                assertTrue(isSuccess, "Expected true but got: " + result);
                break;
            case "assertFalse":
                assertFalse(result != null && Boolean.TRUE.equals(result), 
                    "Expected false but got: " + result);
                break;
            case "assertEquals":
                assertEquals(expectedResult, result, "Expected: " + expectedResult + ", but got: " + result);
                break;
            case "assertNotNull":
                assertNotNull(result, "Expected not null but got null");
                break;
            case "assertNull":
                assertNull(result, "Expected null but got: " + result);
                break;
            case "assertThrows":
                // 期望抛出异常
                assertNotNull(exception, "Expected exception but none was thrown");
                break;
            case "assertDoesNotThrow":
                assertNull(exception, "Expected no exception but got: " + exception);
                break;
            default:
                throw new IllegalArgumentException("Unknown caseType: " + caseType);
        }
    }

    /**
     * 记录测试结果
     * @param caseName 用例名称
     * @param success 是否成功
     * @param exception 异常信息
     */
    private void recordTestResult(String caseName, boolean success, Throwable exception) {
        TestResult result = new TestResult();
        result.setCaseName(caseName);
        result.setSuccess(success);
        result.setException(exception);
        result.setTimestamp(LocalDateTime.now());
        testResults.put(caseName, result);
    }

    /**
     * 输出测试结果汇总
     */
    @AfterAll
    static void outputTestResults() {
        System.out.println("\n=== 测试结果汇总 ===");
        System.out.println("总用例数: " + testResults.size());
        long successCount = testResults.values().stream()
                .filter(TestResult::isSuccess)
                .count();
        System.out.println("成功用例数: " + successCount);
        System.out.println("失败用例数: " + (testResults.size() - successCount));
        System.out.println("\n=== 详细结果 ===");
        testResults.values().stream()
                .sorted(Comparator.comparing(TestResult::getTimestamp))
                .forEach(result -> {
                    String status = result.isSuccess() ? "✓" : "✗";
                    System.out.printf("%s %s - %s%n", 
                        status, result.getCaseName(), 
                        result.getException() != null ? result.getException().getMessage() : "成功");
                });
    }

    /**
     * 测试结果内部类
     */
    protected static class TestResult {
        private String caseName;
        private boolean success;
        private Throwable exception;
        private LocalDateTime timestamp;
        // Getters and Setters
        public String getCaseName() { return caseName; }
        public void setCaseName(String caseName) { this.caseName = caseName; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public Throwable getException() { return exception; }
        public void setException(Throwable exception) { this.exception = exception; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
} 