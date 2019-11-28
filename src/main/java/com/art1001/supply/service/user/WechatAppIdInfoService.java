package com.art1001.supply.service.user;

import com.art1001.supply.wechat.login.entity.WechatAppIdInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author heshaohua
 * @since 2019-11-26
 */
public interface WechatAppIdInfoService extends IService<WechatAppIdInfo> {

    /**
     * 根据用户id获取微信登陆的openId
     * @param userId 用户id
     * @return 信息
     */
    WechatAppIdInfo getAppIdInfoByUserId(String userId, Integer type);
}
