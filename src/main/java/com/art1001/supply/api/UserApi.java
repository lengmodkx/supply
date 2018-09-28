package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.EmailUtil;
import com.art1001.supply.util.NumberUtils;
import com.art1001.supply.util.RegexUtils;
import com.art1001.supply.util.SendSmsUtils;
import com.art1001.supply.util.crypto.EndecryptUtils;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

/**
 * 用户
 * @author 汪亚锋
 * [POST]   // 新增
 * [GET]    // 查询
 * [PATCH]  // 更新
 * [PUT]    // 覆盖，全部更新
 * [DELETE] // 删除
 */
@Slf4j
@RestController
public class UserApi {

    @Resource
    private UserService userService;

    @Resource
    private Producer captchaProducer;

    /**
     * 用户登陆
     * @param accountName 账户名称
     * @param password 密码
     * @param rememberMe 记住我
     */
    @PostMapping("/login")
     public JSONObject login(@RequestParam String accountName,
                             @RequestParam String password,
                             @RequestParam(required = false, defaultValue = "true") Boolean rememberMe){
         JSONObject object = new JSONObject();
         try {
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(accountName, password, rememberMe);
            subject.login(token);
            if (subject.isAuthenticated()) {
                object.put("result", 1);
                object.put("msg", "登陆成功");
            } else {
                object.put("result", 0);
                object.put("msg", "账号或密码错误");
            }
         } catch (UnknownAccountException e) {
            // 账户不存在
            log.error("账户不存在, {}", e);
            object.put("result", 0);
            object.put("msg", "账户不存在");
         } catch (IncorrectCredentialsException e) {
            // 密码错误,连续输错5次,帐号将被锁定10分钟
            log.error("密码错误,连续输错5次,帐号将被锁定10分钟, {}", e);
            object.put("result", 0);
            object.put("msg", "密码错误,连续输错5次,帐号将被锁定10分钟");
         } catch (LockedAccountException e) {
            // 您的账户已被锁定,请与管理员联系或10分钟后重试！
            log.error("您的账户已被锁定,请与管理员联系或10分钟后重试！, {}", e);
            object.put("result", 0);
            object.put("msg", "您的账户已被锁定,请与管理员联系或10分钟后重试！");
         } catch (ExcessiveAttemptsException e) {
            // 您连续输错密码5次,帐号将被锁定10分钟!
            log.error("您连续输错密码5次,帐号将被锁定10分钟!, {}", e);
            object.put("result", 0);
            object.put("msg", "您连续输错密码5次,帐号将被锁定10分钟!");
         } catch (ExpiredCredentialsException e) {
            // 账户凭证过期！
            log.error("账户凭证过期！, {}", e);
            object.put("result", 0);
            object.put("msg", "账户凭证过期！");
         } catch (AuthenticationException e) {
            // 账户验证失败！
            log.error("账户验证失败！, {}", e);
            object.put("result", 0);
            object.put("msg", "账户验证失败！");
         } catch (Exception e) {
            // 登录异常，请联系管理员！
            log.error("登录异常，请联系管理员！, {}", e);
            object.put("result", 0);
            object.put("msg", "登录异常，请联系管理员！");
         }
         return object;
     }

    /**
     * 用户注册
     *
     * @param captcha 图形验证码
     * @param userEntity 用户实体
     */
    @PostMapping("/register")
    public JSONObject register(@RequestParam String captcha, UserEntity userEntity, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        String kaptcha = ShiroAuthenticationManager.getKaptcha(request.getSession().getId());

        if(!captcha.equalsIgnoreCase(kaptcha)){
            jsonObject.put("result",0);
            jsonObject.put("msg","验证码填写错误");
            return jsonObject;
        }

        //设置创建者姓名
        userEntity.setCreatorName(userEntity.getAccountName());
        userEntity.setCreateTime(new Date(System.currentTimeMillis()));
        // 加密用户输入的密码，得到密码和加密盐，保存到数据库
        UserEntity user = EndecryptUtils.md5Password(userEntity.getAccountName(), userEntity.getPassword(), 2);
        //设置添加用户的密码和加密盐
        userEntity.setPassword(user.getPassword());
        userEntity.setCredentialsSalt(user.getCredentialsSalt());
        try {
            // 保存用户注册信息
            userService.insert(userEntity, userEntity.getPassword());
            jsonObject.put("result", 1);
            jsonObject.put("msg", "注册成功");
        } catch (Exception e) {
            log.error("注册失败:", e);
        }

        return jsonObject;
    }

    /**
     * 获取图形验证码
     */
    @GetMapping("/captcha")
    public void getImageCode(HttpServletRequest request,HttpServletResponse response) {
        ServletOutputStream out = null;
        try {
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("image/jpeg");
            String capText = captchaProducer.createText();
            //将验证码存入shiro 登录用户的session
            ShiroAuthenticationManager.setSessionAttribute(request.getSession().getId(), capText);
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

    /**
     * 获取验证码
     * @param accountName 用户名
     * @param captcha 图形验证码
     * @param request
     * @return
     */
    @GetMapping("/code")
    public JSONObject code(@RequestParam String accountName,@RequestParam String captcha,HttpServletRequest request){
        String kaptcha = ShiroAuthenticationManager.getKaptcha(request.getSession().getId());
        JSONObject jsonObject = new JSONObject();
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
            sendSmsUtils.sendSms(accountName,"您的手机验证码为:"+String.valueOf(valid));
            ShiroAuthenticationManager.setSessionAttribute(request.getSession().getId(),String.valueOf(valid));
        }
        jsonObject.put("result",1);
        jsonObject.put("msg","发送成功");
        return jsonObject;
    }

    /**
     * 忘记密码
     * @param accountName 用户名
     * @param password 密码
     * @param code 验证码
     * @param request
     * @return
     */
    @PutMapping("/forget")
    public JSONObject forget(@RequestParam String accountName,
                             @RequestParam String password,
                             @RequestParam String code,HttpServletRequest request){
        JSONObject jsonObject = new JSONObject();
        try {
            String  kaptcha = ShiroAuthenticationManager.getKaptcha(request.getSession().getId());
            if(!kaptcha.equalsIgnoreCase(code)){
                jsonObject.put("result",0);
                jsonObject.put("msg","手机验证码输入错误！");
                return jsonObject;
            }

            UserEntity userEntity = userService.findByName(accountName);
            if(userEntity==null){
                jsonObject.put("result",0);
                jsonObject.put("msg","用户不存在，请检查");
                return jsonObject;
            }
            //加密用户输入的密码，得到密码和加密盐，保存到数据库
            UserEntity user = EndecryptUtils.md5Password(accountName, password, 2);
            //设置添加用户的密码和加密盐
            userEntity.setPassword(user.getPassword());
            userEntity.setCredentialsSalt(user.getCredentialsSalt());
            userService.updatePassword(userEntity, password);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "密码修改成功,请重新登录");
        }catch (Exception e){
            log.error("系统异常,密码修改失败:",e);
            throw new AjaxException(e);
        }

        return jsonObject;
    }
    /**
     * 用户退出
     * @return	视图信息
     */
    @GetMapping(value = "/logout")
    public void logout() {
        //这里执行退出系统之前需要清理数据的操作
        // 注销登录
        ShiroAuthenticationManager.logout();
    }
}
