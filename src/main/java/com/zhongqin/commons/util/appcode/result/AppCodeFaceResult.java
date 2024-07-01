package com.zhongqin.commons.util.appcode.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/7/1 13:16 星期一
 */
@Data
public class AppCodeFaceResult {

    /**
     * 行驶证正反面
     */
    @JsonProperty(value = "config_str")
    private String configStr;

    /**
     * 车牌号码
     */
    @JsonProperty(value = "plate_num")
    private String plateNum;

    /**
     * 车辆类型
     */
    @JsonProperty(value = "vehicleType")
    private String vehicleType;

    /**
     * 所有人名称
     */
    @JsonProperty(value = "owner")
    private String owner;

    /**
     * 使用性质
     */
    @JsonProperty(value = "use_character")
    private String useCharacter;

    /**
     * 地址
     */
    @JsonProperty(value = "addr")
    private String addr;

    /**
     * 品牌型号
     */
    @JsonProperty(value = "model")
    private String model;

    /**
     * 车辆识别代号
     */
    @JsonProperty(value = "vin")
    private String vin;

    /**
     * 发动机号码
     */
    @JsonProperty(value = "engine_num")
    private String engineNum;

    /**
     * 注册日期
     */
    @JsonProperty(value = "register_date")
    private String registerDate;

    /**
     * 发证日期
     */
    @JsonProperty(value = "issue_date")
    private String issueDate;

    /**
     * 签发机关
     */
    @JsonProperty(value = "issue_authority")
    private String issueAuthority;

    /**
     * 是否是复印件（true/false）
     */
    @JsonProperty(value = "is_copy")
    private Boolean isCopy;

    /**
     * 识别成功与否（true/false）
     */
    @JsonProperty(value = "success")
    private Boolean success;

    /**
     * 请求对应的唯一表示
     */
    @JsonProperty(value = "request_id")
    private String requestId;

}
