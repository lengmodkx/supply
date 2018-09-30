package com.art1001.supply.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.FileVersion;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.file.FileVersionService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.AliyunOss;
import com.art1001.supply.util.CommonUtils;
import com.art1001.supply.util.FileExt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
     * 文件版本的逻辑层接口
     */
    @Resource
    private FileVersionService fileVersionService;

    /**
     * 项目逻辑层接口
     */
    @Resource
    private ProjectService projectService;

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
     * 项目成员 逻辑层接口
     */
    @Resource
    private ProjectMemberService projectMemberService;


    /**
     * 加载项目下文件列表数据
     * @param projectId 项目id
     * @param fileId 文件id
     * @return
     */
    @GetMapping("/{projectId}")
    public JSONObject fileList(@PathVariable(value = "projectId") String projectId,
                           @RequestParam(value = "fileId",required = false,defaultValue = "0") String fileId) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<File> fileList = fileService.findProjectFile(projectId, fileId);
            jsonObject.put("data", fileList);
            jsonObject.put("parentId", fileId);
            jsonObject.put("projectId", projectId);
            jsonObject.put("currentGroup", projectMemberService.findDefaultGroup(projectId,ShiroAuthenticationManager.getUserId()));
            jsonObject.put("status",200);
            jsonObject.put("result",1);
            //获取文件的后缀名
            jsonObject.put("exts",FileExt.extMap);
        } catch (Exception e){
            log.error("系统异常:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 打开文件详情
     */
    @GetMapping("/{fileId}/details")
    public JSONObject openDownloadFile(@PathVariable(value = "fileId") String fileId, Model model) {
        JSONObject jsonObject = new JSONObject();
        try {
            File file = fileService.findFileById(fileId);
            if(file == null){
                jsonObject.put("msg","文件不存在!");
                jsonObject.put("result",1);
                return jsonObject;
            }
            List<FileVersion> fileVersionList = fileVersionService.findByFileId(fileId);
            jsonObject.put("data",file);

            //查询出文件的日志信息
            List<Log> logs = logService.initLog(fileId);
            Collections.reverse(logs);
            jsonObject.put("logs",logs);
            //查询出任务的关联信息
            jsonObject.put("bindings",bindingService.list(new QueryWrapper<Binding>().eq("public_id", fileId)));
            //查询该文件有没有被当前用户收藏
            jsonObject.put("isCollect",publicCollectService.isCollItem(file.getFileId()));
            //查询出该文件的所有参与者信息
            jsonObject.put("joins",userService.findManyUserById(fileService.findJoinId(file.getFileId())));
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 获取子目录
     */
    @GetMapping("{folderId}/child_folder")
    public JSONObject getChildFolder(@PathVariable(value = "folderId") String fileId) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<File> fileList = fileService.findChildFolder(fileId);
            if(CommonUtils.listIsEmpty(fileList)){
                jsonObject.put("result", 1);
                jsonObject.put("msg","无数据");
                return jsonObject;
            }
            jsonObject.put("result", 1);
            jsonObject.put("data", fileList);
        } catch (Exception e) {
            log.error("系统异常:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 创建文件夹
     * @param projectId  项目id
     * @param parentId   上一级目录id  默认为0
     * @param folderName 文件夹名称
     */
    @PostMapping("/{parentId}/create_folder")
    public JSONObject createFolder(
            @RequestParam String projectId,
            @PathVariable(required = false) String parentId,
            @RequestParam String folderName
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.createFolder(projectId,parentId,folderName);
            jsonObject.put("result",1);
            jsonObject.put("status",201);
        } catch (ServiceException e){
            log.error("文件夹已存在!",e);
            throw new AjaxException(e);
        } catch (Exception e) {
            log.error("创建文件夹失败:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 上传文件
     *
     * @param projectId 项目id
     */
    @PostMapping("/{parentId}/upload")
    public JSONObject uploadFile(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "files",required = false) String files,
            @PathVariable(value = "parentId") String parentId
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            //文件为空则不执行
            if(files == null){
                jsonObject.put("msg", "上传失败");
                return jsonObject;
            }
            fileService.saveFileBatch(projectId,files,parentId);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("上传文件异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 上传模型文件
     *
     * @param projectId 项目id
     */
    @PostMapping("/{parentId}/model")
    public JSONObject uploadModel(
            @RequestParam String projectId,
            @RequestParam(value = "fileCommon") String fileCommon
            ,@RequestParam(value = "fileModel") String fileModel
            ,@PathVariable(value = "parentId",required = false) String parentId
            ,@RequestParam(value = "filename") String filename
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            //查询出当前文件夹的level
            int parentLevel = fileService.getOne(new QueryWrapper<File>().select("level").eq("file_id",parentId)).getLevel();

            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            JSONObject array = JSON.parseObject(fileCommon);
            JSONObject object = JSON.parseObject(fileModel);
            String fileName = object.getString("fileName");
            String fileUrl = object.getString("fileUrl");
            String size = object.getString("size");
            File modelFile = new File();
            // 用原本的文件名
            modelFile.setFileName(filename);
            modelFile.setLevel(parentLevel+1);
            modelFile.setSize(size);
            modelFile.setFileUrl(fileUrl);
            modelFile.setParentId(parentId);
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
        } catch (Exception e) {
            log.error("上传文件异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更新文件版本
     * @param fileId 文件id
     * @param file 文件对象
     * @return
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
            jsonObject.put("data", url);
            jsonObject.put("result", 1);
        } catch (ServiceException e){
            log.error("文件版本更新失败:",e);
            throw new AjaxException(e);
        } catch (Exception e) {
            log.error("系统异常:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 删除文件
     * @param fileId 文件id
     * @param projectId
     * @return
     */
    @DeleteMapping("/{fileId}")
    public JSONObject deleteFile(@PathVariable(value = "fileId") String fileId, @RequestParam(value = "projectId") String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.deleteFileById(fileId);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "删除成功");
        } catch (Exception e) {
            log.error("删除文件异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 从回收站中恢复文件
     * @param fileId 文件id
     * @param projectId 项目id
     * @return
     */
    @PatchMapping("/{fileId}/recovery")
    public JSONObject recoveryFile(@PathVariable(value = "fileId") String fileId, @RequestParam(value = "projectId") String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.recoveryFile(fileId);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("文件恢复失败:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 下载
     * @param fileId 文件id
     * @param isPublic 下载的是不是模型文件
     */
    @GetMapping("/{fileId}/download")
    public void downloadFile(@PathVariable(value = "fileId") String fileId,
                             @RequestParam(value = "isPublic",defaultValue = "false") boolean isPublic,
                             HttpServletResponse response){
        try {
            File file = fileService.findFileById(fileId);
            String fileName = file.getFileName();
            InputStream inputStream = AliyunOss.downloadInputStream(file.getFileUrl(),response);
            // 设置响应类型
            response.setContentType("multipart/form-data");
            // 设置头信息
            // 设置fileName的编码
            fileName = URLEncoder.encode(fileName+file.getExt(), "UTF-8");
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
        } catch (NullPointerException e){
            log.error("系统异常,文件不存在:",e);
            throw new SystemException(e);
        } catch (Exception e){
            log.error("系统异常:",e);
            throw new SystemException(e);
        }
    }

    /**
     * 复制和移动文件时 获取弹框数据
     * @param fileIds 文件id数组
     * @return
     */
    @GetMapping("/{fileIds}/copy_move_file")
    public JSONObject copyFilePage(@PathVariable String[] fileIds) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<File> selectFileList = fileService.findByIds(fileIds);
            String projectId = selectFileList.get(0).getProjectId();
            // 文件数量
            AtomicInteger fileNum = new AtomicInteger();
            // 文件夹数量
            AtomicInteger folderNum = new AtomicInteger();
            selectFileList.forEach(file -> {
                if (file.getCatalog() == 1) {
                    folderNum.addAndGet(1);
                } else {
                    fileNum.addAndGet(1);
                }
            });
            // 获取所有参与的项目
            List<Project> projectList = projectService.findProjectByMemberId(ShiroAuthenticationManager.getUserId(),0);

            // 获取项目顶级的文件夹
            File file = new File();
            file.setProjectId(projectId);
            file.setParentId("0");
            file.setCatalog(1);
            List<File> fileList = fileService.findFileList(file);

            jsonObject.put("fileMsg", fileNum + "个文件");
            jsonObject.put("folderMsg", folderNum + "文件夹");
            jsonObject.put("projectList", projectList);
            jsonObject.put("fileList", fileList);
        } catch (Exception e){
            log.error("系统异常:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 移动文件
     *
     * @param fileIds  文件id数组
     * @param folderId 目标文件夹id
     */
    @PatchMapping("/{folderId}/m_move")
    public JSONObject moveFile(
            @PathVariable String folderId,
            @RequestParam String[] fileIds
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.moveFile(fileIds, folderId);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("移动文件异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 复制文件
     *
     * @param fileIds  多个文件id
     * @param folderId 目标文件夹id
     */
    @PostMapping("/copy")
    public JSONObject copyFile(
            @RequestParam(value = "fileIds") String[] fileIds,
            @RequestParam(value = "folderId",required = false,defaultValue = "0") String folderId
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            for (String fileId : fileIds) {
                // 获取源文件
                File file = fileService.findFileById(fileId);
                //文件夹处理
                if (file.getCatalog() == 1) {
                    file.setParentId(folderId);
                    String fId = file.getFileId();
                    String projectId = file.getProjectId();
                    fileService.saveFile(file);
                    List<File> childFile = fileService.findChildFile(projectId, fId);
                    if (childFile.size() > 0) {
                        Map<String, List<File>> map = new HashMap<>(10);
                        map.put(file.getFileId(), childFile);
                        this.copyFolder(map);
                    }
                } else {
                    this.copyFileSave(file, folderId);
                }
            }
        } catch (Exception e){
            log.error("系统异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 复制文件夹
     */
    private void copyFolder(Map<String, List<File>> map) {
        Map<String, List<File>> fileMap = new HashMap<>(10);
        for (Map.Entry<String, List<File>> entry : map.entrySet()) {
            // 得到键，即parentId
            String parentId = entry.getKey();
            // 得到值，即FileList
            List<File> fileList = entry.getValue();
            for (File file : fileList) {
                // 文件夹
                if (file.getCatalog() == 1) {
                    // 设置父级id
                    file.setParentId(parentId);
                    String fId = file.getFileId();
                    String projectId = file.getProjectId();
                    // 存库
                    fileService.saveFile(file);
                    // 得到此文件夹下一层的子集
                    List<File> childFile = fileService.findChildFile(projectId, fId);
                    fileMap.put(file.getFileId(), childFile);
                } else { //文件
                    this.copyFileSave(file, parentId);
                }
            }

            if (fileMap.size() > 0) {
                this.copyFolder(fileMap);
            }
        }
    }

    /**
     * 复制文件保存
     */
    private void copyFileSave(File file, String parentId) {
        // 得到源文件objectName
        String fileUrl = file.getFileUrl();
        // 源文件名
        String fileName = file.getFileName();
        // 得到后缀名
        String ext = fileName.substring(fileName.lastIndexOf("."), fileName.length() - 1);
        // 设置现在文件名
        String newFileName = System.currentTimeMillis() + "." + ext;
        // 设置现在文件objectName
        String newFileUrl = fileUrl.substring(0, fileUrl.lastIndexOf("/"));

        // 在oss上复制
        AliyunOss.copyFile(fileUrl, newFileName);

        // 设置文件名
        file.setFileName(newFileName);
        // 设置url
        file.setFileUrl(newFileUrl);
        // 设置父级id
        file.setParentId(parentId);

        file.setFileUrl(fileUrl);
        fileService.saveFile(file);
    }

    /**
     * 将文件回收站
     * @param fileIds ids
     * @param projectId 项目id
     */
    @PatchMapping("/m_recycle")
    public JSONObject moveToRecycleBin(
            @RequestParam(value = "fileIds") String[] fileIds,
            @RequestParam(value = "projectId") String projectId
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.moveToRecycleBin(fileIds);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("移入回收站异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    @GetMapping(value = "/pdfStreamHandeler")
    public void pdfStreamHandeler(@RequestParam(value = "fileId") String fileId, HttpServletResponse response) {
        try {
            File file = fileService.findFileById(fileId);
            ServletOutputStream sos = response.getOutputStream();
            URL url = new URL(Constants.OSS_URL + file.getFileUrl());

            HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();

            httpUrl.connect();
            //获取网络输入流
            BufferedInputStream bis = new BufferedInputStream(httpUrl.getInputStream());
            int b;
            while ((b = bis.read()) != -1) {
                sos.write(b);
            }
            sos.close();
            bis.close();
        } catch (Exception e) {
            log.error("关闭文件IOException!");
            throw new SystemException(e);
        }
    }

    /**
     * 获取该文件夹下的 所有文件和子文件夹
     */
    @GetMapping("/{fileId}/child")
    public JSONObject findChildFile(@RequestParam(value = "projectId") String projectId, @PathVariable(value = "fileId") String fileId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",fileService.findChildFile(projectId,fileId));
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常!",e);
            throw new SystemException(e);
        }
        return jsonObject;
    }

    /**
     * 查询出该文件的参与者 和 项目成员
     * @param fileId 文件id
     * @param  projectId 该项目的id
     * @return
     */
    @GetMapping("/join_info")
    public JSONObject findProjectMember(@RequestParam(value = "fileId") String fileId, @RequestParam(value = "projectId") String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            //查询出该文件的所有参与者id
            String uids = fileService.findJoinId(fileId);
            List<UserEntity> joinInfo = userService.findManyUserById(uids);
            List<UserEntity> projectMembers = userService.findProjectAllMember(projectId);
            //比较项目全部成员集合 和 文件参与者集合的差集
            List<UserEntity> reduce1 = projectMembers.stream().filter(item -> !joinInfo.contains(item)).collect(Collectors.toList());
            jsonObject.put("joinInfo",joinInfo);
            jsonObject.put("projectMember",reduce1);
        } catch (Exception e){
            log.error("系统异常!",e);
            throw new SystemException(e);
        }
        return jsonObject;
    }

    /**
     * 添加或者移除文件的参与者
     * @param fileId 文件id
     * @param newJoin 新的参与者id 数组
     * @return
     */
    @PatchMapping("/{fileId}/add_remove_join")
    public JSONObject addAndRemoveFileJoin(@PathVariable(value = "fileId") String fileId, @RequestParam(value = "newJoin") String newJoin){
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.addAndRemoveFileJoin(fileId, newJoin);
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,数据拉取失败:{}",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 查询我参与的文件
     * @return
     */
    @GetMapping("/my_join")
    public JSONObject findJoinFile(){
        JSONObject jsonObject = new JSONObject();
        try {
            List<File> listFile = fileService.findJoinFile();
            if(CommonUtils.listIsEmpty(listFile)){
                jsonObject.put("result",1);
                jsonObject.put("data","无数据");
                return jsonObject;
            }
            jsonObject.put("data",listFile);
        } catch (Exception e){
            log.error("系统异常,数据拉取失败,{}",e);
            throw new SystemException(e);
        }
        return jsonObject;
    }

    /**
     * 修改文件名称
     * @param fileName 文件名称
     * @param fileId 文件id
     * @return
     */
    @PatchMapping("/{fileId}/name")
    public JSONObject changeFileName(@RequestParam(value = "fileName") String fileName, @PathVariable(value = "fileId") String fileId){
        JSONObject jsonObject = new JSONObject();
        try {
            File file = fileService.findFileById(fileId);
            file.setFileName(fileName);
            fileService.updateFile(file);
            jsonObject.put("result",1);
        }catch (Exception e){
            log.error("系统异常,文件名称更新失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }


}
