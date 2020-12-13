package com.fn.bi.report.generate.model;

import lombok.Data;

@Data
public class Font {
    private String fontName;    //字体
    private Integer fontSize;   //字体大小
    private Boolean isBold;     //是否加粗
    private String fontColor;   //字体颜色
}
