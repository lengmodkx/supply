package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.entity.fabulous.Fabulous;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.fabulous.FabulousService;
import com.art1001.supply.service.publics.PublicService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Description 公共功能接口
 * @Date:2019/4/3 17:41
 * @Author heshaohua
 **/
@RequestMapping("/public")
@RestController
public class PublicApi {

    @Resource
    private FabulousService fabulousService;

    @Resource
    private TaskService taskService;

    @Resource
    private BindingService bindingService;

    @Resource
    private PublicService publicService;

    /**
     * 点赞
     * @param publicId 公共id
     * @return 是否成功
     */
    @Push(value = PushType.A20,type = 1)
    @PostMapping("/fabulous")
    public JSONObject fabulous(@RequestParam String publicId){
        JSONObject jsonObject = new JSONObject();
        try {
            if(publicService.fabulous(publicId) == 1){
                jsonObject.put("result",1);
                jsonObject.put("msgId",bindingService.getProjectId(publicId));
                jsonObject.put("data", publicId);
            } else {
                jsonObject.put("result",0);
            }
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,点赞失败!",e);
        }
    }

    /**
     * 取消点赞
     * @param publicId 公共id
     * @return 是否成功
     */
    @Push(value = PushType.A20,type = 1)
    @DeleteMapping("/{publicId}/cancle_fabulous")
    public JSONObject cancleFabulous(@PathVariable String publicId){
        JSONObject jsonObject = new JSONObject();
        try {
            if(fabulousService.remove(new QueryWrapper<Fabulous>().eq("member_id",ShiroAuthenticationManager.getUserId()).eq("public_id",publicId))){
                jsonObject.put("result",1);
                jsonObject.put("msgId",bindingService.getProjectId(publicId));
                jsonObject.put("data", publicId);
            } else{
                jsonObject.put("result",0);
            }
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,取消点赞失败!",e);
        }
    }
}
