package com.fn.bi.report.generate.util;


import com.fn.bi.backend.common.model.BackendExcel;
import com.fn.bi.backend.common.model.MsgException;
import com.fn.bi.backend.common.util.FileUtil;
import com.fn.bi.report.generate.model.Font;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class ExcelWriteUtil2 {
    private static final ThreadLocal<XSSFWorkbook> workbook = new ThreadLocal<>();
    private static final ThreadLocal<AtomicInteger> rowNumCount = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> isMerge = new ThreadLocal<>();
    private static final ThreadLocal<int[]> mergeCols = new ThreadLocal<>();


    /**
     * 对外提供统一调用接口
     */
    public static void writeReport(BackendExcel<?> backendExcel) {
        try {
            loadTemplate(backendExcel.getTemplatePath());
            List<List<List<LinkedHashMap<String, Object>>>> data = (List<List<List<LinkedHashMap<String, Object>>>>) backendExcel.getData();
            rowNumCount.set(new AtomicInteger(0));
            setExcel(data, backendExcel.getTitles());
            writeExcel(backendExcel.getTargetFileName());
        } finally {
            workbook.remove();
            rowNumCount.remove();
            isMerge.remove();
            mergeCols.remove();
        }
    }

    /**
     * 读取模板
     */
    public static void loadTemplate(String path) {
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            workbook.set(new XSSFWorkbook(fileInputStream));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("未找到模板");
        }
    }

    /**
     * 写出excel
     */
    private static void writeExcel(String path) {
        FileUtil.mkdir(path);
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(path))) {
            workbook.get().write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MsgException("写出路径无效", e.getCause());
        }
    }

    /**
     * 自动判断单元格值类型获取单元格的值
     */
    public static Object getValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
            case _NONE:
                return cell.getStringCellValue();
            case NUMERIC:
                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case BLANK:
                log.error("当前单元为null，坐标为:" + cell.getRowIndex() + "," + cell.getColumnIndex());
                break;
            case FORMULA:
                log.error("当前单元格格式为公式型，坐标为:" + cell.getRowIndex() + "," + cell.getColumnIndex());
                break;
            case ERROR:
                log.error("当前单元格格式损坏，坐标为:" + cell.getRowIndex() + "," + cell.getColumnIndex());
        }
        return null;
    }

    //遍历所有sheet
    public static void setExcel(List<List<List<LinkedHashMap<String, Object>>>> excelData, String[][] titles) {
        ArrayList<String> sheetNames = new ArrayList<>();
        //遍历所有数据，取出每个sheet的数据
        for (int i = 0; i < excelData.size(); i++) {
            XSSFSheet targetSheet = workbook.get().createSheet(i + "");
            setSheet(excelData.get(i), workbook.get().getSheetAt(i), targetSheet, titles[i]);
            sheetNames.add(workbook.get().getSheetAt(i).getSheetName());
            rowNumCount.get().set(0);
            if (isMerge.get() != null && isMerge.get()) {
                mergeCell1(mergeCols.get());
            }
            isMerge.set(Boolean.FALSE);
        }

        for (int i = 0; i < excelData.size(); i++) {
            workbook.get().removeSheetAt(0);
        }

        for (int i = 0; i < excelData.size(); i++) {
            workbook.get().setSheetName(i, sheetNames.get(i));
        }

    }

    //处理sheet
    public static void setSheet(List<List<LinkedHashMap<String, Object>>> sheetData, XSSFSheet sourceSheet, XSSFSheet targetSheet, String[] title) {
        //解析模板，获取数据块，返回一个list装着row对象
        ArrayList<List<Row>> signBodies = parsingSheet(sourceSheet, title);
        //根据标记判断，有几个数据体
        for (int i = 0; i < sheetData.size(); i++) {
            //数据块，数据块模板
            setBody(sheetData.get(i), signBodies.get(i), sourceSheet, targetSheet);
            XSSFRow row = targetSheet.createRow(rowNumCount.get().getAndIncrement());
            row.createCell(0).setCellValue("");
        }
    }

    //处理数据体
    public static void setBody(List<LinkedHashMap<String, Object>> bodyData, List<Row> signBody, XSSFSheet sourceSheet, XSSFSheet targetSheet) {
        int beginRowNum = 0;
        int beginColNum = 0;
        boolean isSpilt = false;
        StringBuilder spiltKey = new StringBuilder();
        for (int i = 0; i < signBody.size(); i++) {
            copyRows(sourceSheet, targetSheet, signBody.get(i).getRowNum());
            for (Cell cell : signBody.get(i)) {
                String value = String.valueOf(getValue(cell) == null ? "" : getValue(cell));
                if (StringUtils.isEmpty(value)) continue;
                if (value.contains("data")) {
                    if (value.contains("merge")) {
                        String[] split = value.substring(value.indexOf("(") + 1, value.indexOf(")")).split(",");
                        int[] inits = new int[split.length];
                        for (int j = 0; j < split.length; j++) {
                            inits[j] = Integer.parseInt(split[j]);
                        }
                        mergeCols.set(inits);
                        isMerge.set(Boolean.TRUE);
                    }
                    beginRowNum = i;
                    beginColNum = cell.getColumnIndex();
                }
                if (value.contains("spilt")) {
                    beginRowNum = i;
                    beginColNum = cell.getColumnIndex();
                    isSpilt = true;
                    spiltKey.append(value.split(":")[1]);
                    targetSheet.createFreezePane(0, 1, 0, 1);
                }
            }
        }

        Row sourceRow = signBody.get(beginRowNum);

        if (isSpilt) {
            //切分数据
            LinkedHashMap<String, List<LinkedHashMap<String, Object>>> spiltData = bodyData.stream().collect(Collectors.groupingBy(map -> map.get(spiltKey.toString()).toString(), LinkedHashMap::new, Collectors.toList()));
            Iterator<String> iterator = spiltData.keySet().iterator();
            for (int i = 0; iterator.hasNext(); i++) {
                if (i > 0) {
                    for (int j = 0; j < signBody.size() - 1; j++) {
                        rowNumCount.get().getAndIncrement();
                        copyRows(sourceSheet, targetSheet, signBody.get(j).getRowNum());
                    }
                }
                List<LinkedHashMap<String, Object>> list = spiltData.get(iterator.next());
                for (LinkedHashMap<String, Object> rowData : list) {
                    setRow(rowData, sourceRow, targetSheet, beginColNum);
                }
                XSSFRow row = targetSheet.createRow(rowNumCount.get().get());
                row.createCell(0).setCellValue(" ");
            }
        } else {
            for (LinkedHashMap<String, Object> rowData : bodyData) {
                setRow(rowData, sourceRow, targetSheet, beginColNum);
            }
        }
        for (Cell cell : sourceRow) {
            targetSheet.setColumnWidth(cell.getColumnIndex(), sourceSheet.getColumnWidth(cell.getColumnIndex()));
        }
    }

    //处理行
    public static void setRow(LinkedHashMap<String, Object> rowData, Row sourceRow, XSSFSheet targetSheet, int beginColNum) {
        int colOffset = 0;
        boolean totalFlag = false;
        boolean enFlag = false;
        boolean mnFlag = false;
        //创建行对象
        XSSFRow row = targetSheet.createRow(rowNumCount.get().getAndIncrement());
        row.setHeight(sourceRow.getHeight());
        for (Iterator<Map.Entry<String, Object>> iterator = rowData.entrySet().iterator(); iterator.hasNext(); colOffset++) {
            XSSFCell cell = row.createCell(beginColNum + colOffset);
            Cell sourceCell = sourceRow.getCell(colOffset);
            String valueStr = iterator.next().getValue().toString();
            setCellValue(cell, valueStr);
            cell.setCellStyle(sourceCell.getCellStyle());
            if (valueStr.equals("合计") || valueStr.equals("总计") || totalFlag) {
                XSSFFont font = workbook.get().createFont();
                XSSFCellStyle cellStyle = workbook.get().createCellStyle();
                font.setBold(true);
                cellStyle.cloneStyleFrom(sourceCell.getCellStyle());
                cellStyle.setFont(font);
                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 240, 225, 255)));
                cell.setCellStyle(cellStyle);
                totalFlag = true;
            }
            if (valueStr.equals("EN") || enFlag) {
                XSSFFont font = workbook.get().createFont();
                XSSFCellStyle cellStyle = workbook.get().createCellStyle();
                cellStyle.cloneStyleFrom(sourceCell.getCellStyle());
                cellStyle.setFont(font);
                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 255, 0, 255)));
                cell.setCellStyle(cellStyle);
                enFlag = true;
            }
            if (valueStr.equals("MN") || mnFlag) {
                XSSFFont font = workbook.get().createFont();
                XSSFCellStyle cellStyle = workbook.get().createCellStyle();
                cellStyle.cloneStyleFrom(sourceCell.getCellStyle());
                cellStyle.setFont(font);
                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(128, 128, 128, 255)));
                cell.setCellStyle(cellStyle);
                mnFlag = true;
            }
            setCellType(cell, sourceCell);

        }
    }

    public static void setCellValue(Cell cell, String valueStr) {
        try {
            BigDecimal bigDecimal = new BigDecimal(valueStr);
            double value = bigDecimal.doubleValue();
            cell.setCellValue(value);
        } catch (Exception e) {
            cell.setCellValue(valueStr);
        }
    }

    public static void copyRows(Sheet sourceSheet, Sheet targetSheet, int sourceRowNum) {
        Row sourceRow = sourceSheet.getRow(sourceRowNum);
        if (sourceRow == null) return;

        for (Cell cell : sourceRow) {
            String value = String.valueOf(getValue(cell) == null ? "" : getValue(cell));
            if (!StringUtils.isEmpty(value) && (value.contains("data") || value.contains("spilt"))) {
                return;
            }
        }

        int targetRowNum = rowNumCount.get().get();

//        getAndIncrement()
        Row targetRow = targetSheet.createRow(targetRowNum);
        targetRow.setHeight(sourceRow.getHeight());

        List<CellRangeAddress> oldRanges = new ArrayList<>();
        for (int i = 0; i < sourceSheet.getNumMergedRegions(); i++) {
            oldRanges.add(sourceSheet.getMergedRegion(i));
        }
        for (CellRangeAddress oldRange : oldRanges) {
            if (oldRange.getFirstRow() == sourceRowNum) {
                CellRangeAddress newRange = new CellRangeAddress(targetRowNum, targetRowNum,
                        oldRange.getFirstColumn(), oldRange.getLastColumn());
                targetSheet.addMergedRegion(newRange);
            }
        }

        for (Cell cell : sourceRow) {
            targetSheet.setColumnWidth(cell.getColumnIndex(), sourceSheet.getColumnWidth(cell.getColumnIndex()));
        }

        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            Cell sourceCell = sourceRow.getCell(i);
            Cell targetCell = targetRow.createCell(i);
            targetCell.setCellStyle(sourceCell.getCellStyle());
            setCellType(sourceCell, targetCell);
        }
        rowNumCount.get().getAndIncrement();
    }

    private static void setCellType(Cell sourceCell, Cell targetCell) {
        CellType cType = sourceCell.getCellType();
        targetCell.setCellType(cType);
        switch (cType.getCode()) {
            case 4:
                targetCell.setCellValue(sourceCell.getBooleanCellValue());
                break;
            case 5:
                targetCell.setCellValue(sourceCell.getErrorCellValue());
                break;
            case 2:
                targetCell.setCellValue(sourceCell.getCellFormula());
                break;
            case 0:
                targetCell.setCellValue(sourceCell.getNumericCellValue());
                break;
            case 1:
                targetCell.setCellValue(sourceCell.getRichStringCellValue());
                break;
        }
    }

    /**
     * 传入指定列，根据单元格的值是否相同合并单元格
     */
    public static void mergeCell1(int[] columnNums) {
        Sheet sheet = workbook.get().getSheetAt(workbook.get().getNumberOfSheets() - 1);
        int beginRowIndex = 0;
        for (int columnNum : columnNums) {
            LinkedList<Object> cells = new LinkedList<>();
            for (int rowNum = beginRowIndex; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Cell cell = sheet.getRow(rowNum).getCell(columnNum);
                cells.add(Objects.requireNonNullElseGet(cell, Font::new));
            }
            AtomicBoolean flag = new AtomicBoolean(false);
            for (AtomicInteger first = new AtomicInteger(0); first.get() < cells.size(); first.getAndIncrement()) {
                flag.set(false);
                Optional.ofNullable(cells.get(first.get())).ifPresent(f -> {
                    if (!(f instanceof Cell)) return;
                    Cell firstCell = (Cell) f;
                    String firstValue = getValue(firstCell) == null ? "" : String.valueOf(getValue(firstCell) == null ? "" : getValue(firstCell));
                    for (AtomicInteger second = new AtomicInteger(first.get() + 1); second.get() < cells.size(); second.getAndIncrement()) {
                        Optional.ofNullable(cells.get(second.get())).ifPresent(s -> {
                            if (s instanceof Font) {
                                if (flag.get()) {
                                    sheet.addMergedRegion(new CellRangeAddress(firstCell.getRowIndex(), second.get() - 1, columnNum, columnNum));
                                    first.set(second.get() - 1);
                                    flag.set(false);
                                    return;
                                }
                                return;
                            }
                            Cell secondCell = (Cell) s;
                            String secondValue = getValue(secondCell) == null ? "" : String.valueOf(getValue(secondCell) == null ? "" : getValue(secondCell));
                            assert firstValue != null;
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


    public static ArrayList<List<Row>> parsingSheet(Sheet targetSheet, String[] title) {
        int i = 0;
        boolean flag = false;
        ArrayList<List<Row>> bodies = new ArrayList<>();
        List<Row> body = null;
        for (Row row : targetSheet) {
            for (Cell cell : row) {
                String value = String.valueOf(getValue(cell) == null ? "" : getValue(cell));
                if (StringUtils.isEmpty(value)) continue;
                if (value.equals("{")) {
                    flag = true;
                    body = new ArrayList<>();
                }
                if (value.equals("}")) {
                    flag = false;
                    assert body != null;
                    body.remove(0);
                    bodies.add(body);
                }
                if (value.contains("#title")) {
                    String titleStr = StringUtils.isEmpty(title[i]) ? "" : title[i];
                    cell.setCellValue(titleStr);
                    i++;
                }
            }
            if (flag) body.add(row);
        }
        return bodies;
    }


}
