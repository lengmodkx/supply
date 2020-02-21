package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserNewsService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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

    @Resource
    private UserNewsService userNewsService;

    @Resource
    private TaskService taskService;

    @Resource
    private UserService userService;

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
            UserEntity byId = userService.getById(ShiroAuthenticationManager.getUserId());
            Log log = new Log();
            log.setPublicId(publicId);
            log.setProjectId(projectId);
            log.setLogType(1);
            log.setContent(content);
            log.setCreateTime(System.currentTimeMillis());
            log.setMemberId(ShiroAuthenticationManager.getUserId());
            if(logService.save(log)) {
                String[] taskJoinAndExecutorId = taskService.getTaskJoinAndExecutorId(publicId);
                if(taskJoinAndExecutorId != null && taskJoinAndExecutorId.length > 0){
                    userNewsService.saveUserNews(taskJoinAndExecutorId,publicId,publicType,byId.getUserName()+": "+ content);
                }
                log.setMemberImg(byId.getImage());
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
     * 加载剩余消息数据
     * @param publicId 信息id
     * @param surpluscount 当前显示的最后一条消息的索引
     * @return 返回消息数据
     */
    @GetMapping("/{publicId}/surplus_msg")
    public JSONObject getSurplusMsg(@PathVariable String publicId,@RequestParam Integer surpluscount){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",logService.getSurplusMsg(publicId,surpluscount));
            jsonObject.put("result",1);
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,消息加载失败!",e);
        }
    }

    /**
     * 校验消息的类型 是否合法
     */
    private void msgTypeCheck(String publicType){
        if(!(Constants.TASK.equals(publicType) || Constants.FILE.equals(publicType) || Constants.SHARE.equals(publicType) || Constants.SCHEDULE.equals(publicType))){
            throw new AjaxException("消息类型不合法!");
        }
    }
}
