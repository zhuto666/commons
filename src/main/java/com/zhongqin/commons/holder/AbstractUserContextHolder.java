package com.zhongqin.commons.holder;

/**
 * @author Kevin
 * @date 2021/12/13 15:19
 * 用于获取登录用户信息
 */
public abstract class AbstractUserContextHolder<T> {

    private final ThreadLocal<T> threadLocal;

    public AbstractUserContextHolder() {
        this.threadLocal = new ThreadLocal<>();
    }

    public void setContext(T t) {
        threadLocal.set(t);
    }

    public T getContext() {
        return threadLocal.get();
    }

    public void clearContext() {
        threadLocal.remove();
    }

}
