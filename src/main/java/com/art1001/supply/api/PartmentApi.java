package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.partment.Partment;
import com.art1001.supply.entity.partment.PartmentMember;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.partment.PartmentMemberService;
import com.art1001.supply.service.partment.PartmentService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("partments")
public class PartmentApi {

    @Resource
    private PartmentService partmentService;

    @Resource
    private PartmentMemberService partmentMemberService;

    /**
     * 添加部门/添加子部门
     *
     * @param orgId        企业id
     * @param partmentName 部门名称
     * @param partmentLogo 部门logo
     * @return 结果
     */
    @Push(value = PushType.P1, type = 1)
    @PostMapping("/{orgId}")
    public JSONObject addPartment(@PathVariable(value = "orgId") String orgId,
                                  @RequestParam(value = "partmentName") String partmentName,
                                  @RequestParam(value = "parentId", required = false) String parentId,
                                  @RequestParam(value = "partmentLogo", required = false) String partmentLogo,
                                  @RequestParam(value = "memberId",required = false)String memberId) {
        JSONObject jsonObject = new JSONObject();
        try {
            Partment partment = new Partment();
            if (StringUtils.isNotEmpty(parentId)) {
                if (!partmentService.checkPartmentIsExist(parentId)) {
                    jsonObject.put("result", 0);
                    jsonObject.put("msg", "父级部门不存在!");
                    return jsonObject;
                }
                partment.setParentId(parentId);
            }
            partment.setOrganizationId(orgId);
            partment.setPartmentName(partmentName);
            partment.setPartmentLogo(partmentLogo);
            partment.setCreateTime(System.currentTimeMillis());
            partment.setUpdateTime(System.currentTimeMillis());
            partmentService.savePartment(partment);

            //保存部门成员
            partmentMemberService.savePartmentMember(partment.getPartmentId(),memberId);

            jsonObject.put("msg", partment);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("系统异常,部门创建失败:", e);
            throw new AjaxException("系统异常,部门创建失败!", e);
        }
        return jsonObject;
    }

    /**
     * 删除部门
     *
     * @param partmentId 部门id
     * @return 结果
     */
    @DeleteMapping("/{partmentId}")
    public JSONObject deleteParment(@PathVariable(value = "partmentId") String partmentId) {
        JSONObject jsonObject = new JSONObject();
        try {
            partmentService.deletePartmentByPartmentId(partmentId);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("系统异常,部门删除失败:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更新部门
     *
     * @param partmentId   部门id
     * @param partmentName 部门名称
     * @param partmentLogo 部门logo
     * @return 结果
     */
    @PutMapping("/{partmentId}")
    public JSONObject updatePartment(@PathVariable(value = "partmentId") String partmentId,
                                     @RequestParam(value = "partmentName", required = false) String partmentName,
                                     @RequestParam(value = "partmentLogo", required = false) String partmentLogo,
                                     @RequestParam(value = "memberId",required = false)String memberId) {
        JSONObject jsonObject = new JSONObject();
        try {
            Partment partment = new Partment();
            partment.setPartmentId(partmentId);
            partment.setPartmentName(partmentName);
            partment.setPartmentLogo(partmentLogo);
            partment.setUpdateTime(System.currentTimeMillis());
            partmentService.updatePartment(partment);
            if (StringUtils.isNotEmpty(memberId)) {
                partmentMemberService.updatePartMentMaster(partmentId,memberId);
            }
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("系统异常,部门信息更新失败:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 获取企业下部门
     *
     * @param orgId 企业id
     * @return 结果
     */
    @Push(value = PushType.P2, type = 1)
    @GetMapping("/{orgId}")
    public JSONObject allParment(@PathVariable String orgId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", partmentService.findOrgPartmentInfo(orgId));
        } catch (Exception e) {
            log.error("系统异常,信息获取失败:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 排序分组的顺序
     *
     * @param partmentIds 排序后的部门id顺序
     * @return 结果
     */
    @PutMapping("/order")
    public JSONObject updateOrder(String[] partmentIds) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (partmentService.orderPartment(partmentIds)) {
                jsonObject.put("result", 1);
            }
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,排序失败!");
        }
    }

    /**
     * 获取子部门信息
     *
     * @param partmentId 父级部门id
     * @return 子部门信息
     */
    @GetMapping("/{partmentId}/sub")
    public JSONObject getSubPartment(@PathVariable String partmentId) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<Partment> partments = partmentService.findSubPartment(partmentId);
            jsonObject.put("data", partments);
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            throw new SystemException("系统异常,子部门信息获取失败!");
        }
    }

    /**
     * 获取部门树
     *
     * @param orgId        企业id
     * @param departmentId 部门id
     * @return 结果
     */
    @PostMapping("/tree")
    public Result getTree(@RequestParam(required = false) String orgId, @RequestParam(required = false) String departmentId) {
        log.info("Get department tree. [{},{}]", orgId, departmentId);

        if (StringUtils.isNotEmpty(orgId) && StringUtils.isNotEmpty(departmentId)) {
            return Result.fail("只能传递一个参数");
        }

        if (StringUtils.isEmpty(orgId) && StringUtils.isEmpty(departmentId)) {
            return Result.fail("参数不能全部为空!");
        }

        return Result.success(partmentService.getTree(orgId, departmentId));
    }

    /**
    * @Author: 邓凯欣
    * @Email：dengkaixin@art1001.com
    * @Param: orgId 企业id
    * @return:
    * @Description: 获取企业下部门名称
    * @create: 15:08 2020/4/23
    */
    @GetMapping("/{orgId}/getDeptNameByOrgId")
    public JSONObject getDeptNameByOrgId(@PathVariable String orgId) {

        JSONObject jsonObject = new JSONObject();
        List<Partment> orgPartmentInfo = partmentService.findOrgParentByOrgId(orgId);
        try {
            if (!CollectionUtils.isEmpty(orgPartmentInfo)) {
                jsonObject.put("result", 1);
                jsonObject.put("data", orgPartmentInfo);
            }
        } catch (Exception e) {
           throw new AjaxException("系统异常，获取失败");
        }
        return jsonObject;
    }

    /**
     * 搜索部门
     * @param keyWord
     * @return
     */
    @GetMapping("/searchOrgByKeyWord/{orgId}")
    public JSONObject searchOrgByKeyWord(@PathVariable(value = "orgId")String orgId,
                                         @RequestParam(value = "keyWord") String keyWord){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", partmentService.searchDeptByKeyWord(keyWord,orgId));
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

}
