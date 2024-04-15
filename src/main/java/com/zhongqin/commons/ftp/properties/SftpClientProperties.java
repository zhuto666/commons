package com.zhongqin.commons.ftp.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * sftp客服端连接配置
 *
 * @author Kevin
 * @date 2020/11/23 10:47
 */
@Getter
@Setter
public class SftpClientProperties {

    /**
     * sftp地址
     */
    private String host;

    /**
     * 端口号
     */
    private Integer port = 22;

    /**
     * 协议
     */
    private String protocol = "sftp";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 远程路径
     */
    private String remotePath = "/";

    /**
     * 密钥文件路径
     */
    private String privateKey;

    /**
     * 密钥的密码
     */
    private String passphrase;

    /**
     * 会话严格密钥检查
     */
    private String sessionStrictHostKeyChecking = "no";

    /**
     * session连接超时时间
     */
    private Integer sessionConnectTimeout = 15000;

    /**
     * channel连接超时时间
     */
    private Integer channelConnectedTimeout = 15000;
}
