package com.zhongqin.commons.util.ocr;

import com.zhongqin.commons.util.JsonTools;
import com.zhongqin.commons.util.appcode.APPCodeUtil;
import com.zhongqin.commons.util.idcard.IdCardUtil;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/7/4 10:36 星期四
 */
public class OrcUtil {

    public static <T> T ocr(String url, String configStr, String certificates, Class<T> clazz) {
        switch (certificates) {
            case "行驶证":
                String appCode = APPCodeUtil.getAppCode(configStr, url);
                System.out.println("行驶证" + appCode);
                return JsonTools.jsonToObject(appCode, clazz);
            case "身份证":
                String idCard = IdCardUtil.getOcrIdCard(configStr, url);
                System.out.println("身份证" + idCard);
                return JsonTools.jsonToObject(idCard, clazz);
        }
        return null;
    }

}
