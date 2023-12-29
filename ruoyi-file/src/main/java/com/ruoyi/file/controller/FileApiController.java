package com.ruoyi.file.controller;

import com.jcraft.jsch.ChannelSftp;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.file.bean.DataTableBean;
import com.ruoyi.file.bean.FileBean;
import com.ruoyi.file.bean.FtpInfo;
import com.ruoyi.file.bean.ResponseBean;
import com.ruoyi.file.service.FileApiService;
import com.ruoyi.file.service.impl.FileApiServiceImpl;
import com.ruoyi.file.tool.*;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ldq
 * @version 1.0
 * @date 2023/12/27 13:11
 * @Description: 文件操作
 */
@Controller
@RequestMapping("/file")

public class FileApiController extends BaseController {

    private static Logger log = LoggerFactory.getLogger(FileApiController.class);

    @Autowired
    private FileApiService fileApiService;

    /**
     * 文件列表视图
     */
    @Anonymous
    @RequestMapping("/view")
    public ModelAndView listFile(ModelAndView mv) {
        mv.setViewName("business/ftp_file");
        return mv;
    }

    /**
     * 文件列表
     */
    @Anonymous
    @ResponseBody
    @RequestMapping("/lslist")
//    @WebLog(msgs = LogMsgs.FTP3_LSFILE, type = LogType.ELSE_LOG)
    public DataTableBean lsfile(String filePath) {
        DataTableBean tableData = new DataTableBean();
        String userName = System.getProperty("user.name");
        System.out.println("当前Linux用户：" + userName);
        String currPath = FileUtil.getCurrPath(userName,filePath);
        File currentDir = new File(currPath);
        File[] files = currentDir.listFiles();
        List<FileBean> fileList = new ArrayList<>();
        try {
            for (int i = 0 ; i < files.length ; i++){
                FileBean fileBean = new FileBean();
                fileBean.setFilePath(files[i].getAbsolutePath());
                fileBean.setFileName(files[i].getName());
                if (files[i].isFile()){
                    fileBean.setFileType(true);
                }else {
                    fileBean.setFileType(false);
                }

                fileBean.setFileSize(files[i].length());
                fileBean.setAccessTime(DateUtil.timeStampToDate(System.currentTimeMillis(), DateUtil.DATE_STR_FULL));
                fileBean.setModifyTime(DateUtil.timeStampToDate(new File(files[i].getAbsolutePath()).lastModified(), DateUtil.DATE_STR_FULL));
                if (files[i].isHidden()){
                    fileBean.setFileHidden(true);
                }else {
                    fileBean.setFileHidden(false);
                    fileList.add(fileBean);
                }
            }
            Map<String, Object> resultMap = new HashMap<>();
            tableData.setCode(200);
            tableData.setCount((long) fileList.size());
            tableData.setData(fileList);
            resultMap.put("showFile", false);//是否显示隐藏文件
            resultMap.put("homePath", FileUtil.getHomePath(userName));//主根目录
            resultMap.put("lastPath", FileUtil.getLastPath(userName, filePath));//上级目录
            resultMap.put("currPath", FileUtil.getCurrPath(userName, filePath));//当前目录
            tableData.setParam(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tableData;
    }

    /**
     * 下载文件
     */
    @Anonymous
    @RequestMapping(value = "/dodown")
    public void dodown(HttpServletResponse response, String filePath, String fileName) {
        try {
            FileUtil.downFile(response, filePath, fileName);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 上传文件弹窗
     */
    @RequestMapping("/pick")
    public ModelAndView pickFile(ModelAndView mv, String currPath) {
        mv.addObject("currPath", currPath);
        mv.setViewName("business/ftp_upload");
        return mv;
    }

    /**
     * 上传文件
     */
    @Anonymous
    @ResponseBody
    @RequestMapping(value = "/upload")
    public ResponseBean upload(MultipartFile file,String currPath) {
        ResponseBean responseBean = new ResponseBean();
        if (!file.isEmpty()) {
            try {
                String filename = file.getOriginalFilename();
                currPath = currPath.replace("\\", "/");
                File targetFile = new File(currPath, filename);
                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }
                file.transferTo(targetFile);
                responseBean.setMsgs("上传文件成功");
            } catch (Exception e) {
                responseBean.setMsgs("上传文件失败");
                e.printStackTrace();
                responseBean.setCode(-1);
            }
        } else {
            responseBean.setMsgs("上传文件不能为空");
            responseBean.setCode(-1);
        }
        return responseBean;
    }
}
