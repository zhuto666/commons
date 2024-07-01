package com.zhongqin.commons.util.sms;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import com.zhongqin.commons.exception.CustomException;
import com.zhongqin.commons.util.JsonTools;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

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

    @SneakyThrows
    public static String sendMessage(String phoneNumbers) {
        Client client = SendMobileSmsUtil.createClient();
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phoneNumbers)
                .setSignName("晟富")
                .setTemplateCode("SMS_465364845");
        try {
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


}
