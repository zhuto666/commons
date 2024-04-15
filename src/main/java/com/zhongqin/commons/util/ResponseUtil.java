package com.zhongqin.commons.util;

import com.alibaba.fastjson.JSON;
import com.zhongqin.commons.result.Result;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Request,Response工具类
 *
 * @author Kevin
 * @date 2020/2/26 12:56
 */
public class ResponseUtil {

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : requestAttributes.getRequest();
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : requestAttributes.getResponse();
    }

    /**
     * 使用response输出JSON返回前台
     *
     * @param result
     */
    public static void write(Result result) {
        PrintWriter out = null;
        try {
            getResponse().setCharacterEncoding("UTF-8");
            getResponse().setContentType("application/json;charset=utf-8");
            out = getResponse().getWriter();
            out.write(JSON.toJSONString(result));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }
}
