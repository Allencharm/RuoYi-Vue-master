package com.ruoyi.file.bean;

import lombok.Data;

/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/8 20:33
 * @description:列表展示目录实体类
 */
@Data
public class DataTableBean {
    private Integer code;

    private String msgs;

    private Long count;

    private Object data;

    private Object param;
}
