package main

import (
	"encoding/json"
	"flag"
	"fmt"
	"go/ast"
	"go/parser"
	"go/token"
	"os"
)

func main() {
	var outPath string
	var src string
	flag.StringVar(&src, "source", "", "go source file to analyze")
	flag.StringVar(&outPath, "o", "param_map.json", "output json file path")
	flag.Parse()
	if src == "" {
		fmt.Println("请指定-source参数，如: go run extract_param_names.go -source user_test.go -o param_map.json")
		os.Exit(1)
	}
	paramMap := map[string][]string{}
	fset := token.NewFileSet()
	astFile, err := parser.ParseFile(fset, src, nil, parser.AllErrors)
	if err != nil {
		fmt.Fprintf(os.Stderr, "解析文件失败: %s, err: %v\n", src, err)
		os.Exit(1)
	}
	ast.Inspect(astFile, func(n ast.Node) bool {
		if fn, ok := n.(*ast.FuncDecl); ok {
			paramNames := []string{}
			for _, param := range fn.Type.Params.List {
				for _, name := range param.Names {
					paramNames = append(paramNames, name.Name)
				}
			}
			if fn.Recv == nil { // 普通函数
				paramMap[fn.Name.Name] = paramNames
			} else { // 方法
				// 方法名格式: 方法名-fm，兼容reflect.FuncForPC
				methodKey := fn.Name.Name + "-fm"
				paramMap[methodKey] = paramNames
			}
		}
		return true
	})
	f, err := os.Create(outPath)
	if err != nil {
		fmt.Fprintf(os.Stderr, "无法创建输出文件: %v\n", err)
		os.Exit(1)
	}
	defer f.Close()
	enc := json.NewEncoder(f)
	enc.SetIndent("", "  ")
	if err := enc.Encode(paramMap); err != nil {
		fmt.Fprintf(os.Stderr, "写入json失败: %v\n", err)
		os.Exit(1)
	}
	fmt.Printf("参数名映射表已生成: %s\n", outPath)
}
