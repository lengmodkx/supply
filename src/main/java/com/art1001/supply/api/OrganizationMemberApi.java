package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.InvitationLink;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.organization.OrganizationMemberInfo;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.role.RoleUser;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.organization.InvitationLinkService;
import com.art1001.supply.service.organization.OrganizationMemberInfoService;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.role.RoleUserService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.crypto.ShortCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.misc.Hash;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 企业成员
 *
 * @author 汪亚锋
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/organization/members")
public class OrganizationMemberApi {

    @Resource
    private OrganizationMemberService organizationMemberService;

    @Resource
    private ProjectMemberService projectMemberService;

    @Resource
    private UserService userService;

    @Resource
    private RoleUserService roleUserService;

    @Resource
    private RoleService roleService;

    @Resource
    private InvitationLinkService invitationLinkService;

    /**
     * 未分配部门的员工
     */
    private static final int NOT_PARTMENT = 1;

    /**
     * 账号停用的员工
     */
    private static final int MEMBER_LOCK = 2;

    /**
     * 新加入的成员
     */
    private static final int MEMBER_NEW = 3;


    private static final int IS_PARTMENT = 4;

    /**
     * 外部成员
     */
    private static final int IS_OTHER = 5;


    /**
     * 给企业添加员工/给成员添加部门
     *
     * @param orgId     企业id
     * @param parmentId 部门id
     * @param memberId  成员id
     * @return
     */
    @PostMapping
    public JSONObject addMember(@RequestParam(value = "orgId", required = false) String orgId,
                                @RequestParam(value = "parmentId", required = false) String parmentId,
                                @RequestParam(value = "memberId") String memberId) {
        JSONObject jsonObject = new JSONObject();
        try {
            OrganizationMember member = organizationMemberService.findOrgByMemberId(memberId, orgId);
            OrganizationMember organizationMember = new OrganizationMember();
            organizationMember.setOrganizationId(orgId);
            organizationMember.setPartmentId(parmentId);
            organizationMember.setMemberId(memberId);
            organizationMember.setOther(1);
            if (member == null) {
                //新增成员的部门id统一为0
                organizationMember.setPartmentId("0");
                organizationMember.setMemberLock(1);
                organizationMemberService.saveOrganizationMember(organizationMember);
                jsonObject.put("result", 1);
                jsonObject.put("msg", "添加成功");
                jsonObject.put("data", organizationMemberService.findOrgByMemberId(memberId, orgId));
            } else {
               /* if(member.getMemberLock()==1){
                    organizationMember.setId(member.getId());
                    organizationMemberService.updateOrganizationMember(organizationMember);
                    jsonObject.put("result",1);
                    jsonObject.put("msg","添加成功");
                }else{*/
                jsonObject.put("result", 0);
                jsonObject.put("msg", "邀请失败，该成员已在企业中");
                //}
            }
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 企业成员
     *
     * @param pageNo   分页
     * @param pageSize 分页
     * @param flag     1是未分配部门的员工 2是账号停用的员工
     * @param orgId    企业id
     * @return
     */
    @GetMapping("/{orgId}")
    public JSONObject members(@PathVariable(value = "orgId") String orgId,
                              @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                              @RequestParam(value = "pageSize", defaultValue = "999") Integer pageSize,
                              @RequestParam(value = "flag", required = false, defaultValue = "0") Integer flag,
                              @RequestParam(value = "parmentId", required = false, defaultValue = "0") String parmentId) {
        JSONObject jsonObject = new JSONObject();

        try {
            Pager pager = new Pager();
            pager.setPageNo(pageNo);
            pager.setPageSize(pageSize);
            OrganizationMember organizationMember = new OrganizationMember();
            organizationMember.setOrganizationId(orgId);
            organizationMember.setMemberLock(1);
            organizationMember.setId(orgId);
            if (flag == NOT_PARTMENT) {
                organizationMember.setPartmentId("0");
            } else if (flag == MEMBER_LOCK) {
                organizationMember.setMemberLock(0);
            } else if (flag == MEMBER_NEW) {
                organizationMember.setCreateTime(System.currentTimeMillis());
            } else if (flag == IS_PARTMENT) {
                organizationMember.setPartmentId(parmentId);
            } else if (flag == IS_OTHER) {
                organizationMember.setOther(0);
            }
            pager.setCondition(organizationMember);
            List<OrganizationMember> memberList = organizationMemberService.findOrganizationMemberPagerList(pager);
            jsonObject.put("result", 1);
            jsonObject.put("data", memberList);
        } catch (Exception e) {
            log.error("系统异常:", e);
            throw new SystemException(e);
        }
        return jsonObject;
    }

    /**
     * 根据用户手机号,获取用户信息
     *
     * @param phone 手机号
     * @return 用户信息
     */
    @GetMapping("/{phone}/user")
    public JSONObject getUserByPhone(@PathVariable String phone, String orgId) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<UserEntity> users = userService.list(new QueryWrapper<UserEntity>().like("account_name", phone).select("user_id", "user_name", "image", "telephone"));
            if (users.isEmpty()) {
                users = userService.list(new QueryWrapper<UserEntity>().like("user_name", phone).select("user_id", "user_name", "image", "telephone"));
            }
            if (users.isEmpty()) {
                jsonObject.put("data", null);
                jsonObject.put("msg", "未搜索到成员");
                jsonObject.put("result", 0);
            } else {
                //判断该成员是否已在企业里
                for (UserEntity userEntity : users) {
                    OrganizationMember member = organizationMemberService.findOrgByMemberId(userEntity.getUserId(), orgId);
                    if (member != null) {
                        userEntity.setExistId(1);
                    } else {
                        userEntity.setExistId(0);
                    }
                }
                jsonObject.put("data", users);
                jsonObject.put("result", 1);
            }
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SystemException("系统异常,获取用户信息失败!", e);
        }
    }

