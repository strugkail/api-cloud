package com.fn.bi.report.generate.model;

import lombok.Data;

import java.util.List;

@Data
public class Style {
    private String spiltKey;    //根据哪个字段切割
    private List<RowStyle> rowStyles;   //需要修改样式的特殊行
    private List<Integer> merge;    //需要合并的列号，下标从零开始
    private Boolean freezeRow;      //冻结首行
    private Boolean freezeCol;      //冻结首列
}
