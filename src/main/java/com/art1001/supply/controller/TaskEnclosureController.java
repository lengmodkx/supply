package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.task.PushType;
import com.art1001.supply.entity.task.TaskFile;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.service.task.TaskFileService;
import com.art1001.supply.util.AliyunOss;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("task/file/")
public class TaskEnclosureController {

    @Resource
    private TaskFileService taskFileService;

    /** 用于订阅推送消息 */
    @Resource
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("modelUplaod.html")
    public String modelUplaodPage(String projectId, String taskId, String windowName, Model model){
        model.addAttribute("projectId",projectId);
        model.addAttribute("taskId",taskId);
        model.addAttribute("windowName",windowName);
        return "task-model-file";
    }


    /**
     * 上传文件
     *
     * @param projectId 项目id
     */
    @PostMapping("/uploadModel")
    @ResponseBody
    public JSONObject uploadModel(String projectId, String fileCommon,String fileModel,String taskId) {
        JSONObject jsonObject = new JSONObject();
        if(fileModel.equals("{}") || fileCommon.equals("{}")){
            jsonObject.put("result",0);
            jsonObject.put("msg","请补充 模型文件或者缩略图!");
            return jsonObject;
        }
        try {
            JSONObject array = JSON.parseObject(fileCommon);
            JSONObject object = JSON.parseObject(fileModel);
            String fileName = object.getString("fileName");
            String fileUrl = object.getString("fileUrl");
            String size = object.getString("size");
            String ext = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            // 写库
            TaskFile taskFile = new TaskFile();
            taskFile.setTaskId(taskId);
            taskFile.setFileName(fileName);
            taskFile.setFileSize(size);
            taskFile.setFileExt(ext);
            taskFile.setFileUrl(fileUrl);
            taskFile.setFileThumbnail(array.getString("fileUrl"));
            taskFileService.saveTaskFile(taskFile);

            PushType pushType = new PushType(TaskLogFunction.A31.getName());
            Map<String,Object> map = new HashMap<>();
            map.put("taskFile",taskFile);
            map.put("taskId",taskId);
            pushType.setObject(map);
            messagingTemplate.convertAndSend("/topic/"+taskId, new ServerMessage(JSON.toJSONString(pushType)));
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("上传文件异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "上传失败");
        }
        return jsonObject;
    }


    /**
     * 下载
     *
     * @param fileId
     */
    @RequestMapping(value = "/download",method = RequestMethod.GET)
    @ResponseBody
    public void downloadFile(@RequestParam String fileId,HttpServletResponse response) throws IOException {
        // 通过response对象获取OutputStream流
        OutputStream os = response.getOutputStream();
        try {
            TaskFile file = taskFileService.findTaskFileById(fileId);
            InputStream inputStream  = AliyunOss.downloadInputStream(file.getFileUrl());

            //设置content-disposition响应头控制浏览器弹出保存框，若没有此句则浏览器会直接打开并显示文件。
            //中文名要经过URLEncoder.encode编码，否则虽然客户端能下载但显示的名字是乱码
            // 设置响应类型
            response.setContentType("application/x-msdownload");
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(file.getFileName(), "UTF-8"));
            int byteCount;
            //1M逐个读取
            byte[] bytes = new byte[1024*1024];
            assert inputStream != null;
            while ((byteCount = inputStream.read(bytes)) != -1){
                os.write(bytes, 0, byteCount);
            }
            inputStream.close();
            os.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
