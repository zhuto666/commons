package com.zhongqin.commons.util.sms;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import com.zhongqin.commons.exception.CustomException;
import com.zhongqin.commons.util.JsonTools;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @author Kevin
 * 阿里云发送短信
 * @version 1.0
 * @date 2024/7/1 13:53 星期一
 */
@Slf4j
public class SendMobileSmsUtil {

    public static Client createClient() throws Exception {
        // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考。
        // 建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html。
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID。
                .setAccessKeyId("LTAI5tEtLCHydabsv4pV1KBT")
                // 必填，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
                .setAccessKeySecret("EONv6deF1WCilbjRmZ6wQ90YYaxJEU");
        // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new Client(config);
    }

    /**
     * 发送短信提醒
     *
     * @param phoneNumbers 手机号
     * @param signName     签名
     * @return str
     */
    public static String sendMessage(String phoneNumbers, String signName) {
        try {

            Client client = SendMobileSmsUtil.createClient();
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(phoneNumbers)
                    .setSignName(signName)
                    .setTemplateCode("SMS_465364845");
            // 复制代码运行请自行打印 API 的返回值
            SendSmsResponse sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, new RuntimeOptions());
            return JsonTools.objectToJson(sendSmsResponse.body);
        } catch (TeaException error) {
            log.error("发送短信出现TeaException异常");
            log.error("诊断地址{}", error.getData().get("Recommend"));
            throw new CustomException(error.message);
        } catch (Exception exception) {
            TeaException error = new TeaException(exception.getMessage(), exception);
            log.error("发送短信出现TeaException异常");
            log.error("诊断地址{}", error.getData().get("Recommend"));
            throw new CustomException(error.message);
        }
    }

    /**
     * 发送短信验证码
     *
     * @param phoneNumbers 手机号
     * @param signName     签名
     * @param code         验证码
     * @return str
     */
    public static String sendMessage(String phoneNumbers, String signName, String code) {
        try {
            Client client = SendMobileSmsUtil.createClient();
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(phoneNumbers)
                    .setSignName(signName)
                    .setTemplateCode("SMS_471800250")
                    .setTemplateParam("{\"code\":\"" + code + "\"}");
            // 复制代码运行请自行打印 API 的返回值
            SendSmsResponse sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, new RuntimeOptions());
            return JsonTools.objectToJson(sendSmsResponse.body);
        } catch (TeaException error) {
            log.error("发送短信出现TeaException异常");
            log.error("诊断地址{}", error.getData().get("Recommend"));
            throw new CustomException(error.message);
        } catch (Exception exception) {
            TeaException error = new TeaException(exception.getMessage(), exception);
            log.error("发送短信出现TeaException异常");
            log.error("诊断地址{}", error.getData().get("Recommend"));
            throw new CustomException(error.message);
        }
    }

    public static String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 1; i <= 4; i++) {
            code.append(String.valueOf(System.currentTimeMillis()).charAt(random.nextInt(13)));
        }
        return String.valueOf(code);
    }


}
