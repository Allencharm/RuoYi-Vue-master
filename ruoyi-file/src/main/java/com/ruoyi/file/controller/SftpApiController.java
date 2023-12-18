package com.ruoyi.file.controller;

import com.jcraft.jsch.ChannelSftp;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.file.bean.DataTableBean;
import com.ruoyi.file.bean.FileBean;
import com.ruoyi.file.bean.ResponseBean;
import com.ruoyi.file.bean.ZTreeBean;
import com.ruoyi.file.tool.DataUtil;
import com.ruoyi.file.tool.HttpWebUtil;
import com.ruoyi.file.tool.SftpPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/8 20:12
 * @description:ftp连接完成后操作
 */
@Controller
@RequestMapping("/rtb/sftp")
public class SftpApiController {

    @Autowired
    private DataUtil dataUtil;

    @Anonymous
    @ResponseBody
    @RequestMapping("/lsfile")
//    @WebLog(msgs = LogMsgs.FTP3_LSFILE, type = LogType.ELSE_LOG)
    public DataTableBean lsfile(String ftpId, Boolean showFile, String filePath) {
        DataTableBean tableData = new DataTableBean();

        try {
            Map<String, Object> resultMap = new HashMap<>();
            String ftpUser = DataUtil.getSftpInfo(ftpId).getFtpUser();
            ChannelSftp sftp = dataUtil.getChannelSftp(ftpId);
            List<FileBean> fileList = SftpPoolUtil.getWebFileList(sftp, ftpUser, showFile, filePath);
            tableData.setCode(200);
            tableData.setCount((long) fileList.size());
            tableData.setData(fileList);
            resultMap.put("showFile", showFile);//是否显示隐藏文件
            resultMap.put("homePath", SftpPoolUtil.getHomePath(ftpUser));//主根目录
            resultMap.put("lastPath", SftpPoolUtil.getLastPath(ftpUser, filePath));//上级目录
            resultMap.put("currPath", SftpPoolUtil.getCurrPath(ftpUser, filePath));//当前目录
            tableData.setParam(resultMap);
        } catch (Exception e) {
            tableData.setCode(500);
            tableData.setMsgs(e.getMessage());
            e.printStackTrace();
        }
        return tableData;
    }

    @Anonymous
    @ResponseBody
    @RequestMapping("/mkpath")
//    @WebLog(msgs = LogMsgs.FTP3_MKPATH, type = LogType.ELSE_LOG)
    public ResponseBean mkpath(String ftpId, String filePath, String fileName) {
        ResponseBean responseBean = new ResponseBean();
        try {
            ChannelSftp sftp = dataUtil.getChannelSftp(ftpId);
            sftp.mkdir(filePath + fileName);
            responseBean.setMsgs("创建文件夹成功");
        } catch (Exception e) {
            responseBean.setMsgs("创建文件夹失败");
            e.printStackTrace();
            responseBean.setCode(0);
        }
        return responseBean;
    }

    @Anonymous
    @ResponseBody
    @RequestMapping("/rmfile")
//    @WebLog(msgs = LogMsgs.FTP3_RMFILE, type = LogType.ELSE_LOG)
    public ResponseBean rmfile(String ftpId, String filePath) {
        ResponseBean responseBean = new ResponseBean();
        try {
            ChannelSftp sftp = dataUtil.getChannelSftp(ftpId);
            SftpPoolUtil.rmFile(sftp, filePath);
            responseBean.setMsgs("删除成功");
        } catch (Exception e) {
            responseBean.setMsgs("删除失败");
            e.printStackTrace();
            responseBean.setCode(0);
        }
        return responseBean;
    }

    @Anonymous
    @ResponseBody
    @RequestMapping("/rename")
//    @WebLog(msgs = LogMsgs.FTP3_RENAME, type = LogType.ELSE_LOG)
    public ResponseBean rename(String ftpId, String oldFilePath, String newFilePath) {
        ResponseBean responseBean = new ResponseBean();
        try {
            ChannelSftp sftp = dataUtil.getChannelSftp(ftpId);
            sftp.rename(oldFilePath, newFilePath);
            responseBean.setMsgs("重命名成功");
        } catch (Exception e) {
            responseBean.setMsgs("重命名失败");
            e.printStackTrace();
            responseBean.setCode(0);
        }
        return responseBean;
    }

