package com.art1001.supply.service.chat.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.chat.HxChatNotice;
import com.art1001.supply.entity.organization.OrganizationGroupMember;
import com.art1001.supply.mapper.chat.HxChatNoticeMapper;
import com.art1001.supply.service.chat.HxChatNoticeService;
import com.art1001.supply.service.organization.OrganizationGroupMemberService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName HxChatNoticeServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/8/18 14:28
 * @Discription 环信聊天通知表
 */
@Service
public class HxChatNoticeServiceImpl extends ServiceImpl<HxChatNoticeMapper, HxChatNotice> implements HxChatNoticeService {

    @Resource
    private HxChatNoticeMapper hxChatNoticeMapper;

    @Resource
    private OrganizationGroupMemberService organizationGroupMemberService;

    @Override
    public Integer saveChatCount(String memberId, Integer contentFrom, String hxGroupId, String groupId) {
        if (contentFrom.equals(Constants.B_ONE)) {
            HxChatNotice hxChatNotice = hxChatNoticeMapper.selectOne(new QueryWrapper<HxChatNotice>().eq("news_to_user", memberId)
                    .eq("news_from_user", ShiroAuthenticationManager.getUserId()));
            if (hxChatNotice==null) {
                HxChatNotice hxChatNotice1 = HxChatNotice.builder()
                        .newsFromUser(ShiroAuthenticationManager.getUserId())
                        .newsToUser(memberId).newsContent("您有一条新消息请查收").newsAddress(0)
                        .newsCount(1).newsHandle(0).createTime(System.currentTimeMillis())
                        .updateTime(System.currentTimeMillis()).build();
                hxChatNoticeMapper.insert(hxChatNotice1);
            }else{
                hxChatNotice.setNewsCount(hxChatNotice.getNewsCount()+1);
                hxChatNoticeMapper.updateById(hxChatNotice);
            }
        }
        if (contentFrom.equals(Constants.B_ONE)) {
            String newsToUser=null;
            List<OrganizationGroupMember> groupMembers = organizationGroupMemberService.getGroupMembers(groupId);
            if (CollectionUtils.isNotEmpty(groupMembers)) {
                newsToUser= StringUtils.join(groupMembers.stream().map(OrganizationGroupMember::getMemberId).collect(Collectors.toList()),",");
            }
            HxChatNotice hxChatNotice = hxChatNoticeMapper.selectOne(new QueryWrapper<HxChatNotice>().eq("hx_group_id", hxGroupId).eq("news_address",1));
            if (hxChatNotice==null) {
                HxChatNotice hxChatNotice1 = HxChatNotice.builder()
                        .newsToUser(newsToUser).newsContent("您有新消息请查收")
                        .newsCount(1).newsHandle(0).hxGroupId(hxGroupId).groupId(groupId)
                        .createTime(System.currentTimeMillis()).newsAddress(1)
                        .updateTime(System.currentTimeMillis()).build();
                hxChatNoticeMapper.insert(hxChatNotice1);
            }else {
                hxChatNotice.setNewsCount(hxChatNotice.getNewsCount()+1);
                hxChatNoticeMapper.updateById(hxChatNotice);
            }
        }
        return 1;
    }
}
