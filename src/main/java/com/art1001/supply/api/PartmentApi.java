package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.partment.Partment;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.partment.PartmentService;
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
     * 添加部门
     * @param orgId 企业id
     * @param partmentName 部门名称
     * @param partmentLogo 部门logo
     * @return
     */
    @PostMapping("/{orgId}")
    public JSONObject addPartment(@PathVariable(value = "orgId") String orgId,
                                  @RequestParam(value = "partmentName") String partmentName,
                                  @RequestParam(value = "partmentLogo") String partmentLogo){
        JSONObject jsonObject = new JSONObject();
        try {
            Partment partment = new Partment();
            partment.setOrganizationId(orgId);
            partment.setPartmentName(partmentName);
            partment.setPartmentLogo(partmentLogo);
            partmentService.savePartment(partment);
            jsonObject.put("result",1);
        }catch (Exception e){
            log.error("系统异常,部门创建失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 删除部门
     * @param partmentId 部门id
     * @return
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
     * @param orgId 企业id
     * @param partmentName 部门名称
     * @param partmentLogo 部门logo
     * @return
     */
    @PutMapping("/{orgId}")
    public JSONObject updatePartment(@PathVariable(value = "orgId") String orgId,
                                     @RequestParam(value = "partmentName",required = false) String partmentName,
                                     @RequestParam(value = "partmentLogo",required = false) String partmentLogo){
        JSONObject jsonObject = new JSONObject();
        try {
            Partment partment = new Partment();
            partment.setOrganizationId(orgId);
            partment.setPartmentName(partmentName);
            partment.setPartmentLogo(partmentLogo);
            partment.setUpdateTime(System.currentTimeMillis());
            partment.setCreateTime(System.currentTimeMillis());
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
     * @return
     */
    @GetMapping("/{orgId}")
    public JSONObject allParment(@PathVariable String orgId){
        JSONObject jsonObject = new JSONObject();
        try {
            Partment partment = new Partment();
            partment.setOrganizationId(orgId);
            partment.setOrderBy(1);
            jsonObject.put("result",1);
            jsonObject.put("data",partmentService.findPartmentAllList(partment));
        }catch (Exception e){
            log.error("系统异常,信息获取失败:",e);
            throw new SystemException(e);
        }
        return jsonObject;
    }
}
