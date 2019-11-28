package com.art1001.supply.service.user.impl;

import com.art1001.supply.exception.ValidatedUtil;
import com.art1001.supply.mapper.user.WechatAppIdInfoMapper;
import com.art1001.supply.service.user.WechatAppIdInfoService;
import com.art1001.supply.wechat.login.entity.WechatAppIdInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heshaohua
 * @since 2019-11-26
 */
@Service
public class WechatAppIdInfoServiceImpl extends ServiceImpl<WechatAppIdInfoMapper, WechatAppIdInfo> implements WechatAppIdInfoService {


    @Override
    public WechatAppIdInfo getAppIdInfoByUserId(String userId, Integer type) {
        ValidatedUtil.filterNullParam(userId, type);

        LambdaQueryWrapper<WechatAppIdInfo> eq = new QueryWrapper<WechatAppIdInfo>().lambda()
                .eq(WechatAppIdInfo::getUserId, userId)
                .eq(WechatAppIdInfo::getType, type);

        return this.getOne(eq);
    }
}
