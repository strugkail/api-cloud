package com.fn.bi.backend.common.ienum;


public enum GenerateType {
    adb("adb"),
    vertical("vertical");

    public String val;

    GenerateType(String val){
        this.val = val;
    }
}
