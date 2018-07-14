package com.art1001.supply.controller;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.binding.BindingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/binding")
public class BindingController {

    @Resource
    private BindingService bindingService;

    /**
     * 任务,日程，文件，分享与任务,日程，文件，分享的绑定关系
     * @param publicId 任务,日程，文件，分享的id
     * @param bindId 被绑定的任务,日程，文件，分享的id
     * @param publicType 绑定类型 任务,日程，文件，分享 枚举类型
     * @return
     */
    @RequestMapping("/saveBinding")
    @ResponseBody
    public JSONObject saveBinding(@RequestParam String publicId,@RequestParam String bindId,@RequestParam String publicType){
        JSONObject jsonObject = new JSONObject();


        Binding binding = new Binding();
        try {
            binding.setPublicId(publicId);
            binding.setBindId(bindId);
            binding.setPublicType(publicType);
            bindingService.saveBinding(binding);
            jsonObject.put("result",1);
            jsonObject.put("msg","保存成功");
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    /**
     *
     * @param Id 绑定id
     * @return
     */
    @RequestMapping("/deleteBinding")
    @ResponseBody
    public JSONObject deleteBinding(@RequestParam String Id){
        JSONObject jsonObject = new JSONObject();

        try {
            bindingService.deleteBindingById(Id);
            jsonObject.put("result",1);
            jsonObject.put("msg","删除成功");
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }

}
