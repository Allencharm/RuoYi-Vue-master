package com.ruoyi.file.service;

import com.ruoyi.file.bean.FtpInfo;

import java.util.List;

/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/7 18:58
 * @description:ftp服务类
 */
public interface FtpInfoService {
    int insertFtpInfo(FtpInfo record);

    int updateFtpInfo(FtpInfo record);

    int deleteFtpInfo(String ftpId);

    FtpInfo selectFtpInfoByFtpId(String ftpId);

    List<FtpInfo> selectFtpInfoList(FtpInfo record);
}
