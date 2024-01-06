package com.ruoyi.file.tool;

import com.jcraft.jsch.*;
import com.ruoyi.file.bean.DataTableBean;
import com.ruoyi.file.bean.FileBean;
import com.ruoyi.file.bean.FtpInfo;
import com.ruoyi.file.bean.ZTreeBean;
import com.ruoyi.file.controller.FileApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sound.midi.Soundbank;
import java.io.*;
import java.util.*;

/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/8 19:19
 * @description:sftp链接池
 */
public class FileUtil {

    private static Logger log = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 执行shell命令
     */
    private static void execCommand(ChannelSftp sftp, String command) throws Exception {
        ChannelExec channelExec = null;
        channelExec = (ChannelExec) sftp.getSession().openChannel("exec");
        channelExec.setCommand(command);
        channelExec.connect();
        channelExec.disconnect();
    }

    /**
     * 删除文件
     */
    public static void rmFile(ChannelSftp sftp, String filePath) throws Exception {
        String command = "rm -rf " + filePath;
        execCommand(sftp, command);
    }

    /**
     * 拷贝文件
     */
    public static void cpFile(ChannelSftp sftp, String filePath, String distPath) throws Exception {
        String command = "cp -r " + filePath + " " + distPath;
        execCommand(sftp, command);
    }

    /**
     * 移动文件
     */
    public static void mvFile(ChannelSftp sftp, String filePath, String distPath) throws Exception {
        String command = "mv " + filePath + " " + distPath;
        execCommand(sftp, command);
    }

    /**
     * 下载文件
     */
    public static void downFile(HttpServletResponse response, String filePath, String fileName) throws IOException {
        //获取输出流
        ServletOutputStream os = response.getOutputStream();
        try {
            byte[] bytes = fileName.getBytes("utf-8");
            fileName = new String(bytes, "ISO8859-1");
            response.setCharacterEncoding("UTF-8");
            //修改响应的头部属性content-disposition值为attachment
            response.setHeader("content-disposition", "attachment;filename=" + fileName);
            //获取连接服务器端资源文件的输入流
            fileName = new String(bytes, "utf-8");
            filePath.replace("\\", "/");
            InputStream is = new FileInputStream(filePath);
            //获取输出流
            //ServletOutputStream os = response.getOutputStream();
            //将输入流中的数据写入到输出流中
            int len;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
            os.close();
        } catch (Exception e) {
            log.error("file down error:{}",e);
            e.printStackTrace();
            os.close();
        }
    }

    /**
     * 判断文件夹是否存在
     */
    private static boolean isExistDir(final String filePath, final ChannelSftp sftp) {
        try {
            Vector<?> vector = sftp.ls(filePath);
            if (null == vector) {
                return false;
            } else {
                return true;
            }
        } catch (SftpException e) {
            return false;
        }
    }

    /**
     * 获取目录下文件列表
     */
/*
    public static List<FileBean> getWebFileList(ChannelSftp sftp, String ftpUser, Boolean isShow, String filePath) throws Exception {
        filePath = getCurrPath(ftpUser, filePath);
        List<ChannelSftp.LsEntry> lsFileList = sftp.ls(filePath);
        List<FileBean> file0List = new ArrayList<>();
        List<FileBean> file1List = new ArrayList<>();
        List<FileBean> file2List = new ArrayList<>();
        if (!isShow) {
            //封装不含隐藏文件集
            for (ChannelSftp.LsEntry lsEntry : lsFileList) {
                if (".".equals(lsEntry.getFilename()) || "..".equals(lsEntry.getFilename())) {
                } else {
                    if (!lsEntry.getFilename().startsWith(".")) {
                        FileBean fileBean = getWebFileBean(lsEntry, filePath);
                        if (fileBean.isFileType()) {
                            file1List.add(fileBean);
                        } else {
                            file2List.add(fileBean);
                        }
                    }
                }
            }
        } else {
            //封装全部文件集合
            for (ChannelSftp.LsEntry lsEntry : lsFileList) {
                if (".".equals(lsEntry.getFilename()) || "..".equals(lsEntry.getFilename())) {
                } else {
                    FileBean fileBean = getWebFileBean(lsEntry, filePath);
                    if (fileBean.isFileType()) {
                        file1List.add(fileBean);
                    } else {
                        file2List.add(fileBean);
                    }
                }
            }
        }
        if (file1List.size() > 0) {
            file0List.addAll(file1List);
        }
        if (file2List.size() > 0) {
            file0List.addAll(file2List);
        }
        return file0List;
    }
*/

