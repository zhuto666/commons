package com.zhongqin.commons.util.idcard.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/7/1 13:07 星期一
 */
@Data
public class IdCardFaceResult {

    /**
     * 身份证正反面
     */
    @JsonProperty(value = "config_str")
    private String configStr;

    /**
     * 地址信息
     */
    @JsonProperty(value = "address")
    private String address;

    /**
     * 姓名
     */
    @JsonProperty(value = "name")
    private String name;

    /**
     * 民族
     */
    @JsonProperty(value = "nationality")
    private String nationality;

    /**
     * 身份证号
     */
    @JsonProperty(value = "num")
    private String num;

    /**
     * 性别
     */
    @JsonProperty(value = "sex")
    private String sex;

    /**
     * 出生日期
     */
    @JsonProperty(value = "birth")
    private String birth;

    /**
     * true表示成功，false表示失败
     */
    @JsonProperty(value = "success")
    private Boolean success;

    /**
     * 是否是复印件
     */
    @JsonProperty(value = "is_fake")
    private Boolean isFake;

}
