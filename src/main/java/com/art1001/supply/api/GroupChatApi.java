package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSException;
import com.art1001.supply.entity.chat.Chat;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.chat.ChatService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.AliyunOss;
import com.art1001.supply.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 项目
 * @author 汪亚锋
 * [POST]   // 新增
 * [GET]    // 查询
 * [PATCH]  // 更新
 * [PUT]    // 覆盖，全部更新
 * [DELETE] // 删除
 */
@Slf4j
@RestController
@RequestMapping("groupchat")
public class GroupChatApi {

    @Resource
    private FileService fileService;
    @Resource
    private ChatService chatService;
    @Resource
    private LogService logService;

    /**
     * 发消息
     * @param projectId 项目id
     * @param content 发送的内容
     * @param files 是否附带文件
     * @return
     */
    @PostMapping
    public JSONObject chat(@RequestParam String projectId,
                           @RequestParam(required = false, defaultValue = "") String content,
                           @RequestParam(value = "files", required = false) String files){
        JSONObject object = new JSONObject();
        try{
            //文件和内容都为空则不发送推送消息
            if (files == null && StringUtils.isEmpty(content)) {
                object.put("result",0);
                return object;
            }
            Chat chat = new Chat();
            chat.setMemberId(ShiroAuthenticationManager.getUserId());
            chat.setContent(content);
            chat.setProjectId(projectId);
            chatService.saveChat(chat,files);

            object.put("result",1);
            object.put("msg","保存成功");
        }catch(Exception e){
            log.error("系统异常,群聊消息发送失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 撤回消息
     *
     * @param id 消息id
     * @return
     */
    @PatchMapping("/{id}/withdraw")
    public JSONObject withdrawMessage(@PathVariable(value = "id") String id, @RequestParam(value = "projectId") String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            logService.withdrawMessage(id);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("系统异常,撤回失败:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 下载当前消息中的附件
     * @param chatId 消息id
     */
    @GetMapping("/{chatId}")
    public void downloadBatch(@PathVariable(value = "chatId") String chatId, HttpServletResponse response, HttpServletRequest request){
        try{
            List<File> fileList = fileService.findFileByPublicId(chatId);
            AliyunOss.downloadzip(fileList,response,request);
        } catch (OSSException e){
            log.error("文件不存在!",e);
            throw new AjaxException(e);
        } catch(Exception e){
            log.error("系统异常:",e);
            throw new AjaxException(e);
        }
    }

    @GetMapping
    public JSONObject chats(){
        JSONObject object = new JSONObject();
        try{
            List<Chat> chatList = chatService.findChatAllList();
            if(CommonUtils.listIsEmpty(chatList)){
                object.put("data","无数据");
                object.put("result",1);
                return object;
            }
            object.put("result",1);
            object.put("data",chatList);
        }catch(Exception e){
            log.error("系统异常:",e);
            throw new AjaxException(e);
        }
        return object;
    }
}
