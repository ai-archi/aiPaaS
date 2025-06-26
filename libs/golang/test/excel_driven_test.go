package test

import (
	"encoding/json"
	"fmt"
	"os"
	"os/exec"
	"reflect"
	"runtime"
	"testing"
	"time"

	"github.com/google/uuid"
	"github.com/xuri/excelize/v2"
)

// 全局统计所有测试方法的用例结果
type GlobalTestResult struct {
	CaseName  string
	Success   bool
	Exception error
	Timestamp time.Time
}

var AllTestResults = map[string]*GlobalTestResult{}

// ExcelDrivenTest 提供基于Excel的测试驱动能力
// 负责加载用例、执行方法、断言和结果统计
// 需嵌入到具体测试结构体中

type ExcelDrivenTest struct {
	AllTestCases []map[string]string
	TestResults  map[string]*TestResult
	CurrentCase  map[string]string
	ParamNameMap map[string][]string // 方法名到参数名映射
}

type TestResult struct {
	CaseName  string
	Success   bool
	Exception error
	Timestamp time.Time
}

// LoadParamNameMap 加载参数名映射表
func (e *ExcelDrivenTest) LoadParamNameMap(jsonPath string) error {
	f, err := os.Open(jsonPath)
	if err != nil {
		return err
	}
	defer f.Close()
	dec := json.NewDecoder(f)
	return dec.Decode(&e.ParamNameMap)
}

// AutoExtractParamNames 自动调用go/ast脚本生成参数名映射表，支持多个源码文件
func AutoExtractParamNames(srcFiles []string, outJson string) error {
	if len(srcFiles) == 0 {
		return nil
	}
	args := []string{"run", "tools/extract_param_names.go", "-source", srcFiles[0], "-o", outJson}
	cmd := exec.Command("go", args...)
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	return cmd.Run()
}

// LoadTestCases 加载Excel用例数据，并自动生成/加载参数名映射表
// srcFiles 支持传入多个源码文件
func (e *ExcelDrivenTest) LoadTestCases(excelPath string, srcFiles ...string) error {
	if len(srcFiles) == 0 {
		fmt.Println("[参数名提取] 未传入源码文件，跳过参数名映射表生成")
	} else {
		// 每次都强制生成 param_map.json
		if err := AutoExtractParamNames(srcFiles, "param_map.json"); err != nil {
			fmt.Printf("[参数名提取] 自动生成参数名映射表失败: %v\n", err)
		} else {
			fmt.Println("[参数名提取] 已自动生成参数名映射表 param_map.json")
		}
		if err := e.LoadParamNameMap("param_map.json"); err != nil {
			fmt.Printf("[参数名提取] 加载参数名映射表失败: %v\n", err)
		} else {
			fmt.Printf("[参数名提取] 加载参数名映射表成功: %+v\n", e.ParamNameMap)
		}
	}
	f, err := excelize.OpenFile(excelPath)
	if err != nil {
		return fmt.Errorf("无法打开Excel文件: %w", err)
	}
	defer f.Close()
	sheetName := f.GetSheetName(0)
	rows, err := f.GetRows(sheetName)
	if err != nil {
		return fmt.Errorf("读取Excel行失败: %w", err)
	}
	if len(rows) < 2 {
		return fmt.Errorf("Excel用例数据不足")
	}
	headers := rows[0]
	for i, row := range rows[1:] {
		if len(row) == 0 {
			continue
		}
		caseData := map[string]string{}
		for j, h := range headers {
			if j < len(row) {
				caseData[h] = row[j]
			} else {
				caseData[h] = ""
			}
		}
		e.AllTestCases = append(e.AllTestCases, caseData)
		fmt.Printf("加载用例%d: %s\n", i+1, caseData["caseName"])
	}
	return nil
}

// GetUseCase 获取指定用例
func (e *ExcelDrivenTest) GetUseCase(caseName string) (map[string]string, error) {
	for _, c := range e.AllTestCases {
		if c["caseName"] == caseName {
			return c, nil
		}
	}
	return nil, fmt.Errorf("未找到用例: %s", caseName)
}

// 定义接口
// ParamValueProvider 提供参数值获取能力
type ParamValueProvider interface {
	GetMethodParamValue(paramName string, caseData map[string]string) interface{}
}

