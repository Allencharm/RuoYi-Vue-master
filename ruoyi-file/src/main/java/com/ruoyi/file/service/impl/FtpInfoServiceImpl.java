package com.ruoyi.file.service.impl;

import com.ruoyi.file.bean.FtpInfo;
import com.ruoyi.file.dao.FtpInfoMapper;
import com.ruoyi.file.service.FtpInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/7 18:59
 * @description:
 */
@Service
public class FtpInfoServiceImpl implements FtpInfoService {

    @Autowired
    private FtpInfoMapper ftpInfoMapper;

    public int insertFtpInfo(FtpInfo record){
        return this.ftpInfoMapper.insert(record);
    }

    public int updateFtpInfo(FtpInfo record){
        return this.ftpInfoMapper.update(record);
    }

    public int deleteFtpInfo(String ftpId){
        return this.ftpInfoMapper.delete(ftpId);
    }

    public FtpInfo selectFtpInfoByFtpId(String ftpId){
        return this.ftpInfoMapper.selectFtpInfoByFtpId(ftpId);
    }

    public List<FtpInfo> selectFtpInfoList(FtpInfo record) {
        return this.ftpInfoMapper.selectFtpInfoList(record);
    }

}
