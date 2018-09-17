package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.project.OrganizationMember;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.project.OrganizationMemberService;
import lombok.extern.slf4j.Slf4j;
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



    /**
     * 给企业添加员工/给成员添加部门
     * @param orgId 企业id
     * @param parmentId 部门id
     * @param memberId 成员id
     * @return
     */
    @PostMapping("")
    public JSONObject addMember(@RequestParam(value = "orgId",required = false) String orgId,
                                @RequestParam(value = "parmentId",required = false) String parmentId,
                                @RequestParam(value = "memberId") String memberId){
        JSONObject jsonObject = new JSONObject();
        try {
            OrganizationMember member = organizationMemberService.findOrgByMemberId(memberId);
            OrganizationMember organizationMember = new OrganizationMember();
            organizationMember.setOrganizationId(orgId);
            organizationMember.setPartmentId(parmentId);
            organizationMember.setMemberId(memberId);
            organizationMember.setOrganizationLable(1);
            if(member==null){
                //新增成员的部门id统一为0
                organizationMember.setPartmentId("0");
                organizationMemberService.saveOrganizationMember(organizationMember);
                jsonObject.put("result",1);
                jsonObject.put("msg","添加成功");
            }else{
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
                              @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                              @RequestParam(value = "flag",required = false,defaultValue = "1") Integer flag,
                              @RequestParam(value = "parmentId",required = false,defaultValue = "0")String parmentId){
        JSONObject jsonObject = new JSONObject();

        try {
            Pager pager = new Pager();
            pager.setPageNo(pageNo);
            pager.setPageSize(pageSize);
            OrganizationMember organizationMember = new OrganizationMember();
            organizationMember.setMemberLock(1);
            organizationMember.setId(orgId);
            if(flag==NOT_PARTMENT){
                organizationMember.setPartmentId("0");
            }else if(flag==MEMBER_LOCK){
                organizationMember.setMemberLock(0);
            }else if (flag==MEMBER_NEW){
                organizationMember.setCreateTime(System.currentTimeMillis());
            }else{
                organizationMember.setPartmentId(parmentId);
            }

            pager.setCondition(organizationMember);
            List<OrganizationMember> memberList = organizationMemberService.findOrganizationMemberPagerList(pager);
            jsonObject.put("result",1);
            jsonObject.put("msg","获取成功");
            jsonObject.put("data",memberList);

        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
    }
}
