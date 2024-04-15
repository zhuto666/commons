package com.zhongqin.commons.ftp.service;


import com.zhongqin.commons.ftp.properties.SftpClientProperties;

/**
 * @author Kevin
 * @date 2020/11/23 13:42
 */
public interface SftpService {
    /**
     * 删除文件
     *
     * @param sftpClientProperties
     * @param fileName             要删除的文件名称
     */
    void deleteFile(SftpClientProperties sftpClientProperties, String fileName);

    /**
     * 下载文件
     *
     * @param sftpClientProperties
     * @param fileName             需要下载的文件名称
     * @param localPath            下载后的文件路径
     * @param isDelete             是否删除文件
     */
    void downloadFile(SftpClientProperties sftpClientProperties, String fileName, String localPath, boolean isDelete);

    /**
     * 下载所有文件
     *
     * @param sftpClientProperties
     * @param localPath            下载后的文件路径
     * @param isDelete             是否删除文件
     */
    void downloadFile(SftpClientProperties sftpClientProperties, String localPath, boolean isDelete);

    /**
     * 上传文件
     *
     * @param sftpClientProperties
     * @param filePath             文件路径
     * @param fileName             文件名称
     */
    void uploadFile(SftpClientProperties sftpClientProperties, String filePath, String fileName);
}

