package com.ruoyi.file.tool;

import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;

/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/8 18:57
 * @description: 接口返回信息
 */
public class ReModel {

    @Nullable
    private ModelMap model;
//    @Nullable
//    private HttpStatus status;
    private boolean cleared = false;

    public ReModel() {
    }

    public ModelMap getModelMap() {
        if (this.model == null) {
            this.model = new ModelMap();
        }

        return this.model;
    }
    public ReModel addObject(String attributeName, Object attributeValue ){
        this.getModelMap().addAttribute(attributeName,attributeValue);
        return this;

    }
}
