package com.generator.myType;

import com.baomidou.mybatisplus.generator.config.rules.IColumnType;

public enum MyType implements IColumnType {
    BACKEND_SHEETS("List<BackendSheet>","java.util.List");


    private final String type;
    private final String pkg;

    MyType(final String type, final String pkg) {
        this.type = type;
        this.pkg = pkg;
    }

    public String getType() {
        return this.type;
    }

    public String getPkg() {
        return this.pkg;
    }

//    @Override
//    public String getType() {
//
//        return "List<BackendSheet>";
//    }
//
//    @Override
//    public String getPkg() {
//        return "import java.util.List;";
//    }
}
