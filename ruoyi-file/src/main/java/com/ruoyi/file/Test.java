package com.ruoyi.file;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author cnsibo
 * @version 1.0
 * @email cnsibo01@gmail.com
 * @date 2023/10/5 16:37
 */
@RestController
@RequestMapping("/hello")
public class Test {

    @Anonymous
    @GetMapping("/testpro")
    public String testRro()
    {
        System.out.println("*****cindy is running fine");
        return "hello project";
    }
}
