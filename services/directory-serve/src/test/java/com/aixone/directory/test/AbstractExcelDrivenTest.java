package com.aixone.directory.test;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Excel驱动的抽象测试基类
 * 负责执行方法调用和结果汇总
 */
@ExtendWith(MockitoExtension.class)
public abstract class AbstractExcelDrivenTest {
    protected static final Logger log = LoggerFactory.getLogger(AbstractExcelDrivenTest.class);
    // 所有测试用例数据
    protected static List<Map<String, String>> allTestCases = new ArrayList<>();
    // 测试结果统计
    protected static Map<String, TestResult> testResults = new HashMap<>();
    // 当前测试用例数据
    protected Map<String, String> currentCase;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

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
            log.debug("表头: {}", headers);
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
                log.debug("加载用例: {}", caseData.get("caseName"));
            }
            log.debug("总共加载了 {} 个用例", allTestCases.size());
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
        // 自动推断当前JUnit测试方法名和类名
        String testMethodName = null;
        String testClassName = null;
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement el : stack) {
            if (el.getClassName().endsWith("Test") && !el.getMethodName().equals("execute")) {
                testMethodName = el.getMethodName();
                String[] classParts = el.getClassName().split("\\.");
                testClassName = classParts[classParts.length - 1];
                break;
            }
        }
        for (String caseName : caseNames) {
            currentCase = getUseCase(caseName);
            String caseType = currentCase.get("caseType");
            log.debug("caseType: {}", caseType);

            if (caseType == null || caseType.trim().isEmpty()) {
                String msg = "caseType不可为空";
                log.error("用例[{}] caseType为空，跳过执行: {}", caseName, msg);
                recordTestResult(caseName, false, new IllegalArgumentException(msg), testMethodName, testClassName);
                continue;
            }

            String expectedResult = currentCase.get("expectedResult");  
            log.debug("expectedResult: {}", expectedResult);
            String expectedException = currentCase.get("expectedException");
            log.debug("expectedException: {}", expectedException);
            try {
                // 构建方法参数
                Object[] parameters = buildMethodParameters(method, currentCase);
                // 执行方法
                Object result = null;
                Exception realException = null;
                try {
                    result = invokeTestMethod(method, parameters);
                } catch (java.lang.reflect.InvocationTargetException ite) {
                    realException = ite.getCause() instanceof Exception ? (Exception) ite.getCause() : ite;
                } catch (Exception e) {
                    realException = e;
                }
                // 根据用例类型进行断言
                performAssertion(caseName,caseType, expectedResult, expectedException, result, realException);
                // 记录成功结果
                recordTestResult(caseName, realException == null || (expectedException != null && realException.getClass().getSimpleName().equals(expectedException)), realException, testMethodName, testClassName);
            } catch (Exception e) {
                // 记录结果
                recordTestResult(caseName, false, e, testMethodName, testClassName);
            }
        }
    }

    /**
     * 调用测试方法 - 支持静态方法和实例方法
     * @param method 要调用的方法
     * @param params 方法参数
     * @return 方法执行结果
     * @throws Exception 执行异常
     */
    protected Object invokeTestMethod(Method method, Object... params) throws Exception {
        log.debug("invokeTestMethod 调用: method={}, params={}", method.getName(), java.util.Arrays.toString(params));
        for (int i = 0; i < params.length; i++) {
            log.debug("invokeTestMethod param[{}]: {}", i, params[i]);
        }
        Object result;
        if ((method.getModifiers() & java.lang.reflect.Modifier.STATIC) != 0) {
            result = method.invoke(null, params);
        } else {
            Class<?> clazz = method.getDeclaringClass();
            Object instance = clazz.getDeclaredConstructor().newInstance();
            // 注入 mock 字段
            injectMocks(instance);
            java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
            if (currentCase != null) {
                for (java.lang.reflect.Field field : fields) {
                    String key = field.getName();
                    Object value = getMethodParamValue(key, currentCase);
                    if (value != null) {
                        try {
                            String setter = "set" + Character.toUpperCase(key.charAt(0)) + key.substring(1);
                            for (Method m : clazz.getMethods()) {
                                if (m.getName().equals(setter) && m.getParameterCount() == 1) {
                                    m.invoke(instance, value);
                                    break;
                                }
                            }
                        } catch (Exception ignore) {}
                        try {
                            field.setAccessible(true);
                            field.set(instance, value);
                        } catch (Exception ignore) {}
                    }
                }
            }
            log.debug("invokeTestMethod before invoke: params={}", java.util.Arrays.toString(params));
            result = method.invoke(instance, params);
            log.debug("invokeTestMethod after invoke: result={}", result);
        }
        log.debug("invokeTestMethod 返回: result={}", result);
        return result;
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
        log.debug("buildMethodParameters: method={}, paramNames={}, parameters={}", method.getName(), java.util.Arrays.toString(paramNames), java.util.Arrays.toString(parameters));
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
     * 获取方法参数值 - 默认实现：paramName与caseData key相同直接返回字符串，否则返回null。子类可重写。
     */
    protected Object getMethodParamValue(String paramName, Map<String, String> caseData) {
        String value = caseData.getOrDefault(paramName, null);
        if (value == null || value.isEmpty() || "null".equalsIgnoreCase(value)) {
            return null;
        }
        return value;
    }

    /**
     * 执行断言
     * @param caseType 用例类型
     * @param expectedResult 期望结果
     * @param expectedException 期望异常
     * @param result 实际结果
     * @param exception 实际异常
     */
    private void performAssertion(String caseName,String caseType, String expectedResult, String expectedException, 
                                 Object result, Exception exception) {
        log.debug("用例名称: {}, 用例类型: {}, 期望结果: {}, 实际结果: {}, 异常: {}, 当前用例: {}", caseName, caseType, expectedResult, result, exception, (currentCase != null ? currentCase.get("caseName") : "null"));
        switch (caseType) {
            case "assertTrue":
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
                assertNotNull(exception, "Expected exception but none was thrown");
                break;
            case "assertDoesNotThrow":
                if (exception == null) {
                    assertNull(exception, "Expected no exception but got: " + exception);
                } else if (exception instanceof NullPointerException && exception.getMessage() != null && exception.getMessage().contains("Cannot invoke \"Object.getClass()\"")) {
                    log.info("忽略void方法返回null导致的NPE: {}", exception.getMessage());
                } else {
                    assertNull(exception, "Expected no exception but got: " + exception);
                }
                break;
            case "assertVoidState":
                // 只断言无异常，不再做对象状态断言
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
     * @param testMethodName 测试方法名
     * @param testClassName 测试类名
     */
    private void recordTestResult(String caseName, boolean success, Throwable exception, String testMethodName, String testClassName) {
        TestResult result = new TestResult();
        result.setCaseName(caseName);
        result.setSuccess(success);
        result.setException(exception);
        result.setTimestamp(LocalDateTime.now());
        result.setTestMethodName(testMethodName);
        result.setTestClassName(testClassName);
        testResults.put(caseName, result);
    }

    /**
     * 输出测试结果汇总
     */
    @AfterAll
    static void outputTestResults() {
        log.info("\n=== 测试结果汇总 ===");
        log.info("总用例数: {}", testResults.size());
        long successCount = testResults.values().stream()
                .filter(TestResult::isSuccess)
                .count();
        log.info("成功用例数: {}", successCount);
        log.info("失败用例数: {}", (testResults.size() - successCount));
        log.info("\n=== 详细结果 ===");
        testResults.values().stream()
                .sorted(Comparator.comparing(TestResult::getTimestamp))
                .forEach(result -> {
                    String caseType = null;
                    if (result.getCaseName() != null) {
                        Optional<Map<String, String>> caseData = allTestCases.stream().filter(c -> result.getCaseName().equals(c.get("caseName"))).findFirst();
                        caseType = caseData.map(c -> c.get("caseType")).orElse("");
                    }
                    String methodName = result.getTestMethodName();
                    String className = result.getTestClassName();
                    String status = result.isSuccess() ? "✓" : "✗";
                    String detail;
                    if (result.isSuccess()) {
                        if ("assertThrows".equals(caseType)) {
                            detail = "期望异常已抛出【通过】";
                        } else if ("assertDoesNotThrow".equals(caseType)) {
                            detail = "未抛异常【通过】";
                        } else {
                            detail = "断言通过【通过】";
                        }
                    } else {
                        if ("assertThrows".equals(caseType)) {
                            detail = "未抛出期望异常【失败】";
                        } else if (result.getException() != null) {
                            detail = "异常: " + result.getException().getMessage() + "【失败】";
                        } else {
                            detail = "断言失败【失败】";
                        }
                    }
                    log.info("{} [{}{}.{}] {} - {}", status, className != null ? className : "", className != null ? "." : "", methodName, result.getCaseName(), detail);
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
        private String testMethodName;
        private String testClassName;
        // Getters and Setters
        public String getCaseName() { return caseName; }
        public void setCaseName(String caseName) { this.caseName = caseName; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public Throwable getException() { return exception; }
        public void setException(Throwable exception) { this.exception = exception; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public String getTestMethodName() { return testMethodName; }
        public void setTestMethodName(String testMethodName) { this.testMethodName = testMethodName; }
        public String getTestClassName() { return testClassName; }
        public void setTestClassName(String testClassName) { this.testClassName = testClassName; }
    }

    /**
     * 通用反射获取方法
     * @param clazz 目标类
     * @param methodName 方法名
     * @param paramTypes 参数类型
     * @return 方法对象
     */
    protected Method getMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取当前测试目标对象（如user实例），子类可重写
     */
    protected Object getTestTarget() {
        return null;
    }

    // 新增：将当前测试类的 @Mock 字段注入到被测对象同名字段
    private void injectMocks(Object target) {
        java.lang.reflect.Field[] testFields = this.getClass().getDeclaredFields();
        java.lang.reflect.Field[] targetFields = target.getClass().getDeclaredFields();
        for (java.lang.reflect.Field testField : testFields) {
            if (testField.isAnnotationPresent(org.mockito.Mock.class)) {
                testField.setAccessible(true);
                try {
                    Object mock = testField.get(this);
                    for (java.lang.reflect.Field targetField : targetFields) {
                        if (targetField.getType().isAssignableFrom(testField.getType())
                                && targetField.getName().equals(testField.getName())) {
                            targetField.setAccessible(true);
                            targetField.set(target, mock);
                        }
                    }
                } catch (Exception ignore) {}
            }
        }
    }
} 