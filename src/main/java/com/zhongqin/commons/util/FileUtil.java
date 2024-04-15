package com.zhongqin.commons.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作文件工具类
 *
 * @author Kevin
 * @date 2020/11/24 14:11
 */
@Slf4j
public class FileUtil {

    /**
     * 读取文件
     *
     * @param filePath 文件路径
     * @param fileName 文件名称
     * @return
     */
    public static String readFile(String filePath, String fileName) {
        byte[] buffer = new byte[1024];
        int bytesRead;
        BufferedInputStream inStream = null;
        File file = new File(filePath + File.separator + fileName);
        try {
            // 创建BufferedInputStream 对象
            inStream = new BufferedInputStream(new FileInputStream(file));
            StringBuffer stringBuffer = new StringBuffer();
            while ((bytesRead = inStream.read(buffer)) != -1) {
                // 将读取的字节转为字符串对象
                String chunk = new String(buffer, 0, bytesRead);
                stringBuffer.append(chunk);
            }
            return stringBuffer.toString();
        } catch (Exception ex) {
            log.error("readFile file failure!", ex);
        } finally {
            IOUtils.closeQuietly(inStream);
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @param fileName 文件名称
     * @return
     */
    public static boolean deleteFile(String filePath, String fileName) {
        File file = new File(filePath + File.separator + fileName);
        return file.delete();
    }

    /**
     * 移动文件
     *
     * @param originFilePath 源文件地址
     * @param originFileName 源文件名称
     * @param moveDirectory  移动目录
     * @return
     */
    public static boolean moveFile(String originFilePath, String originFileName, String moveDirectory) {
        try {
            // 源文件
            File originFile = new File(originFilePath + File.separator + originFileName);
            // 目标目录
            File endDirection = new File(moveDirectory);
            // 如果目标目录不存在,则进行创建
            if (!endDirection.exists()) {
                endDirection.mkdirs();
            }
            // 目标目录+源文件名称
            File endFile = new File(endDirection + File.separator + originFile.getName());
            // 移动文件
            return originFile.renameTo(endFile);
        } catch (Exception ex) {
            log.error("readFile file failure!", ex);
        }
        return false;
    }

    /**
     * 获取某个目录下所有直接下级文件,不包括目录下的子目录的下的文件
     *
     * @param filePath
     * @return
     */
    public static List<String> getFiles(String filePath) {
        List<String> fileList = new ArrayList<>();
        File file = new File(filePath);
        File[] tempList = file.listFiles();
        for (File fil : tempList) {
            if (fil.isFile()) {
                // 文件名，不包含路径
                fileList.add(fil.getName());
            }
        }
        return fileList;
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
