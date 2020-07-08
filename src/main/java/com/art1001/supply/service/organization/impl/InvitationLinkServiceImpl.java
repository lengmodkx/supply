package com.art1001.supply.service.organization.impl;

import com.art1001.supply.entity.organization.InvitationLink;
import com.art1001.supply.entity.organization.InvitationLinkVO;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserVO;
import com.art1001.supply.mapper.organization.InvitationLinkMapper;
import com.art1001.supply.service.organization.InvitationLinkService;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.RedisUtil;
import com.art1001.supply.util.crypto.ShortCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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

    @Resource
    private UserService userService;

    @Resource
    private OrganizationService organizationService;

    @Resource
    private ProjectService projectService;

    @Resource
    private ProjectMemberService projectMemberService;

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

    /**
     * 获取
     * @param memberId
     * @param orgId
     * @param projectId
     * @return
     */
    @Override
    public UserVO getInviteMemberInfo(String memberId, String orgId, String projectId) {
        UserVO userVO = new UserVO();
        UserEntity byId = userService.findById(memberId);
        userVO.setUserId(memberId);
        userVO.setUserName(byId.getUserName());
        userVO.setImage(byId.getImage());
        userVO.setJob(byId.getJob());
        userVO.setPhone(byId.getAccountName());
        if (StringUtils.isNotEmpty(orgId)) {
            Organization organization = organizationService.getById(orgId);
            userVO.setOrganizationId(orgId);
            userVO.setOrganizationName(organization.getOrganizationName());
        }
        if (StringUtils.isNotEmpty(projectId)) {
            Project project = projectService.getById(projectId);
            userVO.setProjectId(projectId);
            userVO.setProjectName(project.getProjectName());
            String defaultGroup = projectMemberService.findDefaultGroup(projectId, memberId);
            userVO.setGroupId(defaultGroup);
        }
        return userVO;

    }
}
