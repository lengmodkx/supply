package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.recycle.RecycleParams;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.recycle.RecycleBinService;
import com.art1001.supply.util.BeanPropertiesUtil;
import com.art1001.supply.util.Stringer;
import com.art1001.supply.util.ValidatorUtils;
import com.art1001.supply.validation.organization.SaveOrg;
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
            //校验typeCorrect是否合法
            boolean typeCorrect = (!Stringer.isNullOrEmpty(type)) && (Constants.TASK_EN.equals(type) || Constants.FILE_EN.equals(type) || Constants.SCHEDULE_EN.equals(type) || Constants.SHARE_EN.equals(type) || Constants.TAG_EN.equals(type) || Constants.GROUP_EN.equals(type));
            if(!typeCorrect){
                jsonObject.put("result", 0);
                jsonObject.put("msg", "type参数不合法!");
                return jsonObject;
            }
            //判断fileType参数不能为空,并且参数值只能为 0 和 1. 否则抛出异常
            if(Constants.FILE_EN.equals(type)){
                boolean fileCorrect = (!Stringer.isNullOrEmpty(fileType)) && (Constants.ZERO.equals(fileType) || Constants.ONE.equals(fileType));
                if(!fileCorrect){
                    jsonObject.put("result", 0);
                    jsonObject.put("msg", "fileType参数不合法!");
                    return jsonObject;
                }
            }
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
        return jsonObject;
    }
}
