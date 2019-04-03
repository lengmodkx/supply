package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import com.art1001.supply.util.Stringer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志消息api
 * @Description 日志、消息接口
 * @Date:2019/3/30 17:54
 * @Author heShaoHua
 **/
@Slf4j
@RestController
@RequestMapping("logs")
public class LogApi extends BaseController {

    /**
     * 注入日志逻辑层接口
     */
    @Resource
    private LogService logService;

    /**
     * 发送消息
     * @param publicId 公共id
     * @param projectId 项目id
     * @param content 发送内容
     * @param publicType 是哪个模块的消息(task,file,share,schedule)
     * @return 是否发送成功
     */
    @Push(value = PushType.F1)
    @PostMapping("/chat")
    public JSONObject sendChat(@RequestParam String publicId,@RequestParam String projectId,@RequestParam String content,@RequestParam String publicType){
        JSONObject jsonObject = new JSONObject();
        try {
            //校验publicType合法性
            msgTypeCheck(publicType);
            Log log = new Log();
            log.setPublicId(publicId);
            log.setProjectId(projectId);
            log.setLogType(1);
            log.setContent(content);
            log.setMemberId(ShiroAuthenticationManager.getUserId());
            if(logService.save(log)) {
                log.setMemberImg(ShiroAuthenticationManager.getUserEntity().getImage());
                jsonObject.put("data",new JSONObject().fluentPut("log",log).fluentPut("type",publicType));
                jsonObject.put("msgId",projectId);
                jsonObject.put("result",1);
                jsonObject.put("msg","发送成功!");
                return jsonObject;
            } else{
                return error("消息发送失败!");
            }
        } catch (Exception e){
            throw new AjaxException("系统异常,消息发送失败!",e);
        }
    }

    /**
     * 校验消息的类型 是否合法
     */
    private void msgTypeCheck(String publicType){
        if(Stringer.isNullOrEmpty(publicType)){
            throw new AjaxException("消息类型不能为空!");
        }
        if(Constants.TASK.equals(publicType) || Constants.FILE.equals(publicType) || Constants.SHARE.equals(publicType) || Constants.SCHEDULE.equals(publicType)){
            throw new AjaxException("消息类型不合法!");
        }
    }
}
