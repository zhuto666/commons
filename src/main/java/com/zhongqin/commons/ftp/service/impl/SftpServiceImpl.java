package com.zhongqin.commons.ftp.service.impl;

import com.zhongqin.commons.exception.CustomException;
import com.zhongqin.commons.ftp.properties.SftpClientProperties;
import com.zhongqin.commons.ftp.service.SftpService;
import com.zhongqin.commons.util.ExceptionUtil;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Vector;

/**
 * @author Kevin
 * @date 2020/11/23 13:43
 */
@Slf4j
@Service
public class SftpServiceImpl implements SftpService {

    @Override
    public void deleteFile(SftpClientProperties sftpClientProperties, String fileName) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = connectSftp(sftpClientProperties);
            channelSftp.rm(fileName);
        } catch (Exception ex) {
            log.error("download file failure!", ex);
            throw new CustomException("delete file failure!" + ExceptionUtil.getStackTrace(ex));
        } finally {
            disConnect(channelSftp);
        }
    }

    @Override
    public void downloadFile(SftpClientProperties sftpClientProperties, String fileName, String localPath, boolean isDelete) {
        ChannelSftp channelSftp = null;
        try {
            File localDirFile = new File(localPath);
            // 判断本地目录是否存在，不存在需要新建各级目录
            if (!localDirFile.exists()) {
                localDirFile.mkdirs();
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(localPath).append(File.separator).append(fileName);
            channelSftp = connectSftp(sftpClientProperties);
            OutputStream output = new FileOutputStream(new File(stringBuilder.toString()));
            channelSftp.get(fileName, output);
            IOUtils.closeQuietly(output);
            // 是否删除源文件
            if (isDelete) {
                channelSftp.rm(fileName);
            }
        } catch (Exception ex) {
            log.error("download file failure!", ex);
            throw new CustomException("download file failure!" + ExceptionUtil.getStackTrace(ex));
        } finally {
            disConnect(channelSftp);
        }
    }

    @Override
    public void downloadFile(SftpClientProperties sftpClientProperties, String localPath, boolean isDelete) {
        ChannelSftp channelSftp = null;
        try {
            File localDirFile = new File(localPath);
            // 判断本地目录是否存在，不存在需要新建各级目录
            if (!localDirFile.exists()) {
                localDirFile.mkdirs();
            }
            channelSftp = connectSftp(sftpClientProperties);
            Vector<ChannelSftp.LsEntry> lsEntries = channelSftp.ls(sftpClientProperties.getRemotePath());
            log.info("远程目录下的文件为[{}]", lsEntries);
            for (ChannelSftp.LsEntry entry : lsEntries) {
                String fileName = entry.getFilename();
                if (checkFileName(fileName)) {
                    continue;
                }
                String remoteFileName = getRemoteFilePath(sftpClientProperties.getRemotePath(), fileName);
                channelSftp.get(remoteFileName, localPath);
                // 是否删除源文件
                if (isDelete) {
                    channelSftp.rm(fileName);
                }
            }
        } catch (Exception ex) {
            log.error("download file failure!", ex);
            throw new CustomException("download file failure!" + ExceptionUtil.getStackTrace(ex));
        } finally {
            disConnect(channelSftp);
        }
    }

    @Override
    public void uploadFile(SftpClientProperties sftpClientProperties, String filePath, String fileName) {
        ChannelSftp channelSftp = connectSftp(sftpClientProperties);
        BufferedInputStream inStream = null;
        File uploadFile = new File(filePath + File.separator + fileName);
        try {
            inStream = new BufferedInputStream(new FileInputStream(uploadFile));
            channelSftp.put(inStream, uploadFile.getName());
        } catch (FileNotFoundException ex) {
            log.error("file not found!{}", uploadFile);
            throw new CustomException("file not found!" + ExceptionUtil.getStackTrace(ex));
        } catch (Exception ex) {
            log.error("upload file failure!", ex);
            throw new CustomException("upload file failure!" + ExceptionUtil.getStackTrace(ex));
        } finally {
            disConnect(channelSftp);
            IOUtils.closeQuietly(inStream);
        }
    }

    private String getRemoteFilePath(String remoteFilePath, String fileName) {
        if (remoteFilePath.endsWith("/")) {
            return remoteFilePath.concat(fileName);
        } else {
            return remoteFilePath.concat("/").concat(fileName);
        }
    }

    private boolean checkFileName(String fileName) {
        if (".".equals(fileName) || "..".equals(fileName)) {
            return true;
        }
        return false;
    }

    /**
     * 连接SFTP切换工作目录,并返回ChannelSftp对象
     *
     * @param sftpClientProperties
     * @return
     */
    private ChannelSftp connectSftp(SftpClientProperties sftpClientProperties) {
        ChannelSftp channelSftp;
        try {
            // 创建JSch对象
            JSch jsch = new JSch();
            // 设置密钥和密码 ,支持密钥的方式登陆
            if (StringUtils.isNotBlank(sftpClientProperties.getPrivateKey())) {
                if (StringUtils.isNotBlank(sftpClientProperties.getPassphrase())) {
                    // 设置带口令的密钥
                    jsch.addIdentity(sftpClientProperties.getPrivateKey(), sftpClientProperties.getPassphrase());
                } else {
                    // 设置不带口令的密钥
                    jsch.addIdentity(sftpClientProperties.getPrivateKey());
                }
            }
            log.info("Session created ... UserName:{} host:{} port:{} ", sftpClientProperties.getUsername(),
                    sftpClientProperties.getHost(), sftpClientProperties.getPort());
            // 根据用户名 主机ip 端口获取一个Session对象
            Session sshSession = jsch.getSession(sftpClientProperties.getUsername(), sftpClientProperties.getHost(), sftpClientProperties.getPort());
            // 设置第一次登陆的时候提示 可选值：(ask | yes | no)
            sshSession.setConfig("StrictHostKeyChecking", sftpClientProperties.getSessionStrictHostKeyChecking());
            // 设置连接密码
            sshSession.setPassword(sftpClientProperties.getPassword());
            // 通过Session建立链接
            sshSession.connect(sftpClientProperties.getSessionConnectTimeout());
            // 打开SFTP通道
            Channel channel = sshSession.openChannel(sftpClientProperties.getProtocol());
            // 建立SFTP通道的连接
            channel.connect(sftpClientProperties.getChannelConnectedTimeout());
            channelSftp = (ChannelSftp) channel;
            log.info("SSH Channel connected.");
            // 切换目录
            channelSftp.cd(sftpClientProperties.getRemotePath());
        } catch (Exception ex) {
            throw new CustomException(ExceptionUtil.getStackTrace(ex));
        }
        return channelSftp;
    }

    /**
     * 断掉连接
     *
     * @param channelSftp
     */
    public void disConnect(ChannelSftp channelSftp) {
        try {
            if (channelSftp != null) {
                channelSftp.disconnect();
                channelSftp.getSession().disconnect();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
