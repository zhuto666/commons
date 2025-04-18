package com.zhongqin.commons.util;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * JDK的HttpClient工具类
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2021/11/29 20:27
 * @since jdk11
 */
public class CustomHttpClient {
    private volatile HttpClient client;
    private HttpClientProps httpClientProps;

    public CustomHttpClient(HttpClientProps httpClientProps) {
        if (client == null) {
            synchronized (CustomHttpClient.class) {
                if (client == null) {
                    this.httpClientProps = httpClientProps;
                    HttpClient.Builder builder = HttpClient.newBuilder()
                            .version(httpClientProps.getVersion())
                            .connectTimeout(Duration.ofMillis(httpClientProps.getConnectTimeout()))
                            .followRedirects(httpClientProps.getRedirect());
                    Optional.ofNullable(httpClientProps.getAuthenticator()).ifPresent(builder::authenticator);
                    Optional.ofNullable(httpClientProps.getCookieHandler()).ifPresent(builder::cookieHandler);
                    Optional.ofNullable(httpClientProps.getProxySelector()).ifPresent(builder::proxy);
                    Optional.ofNullable(httpClientProps.getExecutor()).ifPresent(builder::executor);
                    client = builder.build();
                }
            }
        }
    }


    //=================================GET BEGIN========================================//
    /**
     * @Author: zhuquanwen
     * @description: 同步GET请求，返回值解析为字符串
     * @date: 2021/11/29 21:07
     * @param: url 访问URL
     * @exception: IOException IO异常
     * @exception: InterruptedException
     * @return: java.lang.String
     */
    public String doGet(String url) throws IOException, InterruptedException {
        return doGet(url, Map.of());
    }

    /**
     * @Author: zhuquanwen
     * @description: 同步GET请求，返回值解析为字符串
     * @date: 2021/11/29 21:07
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @exception: IOException IO异常
     * @exception: InterruptedException
     * @return: java.lang.String
     */
    public String doGet(String url, Map<String, String> headerMap) throws IOException, InterruptedException {
        return doGet(url, headerMap, httpClientProps.getDefaultReadTimeout());
    }

    /**
     * @Author: zhuquanwen
     * @description: 同步GET请求，返回值解析为字符串
     * @date: 2021/11/29 21:07
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: timeout 超时时间
     * @exception: IOException IO异常
     * exception: InterruptedException
     * @return: java.lang.String
     */
    public String doGet(String url, Map<String, String> headerMap, long timeout) throws IOException, InterruptedException {
        return doGet(url, headerMap, timeout, String.class);
    }

