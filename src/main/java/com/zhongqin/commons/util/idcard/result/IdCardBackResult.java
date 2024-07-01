package com.zhongqin.commons.util.idcard.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/7/1 13:07 星期一
 */
@Data
public class IdCardBackResult {

    /**
     * 身份证正反面
     */
    @JsonProperty(value = "config_str")
    private String configStr;

    /**
     * 有效开始期限
     */
    @JsonProperty(value = "start_date")
    private String startDate;

    /**
     * 有效结束期限
     */
    @JsonProperty(value = "end_date")
    private String endDate;

    /**
     * 签发机关
     */
    @JsonProperty(value = "issue")
    private String issue;

    /**
     * 识别成功与否（true/false）
     */
    @JsonProperty(value = "success")
    private String success;

    /**
     * 是否是复印件
     */
    @JsonProperty(value = "is_fake")
    private String isFake;


}
