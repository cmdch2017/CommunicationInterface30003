package com.example.communicationinterface30003.excel;

import cn.hutool.core.util.ObjectUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ExcelWriter {

    private static final int MAX_ROWS_PER_SHEET = 5000;

    public static void writeToExcel(List<Map<String, Object>> resultList) {
        Workbook workbook;
        Sheet sheet;

        File file = new File("changes.xlsx");

        if (file.exists()) {
            // If file exists, read existing workbook and sheet
            try (FileInputStream fileIn = new FileInputStream(file)) {
                workbook = WorkbookFactory.create(fileIn);
                getOrCreateSheet(workbook, "Changes");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        } else {
            // If file doesn't exist, create new workbook and sheet
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Changes");
            createHeaderRow(sheet);
        }

        int lastSheetIndex = workbook.getNumberOfSheets() - 1;
        sheet = workbook.getSheetAt(lastSheetIndex);
        int rowNum = sheet.getLastRowNum() + 1;

        for (Map<String, Object> result : resultList) {
            // Check if the maximum rows per sheet is reached
            if (rowNum > MAX_ROWS_PER_SHEET) {
                lastSheetIndex++;
                sheet = workbook.createSheet("Changes_" + lastSheetIndex);
                createHeaderRow(sheet);
                rowNum = 1; // Reset row number for the new sheet
            }

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) result.get("变化字段"));
            row.createCell(1).setCellValue(String.valueOf(result.get("变化前的值")));
            row.createCell(2).setCellValue(String.valueOf(result.get("变化后的值")));
            if (ObjectUtil.isNotEmpty(result.get("状态"))) {
                row.createCell(3).setCellValue(String.valueOf(result.get("状态")));

                // 设置不正常单元格背景色为黄色
                if ((String.valueOf(result.get("状态")).equals("不正常"))) {
                    CellStyle yellowBackgroundStyle = workbook.createCellStyle();
                    yellowBackgroundStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                    yellowBackgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    row.getCell(3).setCellStyle(yellowBackgroundStyle);
                } else if (String.valueOf(result.get("状态")).equals("正常")) {
                    CellStyle greenBackGroundStyle = workbook.createCellStyle();
                    greenBackGroundStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
                    greenBackGroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    row.getCell(3).setCellStyle(greenBackGroundStyle);
                }
            }
        }

        // 调整列宽
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(3);

        // Save the workbook to the same file
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Sheet getOrCreateSheet(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
            createHeaderRow(sheet);
        }
        return sheet;
    }

    private static void createHeaderRow(Sheet sheet) {
        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("变化字段");
        headerRow.createCell(1).setCellValue("变化前的值");
        headerRow.createCell(2).setCellValue("变化后的值");
        headerRow.createCell(3).setCellValue("状态");
    }
}
