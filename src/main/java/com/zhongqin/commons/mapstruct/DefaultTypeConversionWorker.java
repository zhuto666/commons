package com.zhongqin.commons.mapstruct;

import com.zhongqin.commons.util.LocalDateTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author Kevin
 * @date 2021/12/23 10:35
 */
public class DefaultTypeConversionWorker {

    /**
     * String 去空格
     *
     * @param str
     * @return
     */
    @Named("trim")
    public String trim(String str) {
        return StringUtils.isBlank(str) ? str : str.trim();
    }

    /**
     * 字符串转boolean
     *
     * @param str
     * @return
     */
    @Named("strToBoolean")
    public Boolean strToBoolean(String str) {
        if (StringUtils.isBlank(str)) {
            return Boolean.FALSE;
        }
        str = trim(str);
        return "Y".equalsIgnoreCase(str) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * boolean转字符串
     *
     * @param value
     * @return
     */
    @Named("booleanToString")
    public String booleanToString(boolean value) {
        if (value) {
            return "Y";
        }
        return "N";
    }

    /**
     * String 格式 yyyy-MM-dd HH:mm:ss 转指定 LocalDateTime
     *
     * @param str
     * @return
     */
    @Named("defaultStrToLocalDateTime")
    public LocalDateTime defaultStrToLocalDateTime(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return formatterLocalDateTime(str, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * String 格式 yyyy-MM-d 转指定 LocalDateTime
     *
     * @param str
     * @return
     */
    @Named("strToLocalDateTimeFormatteryyyyMMd")
    public LocalDateTime strToLocalDateTimeFormatteryyyyMMd(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return formatterLocalDateTime(str + " 00:00:00", "yyyy-MM-d HH:mm:ss");
    }

    private LocalDateTime formatterLocalDateTime(String str, String format) {
        str = trim(str);
        return LocalDateTimeUtil.getLocalDateTimeByString(str, format);
    }

    @Named("strToBigDecimal")
    public BigDecimal strToBigDecimal(String str) {
        return StringUtils.isBlank(str) ? BigDecimal.ZERO : new BigDecimal(str.trim());
    }

    @Named("bigDecimalSetScale")
    public BigDecimal bigDecimalSub(BigDecimal str) {
        return str.setScale(2, RoundingMode.UP);
    }

    @Named("strToInteger")
    public Integer strToInteger(String str) {
        return StringUtils.isBlank(str) ? 0 : Integer.parseInt(str);
    }

    @Named("strToLocalTime")
    public LocalTime defaultStrToLocalTime(String str) {
        return StringUtils.isBlank(str) ? null : LocalTime.parse(str.substring(11));
    }

    @Named("strToLocalDateTimeBySpecialString")
    public LocalDateTime strToLocalDateTime(String str) {
        return StringUtils.isBlank(str) ? null : LocalDateTimeUtil.getLocalDateTimeBySpecialString(str, "M/d/yyyy HH:mm:ss");
    }

}
