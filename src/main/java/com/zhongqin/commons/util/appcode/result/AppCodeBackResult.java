package com.zhongqin.commons.util.appcode.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/7/1 13:16 星期一
 */
@Data
public class AppCodeBackResult {

    /**
     * 行驶证正反面
     */
    @JsonProperty(value = "config_str")
    private String configStr;

    /**
     * 核定载人数
     */
    @JsonProperty(value = "appproved_passenger_capacity")
    private String appprovedPassengerCapacity;

    /**
     * 核定载质量
     */
    @JsonProperty(value = "approved_load")
    private String approvedLoad;

    /**
     * 档案编号
     */
    @JsonProperty(value = "file_no")
    private String fileNo;

    /**
     * 总质量
     */
    @JsonProperty(value = "gross_mass")
    private String grossMass;

    /**
     * 检验记录
     */
    @JsonProperty(value = "inspection_record")
    private String inspectionRecord;

    /**
     * 外廓尺寸
     */
    @JsonProperty(value = "overall_dimension")
    private String overallDimension;

    /**
     * 准牵引总质量
     */
    @JsonProperty(value = "traction_mass")
    private String tractionMass;

    /**
     * 整备质量
     */
    @JsonProperty(value = "unladen_mass")
    private String unladenMass;

    /**
     * 牌号码
     */
    @JsonProperty(value = "plate_num")
    private String plateNum;

    /**
     * 条形码编号
     */
    @JsonProperty(value = "barcode_number")
    private String barcodeNumber;

    /**
     * 能源类型
     */
    @JsonProperty(value = "energy_type")
    private String energyType;

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
