package com.art1001.supply.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.binding.BindingVo;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.FileVersion;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.file.FileVersionService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.AliyunOss;
import com.art1001.supply.util.FileExt;
import com.art1001.supply.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author heshaohua
 * @Title: FileApi
 * @Description: TODO 文件API接口
 * @date 2018/9/17 14:34
 **/
@Slf4j
@RequestMapping("files")
@RestController
public class FileApi {

    /**
     * 文件逻辑层接口
     */
    @Resource
    private FileService fileService;

    /**
     * 分组/菜单 逻辑层接口
     */
    @Resource
    private RelationService relationService;

    /**
     * 文件版本的逻辑层接口
     */
    @Resource
    private FileVersionService fileVersionService;

    /**
     * 关联信息的逻辑层接口
     */
    @Resource
    private BindingService bindingService;

    /**
     * 收藏信息的逻辑层接口
     */
    @Resource
    private PublicCollectService publicCollectService;

    /**
     * 日志的逻辑层接口
     */
    @Resource
    private LogService logService;

    /**
     * 用户信息的逻辑层接口
     */
    @Resource
    private UserService userService;


    /**
     * 加载项目下文件列表数据
     * @param projectId 项目id
     * @param fileId 文件id
     * @return
     */
    @GetMapping
    public JSONObject fileList(@RequestParam(value = "projectId") String projectId,
                           @RequestParam(value = "fileId",required = false,defaultValue = "0") String fileId) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<File> fileList = fileService.findProjectFile(projectId, fileId);
            jsonObject.put("data", fileList);
            jsonObject.put("parentId", fileId);
            jsonObject.put("projectId", projectId);
            jsonObject.put("currentGroup",relationService.findDefaultRelation(projectId));
            jsonObject.put("code",200);
            jsonObject.put("result",1);
            //获取文件的后缀名
            jsonObject.put("exts",FileExt.extMap);
        } catch (Exception e){
            e.printStackTrace();
            log.error("系统异常,{}",e.getMessage());
            jsonObject.put("code",500);
            jsonObject.put("msg","系统异常");
        }
        return jsonObject;
    }

    /**
     * 打开文件详情
     */
    @GetMapping("/{fileId}")
    public JSONObject openDownloadFile(@PathVariable(value = "fileId") String fileId, Model model) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result",0);
        try {
            File file = fileService.findFileById(fileId);
            if(file == null){
                jsonObject.put("msg","文件不存在!");
                jsonObject.put("code",404);
                return jsonObject;
            }
            List<FileVersion> fileVersionList = fileVersionService.findByFileId(fileId);
            jsonObject.put("data",file);

            //查询出文件的日志信息
            List<Log> logs = logService.initLog(fileId);
            Collections.reverse(logs);
            jsonObject.put("logs",logs);
            //查询出任务的关联信息
            jsonObject.put("bindings",bindingService.listBindingInfoByPublicId(file.getFileId()));
            //查询该文件有没有被当前用户收藏
            jsonObject.put("isCollect",publicCollectService.isCollItem(file.getFileId()));
            //查询出该文件的所有参与者信息
            jsonObject.put("joins",userService.findManyUserById(fileService.findJoinId(file.getFileId())));
            jsonObject.put("result",1);
            jsonObject.put("code",200);
        } catch (Exception e){
            log.error("系统异常,{}",e.getMessage());
            jsonObject.put("msg","系统异常!");
            jsonObject.put("code",500);
        }
        return jsonObject;
    }

    /**
     * 获取子目录
     */
    @GetMapping("/childFolder/{folderId}")
    public JSONObject getChildFolder(@PathVariable(value = "folderId") String fileId) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<File> fileList = fileService.findChildFolder(fileId);
            jsonObject.put("result", 1);
            jsonObject.put("data", fileList);
            jsonObject.put("code",200);
        } catch (Exception e) {
            log.error("获取子目录失败， {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "获取失败");
            jsonObject.put("code",500);
        }
        return jsonObject;
    }

    /**
     * 创建文件夹
     * @param projectId  项目id
     * @param parentId   上一级目录id  默认为0
     * @param folderName 文件夹名称
     */
    @PostMapping("/createFolder")
    public JSONObject createFolder(
            @RequestParam String projectId,
            @RequestParam(required = false, defaultValue = "0") String parentId,
            @RequestParam String folderName
    ) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result",0);
        try {
            fileService.createFolder(projectId,parentId,folderName);
            jsonObject.put("result",1);
            jsonObject.put("code",201);
        } catch (ServiceException e){
            log.error("文件夹已存在!");
            jsonObject.put("msg", "文件夹已存在!");
            jsonObject.put("code",409);
        } catch (Exception e) {
            log.error("创建文件夹异常, {}", e);
            jsonObject.put("msg", "创建失败");
        }
        return jsonObject;
    }

    /**
     * 上传文件
     *
     * @param projectId 项目id
     */
    @PostMapping("/upload")
    public JSONObject uploadFile(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "files",required = false) String files,
            @RequestParam(value = "parentId",defaultValue = "0") String parentId
    ) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", 0);
        try {
            //文件为空则不执行
            if(files == null){
                jsonObject.put("msg", "上传失败");
                return jsonObject;
            }
            fileService.saveFileBatch(projectId,files,parentId);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("上传文件异常, {}", e);
            jsonObject.put("code",500);
            jsonObject.put("msg", "上传失败");
        }
        return jsonObject;
    }

    /**
     * 上传文件
     *
     * @param projectId 项目id
     */
    @PostMapping("/uploadModel")
    public JSONObject uploadModel(
            @RequestParam String projectId,
            @RequestParam(value = "fileCommon") String fileCommon
            ,@RequestParam(value = "fileModel") String fileModel
            ,@RequestParam(value = "parentId",defaultValue = "0",required = false) String parentId
            ,@RequestParam(value = "filename") String filename
    ) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result",0);
        try {
            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            JSONObject array = JSON.parseObject(fileCommon);
            JSONObject object = JSON.parseObject(fileModel);
            String fileName = object.getString("fileName");
            String fileUrl = object.getString("fileUrl");
            String size = object.getString("size");
            File modelFile = new File();
            // 用原本的文件名
            modelFile.setFileName(filename);
            modelFile.setSize(size);
            modelFile.setFileUrl(fileUrl);
            modelFile.setFileName(fileName);
            modelFile.setProjectId(projectId);
            modelFile.setFileThumbnail(array.getString("fileUrl"));
            fileService.saveFile(modelFile);

            //版本历史更新
            FileVersion fileVersion = new FileVersion();
            fileVersion.setFileId(modelFile.getFileId());
            fileVersion.setFileSize(size);
            fileVersion.setFileUrl(fileUrl);
            fileVersion.setIsMaster(1);
            Date time = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String format = simpleDateFormat.format(time);
            fileVersion.setInfo(userEntity.getUserName() + " 上传于 " + format);
            fileVersionService.saveFileVersion(fileVersion);
            jsonObject.put("result",1);
            jsonObject.put("code",201);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件异常, {}", e);
            jsonObject.put("msg", "上传失败");
            jsonObject.put("code",500);
        }
        return jsonObject;
    }

    /**
     * 更新文件
     */
    @PostMapping("/{fileId}/version")
    public JSONObject updateUploadFile(
            @PathVariable(value = "fileId") String fileId,
            MultipartFile file
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            String url = fileService.updateVersion(file,fileId);
            // 设置返回数据
            jsonObject.put("result", 1);
            jsonObject.put("data", url);
        } catch (ServiceException e){
            jsonObject.put("result", 0);
            jsonObject.put("data", "更新文件时,发生错误!");
            jsonObject.put("code","");
        }
        catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("result", 0);
            jsonObject.put("data", "更新失败");
        }
        return jsonObject;
    }

    /**
     * 删除
     *
     * @param fileId 文件id
     */
    @DeleteMapping("/{fileId}")
    public JSONObject deleteFile(@PathVariable(value = "fileId") String fileId, @RequestParam(value = "projectId") String projectId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result",0);
        try {
            fileService.deleteFileById(fileId);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除文件异常, {}", e);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "删除成功");
        }
        return jsonObject;
    }

}
