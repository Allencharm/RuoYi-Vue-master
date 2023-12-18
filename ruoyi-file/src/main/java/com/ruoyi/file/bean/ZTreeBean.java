package com.ruoyi.file.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/8 19:22
 * @description:目录树
 */
public class ZTreeBean {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean getIsParent() {
        return isParent;
    }

    public void setIsParent(boolean isParent) {
        this.isParent = isParent;
    }

    private String id;

    @JSONField(name = "pId")
    private String pId;

    private String name;

    private String path;

    private boolean checked = false;

    @JSONField(name = "isParent")
    private boolean isParent;

    public ZTreeBean() {
    }

    public ZTreeBean(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public ZTreeBean(String id, String name, String path, boolean isParent) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.isParent = isParent;
    }

    public ZTreeBean(String id, String pId, String name) {
        this.id = id;
        this.pId = pId;
        this.name = name;
    }

    public ZTreeBean(String id, String pId, String name, String path, boolean isParent) {
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.path = path;
        this.isParent = isParent;
    }
}
