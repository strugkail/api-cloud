package com.fn.bi.report.generate.model;

import lombok.Data;

@Data
public class RowStyle {
    private String rowKey;  //特殊行关键字
    private Font font;      //修改的字体
    private String color;   //单元格背景色
}
