package com.ruoyi.file.bean;

import lombok.Data;

import java.util.Date;

/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/7 18:52
 * @description:父级实体
 */
@Data
public class BaseBean {
    //创建时间
    private Date createTime;

    //修改时间
    private Date updateTime;

    //创建人
    private String createUser;

    //修改人
    private String updateUser;

}
