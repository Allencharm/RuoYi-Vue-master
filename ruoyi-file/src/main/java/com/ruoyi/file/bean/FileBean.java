package com.ruoyi.file.bean;

import lombok.Data;

/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/8 19:21
 * @description:文件实体类
 */
@Data
public class FileBean {
    private String filePath;

    private String fileName;

    private boolean fileType;

    private long fileSize;

    private String accessTime;

    private String modifyTime;
}
