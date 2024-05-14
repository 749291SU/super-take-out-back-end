package com.sky.poi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @projectName: super-takeout
 * @package: com.sky.poi
 * @className: POITest
 * @author: 749291
 * @description: TODO
 * @date: 5/14/2024 15:12
 * @version: 1.0
 */

/**
 * 使用Apache POI操作Excel
 */
@SpringBootTest
public class POITest {
    public static void write() throws FileNotFoundException {
        XSSFWorkbook sheets = new XSSFWorkbook();
        Sheet sheet = sheets.createSheet("用户信息");
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("用户ID");
        row.createCell(1).setCellValue("用户名");
        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("1");
        row1.createCell(1).setCellValue("张三");

        FileOutputStream out = new FileOutputStream("D:/user.xlsx");
        try {
            sheets.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void read() {
        // 读取D:/user.xlsx
        try {
            Workbook workbook = new XSSFWorkbook("D:/user.xlsx");
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                for (Cell cell : row) {
                    System.out.print(cell.getStringCellValue() + "\t");
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            write();
            read();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
