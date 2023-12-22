package com.ruoyi.file.tool;

import com.jcraft.jsch.*;
import com.ruoyi.file.bean.FileBean;
import com.ruoyi.file.bean.FtpInfo;
import com.ruoyi.file.bean.ZTreeBean;
import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.*;

/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/8 19:19
 * @description:sftp链接池
 */
public class SftpPoolUtil {

    /**
     * sftp连接池
     */
    private static final Map<String, Channel> SFTP_CHANNEL_POOL = new HashMap<>();

    /**
     * 打开sftp协议连接
     */
    public static ChannelSftp openSftpConnect(FtpInfo sftpBean) throws JSchException {
        Session session = null;
        Channel channel = null;
        ChannelSftp sftp = null;
        String user = sftpBean.getFtpUser();
        /*String pass = sftpBean.getFtpPass();*/
        String host = sftpBean.getFtpHost();
        int port = sftpBean.getFtpPort();
        String privateKey = sftpBean.getFtpPkey();
        StringBuffer keyBuf = new StringBuffer();
        keyBuf.append(host);
        keyBuf.append(",");
        keyBuf.append(port);
        keyBuf.append(",");
        keyBuf.append(user);
        /*keyBuf.append(",");
        keyBuf.append(pass);*/
        String key = keyBuf.toString();
        if (null == SFTP_CHANNEL_POOL.get(key)) {
            JSch jsch = new JSch();
            jsch.getSession(user, host, port);
            session = jsch.getSession(user, host, port);
            /*if (!StringUtils.isEmpty(pass)) {
                session.setPassword(pass);
            }*/
            if (!StringUtils.isEmpty(privateKey)) {
                jsch.addIdentity(privateKey);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            SFTP_CHANNEL_POOL.put(key, channel);
        } else {
            channel = SFTP_CHANNEL_POOL.get(key);
            session = channel.getSession();
            if (!session.isConnected()) {
                session.connect();
            }
            if (!channel.isConnected()) {
                channel.connect();
            }
        }
        sftp = (ChannelSftp) channel;
        return sftp;
    }


    /**
     * 关闭协议
     */
    public static void closeSftpConnect(final ChannelSftp sftp) {
        try {
            if (sftp != null) {
                Session session = sftp.getSession();
                if (session != null) {
                    if (session.isConnected()) {
                        session.disconnect();
                    }
                }
                if (sftp.isConnected()) {
                    sftp.disconnect();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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


    public static void downFile(HttpServletResponse response, ChannelSftp sftp, String filePath, String fileName) throws Exception {
        byte[] bytes = fileName.getBytes("utf-8");
        fileName = new String(bytes, "ISO8859-1");
        //修改响应的头部属性content-disposition值为attachment
        response.setHeader("content-disposition", "attachment;filename=" + fileName);
        //获取连接服务器端资源文件的输入流
        InputStream is = sftp.get(filePath);
        //获取输出流
        ServletOutputStream os = response.getOutputStream();
        //将输入流中的数据写入到输出流中
        int len;
        byte[] buf = new byte[1024];
        while ((len = is.read(buf)) != -1) {
            os.write(buf, 0, len);
        }
        os.close();
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

    /**
     * 层状目录树集合
     */
    private static List<FileBean> getFileTree(ChannelSftp sftp, String filePath) throws Exception {
        List<ChannelSftp.LsEntry> lsFileList = sftp.ls(filePath);
        List<FileBean> fileList = new ArrayList<>();
        for (ChannelSftp.LsEntry lsEntry : lsFileList) {
            if (".".equals(lsEntry.getFilename()) || "..".equals(lsEntry.getFilename())) {
            } else {
                FileBean fileBean = getWebFileBean(lsEntry, filePath);
                if (fileBean.isFileType() && !lsEntry.getFilename().startsWith(".")) {
                    fileList.add(fileBean);
                }
            }
        }
        return fileList;
    }

    /**
     * 封装目录树集合
     */
    public static List<ZTreeBean> getTreeList(ChannelSftp sftp, String ftpUser, String id, String path) throws Exception {
        List<ZTreeBean> fileTreeList = new ArrayList<>();
        if (StringUtils.isEmpty(id)) {
            id = KeyGeneratorUtil.getUUIDKey();
            fileTreeList.add(new ZTreeBean(id, "全部文件", SftpPoolUtil.getHomePath(ftpUser), true));
            List<FileBean> fileList = getFileTree(sftp, SftpPoolUtil.getHomePath(ftpUser));
            if (!fileList.isEmpty()) {
                for (FileBean fileBean : fileList) {
                    fileTreeList.add(new ZTreeBean(KeyGeneratorUtil.getUUIDKey(), id, fileBean.getFileName(), fileBean.getFilePath(), true));
                }
            }
        } else {
            List<FileBean> fileList = getFileTree(sftp, path);
            if (!fileList.isEmpty()) {
                for (FileBean fileBean : fileList) {
                    fileTreeList.add(new ZTreeBean(KeyGeneratorUtil.getUUIDKey(), id, fileBean.getFileName(), fileBean.getFilePath(), true));
                }
            }
        }
        return fileTreeList;
    }

    /**
     * 封装文件信息
     */
    private static FileBean getWebFileBean(ChannelSftp.LsEntry lsEntry, String basePath) {
        SftpATTRS attrs = lsEntry.getAttrs();
        FileBean fileBean = new FileBean();
        fileBean.setFileName(lsEntry.getFilename());
        fileBean.setFileSize(attrs.getSize());
        fileBean.setFileType(attrs.isDir());
        Integer atime = attrs.getATime();
        Integer mtime = attrs.getMTime();
        fileBean.setAccessTime(DateUtil.unixTimeStampToDate(atime.longValue(), DateUtil.DATE_STR_FULL));
        fileBean.setModifyTime(DateUtil.unixTimeStampToDate(mtime.longValue(), DateUtil.DATE_STR_FULL));
        if (fileBean.isFileType()) {
            fileBean.setFilePath(basePath + lsEntry.getFilename() + "/");
        } else {
            fileBean.setFilePath(basePath + lsEntry.getFilename());
        }
        return fileBean;
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
        String lastPath;
        String basePath = getHomePath(ftpUser);
        if (basePath.equals(filePath)) {
            //根目录
            lastPath = basePath;
        } else {
            String[] pathArray = filePath.replace(basePath, "/").split("/", -1);
            lastPath = pathArray[pathArray.length - 2];
            lastPath = filePath.replace(lastPath, "");
            lastPath = lastPath.substring(0, lastPath.length() - 1);
        }
        return lastPath;
    }

}
