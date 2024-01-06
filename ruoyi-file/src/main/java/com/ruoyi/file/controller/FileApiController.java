package com.ruoyi.file.controller;

import com.jcraft.jsch.ChannelSftp;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.file.bean.DataTableBean;
import com.ruoyi.file.bean.FileBean;
import com.ruoyi.file.bean.ResponseBean;
import com.ruoyi.file.bean.ZTreeBean;
import com.ruoyi.file.service.FileApiService;
import com.ruoyi.file.tool.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.Operator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

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
    public DataTableBean lsfile(String filePath) {
        DataTableBean tableData = new DataTableBean();
        String userName = System.getProperty("user.name");
        String currPath = FileUtil.getCurrPath(userName,filePath);
        File currentDir = new File(currPath);
        File[] files = currentDir.listFiles();
        List<FileBean> fileList = new ArrayList<>();
        try {
            FileUtil.getFileTree(fileList,files,filePath);
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

    @Anonymous
    @ResponseBody
    @RequestMapping("/mkpath")
    public ResponseBean mkpath(String filePath, String fileName) {
        ResponseBean responseBean = new ResponseBean();
        try {
            /*存在问题根目录*/
            File folder = new File(filePath + fileName);
            if (folder.exists()){
                responseBean.setMsgs("文件夹已存在创建失败");
                responseBean.setCode(201);
            }else {
                boolean success = folder.mkdir();
                if (success) {
                    responseBean.setMsgs("文件夹创建成功");
                } else {
                    responseBean.setMsgs("文件夹创建失败");
                }
            }
        } catch (SecurityException e) {
            responseBean.setMsgs("权限不足，无法创建文件夹");
        }catch (Exception e){
            log.error("创建文件夹时发生异常: " + e.getMessage());
        }
        return responseBean;
    }

    @Anonymous
    @ResponseBody
    @RequestMapping("/rename")
    public ResponseBean rename(String oldFilePath, String newFilePath) {
        ResponseBean responseBean = new ResponseBean();
        try {
            if (!oldFilePath.equals(newFilePath)) {// 新的文件名和以前文件名不同时,才有必要进行重命名
                oldFilePath.replace("\\", "/");
                newFilePath.replace("\\", "/");
                Path oldfile = Paths.get(oldFilePath);
                Path newfile = Paths.get(newFilePath);
                if (Files.exists(newfile)) {// 若在该目录下已经有一个文件和新文件名相同，则不允许重命名
                    responseBean.setMsgs("重命名文件已经存在");
                } else {
                    Files.move(oldfile, newfile, StandardCopyOption.REPLACE_EXISTING);
                    responseBean.setMsgs("文件重命名成功");
                }
            }else{
                responseBean.setMsgs("新老文件名一致，重命名失败");
            }
        } catch (Exception e) {
            responseBean.setMsgs("重命名失败");
            e.printStackTrace();
            responseBean.setCode(0);
        }
        return responseBean;
    }

    @Anonymous
    @RequestMapping("/copy")
    public ModelAndView copyFile(ModelAndView mv, String filePath,String fileType) {
        mv.addObject("filePath", filePath);
        mv.addObject("fileType", fileType);
        mv.setViewName("business/ftp_copy");
        return mv;
    }

    @Anonymous
    @ResponseBody
    @RequestMapping(value = "/mytree")
    public ResponseBean mytree(String id, String path) {
        ResponseBean responseBean = new ResponseBean();
        List<ZTreeBean> fileTreeList = null;
        try {
            fileTreeList = FileUtil.getTreeList(id, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseBean.setData(fileTreeList);
        return responseBean;
    }

    @Anonymous
    @ResponseBody
    @RequestMapping(value = "/docopy")
    public ResponseBean docopy(boolean fileType, String filePath, String distPath) {
        ResponseBean responseBean = new ResponseBean();
        File fileFile = new File(filePath);
        File distFile = new File(distPath);
        try {
            if (!distFile.exists()) {
                distFile.mkdirs(); // 创建目标文件夹（若不存在）
            }
            if (fileFile.isDirectory()){
                String replace = filePath.replace("\\", "/");
                String[] split = replace.split("/");
                String fileDir = split[split.length - 1];
                distFile = new File(distFile.getPath() + "/" + fileDir);
                distFile.mkdir();
                if (fileFile.listFiles().length > 0){
                    for (File file : fileFile.listFiles()) {
                        if (file.isDirectory()) {
                            docopy(fileType,file.getPath(), new File(distFile, file.getName()).getPath()); // 递归调用自身处理子文件夹
                        } else {
                            Files.copy(file.toPath(), Paths.get(distFile + "/" + file.getName()), StandardCopyOption.REPLACE_EXISTING); // 复制文件
                        }
                    }
                    responseBean.setMsgs("复制成功");
                }else {
                    responseBean.setMsgs("复制成功");
                }
            }else {
                Files.copy(fileFile.toPath(), Paths.get(distFile + "/" + fileFile.getName()), StandardCopyOption.REPLACE_EXISTING); // 复制文件
                responseBean.setMsgs("复制成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseBean.setMsgs("复制失败");
            responseBean.setCode(0);
        }
        return responseBean;
    }

    @Anonymous
    @ResponseBody
    @RequestMapping("/rmfile")
    public ResponseBean rmfile(String distPath,String filePath) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        ResponseBean responseBean = new ResponseBean();
        File fileFile = new File(filePath);
        String userName = System.getProperty("user.name");
        String homePath = FileUtil.getHomePath(userName);
        distPath = homePath + "bak";
        File distFile = new File(distPath);
        try {
            if (!distFile.exists()) {
                distFile.mkdirs(); // 创建目标文件夹（若不存在）
            }
            if (fileFile.isDirectory()){
                String replace = filePath.replace("\\", "/");
                String[] split = replace.split("/");
                String fileDir = split[split.length - 1];
                distFile = new File(distFile.getPath() + "/" + fileDir);
                if (!distFile.exists()){
                    distFile.mkdir();
                }
                if (fileFile.listFiles().length > 0){
                    for (File file : fileFile.listFiles()) {
                        if (file.isDirectory()) {
                            rmfile(file.getPath(), new File(distFile, file.getName()).getPath()); // 递归调用自身处理子文件夹
                        } else {
                            Files.copy(file.toPath(), Paths.get(distFile + "/" + file.getName().substring(0,file.getName().lastIndexOf(".")) + "-" + simpleDateFormat.format(new Date()) + file.getName().substring(file.getName().lastIndexOf("."))), StandardCopyOption.REPLACE_EXISTING); // 复制文件
                        }
                        file.delete();
                    }
                    responseBean.setMsgs("删除成功");
                    fileFile.delete();
                }else {
                    fileFile.delete();
                    responseBean.setMsgs("删除成功");
                }
            }else {
                Files.copy(fileFile.toPath(), Paths.get(distFile + "/" + fileFile.getName().substring(0,fileFile.getName().lastIndexOf(".")) + "-" + simpleDateFormat.format(new Date()) + fileFile.getName().substring(fileFile.getName().lastIndexOf("."))), StandardCopyOption.REPLACE_EXISTING); // 复制文件
                responseBean.setMsgs("删除成功");
                fileFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseBean.setMsgs("删除失败");
            responseBean.setCode(0);
        }
        return responseBean;
    }

    @Anonymous
    @RequestMapping("/move")
    public ModelAndView moveFile(ModelAndView mv, String ftpId, String ftpType, String filePath) {
        mv.addObject("filePath", filePath);
        mv.setViewName("business/ftp_move");
        return mv;
    }

    @Anonymous
    @ResponseBody
    @RequestMapping("/domove")
    public ResponseBean domove(String filePath, String distPath) {
        ResponseBean responseBean = new ResponseBean();
        File fileFile = new File(filePath);
        File distFile = new File(distPath);
        try {
            if (!distFile.exists()) {
                distFile.mkdirs(); // 创建目标文件夹（若不存在）
            }
            if (fileFile.isDirectory()){
                String replace = filePath.replace("\\", "/");
                String[] split = replace.split("/");
                String fileDir = split[split.length - 1];
                distFile = new File(distFile.getPath() + "/" + fileDir);
                distFile.mkdir();
                if (fileFile.listFiles().length > 0){
                    for (File file : fileFile.listFiles()) {
                        if (file.isDirectory()) {
                            domove(file.getPath(), new File(distFile, file.getName()).getPath()); // 递归调用自身处理子文件夹
                        } else {
                            Files.copy(file.toPath(), Paths.get(distFile + "/" + file.getName()), StandardCopyOption.REPLACE_EXISTING); // 复制文件
                        }
                        file.delete();
                    }
                    responseBean.setMsgs("移动成功");
                    fileFile.delete();
                }else {
                    responseBean.setMsgs("移动成功");
                    fileFile.delete();
                }
            }else {
                Files.copy(fileFile.toPath(), Paths.get(distFile + "/" + fileFile.getName()), StandardCopyOption.REPLACE_EXISTING); // 复制文件
                responseBean.setMsgs("移动成功");
                fileFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseBean.setMsgs("复制失败");
            responseBean.setCode(0);
        }
        return responseBean;
    }
}
