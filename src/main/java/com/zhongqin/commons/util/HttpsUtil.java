package com.zhongqin.commons.util;

import com.zhongqin.commons.exception.CustomException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * HTTP,HTTPS请求工具类
 *
 * @author Kevin
 * @date 2020/9/11 13:45
 */
@Slf4j
public class HttpsUtil {

    private static RequestConfig requestConfig;

    /**
     * 建立连接的超时时间 (ms)
     */
    private static final int CONNECT_TIME_OUT = 600000;

    /**
     * 获取数据的超时时间 (ms)
     */
    private static final int SOCKET_TIME_OUT = 600000;

    /**
     * 连接池获取到连接的超时时间 (ms)
     */
    private static final int CONNECTION_REQUEST_TIME_OUT = 600000;

    static {
        RequestConfig.Builder configBuilder = RequestConfig.custom()
                // 设置建立连接的超时时间
                .setConnectTimeout(CONNECT_TIME_OUT)
                // 设置获取数据的超时时间
                .setSocketTimeout(SOCKET_TIME_OUT)
                // 设置连接池获取到连接的超时时间
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT);
        requestConfig = configBuilder.build();
    }


    /**
     * 发送GET请求（HTTP），不带参数
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String doGet(String url) {
        return doGet(url, null, null);
    }


    public static String doGet(String url, Map<String, Object> params, Map<String, String> headerParams) {
        HttpClient httpClient = HttpClients.createDefault();
        String result = null;
        try {
            URIBuilder builder = new URIBuilder(url);
            // 添加参数
            if (!Objects.isNull(params)) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue().toString();
                    builder.setParameter(key, value);
                }
            }
            HttpGet httpGet = new HttpGet(builder.build());
            // 添加Header
            if (!Objects.isNull(headerParams)) {
                for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    httpGet.setHeader(key, value);
                }
            }
            httpGet.setConfig(requestConfig);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, StandardCharsets.UTF_8.toString());
            }
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage());
        }
        return result;
    }

    /**
     * 发送 POST 请求（HTTP），不带输入数据
     *
     * @param url
     * @return
     */
    public static String doPost(String url) {
        return doPost(url, "", null);
    }


    /**
     * 发送 POST 请求，K-V形式
     *
     * @param url    API接口URL
     * @param params 参数map
     * @return
     */
    public static String doPost(String url, Map<String, Object> params, Map<String, String> headerParams) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result;
        CloseableHttpResponse response = null;
        try {
            URIBuilder builder = new URIBuilder(url);
            HttpPost httpPost = new HttpPost(builder.build());
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList<>();
            // 添加参数
            if (!Objects.isNull(params)) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue().toString();
                    NameValuePair pair = new BasicNameValuePair(key, value);
                    pairList.add(pair);
                }
            }
            // 添加Header
            if (!Objects.isNull(headerParams)) {
                for (Map.Entry<String, String> stringStringEntry : headerParams.entrySet()) {
                    Map.Entry<String, String> entry;
                    entry = stringStringEntry;
                    String key = entry.getKey();
                    String value = entry.getValue();
                    httpPost.setHeader(key, value);
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, StandardCharsets.UTF_8.toString()));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, StandardCharsets.UTF_8.toString());
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage());
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return result;
    }


    /**
     * 发送 POST 请求，JSON形式
     *
     * @param url
     * @param json
     * @return
     */
    public static String doPost(String url, String json, Map<String, String> headerParams) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result;
        CloseableHttpResponse response = null;
        try {
            URIBuilder builder = new URIBuilder(url);
            HttpPost httpPost = new HttpPost(builder.build());
            httpPost.setConfig(requestConfig);
            // 添加Header
            if (!Objects.isNull(headerParams)) {
                for (Map.Entry<String, String> stringStringEntry : headerParams.entrySet()) {
                    Map.Entry<String, String> entry;
                    entry = stringStringEntry;
                    String value = entry.getValue();
                    String key = entry.getKey();
                    httpPost.setHeader(key, value);
                }
            }
            StringEntity stringEntity = new StringEntity(json, StandardCharsets.UTF_8.toString());
            stringEntity.setContentEncoding(StandardCharsets.UTF_8.toString());
            stringEntity.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, StandardCharsets.UTF_8.toString());
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage());
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 文件上传至 only office 文件的方式
     *
     * @param url          请求路径
     * @param params       参数map
     * @param headerParams header 参数map
     * @return str
     */
    public static String doFilePost(String url, Map<String, File> params, Map<String, String> headerParams) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(StandardCharsets.UTF_8);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        String string;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            HttpPost httpPost = new HttpPost(uriBuilder.build());
            // 添加参数
            if (!Objects.isNull(params)) {
                for (Map.Entry<String, File> entry : params.entrySet()) {
                    String key = entry.getKey();
                    File file = entry.getValue();
                    builder.addBinaryBody(key, file, ContentType.DEFAULT_BINARY, file.getName());
                }
            }
            // 添加Header
            if (!Objects.isNull(headerParams)) {
                for (Map.Entry<String, String> stringStringEntry : headerParams.entrySet()) {
                    Map.Entry<String, String> entry;
                    entry = stringStringEntry;
                    String key = entry.getKey();
                    String value = entry.getValue();
                    httpPost.setHeader(key, value);
                }
            }
            httpPost.setEntity(builder.build());
            CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpPost);
            HttpEntity entity = closeableHttpResponse.getEntity();
            string = EntityUtils.toString(entity, StandardCharsets.UTF_8.toString());
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        return string;
    }

    /**
     * 文件上传至 only office 已流的方式
     *
     * @param url          请求路径
     * @param params       参数map
     * @param headerParams header 参数map
     * @return str
     */
    public static String doMultipartFilePost(String url, Map<String, MultipartFile> params, Map<String, String> headerParams) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(StandardCharsets.UTF_8);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        String string;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            HttpPost httpPost = new HttpPost(uriBuilder.build());
            // 添加参数
            if (!Objects.isNull(params)) {
                for (Map.Entry<String, MultipartFile> entry : params.entrySet()) {
                    String key = entry.getKey();
                    MultipartFile multipartFile = entry.getValue();
                    String originalFileName = multipartFile.getOriginalFilename();
                    Assert.isNull(originalFileName, "上传文件原始文件名不能为空");
                    builder.addBinaryBody(key, multipartFile.getInputStream(), ContentType.DEFAULT_BINARY, originalFileName);
                }
            }
            // 添加Header
            if (!Objects.isNull(headerParams)) {
                for (Map.Entry<String, String> stringStringEntry : headerParams.entrySet()) {
                    Map.Entry<String, String> entry;
                    entry = stringStringEntry;
                    String key = entry.getKey();
                    String value = entry.getValue();
                    httpPost.setHeader(key, value);
                }
            }
            httpPost.setEntity(builder.build());
            CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpPost);
            HttpEntity entity = closeableHttpResponse.getEntity();
            string = EntityUtils.toString(entity, StandardCharsets.UTF_8.toString());
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        return string;
    }

    public static String doInputStreamPost(String url, Map<String, InputStream> params, String filename, Map<String, String> headerParams) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(StandardCharsets.UTF_8);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        String string;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            HttpPost httpPost = new HttpPost(uriBuilder.build());
            // 添加参数
            if (!Objects.isNull(params)) {
                for (Map.Entry<String, InputStream> entry : params.entrySet()) {
                    String key = entry.getKey();
                    InputStream inputStream = entry.getValue();
                    builder.addBinaryBody(key, inputStream, ContentType.DEFAULT_BINARY, filename);
                }
            }
            // 添加Header
            if (!Objects.isNull(headerParams)) {
                for (Map.Entry<String, String> stringStringEntry : headerParams.entrySet()) {
                    Map.Entry<String, String> entry;
                    entry = stringStringEntry;
                    String key = entry.getKey();
                    String value = entry.getValue();
                    httpPost.setHeader(key, value);
                }
            }
            httpPost.setEntity(builder.build());
            CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpPost);
            HttpEntity entity = closeableHttpResponse.getEntity();
            string = EntityUtils.toString(entity, StandardCharsets.UTF_8.toString());
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        return string;
    }

    /**
     * @param httpPath 文件网络地址
     * @param fileName 带后缀的文件名   aaa.docx
     * @param savePath 不带文件名的路径
     */
    @SneakyThrows
    public static String getInputStream(String httpPath, String fileName, String savePath) {
        // 替换成你需要保存图片的本地目录
        savePath = savePath + fileName;
        log.info("转UTF-8格式前请求：" + httpPath);
        httpPath = hanldChinese(httpPath);
        log.info("转UTF-8格式后请求：" + httpPath);
        try {
            // 打开连接
            URL url = new URL(httpPath);
            URLConnection connection = url.openConnection();
            // 设置请求超时为5秒
            connection.setConnectTimeout(5 * 1000);
            // 读取数据流并保存到本地
            InputStream input = connection.getInputStream();
            byte[] data = new byte[1024];
            int len;
            FileOutputStream output = new FileOutputStream(savePath);
            while ((len = input.read(data)) != -1) {
                output.write(data, 0, len);
            }
            output.close();
            input.close();
            log.info("文件保存本地成功：" + savePath);

            //*本地文件路径给出  左斜线*/
            File file = new File(savePath);
            return file.getPath();
        } catch (IOException e) {
            e.printStackTrace();
            log.info("文件保存失败：" + e.getMessage());
            return null;
        }
    }

    /**
     * 对中文字符进行UTF-8编码
     *
     * @param source 要转义的字符串
     */
    public static String hanldChinese(String source) {
        char[] arr = source.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char temp : arr) {
            if (isChinese(temp)) {
                sb.append(URLEncoder.encode(String.valueOf(temp), StandardCharsets.UTF_8));
                continue;
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

}
