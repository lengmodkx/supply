package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.aliyun.message.enums.KeyWord;
import com.art1001.supply.aliyun.message.service.aliyun.AliyunMessageService;
import com.art1001.supply.aliyun.message.util.PhoneTest;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.CodeMsg;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserInfo;
import com.art1001.supply.entity.user.WeChatLoginUtils;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.shiro.util.JwtUtil;
import com.art1001.supply.util.RedisUtil;
import com.art1001.supply.util.RegexUtils;
import com.art1001.supply.util.crypto.EndecryptUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 用户
 * @author 汪亚锋
 * [POST]   // 新增
 * [GET]    // 查询
 * [PATCH]  // 更新
 * [PUT]    // 覆盖，全部更新
 * [DELETE] // 删除
 */
@Validated
@Slf4j
@RestController
public class UserApi {

    @Resource
    private UserService userService;

    @Resource
    private Producer captchaProducer;

    @Resource
    private FileService fileService;

    @Resource
    private AliyunMessageService aliyunMessageService;

    @Resource
    private RedisUtil redisUtil;

    /**
     * 用户登陆
     * @param accountName 账户名称
     * @param password 密码
     * @param rememberMe 记住我
     */
    @PostMapping("/login")
     public Result<UserInfo> login(@RequestParam String accountName,
                                   @RequestParam String password,
                                   @RequestParam(required = false, defaultValue = "true") Boolean rememberMe){
         try {
             Subject subject = SecurityUtils.getSubject();
             UsernamePasswordToken token = new UsernamePasswordToken(accountName,password,rememberMe);
             subject.login(token);
             if(subject.isAuthenticated()) {
                 UserInfo userInfo = userService.findInfo(accountName);
                 redisUtil.set(Constants.USER_INFO + userInfo.getUserId(), userInfo);
                 return Result.success(userInfo);
             } else {
                 return Result.fail(CodeMsg.ACCOUNT_OR_PASSWORD_ERROR);
             }
         } catch (Exception e) {
            // 登录异常，请联系管理员！
            log.error("登录异常，请联系管理员！", e);
            return Result.fail(CodeMsg.ACCOUNT_OR_PASSWORD_ERROR);
         }
     }

    /**
     * 用户注册
     * @param captcha 推行验证码
     * @param accountName 用户名
     * @param password 密码
     * @param userName 昵称
     * @param job 职务
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestParam String captcha,
                           @RequestParam String accountName,
                           @RequestParam String password,
                           @RequestParam String userName,
                           @RequestParam String job,
                           HttpServletRequest request) {
        if(!captcha.equalsIgnoreCase(String.valueOf(request.getSession().getAttribute("captcha")))){
            return Result.fail(CodeMsg.CAPTCHA_ERROR);
        }
        UserEntity entity = userService.findByName(accountName);
        if(entity!=null){
            return Result.fail("用户已存在");
        }
        //设置创建者姓名
        UserEntity userEntity = new UserEntity();
        userEntity.setCreatorName(accountName);
        userEntity.setAccountName(accountName);
        userEntity.setUserName(userName);
        userEntity.setJob(job);
        userEntity.setCreateTime(new Date(System.currentTimeMillis()));
        // 加密用户输入的密码，得到密码和加密盐，保存到数据库
        UserEntity user = EndecryptUtils.md5Password(accountName, password, 2);
        //设置添加用户的密码和加密盐
        userEntity.setPassword(user.getPassword());
        userEntity.setCredentialsSalt(user.getCredentialsSalt());
        try {
            // 保存用户注册信息
            userService.insert(userEntity, password);
            return Result.success();
        } catch (Exception e) {
            log.error("注册失败:", e);
            return Result.fail(CodeMsg.REGISTER_FAIL);
        }
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
     * @return
     */
    @GetMapping("/code")
    public Result code(@RequestParam String accountName,@RequestParam String captcha,HttpServletRequest request){
        String kaptcha = String.valueOf(request.getSession().getAttribute("captcha"));
        if(!kaptcha.equalsIgnoreCase(captcha)){
            return Result.fail(CodeMsg.CAPTCHA_ERROR);
        }
        //通过短信发送验证码
        if(RegexUtils.checkMobile(accountName)){
            UserEntity byName = userService.findByName(accountName);
            if(byName == null){
                return Result.fail("用户不存在");
            }
            aliyunMessageService.sendCode( byName.getUserId(), accountName);
        } else {
            return Result.fail(CodeMsg.PHONE_ERROR);
        }
        return Result.success();

    }

