package com.art1001.supply.controller;

import com.art1001.supply.controller.base.BaseController;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.shiro.util.ShiroMD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class IndexController extends BaseController {

    @GetMapping("/")
    public String index() {
        return "demo";
    }

    @GetMapping("/imageCode")
    public void getImageCodee() {

    }

    /**
     * 跳转到登陆页面
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 跳转到注册
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    /**
     * 用户登陆
     *
     * @param accountName 账户名称
     * @param password 密码
     */
    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestParam String accountName,
            @RequestParam String password
    ) {
        Map<String, Object> map = new HashMap<>();
        try {
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(accountName, password, true);
            subject.login(token);
            map.put("result", 1);
            map.put("msg", "登陆成功");
        } catch (Exception e) {
            log.error("登陆错误, {}", e);
            map.put("result", 0);
            map.put("msg", "登陆错误");
        }

        return map;
    }

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param password  密码
     * @param mobile    手机
     * @param mobileCode    手机验证码
     */
    @PostMapping("/register")
    public void register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String mobile,
            @RequestParam String mobileCode
    ) {

    }

    public static void main(String[] args) {
        UserEntity userEntity = new UserEntity();
        userEntity.setAccountName("admin");
        userEntity.setPassword("admin");
        String md5 = ShiroMD5Util.Md5(userEntity);
        System.out.println(md5);
    }

}
