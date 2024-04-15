package com.zhongqin.commons.interceptor;

import com.zhongqin.commons.holder.AbstractUserContextHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author Kevin
 * @date 2023/3/30 13:49
 */
public abstract class AbstractGetUserInterceptor<T> implements HandlerInterceptor {

    private final AbstractUserContextHolder<T> abstractUserContextHolder;

    public AbstractGetUserInterceptor(AbstractUserContextHolder<T> abstractUserContextHolder) {
        this.abstractUserContextHolder = abstractUserContextHolder;
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object obj) {
        T context = getContext(request, response);
        if (Objects.nonNull(context)) {
            abstractUserContextHolder.setContext(context);
        }
        return true;
    }

    /**
     * 获取登录用户上下文对象
     *
     * @param request
     * @param response
     * @return
     */
    protected abstract T getContext(HttpServletRequest request, HttpServletResponse response);

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {
        abstractUserContextHolder.clearContext();
    }

}
