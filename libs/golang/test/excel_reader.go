package test

import (
	"fmt"
	"os"

	"github.com/xuri/excelize/v2"
)

// ExcelReader 用于读取Excel文件并打印内容
func ExcelReader(excelPath string) error {
	fmt.Printf("正在读取Excel文件: %s\n", excelPath)
	f, err := excelize.OpenFile(excelPath)
	if err != nil {
		return fmt.Errorf("找不到Excel文件: %w", err)
	}
	defer f.Close()
	sheetName := f.GetSheetName(0)
	rows, err := f.GetRows(sheetName)
	if err != nil {
		return fmt.Errorf("读取Excel失败: %w", err)
	}
	if len(rows) == 0 {
		return fmt.Errorf("Excel无数据")
	}
	fmt.Println("表头:")
	headers := rows[0]
	for _, h := range headers {
		fmt.Printf("%s\t", h)
	}
	fmt.Println()
	fmt.Println("数据行:")
	for _, row := range rows[1:] {
		if len(row) == 0 {
			continue
		}
		for j := 0; j < len(headers); j++ {
			val := ""
			if j < len(row) {
				val = row[j]
			}
			fmt.Printf("%s\t", val)
		}
		fmt.Println()
	}
	return nil
}

// main函数示例
func main() {
	if len(os.Args) < 2 {
		fmt.Println("用法: excel_reader <excel文件路径>")
		return
	}
	err := ExcelReader(os.Args[1])
	if err != nil {
		fmt.Printf("读取Excel文件失败: %v\n", err)
	}
}
