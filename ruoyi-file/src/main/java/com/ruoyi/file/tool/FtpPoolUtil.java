package com.ruoyi.file.tool;

import com.ruoyi.file.bean.FileBean;
import com.ruoyi.file.bean.FtpInfo;
import org.apache.commons.net.ftp.*;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/8 19:28
 * @description:ftp连接池
 */
public class FtpPoolUtil {

    /**
     * ftp缓存
     */
    private static final Map<String, FTP> FTP_POOL = new HashMap<>();
    /**
     * FTP的端口号
     */
    public static final int FTP_PORT = 21;

    public static FTPClient getFtpClient(FtpInfo ftpInfo) {
        String host = ftpInfo.getFtpHost();
        String userName = ftpInfo.getFtpUser();
        String password = ftpInfo.getFtpPass();
        String ftpKey = host;
        FTPClient ftp;
        if (FTP_POOL.get(ftpKey) != null) {
            ftp = (FTPClient) FTP_POOL.get(ftpKey);
        } else {
            ftp = new FTPClient();
        }
        FTPClientConfig config = new FTPClientConfig();

        ftp.configure(config);
        try {
            int reply;
            ftp.connect(host);
            ftp.login(userName, password);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return null;
            }
            return ftp;
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件列表
     *
     * @param filePath
     * @param ftpClient
     * @return
     */
    public static List<FileBean> listFiles(final String filePath, final FTPClient ftpClient) {
        List<FileBean> fileList = null;
        try {
            FTPFile[] ftpFiles;
            if (StringUtils.isEmpty(filePath)) {
                ftpFiles = ftpClient.listFiles();
            } else {
                ftpClient.changeWorkingDirectory(filePath);
                ftpFiles = ftpClient.listFiles();
            }
            if (!StringUtils.isEmpty(ftpFiles)) {
                fileList = new ArrayList<>();
                for (FTPFile ftpFile : ftpFiles) {
                    FileBean fileBean = new FileBean();
                    fileBean.setFileName(ftpFile.getName());
                    fileBean.setFileSize(ftpFile.getSize());
                    fileBean.setFileType(ftpFile.isDirectory());
                    fileBean.setAccessTime(DateUtil.timeStampToDate(ftpFile.getTimestamp().getTimeInMillis(), DateUtil.DATE_STR_FULL));
                    fileBean.setModifyTime(DateUtil.timeStampToDate(ftpFile.getTimestamp().getTimeInMillis(), DateUtil.DATE_STR_FULL));
                    fileBean.setFilePath("/" + ftpFile.getName() + "/");
                    fileList.add(fileBean);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileList;
    }

    /**
     * 重命名文件
     * @param filePath
     * @param fromFileName 文件原来的名字
     * @param toFileName 文件目标名字
     * @param ftpClient
     * @return
     */
    public static boolean rename(String filePath, String fromFileName, String toFileName, FTPClient ftpClient) {
        boolean result = false;
        try {
            ftpClient.changeWorkingDirectory(filePath);
            result = ftpClient.rename(fromFileName, toFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取当前用户的根目录
     */
    public static String getHomePath() {
        return "/";
    }

    /**
     * 获取当前文件目录
     */
    public static String getCurrPath(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return getHomePath();
        }
        return filePath;
    }

    /**
     * 获取当前文件的上级目录
     */
    public static String getLastPath(String filePath) {
        filePath = getCurrPath(filePath);
        String lastPath;
        String basePath = getHomePath();
        if (filePath.equals(basePath)) {
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
