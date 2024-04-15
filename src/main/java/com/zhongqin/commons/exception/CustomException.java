package com.zhongqin.commons.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统自定义异常
 *
 * @author Kevin
 * @date 2020/9/10 14:53
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomException extends RuntimeException {

    /**
     * 状态码
     */
    private String code;

    /**
     * 信息
     */
    private String msg;

    /**
     * 信息参数
     */
    private String[] args;

    public CustomException(String msg) {
        super(msg);
        this.code = DefaultSystemErrorCode.SYSTEM_ERROR;
        this.msg = msg;
    }

    public CustomException(String msg, String args) {
        super(msg);
        this.code = DefaultSystemErrorCode.SYSTEM_ERROR;
        this.msg = msg;
        this.args = new String[]{args};
    }

    public CustomException(String msg, String... args) {
        super(msg);
        this.code = DefaultSystemErrorCode.SYSTEM_ERROR;
        this.msg = msg;
        this.args = args;
    }

}
