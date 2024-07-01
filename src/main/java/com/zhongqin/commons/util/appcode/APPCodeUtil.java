package com.zhongqin.commons.util.appcode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.zhongqin.commons.exception.CustomException;
import com.zhongqin.commons.util.AlibabaHttpUtils;
import com.zhongqin.commons.util.JsonTools;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.data.repository.init.ResourceReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

/**
 * @author Kevin
 * 阿里云获取行驶证信息
 * @version 1.0
 * @date 2024/7/1 12:12 星期一
 */
public class APPCodeUtil {

    /**
     * @param side    face 正面 back 反面
     * @param imgPath 图片路径
     * @return str
     */
    public static String getAppCode(String side, String imgPath) {
        String host = "https://driving.market.alicloudapi.com";
        String path = "/rest/160601/ocr/ocr_vehicle.json";
        String appcode = "c89ae79bb9ed4d7a87e774b643e48a97";
        JSONObject configObj = new JSONObject();
        configObj.put("side", side);
        String configStr = configObj.toString();
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> query = new HashMap<>();
        // 对图像进行base64编码
        String imgBase64 = "";
        try {
            File file = new File(imgPath);
            byte[] content = new byte[(int) file.length()];
            FileInputStream finputstream = new FileInputStream(file);
            finputstream.read(content);
            finputstream.close();
            imgBase64 = new String(encodeBase64(content));
        } catch (IOException e) {
            throw new CustomException(e.getMessage());
        }
        // 拼装请求body的json字符串
        JSONObject requestObj = new JSONObject();
        try {
            requestObj.put("image", imgBase64);
            if (configStr.length() > 0) {
                requestObj.put("configure", configStr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String bodys = requestObj.toString();
        try {
            HttpResponse response = AlibabaHttpUtils.doPost(host, path, method, headers, query, bodys);
            int stat = response.getStatusLine().getStatusCode();
            if (stat != 200) {
                System.out.println("Http code: " + stat);
                System.out.println("http header error msg: " + response.getFirstHeader("X-Ca-Error-Message"));
                System.out.println("Http body error msg:" + EntityUtils.toString(response.getEntity()));
                throw new CustomException("行驶证" + side + "识别失败");
            }
            String res = EntityUtils.toString(response.getEntity());
            JSONObject resObj = JSON.parseObject(res);
            return JsonTools.objectToJson(resObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
