package com.ruoyi.file.bean;

import com.ruoyi.file.tool.DateUtil;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/8 15:58
 * @description:响应实体
 */
@Component
@Data
public class ResponseBean {

    private Integer code;// 响应代码
    private String msgs;// 响应信息
    private Object data;// 响应数据
    private String time;// 响应时间

    public ResponseBean() {
        this.code = 200;
        this.time = DateUtil.formatDateTime(new Date());
    }

    public ResponseBean(Integer code, String msgs, Object data) {
        this.code = code;
        this.msgs = msgs;
        this.data = data;
        this.time = DateUtil.formatDateTime(new Date());
    }
}
