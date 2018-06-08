package com.art1001.supply.controller;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.controller.base.BaseController;
import com.art1001.supply.entity.role.RoleEntity;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.service.user.UserSessionService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.*;
import com.art1001.supply.util.crypto.EndecryptUtils;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.sun.org.glassfish.external.probe.provider.annotations.ProbeParam;
import jodd.props.Props;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

@Controller
@Slf4j
public class IndexController extends BaseController {

    @Resource
    private RoleService roleService;

    @Resource
    private UserService userService;

    @Resource
    private Producer captchaProducer;

    @GetMapping("/home.html")
    public String index() {
        return "home";
    }

    /**
     * 跳转到登陆页面
     */
    @GetMapping("/login.html")
    public String login() {
        return "login";
    }

    /**
     * 跳转到注册
     */
    @GetMapping("/register.html")
    public String register() {
        return "register";
    }

    /**
     * 用户登陆
     *
     * @param accountName 账户名称
     * @param password 密码
     * @param rememberMe 记住我
     */
    @PostMapping("/login")
    @ResponseBody
    public JSONObject login(
            @RequestParam String accountName,
            @RequestParam String password,
            @RequestParam(required = false, defaultValue = "true") Boolean rememberMe,
            HttpServletRequest request
    ) {
        JSONObject jsonObject = new JSONObject();
        // 得到是哪个页面跳转的登陆
        String refer = request.getHeader("REFERER");
        try {
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(accountName, password, rememberMe);
            subject.login(token);
            if (subject.isAuthenticated()) {
                jsonObject.put("result", 1);
                jsonObject.put("refer", refer);
                jsonObject.put("msg", "登陆成功");
            } else {
                jsonObject.put("result", 0);
                jsonObject.put("msg", "账号或密码错误");
            }

        } catch (UnknownAccountException e) {
            // 账户不存在
            log.error("账户不存在, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "账户不存在");
        } catch (IncorrectCredentialsException e) {
            // 密码错误,连续输错5次,帐号将被锁定10分钟
            log.error("密码错误,连续输错5次,帐号将被锁定10分钟, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "密码错误,连续输错5次,帐号将被锁定10分钟");
        } catch (LockedAccountException e) {
            // 您的账户已被锁定,请与管理员联系或10分钟后重试！
            log.error("您的账户已被锁定,请与管理员联系或10分钟后重试！, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "您的账户已被锁定,请与管理员联系或10分钟后重试！");
        } catch (ExcessiveAttemptsException e) {
            // 您连续输错密码5次,帐号将被锁定10分钟!
            log.error("您连续输错密码5次,帐号将被锁定10分钟!, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "您连续输错密码5次,帐号将被锁定10分钟!");
        } catch (ExpiredCredentialsException e) {
            // 账户凭证过期！
            log.error("账户凭证过期！, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "账户凭证过期！");
        } catch (AuthenticationException e) {
            // 账户验证失败！
            log.error("账户验证失败！, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "账户验证失败！");
        } catch (Exception e) {
            // 登录异常，请联系管理员！
            log.error("登录异常，请联系管理员！, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "登录异常，请联系管理员！");
        }
        return jsonObject;
    }

    /**
     * 用户注册
     *
     * @param captcha 图形验证码
     * @param userEntity 用户实体
     */
    @PostMapping("/register")
    @ResponseBody
    public JSONObject register(
            @RequestParam String captcha,
            UserEntity userEntity,
            HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        // 得到是哪个页面跳转的登陆
        String refer = request.getHeader("REFERER");
        String kaptcha = ShiroAuthenticationManager.getKaptcha(Constants.KAPTCHA_SESSION_KEY);
        if(StringUtils.isEmpty(captcha)){
            jsonObject.put("result",0);
            jsonObject.put("msg","请输入验证码");
            return jsonObject;
        }

        if(!captcha.equalsIgnoreCase(kaptcha)){
            jsonObject.put("result",0);
            jsonObject.put("msg","验证码填写错误");
            return jsonObject;
        }

        //设置创建者姓名
        userEntity.setCreatorName(userEntity.getAccountName());
        userEntity.setCreateTime(new Date(System.currentTimeMillis()));
        userEntity.setAccountName(userEntity.getAccountName());
        // 加密用户输入的密码，得到密码和加密盐，保存到数据库
        UserEntity user = EndecryptUtils.md5Password(userEntity.getAccountName(), userEntity.getPassword(), 2);
        //设置添加用户的密码和加密盐
        userEntity.setPassword(user.getPassword());
        userEntity.setCredentialsSalt(user.getCredentialsSalt());
        //设置锁定状态：未锁定；删除状态：未删除
        userEntity.setLocked(0);
        userEntity.setDeleteStatus(0);
        //通过注册页面注册的用户统一设置为普通用户
        RoleEntity roleEntity = roleService.findByName("普通用户");
        userEntity.setRole(roleEntity);
        try {
            // 保存用户注册信息
            userService.insert(userEntity, userEntity.getPassword());
            jsonObject.put("result", 1);
            jsonObject.put("msg", "注册成功");
            jsonObject.put("refer", refer);
        } catch (Exception e) {
            log.error("注册失败, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "注册失败");
        }

        return jsonObject;
    }

    /**
     * 用户退出
     * @return	视图信息
     */
    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout() {
        //这里执行退出系统之前需要清理数据的操作

        // 注销登录
        ShiroAuthenticationManager.logout();
        return "redirect:/login.html";
    }


    /**
     * 获取图形验证码
     */
    @GetMapping("/captcha.html")
    public void getImageCode(HttpServletResponse response) {
        ServletOutputStream out = null;
        try {
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("image/jpeg");
            String capText = captchaProducer.createText();
            //将验证码存入shiro 登录用户的session
            ShiroAuthenticationManager.setSessionAttribute(Constants.KAPTCHA_SESSION_KEY, capText);
            BufferedImage image = captchaProducer.createImage(capText);
            out = response.getOutputStream();
            ImageIO.write(image, "jpg", out);
            out.flush();
        }catch(IOException e)
        {
            throw new SystemException(e);
        } finally {
            try {
                if(null != out)
                {
                    out.close();
                }
            } catch (IOException e) {
                log.error("关闭输出流异常:", e);
            }
        }
    }

    @PostMapping("/code")
    @ResponseBody
    public JSONObject code(@RequestParam String accountName,@RequestParam String captcha){
        String kaptcha = ShiroAuthenticationManager.getKaptcha(Constants.KAPTCHA_SESSION_KEY);
        JSONObject jsonObject = new JSONObject();
        if(StringUtils.isEmpty(accountName)){
            jsonObject.put("result",0);
            jsonObject.put("msg","请输入用户名！");
            return jsonObject;
        }

        if (StringUtils.isEmpty(captcha)){
            jsonObject.put("result",0);
            jsonObject.put("msg","请输入验证码！");
            return jsonObject;
        }

        if(!kaptcha.equalsIgnoreCase(captcha)){
            jsonObject.put("result",0);
            jsonObject.put("msg","验证码输入错误！");
            return jsonObject;
        }

        Integer valid = NumberUtils.getRandomInt(99999);
        //通过邮箱发送验证码
        if(RegexUtils.checkEmail(accountName)){
            try {
                EmailUtil emailUtil = new EmailUtil();
                emailUtil.send126Mail("","",String.valueOf(valid));
            }catch (Exception e){
                throw new SystemException(e);
            }
        }

        //通过短信发送验证码
        if(RegexUtils.checkMobile(accountName)){
            SendSmsUtils sendSmsUtils = new SendSmsUtils();
            sendSmsUtils.sendSms(accountName,String.valueOf(valid));

        }
        return jsonObject;
    }

}
