package com.zhongqin.commons.handler;

import com.zhongqin.commons.exception.CustomException;
import com.zhongqin.commons.result.Result;
import com.zhongqin.commons.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Kevin
 * @date 2020/9/10 14:23
 */
@Slf4j
public class DefaultExceptionHandler {

    /**
     * 全局异常捕捉处理
     */
    @ExceptionHandler(value = Exception.class)
    public Result errorHandler(Exception ex) {
        log.error("exception:{}", ExceptionUtil.getStackTrace(ex));
        return Result.fail(ExceptionUtil.getStackTrace(ex));
    }

    /**
     * 自定义异常 CustomException.class
     */
    @ExceptionHandler(value = CustomException.class)
    public Result httpResult(CustomException ex) {
        log.error("CustomException:{}", ex.getMsg());
        return Result.fail(ex.getMsg());
    }

}
