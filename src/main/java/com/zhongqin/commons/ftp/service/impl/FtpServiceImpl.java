package com.zhongqin.commons.ftp.service.impl;

import com.zhongqin.commons.exception.CustomException;
import com.zhongqin.commons.ftp.properties.FtpClientProperties;
import com.zhongqin.commons.ftp.service.FtpService;
import com.zhongqin.commons.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * @author Kevin
 * @date 2020/11/23 13:40
 */
@Slf4j
@Service
public class FtpServiceImpl implements FtpService {

    @Override
    public void deleteFile(FtpClientProperties ftpClientProperties, String fileName) {
        try {
            FTPClient ftpClient = connectFtp(ftpClientProperties);
            ftpClient.dele(fileName);
            ftpClient.logout();
        } catch (Exception ex) {
            log.error("delete file failure!", ex);
            throw new CustomException("delete file failure!" + ExceptionUtil.getStackTrace(ex));
        }
    }

    @Override
    public FTPFile[] getFtpFiles(FtpClientProperties ftpClientProperties) {
        FTPClient ftpClient = connectFtp(ftpClientProperties);
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles();
            ftpClient.logout();
            return ftpFiles;
        } catch (IOException ex) {
            log.error("get ftp files failure!", ex);
            throw new CustomException("get ftp files failure!" + ExceptionUtil.getStackTrace(ex));
        }
    }

    @Override
    public void downloadFile(FtpClientProperties ftpClientProperties, String fileName, String localPath) {
        FTPClient ftpClient = connectFtp(ftpClientProperties);
        File localDirFile = new File(localPath);
        // 判断本地目录是否存在，不存在需要新建各级目录
        if (!localDirFile.exists()) {
            localDirFile.mkdirs();
        }
        log.info("download file{}", fileName);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(localPath).append(File.separator).append(fileName);
        File localFile = new File(stringBuilder.toString());
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(localFile);
            ftpClient.retrieveFile(fileName, outputStream);
            IOUtils.closeQuietly(outputStream);
            ftpClient.logout();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void downloadFile(FtpClientProperties ftpClientProperties, String fileName, String localPath, boolean isDelete, boolean isContainsFileName) {
        try {
            // 连接FTP
            FTPClient ftpClient = connectFtp(ftpClientProperties);
            File localDirFile = new File(localPath);
            // 判断本地目录是否存在，不存在需要新建各级目录
            if (!localDirFile.exists()) {
                localDirFile.mkdirs();
            }
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                if (isContainsFileName) {
                    if (file.getName().contains(fileName)) {
                        log.info("download file{}", file.getName());
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(localPath).append(File.separator).append(file.getName());
                        File localFile = new File(stringBuilder.toString());
                        OutputStream outputStream = new FileOutputStream(localFile);
                        ftpClient.retrieveFile(file.getName(), outputStream);
                        IOUtils.closeQuietly(outputStream);
                        // 是否删除源文件
                        if (isDelete) {
                            ftpClient.dele(file.getName());
                        }
                    }
                } else {
                    if (fileName.equalsIgnoreCase(file.getName())) {
                        log.info("download file{}", file.getName());
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(localPath).append(File.separator).append(file.getName());
                        File localFile = new File(stringBuilder.toString());
                        OutputStream outputStream = new FileOutputStream(localFile);
                        ftpClient.retrieveFile(file.getName(), outputStream);
                        IOUtils.closeQuietly(outputStream);
                        // 是否删除源文件
                        if (isDelete) {
                            ftpClient.dele(file.getName());
                        }
                    }
                }
            }
            ftpClient.logout();
        } catch (Exception ex) {
            log.error("download file failure!", ex);
            throw new CustomException("download file failure!" + ExceptionUtil.getStackTrace(ex));
        }
    }

    @Override
    public void downloadFile(FtpClientProperties ftpClientProperties, String localPath, boolean isDelete) {
        try {
            FTPClient ftpClient = connectFtp(ftpClientProperties);
            File localDirFile = new File(localPath);
            // 判断本地目录是否存在，不存在需要新建各级目录
            if (!localDirFile.exists()) {
                localDirFile.mkdirs();
            }
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                log.info("download file{}", file.getName());
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(localPath).append(File.separator).append(file.getName());
                File localFile = new File(stringBuilder.toString());
                OutputStream outputStream = new FileOutputStream(localFile);
                ftpClient.retrieveFile(file.getName(), outputStream);
                IOUtils.closeQuietly(outputStream);
                // 是否删除源文件
                if (isDelete) {
                    ftpClient.dele(file.getName());
                }
            }
            ftpClient.logout();
        } catch (Exception ex) {
            log.error("download file failure!", ex);
            throw new CustomException("download file failure!" + ExceptionUtil.getStackTrace(ex));
        }
    }

    @Override
    public void uploadFile(FtpClientProperties ftpClientProperties, String filePath, String fileName) {
        BufferedInputStream inStream = null;
        File uploadFile = new File(filePath + File.separator + fileName);
        try {
            FTPClient ftpClient = connectFtp(ftpClientProperties);
            inStream = new BufferedInputStream(new FileInputStream(uploadFile));
            final int retryTimes = 3;
            for (int j = 0; j <= retryTimes; j++) {
                ftpClient.storeFile(uploadFile.getName(), inStream);
            }
            ftpClient.logout();
        } catch (FileNotFoundException ex) {
            log.error("file not found!{}", uploadFile);
            throw new CustomException("file not found!" + ExceptionUtil.getStackTrace(ex));
        } catch (Exception ex) {
            log.error("upload file failure!", ex);
            throw new CustomException("upload file failure!" + ExceptionUtil.getStackTrace(ex));
        } finally {
            IOUtils.closeQuietly(inStream);
        }
    }

    /**
     * 连接FTP切换工作目录,并返回FTPClient对象
     *
     * @param ftpClientProperties
     * @return
     */
    private FTPClient connectFtp(FtpClientProperties ftpClientProperties) {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding(ftpClientProperties.getEncoding());
        ftpClient.setDataTimeout(ftpClientProperties.getDataTimeout());
        ftpClient.setConnectTimeout(ftpClientProperties.getConnectTimeout());
        ftpClient.setControlKeepAliveTimeout(ftpClientProperties.getKeepAliveTimeout());
        try {
            ftpClient.connect(ftpClientProperties.getHost(), ftpClientProperties.getPort());
            int replyCode = ftpClient.getReplyCode();
            // 验证FTP服务器是否登录成功
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                ftpClient.disconnect();
                log.warn("ftpServer refused connection,replyCode:{}", replyCode);
                throw new CustomException("ftpServer refused connection, replyCode:" + replyCode);
            }
            if (!ftpClient.login(ftpClientProperties.getUsername(), ftpClientProperties.getPassword())) {
                log.warn("ftpClient login failed... username is {}; password: {}", ftpClientProperties.getUsername(), ftpClientProperties.getPassword());
                throw new CustomException("ftpClient login failed...");
            }
            ftpClient.setBufferSize(ftpClientProperties.getBufferSize());
            ftpClient.setFileType(ftpClientProperties.getTransferFileType());
            if (ftpClientProperties.isPassiveMode()) {
                ftpClient.enterLocalPassiveMode();
            }
            // 改变工作路径
            ftpClient.changeWorkingDirectory(ftpClientProperties.getRemotePath());
            // 开启一个端口用来传输数据
            ftpClient.enterLocalPassiveMode();
        } catch (IOException ex) {
            log.error("create ftp connection failed...", ex);
            throw new CustomException("create ftp connection failed..." + ExceptionUtil.getStackTrace(ex));
        }
        return ftpClient;
    }
}
