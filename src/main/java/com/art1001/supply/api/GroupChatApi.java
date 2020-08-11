package com.art1001.supply.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSException;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.communication.service.impl.EasemobSendMessage;
import com.art1001.supply.entity.chat.Chat;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.chat.ChatService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.AliyunOss;
import com.art1001.supply.util.FileExt;
import com.art1001.supply.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
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


    /**
     * 发消息
     * @param projectId 项目id
     * @param content 发送的内容
     * @param files 是否附带文件
     * @return 返回结果
     */
    @Push(value = PushType.G1)
    @PostMapping
    public JSONObject chat(String projectId,
                           @RequestParam(required = false, defaultValue = "") String content,
                           @RequestParam("files") String files){
        JSONObject object = new JSONObject();
        try{

            //文件和内容都为空则不发送推送消息
            if (StringUtils.isEmpty(files) && StringUtils.isEmpty(content)) {
                object.put("result",0);
                return object;
            }

            Chat chat = new Chat();
            chat.setMemberId(ShiroAuthenticationManager.getUserId());
            chat.setCreateTime(System.currentTimeMillis());
            chat.setContent(content);
            chat.setProjectId(projectId);
            chatService.save(chat);

            if(StringUtils.isNotEmpty(files)){
                JSONArray objects = JSON.parseArray(files);
                for (int i=0;i<objects.size();i++) {
                    JSONObject jsonObject = objects.getJSONObject(i);
                    File file = new File();
                    file.setProjectId(projectId);
                    file.setPublicId(chat.getChatId());
                    file.setPublicLable(1);
                    file.setFileUrl(jsonObject.getString("url"));
                    file.setFileName(jsonObject.getString("name"));
                    file.setSize(jsonObject.getString("size"));
                    file.setExt(jsonObject.getString("ext"));
                    fileService.save(file);
                }
            }
            Chat chatById = chatService.findChatById(chat.getChatId());
            chatById.setIsOwn(1);
            object.put("result",1);
            object.put("data",chatById);
            object.put("msgId",projectId);
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
     * @param chatId 消息id
     * @return
     */
    @Push(value = PushType.G2)
    @PutMapping("/{chatId}/revoke")
    public JSONObject revokeMessage(@PathVariable(value = "chatId") String chatId, @RequestParam(value = "projectId") String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            Chat chat = new Chat();
            chat.setChatId(chatId);
            chat.setChatDel(1);
            chatService.updateById(chat);
            jsonObject.put("data",chatId);
            jsonObject.put("msgId",projectId);
            jsonObject.put("result", 1);
            jsonObject.put("msg","撤回成功");
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
            throw new AjaxException(e);
        }
    }

    /**
     * 进入群聊的首页加载所有消息
     * @return 群聊消息
     */
    @GetMapping
    public JSONObject chats(@RequestParam String projectId){
        JSONObject object = new JSONObject();
        try{
            List<Chat> chatList = chatService.findChatList(projectId);
            object.put("result",1);
            object.put("data",chatList);
            object.put("images", FileExt.extMap.get("images"));
        }catch(Exception e){
            throw new AjaxException(e);
        }
        return object;
    }


}