    //搜索企业成员
    @GetMapping("/{phone}/searchOrgUser")
    public JSONObject searchMembers(@PathVariable String phone, String orgId) {
        JSONObject jsonObject = new JSONObject();
        try {


            List<UserEntity> users = userService.getUserByOrgId(phone, orgId);

            if (!users.isEmpty()) {
                List<OrganizationMember> memberList = new ArrayList<>();
                for (UserEntity u : users) {
                    OrganizationMember organizationMember = new OrganizationMember();
                    organizationMember.setUserEntity(u);
                    memberList.add(organizationMember);
                }
                jsonObject.put("data", memberList);
                jsonObject.put("msg", "搜索成功");
                jsonObject.put("result", 1);
            } else {
                jsonObject.put("msg", "搜索失败");
                jsonObject.put("result", 0);
            }


            return jsonObject;

        } catch (Exception e) {
            e.printStackTrace();
            throw new SystemException("系统异常,获取用户信息失败!", e);
        }
    }


    /**
     * @Param: orgId企业id userId用户id
     * @return:
     * @Description: 移除企业成员
     */
    @DeleteMapping
    public Result removeOrgUser(@RequestParam(value = "orgId", required = false) String orgId,
                                @RequestParam(value = "userId") String userId) {
        organizationMemberService.remove(new QueryWrapper<OrganizationMember>().eq("organization_id", orgId).eq("member_id", userId));
        roleUserService.remove(new QueryWrapper<RoleUser>().eq("org_id", orgId).eq("u_id", userId));

        return Result.success();
    }

    /**
     * 判断当前用户是否能够看到成员选项
     *
     * @param orgId 企业id
     * @return
     */
    @GetMapping("/check/member_visible")
    public Result checkVisibleMember(String orgId) {
        LambdaQueryWrapper<RoleUser> eq = new QueryWrapper<RoleUser>().lambda().eq(RoleUser::getOrgId, orgId).eq(RoleUser::getUId, ShiroAuthenticationManager.getUserId());
        RoleUser one = roleUserService.getOne(eq);
        Integer roleId = one.getRoleId();
        Role byId = roleService.getById(roleId);
        Boolean result;
        if (Constants.OWNER_KEY.equals(byId.getRoleKey()) || Constants.ADMIN_KEY.equals(byId.getRoleKey())) {
            result = Boolean.TRUE;
        } else {
            result = Boolean.FALSE;
        }

        return Result.success(result);
    }

