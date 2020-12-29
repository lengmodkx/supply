package com.art1001.supply.service.chat.impl;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.chat.HxChatNotice;
import com.art1001.supply.entity.organization.OrganizationGroupMember;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.chat.HxChatNoticeMapper;
import com.art1001.supply.mapper.organization.OrganizationGroupMemberMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.chat.HxChatNoticeService;
import com.art1001.supply.service.organization.OrganizationGroupMemberService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
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
    private OrganizationGroupMemberService organizationGroupMemberService;

    @Resource
    private UserMapper userMapper;

    /**
     * 保存未接收环信消息消息数量
     * @param memberId
     * @param contentFrom
     * @param hxGroupId
     * @param groupId
     * @return
     */
    @Override
    public Integer saveChatCount(String memberId, Integer contentFrom, String hxGroupId, String groupId) {
        //私聊
        if (contentFrom.equals(Constants.B_ZERO)) {
            HxChatNotice hxChatNotice = getOne(new QueryWrapper<HxChatNotice>().eq("news_to_user", memberId)
                    .eq("news_from_user", ShiroAuthenticationManager.getUserId()));
            if (hxChatNotice==null) {
                HxChatNotice hxChatNotice1 = HxChatNotice.builder()
                        .newsFromUser(ShiroAuthenticationManager.getUserId())
                        .newsToUser(memberId).newsContent("您有新消息请查收").newsAddress(0)
                        .newsCount(1).newsHandle(0).createTime(System.currentTimeMillis())
                        .updateTime(System.currentTimeMillis()).build();
                save(hxChatNotice1);
            }else{
                hxChatNotice.setNewsCount(hxChatNotice.getNewsCount()+1);
                updateById(hxChatNotice);
            }
        }
        //群聊
        if (contentFrom.equals(Constants.B_ONE)) {
            //群聊人数
            String newsToUser=null;
            List<OrganizationGroupMember> groupMembers = organizationGroupMemberService.getGroupMembers(groupId);
            if (CollectionUtils.isNotEmpty(groupMembers)) {
                newsToUser= StringUtils.join(groupMembers.stream().map(OrganizationGroupMember::getMemberId).collect(Collectors.toList()),",");
            }
            HxChatNotice hxChatNotice = getOne(new QueryWrapper<HxChatNotice>().eq("hx_group_id", hxGroupId).eq("news_address",1));
            if (hxChatNotice==null) {
                HxChatNotice hxChatNotice1 = HxChatNotice.builder()
                        .newsToUser(newsToUser).newsContent("您有新消息请查收")
                        .newsCount(1).newsHandle(0).hxGroupId(hxGroupId).groupId(groupId)
                        .createTime(System.currentTimeMillis()).newsAddress(1)
                        .updateTime(System.currentTimeMillis()).build();
                save(hxChatNotice1);
            }else {
                hxChatNotice.setNewsCount(hxChatNotice.getNewsCount()+1);
                updateById(hxChatNotice);
            }
        }
        return 1;
    }

    @Override
    public JSONObject getChatCount() {
        JSONObject jsonObject = new JSONObject();

        Integer count = 0;
        //私聊
        List<HxChatNotice> hxChatNoticeList = list(new QueryWrapper<HxChatNotice>().eq("news_to_user", ShiroAuthenticationManager.getUserId()).eq("news_address",0).eq("news_handle",0));
        if (CollectionUtils.isNotEmpty(hxChatNoticeList)) {
            for (HxChatNotice hxChatNotice : hxChatNoticeList) {
                UserEntity byId = userMapper.selectById(hxChatNotice.getNewsFromUser());
                hxChatNotice.setNewsFromUserAccountName(byId.getAccountName());
                count+=hxChatNotice.getNewsCount();
            }
        }

        //群聊
        List<HxChatNotice> chatNoticeList= Lists.newArrayList();
        List<OrganizationGroupMember> organizationGroupMemberList = organizationGroupMemberService.list(new QueryWrapper<OrganizationGroupMember>().eq("member_id", ShiroAuthenticationManager.getUserId()));
        if (CollectionUtils.isNotEmpty(organizationGroupMemberList)) {
            List<String> groupIds = organizationGroupMemberList.stream().map(OrganizationGroupMember::getGroupId).collect(Collectors.toList());
            chatNoticeList = list(new QueryWrapper<HxChatNotice>().in("group_id", groupIds).eq("news_address",1).eq("news_handle",0));
            if (CollectionUtils.isNotEmpty(chatNoticeList)) {
                for (HxChatNotice hxChatNotice : chatNoticeList) {
                    count+=hxChatNotice.getNewsCount();
                }
            }
        }

        hxChatNoticeList.addAll(chatNoticeList);
        jsonObject.put("result",1);
        jsonObject.put("count",count);
        jsonObject.put("data",hxChatNoticeList);

        return jsonObject;
    }
}
