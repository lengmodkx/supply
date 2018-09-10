package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.controller.base.BaseController;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.relation.GroupVO;
import com.art1001.supply.entity.task.PushType;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.AliyunOss;
import com.art1001.supply.util.FileExt;
import com.art1001.supply.util.FileUtils;
import com.art1001.supply.util.IdGen;
import com.art1001.supply.util.httpclient.HttpResponse;
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
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
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
    private RelationService relationService;

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    @RequestMapping("/chat.html")
    public String share(@RequestParam String projectId, String currentGroup,Model model) {
        List<Log> logs = logService.initAllLog(projectId);
        model.addAttribute("logs", logs);
        model.addAttribute("exts", FileExt.extMap.get("images"));
        model.addAttribute("user", ShiroAuthenticationManager.getUserEntity());
        model.addAttribute("project", projectService.findProjectByProjectId(projectId));
        model.addAttribute("currentGroup",currentGroup);
        //加载该项目下所有分组的信息
        List<GroupVO> groups = relationService.loadGroupInfo(projectId);
        model.addAttribute("groups",groups);
        return "chat";
    }

    /**
     * 保存项目群聊的消息 和 文件
     *
     * @param projectId 项目id
     * @param content   消息内容
     * @param files     上传的文件
     * @return
     */
    @PostMapping("saveChat")
    @ResponseBody
    public JSONObject saveChat(
            @RequestParam String projectId,
            @RequestParam(required = false, defaultValue = "") String content,
            @RequestParam(value = "files", required = false) String files) {
        JSONObject jsonObject = new JSONObject();

        JSONObject pushData = new JSONObject();
        try {
            //文件和内容都为空则不发送推送消息
            if (files == null && StringUtils.isEmpty(content)) {
                return jsonObject;
            }

            List<String> fileIds = new ArrayList<String>();
            if (StringUtils.isNotEmpty(files)) {
                JSONArray array = JSON.parseArray(files);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    String fileName = object.getString("fileName");
                    String fileUrl = object.getString("fileUrl");
                    String size = object.getString("size");
                    String ext = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
                    // 写库
                    File myFile = new File();
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
                    //myFile.setFileSourceId(projectId);
                    fileService.saveFile(myFile);
                    fileIds.add(myFile.getFileId());
                }
            }

            Log log = new Log();
            log.setId(IdGen.uuid());
            log.setLogType(1);
            log.setMemberId(ShiroAuthenticationManager.getUserId());
            log.setPublicId(projectId);
            log.setCreateTime(System.currentTimeMillis());
            log.setFileIds(StringUtils.join(fileIds, ","));
            log.setLogIsWithdraw(0);
            //处理聊天内容
            if (StringUtils.isNotEmpty(content)) {
                if (StringUtils.isEmpty(content)) {
                    log.setContent("");
                }
                log.setContent(content);
            }
            Log log1 = logService.saveLog(log);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "上传成功");
            //封装推送数据包
            pushData.put("exts", FileExt.extMap.get("images"));
            pushData.put("type", "收到消息");
            pushData.put("data", log1);
            pushData.put("userId", ShiroAuthenticationManager.getUserId());
            messagingTemplate.convertAndSend("/topic/chat/" + projectId, new ServerMessage(JSON.toJSONString(pushData)));
        } catch (Exception e) {
            log.error("系统异常,消息发送失败,{}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "系统异常,消息发送失败!");
        }
        return jsonObject;

    }

    /**
     * 撤回消息
     *
     * @param id 消息id
     * @return
     */
    @PostMapping("withdrawMessage")
    @ResponseBody
    public JSONObject withdrawMessage(@RequestParam String id, @RequestParam String projectId) {
        JSONObject jsonObject = new JSONObject();
        JSONObject pushData = new JSONObject();

        Log log1 = logService.findLogById(id);

        //如果撤回的消息超过两分钟 禁止撤回
        if (log1.getCreateTime() - System.currentTimeMillis() == 120000) {
            jsonObject.put("result", 0);
            jsonObject.put("msg", "发送时间超过两分钟的消息,不能被撤回。");
            return jsonObject;
        }
        try {
            logService.withdrawMessage(id);
            jsonObject.put("result", 1);

            //推送消息
            pushData.put("name", log1.getUserEntity().getUserName());
            pushData.put("type", "撤回了消息");
            pushData.put("id", ShiroAuthenticationManager.getUserId());
            pushData.put("logId", id);
            messagingTemplate.convertAndSend("/topic/chat/" + projectId, new ServerMessage(JSON.toJSONString(pushData)));
        } catch (Exception e) {
            log.error("系统异常,撤回失败,{}", e);
            jsonObject.put("msg", "系统异常,撤回失败!");
            jsonObject.put("result", 0);
        }
        return jsonObject;
    }

    /**
     * 下载当前消息中的附件
     *
     * @param fileIds 附件的所有文件的id
     * @return
     */
    @RequestMapping("downLoadEnclosure")
    @ResponseBody
    public JSONObject downLoadEnclosure(String[] fileIds, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        try {

            //临时文件
            String deleteUrl = "";

            InputStream inputStream = null;

            // 获取文件
            File file = fileService.findFileById(fileIds[0]);
            String fileName = file.getFileName();

            //判断要下载的文件的数量 如果只有1个 就下载当前文件  如果有多个  就打成压缩包下载
            if (fileIds.length == 1) {
                // 文件  在oss上得到流
                inputStream = AliyunOss.downloadInputStream(file.getFileUrl(),response);
            } else {
                // 得到临时下载文件目录
                String tempPath = FileUtils.getTempPath();
                // 创建文件夹，加时间戳，区分
                String path = tempPath + "\\" + System.currentTimeMillis() + "\\" + fileName;
                java.io.File folder = new java.io.File(path);
                folder.mkdirs();

                // 下载到临时文件
                this.downloadZip(fileService.findByIds(fileIds), path,response);

                // 把临时文件打包成zip下载
                String downloadPath = path + ".zip";
                FileOutputStream fos1 = new FileOutputStream(new java.io.File(downloadPath));
                FileUtils.toZip(path, fos1, true);

                // 开始下载

                // 以流的形式下载文件。
                inputStream = new BufferedInputStream(new FileInputStream(downloadPath));
                fileName += ".zip";

                // 删除临时文件
                deleteUrl = downloadPath.substring(0, downloadPath.lastIndexOf("\\"));
            }
            // 设置响应类型
            response.setContentType("multipart/form-data");

            // 设置头信息
            // 设置fileName的编码
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] bytes = new byte[1024];
            int n;
            assert inputStream != null;
            while ((n = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, n);
            }
            outputStream.close();
            inputStream.close();

            if (file.getCatalog() == 1) {
                FileUtils.delFolder(deleteUrl);
            }
        } catch (Exception e) {
            log.error("系统异常,下载失败,{}", e);
            jsonObject.put("msg", "系统异常,下载失败!");
            jsonObject.put("result", 0);
        }
        return jsonObject;

}

    /**
     * 下载zip
     *
     * @param fileList 下载条件
     * @param path     url
     */
    private void downloadZip(List<File> fileList, String path,HttpServletResponse response) {
        List<File> fileUrl = new ArrayList<>();
        for (File file : fileList) {
            int byteSum = 0;
            int byteRead = 0;
            InputStream inStream = null;
            FileOutputStream fs = null;
            try {
                inStream = AliyunOss.downloadInputStream(file.getFileUrl(),response);
                fs = new FileOutputStream(path + "\\" + file.getFileName());

                byte[] buffer = new byte[1204];
                assert inStream != null;
                while ((byteRead = inStream.read(buffer)) != -1) {
                    byteSum += byteRead;
                    fs.write(buffer, 0, byteRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    assert inStream != null;
                    inStream.close();
                    assert fs != null;
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}