    /**
     * 获取企业所有成员
     *
     * @param orgId 企业id
     * @returnp
     */
    @GetMapping("/getMembers/{orgId}")
    public JSONObject getMemberCompanies(@PathVariable String orgId) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<UserEntity> memberList = organizationMemberService.getUserList(orgId);
            if (memberList.isEmpty()) {
                jsonObject.put("result", 0);
                jsonObject.put("data", null);
                jsonObject.put("msg", "还未添加员工");
            } else {
                jsonObject.put("result", 1);
                jsonObject.put("data", memberList);
            }
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SystemException("系统异常,获取用户信息失败!", e);
        }
    }


    /**
     * 移交企业权限
     *
     * @param orgId   企业id
     * @param ownerId 企业拥有者id
     * @param userId  成员id
     * @return
     */
    @PostMapping("/transferOwner")
    public Result transferPower(@RequestParam(value = "orgId") String orgId,
                                @RequestParam(value = "ownerId") String ownerId,
                                @RequestParam(value = "userId") String userId) {
        JSONObject jsonObject = new JSONObject();
        try {
            Boolean update = organizationMemberService.transferOwner(orgId, ownerId, userId);
            if (update) {
                return Result.success();
            } else {
                return Result.fail("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SystemException("系统异常,获取用户信息失败!", e);
        }

    }


    @PostMapping("/{orgId}")
    public Result changeOrg(@PathVariable(value = "orgId") String orgId) {
        organizationMemberService.updateUserDefaultOrg(orgId, ShiroAuthenticationManager.getUserId());
        return Result.success();
    }

    /**
     * 根据关键字 模糊 获取企业中的用户列表
     *
     * @param orgId     企业id
     * @param projectId 项目id
     * @param keyword   关键字
     * @return 用户列表
     */
    @RequestMapping("/keyword")
    public Result getOrgMemberByKeyword(@NotNull(message = "企业id不能为空!") String orgId, String keyword, @NotNull(message = "项目id不能为空!") String projectId) {
        log.info("Get organization member list by keyword. [{},{}]", orgId, keyword);
        List<UserEntity> orgMemberByKeyword = organizationMemberService.getOrgMemberByKeyword(orgId, keyword);
        //判断该成员是否已在企业里
        for (UserEntity userEntity : orgMemberByKeyword) {
            int member = projectMemberService.findMemberIsExist(projectId, userEntity.getUserId());
            if (member != 0) {
                userEntity.setExistId(1);
            } else {
                userEntity.setExistId(0);
            }
        }
        return Result.success(orgMemberByKeyword);
    }



    //停用/启用企业成员
    @PutMapping("/{orgId}/lock")
    public Result lockUser(@PathVariable String orgId,
                           @RequestParam String userId,
                           @RequestParam Integer lock) {
        OrganizationMember member = new OrganizationMember();
        member.setMemberLock(lock);
        organizationMemberService.update(member, new UpdateWrapper<OrganizationMember>().eq("organization_id", orgId).eq("member_id", userId));
        return Result.success();
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param:
     * @return:
     * @Description: 生成链接邀请成员
     * @create: 15:14 2020/5/18
     */
    @GetMapping("/getOrganizationMemberByUrl")
    public JSONObject getOrganizationMemberByUrl(String orgId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", invitationLinkService.getOrganizationMemberByUrl(orgId));
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @param keyword   关键字
     * @return 用户列表
     * @Description: 根据电话号模糊搜索用户判断是否在项目
     * @create: 15:41 2020/5/26
     */
    @GetMapping("/getProjectMemberByKeyword")
    public JSONObject getProjectMemberByKeyword(String keyword,@NotNull(message = "项目id不能为空!")String projectId) {
        JSONObject jsonObject = new JSONObject();

        try {
            List<UserEntity> list = userService.list(new QueryWrapper<UserEntity>().lambda().like(UserEntity::getAccountName, keyword));
            Optional.ofNullable(list).ifPresent(s->s.stream().forEach(r->{
                if(projectMemberService.findMemberIsExist(projectId, r.getUserId())!=0){
                    r.setExistId(1);
                }else {
                    r.setExistId(0);
                }
            }));
            jsonObject.put("result",1);
            jsonObject.put("data",list);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常，请稍后再试");
        }
    }



}
