package com.zhongqin.commons.util.easypoi;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kevin
 * @date 2021/12/29 11:52
 * Excel 类型枚举
 */
@Getter
@AllArgsConstructor
public enum ExcelTypeEnum {

    /**
     * xls
     * 每个工作页中包含65535行(Row)和256列(Column)
     */
    XLS("xls"),

    /**
     * xlsx
     * 每个工作页包含1048576行(Row),16384列(Column)
     */
    X_LSX("xlsx");

    private String value;
}