    @Anonymous
    @ResponseBody
    @RequestMapping(value = "/upload")
//    @WebLog(msgs = LogMsgs.FTP3_UPLOAD, type = LogType.ELSE_LOG)
    public ResponseBean upload(MultipartFile file, String ftpId, String currPath) {
        ResponseBean responseBean = new ResponseBean();
        if (!file.isEmpty()) {
            try {
                ChannelSftp sftp = dataUtil.getChannelSftp(ftpId);
                sftp.put(file.getInputStream(), currPath + file.getOriginalFilename());
                responseBean.setMsgs("上传文件成功");
            } catch (Exception e) {
                responseBean.setMsgs("上传文件失败");
                e.printStackTrace();
                responseBean.setCode(0);
            }
        } else {
            responseBean.setMsgs("上传文件不能为空");
            responseBean.setCode(0);
        }
        return responseBean;
    }

    @Anonymous
    @ResponseBody
    @RequestMapping(value = "/mytree")
//    @WebLog(msgs = LogMsgs.FTP3_MYTREE, type = LogType.ELSE_LOG)
    public ResponseBean mytree(String ftpId, String id, String path) {
        ResponseBean responseBean = new ResponseBean();
        List<ZTreeBean> fileTreeList = null;
        try {
            String ftpUser = DataUtil.getSftpInfo(ftpId).getFtpUser();
            ChannelSftp sftp = dataUtil.getChannelSftp(ftpId);
            fileTreeList = SftpPoolUtil.getTreeList(sftp, ftpUser, id, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseBean.setData(fileTreeList);
        return responseBean;
    }

    @Anonymous
    @ResponseBody
    @RequestMapping(value = "/docopy")
//    @WebLog(msgs = LogMsgs.FTP3_DOCOPY, type = LogType.ELSE_LOG)
    public ResponseBean docopy(String ftpId, String filePath, String distPath) {
        ResponseBean responseBean = new ResponseBean();
        try {
            ChannelSftp sftp = dataUtil.getChannelSftp(ftpId);
            SftpPoolUtil.cpFile(sftp, filePath, distPath);
            responseBean.setMsgs("复制成功");
        } catch (Exception e) {
            e.printStackTrace();
            responseBean.setMsgs("复制失败");
            responseBean.setCode(0);
        }
        return responseBean;
    }

    @Anonymous
    @ResponseBody
    @RequestMapping(value = "/domove")
//    @WebLog(msgs = LogMsgs.FTP3_DOMOVE, type = LogType.ELSE_LOG)
    public ResponseBean domove(String ftpId, String filePath, String distPath) {
        ResponseBean responseBean = new ResponseBean();
        try {
            ChannelSftp sftp = dataUtil.getChannelSftp(ftpId);
            SftpPoolUtil.mvFile(sftp, filePath, distPath);
            responseBean.setMsgs("移动成功");
        } catch (Exception e) {
            e.printStackTrace();
            responseBean.setMsgs("移动失败");
            responseBean.setCode(0);
        }
        return responseBean;
    }

    @Anonymous
    @RequestMapping(value = "/dodown")
//    @WebLog(msgs = LogMsgs.FTP3_DODOWN, type = LogType.ELSE_LOG)
    public void dodown(HttpServletResponse response, String ftpId, String filePath, String fileName) {
        try {
            ChannelSftp sftp = dataUtil.getChannelSftp(ftpId);
            SftpPoolUtil.downFile(response, sftp, filePath, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Autowired
//    private DataUtil dataUtil;
//
//    @RequestMapping("/lsfile")
//    @Anonymous
//    @ResponseBody
//    public DataTableBean lsfile(String ftpId, Boolean showFile, String filePath) {
//        DataTableBean tableData = new DataTableBean();
//
//
//        try {
//            Map<String, Object> resultMap = new HashMap<>();
//            String ftpUser = DataUtil.getSftpInfo(ftpId).getFtpUser();
//            ChannelSftp sftp = dataUtil.getChannelSftp(ftpId);
//            List<FileBean> fileList = SftpPoolUtil.getWebFileList(sftp, ftpUser, showFile, filePath);
//            tableData.setCode(200);
//            tableData.setCount((long) fileList.size());
//            tableData.setData(fileList);
//            resultMap.put("showFile", showFile);//是否显示隐藏文件
//            resultMap.put("homePath", SftpPoolUtil.getHomePath(ftpUser));//主根目录
//            resultMap.put("lastPath", SftpPoolUtil.getLastPath(ftpUser, filePath));//上级目录
//            resultMap.put("currPath", SftpPoolUtil.getCurrPath(ftpUser, filePath));//当前目录
//            tableData.setParam(resultMap);
//        } catch (Exception e) {
//            tableData.setCode(500);
//            tableData.setMsgs(e.getMessage());
//            e.printStackTrace();
//        }
//        return tableData;
//    }

}
