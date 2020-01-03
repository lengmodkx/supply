package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.recycle.RecycleParams;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.recycle.RecycleBinService;
import com.art1001.supply.util.BeanPropertiesUtil;
import com.art1001.supply.util.ValidatorUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Description 回收站Api
 * @Date:2019/5/14 10:37
 * @Author heshaohua
 **/
@Validated
@RestController
@RequestMapping("/recycle_bin")
public class RecycleBinApi {

    /**
     * 注入任务逻辑层Bean
     */
    @Resource
    private RecycleBinService recycleBinService;

    /**
     * 获取一个项目回收站中的信息
     * @param projectId 项目id
     * @param type 信息类型
     * @param fileType 文件类型(获取的是文件夹还是文件)
     * @return 回收站信息
     */
    @GetMapping("/{projectId}/{type}")
    public JSONObject getRecycleBinItem(@PathVariable String projectId, @PathVariable String type, @RequestParam(required = false) String fileType){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",recycleBinService.getRecycleBinItem(projectId, type,fileType));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,数据获取失败!",e);
        }
    }

    /**
     * 恢复回收站中的内容
     * @param recycleParams 参数对象
     * @return 结果
     */

    @PutMapping("/recovery")
    @Push(type = 1,value = PushType.D14)
    public JSONObject recovery(RecycleParams recycleParams){
        JSONObject jsonObject = new JSONObject();
        ValidatorUtils.validateEntity(recycleParams);
        if(Constants.TASK_EN.equals(recycleParams.getPublicType())){
            try {
                BeanPropertiesUtil.fieldsNotNullOrEmpty(recycleParams, new String[]{"projectId","groupId","menuId"});
            } catch (IllegalAccessException e) {
                throw new AjaxException("系统异常!",e);
            }
        }
        jsonObject.put("result", recycleBinService.recovery(recycleParams));
        jsonObject.put("msgId",recycleParams.getProjectId());
        jsonObject.put("data",recycleParams.getProjectId());
        return jsonObject;
    }
}
