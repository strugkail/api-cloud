package com.fn.bi.report.generate.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.LinkedHashMap;
import java.util.List;

@DS("mysql")
public interface GenerateMapper {
    @Select("${sql}")
    List<LinkedHashMap<String, Object>> executeSql(@Param("sql") String sql);
}
