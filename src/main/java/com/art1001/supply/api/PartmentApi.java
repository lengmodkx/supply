package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.partment.Partment;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.partment.PartmentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("partments")
public class PartmentApi {

    @Resource
    private PartmentService partmentService;

    /**
     * 添加部门/添加子部门
     * @param orgId 企业id
     * @param partmentName 部门名称
     * @param partmentLogo 部门logo
     * @return 结果
     */
    @PostMapping("/{orgId}")
    public JSONObject addPartment(@PathVariable(value = "orgId") String orgId,
                                  @RequestParam(value = "partmentName") String partmentName,
                                  @RequestParam(value = "parentId",required = false) String parentId,
                                  @RequestParam(value = "partmentLogo",required = false) String partmentLogo){
        JSONObject jsonObject = new JSONObject();
        try {
            if(parentId != null){
                if(!partmentService.checkPartmentIsExist(parentId)){
                    jsonObject.put("result", 0);
                    jsonObject.put("msg", "父级部门不存在!");
                    return jsonObject;
                }
            }
            Partment partment = new Partment();
            partment.setOrganizationId(orgId);
            partment.setParentId(parentId);
            partment.setPartmentName(partmentName);
            partment.setPartmentLogo(partmentLogo);
            partmentService.savePartment(partment);
            jsonObject.put("data", partment);
            jsonObject.put("result",1);
        }catch (Exception e){
            log.error("系统异常,部门创建失败:",e);
            throw new AjaxException("系统异常,部门创建失败!",e);
        }
        return jsonObject;
    }

    /**
     * 删除部门
     * @param partmentId 部门id
     * @return 结果
     */
    @DeleteMapping("/{partmentId}")
    public JSONObject deleteParment(@PathVariable(value = "partmentId") String partmentId){
        JSONObject jsonObject = new JSONObject();
        try {
            partmentService.deletePartmentByPartmentId(partmentId);
            jsonObject.put("result",1);
        }catch (Exception e){
            log.error("系统异常,部门删除失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更新部门
     * @param partmentId 部门id
     * @param partmentName 部门名称
     * @param partmentLogo 部门logo
     * @return 结果
     */
    @PutMapping("/{partmentId}")
    public JSONObject updatePartment(@PathVariable(value = "partmentId") String partmentId,
                                     @RequestParam(value = "partmentName",required = false) String partmentName,
                                     @RequestParam(value = "partmentLogo",required = false) String partmentLogo){
        JSONObject jsonObject = new JSONObject();
        try {
            Partment partment = new Partment();
            partment.setPartmentId(partmentId);
            partment.setPartmentName(partmentName);
            partment.setPartmentLogo(partmentLogo);
            partment.setUpdateTime(System.currentTimeMillis());
            partmentService.updatePartment(partment);
            jsonObject.put("result",1);
        }catch (Exception e){
            log.error("系统异常,部门信息更新失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 获取企业下部门
     * @param orgId 企业id
     * @return 结果
     */
    @GetMapping("/{orgId}")
    public JSONObject allParment(@PathVariable String orgId){
        JSONObject jsonObject = new JSONObject();
        try {
            Partment partment = new Partment();
            partment.setOrganizationId(orgId);
            partment.setPartmentOrder(1);
            jsonObject.put("result",1);
            jsonObject.put("data",partmentService.findPartmentAllList(partment));
        }catch (Exception e){
            log.error("系统异常,信息获取失败:",e);
            throw new SystemException(e);
        }
        return jsonObject;
    }

    /**
     * 排序分组的顺序
     * @param partmentIds 排序后的部门id顺序
     * @return 结果
     */
    @PutMapping("/order")
    public JSONObject updateOrder(String[] partmentIds){
        JSONObject jsonObject = new JSONObject();
        try {
            if(partmentService.orderPartment(partmentIds)){
                jsonObject.put("result",1);
            }
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,排序失败!");
        }
    }

    /**
     * 获取子部门信息
     * @param partmentId 父级部门id
     * @return 子部门信息
     */
    @GetMapping("/{partmentId}/sub")
    public JSONObject getSubPartment(@PathVariable String partmentId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", partmentService.list(new QueryWrapper<Partment>().eq("parent_id", partmentId)));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e){
            throw new SystemException("系统异常,子部门信息获取失败!");
        }
    }
}
