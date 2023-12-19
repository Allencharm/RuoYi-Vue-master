package com.ruoyi.file.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jcraft.jsch.ChannelSftp;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.file.bean.DataTableBean;
import com.ruoyi.file.bean.FTPListBean;
import com.ruoyi.file.bean.FtpInfo;
import com.ruoyi.file.bean.ResponseBean;
import com.ruoyi.file.service.FtpInfoService;
import com.ruoyi.file.tool.*;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/7 18:46
 * @description:ftp连接控制类
 */
@Controller
@RequestMapping("/rtb/ftp")
public class FtpInfoController extends BaseController {


    @Autowired
    private FtpInfoService ftpInfoService;

    @Anonymous
    @RequestMapping("/list")
//    @WebLog(msgs = LogMsgs.FTP0_LIST, type = LogType.ELSE_LOG)
    public String listFtp() {
        return "business/ftp_list";
    }

    @Anonymous
    @RequestMapping("/save")
//    @WebLog(msgs = LogMsgs.FTP0_SAVE, type = LogType.ELSE_LOG)
    public String saveFtp() {
        return "business/ftp_save";
    }

    @Anonymous
    @RequestMapping("/edit")
//    @WebLog(msgs = LogMsgs.FTP0_EDIT, type = LogType.ELSE_LOG)
    public ModelAndView editFtp(ModelAndView mv, String ftpId) {
        FtpInfo ftpInfo = this.ftpInfoService.selectFtpInfoByFtpId(ftpId);
        mv.addObject("ftpInfo", ftpInfo);
        mv.setViewName("business/ftp_edit");
        return mv;
    }

    @Anonymous
    @RequestMapping("/look")
//    @WebLog(msgs = LogMsgs.FTP0_LOOK, type = LogType.ELSE_LOG)
    public ModelAndView lookFtp(ModelAndView mv, String ftpId) {
        FtpInfo ftpInfo = this.ftpInfoService.selectFtpInfoByFtpId(ftpId);
        mv.addObject("ftpInfo", ftpInfo);
        mv.setViewName("business/ftp_look");
        return mv;
    }

