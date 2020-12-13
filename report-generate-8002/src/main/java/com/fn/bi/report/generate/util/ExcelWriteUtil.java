package com.fn.bi.report.generate.util;

import com.fn.bi.backend.common.model.BackendExcel;
import com.fn.bi.backend.common.model.MsgException;
import com.fn.bi.backend.common.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.poi.ss.usermodel.CellType.STRING;

@Slf4j
public class ExcelWriteUtil {
    private static final ThreadLocal<List<List<Cell>>> workBookSignCells = new ThreadLocal<>();
    private static final ThreadLocal<XSSFWorkbook> workbook1 = new ThreadLocal<>();


    /**
     * 对外提供统一调用接口
     */
    public static void writeReport(BackendExcel<?> backendExcel) throws MsgException {

        try {
            loadTemplate(backendExcel.getTemplatePath());
            setWorkBookSignCells(backendExcel.getTitles());
            Object data = backendExcel.getData();
            String reportForm = backendExcel.getReportForm();
            if (reportForm.equals("回购率")) {
                setWorkBookDatas((List<List<List<LinkedHashMap<String, Object>>>>) data);
            }
            if (reportForm.equals("默认")) {
                setWorkBookDatasOne((List<List<LinkedHashMap<String, Object>>>) data);
            }
            if (reportForm.equals("合并")) {
                int[] col = {0, 2};
                mergeCell1(col);
            }
            writeExcel(backendExcel.getTargetFileName());
        } finally {
            workBookSignCells.remove();
            workbook1.remove();
        }
    }

