package com.art1001.supply.api;


import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.partment.Partment;
import com.art1001.supply.entity.partment.PartmentMember;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.partment.PartmentMemberService;
import com.art1001.supply.service.partment.PartmentService;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.user.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.rmi.ServerException;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author shaohua
 * @since 2019-04-29
 */
@RestController
@RequestMapping("/partment_members/")
public class PartmentMemberApi {

    public static final Integer MANAGER = 3;
    public static final Integer MEMBER = 1;

    /**
     * 注入部门成员逻辑bean
     */
    @Resource
    private PartmentMemberService partmentMemberService;

    @Resource
    private PartmentService partmentService;

    @Resource
    private OrganizationMemberService organizationMemberService;

    @Resource
    private UserService userService;
    /**
     * 添加部门成员
     * @param memberId 成员id
     * @return 添加结果
     */
    @PostMapping("/{partmentId}/add")
    public JSONObject addPartmentMember(@PathVariable String partmentId,
                                        @RequestParam String orgId,
                                        @RequestParam List<String> memberId){
        JSONObject jsonObject = new JSONObject();
        try {
             partmentMemberService.addDeptMember(partmentId, orgId, memberId);
            Partment partment = partmentService.getById(partmentId);
            jsonObject.put("data",partment);

            jsonObject.put("result", 1);
            return jsonObject;
        } catch (ServiceException e){
            throw new AjaxException(e.getMessage(),e);
        } catch (Exception e){
            throw new AjaxException("系统异常，成员添加失败!",e);
        }
    }

    /**
     * 更改部门成员的身份
     * @param partmentId 部门id
     * @param memberId 成员id
     * @return 结果
     */
    @PutMapping("/{partmentId}/identity")
    public JSONObject updateMemberIdentity(@PathVariable String partmentId,@RequestParam String memberId,@RequestParam Integer memberLabel){
        JSONObject jsonObject = new JSONObject();
        if(memberLabel != null && (memberLabel < MEMBER || memberLabel > MANAGER)){
            jsonObject.put("result", 0);
            jsonObject.put("msg", "身份标签错误!");
        }
        try {
            PartmentMember partmentMember = new PartmentMember();
            partmentMember.setMemberLabel(memberLabel);
            if(partmentMemberService.update(partmentMember, new QueryWrapper<PartmentMember>().eq("member_id", memberId).eq("partment_id", partmentId))){
                jsonObject.put("result", 1);
            }
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,身份更新失败!",e);
        }
    }

    /**
     * 移除部门成员
     * @param partmentId 部门id
     * @param memberId 成员id
     * @return 结果
     */
    @DeleteMapping("/{partmentId}/member")
    public JSONObject removePartmentMember(@PathVariable String partmentId,
                                           @RequestParam String orgId,
                                           @RequestParam List<String> memberId){
        JSONObject jsonObject = new JSONObject();
        try {
            OrganizationMember member = new OrganizationMember();
            member.setPartmentId("0");
            organizationMemberService.update(member,new UpdateWrapper<OrganizationMember>().eq("organization_id",orgId).in("member_id",memberId));
            partmentMemberService.remove(new QueryWrapper<PartmentMember>().eq("partment_id", partmentId).in("member_id", memberId));
            Partment partment = partmentService.getById(partmentId);
            jsonObject.put("result", 1);
            jsonObject.put("data",partment);
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,移除失败!",e);
        }
    }

    /**
     * 将某个成员设为负责人
     * @param partmentId 部门id
     * @param  memberId 成员id
     * @param isMaster 要设置的值 (true 设为负责人   false 取消负责人)
     * @return 结果
     */
    @PutMapping("/{partmentId}/set_master")
    public JSONObject master(@PathVariable String partmentId, @RequestParam String memberId, @RequestParam Boolean isMaster){
        JSONObject jsonObject = new JSONObject();
        try {
            PartmentMember partmentMember = new PartmentMember();
            partmentMember.setIsMaster(isMaster);
            if(partmentMemberService.update(partmentMember,new QueryWrapper<PartmentMember>().eq("partment_id", partmentId).eq("member_id", memberId))){
                jsonObject.put("result", 1);
            }
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,设置失败!",e);
        }
    }

    /**
     * 获取一个部门的全部成员信息
     * @return 成员信息
     */
    @GetMapping("/{partmentId}/members")
    public JSONObject getPartmentMemberInfo(@PathVariable String partmentId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",partmentMemberService.getMemberByPartmentId(partmentId));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e){
            throw new SystemException("系统异常,成员信息获取失败!",e);
        }
    }
}

