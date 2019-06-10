package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.WeChatLoginUtils;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.redis.RedisManager;
import com.art1001.supply.service.user.UserNewsService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.shiro.util.JwtUtil;
import com.art1001.supply.util.EmailUtil;
import com.art1001.supply.util.NumberUtils;
import com.art1001.supply.util.RegexUtils;
import com.art1001.supply.util.SendSmsUtils;
import com.art1001.supply.util.crypto.EndecryptUtils;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

    @Resource
    private RedisManager redisManager;

    @Resource
    private UserNewsService userNewsService;

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
             UsernamePasswordToken token = new UsernamePasswordToken(accountName,password,rememberMe);
             subject.login(token);
             if(subject.isAuthenticated()) {
                 object.put("result", 1);
                 object.put("msg", "登陆成功");
                 object.put("userInfo",ShiroAuthenticationManager.getUserEntity());
                 object.put("accessToken",JwtUtil.sign(accountName,ShiroAuthenticationManager.getUserEntity().getCredentialsSalt()));
            } else {
                 object.put("result", 0);
                 object.put("msg", "账号或密码错误");
            }
         } catch (Exception e) {
            // 登录异常，请联系管理员！
            log.error("登录异常，请联系管理员！, {}", e);
            object.put("result", 0);
            object.put("msg", "登录异常，用户名或密码错误！");
         }
         return object;
     }

    /**
     * 用户注册
     * @param captcha 推行验证码
     * @param accountName 用户名
     * @param password 密码
     * @param userName 昵称
     * @return
     */
    @PostMapping("/register")
    public JSONObject register(@RequestParam String captcha,
                               @RequestParam String accountName,
                               @RequestParam String password,
                               @RequestParam String userName,
                               HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        if(!captcha.equalsIgnoreCase(String.valueOf(request.getSession().getAttribute("captcha")))){
            jsonObject.put("result",0);
            jsonObject.put("msg","验证码填写错误");
            return jsonObject;
        }

        //设置创建者姓名
        UserEntity userEntity = new UserEntity();
        userEntity.setCreatorName(accountName);
        userEntity.setAccountName(accountName);
        userEntity.setUserName(userName);
        userEntity.setCreateTime(new Date(System.currentTimeMillis()));
        // 加密用户输入的密码，得到密码和加密盐，保存到数据库
        UserEntity user = EndecryptUtils.md5Password(accountName, password, 2);
        //设置添加用户的密码和加密盐
        userEntity.setPassword(user.getPassword());
        userEntity.setCredentialsSalt(user.getCredentialsSalt());
        try {
            // 保存用户注册信息
            userService.insert(userEntity, password);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "注册成功");
        } catch (ServiceException e){
            throw new AjaxException(e.getMessage(),e);
        } catch (Exception e) {
            log.error("注册失败:", e);
            throw new AjaxException("注册失败",e);
        }
        return jsonObject;
    }

    /**
     * 获取图形验证码
     */
    @GetMapping("/captcha")
    public void getImageCode(HttpServletResponse response, HttpServletRequest request) {
        ServletOutputStream out = null;
        try {
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("image/jpeg");
            String capText = captchaProducer.createText();
            //将验证码存入httpSession
            request.getSession().setAttribute("captcha", capText);
            BufferedImage image = captchaProducer.createImage(capText);
            out = response.getOutputStream();
            ImageIO.write(image, "jpg", out);
            out.flush();
        }catch(IOException e)
        {
            throw new AjaxException(e);
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
        String kaptcha = ShiroAuthenticationManager.getKaptcha("captcha");
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
                throw new AjaxException(e);
            }
        }

        //通过短信发送验证码
        if(RegexUtils.checkMobile(accountName)){
            SendSmsUtils sendSmsUtils = new SendSmsUtils();
            sendSmsUtils.sendSms(accountName,"您的手机验证码为:"+String.valueOf(valid));
            ShiroAuthenticationManager.setSessionAttribute("phoneCode",String.valueOf(valid));
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
            String phoneCode = ShiroAuthenticationManager.getKaptcha("phoneCode");
            if(!phoneCode.equalsIgnoreCase(code)){
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

    @GetMapping("test")
    public String test1(){
        return "1";
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

    /**
     * 微信登录
     */
    @PostMapping("chat_login")
    public void weChatLogin(HttpServletResponse response){
        try {
            response.sendRedirect(WeChatLoginUtils.genUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/getUserInfo/{userId}")
    public  JSONObject  getUserInfo(@PathVariable String userId){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("data",userService.findById(userId));
            jsonObject.put("msg","用户信息获取成功");
            jsonObject.put("result","1");
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("msg","信息获取失败,请稍后再试");
            jsonObject.put("result","0");
        }
        return  jsonObject;
    }


    /**
     * 修改用户信息
     */
    @PostMapping("/updateUserInfo")
    public  JSONObject  updateUserInfo(@RequestParam(value = "userId") String userId,
                                       @RequestParam(value = "defaultImage") String defaultImage,
                                       @RequestParam(value = "userName") String userName,
                                       @RequestParam(value = "job") String job,
                                       @RequestParam(value = "telephone") String telephone,
                                       @RequestParam(value = "birthday") String birthday,
                                       @RequestParam(value = "address") String address,
                                       @RequestParam(value = "email") String email
                                       ){
        JSONObject jsonObject=new JSONObject();
        try {

            SimpleDateFormat myFmt2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date birthDay = myFmt2.parse(birthday);


            UserEntity userEntity=new UserEntity();
            userEntity.setUserId(userId);
            userEntity.setDefaultImage(defaultImage);
            userEntity.setUserName(userName);
            userEntity.setJob(job);
            userEntity.setTelephone(telephone);
            userEntity.setBirthday(birthDay);
            userEntity.setAddress(address);
            userEntity.setEmail(email);
            userEntity.setUpdateTime(new Date());
            userService.updateById(userEntity);
           jsonObject.put("msg","用户信息修改成功");
           jsonObject.put("result","1");
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("msg","信息修改失败,请稍后再试");
            jsonObject.put("result","0");
        }
        return  jsonObject;
    }
}
