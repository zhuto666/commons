package com.zhongqin.commons.handler;

import com.zhongqin.commons.util.LocalDateTimeUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Kevin
 * @date 2021/1/12 17:14
 * 切面，插入创建人，创建时间，修改人，修改时间
 */
public abstract class AbstractDefaultMapperAspectHandler {

    private static final String CREATE_BY = "createdBy";
    private static final String GMT_CREATED = "gmtCreated";
    private static final String MODIFIED_BY = "modifiedBy";
    private static final String GMT_MODIFIED = "gmtModified";
    private static final String INSERT = "I";
    private static final String UPDATE = "U";

    /**
     * 获取操作人id
     *
     * @return
     */
    protected abstract String operatorId();

    /**
     * 修改切面注解
     */
    protected abstract void mapperUpdate();

    /**
     * 新增切面注解
     */
    protected abstract void mapperInsert();


    @Around("mapperUpdate()")
    public Object doAroundUpdate(ProceedingJoinPoint pjp) throws Throwable {
        return getObject(pjp, UPDATE);
    }

    @Around("mapperInsert()")
    public Object doAroundInsert(ProceedingJoinPoint pjp) throws Throwable {
        return getObject(pjp, INSERT);
    }

    private Object getObject(ProceedingJoinPoint pjp, String update) throws Throwable {
        Object[] objects = pjp.getArgs();
        if (objects != null) {
            for (Object arg : objects) {
                if (arg instanceof ArrayList) {
                    List<Object> list = (ArrayList<Object>) arg;
                    for (Object args : list) {
                        setObjectAttribute(args, update);
                    }
                } else {
                    setObjectAttribute(arg, update);
                }
            }
        }
        return pjp.proceed();
    }

    private void setObjectAttribute(Object arg, String type) throws Exception {
        String userInfoId = operatorId();
        Map<String, String> maps = BeanUtils.describe(arg);
        String isCreatedBy = maps.get(CREATE_BY);
        String isGmtCreated = maps.get(GMT_CREATED);
        String isModifiedBy = maps.get(MODIFIED_BY);
        String isGmtModified = maps.get(GMT_MODIFIED);
        if (type.equals(INSERT)) {
            setProperty(arg, userInfoId, isCreatedBy, isGmtCreated, CREATE_BY, GMT_CREATED, LocalDateTime.now());
            setProperty(arg, "0", isModifiedBy, isGmtModified, MODIFIED_BY, GMT_MODIFIED, LocalDateTimeUtil.defaultDate());
        } else {
            setProperty(arg, userInfoId, null, null, MODIFIED_BY, GMT_MODIFIED, LocalDateTime.now());
        }
    }

    private void setProperty(Object arg, String userInfoId, String isCreatedBy, String isGmtCreated, String createBy,
                             String gmtCreated, LocalDateTime now)
            throws IllegalAccessException, InvocationTargetException {
        if (StringUtils.isEmpty(isCreatedBy)) {
            BeanUtils.setProperty(arg, createBy, userInfoId);
        }
        if (StringUtils.isEmpty(isGmtCreated)) {
            BeanUtils.setProperty(arg, gmtCreated, now);
        }
    }
}
