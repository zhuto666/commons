package com.zhongqin.commons.interceptor;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Kevin
 * @date 2023/3/30 13:50
 */
public abstract class AbstractVerifyUserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object obj) {
        return checkAccessTokenEffectiveness(request, response);
    }

    /**
     * 设置验证Token方法让子类去实现
     * 验证Token是否为空 是否有效
     *
     * @param request
     * @param response
     * @return
     */
    protected abstract boolean checkAccessTokenEffectiveness(HttpServletRequest request, HttpServletResponse response);

}