    @Anonymous
    @RequestMapping("/file")
//    @WebLog(msgs = LogMsgs.FTP0_FILE, type = LogType.ELSE_LOG)
    public ModelAndView listFile(ModelAndView mv, String ftpId, String ftpType) {
        try {
            // 三种协议的不同处理逻辑
            switch (ftpType) {
                case "1":
                    FTPClient ftpClient = (FTPClient) HttpWebUtil.getSessionAttribute("ftpObj");
                    if (null == ftpClient || !ftpClient.isConnected()) {
                        FtpInfo ftpInfo = this.ftpInfoService.selectFtpInfoByFtpId(ftpId);
                        ftpClient = FtpPoolUtil.getFtpClient(ftpInfo);
                        Map<String, Object> sessionMap = new HashMap<>(50);
                        sessionMap.put("ftpInfo", ftpInfo);
                        sessionMap.put("ftpObj", ftpClient);
                        HttpWebUtil.setSessionAttribute(ftpId, sessionMap);
                    }
                    break;
                case "2":
                    break;
                case "3":
                    ChannelSftp sftp = (ChannelSftp) HttpWebUtil.getSessionAttribute("sftpObj");
                    if (null == sftp || !sftp.isConnected()) {
                        FtpInfo ftpInfo = this.ftpInfoService.selectFtpInfoByFtpId(ftpId);
                        sftp = SftpPoolUtil.openSftpConnect(ftpInfo);
                        Map<String, Object> sessionMap = new HashMap<>();
                        sessionMap.put("ftpInfo", ftpInfo);
                        sessionMap.put("sftpObj", sftp);
                        HttpWebUtil.setSessionAttribute(ftpId, sessionMap);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
        }
        mv.addObject("ftpId", ftpId);
        mv.addObject("ftpType", ftpType);
        mv.setViewName("business/ftp_file");
        return mv;
    }

    @Anonymous
    @RequestMapping("/pick")
//    @WebLog(msgs = LogMsgs.FTP0_PICK, type = LogType.ELSE_LOG)
    public ModelAndView pickFile(ModelAndView mv, String ftpId, String ftpType, String currPath) {
        mv.addObject("ftpId", ftpId);
        mv.addObject("ftpType", ftpType);
        mv.addObject("currPath", currPath);
        mv.setViewName("business/ftp_upload");
        return mv;
    }

    @Anonymous
    @RequestMapping("/copy")
//    @WebLog(msgs = LogMsgs.FTP0_COPY, type = LogType.ELSE_LOG)
    public ModelAndView copyFile(ModelAndView mv, String ftpId, String ftpType, String filePath) {
        mv.addObject("ftpId", ftpId);
        mv.addObject("ftpType", ftpType);
        mv.addObject("filePath", filePath);
        mv.setViewName("business/ftp_copy");
        return mv;
    }

    @Anonymous
    @RequestMapping("/move")
//    @WebLog(msgs = LogMsgs.FTP0_MOVE, type = LogType.ELSE_LOG)
    public ModelAndView moveFile(ModelAndView mv, String ftpId, String ftpType, String filePath) {
        mv.addObject("ftpId", ftpId);
        mv.addObject("ftpType", ftpType);
        mv.addObject("filePath", filePath);
        mv.setViewName("business/ftp_move");
        return mv;
    }

    @Anonymous
    @ResponseBody
    @RequestMapping("/insert")
//    @WebLog(msgs = LogMsgs.FTP0_INSERT, type = LogType.ELSE_LOG)
    public ResponseBean insertFtp(FtpInfo ftpInfo) {
        ResponseBean responseBean = new ResponseBean();
        ftpInfo.setFtpId(KeyGeneratorUtil.getUUIDKey());
        ftpInfo.setCreateUser(DataUtil.getUserName());
        ftpInfo.setCreateTime(new Date());
        int insFlag = ftpInfoService.insertFtpInfo(ftpInfo);
        if (insFlag == 1) {
            responseBean.setMsgs("新增FTP成功");
        } else {
            responseBean.setMsgs("新增FTP失败");
        }
        return responseBean;
    }

    @Anonymous
    @ResponseBody
    @RequestMapping("/update")
//    @WebLog(msgs = LogMsgs.FTP0_UPDATE, type = LogType.ELSE_LOG)
    public ResponseBean updateFtp(FtpInfo ftpInfo) {
        ResponseBean responseBean = new ResponseBean();
        ftpInfo.setUpdateUser(DataUtil.getUserName());
        ftpInfo.setUpdateTime(new Date());
        int updFlag = ftpInfoService.updateFtpInfo(ftpInfo);
        if (updFlag == 1) {
            responseBean.setMsgs("编辑FTP成功");
        } else {
            responseBean.setMsgs("编辑FTP失败");
        }
        return responseBean;
    }

    @Anonymous
    @ResponseBody
    @RequestMapping("/delete")
//    @WebLog(msgs = LogMsgs.FTP0_DELETE, type = LogType.ELSE_LOG)
    public ResponseBean deleteFtp(String ftpId) {
        ResponseBean responseBean = new ResponseBean();
        int delFlag = ftpInfoService.deleteFtpInfo(ftpId);
        if (delFlag == 1) {
            responseBean.setMsgs("删除FTP成功");
        } else {
            responseBean.setMsgs("删除FTP失败");
        }
        return responseBean;
    }


    @Anonymous
    @ResponseBody
    @RequestMapping("/select")
//    @WebLog(msgs = LogMsgs.FTP0_SELECT, type = LogType.ELSE_LOG)
    public DataTableBean selectFtp(FtpInfo ftpInfo, int page, int limit) {
        DataTableBean tableData = new DataTableBean();
        Page<FtpInfo> pager = PageHelper.startPage(page, limit);
        List<FtpInfo> ftpInfoList = this.ftpInfoService.selectFtpInfoList(ftpInfo);
        tableData.setCode(200);
        tableData.setCount(pager.getTotal());
        tableData.setMsgs("");
        tableData.setData(ftpInfoList);
        return tableData;
    }



//    @Autowired
//    private FtpInfoService ftpInfoService;
//
//
//    /**
//     * 插入一个ftp的链接信息
//     * @param ftpInfo
//     * @return
//     */
//    @Anonymous
//    @ResponseBody
//    @PostMapping("/insert")
//    public ResponseBean insertFtp(FtpInfo ftpInfo) {
//        ResponseBean responseBean = new ResponseBean();
//        ftpInfo.setFtpId(KeyGeneratorUtil.getUUIDKey());
////        ftpInfo.setCreateUser(DataUtil.getUserName()); //哪位用户做的
//        ftpInfo.setCreateTime(new Date());
//        int insFlag = ftpInfoService.insertFtpInfo(ftpInfo);
//        if (insFlag == 1) {
//            responseBean.setMsgs("新增FTP成功");
//        } else {
//            responseBean.setMsgs("新增FTP失败");
//        }
//        return responseBean;
//    }
//
//    /**
//     * 查询ftp链接
//     * @param ftpInfo
//     * @param page
//     * @param limit
//     * @return
//     */
//    @Anonymous
//    @GetMapping("/select")
//    @ResponseBody
//    public FTPListBean selectFtp(FtpInfo ftpInfo, Integer page, Integer limit) {
//        FTPListBean tableData = new FTPListBean();
//        Page<FtpInfo> pager = PageHelper.startPage(page.intValue(), limit.intValue());
//        List<FtpInfo> ftpInfoList = this.ftpInfoService.selectFtpInfoList(ftpInfo);
//        tableData.setCode(200);
//        tableData.setCount(pager.getTotal());
//        tableData.setMsgs("");
//        tableData.setData(ftpInfoList);
//        return tableData;
//    }
//
//
//    /**
//     * 进行ftp链接
//     * @param reModel
//     * @param ftpId
//     * @param ftpType
//     * @return
//     */
//    @GetMapping("/file")
//    @Anonymous
//    @ResponseBody
//    public ReModel listFile(ReModel reModel, String ftpId, String ftpType) {
//        try {
//            // 三种协议的不同处理逻辑
//            switch (ftpType) {
////                ftp协议
//                case "1":
//                    FTPClient ftpClient = (FTPClient) HttpWebUtil.getSessionAttribute("ftpObj");
//                    if (null == ftpClient || !ftpClient.isConnected()) {
//                        FtpInfo ftpInfo = this.ftpInfoService.selectFtpInfoByFtpId(ftpId);
//                        ftpClient = FtpPoolUtil.getFtpClient(ftpInfo);
//                        Map<String, Object> sessionMap = new HashMap<>(50);
//                        sessionMap.put("ftpInfo", ftpInfo);
//                        sessionMap.put("ftpObj", ftpClient);
//                        HttpWebUtil.setSessionAttribute(ftpId, sessionMap);
//                    }
//                    break;
//                case "2":
//                    break;
////                    sftp协议
//                case "3":
//                    ChannelSftp sftp = (ChannelSftp) HttpWebUtil.getSessionAttribute("sftpObj");
//                    if (null == sftp || !sftp.isConnected()) {
//                        FtpInfo ftpInfo = this.ftpInfoService.selectFtpInfoByFtpId(ftpId);
//                        sftp = SftpPoolUtil.openSftpConnect(ftpInfo);
//                        Map<String, Object> sessionMap = new HashMap<>();
//                        sessionMap.put("ftpInfo", ftpInfo);
//                        sessionMap.put("sftpObj", sftp);
//                        HttpWebUtil.setSessionAttribute(ftpId, sessionMap);
//                    }
//                    break;
//                default:
//                    break;
//            }
//        } catch (Exception e) {
//        }
//        reModel.addObject("ftpId", ftpId);
//        reModel.addObject("ftpType", ftpType);
//        return reModel;
//    }



}
