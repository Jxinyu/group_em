package com.gp.controller;

import com.gp.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ljb
 * @create 2023/6/10
 */
@RestController
@Slf4j
public class ErrorController {
    @RequestMapping("/{msg}/400")
    public R unAuth(@PathVariable("msg") String msg){

        if (msg.equals("unauthenticated")){
            log.info("用户没有登录");
            return R.error(400, "请登录");
        }
        if (msg.equals("1")){
            return R.error(400, "文件过大");
        }
        log.info("用户无权限");
        return R.error(400, "权限不足");
    }

}
