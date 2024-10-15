package com.zhongqin.commons.qrcode.properties;

import lombok.Data;

/**
 * @author Kevin
 * @date 2023/4/7 12:37 星期五
 */
@Data
public class GenerateQrCodeProperties {

    /**
     * 图片尺寸
     */
    private int qrcodeSize = 1000;

    /**
     * 图片类型
     */
    private String format = "png";

    /**
     * 图片内容
     */
    private String contents = "我的v：o_o0807z";

    /**
     * 底部文字
     */
    private String bottomTest = "扫一扫上面的二维码图案，查看我的内心。署名：Kevin";
    /**
     * logo
     */
    private String logo = "logo.jpg";
    /**
     * 背景颜色
     */
    private int backgroundColor = 0x000000;

}
