package com.art1001.supply.wechat.login.controller;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.util.JwtUtil;
import com.art1001.supply.util.ObjectsUtil;
import com.art1001.supply.util.RedisUtil;
import com.art1001.supply.wechat.login.dto.UpdateUserInfoRequest;
import com.art1001.supply.wechat.login.dto.WeChatDecryptResponse;
import com.art1001.supply.wechat.login.service.WeChatAppLogin;
import com.art1001.supply.wechat.util.WeChatUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

/**
 * @author heshaohua
 * @date 2019/11/20 13:50
 **/
@Slf4j
@RestController
@RequestMapping("/wx_app")
public class WeChatAppLoginController extends BaseController {

    @Resource
    private WeChatAppLogin weChatAppLogin;

    @Resource
    private UserService userService;

    @Resource
    private OrganizationMemberService organizationMemberService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private WeChatUtil weChatUtil;


    /**
     * 微信小程序登录
     */
    @GetMapping("/login")
    public Object wxAppLogin(@Validated @NotNull(message = "code 不能为空！") String code) {
        log.info("weChat app login.[{}]", code);
        return success(weChatAppLogin.login(code));
    }

    /**
     * 存储微信小程序登录用户的信息
     *
     * @param user 用户加密信息
     */
    @PostMapping("/user/info")
    public JSONObject updateWeChatUserInfo(@Validated UpdateUserInfoRequest user) throws Exception {
        log.info("save weChat user info. [{}]", user);

        JSONObject jsonObject = new JSONObject();
        WeChatDecryptResponse res = WeChatUtil.deciphering(
                user.getEncryptedData(), user.getIv(), redisUtil.get(Constants.WE_CHAT_SESSION_KEY_PRE + user.getOpenId()), WeChatDecryptResponse.class
        );
        redisUtil.remove(Constants.WE_CHAT_SESSION_KEY_PRE + user.getOpenId());
        LambdaQueryWrapper<UserEntity> getSingleUserByWxUnionId = new QueryWrapper<UserEntity>().lambda()
                .eq(UserEntity::getWxUnionId, res.getUnionId());
        UserEntity one = userService.getOne(getSingleUserByWxUnionId);
        //如果pc微信已经注册
        if (ObjectsUtil.isNotEmpty(one)) {
            UserEntity saveUserInfo = new UserEntity();
            saveUserInfo.setUserId(one.getUserId());
            saveUserInfo.setWxAppOpenId(res.getOpenId());
            saveUserInfo.setUpdateTime(new Date());
            userService.updateById(saveUserInfo);

//            String secret = redisUtil.get("power:" + one.getUserId());
            jsonObject.put("userId", one.getUserId());
//            jsonObject.put("accessToken", JwtUtil.sign(one.getUserId(), secret));
            jsonObject.put("accessToken", JwtUtil.sign(one.getUserId(), "1qaz2wsx#EDC"));
            jsonObject.put("result", 1);
        } else {
            jsonObject.put("result", 0);
            jsonObject.put("msg", "请先到pc端绑定微信之后在登陆小程序");
        }


        return jsonObject;
    }

    /**
     * 绑定小程序手机号
     *
     * @param data 加密数据
     * @param iv   偏移量
     */
    @PostMapping("/bind/phone")
    public Object getWeChatAppPhone(@Validated
                                    @RequestParam @NotNull(message = "data不能为空") String data,
                                    @Validated
                                    @RequestParam @NotNull(message = "加密偏移量不能为空") String iv,
                                    @Validated
                                    @RequestParam @NotNull(message = "code不能为空") String code
    ) throws Exception {
        log.info("Get wCchat login user phone. [{},{},{}]", data, iv, code);

        Map result = weChatAppLogin.bindPhone(data, iv, code);

        return success(result);
    }

    /**
     * 获取默认企业id
     *
     * @param userId
     * @return
     */
    @GetMapping("/getUserDefaultOrgId")
    public JSONObject getUserDefaultOrgId(@RequestParam("userId") String userId) {
        JSONObject jsonObject = new JSONObject();
        try {
            OrganizationMember one = organizationMemberService.getOne(new QueryWrapper<OrganizationMember>().eq("member_id", userId).eq("user_default", 1));
            if (one != null) {
                jsonObject.put("result", 1);
                jsonObject.put("data", one.getOrganizationId());
            } else {
                jsonObject.put("result", 0);
                jsonObject.put("msg", "你还没有加入任何企业，请加入或创建一个企业后再试吧");
            }
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    @GetMapping("access_token")
    public JSONObject getAccessToken() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", weChatUtil.getAccessToken());
        jsonObject.put("result", 1);
        return jsonObject;
    }
}