    /**
     * 读取模板
     */
    public static void loadTemplate(String path) throws MsgException {
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            workbook1.set(new XSSFWorkbook(fileInputStream));
        } catch (Exception e) {
            e.printStackTrace();
            throw new MsgException("未找到模板", e.getCause());
        }
    }

    /**
     * 写出excel
     */
    private static void writeExcel(String path) throws MsgException {
        FileUtil.mkdir(path);
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(path))) {
            workbook1.get().write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MsgException("写出路径无效", e.getCause());
        }
    }

    /**
     * 获取excel标记单元格并设置title
     */
    public static void setWorkBookSignCells(String[][] titles) {
        XSSFWorkbook xssfWorkbook = workbook1.get();
        List<List<Cell>> signCells = new ArrayList<>();
        for (int sheetIndex = 0; sheetIndex < xssfWorkbook.getNumberOfSheets(); sheetIndex++) {
            XSSFSheet sheet = xssfWorkbook.getSheetAt(sheetIndex);
            try {
                signCells.add(getSheetSignCells(sheet, titles[sheetIndex]));
            } catch (Exception e) {
                signCells.add(getSheetSignCells(sheet, null));
                log.warn("标题数量与模板sheet数量不符");
            }
        }
        workBookSignCells.set(signCells);
    }

    /**
     * 获取每个sheet标记单元格，并修改title
     */
    public static LinkedList<Cell> getSheetSignCells(XSSFSheet sheet, String[] titles) {
        int titleCount = 0;
        LinkedList<Cell> sheetCells = new LinkedList<>();
        for (Row row : sheet) {
            for (Cell cell : row) {
                try {
                    CellType cellType = cell.getCellType();
                    if (cellType != STRING) {
                        continue;
                    }
                    if (cell.getStringCellValue().equals("#title") && titles != null) {
                        cell.setCellValue(titles[titleCount++]);
                    }
                    if (cell.getStringCellValue().equals("datas")) {
                        sheetCells.add(cell);
                    }
                } catch (Exception e) {
                    log.warn("标题数量与模板#title标记数量不符");
                }

            }
        }
        return sheetCells;
    }

    /**
     * 填充excel数据（每个sheet有多数据块）如回购率报表
     */
    public static void setWorkBookDatas(List<List<List<LinkedHashMap<String, Object>>>> dataMap) {
        for (int sheetNum = 0; sheetNum < workbook1.get().getNumberOfSheets(); sheetNum++) {
            List<Cell> sheetSignCells = workBookSignCells.get().get(sheetNum);
            List<List<LinkedHashMap<String, Object>>> sheetData;
            try {
                sheetData = dataMap.get(sheetNum);
            } catch (IndexOutOfBoundsException e) {
                log.warn("数据与sheet页不匹配");
                continue;
            }
            int signNum = 0;
            for (List<LinkedHashMap<String, Object>> stringListEntry : sheetData) {
                setSheetDatas(workbook1.get().getSheetAt(sheetNum), stringListEntry, sheetSignCells.get(signNum++));
            }
        }
    }

    /**
     * 填充excel数据（单个数据块）
     */
    public static void setWorkBookDatasOne(List<List<LinkedHashMap<String, Object>>> dds) {
        for (int sheetNum = 0; sheetNum < dds.size(); sheetNum++) {
            try {
                setSheetDatas(workbook1.get().getSheetAt(sheetNum), dds.get(sheetNum), workBookSignCells.get().get(sheetNum));
            } catch (IndexOutOfBoundsException e) {
                log.warn("数据与sheet页不匹配");
            }
        }
    }

    /**
     * 填充每个sheet数据（非固定模板）
     */
    public static void setSheetDatas(XSSFSheet sheet, List<LinkedHashMap<String, Object>> sheetDatas, List<Cell> sheetSignCells) {
        for (Cell signCell : sheetSignCells) {
            int beginRowNum = signCell.getRowIndex();
            int beginColumnNum = signCell.getColumnIndex();
            XSSFRow rowTemplate = sheet.getRow(beginRowNum);
            short height = rowTemplate.getHeight();
            for (int rowDataNum = 0; rowDataNum < sheetDatas.size(); rowDataNum++) {
                XSSFRow row;
                if (rowDataNum == 0) {
                    row = rowTemplate;
                    beginRowNum++;
                } else {
                    row = sheet.createRow(beginRowNum++);
                    row.setHeight(height);
                }
                setRowDatas(row, sheetDatas.get(rowDataNum), beginColumnNum, rowTemplate);
            }
        }
    }

    /**
     * 填充每个sheet数据（固定模板）
     */
    public static void setSheetDatas(XSSFSheet sheet, List<LinkedHashMap<String, Object>> sheetDatas, Cell signCell) {
        int beginRowNum = signCell.getRowIndex();
        int beginColumnNum = signCell.getColumnIndex();
        XSSFRow rowTemplate = sheet.getRow(beginRowNum);
        short height = rowTemplate.getHeight();
        for (LinkedHashMap<String, Object> sheetData : sheetDatas) {
            XSSFRow row = sheet.getRow(beginRowNum++);
            if (row != null) {
                row.setHeight(height);
                setRowDatas(row, sheetData, beginColumnNum, null);
            } else {
                log.warn("数据行数超出模板行数");
            }
        }
    }

    /**
     * 填充row数据
     */
    public static void setRowDatas(Row row, LinkedHashMap<String, Object> rowDatas, int beginColumnNum, XSSFRow rowTemplate) {
        int cellNum = 0;
        for (Iterator<Map.Entry<String, Object>> iterator = rowDatas.entrySet().iterator(); iterator.hasNext(); cellNum++) {
            Cell cell;
            Cell cellTemplate;
            String cellValue = iterator.next().getValue().toString();
            if (rowTemplate == null || row == rowTemplate) {
                cell = row.getCell(cellNum + beginColumnNum);
                if (cell == null) {
                    cell = row.createCell(cellNum + beginColumnNum);
                    log.warn("数据列数超出模板");
                }
            } else {
                cell = row.createCell(cellNum + beginColumnNum);
                if (rowTemplate.getLastCellNum() + 1 > cellNum + beginColumnNum) {
                    cellTemplate = rowTemplate.getCell(cellNum + beginColumnNum);
                } else {
                    cellTemplate = rowTemplate.getCell(1);
                }
                cell.setCellStyle(cellTemplate.getCellStyle());
            }
            try {
                BigDecimal bigDecimal = new BigDecimal(cellValue);
                double value = bigDecimal.doubleValue();
                cell.setCellValue(value);
            } catch (Exception e) {
                cell.setCellValue(Objects.requireNonNullElse(cellValue, ""));
            }
        }
    }

    /**
     * 传入指定列，根据单元格的值是否相同合并单元格
     */
    public static void mergeCell1(int[] columnNums) {
        for (int sheetNum = 0; sheetNum < workbook1.get().getNumberOfSheets(); sheetNum++) {
            Sheet sheet = workbook1.get().getSheetAt(sheetNum);
            Cell signCell = workBookSignCells.get().get(sheetNum).get(0);
            int beginRowIndex = signCell.getRowIndex();
            for (int columnNum : columnNums) {
                LinkedList<Cell> cells = new LinkedList<>();
                for (int rowNum = beginRowIndex; rowNum <= sheet.getLastRowNum(); rowNum++) {
                    Row row = sheet.getRow(rowNum);
                    cells.add(row.getCell(columnNum));
                }
                AtomicBoolean flag = new AtomicBoolean(false);
                for (AtomicInteger first = new AtomicInteger(0); first.get() < cells.size(); first.getAndIncrement()) {
                    flag.set(false);
                    Optional.ofNullable(cells.get(first.get())).ifPresent(firstCell -> {
                        String firstValue = String.valueOf(getCellValue(firstCell) == null ? "" : getCellValue(firstCell));
                        for (AtomicInteger second = new AtomicInteger(first.get() + 1); second.get() < cells.size(); second.getAndIncrement()) {
                            Optional.ofNullable(cells.get(second.get())).ifPresent(secondCell -> {
                                String secondValue = String.valueOf(getCellValue(secondCell) == null ? "" : getCellValue(secondCell));
                                if (firstValue.equals(secondValue)) {
                                    if (second.get() == cells.size() - 1)
                                        sheet.addMergedRegion(new CellRangeAddress(firstCell.getRowIndex(), secondCell.getRowIndex(), columnNum, columnNum));
                                    else flag.set(true);
                                } else {
                                    if (flag.get()) {
                                        sheet.addMergedRegion(new CellRangeAddress(firstCell.getRowIndex(), secondCell.getRowIndex() - 1, columnNum, columnNum));
                                        first.set(second.get() - 1);
                                        flag.set(false);
                                    }
                                }
                            });
                            if (!flag.get()) break;
                        }
                    });
                }
            }
        }
    }

    /**
     * 特殊行设置颜色
     */
//    public static void setBackgroundColorByRowsNum(XSSFSheet workbook, Map<Integer, Short> map) {
//        for (int rowNum : map.keySet()) {
//            XSSFRow row = workbook.getRow(rowNum);
//            for (Cell cell : row) {
//                XSSFCellStyle cellStyle = (XSSFCellStyle) ((XSSFCellStyle) cell.getCellStyle()).clone();
//                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//                cellStyle.setFillForegroundColor(map.get(rowNum));
//                cell.setCellStyle(cellStyle);
//            }
//        }
//    }

    /**
     * 自动判断单元格值类型获取单元格的值
     */
    public static Object getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
            case _NONE:
                return cell.getStringCellValue();
            case NUMERIC:
                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case BLANK:
            case FORMULA:
                log.error("当前单元格格式为公式型，坐标为:" + cell.getRowIndex() + "," + cell.getColumnIndex());
            case ERROR:
                log.error("当前单元格格式损坏，坐标为:" + cell.getRowIndex() + "," + cell.getColumnIndex());
        }
        return null;
    }


}
