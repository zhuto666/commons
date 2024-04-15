package com.zhongqin.commons.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Mybatis Plus自动填充创建人创建时间等
 *
 * @author Kevin
 * @date 2020/9/10 14:08
 */
public abstract class AbstractDefaultPoMetaObjectHandler implements MetaObjectHandler {

    private static final String CREATE_BY = "createdBy";
    private static final String GMT_CREATED = "gmtCreated";
    private static final String MODIFIED_BY = "modifiedBy";
    private static final String GMT_MODIFIED = "gmtModified";
    private static final String ZERO_STR = "0";
    private static final String DELETED = "deleted";
    private static final String VERSION = "version";

    /**
     * 获取操作人id
     *
     * @return
     */
    protected abstract String operatorId();

    @Override
    public void insertFill(MetaObject metaObject) {
        String loginUserId = Optional.ofNullable(operatorId()).orElse(ZERO_STR);
        this.setFieldValByName(CREATE_BY, loginUserId, metaObject);
        this.setFieldValByName(GMT_CREATED, LocalDateTime.now(), metaObject);
        this.setFieldValByName(MODIFIED_BY, ZERO_STR, metaObject);
        this.setFieldValByName(GMT_MODIFIED, null, metaObject);
        this.setFieldValByName(DELETED, false, metaObject);
        this.setFieldValByName(VERSION, 0, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String loginUserId = Optional.ofNullable(operatorId()).orElse(ZERO_STR);
        this.setFieldValByName(MODIFIED_BY, loginUserId, metaObject);
        this.setFieldValByName(GMT_MODIFIED, LocalDateTime.now(), metaObject);
    }
}