    /**
     * 忘记密码
     * @param accountName 用户名
     * @param password 密码
     * @param code 验证码
     * @return
     */
    @PutMapping("/forget")
    public Result forget(@RequestParam String accountName,
                         @RequestParam String password,
                         @RequestParam String code){
        try {
            UserEntity userEntity = userService.findByName(accountName);
            if(userEntity==null){
                return Result.fail(CodeMsg.USER_NO);
            }

            if(!redisUtil.exists(KeyWord.PREFIX.getCodePrefix() + userEntity.getUserId())){
                return Result.fail(CodeMsg.CAPTCHA_NO_USE);
            }

            String redisCode = redisUtil.get(KeyWord.PREFIX.getCodePrefix() + userEntity.getUserId());
            if(!Objects.equals(code, redisCode)){
                return Result.fail(CodeMsg.CAPTCHA_ERROR);
            }

            //加密用户输入的密码，得到密码和加密盐，保存到数据库
            UserEntity user = EndecryptUtils.md5Password(accountName, password, 2);
            //设置添加用户的密码和加密盐
            userEntity.setPassword(user.getPassword());
            userEntity.setCredentialsSalt(user.getCredentialsSalt());
            userService.updateById(userEntity);
            return Result.success();
        }catch (Exception e){
            log.error("系统异常,密码修改失败:",e);
            return Result.fail(CodeMsg.SERVER_ERROR);
        }
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
    @GetMapping("wechatcode")
    public Result<String> weChatLogin(@Validated @NotNull(message = "回调地址不能为空！")@RequestParam String redirectUri){
        try {
            return Result.success(WeChatLoginUtils.genUrl(redirectUri));
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 微信登录保存用户信息
     * @param code
     * @return
     */
    @RequestMapping("wechattoken")
    public Result<UserInfo> getWeChatToken(@RequestParam String code){
        log.info("weChat token code is [{}]",code);
        UserInfo userInfo = userService.saveWeChatUserInfo(code);
        return Result.success(userInfo);
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
                                       @RequestParam(value = "image",required = false) String image,
                                       @RequestParam(value = "userName",required = false) String userName,
                                       @RequestParam(value = "job",required = false) String job,
                                       @RequestParam(value = "telephone",required = false) String telephone,
                                       @RequestParam(value = "birthday",required = false) String birthday,
                                       @RequestParam(value = "address",required = false) String address,
                                       @RequestParam(value = "email",required = false) String email
                                       ){
        JSONObject jsonObject=new JSONObject();
        try {
            UserEntity userEntity=new UserEntity();
            userEntity.setUserId(userId);
            if(StringUtils.isNotEmpty(image)){
                userEntity.setImage(image);
                userEntity.setDefaultImage(image);
            }

            if(StringUtils.isNotEmpty(userName)){
                userEntity.setUserName(userName);
            }

            if(StringUtils.isNotEmpty(job)){
                userEntity.setJob(job);
            }

            if(StringUtils.isNotEmpty(telephone)){
                userEntity.setTelephone(telephone);
            }

            if(StringUtils.isNotEmpty(birthday)){
                SimpleDateFormat myFmt2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date birthDay = myFmt2.parse(birthday);
                userEntity.setBirthday(birthDay);
            }
            if(StringUtils.isNotEmpty(address)){
                userEntity.setAddress(address);
            }
            if(StringUtils.isNotEmpty(email)){
                userEntity.setEmail(email);
            }

            userEntity.setUpdateTime(new Date());
            userService.updateById(userEntity);
            jsonObject.put("msg","用户信息修改成功");
            jsonObject.put("result",1);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("msg","信息修改失败,请稍后再试");
            jsonObject.put("result","0");
        }
        return  jsonObject;
    }

    /**
     * 用户绑定手机号码
     * @param phone 手机号码
     * @param code 验证码
     * @return 结果
     */
    @PostMapping("/bind/phone")
    public JSONObject bindPhone(
                                @Validated @NotNull(message = "手机号不能为空！") String phone,
                                @Validated @NotNull(message = "code码不能为空！") String code,
                                @Validated @NotNull(message = "用户id不能为空！") String userId,
                                @RequestParam(required = false) String nickName){


        log.info("bind phone. [{},{},{},{}]", phone, code ,userId, nickName);

        PhoneTest.testPhone(phone);
        userService.bindPhone(phone, code, userId, nickName);

        UserEntity byId = userService.findByName(phone);

        JSONObject json = new JSONObject();

        json.put("userInfo", byId);
        json.put("accessToken", JwtUtil.sign(phone, byId.getCredentialsSalt()));
        json.put("result",1);
        return json;
    }

    @GetMapping("/is_bind_phone")
    public Object isBindPhone(){
        JSONObject jsonObject = new JSONObject();
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        jsonObject.put("bindPhone", userEntity.getAccountName().length() > 11);
        return jsonObject;
    }

//    @PutMapping("/wechat_info")
//    public Object wechatInfo(){
//
//    }

    @PostMapping("/qianyi")
    public Object qianyi(){
        List<String> userIds = userService.getAllUserId();

        List<String> phoneList = userService.getPhoneList();


        userIds.forEach(id -> {
            phoneList.forEach(phone -> {
                LambdaQueryWrapper<UserEntity> eq = new QueryWrapper<UserEntity>().lambda().eq(UserEntity::getAccountName,phone);
                UserEntity one = userService.getOne(eq);
                if(one != null){
//                    projectService.updateAllProject(one.getUserId(), id);
//                    projectMemberService.updateAll(one.getUserId(),id);
//                    taskService.updateAll(one.getUserId(),id);
//                    fileService.updateAll(one.getUserId(),id);
                    fileService.updateAllUser(one.getUserId(),id);
                }

            });
        });
        return null;
    }

    /**
     * 原有平台账号绑定微信号
     * @param code 微信code码
     * @param useId 用户id
     */
    @PostMapping("/bind/wechat")
    public Result bindWeChat(@Validated @NotNull(message = "code码不能为空！") String code,
                                 @Validated @NotNull(message = "useId不能为空！") String useId){

        log.info("bind weChat [{},{}]", code, useId);
        userService.bindWeChat(code,useId);
        return Result.success();
    }

    @PostMapping("/notbind/wechat")
    public Result notBindWeChat(@Validated @NotNull(message = "useId不能为空！") String useId){

        log.info("bind weChat [{}]", useId);
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(useId);
        userEntity.setWxOpenId("");
        userEntity.setWxUnionId("");
        userService.updateById(userEntity);
        return Result.success();
    }

    @PostMapping("reset_password")
    public Object resetPassword(@Validated @NotNull(message = "密码不能为空") String password,String accountName){
        JSONObject jsonObject = new JSONObject();
        UserEntity byName = userService.findByName(accountName);

        String password_cryto = new Md5Hash(password,byName.getAccountName()+byName.getCredentialsSalt(),2).toBase64();

        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(byName.getUserId());
        userEntity.setUpdateTime(new Date());
        userEntity.setPassword(password_cryto);
        userService.updateById(userEntity);
        return jsonObject;
    }

    @PostMapping("change_password")
    public Result changePassword(@Validated @NotNull(message = "原密码不能为空") String oldPassword,
                                 @Validated @NotNull(message = "新密码不能为空") String newPassword){
        log.info("Change current user password. [{},{},{}]", oldPassword, newPassword, ShiroAuthenticationManager.getUserId());
        userService.changePasswordByUserId(oldPassword, newPassword, ShiroAuthenticationManager.getUserId());

        return Result.success();
    }
    /**
     * 管理员登陆
     * @param accountName 账户名称
     * @param password 密码
     */
    @PostMapping("/adminlogin")
    public JSONObject adminLogin(@RequestParam String accountName,
                                 @RequestParam String password){
        JSONObject object = new JSONObject();
        try {
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(accountName,password);
            subject.login(token);
            if(subject.isAuthenticated()) {
                object.put("fileId", Constants.MATERIAL_BASE);
                object.put("result", 1);
                object.put("userInfo",ShiroAuthenticationManager.getUserEntity());
                object.put("accessToken",JwtUtil.sign(ShiroAuthenticationManager.getUserId(),"1qaz2wsx#EDC"));
            } else {
                object.put("result", 0);
                object.put("msg", "账号或密码错误");
            }
        } catch (Exception e) {
            // 登录异常，请联系管理员！
            log.error("登录异常，请联系管理员！", e);
            object.put("result", 0);
            object.put("msg", "登录异常，用户名或密码错误！");
        }
        return object;
    }

    /**
     * 查询用户名是否已经存在
     * @return 是否存在
     */
    @GetMapping("check_account_exist")
    public Result checkAccountNameIsExist(@NotNull(message = "用户名不能为空") String accountName){
        log.info("Check user accountName is exist. [{}]", accountName);
        return Result.success(userService.checkUserIsExistByAccountName(accountName));
    }
}
