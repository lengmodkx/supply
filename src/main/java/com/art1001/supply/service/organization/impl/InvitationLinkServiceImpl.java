package com.art1001.supply.service.organization.impl;

import com.art1001.supply.entity.organization.InvitationLink;
import com.art1001.supply.entity.organization.InvitationLinkVO;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.organization.InvitationLinkMapper;
import com.art1001.supply.service.organization.InvitationLinkService;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.RedisUtil;
import com.art1001.supply.util.crypto.ShortCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.in;

/**
 * @ClassName InvitationLinkServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/5/19 11:29
 * @Discription 邀请链接实现类
 */
@Service
public class InvitationLinkServiceImpl extends ServiceImpl<InvitationLinkMapper, InvitationLink>implements InvitationLinkService {

    @Resource
    private InvitationLinkMapper invitationLinkMapper;

    @Resource
    private OrganizationMemberService organizationMemberService;

    @Resource
    private ShortCodeUtils shortCodeUtils;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public InvitationLinkVO getOrganizationMemberByUrl(String orgId) {
        List<UserEntity> userList = organizationMemberService.getUserList(orgId);

        String encode = shortCodeUtils.encode(orgId);
        long twoDayLater = LocalDateTime.now().withNano(0).plusHours(48).toInstant(ZoneOffset.of("+8")).toEpochMilli();

        InvitationLink inviteVO=InvitationLink.builder().organizationId(orgId)
                .shortUrl("http://aldbim.in/invite/"+encode)
                .completeUrl("http://www.aldbim.com/invite_from_link/"+orgId)
                .createTime(currentTimeMillis())
                .expireTime(twoDayLater).isExpire(0)
                .memberId(ShiroAuthenticationManager.getUserId())
                .updateTime(System.currentTimeMillis()).hash(encode).build();
        this.save(inviteVO);

        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(twoDayLater), ZoneId.systemDefault());
        DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return InvitationLinkVO.builder().memberNum(userList.size())
                .expireTime(dateTimeFormatter1.format(localDateTime))
                .shortUrl("http://aldbim.in/invite/"+encode).build();

    }

    @Override
    public InvitationLink getRedrectUrl(String hash) {
        String value = redisUtil.get("shortUrl:" + hash);
        InvitationLink invitationLink=new InvitationLink();
        if (StringUtils.isNotEmpty(value)) {
             invitationLink = invitationLinkMapper.selectOne(new QueryWrapper<InvitationLink>().eq("hash", redisUtil.get("shortUrl:" + value)));
        }
        return invitationLink;

    }
}