    /**
     * 层状目录树集合
     */
    public static List<FileBean> getFileTree(List<FileBean> fileList, File[] files, String filePath){
        try {
            for (int i = 0 ; i < files.length ; i++){
                FileBean fileBean = new FileBean();

                fileBean.setFileName(files[i].getName());
                if (files[i].isFile()){
                    fileBean.setFileType(true);
                    fileBean.setFilePath(files[i].getAbsolutePath());
                }else {
                    fileBean.setFileType(false);
                    fileBean.setFilePath(files[i].getAbsolutePath() + '/');
                }

                fileBean.setFileSize(files[i].length());
                fileBean.setAccessTime(DateUtil.timeStampToDate(System.currentTimeMillis(), DateUtil.DATE_STR_FULL));
                fileBean.setModifyTime(DateUtil.timeStampToDate(new File(files[i].getAbsolutePath()).lastModified(), DateUtil.DATE_STR_FULL));
                if (files[i].isHidden() || "bak".equals(files[i].getName())){
                    fileBean.setFileHidden(true);
                }else {
                    fileBean.setFileHidden(false);
                    fileList.add(fileBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileList;
    }

    /**
     * 封装目录树集合
     */
    public static List<ZTreeBean> getTreeList(String id, String path) throws Exception {
        String ftpUser = System.getProperty("user.name");
        List<FileBean> fileList = new ArrayList<>();
        String currPath = FileUtil.getCurrPath(ftpUser,path);
        File currentDir = new File(currPath);
        File[] files = currentDir.listFiles();
        List<ZTreeBean> fileTreeList = new ArrayList<>();
        if (StringUtils.isEmpty(id)) {
            id = KeyGeneratorUtil.getUUIDKey();
            fileTreeList.add(new ZTreeBean(id, "全部文件", FileUtil.getHomePath(ftpUser), true));
            fileList = getFileTree(fileList,files,FileUtil.getHomePath(ftpUser));
            if (!fileList.isEmpty()) {
                for (FileBean fileBean : fileList) {
                    if (!fileBean.isFileType()){
                        fileTreeList.add(new ZTreeBean(KeyGeneratorUtil.getUUIDKey(), id, fileBean.getFileName(), fileBean.getFilePath(), true));
                    }
                }
            }
        } else {
            fileList = getFileTree(fileList,files,FileUtil.getHomePath(ftpUser));
            if (!fileList.isEmpty()) {
                for (FileBean fileBean : fileList) {
                    if (!fileBean.isFileType()) {
                        fileTreeList.add(new ZTreeBean(KeyGeneratorUtil.getUUIDKey(), id, fileBean.getFileName(), fileBean.getFilePath(), true));
                    }
                }
            }
        }
        return fileTreeList;
    }

    /**
     * 获取
     * \
     * 当前用户的根目录
     */
    public static String getHomePath(String ftpUser) {
        if ("root".equals(ftpUser)) {
            return "/";
        } else {
            return "/home/" + ftpUser + "/";
        }
    }

    /**
     * 获取当前文件目录
     */
    public static String getCurrPath(String ftpUser, String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            if ("root".equals(ftpUser)) {
                filePath = "/";
            } else {
                filePath = "/home/" + ftpUser + "/";
            }
        }
        return filePath;
    }

    /**
     * 获取当前文件的上级目录
     */
    public static String getLastPath(String ftpUser, String filePath) {
        filePath = getCurrPath(ftpUser, filePath);
        String lastPath = "";
        String basePath = getHomePath(ftpUser);
        if (basePath.equals(filePath)) {
            //根目录
            lastPath = basePath;
        } else {
            String[] pathArray = filePath.split("/");
            for (int i = 1; i < pathArray.length -1; i++) {
                if (i != pathArray.length -2){
                    lastPath = lastPath +"/" + pathArray[i];
                }else {
                    lastPath = lastPath + "/" + pathArray[i] + "/";
                    break;
                }
            }
        }
        if (StringUtils.isEmpty(lastPath)) lastPath = basePath;
        return lastPath;
    }

}
