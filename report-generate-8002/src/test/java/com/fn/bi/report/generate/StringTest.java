package com.fn.bi.report.generate;

import com.fn.bi.report.generate.util.ExcelWriteUtil2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootTest
public class StringTest {


//    @Test
//    public void str() {
//        Pattern compile = Pattern.compile("[#now+]+(-?\\d{2}|-?\\d{1})");
//
//        String str = "65x46wwx416wq#now+55 ssssss  #now+44  55";
//        Matcher matcher = compile.matcher(str);
//        String group = null;
//        while (matcher.find()) {
//            group = matcher.group();
//            System.out.println(group);
//            str = str.replace(group, "****");
//        }
//        System.out.println(str);
//        System.out.println(Integer.valueOf(-5));
//    }

    List<File> fileList = new ArrayList<>();

    @Test
    public void fixModel() {
//        C:\Users\yufeng.gong\Desktop\JAVA
//        C:\Users\yufeng.gong\Desktop\BA07.3.xlsx
//        File[] files = new File[]{new File("C:\\Users\\yufeng.gong\\Desktop\\JAVA")};
//        getFile(files);
//        for (File file : fileList) {
//            this.fix(file);
//        }
//        System.out.println(123);
    }

    public void getFile(File[] file) {
        for (File file1 : file) {
            if (file1.isDirectory()) {
                getFile(Objects.requireNonNull(file1.listFiles()));
            } else {
                fileList.add(file1);
            }
        }
    }

    public void fix(File file) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            for (Sheet sheet : workbook) {
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        Object value = ExcelWriteUtil2.getValue(cell);
                        if (value == null) continue;
                        String str = value.toString();
                        if (str.contains("data") || str.contains("datas")||str.contains("spilt")) {
                            Row row1 = cell.getRow();
                            for (Cell cell1 : row1) {
                                Object value1 = ExcelWriteUtil2.getValue(cell1);
                                if (value1 == null) continue;
                                String str1 = value1.toString();
                                str1 = str1
//                                        .replaceAll("datas", "#No")
//                                        .replaceAll("data", "#No")
//                                        .replaceAll("No biz", "No biz data")
//                                        .replaceAll("No biz data", "No data")
                                        .replaceAll("styles", "")
                                        .replaceAll("style", "");
                                cell1.setCellValue(str1);
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
            fileInputStream.close();
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                assert fileInputStream != null;
                fileInputStream.close();
                assert fileOutputStream != null;
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }


}
