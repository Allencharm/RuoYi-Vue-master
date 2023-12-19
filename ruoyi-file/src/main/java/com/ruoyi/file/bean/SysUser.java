package com.ruoyi.file.bean;

import lombok.Data;

/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/16 15:53
 * @description: 用户实体
 */
@Data
public class SysUser extends BaseBean{

    //用户ID
    private String userId;

    //用户名称
    private String userName;

    //用户密码
    private String userPass;

    //密码加盐
    private String userSalt;

    //用户头像
    private String userMail;

    //用户头像
    private String userHead;

    //用户类型，0：系统用户，1:普通用户
    private String userType;

    //用户状态，0：正常，1:冻结，2：其他
    private String userStatus;

    public SysUser() {
    }

    public SysUser(String userId, String userPass) {
        this.userId = userId;
        this.userPass = userPass;
    }

    public SysUser(SysUser user) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.userPass = user.getUserPass();
        this.userSalt = user.getUserSalt();
        this.userMail = user.getUserMail();
        this.userHead = user.getUserHead();
        this.userType = user.getUserType();
        this.userStatus = user.getUserStatus();
    }

}
