package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.log.LogExportRecord;
import com.art1001.supply.entity.log.LogSendParam;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.log.LogExportRecordService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserNewsService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.ExcelUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志消息api
 *
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
    private FileService fileService;

    @Resource
    private ShareService shareService;

    @Resource
    private ScheduleService scheduleService;

    @Resource
    private UserService userService;

    @Resource
    private LogExportRecordService logExportRecordService;

    /**
     * 发送消息
     *
     * @param logSendParam 消息参数
     * @return 是否发送成功
     */
    @Push(value = PushType.F1)
    @PostMapping("/chat")
    public JSONObject sendChat(@Validated @RequestBody LogSendParam logSendParam) {
        JSONObject jsonObject = new JSONObject();
        try {
            //校验publicType合法性
            msgTypeCheck(logSendParam.getPublicType());
            Log log = logService.sendChat(logSendParam);

            String[] joinAndExecutorId = new String[0];

            if (Constants.TASK.equals(logSendParam.getPublicType())) {
                joinAndExecutorId = taskService.getTaskJoinAndExecutorId(logSendParam.getPublicId());
            }
            if (Constants.FILE.equals(logSendParam.getPublicType())) {
                joinAndExecutorId = fileService.getJoinAndCreatorId(logSendParam.getPublicId());
            }
            if (Constants.SCHEDULE.equals(logSendParam.getPublicType())) {
                joinAndExecutorId = scheduleService.getJoinAndCreatorId(logSendParam.getPublicId());
            }
            if (Constants.SHARE.equals(logSendParam.getPublicType())) {
                joinAndExecutorId = shareService.getJoinAndCreatorId(logSendParam.getPublicId());
            }

            if (CollectionUtils.isEmpty(logSendParam.getMentionIdList())) {
                logSendParam.setMentionIdList(new ArrayList<>());
            }
            if (joinAndExecutorId != null && joinAndExecutorId.length > 0) {
                userNewsService.saveUserNews(
                        joinAndExecutorId, logSendParam.getPublicId(),
                        logSendParam.getPublicType(), log.getMemberName() + ": " + logSendParam.getContent(),
                        logSendParam.getMentionIdList()
                );
                log.setMemberImg(log.getMemberImg());
                jsonObject.put("data", new JSONObject().fluentPut("log", log).fluentPut("type", logSendParam.getPublicType()));
                jsonObject.put("msgId", logSendParam.getProjectId());
                jsonObject.put("result", 1);
                jsonObject.put("msg", "发送成功!");
                return jsonObject;
            } else {
                return error("消息发送失败!");
            }
        } catch (Exception e) {
            throw new AjaxException("系统异常,消息发送失败!", e);
        }
    }

    /**
     * 加载剩余消息数据
     *
     * @param publicId     信息id
     * @param surpluscount 当前显示的最后一条消息的索引
     * @return 返回消息数据
     */
    @GetMapping("/{publicId}/surplus_msg")
    public JSONObject getSurplusMsg(@PathVariable String publicId, @RequestParam Integer surpluscount) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", logService.getSurplusMsg(publicId, surpluscount));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,消息加载失败!", e);
        }
    }

    /**
     * 校验消息的类型 是否合法
     */
    private void msgTypeCheck(String publicType) {
        if (!(Constants.TASK.equals(publicType) || Constants.FILE.equals(publicType) || Constants.SHARE.equals(publicType) || Constants.SCHEDULE.equals(publicType))) {
            throw new AjaxException("消息类型不合法!");
        }
    }


    /**
     * 根据条件筛选日志信息
     *
     * @param orgId
     * @param memberId
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/operatingLog/{orgId}")
    public JSONObject operatingLog(@PathVariable(value = "orgId") String orgId,
                                   @RequestParam(value = "memberId", required = false) List<String> memberId,
                                   @RequestParam(value = "startTime", required = false) Long startTime,
                                   @RequestParam(value = "endTime", required = false) Long endTime) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", logService.selectLogByCondition(orgId, memberId, startTime, endTime));
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    @GetMapping("/expUser")
    public JSONObject expUser(HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<Log> logs = logService.getMyLog();
            if (CollectionUtils.isNotEmpty(logs)) {
                ExcelUtils.exportExcel(logs, null, "用户数据", Log.class, "日志信息.xlsx", response);
                jsonObject.put("result", 1);
            }

            //todo
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }

    }
}
