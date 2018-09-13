package com.art1001.supply.controller;
import com.art1001.supply.controller.base.BaseController;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@Slf4j
public class IndexController extends BaseController {
    /**
     * 跳转到登陆页面
     */
    @GetMapping("/")
    public String login() {
        return "login";
    }


    @GetMapping("/login.html")
    public String loginHtml() {
        return "login";
    }


    @GetMapping("/register.html")
    public String register() {
        return "register";
    }

    @GetMapping("/forget.html")
    public String forget(){
        return "forget-pwd";
    }

}
