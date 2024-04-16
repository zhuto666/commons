package com.zhongqin.commons.util;

import com.zhongqin.commons.exception.CustomException;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author Kevin
 * @version 1.0
 * @date 2023/11/7 14:24 星期二
 */
public class DownLoadUtil {

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     */
    public static void downloadFile(String filePath, String originFileName) {
        File file = new File(filePath);
        // 取得文件名
        if (StringUtils.isBlank(originFileName)) {
            originFileName = file.getName();
        }
        if (file.exists() && file.isFile()) {
            throw new CustomException("No resources found.");
        }
        try {
            HttpServletResponse response = SpringWebUtil.getResponse();
            // 设置文件输出类型
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(originFileName, StandardCharsets.UTF_8));
            // 获取输入流
            URL url = new URL(filePath);
            try (BufferedInputStream bis = new BufferedInputStream(url.openStream());
                 BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {
                // 输出流
                IOUtils.copy(bis, bos);
                bos.flush();
            }
        } catch (IOException e) {
            throw new CustomException("Download error.");
        }
    }

}