// BuildMethodParameters 支持多态
func (e *ExcelDrivenTest) BuildMethodParameters(provider ParamValueProvider, method interface{}, caseData map[string]string) []interface{} {
	methodName := runtimeFuncName(reflect.ValueOf(method).Pointer())
	paramNames := e.ParamNameMap[methodName]
	params := make([]interface{}, 0, len(paramNames))
	for _, pname := range paramNames {
		params = append(params, provider.GetMethodParamValue(pname, caseData))
	}
	fmt.Printf("[参数组装debug] methodName: %s, paramNames: %+v\n", methodName, paramNames)
	for i, p := range params {
		fmt.Printf("[参数类型debug] paramName: %s, value: %+v, type: %T\n", paramNames[i], p, p)
	}
	fmt.Printf("[参数组装debug] params: %+v\n", params)
	return params
}

// Execute 方法调用 BuildMethodParameters 时传入 provider
func (e *ExcelDrivenTest) Execute(t *testing.T, provider ParamValueProvider, caseNames []string, method interface{}) {
	methodName := ""
	if fn := reflect.ValueOf(method).Pointer(); fn != 0 {
		methodName = runtimeFuncName(fn)
	}
	for _, caseName := range caseNames {
		c, err := e.GetUseCase(caseName)
		if err != nil {
			t.Errorf("用例获取失败: %v", err)
			continue
		}
		if c != nil {
			c["_testMethod"] = methodName
		}
		e.CurrentCase = c
		caseType := c["caseType"]
		expectedResult := c["expectedResult"]
		expectedException := c["expectedException"]
		var result interface{}
		var realErr error
		params := e.BuildMethodParameters(provider, method, c)
		funcValue := reflect.ValueOf(method)
		if params == nil {
			msg := fmt.Sprintf("【用例数据错误】用例[%s]参数构建失败，字段缺失或格式错误。请检查user_cases.xlsx字段与方法签名。", caseName)
			fmt.Println(msg)
			e.RecordTestResult(caseName, false, fmt.Errorf(msg))
			continue
		}
		if len(params) != funcValue.Type().NumIn() {
			msg := fmt.Sprintf("【用例数据错误】用例[%s]参数数量不匹配，期望%d，实际%d。请检查user_cases.xlsx字段与方法签名。", caseName, funcValue.Type().NumIn(), len(params))
			fmt.Println(msg)
			e.RecordTestResult(caseName, false, fmt.Errorf(msg))
			continue
		}
		in := []reflect.Value{}
		for _, p := range params {
			in = append(in, reflect.ValueOf(p))
		}
		func() {
			defer func() {
				if r := recover(); r != nil {
					realErr = fmt.Errorf("【框架/反射调用错误】用例[%s]反射调用panic: %v", caseName, r)
				}
			}()
			out := funcValue.Call(in)
			if len(out) > 0 {
				result = out[0].Interface()
			}
			if len(out) > 1 {
				if errVal, ok := out[1].Interface().(error); ok {
					realErr = errVal
				}
			}
		}()
		e.PerformAssertion(t, caseType, expectedResult, expectedException, result, realErr)
		e.RecordTestResult(caseName, realErr == nil || (expectedException != "" && realErr != nil && realErr.Error() == expectedException), realErr)
	}
}

// uuidParseOrNil 辅助方法
func uuidParseOrNil(s string) interface{} {
	t, err := uuid.Parse(s)
	if err != nil {
		return nil
	}
	return t
}

// PerformAssertion 执行断言
func (e *ExcelDrivenTest) PerformAssertion(t *testing.T, caseType, expectedResult, expectedException string, result interface{}, err error) {
	switch caseType {
	case "assertTrue":
		if b, ok := result.(bool); !ok || !b {
			t.Errorf("期望true, 实际: %v", result)
		}
	case "assertFalse":
		if b, ok := result.(bool); !ok || b {
			t.Errorf("期望false, 实际: %v", result)
		}
	case "assertEquals":
		if fmt.Sprintf("%v", result) != expectedResult {
			t.Errorf("期望: %s, 实际: %v", expectedResult, result)
		}
	case "assertNotNull":
		if result == nil {
			t.Errorf("期望非空, 实际: nil")
		}
	case "assertNull":
		if result != nil {
			t.Errorf("期望空, 实际: %v", result)
		}
	case "assertThrows":
		if err == nil {
			t.Errorf("期望异常, 实际无异常")
		} else if expectedException != "" {
			errType := reflect.TypeOf(err)
			if errType.Kind() == reflect.Ptr {
				errType = errType.Elem()
			}
			typeName := errType.Name()
			if typeName != expectedException {
				t.Errorf("期望异常类型: %s, 实际: %s", expectedException, typeName)
			}
		}
	case "assertDoesNotThrow":
		if err != nil {
			t.Errorf("期望无异常, 实际: %v", err)
		}
	case "assertVoidState":
		if err != nil {
			t.Errorf("期望无异常, 实际: %v", err)
		}
	default:
		t.Errorf("未知caseType: %s", caseType)
	}
}

