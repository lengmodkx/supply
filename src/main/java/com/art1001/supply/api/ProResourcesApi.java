package com.art1001.supply.api;


import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.entity.resource.ResourceShowVO;
import com.art1001.supply.service.resource.ProResourcesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 资源表 前端控制器
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
@RestController
@Slf4j
@RequestMapping("/pro_res")
public class ProResourcesApi extends BaseController {

    /**
     * 注入项目资源业务层bean
     */
    @Resource
    private ProResourcesService proResourcesService;


    /**
     * @author heShaoHua
     * @describe 获取该项目角色已拥有的资源信息
     * @param roleId 角色id
     * @updateInfo 暂无
     * @date 2019/6/25
     * @return 资源信息
     */
    @GetMapping("/{roleId}")
    public JSONObject getResourcesByRoleId(@PathVariable String roleId){
        JSONObject jsonObject = new JSONObject();
        List<ResourceShowVO> rv = proResourcesService.getResourceVO(roleId);
        jsonObject.put("data",rv);
        jsonObject.put("result", 1);
        return jsonObject;
    }
}

