//package com.fn.bi.report.generate.util;
//
//
//import com.fn.bi.backend.common.model.BackendExcel;
//import com.fn.bi.backend.common.util.FileUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.xssf.usermodel.*;
//import org.springframework.util.StringUtils;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.math.BigDecimal;
//import java.util.*;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.stream.Collectors;
//
//@Slf4j
//public
//class ExcelWriteUtil1 {
//    private static final ThreadLocal<XSSFWorkbook> workbook1 = new ThreadLocal<>();
//    private static final ThreadLocal<AtomicInteger> rowNumCount = new ThreadLocal<>();
//    private static final ThreadLocal<Boolean> isMerge = new ThreadLocal<>();
//    private static final ThreadLocal<int[]> mergeCols = new ThreadLocal<>();
//
//    /**
//     * 对外提供统一调用接口
//     */
//    public static String writeReport(BackendExcel backendExcel) {
//        try {
//            loadTemplate(backendExcel.getTemplatePath());
//            List<List<List<LinkedHashMap<String, Object>>>> data = (List<List<List<LinkedHashMap<String, Object>>>>) backendExcel.getData();
//            rowNumCount.set(new AtomicInteger(0));
//            setExcel(data);
//            writeExcel(backendExcel.getTargetFileName());
//            return "生成成功！";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "生成失败！";
//        } finally {
//            workbook1.remove();
//            rowNumCount.remove();
//            isMerge.remove();
//            mergeCols.remove();
//        }
//    }
//
//    /**
//     * 读取模板
//     */
//    public static void loadTemplate(String path) {
//        try (FileInputStream fileInputStream = new FileInputStream(path)) {
//            workbook1.set(new XSSFWorkbook(fileInputStream));
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("未找到模板");
//        }
//    }
//
//    /**
//     * 写出excel
//     */
//    private static void writeExcel(String path) {
//        FileUtil.mkdir(path);
//        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(path))) {
//            workbook1.get().write(fileOutputStream);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 自动判断单元格值类型获取单元格的值
//     */
//    private static Object getValue(Cell cell) {
//        switch (cell.getCellType()) {
//            case STRING:
//            case _NONE:
//                return cell.getStringCellValue();
//            case NUMERIC:
//                return cell.getNumericCellValue();
//            case BOOLEAN:
//                return cell.getBooleanCellValue();
//            case BLANK:
//            case FORMULA:
//                log.error("当前单元格格式为公式型，坐标为:" + cell.getRowIndex() + "," + cell.getColumnIndex());
//            case ERROR:
//                log.error("当前单元格格式损坏，坐标为:" + cell.getRowIndex() + "," + cell.getColumnIndex());
//        }
//        return null;
//    }
//
//    //遍历excel
//    private static void setExcel(List<List<List<LinkedHashMap<String, Object>>>> excelData) {
//        XSSFWorkbook workBook = workbook1.get();
//        ArrayList<String> sheetNames = new ArrayList<>();
//        //遍历所有数据，取出每个sheet的数据
//        for (int i = 0; i < excelData.size(); i++) {
//            XSSFSheet targetSheet = workBook.createSheet(i + "");
//            setSheet(excelData.get(i), workbook1.get().getSheetAt(i), targetSheet);
//            sheetNames.add(workbook1.get().getSheetAt(i).getSheetName());
//            rowNumCount.get().set(0);
//            if (isMerge.get()) {
//                mergeCell1(mergeCols.get());
//            }
//            isMerge.set(Boolean.FALSE);
//        }
//
//        for (int i = 0; i < excelData.size(); i++) {
//            workbook1.get().removeSheetAt(0);
//        }
//
//        for (int i = 0; i < excelData.size(); i++) {
//            workbook1.get().setSheetName(i, sheetNames.get(i));
//        }
//
//    }
//
//    //处理sheet
//    private static void setSheet(List<List<LinkedHashMap<String, Object>>> sheetData, XSSFSheet sourceSheet, XSSFSheet targetSheet) {
//        //解析模板，获取数据块，返回一个list装着row对象
//        ArrayList<List<Row>> signBodys = parsingSheet(sourceSheet);
//        //根据标记判断，有几个数据体
//        for (int i = 0; i < sheetData.size(); i++) {
//            //数据块，数据块模板
//            setBody(sheetData.get(i), signBodys.get(i), sourceSheet, targetSheet);
//            XSSFRow row = targetSheet.createRow(rowNumCount.get().getAndIncrement());
//            row.createCell(0).setCellValue("");
//        }
//    }
//
//    //处理数据体
//    private static void setBody(List<LinkedHashMap<String, Object>> bodyData, List<Row> signBody, XSSFSheet sourceSheet, XSSFSheet targetSheet) {
//        int beginRowNum = 0;
//        int beginColNum = 0;
//        boolean isSpilt = false;
//        StringBuilder spiltKey = new StringBuilder();
//        for (int i = 0; i < signBody.size(); i++) {
//            Row row = targetSheet.createRow(rowNumCount.get().getAndIncrement());
//            copyRows1(sourceSheet, targetSheet, signBody.get(i).getRowNum(), row.getRowNum());
//            for (Cell cell : signBody.get(i)) {
//                String value = getValue(cell) == null ? null : Objects.requireNonNull(getValue(cell)).toString();
//                if (StringUtils.isEmpty(value)) continue;
//                if (value.contains("data")) {
//                    if (value.contains("merge")) {
//                        String[] split = value.substring(value.indexOf("(") + 1, value.indexOf(")")).split(",");
//                        int[] inits = new int[split.length];
//                        for (int j = 0; j < split.length; j++) {
//                            inits[j] = Integer.parseInt(split[j]);
//                        }
//                        mergeCols.set(inits);
//                        isMerge.set(Boolean.TRUE);
//                    }
//                    beginRowNum = i;
//                    beginColNum = cell.getColumnIndex();
//                }
//                if (value.contains("spilt")) {
//                    beginRowNum = i;
//                    beginColNum = cell.getColumnIndex();
//                    isSpilt = true;
//                    spiltKey.append(value.split(":")[1]);
//                    targetSheet.createFreezePane(0, 1, 0, 1);
//                }
//            }
//        }
//
//        Row sourceRow = signBody.get(beginRowNum);
//        rowNumCount.get().set(rowNumCount.get().get() - beginRowNum);
//
//
//        if (isSpilt) {
//            LinkedHashMap<String, List<LinkedHashMap<String, Object>>> spiltData = bodyData.stream().collect(Collectors.groupingBy(map -> map.get(spiltKey.toString()).toString(), LinkedHashMap::new, Collectors.toList()));
//            Iterator<String> iterator = spiltData.keySet().iterator();
//            for (int i = 0; iterator.hasNext(); i++) {
//                if (i > 0) {
//                    for (int j = 0; j < signBody.size(); j++) {
//                        Row row = targetSheet.createRow(rowNumCount.get().getAndIncrement());
//                        copyRows1(sourceSheet, targetSheet, signBody.get(j).getRowNum(), row.getRowNum());
//                    }
//                }
//                List<LinkedHashMap<String, Object>> list = spiltData.get(iterator.next());
//                for (LinkedHashMap<String, Object> rowData : list) {
//                    setRow(rowData, sourceRow, targetSheet, beginColNum);
//                }
//                XSSFRow row = targetSheet.createRow(rowNumCount.get().getAndIncrement());
//                row.createCell(0).setCellValue(" ");
//            }
//
//            //切分数据
////            TreeMap<String, List<LinkedHashMap<String, Object>>> spiltData = bodyData.stream().collect(Collectors.groupingBy(map -> map.get(spiltKey.toString()).toString(),TreeMap::new,Collectors.toList()));
////            for (String s : spiltData.keySet()) {
////                List<LinkedHashMap<String, Object>> list = spiltData.get(s);
////                for (LinkedHashMap<String, Object> rowData : list) {
////                    setRow(rowData, sourceRow, targetSheet, beginColNum);
////                }
////                targetSheet.createRow(rowNumCount.get().getAndIncrement());
////            }
//        } else {
//            for (LinkedHashMap<String, Object> rowData : bodyData) {
//                setRow(rowData, sourceRow, targetSheet, beginColNum);
//            }
//        }
//        for (Cell cell : sourceRow) {
//            targetSheet.setColumnWidth(cell.getColumnIndex(), sourceSheet.getColumnWidth(cell.getColumnIndex()));
//        }
//    }
//
//    //处理行
//    private static void setRow(LinkedHashMap<String, Object> rowData, Row sourceRow, XSSFSheet targetSheet, int beginColNum) {
//        int colOffset = 0;
//        boolean flag = false;
//        //创建行对象
//        XSSFRow row = targetSheet.createRow(rowNumCount.get().getAndIncrement());
//        row.setHeight(sourceRow.getHeight());
//        for (Iterator<Map.Entry<String, Object>> iterator = rowData.entrySet().iterator(); iterator.hasNext(); colOffset++) {
//            XSSFCell cell = row.createCell(beginColNum + colOffset);
//            Cell sourceCell = sourceRow.getCell(colOffset);
//            String valueStr = iterator.next().getValue().toString();
//            setCellValue(cell, valueStr);
//            if (sourceCell == null)
//                System.out.println("sss");
//            cell.setCellStyle(sourceCell.getCellStyle());
//            setCellType(cell, sourceCell);
//
//            if (valueStr.equals("合计") || valueStr.equals("总计") || flag) {
//                XSSFFont font = workbook1.get().createFont();
//                font.setBold(true);
//                XSSFCellStyle cellStyle = workbook1.get().createCellStyle();
//                cellStyle.cloneStyleFrom(sourceCell.getCellStyle());
//                cellStyle.setFont(font);
//                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//                cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 240, 225, 255)));
//                cell.setCellStyle(cellStyle);
//                flag = true;
//            }
//
//        }
//    }
//
//    private static void setCellValue(Cell cell, String valueStr) {
//        try {
//            BigDecimal bigDecimal = new BigDecimal(valueStr);
//            double value = bigDecimal.doubleValue();
//            cell.setCellValue(value);
//        } catch (Exception e) {
//            cell.setCellValue(valueStr);
//        }
//    }
//
//    private static void copyRows1(Sheet sourceSheet, Sheet targetSheet, int sourceRowNum, int targetRowNum) {
//        if ((sourceRowNum == -1) || (targetRowNum == -1)) {
//            return;
//        }
//        Row sourceRow = sourceSheet.getRow(sourceRowNum);
//        if (sourceRow == null) return;
//
//        for (Cell cell : sourceRow) {
//            String value = getValue(cell) == null ? null : Objects.requireNonNull(getValue(cell)).toString();
//            if (!StringUtils.isEmpty(value) && value.contains("data")) {
//                return;
//            }
//        }
//
//        Row targetRow = targetSheet.createRow(targetRowNum);
//        targetRow.setHeight(sourceRow.getHeight());
//
//
//        List<CellRangeAddress> oldRanges = new ArrayList<CellRangeAddress>();
//        for (int i = 0; i < sourceSheet.getNumMergedRegions(); i++) {
//            oldRanges.add(sourceSheet.getMergedRegion(i));
//        }
//        for (CellRangeAddress oldRange : oldRanges) {
//            CellRangeAddress newRange = new CellRangeAddress(oldRange.getFirstRow(), oldRange.getLastRow(),
//                    oldRange.getFirstColumn(), oldRange.getLastColumn());
//
//            if (oldRange.getFirstRow() >= sourceRowNum && oldRange.getLastRow() <= targetRowNum) {
//                int targetRowFrom = oldRange.getFirstRow() - sourceRowNum;
//                int targetRowTo = oldRange.getLastRow() - sourceRowNum;
//                oldRange.setFirstRow(targetRowFrom);
//                oldRange.setLastRow(targetRowTo);
//                targetSheet.addMergedRegion(oldRange);
//                sourceSheet.addMergedRegion(newRange);
//            }
//        }
//
//        for (Cell cell : sourceRow) {
//            targetSheet.setColumnWidth(cell.getColumnIndex(), sourceSheet.getColumnWidth(cell.getColumnIndex()));
//        }
//
//        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
//            Cell sourceCell = sourceRow.getCell(i);
//            Cell targetCell = targetRow.createCell(i);
//            targetCell.setCellStyle(sourceCell.getCellStyle());
//            setCellType(sourceCell, targetCell);
//        }
//
//    }
//
//    private static void setCellType(Cell sourceCell, Cell targetCell) {
//        CellType cType = sourceCell.getCellType();
//        targetCell.setCellType(cType);
//        switch (cType.getCode()) {
//            case 4:
//                targetCell.setCellValue(sourceCell.getBooleanCellValue());
//                break;
//            case 5:
//                targetCell.setCellValue(sourceCell.getErrorCellValue());
//                break;
//            case 2:
//                targetCell.setCellValue(sourceCell.getCellFormula());
//                break;
//            case 0:
//                targetCell.setCellValue(sourceCell.getNumericCellValue());
//                break;
//            case 1:
//                targetCell.setCellValue(sourceCell.getRichStringCellValue());
//                break;
//        }
//    }
//
//    /**
//     * 传入指定列，根据单元格的值是否相同合并单元格
//     */
//    private static void mergeCell1(int[] columnNums) {
//        Sheet sheet = workbook1.get().getSheetAt(workbook1.get().getNumberOfSheets() - 1);
//        int beginRowIndex = 0;
//        for (int columnNum : columnNums) {
//            LinkedList<Object> cells = new LinkedList<>();
//            for (int rowNum = beginRowIndex; rowNum <= sheet.getLastRowNum(); rowNum++) {
//                Cell cell = sheet.getRow(rowNum).getCell(columnNum);
//                if (cell == null) {
//                    cells.add(rowNum);
//                } else {
//                    cells.add(cell);
//                }
//            }
//            AtomicBoolean flag = new AtomicBoolean(false);
//            for (AtomicInteger first = new AtomicInteger(0); first.get() < cells.size(); first.getAndIncrement()) {
//                flag.set(false);
//                Optional.ofNullable(cells.get(first.get())).ifPresent(f -> {
//                    if (!(f instanceof Cell)) return;
//                    Cell firstCell = (Cell) f;
//                    String firstValue = getValue(firstCell).toString();
//                    for (AtomicInteger second = new AtomicInteger(first.get() + 1); second.get() < cells.size(); second.getAndIncrement()) {
//                        Optional.ofNullable(cells.get(second.get())).ifPresent(s -> {
//                            if (s instanceof Integer) {
//                                if (flag.get()) {
//                                    sheet.addMergedRegion(new CellRangeAddress(firstCell.getRowIndex(), second.get() - 1, columnNum, columnNum));
//                                    first.set(second.get() - 1);
//                                    flag.set(false);
//                                    return;
//                                }
//                                return;
//                            }
//                            Cell secondCell = (Cell) s;
//                            String secondValue = getValue(secondCell).toString();
//                            if (firstValue.equals(secondValue)) {
//                                if (second.get() == cells.size() - 1)
//                                    sheet.addMergedRegion(new CellRangeAddress(firstCell.getRowIndex(), secondCell.getRowIndex(), columnNum, columnNum));
//                                else flag.set(true);
//                            } else {
//                                if (flag.get()) {
//                                    sheet.addMergedRegion(new CellRangeAddress(firstCell.getRowIndex(), secondCell.getRowIndex() - 1, columnNum, columnNum));
//                                    first.set(second.get() - 1);
//                                    flag.set(false);
//                                }
//                            }
//                        });
//                        if (!flag.get()) break;
//                    }
//                });
//            }
//        }
//    }
//
//
//    void test() {
////        rowNumCount.set(new AtomicInteger(0));
////        //第一个sheet
////        List<LinkedHashMap<String, Object>> all = mapper.getAll();
////        List<List<LinkedHashMap<String, Object>>> sheet1Data = new ArrayList<>();
////        sheet1Data.add(all);
////        //第二个sheet
////        List<LinkedHashMap<String, Object>> one = mapper.getOne();
////        List<LinkedHashMap<String, Object>> two = mapper.getTwo();
////        List<List<LinkedHashMap<String, Object>>> sheet2Data = new ArrayList<>();
////        sheet2Data.add(one);
////        sheet2Data.add(two);
////
////        List<List<List<LinkedHashMap<String, Object>>>> dds = new ArrayList<>();
////        dds.add(sheet1Data);
////        dds.add(sheet2Data);
////
////        loadTemplate("C:\\Users\\yufeng.gong\\Desktop\\小润发业绩日报.xlsx");
////        setExcel(dds);
////        writeExcel("C:\\Users\\yufeng.gong\\Desktop\\cccccccccccccccc.xlsx");
//    }
//
//    private static ArrayList<List<Row>> parsingSheet(Sheet sourceSheet) {
//        boolean flag = false;
//        ArrayList<List<Row>> bodys = new ArrayList<>();
//        List<Row> body = null;
//        for (Row row : sourceSheet) {
//            for (Cell cell : row) {
//                String value = getValue(cell) == null ? null : Objects.requireNonNull(getValue(cell)).toString();
//                if (StringUtils.isEmpty(value)) continue;
//                if (value.equals("{")) {
//                    flag = true;
//                    body = new ArrayList<>();
//                }
//                if (value.equals("}")) {
//                    flag = false;
//                    assert body != null;
//                    body.remove(0);
//                    bodys.add(body);
//                }
//            }
//            if (flag) {
//                body.add(row);
//            }
//        }
//        return bodys;
//    }
//
//
//}
