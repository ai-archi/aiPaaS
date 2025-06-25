package com.aixone.directory.test;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

public class ExcelReader {
    public static void main(String[] args) {
        String excelPath = "usecases/user_cases.xlsx";
        System.out.println("正在读取Excel文件: " + excelPath);
        
        try (InputStream is = ExcelReader.class.getClassLoader().getResourceAsStream(excelPath)) {
            if (is == null) {
                System.out.println("找不到Excel文件: " + excelPath);
                return;
            }
            
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            
            // 获取表头
            Row headerRow = sheet.getRow(0);
            System.out.println("表头:");
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                String header = cell != null ? cell.getStringCellValue() : "";
                System.out.print(header + "\t");
            }
            System.out.println();
            
            // 读取数据行
            System.out.println("数据行:");
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
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
                    System.out.print(value + "\t");
                }
                System.out.println();
            }
            
            workbook.close();
        } catch (IOException e) {
            System.out.println("读取Excel文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 