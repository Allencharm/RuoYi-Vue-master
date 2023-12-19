package com.ruoyi.file.bean;

import lombok.Data;

/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/7 18:54
 * @description:
 */
@Data
public class FtpInfo extends BaseBean{


    private String ftpId;

    private String ftpName;

    private String ftpUser;

    private String ftpPass;

    private String ftpPkey;

    private String ftpHost;

    private Integer ftpPort;

    private String ftpMode;

    private String ftpType;

    public FtpInfo() {
    }

    public FtpInfo(String host, int port, String user) {
        super();
        this.ftpHost = host;
        this.ftpPort = port;
        this.ftpUser = user;
    }

    public FtpInfo(String host, int port, String user, String pass) {
        super();
        this.ftpHost = host;
        this.ftpPort = port;
        this.ftpUser = user;
        this.ftpPass = pass;
    }

    public FtpInfo(String host, int port, String user, String pass, String pkey) {
        super();
        this.ftpHost = host;
        this.ftpPort = port;
        this.ftpUser = user;
        this.ftpPass = pass;
        this.ftpPkey = pkey;
    }

}
