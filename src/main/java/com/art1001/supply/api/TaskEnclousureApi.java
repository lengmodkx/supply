package com.art1001.supply.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.task.TaskFile;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.task.TaskFileService;
import com.art1001.supply.util.AliyunOss;
import com.art1001.supply.util.FileExt;
import com.art1001.supply.util.IdGen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author heshaohua
 * @Title: TaskEnclousureApi  任务文件api
 * @Description: TODO
 * @date 2018/9/30 11:32
 **/
@RestController
@Slf4j
@RequestMapping("tasks/file/")
public class TaskEnclousureApi {

    @Resource
    private FileService fileService;

    @Resource
    private TaskFileService taskFileService;

    /**
     * 上传文件
     *
     * @param projectId 项目id
     */
    @PostMapping("/uploadModel")
    public JSONObject uploadModel(String projectId, String fileCommon, String fileModel, String taskId, String filename) {
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
            taskFile.setFileName(filename+ext);
            taskFile.setFileSize(size);
            taskFile.setFileExt(ext);
            taskFile.setFileUrl(fileUrl);
            taskFile.setFileThumbnail(array.getString("fileUrl"));
            taskFile.setId(IdGen.uuid());
            taskFileService.saveTaskFile(taskFile);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("系统异常,任务文件上传失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    @PostMapping("uploadCommon")
    public JSONObject uploadCommon(String projectId,String files ,String taskId){
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray array = JSON.parseArray(files);
            for (int i=0;i<array.size();i++) {
                JSONObject object = array.getJSONObject(i);
                String fileName = object.getString("fileName");
                String fileUrl = object.getString("fileUrl");
                String size = object.getString("size");
                String ext = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
                // 写库
                TaskFile taskFile = new TaskFile();
                taskFile.setId(IdGen.uuid());
                taskFile.setTaskId(taskId);
                taskFile.setFileName(fileName);
                taskFile.setFileSize(size);
                taskFile.setFileExt(ext);
                taskFile.setFileUrl(fileUrl);
                if(FileExt.extMap.get("images").contains(ext)){
                    taskFile.setFileThumbnail(fileUrl);
                }
                taskFileService.saveTaskFile(taskFile);
            }

            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("上传文件异常, {}", e);
            jsonObject.put("result", 0);
        }
        return jsonObject;
    }

    /**
     * 下载
     *
     * @param fileId
     */
    @GetMapping(value = "/{fileId}/download")
    public void downloadFile(@PathVariable(value = "fileId") String fileId,HttpServletResponse response) throws IOException {
        // 通过response对象获取OutputStream流
        OutputStream os = response.getOutputStream();
        try {
            TaskFile file = taskFileService.findTaskFileById(fileId);
            InputStream inputStream  = AliyunOss.downloadInputStream(file.getFileUrl(),response);

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

    @PostMapping("uploadLibrary")
    public JSONObject uploadLibrary(String projectId,String[] files ,String taskId){
        JSONObject jsonObject = new JSONObject();
        try {
            if(files==null){
                jsonObject.put("result", 0);
                return jsonObject;
            }
            List<TaskFile> taskFiles = new ArrayList<>();
            for (int i=0;i<files.length;i++) {
                String fileId = files[i];
                File file = fileService.findFileById(fileId);
                // 写库
                TaskFile taskFile = new TaskFile();
                taskFile.setId(fileId);
                taskFile.setTaskId(taskId);
                taskFile.setFileName(file.getFileName());
                taskFile.setFileSize(file.getSize());
                taskFile.setFileExt(file.getExt());
                taskFile.setFileUrl(file.getFileUrl());
                taskFile.setFileThumbnail(file.getFileThumbnail());
                if(FileExt.extMap.get("images").contains(file.getExt())){
                    taskFile.setFileThumbnail(file.getFileUrl());
                }
                taskFileService.saveTaskFile(taskFile);
                taskFiles.add(taskFile);
            }

            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("上传文件异常, {}", e);
            jsonObject.put("result", 0);
        }
        return jsonObject;
    }
}