// RecordTestResult 记录测试结果
func (e *ExcelDrivenTest) RecordTestResult(caseName string, success bool, err error) {
	if e.TestResults == nil {
		e.TestResults = map[string]*TestResult{}
	}
	one := &TestResult{
		CaseName:  caseName,
		Success:   success,
		Exception: err,
		Timestamp: time.Now(),
	}
	e.TestResults[caseName] = one
	// 全局统计，key加上当前测试方法名避免覆盖
	methodName := ""
	if e.CurrentCase != nil && e.CurrentCase["_testMethod"] != "" {
		methodName = e.CurrentCase["_testMethod"]
	}
	key := caseName
	if methodName != "" {
		key = methodName + ":" + caseName
	}
	// 包含/正则/多关键字策略
	successFlag := success
	if !success && e.CurrentCase != nil && e.CurrentCase["expectedException"] != "" && err != nil {
		ex := e.CurrentCase["expectedException"]
		errType := reflect.TypeOf(err)
		if errType.Kind() == reflect.Ptr {
			errType = errType.Elem()
		}
		typeName := errType.Name()
		successFlag = (typeName == ex)
	}
	AllTestResults[key] = &GlobalTestResult{
		CaseName:  key,
		Success:   successFlag,
		Exception: err,
		Timestamp: one.Timestamp,
	}
}

// OutputTestResults 输出全局测试结果汇总
func (e *ExcelDrivenTest) OutputTestResults() {
	fmt.Println("\n=== 全部测试结果汇总 ===")
	fmt.Printf("总用例数: %d\n", len(AllTestResults))
	successCount := 0
	for _, r := range AllTestResults {
		if r.Success {
			successCount++
		}
	}
	fmt.Printf("成功用例数: %d\n", successCount)
	fmt.Printf("失败用例数: %d\n", len(AllTestResults)-successCount)
	fmt.Println("\n=== 详细结果 ===")
	for _, r := range AllTestResults {
		status := "✗"
		if r.Success {
			status = "✓"
		}
		detail := "断言通过【通过】"
		if !r.Success {
			if r.Exception != nil {
				detail = fmt.Sprintf("异常: %v【失败】", r.Exception)
			} else {
				detail = "断言失败【失败】"
			}
		}
		fmt.Printf("%s %s - %s\n", status, r.CaseName, detail)
	}
}

// TestMain 统一输出全局统计
func TestMain(m *testing.M) {
	code := m.Run() // 运行所有测试
	OutputAllTestResults()
	os.Exit(code)
}

// 获取函数名
func runtimeFuncName(ptr uintptr) string {
	fn := runtime.FuncForPC(ptr)
	if fn == nil {
		return "unknown"
	}
	name := fn.Name()
	// 只取最后一个.后的部分
	if idx := len(name) - 1; idx >= 0 {
		for i := len(name) - 1; i >= 0; i-- {
			if name[i] == '.' {
				return name[i+1:]
			}
		}
	}
	return name
}

// OutputAllTestResults 只输出一次最终统计
var outputOnce bool

func OutputAllTestResults() {
	if outputOnce {
		return
	}
	outputOnce = true
	fmt.Println("\n=== 全部测试结果汇总 ===")
	fmt.Printf("总用例数: %d\n", len(AllTestResults))
	successCount := 0
	for _, r := range AllTestResults {
		if r.Success {
			successCount++
		}
	}
	fmt.Printf("成功用例数: %d\n", successCount)
	fmt.Printf("失败用例数: %d\n", len(AllTestResults)-successCount)
	fmt.Println("\n=== 详细结果 ===")
	for _, r := range AllTestResults {
		status := "✗"
		if r.Success {
			status = "✓"
		}
		detail := "断言通过【通过】"
		if !r.Success {
			if r.Exception != nil {
				detail = fmt.Sprintf("异常: %v【失败】", r.Exception)
			} else {
				detail = "断言失败【失败】"
			}
		}
		fmt.Printf("%s %s - %s\n", status, r.CaseName, detail)
	}
}
