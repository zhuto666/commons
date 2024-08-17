package com.zhongqin.commons.util.idcard;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhongqin.commons.exception.CustomException;
import com.zhongqin.commons.util.AlibabaHttpUtils;
import com.zhongqin.commons.util.JsonTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevin
 * 阿里云获取身份证信息
 * @version 1.0
 * @date 2024/7/1 11:52 星期一
 */
@Slf4j
public class IdCardUtil {

    public static String imgBase64(String path) {
        // 对path进行判断，如果是本地文件就二进制读取并base64编码，如果是url,则返回
        String imgBase64 = "";
        if (path.startsWith("http")) {
            imgBase64 = path;
        } else {
            try {
                File file = new File(path);
                byte[] content = new byte[(int) file.length()];
                FileInputStream finputstream = new FileInputStream(file);
                finputstream.read(content);
                finputstream.close();
                imgBase64 = new String(Base64.encodeBase64(content));
            } catch (IOException e) {
                e.printStackTrace();
                return imgBase64;
            }
        }
        return imgBase64;
    }

    /**
     * @param side    face 正面 back 反面
     * @param imgPath 图片路径
     * @return str
     */
    public static String getOcrIdCard(String side, String imgPath) {
        String host = "https://cardnumber.market.alicloudapi.com";
        String path = "/rest/160601/ocr/ocr_idcard.json";
        String appcode = "e9085263763040c7a1a8c2618ef912ef";
        String method = "POST";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + appcode);
        // 根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/json; charset=UTF-8");
        Map<String, String> query = new HashMap<>();
        // 对图像进行base64编码
        String imgBase64 = imgBase64(imgPath);
        // configure配置
        JSONObject configObj = new JSONObject();
        // face 正面
        // back 反面
        configObj.put("side", side);
        String configStr = configObj.toString();
        // 拼装请求body的json字符串
        JSONObject requestObj = new JSONObject();
        requestObj.put("image", imgBase64);
        if (configObj.size() > 0) {
            requestObj.put("configure", configStr);
        }
        String bodys = requestObj.toString();
        try {
            HttpResponse response = AlibabaHttpUtils.doPost(host, path, method, headers, query, bodys);
            int stat = response.getStatusLine().getStatusCode();
            if (stat != 200) {
                log.error("Http code: " + stat);
                log.error("http header error msg: " + response.getFirstHeader("X-Ca-Error-Message"));
                log.error("Http body error msg:" + EntityUtils.toString(response.getEntity()));
                throw new CustomException("身份证" + ("face".equals(side) ? "正面" : "反面") + "识别失败");
            }
            String res = EntityUtils.toString(response.getEntity());
            JSONObject resObj = JSON.parseObject(res);
            return JsonTools.objectToJson(resObj);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }

}
