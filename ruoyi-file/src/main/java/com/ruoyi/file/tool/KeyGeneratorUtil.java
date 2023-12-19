package com.ruoyi.file.tool;

import java.util.UUID;

/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/8 16:08
 * @description:随机生成32位的UUid
 */
public class KeyGeneratorUtil {

    /**
     * 生成32位的UUid
     */
    public static String getUUIDKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
