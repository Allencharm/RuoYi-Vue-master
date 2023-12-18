package com.ruoyi.file.tool;

import com.jcraft.jsch.ChannelSftp;
import com.ruoyi.file.bean.FtpInfo;
import com.ruoyi.file.bean.SysUser;
import com.ruoyi.file.service.FtpInfoService;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/8 20:52
 * @description:获取ftp连接的信息
 */
@Component
public class DataUtil {


    @Autowired
    private FtpInfoService ftpInfoService;

    public static SysUser getUser() {
        return (SysUser) HttpWebUtil.getSessionAttribute("sysUser");
    }

    public static String getUserId() {
        SysUser user = (SysUser) HttpWebUtil.getSessionAttribute("sysUser");
        return user == null ? null : user.getUserId();
    }

    public static String getUserName() {
        SysUser user = (SysUser) HttpWebUtil.getSessionAttribute("sysUser");
        return user == null ? null : user.getUserName();
    }

    public ChannelSftp getChannelSftp(String ftpId) throws Exception {
        Map sessionMap = (Map) HttpWebUtil.getSessionAttribute(ftpId);
        ChannelSftp sftp = (ChannelSftp) sessionMap.get("sftpObj");
        if (null == sftp || !sftp.isConnected()) {
            FtpInfo sftpBean = ftpInfoService.selectFtpInfoByFtpId(ftpId);
            sftp = SftpPoolUtil.openSftpConnect(sftpBean);
            HttpWebUtil.setSessionAttribute(ftpId, sftp);
        }
        return sftp;
    }

    public static FtpInfo getSftpInfo(String ftpId) {
        Map sessionMap = (Map) HttpWebUtil.getSessionAttribute(ftpId);
        FtpInfo ftpInfo = (FtpInfo) sessionMap.get("ftpInfo");
        return ftpInfo;
    }

    public FTPClient getFTPClient(String ftpId) {
        Map<String, Object> sessionMap = (Map<String, Object>) HttpWebUtil.getSessionAttribute(ftpId);
        FTPClient ftpClient = null;
        if (null == sessionMap || null == sessionMap.get("ftpObj")) {
            FtpInfo ftpInfo = ftpInfoService.selectFtpInfoByFtpId(ftpId);
            ftpClient = FtpPoolUtil.getFtpClient(ftpInfo);
            HttpWebUtil.setSessionAttribute(ftpId, ftpClient);
        } else {
            ftpClient = (FTPClient) sessionMap.get("ftpObj");
        }
        return ftpClient;
    }
}
