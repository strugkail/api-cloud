package com.fn.bi.backend.common.ienum;

public enum Str {
    dds("dds"),
    sort_no("sort_no"),
    number_1("1"),
    number_0("0"),
    _r_n("\r\n"),
    blank(" "),
    l("/"),
    s("."),
    d(","),
    noData("No data"),
    end("<end>"),//SQL和title分隔符
    d_t("#d-t"),
    m_t("#m-t"),
    mt("#mt"),
    dt("#dt"),
    week("#week"),
    firstDay("#firstDay"),
    d_t_h_m("#d-t_h-m"),
    db("#db"),
    xlsx(".xlsx");


    public String val;

    Str(String identifier) {
        this.val = identifier;
    }
}
