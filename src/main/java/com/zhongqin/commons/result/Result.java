package com.zhongqin.commons.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zhongqin.commons.exception.DefaultSystemErrorCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.Instant;
import java.time.ZonedDateTime;

/**
 * 统一返回结果
 *
 * @author Kevin
 * @date 2020/4/29 13:49
 */
@ApiModel(description = "rest请求的返回模型，所有rest正常都返回该类的对象")
@Getter
public class Result<T> {

    public static final String SUCCESSFUL_CODE = DefaultSystemErrorCode.SUCCESS;
    public static final String SUCCESSFUL_MSG = DefaultSystemErrorCode.SUCCESS;

    @ApiModelProperty(value = "处理结果code", required = true)
    private String code;

    @ApiModelProperty(value = "处理结果描述信息")
    private String msg;

    @ApiModelProperty(value = "请求结果生成时间戳")
    private Instant time;

    @ApiModelProperty(value = "处理结果数据信息")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @ApiModelProperty(value = "处理结果List总数信息")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer count;

    /**
     * 初始化
     */
    public Result() {
        this.time = ZonedDateTime.now().toInstant();
    }

    /**
     * 内部使用，用于构造成功的结果
     *
     * @param code
     * @param msg
     * @param data
     */
    private Result(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.time = ZonedDateTime.now().toInstant();
    }

    /**
     * 内部使用，用于构造成功的结果
     *
     * @param code
     * @param msg
     * @param data
     */
    private Result(String code, String msg, Integer count, T data) {
        this.code = code;
        this.msg = msg;
        this.count = count;
        this.data = data;
        this.time = ZonedDateTime.now().toInstant();
    }

    /**
     * 快速创建成功结果并返回结果数据
     *
     * @param data
     * @return Result
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESSFUL_CODE, SUCCESSFUL_MSG, data);
    }


    /**
     * 快速创建成功结果并返回结果数据
     *
     * @param data
     * @return Result
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(SUCCESSFUL_CODE, msg, data);
    }

    /**
     * 快速创建成功结果
     *
     * @return Result
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 快速创建成功结果并返回结果数据(表格)
     *
     * @param count
     * @param data
     * @return
     */
    public static <T> Result<T> success(Integer count, T data) {
        return new Result<>(SUCCESSFUL_CODE, SUCCESSFUL_MSG, count, data);
    }


    /**
     * 系统异常类没有返回数据
     *
     * @return Result
     */
    public static <T> Result<T> fail(String msg) {
        return new Result<T>(DefaultSystemErrorCode.SYSTEM_ERROR, msg, null);
    }

    /**
     * 系统异常类并返回结果数据
     *
     * @param data
     * @param msg
     * @return
     */
    public static <T> Result<T> fail(T data, String msg) {
        return new Result<>(DefaultSystemErrorCode.SYSTEM_ERROR, msg, data);
    }

    /**
     * 系统异常类并没有返回数据
     *
     * @param code
     * @param msg
     * @return
     */
    public static <T> Result<T> fail(String code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * Excel导入表格返回结果
     *
     * @param code
     * @param msg
     * @param data
     * @return Result
     */
    public static <T> Result<T> success(String code, String msg, T data) {
        return new Result<>(code, msg, data);
    }
}
