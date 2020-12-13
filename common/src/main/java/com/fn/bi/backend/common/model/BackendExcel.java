package com.fn.bi.backend.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class BackendExcel<T> {
    private String[][] titles;
    private T data;
    private String targetFileName;
    private String templatePath;
    private String reportForm;
}
