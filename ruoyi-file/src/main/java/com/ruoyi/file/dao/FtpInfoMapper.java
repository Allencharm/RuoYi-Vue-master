package com.ruoyi.file.dao;

import com.ruoyi.file.bean.FtpInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/7 19:00
 * @description:
 */
@Repository
public interface FtpInfoMapper {
    int insert(FtpInfo record);

    int update(FtpInfo record);

    int delete(String ftpId);

    FtpInfo selectFtpInfoByFtpId(String ftpId);

    List<FtpInfo> selectFtpInfoList(FtpInfo record);
}
