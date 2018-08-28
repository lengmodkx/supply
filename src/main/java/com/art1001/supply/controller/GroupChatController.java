package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.controller.base.BaseController;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.task.PushType;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.FileExt;
import com.art1001.supply.util.IdGen;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

@Controller
@Slf4j
@RequestMapping("/chat")
public class GroupChatController extends BaseController {

    @Resource
    private ProjectService projectService;

    @Resource
    private FileService fileService;

    @Resource
    private TaskService taskService;

    @Resource
    private LogService logService;

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    @RequestMapping("/chat.html")
    public String share(@RequestParam String projectId, Model model){
        List<Log> logs = logService.initAllLog(projectId);
        model.addAttribute("logs",logs);
        model.addAttribute("exts",FileExt.extMap.get("images"));
        model.addAttribute("user",ShiroAuthenticationManager.getUserEntity());
        model.addAttribute("project",projectService.findProjectByProjectId(projectId));
        return "chat";
    }

    /**
     * 保存项目群聊的消息 和 文件
     * @param projectId 项目id
     * @param content 消息内容
     * @param files 上传的文件
     * @return
     */
    @PostMapping("saveChat")
    @ResponseBody
    public JSONObject saveChat(
            @RequestParam String projectId,
            @RequestParam(required = false,defaultValue = "") String content,
            @RequestParam(value = "files",required = false) String files){
        JSONObject jsonObject = new JSONObject();

        JSONObject pushData = new JSONObject();
        try {
            //文件和内容都为空则不发送推送消息
            if(files==null&&StringUtils.isEmpty(content)){
                return jsonObject;
            }

            List<String> fileIds = new ArrayList<String>();
            if(StringUtils.isNotEmpty(files)){
                JSONArray array = JSON.parseArray(files);
                for (int i=0;i<array.size();i++) {
                    JSONObject object = array.getJSONObject(i);
                    String fileName = object.getString("fileName");
                    String fileUrl = object.getString("fileUrl");
                    String size = object.getString("size");
                    String ext = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
                    // 写库
                    File myFile = new File();
                    myFile.setFileId(IdGen.uuid());
                    // 用原本的文件名
                    myFile.setFileName(fileName);
                    myFile.setExt(ext);
                    myFile.setProjectId(projectId);
                    myFile.setFileUrl(fileUrl);
                    // 得到上传文件的大小
                    myFile.setSize(size);
                    myFile.setCatalog(0);
                    myFile.setParentId("0");
                    myFile.setFileUids(ShiroAuthenticationManager.getUserId());
                    fileIds.add(myFile.getFileId());
                    fileService.saveFile(myFile);
                }
            }

            //处理聊天内容
            if(StringUtils.isNotEmpty(content)){
                Log log = new Log();
                log.setId(IdGen.uuid());
                if(StringUtils.isEmpty(content)){
                    log.setContent("");
                }
                log.setLogType(1);
                log.setMemberId(ShiroAuthenticationManager.getUserId());
                log.setPublicId(projectId);
                log.setCreateTime(System.currentTimeMillis());
                log.setContent(content);
                log.setFileIds(StringUtils.join(fileIds,","));
                Log log1 = logService.saveLog(log);
                jsonObject.put("result", 1);
                jsonObject.put("msg", "上传成功");

                //封装推送数据包
                pushData.put("exts",FileExt.extMap.get("images"));
                pushData.put("type","收到消息");
                pushData.put("data",log1);
                pushData.put("userId",ShiroAuthenticationManager.getUserId());
                messagingTemplate.convertAndSend("/topic/chat/"+projectId,new ServerMessage(JSON.toJSONString(pushData)));
            }
        } catch (Exception e){
            log.error("系统异常,消息发送失败,{}",e);
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,消息发送失败!");
        }
        return jsonObject;

    }

}
