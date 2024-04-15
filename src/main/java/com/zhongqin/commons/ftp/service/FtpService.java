package com.zhongqin.commons.ftp.service;

import com.zhongqin.commons.ftp.properties.FtpClientProperties;
import org.apache.commons.net.ftp.FTPFile;

/**
 * @author Kevin
 * @date 2020/11/23 13:38
 */
public interface FtpService {

    /**
     * 删除文件
     *
     * @param ftpClientProperties
     * @param fileName            要删除的文件名称
     */
    void deleteFile(FtpClientProperties ftpClientProperties, String fileName);

    /**
     * 获取ftp服务器上面指定目录下所有文件并返回
     *
     * @param ftpClientProperties
     * @return
     */
    FTPFile[] getFtpFiles(FtpClientProperties ftpClientProperties);

    /**
     * 下载指定文件名称的文件
     *
     * @param ftpClientProperties
     * @param fileName            需要下载的文件名称
     * @param localPath           下载后的文件路径
     */
    void downloadFile(FtpClientProperties ftpClientProperties, String fileName, String localPath);

    /**
     * 下载文件
     *
     * @param ftpClientProperties
     * @param fileName            需要下载的文件名称
     * @param localPath           下载后的文件路径
     * @param isDelete            是否删除文件
     * @param isContainsFileName  区分下载文件时采用fileName全部匹配/包含内容
     */
    void downloadFile(FtpClientProperties ftpClientProperties, String fileName, String localPath, boolean isDelete, boolean isContainsFileName);

    /**
     * 下载所有文件
     *
     * @param ftpClientProperties
     * @param localPath
     * @param isDelete            是否删除文件
     */
    void downloadFile(FtpClientProperties ftpClientProperties, String localPath, boolean isDelete);

    /**
     * 上传文件
     *
     * @param ftpClientProperties
     * @param filePath            文件路径
     * @param fileName            文件名称
     */
    void uploadFile(FtpClientProperties ftpClientProperties, String filePath, String fileName);
}
