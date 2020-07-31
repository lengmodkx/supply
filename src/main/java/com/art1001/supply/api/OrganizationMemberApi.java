package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.InvitationLink;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.organization.OrganizationMemberInfo;
import com.art1001.supply.entity.partment.Partment;
import com.art1001.supply.entity.partment.PartmentMember;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.role.RoleUser;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.organization.InvitationLinkService;
import com.art1001.supply.service.organization.OrganizationMemberInfoService;
import com.art1001.supply.service.partment.PartmentMemberService;
import com.art1001.supply.service.partment.PartmentService;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.role.RoleUserService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.ExcelUtils;
import com.art1001.supply.util.crypto.ShortCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.netty.handler.codec.compression.FastLzFrameEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.misc.Hash;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Resource
    private PartmentService partmentService;

    @Resource
    private PartmentMemberService partmentMemberService;

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
     * 外部成员
     */
    private static final int EXTERNAL = 1;

    /**
     * 内部成员
     */
    private static final int INTERNAL = 0;

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
            organizationMember.setCreateTime(System.currentTimeMillis());
            organizationMember.setUpdateTime(System.currentTimeMillis());
            UserEntity byId = userService.findById(memberId);
            if (byId != null) {
                organizationMember.setUserName(byId.getUserName());
                organizationMember.setMemberEmail(byId.getEmail());
                if (byId.getBirthday() != null) {
                    organizationMember.setBirthday(byId.getBirthday() + "");
                }
                organizationMember.setPhone(byId.getAccountName());
                organizationMember.setJob(byId.getJob());
                organizationMember.setImage(byId.getImage());
                organizationMember.setAddress(byId.getAddress());
            }
            if (member == null) {
                //新增成员的部门id统一为0
                organizationMember.setPartmentId("0");
                organizationMember.setMemberLock(1);
                organizationMemberService.saveOrganizationMember(organizationMember);
                jsonObject.put("result", 1);
                jsonObject.put("msg", "添加成功");
                jsonObject.put("data", organizationMemberService.findOrgByMemberId(memberId, orgId));
            } else {
                jsonObject.put("result", 0);
                jsonObject.put("msg", "邀请失败，该成员已在企业中");
            }
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     *
     * @param orgId
     * @param parmentId
     * @param memberId
     * @param param 1外部成员 0成员
     * @return
     */
    @PostMapping("/addMember1")
    public JSONObject addMember1(@RequestParam(value = "orgId", required = false) String orgId,
                                 @RequestParam(value = "parmentId", required = false) String parmentId,
                                 @RequestParam(value = "memberId") String memberId) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (StringUtils.isNotEmpty(orgId)) {
                UserEntity byId = userService.findById(memberId);
                organizationMemberService.saveOrganizationMember2(orgId, byId);
                jsonObject.put("result", 1);
                jsonObject.put("msg", "添加成功");
            } else {
                jsonObject.put("result", 0);
                jsonObject.put("msg", "邀请失败，该成员已在企业中");
            }
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }

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
    public JSONObject searchMembers(@PathVariable String phone,
                                    @RequestParam(value = "orgId") String orgId,
                                    @RequestParam(value = "projectId", required = false) String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<UserEntity> users = userService.getUserByOrgId(phone, orgId);
            if (!users.isEmpty()) {
                List<OrganizationMember> memberList = new ArrayList<>();
                for (UserEntity u : users) {
                    OrganizationMember organizationMember = organizationMemberService.findOrgMembersByUserId(u.getUserId(), orgId);
                    if (StringUtils.isNotEmpty(projectId)) {
                        int memberIsExist = projectMemberService.findMemberIsExist(projectId, u.userId);
                        if (memberIsExist == 0) {
                            u.setExistId(0);
                        } else {
                            u.setExistId(1);
                        }
                    }
                    organizationMember.setUserEntity(u);
                    Partment partment = partmentService.findPartmentByPartmentId(organizationMember.getPartmentId());
                    if (partment != null) {
                        organizationMember.setDeptName(partment.getPartmentName());
                    }
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
                                @RequestParam(value = "userId") List<String> userId) {

        List<Partment> partments = partmentService.list(new QueryWrapper<Partment>().eq("organization_id", orgId));
        if (CollectionUtils.isNotEmpty(partments)) {
            List<String> collect = partments.stream().map(Partment::getPartmentId).collect(Collectors.toList());
            partmentMemberService.remove(new QueryWrapper<PartmentMember>().in("partment_id",collect).in("member_id",userId));
        }
        organizationMemberService.remove(new QueryWrapper<OrganizationMember>().eq("organization_id", orgId).in("member_id", userId));
        roleUserService.remove(new QueryWrapper<RoleUser>().eq("org_id", orgId).in("u_id", userId));


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
                jsonObject.put("msg", memberList.size());
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
                return Result.fail("移交失败，移交对象不能是外部成员");
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
     * @create: 15:14 2020/5/18  todo
     */
    @GetMapping("/getOrganizationMemberByUrl")
    public JSONObject getOrganizationMemberByUrl(@RequestParam(value = "orgId", required = false) String orgId,
                                                 @RequestParam(value = "projectId", required = false) String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", invitationLinkService.getOrganizationMemberByUrl(orgId, projectId));
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    /**
     * @param keyword 关键字
     * @return 用户列表
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Description: 根据关键字模糊搜索用户判断是否在项目
     * @create: 15:41 2020/5/26
     */
    @GetMapping("/getProjectMemberByKeyword")
    public JSONObject getProjectMemberByKeyword(String keyword, @NotNull(message = "项目id不能为空!") String projectId) {
        JSONObject jsonObject = new JSONObject();

        try {
            List<UserEntity> list = userService.list(new QueryWrapper<UserEntity>().lambda().like(UserEntity::getAccountName, keyword));
            Optional.ofNullable(list).ifPresent(s -> s.stream().forEach(r -> {
                if (projectMemberService.findMemberIsExist(projectId, r.getUserId()) != 0) {
                    r.setExistId(1);
                } else {
                    r.setExistId(0);
                }
            }));
            jsonObject.put("result", 1);
            jsonObject.put("data", list);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常，请稍后再试");
        }
    }

    /**
     * 成员和部门功能接口
     *
     * @param orgId
     * @param flag  1全部成员 0外部成员
     * @return
     */
    @GetMapping("/{orgId}/getOrgPartment")
    public JSONObject getOrgPartment(@PathVariable(value = "orgId") String orgId,
                                     @RequestParam(value = "flag", defaultValue = "0") Integer flag,
                                     @RequestParam(value = "memberLabel",required = false) Integer memberLabel) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", new JSONObject().fluentPut("members", organizationMemberService.getMembersAndPartment(orgId, flag,memberLabel)).fluentPut("partment", organizationMemberService.getOrgPartment(orgId)));
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 根据部门成员分类
     *
     * @param partmentId
     * @param memberLebel 成员身份 1:成员 2:拥有者 3:管理员
     * @param flag        是否是企业内员工 1全部成员 0外部成员
     * @return
     */
    @GetMapping("/getOrgPartmentByMemberLebel")
    public JSONObject getOrgPartmentByMemberLebel(@RequestParam(value = "partmentId", required = false) String partmentId,
                                                  @RequestParam(value = "memberLabel", required = false) String memberLebel,
                                                  @RequestParam(value = "flag", defaultValue = "1") String flag,
                                                  @RequestParam(value = "orgId") String orgId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", organizationMemberService.getOrgPartmentByMemberLebel(partmentId, memberLebel, flag, orgId));
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 检查被邀请人电话号是否注册过账号
     *
     * @param phone       电话号列联表
     * @param memberEmail 邮箱列表
     * @return
     */
    @GetMapping("/checkMemberIsRegister/{orgId}")
    public JSONObject checkMemberIsRegister(@PathVariable(value = "orgId") String orgId,
                                            @RequestParam(value = "phone", required = false) String phone,
                                            @RequestParam(value = "memberEmail", required = false) String memberEmail) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", userService.checkMemberIsRegister(phone, memberEmail, orgId));
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 根据电话号/邮箱邀请企业成员
     *
     * @param orgId
     * @param phone       电话号列联表
     * @param memberEmail 邮箱列表
     * @param  param  1外部成员 0成员
     * @return
     */
    @GetMapping("/inviteOrgMemberByPhone/{orgId}")
    public JSONObject inviteOrgMemberByPhone(@PathVariable(value = "orgId") String orgId,
                                             @RequestParam(value = "phone", required = false) List<String> phone,
                                             @RequestParam(value = "memberEmail", required = false) List<String> memberEmail,
                                             @RequestParam(value = "param")Integer param
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", organizationMemberService.inviteOrgMemberByPhone(orgId, phone, memberEmail,param));
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 导出企业成员信息
     *
     * @param response
     * @param orgId
     */
    @GetMapping("/expOrgMember")
    public void expOrgMember(HttpServletResponse response, @RequestParam(value = "orgId") String orgId) {

        List<OrganizationMember> memberList = organizationMemberService.expOrgMember(orgId);
        ExcelUtils.exportExcel(memberList, null, "企业成员", OrganizationMember.class, "企业成员信息表.xlsx", response);
    }

    /**
     * 导入企业成员
     * @param orgId
     * @param file
     * @return
     */
    @PostMapping("/impUser/{orgId}")
    public JSONObject impOrgUser(@PathVariable(value = "orgId") String orgId, MultipartFile file){
        JSONObject jsonObject = new JSONObject();
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
            LocalDateTime localDateTime=LocalDateTime.now();
            String format = df.format(localDateTime);
            Integer result=organizationMemberService.impOrgUser(orgId,file);
            jsonObject.put("result",1);
            jsonObject.put("data","于"+format+"上传成功"+result+"位成员");
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }


    }


}


