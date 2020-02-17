package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.role.RoleUser;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.role.RoleUserService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.CommonUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 企业成员
 * @author 汪亚锋
 */
@Slf4j
@RestController
@RequestMapping("/organization/members")
public class OrganizationMemberApi {

    @Resource
    private OrganizationMemberService organizationMemberService;

    @Resource
    private UserService userService;

    @Resource
    private RoleUserService roleUserService;

    @Resource
    private RoleService roleService;
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
    private static final int MEMBER_NEW =3;

    private static final int IS_PARTMENT =4;



    /**
     * 给企业添加员工/给成员添加部门
     * @param orgId 企业id
     * @param parmentId 部门id
     * @param memberId 成员id
     * @return
     */
    //@RequiresPermissions("create:member")
    @PostMapping
    public JSONObject addMember(@RequestParam(value = "orgId",required = false) String orgId,
                                @RequestParam(value = "parmentId",required = false) String parmentId,
                                @RequestParam(value = "memberId") String memberId){
        JSONObject jsonObject = new JSONObject();
        try {
            OrganizationMember member = organizationMemberService.findOrgByMemberId(memberId,orgId);
            OrganizationMember organizationMember = new OrganizationMember();
            organizationMember.setOrganizationId(orgId);
            organizationMember.setPartmentId(parmentId);
            organizationMember.setMemberId(memberId);
            organizationMember.setOther(1);
            if(member==null){
                //新增成员的部门id统一为0
                organizationMember.setPartmentId("0");
                organizationMember.setMemberLock(1);
                organizationMemberService.saveOrganizationMember(organizationMember);
                jsonObject.put("result",1);
                jsonObject.put("msg","添加成功");
                jsonObject.put("data", organizationMemberService.findOrgByMemberId(memberId,orgId));
            } else{
                if(member.getMemberLock()==1){
                    organizationMember.setId(member.getId());
                    organizationMemberService.updateOrganizationMember(organizationMember);
                    jsonObject.put("result",1);
                    jsonObject.put("msg","添加成功");
                }else{
                    jsonObject.put("result",0);
                    jsonObject.put("msg","邀请失败，该成员已被停用");
                }
            }
        }catch (Exception e){
            throw  new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 企业成员
     * @param pageNo 分页
     * @param pageSize 分页
     * @param flag 1是未分配部门的员工 2是账号停用的员工
     * @param orgId 企业id
     * @return
     */
    @GetMapping("/{orgId}")
    public JSONObject members(@PathVariable(value = "orgId") String orgId,
                              @RequestParam(value = "pageNo",defaultValue = "1") Integer pageNo,
                              @RequestParam(value = "pageSize", defaultValue = "999") Integer pageSize,
                              @RequestParam(value = "flag",required = false,defaultValue = "0") Integer flag,
                              @RequestParam(value = "parmentId",required = false,defaultValue = "0")String parmentId){
        JSONObject jsonObject = new JSONObject();

        try {
            Pager pager = new Pager();
            pager.setPageNo(pageNo);
            pager.setPageSize(pageSize);
            OrganizationMember organizationMember = new OrganizationMember();
            organizationMember.setOrganizationId(orgId);
            organizationMember.setMemberLock(1);
            organizationMember.setId(orgId);
            if(flag==NOT_PARTMENT){
                organizationMember.setPartmentId("0");
            }else if(flag==MEMBER_LOCK){
                organizationMember.setMemberLock(0);
            }else if (flag==MEMBER_NEW){
                organizationMember.setCreateTime(System.currentTimeMillis());
            }else if (flag == IS_PARTMENT){
                organizationMember.setPartmentId(parmentId);
            }

            pager.setCondition(organizationMember);
            List<OrganizationMember> memberList = organizationMemberService.findOrganizationMemberPagerList(pager);
            jsonObject.put("result",1);
            jsonObject.put("data",memberList);
        }catch (Exception e){
            log.error("系统异常:",e);
            throw new SystemException(e);
        }
        return jsonObject;
    }

    /**
     * 根据用户手机号,获取用户信息
     * @param phone 手机号
     * @return 用户信息
     */
    @GetMapping("/{phone}/user")
    public JSONObject getUserByPhone(@PathVariable String phone){
        JSONObject jsonObject = new JSONObject();
        try {
            List<UserEntity> users = userService.list(new QueryWrapper<UserEntity>().like("account_name", phone).select("user_id", "user_name", "image", "telephone"));
            jsonObject.put("data",users);
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e){
            e.printStackTrace();
            throw new SystemException("系统异常,获取用户信息失败!",e);
        }
    }
    @DeleteMapping
    public Result removeOrgUser(@RequestParam(value = "orgId",required = false) String orgId,
                                @RequestParam(value = "userId") String userId){
        organizationMemberService.remove(new QueryWrapper<OrganizationMember>().eq("organization_id",orgId).eq("member_id",userId));
        roleUserService.remove(new QueryWrapper<RoleUser>().eq("org_id",orgId).eq("u_id",userId));
        return Result.success();
    }

    /**
     * 判断当前用户是否能够看到成员选项
     * @param orgId 企业id
     * @return
     */
    @GetMapping("/check/member_visible")
    public Result checkVisibleMember(String orgId){
        LambdaQueryWrapper<RoleUser> eq = new QueryWrapper<RoleUser>().lambda().eq(RoleUser::getOrgId, orgId).eq(RoleUser::getUId, ShiroAuthenticationManager.getUserId());
        RoleUser one = roleUserService.getOne(eq);
        Integer roleId = one.getRoleId();
        Role byId = roleService.getById(roleId);
        Boolean result;
        if(Constants.OWNER_KEY.equals(byId.getRoleKey()) || Constants.ADMIN_KEY.equals(byId.getRoleKey())){
            result = Boolean.TRUE;
        } else {
            result = Boolean.FALSE;
        }

        return Result.success(result);
    }
}
