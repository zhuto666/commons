package com.zhongqin.commons.util.compare;

import lombok.Data;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/11/27 9:45 星期三
 */
@Data
public class CompareNode {

    /**
     * 字段
     */
    private String fieldKey;

    /**
     * 字段值
     */
    private Object fieldValue;

    /**
     * 字段名称
     */
    private String fieldName;

}
