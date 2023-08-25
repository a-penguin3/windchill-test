package com.zybio.pmgt.utils;

import ext.lang.PIExcelUtils;
import ext.pi.PIException;
import ext.pi.core.PICoreHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.util.ArrayList;

public class TestUtilyUtil {

    private static final ArrayList<String> selects = new ArrayList<>();

    public static String CODEBASE;
    public static String FILEPATH;
    public static String excelName;

    static {
        try {
            CODEBASE = PICoreHelper.service.getCodebase();
            FILEPATH = CODEBASE + File.separator + "com" + File.separator + "zybio" + File.separator + "config" + File.separator;
            excelName = FILEPATH + "测试下拉框.xlsx";
        } catch (PIException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<String> getSelect() throws PIException {
        if (selects.isEmpty()){
            setSelect();
        }
        return selects;
    }

    private static void setSelect() throws PIException {
        Workbook wb = PIExcelUtils.getWorkbook(new File(excelName));
        Sheet sheet = wb.getSheetAt(0);
        int rowNums = sheet.getLastRowNum();
        System.out.println("util总行数为：" + rowNums);

        for (int i = 0; i <= rowNums; i++) {
            Row cur = sheet.getRow(i);
            int cellNums = cur.getLastCellNum();
            System.out.println("总列数为：" + cellNums);
            if (cur.getCell(0)==null){
                continue;
            }
            String a = String.valueOf(cur.getCell(0));
            System.out.println("单元格的值为：" + cur.getCell(0));

            selects.add(a);
        }
    }
}