    /**
     * @Author: zhuquanwen
     * @description: 同步GET请求，返回值支持的解析类型有byte[]、String、InputStream
     * @date: 2021/11/29 21:07
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: timeout 超时时间
     * @param: resClass 返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @exception: IOException IO异常
     * @exception: InterruptedException
     * @return: java.lang.String
     */
    public <T> T doGet(String url, Map<String, String> headerMap, long timeout, Class<T> resClass) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildGetRequest(url, headerMap, timeout);
        return getResData(httpRequest, resClass);
    }

    /**
     * 同步GET请求，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: timeout 超时时间
     * @param: resClass 返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @since jdk11
     */
    public <T> HttpResponse<T> doGetResponse(String url, Map<String, String> headerMap, long timeout, Class<T> resClass) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildGetRequest(url, headerMap, timeout);
        return getRes(httpRequest, resClass);
    }

    /**
     * 同步GET请求，返回byte[]
     * @version 1.0
     * @since jdk11
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: timeout 超时时间
     * @throws
     * @return java.util.concurrent.CompletableFuture<java.net.http.HttpResponse<byte[]>>
     */
    public CompletableFuture<HttpResponse<byte[]>> doGetByteResponseAsync(String url, Map<String, String> headerMap, long timeout) {
        HttpRequest httpRequest = buildGetRequest(url, headerMap, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * 同步GET请求，返回String
     * @version 1.0
     * @since jdk11
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: timeout 超时时间
     * @throws
     * @return java.util.concurrent.CompletableFuture<java.net.http.HttpResponse<byte[]>>
     */
    public CompletableFuture<HttpResponse<String>> doGetStringResponseAsync(String url, Map<String, String> headerMap, long timeout) {
        HttpRequest httpRequest = buildGetRequest(url, headerMap, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 同步GET请求，返回InputStream
     * @version 1.0
     * @since jdk11
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: timeout 超时时间
     * @throws
     * @return java.util.concurrent.CompletableFuture<java.net.http.HttpResponse<byte[]>>
     */
    public CompletableFuture<HttpResponse<InputStream>> doGetInputStreamResponseAsync(String url, Map<String, String> headerMap, long timeout) {
        HttpRequest httpRequest = buildGetRequest(url, headerMap, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
    }
    //=================================GET END========================================//

    //=================================POST BEGIN========================================//
    /**
     * 同步POST请求，通过请求体传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: requestBody 请求体
     * @since jdk11
     */
    public String doPost(String url, String requestBody) throws IOException, InterruptedException {
        return doPost(url, Map.of(), requestBody, httpClientProps.getDefaultReadTimeout());
    }

    /**
     * 同步POST请求，通过form传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form form表单
     * @since jdk11
     */
    public String doPost(String url, Map<String, Object> form) throws IOException, InterruptedException {
        return doPost(url, Map.of(), form, httpClientProps.getDefaultReadTimeout());
    }

    /**
     * 同步POST请求，通过请求体传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: requestBody 请求体
     * @since jdk11
     */
    public String doPost(String url, Map<String, String> headerMap, String requestBody) throws IOException, InterruptedException {
        return doPost(url, headerMap, requestBody, httpClientProps.getDefaultReadTimeout());
    }

    /**
     * 同步POST请求，通过Form传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form 表单
     * @since jdk11
     */
    public String doPost(String url, Map<String, String> headerMap, Map<String, Object> form) throws IOException, InterruptedException {
        return doPost(url, headerMap, form, httpClientProps.getDefaultReadTimeout());
    }

    /**
     * 同步POST请求，通过请求体传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: requestBody 请求体
     * @param: timeout 超时时间
     * @since jdk11
     */
    public String doPost(String url, Map<String, String> headerMap, String requestBody, long timeout) throws IOException, InterruptedException {
        return doPost(url, headerMap, requestBody, timeout, String.class);
    }

    /**
     * 同步POST请求，通过FORM传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form 表单
     * @param: timeout 超时时间
     * @since jdk11
     */
    public String doPost(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout) throws IOException, InterruptedException {
        return doPost(url, headerMap, form, timeout, String.class);
    }

    /**
     * 同步POST请求，通过请求体传送数据，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: requestBody 请求体
     * @param: timeout 超时时间
     * @param: resClass 返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @since jdk11
     */
    public <T> T doPost(String url, Map<String, String> headerMap, String requestBody, long timeout, Class<T> resClass) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPostRequest(url, headerMap, requestBody, timeout);
        return getResData(httpRequest, resClass);
    }

    /**
     * 同步POST请求，通过FORM传送数据，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form form表单
     * @param: timeout 超时时间
     * @param: resClass 返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @since jdk11
     */
    public <T> T doPost(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout, Class<T> resClass) throws IOException, InterruptedException {
        return doPostResponse(url, headerMap, form, timeout, resClass).body();
    }

    /**
     * 同步POST请求，通过请求体传送数据，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: requestBody 请求体
     * @param: timeout 超时时间
     * @param: resClass 返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @since jdk11
     */
    public <T> HttpResponse<T> doPostResponse(String url, Map<String, String> headerMap, String requestBody, long timeout, Class<T> resClass) throws IOException, InterruptedException {
        return doPostResponse(url, headerMap, HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8), timeout, resClass);
    }

    /**
     * 同步POST请求，通过FORM表单传送数据，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form 表单
     * @param: timeout 超时时间
     * @param: resClass 返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @since jdk11
     */
    public <T> HttpResponse<T> doPostResponse(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout, Class<T> resClass) throws IOException, InterruptedException {
        String[] headers = createHeader(headerMap, "application/x-www-form-urlencoded");
        Map<String, String> newHeader = new HashMap<>();
        for (int i = 0; i < headers.length; i = i + 2) {
            newHeader.put(headers[i], headers[i + 1]);
        }
        HttpRequest httpRequest = buildPostRequest(url, newHeader, form, timeout);
        return getRes(httpRequest, resClass);
    }

    /**
     * 同步POST请求，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: bodyPublisher 请求体
     * @param: timeout 超时时间
     * @param: resClass 返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @since jdk11
     */
    public <T> HttpResponse<T> doPostResponse(String url, Map<String, String> headerMap, HttpRequest.BodyPublisher bodyPublisher, long timeout, Class<T> resClass) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPostRequest(url, headerMap, bodyPublisher, timeout);
        return getRes(httpRequest, resClass);
    }

    /**
     * 异步POST请求，通过form表单传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form 表单
     * @param: timeout 超时时间
     * @since jdk11
     */
    public CompletableFuture<HttpResponse<byte[]>> doPostByteResponseAsync(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout) {
        HttpRequest httpRequest = buildPostRequest(url, headerMap, form, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * 异步POST请求，通过form表单传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form 表单
     * @param: timeout 超时时间
     * @since jdk11
     */
    public CompletableFuture<HttpResponse<InputStream>> doPostInputStreamResponseAsync(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout) {
        HttpRequest httpRequest = buildPostRequest(url, headerMap, form, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
    }

    /**
     * 异步POST请求，通过form表单传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form 表单
     * @param: timeout 超时时间
     * @since jdk11
     */
    public CompletableFuture<HttpResponse<String>> doPostStringResponseAsync(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout) {
        HttpRequest httpRequest = buildPostRequest(url, headerMap, form, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 异步POST请求，通过请求体传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: requestBody 请求体
     * @param: timeout 超时时间
     * @since jdk11
     */
    public CompletableFuture<HttpResponse<byte[]>> doPostByteResponse(String url, Map<String, String> headerMap, String requestBody, long timeout) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPostRequest(url, headerMap, requestBody, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * 异步POST请求，通过请求体传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: requestBody 请求体
     * @param: timeout 超时时间
     * @since jdk11
     */
    public CompletableFuture<HttpResponse<InputStream>> doPostInputStreamResponse(String url, Map<String, String> headerMap, String requestBody, long timeout) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPostRequest(url, headerMap, requestBody, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
    }

    /**
     * 异步POST请求，通过请求体传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: requestBody 请求体
     * @param: timeout 超时时间
     * @since jdk11
     */
    public CompletableFuture<HttpResponse<String>> doPostStringResponseAsync(String url, Map<String, String> headerMap, String requestBody, long timeout) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPostRequest(url, headerMap, requestBody, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
    }
    //=================================POST END========================================//

    //=================================文件上传 BEGIN========================================//
    /**
     * 同步上传文件，也可以附带数据，如果是文件，formData的value是FileInfo 或 FileInfo[]类型
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: bodyPublisher 请求体
     * @param: timeout 超时时间
     * @param: resClass 返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @since jdk11
     */
    public <T> HttpResponse<T> doUploadResponse(String url, Map<String, String> headerMap, Map<String, Object> formData, long timeout, Class<T> resClass) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildUploadRequest(url, headerMap, formData, timeout);
        return getRes(httpRequest, resClass);
    }

    /**
     * 同步上传文件，也可以附带数据，如果是文件，formData的value是FileInfo或FileInfo[]类型
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: bodyPublisher 请求体
     * @param: timeout 超时时间
     * @param: resClass 返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @since jdk11
     */
    public <T> T doUpload(String url, Map<String, String> headerMap, Map<String, Object> formData, long timeout, Class<T> resClass) throws IOException, InterruptedException {
        return doUploadResponse(url, headerMap, formData, timeout, resClass).body();
    }

    /**
     * 异步POST请求，通过form表单传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form 表单
     * @param: timeout 超时时间
     * @since jdk11
     */
    public CompletableFuture<HttpResponse<byte[]>> doUploadByteResponseAsync(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout) throws IOException {
        HttpRequest httpRequest = buildUploadRequest(url, headerMap, form, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * 异步POST请求，通过form表单传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form 表单
     * @param: timeout 超时时间
     * @since jdk11
     */
    public CompletableFuture<HttpResponse<InputStream>> doUploadInputStreamResponseAsync(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout) throws IOException {
        HttpRequest httpRequest = buildUploadRequest(url, headerMap, form, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
    }

    /**
     * 异步POST请求，通过form表单传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form 表单
     * @param: timeout 超时时间
     * @since jdk11
     */
    public CompletableFuture<HttpResponse<String>> doUploadStringResponseAsync(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout) throws IOException {
        HttpRequest httpRequest = buildUploadRequest(url, headerMap, form, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
    }
    //=================================文件上传 END========================================//

    //=================================文件下载 BEGIN========================================//
    /**
     * 同步下载文件，构建httpRequest的方式参见
     * {@link #buildGetRequest(String, Map, long)}
     * {@link #buildPostRequest(String, Map, String, long)}
     * {@link #buildPostRequest(String, Map, Map, long)}
     * {@link #buildPostRequest(String, Map, HttpRequest.BodyPublisher, long)}
     *
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: httpRequest 请求
     * @param: filePath 文件路径
     * @since jdk11
     */
    public Path doDownload(HttpRequest httpRequest, String filePath) throws IOException, InterruptedException {
        HttpResponse<Path> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofFile(new File(filePath).toPath()));
        return httpResponse.body();
    }

    /**
     * 同步下载文件，构建httpRequest的方式参见
     * {@link #buildGetRequest(String, Map, long)}
     * {@link #buildPostRequest(String, Map, String, long)}
     * {@link #buildPostRequest(String, Map, Map, long)}
     * {@link #buildPostRequest(String, Map, HttpRequest.BodyPublisher, long)}
     *
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: httpRequest 请求
     * @param: filePath 文件路径
     * @since jdk11
     */
    public HttpResponse<Path> doDownloadResponse(HttpRequest httpRequest, String filePath) throws IOException, InterruptedException {
        return client.send(httpRequest, HttpResponse.BodyHandlers.ofFile(new File(filePath).toPath()));
    }
    //=================================文件下载 END========================================//


    //=================================PUT BEGIN========================================//
    /**
     * 同步PUT请求，通过请求体传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: requestBody 请求体
     * @since jdk11
     */
    public String doPut(String url, String requestBody) throws IOException, InterruptedException {
        return doPut(url, Map.of(), requestBody, httpClientProps.getDefaultReadTimeout());
    }

    /**
     * 同步PUT请求，通过form传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form form表单
     * @since jdk11
     */
    public String doPut(String url, Map<String, Object> form) throws IOException, InterruptedException {
        return doPut(url, Map.of(), form, httpClientProps.getDefaultReadTimeout());
    }

    /**
     * 同步PUT请求，通过请求体传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: requestBody 请求体
     * @since jdk11
     */
    public String doPut(String url, Map<String, String> headerMap, String requestBody) throws IOException, InterruptedException {
        return doPut(url, headerMap, requestBody, httpClientProps.getDefaultReadTimeout());
    }

    /**
     * 同步PUT请求，通过Form传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form 表单
     * @since jdk11
     */
    public String doPut(String url, Map<String, String> headerMap, Map<String, Object> form) throws IOException, InterruptedException {
        return doPut(url, headerMap, form, httpClientProps.getDefaultReadTimeout());
    }

    /**
     * 同步PUT请求，通过请求体传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: requestBody 请求体
     * @param: timeout 超时时间
     * @since jdk11
     */
    public String doPut(String url, Map<String, String> headerMap, String requestBody, long timeout) throws IOException, InterruptedException {
        return doPut(url, headerMap, requestBody, timeout, String.class);
    }

    /**
     * 同步PUT请求，通过FORM传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form 表单
     * @param: timeout 超时时间
     * @since jdk11
     */
    public String doPut(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout) throws IOException, InterruptedException {
        return doPut(url, headerMap, form, timeout, String.class);
    }

    /**
     * 同步PUT请求，通过请求体传送数据，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: requestBody 请求体
     * @param: timeout 超时时间
     * @param: resClass 返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @since jdk11
     */
    public <T> T doPut(String url, Map<String, String> headerMap, String requestBody, long timeout, Class<T> resClass) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPutRequest(url, headerMap, requestBody, timeout);
        return getResData(httpRequest, resClass);
    }

    /**
     * 同步PUT请求，通过FORM传送数据，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form form表单
     * @param: timeout 超时时间
     * @param: resClass 返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @since jdk11
     */
    public <T> T doPut(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout, Class<T> resClass) throws IOException, InterruptedException {
        return doPutResponse(url, headerMap, form, timeout, resClass).body();
    }

    /**
     * 同步Put请求，通过请求体传送数据，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: requestBody 请求体
     * @param: timeout 超时时间
     * @param: resClass 返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @since jdk11
     */
    public <T> HttpResponse<T> doPutResponse(String url, Map<String, String> headerMap, String requestBody, long timeout, Class<T> resClass) throws IOException, InterruptedException {
        return doPutResponse(url, headerMap, HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8), timeout, resClass);
    }

    /**
     * 同步Put请求，通过FORM表单传送数据，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form 表单
     * @param: timeout 超时时间
     * @param: resClass 返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @since jdk11
     */
    public <T> HttpResponse<T> doPutResponse(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout, Class<T> resClass) throws IOException, InterruptedException {
        String[] headers = createHeader(headerMap, "application/x-www-form-urlencoded");
        Map<String, String> newHeader = new HashMap<>();
        for (int i = 0; i < headers.length; i = i + 2) {
            newHeader.put(headers[i], headers[i + 1]);
        }
        HttpRequest httpRequest = buildPutRequest(url, newHeader, form, timeout);
        return getRes(httpRequest, resClass);
    }

    /**
     * 同步Put请求，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: bodyPublisher 请求体
     * @param: timeout 超时时间
     * @param: resClass 返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @since jdk11
     */
    public <T> HttpResponse<T> doPutResponse(String url, Map<String, String> headerMap, HttpRequest.BodyPublisher bodyPublisher, long timeout, Class<T> resClass) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPutRequest(url, headerMap, bodyPublisher, timeout);
        return getRes(httpRequest, resClass);
    }

    /**
     * 异步PUT请求，通过form表单传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form 表单
     * @param: timeout 超时时间
     * @since jdk11
     */
    public CompletableFuture<HttpResponse<byte[]>> doPutByteResponseAsync(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout) {
        HttpRequest httpRequest = buildPutRequest(url, headerMap, form, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * 异步PUT请求，通过form表单传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form 表单
     * @param: timeout 超时时间
     * @since jdk11
     */
    public CompletableFuture<HttpResponse<InputStream>> doPutInputStreamResponseAsync(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout) {
        HttpRequest httpRequest = buildPutRequest(url, headerMap, form, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
    }

    /**
     * 异步PUT请求，通过form表单传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: form 表单
     * @param: timeout 超时时间
     * @since jdk11
     */
    public CompletableFuture<HttpResponse<String>> doPutStringResponseAsync(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout) {
        HttpRequest httpRequest = buildPutRequest(url, headerMap, form, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 异步PUT请求，通过请求体传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: requestBody 请求体
     * @param: timeout 超时时间
     * @since jdk11
     */
    public CompletableFuture<HttpResponse<byte[]>> doPutByteResponse(String url, Map<String, String> headerMap, String requestBody, long timeout) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPutRequest(url, headerMap, requestBody, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * 异步Put请求，通过请求体传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: requestBody 请求体
     * @param: timeout 超时时间
     * @since jdk11
     */
    public CompletableFuture<HttpResponse<InputStream>> doPutInputStreamResponse(String url, Map<String, String> headerMap, String requestBody, long timeout) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPutRequest(url, headerMap, requestBody, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
    }

    /**
     * 异步Put请求，通过请求体传送数据
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: requestBody 请求体
     * @param: timeout 超时时间
     * @since jdk11
     */
    public CompletableFuture<HttpResponse<String>> doPutStringResponseAsync(String url, Map<String, String> headerMap, String requestBody, long timeout) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPutRequest(url, headerMap, requestBody, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
    }
    //=================================PUT END========================================//


    //=================================DELETE BEGIN========================================//
    /**
     * @Author: zhuquanwen
     * @description: 同步DELETE请求，返回值解析为字符串
     * @date: 2021/11/29 21:07
     * @param: url 访问URL
     * @exception: IOException IO异常
     * @exception: InterruptedException
     * @return: java.lang.String
     */
    public String doDelete(String url) throws IOException, InterruptedException {
        return doDelete(url, Map.of());
    }

    /**
     * @Author: zhuquanwen
     * @description: 同步DELETE请求，返回值解析为字符串
     * @date: 2021/11/29 21:07
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @exception: IOException IO异常
     * @exception: InterruptedException
     * @return: java.lang.String
     */
    public String doDelete(String url, Map<String, String> headerMap) throws IOException, InterruptedException {
        return doDelete(url, headerMap, httpClientProps.getDefaultReadTimeout());
    }

    /**
     * @Author: zhuquanwen
     * @description: 同步DELETE请求，返回值解析为字符串
     * @date: 2021/11/29 21:07
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: timeout 超时时间
     * @exception: IOException IO异常
     * exception: InterruptedException
     * @return: java.lang.String
     */
    public String doDelete(String url, Map<String, String> headerMap, long timeout) throws IOException, InterruptedException {
        return doDelete(url, headerMap, timeout, String.class);
    }

    /**
     * @Author: zhuquanwen
     * @description: 同步DELETE请求，返回值支持的解析类型有byte[]、String、InputStream
     * @date: 2021/11/29 21:07
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: timeout 超时时间
     * @param: resClass 返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @exception: IOException IO异常
     * @exception: InterruptedException
     * @return: java.lang.String
     */
    public <T> T doDelete(String url, Map<String, String> headerMap, long timeout, Class<T> resClass) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildDeleteRequest(url, headerMap, timeout);
        return getResData(httpRequest, resClass);
    }

    /**
     * 同步DELETE请求，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @return java.net.http.HttpResponse<T>
     * @throws
     * @version 1.0
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: timeout 超时时间
     * @param: resClass 返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @since jdk11
     */
    public <T> HttpResponse<T> doDeleteResponse(String url, Map<String, String> headerMap, long timeout, Class<T> resClass) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildDeleteRequest(url, headerMap, timeout);
        return getRes(httpRequest, resClass);
    }

    /**
     * 同步DELETE请求，返回byte[]
     * @version 1.0
     * @since jdk11
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: timeout 超时时间
     * @throws
     * @return java.util.concurrent.CompletableFuture<java.net.http.HttpResponse<byte[]>>
     */
    public CompletableFuture<HttpResponse<byte[]>> doDeleteByteResponseAsync(String url, Map<String, String> headerMap, long timeout) {
        HttpRequest httpRequest = buildDeleteRequest(url, headerMap, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * 同步DELETE请求，返回String
     * @version 1.0
     * @since jdk11
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: timeout 超时时间
     * @throws
     * @return java.util.concurrent.CompletableFuture<java.net.http.HttpResponse<byte[]>>
     */
    public CompletableFuture<HttpResponse<String>> doDeleteStringResponseAsync(String url, Map<String, String> headerMap, long timeout) {
        HttpRequest httpRequest = buildDeleteRequest(url, headerMap, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 同步Delete请求，返回InputStream
     * @version 1.0
     * @since jdk11
     * @date 2021/11/30
     * @param: url 访问URL
     * @param: headerMap header键值对
     * @param: timeout 超时时间
     * @throws
     * @return java.util.concurrent.CompletableFuture<java.net.http.HttpResponse<byte[]>>
     */
    public CompletableFuture<HttpResponse<InputStream>> doDeleteInputStreamResponseAsync(String url, Map<String, String> headerMap, long timeout) {
        HttpRequest httpRequest = buildDeleteRequest(url, headerMap, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
    }
    //=================================DELETE END========================================//


    /**
     * 获取Http客户端
     *
     * @return HttpClient
     * @version 1.0
     * @date 2021/11/29
     * @since jdk11
     */
    public HttpClient getClient() {
        return client;
    }

    private <T> T getResData(HttpRequest httpRequest, Class<T> resClass) throws IOException, InterruptedException {
        T t = null;
        if (byte[].class == resClass) {
            t = (T) client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray()).body();
        } else if (String.class == resClass) {
            t = (T) client.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        } else if (InputStream.class == resClass) {
            t = (T) client.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream()).body();
        } else {
            throw new UnsupportedOperationException(MessageFormat.format("不支持的返回类型:[{0}]", resClass));
        }
        return t;
    }

    private <T> HttpResponse<T> getRes(HttpRequest httpRequest, Class<T> resClass) throws IOException, InterruptedException {
        HttpResponse<T> response = null;
        if (byte[].class == resClass) {
            response = (HttpResponse<T>) client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
        } else if (String.class == resClass) {
            response = (HttpResponse<T>) client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } else if (InputStream.class == resClass) {
            response = (HttpResponse<T>) client.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        } else {
            throw new UnsupportedOperationException(MessageFormat.format("不支持的返回类型:[{0}]", resClass));
        }
        return response;
    }

    public HttpRequest buildGetRequest(String url, Map<String, String> headerMap, long timeout) {
        return HttpRequest.newBuilder()
                .GET()
                .headers(createHeader(headerMap, httpClientProps.defaultContentType))
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(timeout))
                .build();
    }

    public HttpRequest buildDeleteRequest(String url, Map<String, String> headerMap, long timeout) {
        return HttpRequest.newBuilder()
                .DELETE()
                .headers(createHeader(headerMap, httpClientProps.defaultContentType))
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(timeout))
                .build();
    }

    public HttpRequest buildPostRequest(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout) {
        StringJoiner sj = new StringJoiner("&");
        form.forEach((k, v) -> {
            sj.add(k + "=" + v.toString());
        });
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(sj.toString(), StandardCharsets.UTF_8);
        return buildPostRequest(url, headerMap, bodyPublisher, timeout);
    }

    public HttpRequest buildPutRequest(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout) {
        StringJoiner sj = new StringJoiner("&");
        form.forEach((k, v) -> {
            sj.add(k + "=" + v.toString());
        });
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(sj.toString(), StandardCharsets.UTF_8);
        return buildPutRequest(url, headerMap, bodyPublisher, timeout);
    }

    public HttpRequest buildPostRequest(String url, Map<String, String> headerMap, String requestBody, long timeout) {
        return buildPostRequest(url, headerMap, HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8), timeout);
    }

    public HttpRequest buildPutRequest(String url, Map<String, String> headerMap, String requestBody, long timeout) {
        return buildPutRequest(url, headerMap, HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8), timeout);
    }

    public HttpRequest buildPostRequest(String url, Map<String, String> headerMap, HttpRequest.BodyPublisher bodyPublisher, long timeout) {
        return HttpRequest.newBuilder()
                .POST(bodyPublisher)
                .headers(createHeader(headerMap, httpClientProps.defaultContentType))
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(timeout))
                .build();
    }

    public HttpRequest buildPutRequest(String url, Map<String, String> headerMap, HttpRequest.BodyPublisher bodyPublisher, long timeout) {
        return HttpRequest.newBuilder()
                .PUT(bodyPublisher)
                .headers(createHeader(headerMap, httpClientProps.defaultContentType))
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(timeout))
                .build();
    }

    public HttpRequest buildUploadRequest(String url, Map<String, String> headerMap, Map<String, Object> formData, long timeout) throws IOException {
        String multipartFormDataBoundary = "Java11HttpClientFormBoundary";
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setCharset(StandardCharsets.UTF_8);
        if (formData != null) {
            formData.forEach((k, v) -> {
                if (v instanceof FileInfo) {
                    handleFileInfo(k, (FileInfo) v, builder, multipartFormDataBoundary);
                } else if (v.getClass() == FileInfo[].class) {
                    for (FileInfo fileInfo : ((FileInfo[]) v)) {
                        handleFileInfo(k, fileInfo, builder, multipartFormDataBoundary);
                    }
                } else {
                    builder.addTextBody(k, v.toString(), ContentType.create("text/plain", StandardCharsets.UTF_8));
                }
            });
        }
        HttpEntity httpEntity = builder.build();
        //todo 如果调用 .getContent获取InputStream会报文件过大的错误
        //todo 这里暂时只能转换一次了,会将文件转为内存，再写出。占用大量内存。。。。。
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        httpEntity.writeTo(baos);
        return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofByteArray(baos.toByteArray()))
                .headers(createHeader(headerMap, "multipart/form-data; boundary=" + multipartFormDataBoundary))
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(timeout))
                .build();
    }

    private void handleFileInfo(String k, FileInfo fi, MultipartEntityBuilder builder, String multipartFormDataBoundary) {
        Object data = fi.getData();
        ContentType contentType = ContentType.create("application/octet-stream", StandardCharsets.UTF_8);
        if (data instanceof File) {
            //文件
            builder.addPart(k, new FileBody((File) data, contentType, fi.getFileName()))
                    .setBoundary(multipartFormDataBoundary);
        } else if (data instanceof String) {
            //文件路径
            builder.addPart(k, new FileBody(new File((String) data), contentType, fi.getFileName()))
                    .setBoundary(multipartFormDataBoundary);
        } else if (data.getClass() == byte[].class) {
            //文件的字节
            builder.addBinaryBody(k, (byte[]) data, contentType, fi.getFileName());
        }  else if (InputStream.class.isAssignableFrom(data.getClass())) {
            //输入流
            builder.addBinaryBody(k, (InputStream) data, contentType, fi.getFileName());
        } else {
            throw new UnsupportedOperationException(MessageFormat.format("不支持的文件格式：[{0}]", data.getClass()));
        }
    }

    /**
     * 文件格式的封装，也支持以FileInfo[]的形式以一个key上传多个文件
     * */
    public static class FileInfo<T extends Object> {
        /**
         * File、String(文件路径)、InputStream、byte[]
         * */
        private T data;

        /**
         * 文件名
         * */
        private String fileName;


        public FileInfo() {}

        public FileInfo(T data, String fileName) {
            this.data = data;
            this.fileName = fileName;

        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }

    private String[] createHeader(Map<String, String> headerMap, String contentType) {
        if (headerMap == null) {
            headerMap = new HashMap<>();
            headerMap.put("Content-Type", contentType);
        } else {
            headerMap = new HashMap<>(headerMap);
            Set<String> headerKeys = headerMap.keySet();
            if (headerKeys.stream().noneMatch("Content-Type"::equalsIgnoreCase)) {
                headerMap.put("Content-Type", contentType);
            }
        }
        String[] result = new String[headerMap.size() * 2];
        int index = 0;
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            result[index++] = entry.getKey();
            result[index++] = entry.getValue();
        }
        return result;
    }


    public static class HttpClientProps {


        /**
         * http版本
         */
        private HttpClient.Version version = HttpClient.Version.HTTP_1_1;

        /**
         * 转发策略
         */
        private HttpClient.Redirect redirect = HttpClient.Redirect.NORMAL;

        /**
         * 线程池
         */
        private Executor executor;

        /**
         * 认证
         */
        private Authenticator authenticator;

        /**
         * 代理
         */
        private ProxySelector proxySelector;

        /**
         * cookiehandler
         */
        private CookieHandler cookieHandler;

        /**
         * sslContext
         */
        private SSLContext sslContext;

        /**
         * sslParams
         */
        private SSLParameters sslParameters;

        /**
         * 连接超时时间毫秒
         */
        private int connectTimeout = 10000;

        /**
         * 默认读取数据超时时间
         */
        private int defaultReadTimeout = 1200000;

        /**
         * 默认content-type
         */
        private String defaultContentType = "application/json";


        public HttpClientProps() {
            TrustManager[] trustAllCertificates = new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null; // Not relevant.
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    // TODO Auto-generated method stub
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    // TODO Auto-generated method stub
                }
            }};
            sslParameters = new SSLParameters();
            sslParameters.setEndpointIdentificationAlgorithm("");


            try {
                sslContext = SSLContext.getInstance("TLS");
                System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");//取消主机名验证
                sslContext.init(null, trustAllCertificates, new SecureRandom());
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            }

        }


        public HttpClient.Version getVersion() {
            return version;
        }

        public void setVersion(HttpClient.Version version) {
            this.version = version;
        }

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }


        public HttpClient.Redirect getRedirect() {
            return redirect;
        }

        public void setRedirect(HttpClient.Redirect redirect) {
            this.redirect = redirect;
        }

        public Executor getExecutor() {
            return executor;
        }

        public void setExecutor(Executor executor) {
            this.executor = executor;
        }

        public Authenticator getAuthenticator() {
            return authenticator;
        }

        public void setAuthenticator(Authenticator authenticator) {
            this.authenticator = authenticator;
        }

        public ProxySelector getProxySelector() {
            return proxySelector;
        }

        public void setProxySelector(ProxySelector proxySelector) {
            this.proxySelector = proxySelector;
        }

        public CookieHandler getCookieHandler() {
            return cookieHandler;
        }

        public void setCookieHandler(CookieHandler cookieHandler) {
            this.cookieHandler = cookieHandler;
        }

        public int getDefaultReadTimeout() {
            return defaultReadTimeout;
        }

        public void setDefaultReadTimeout(int defaultReadTimeout) {
            this.defaultReadTimeout = defaultReadTimeout;
        }

        public String getDefaultContentType() {
            return defaultContentType;
        }

        public void setDefaultContentType(String defaultContentType) {
            this.defaultContentType = defaultContentType;
        }
    }
}

