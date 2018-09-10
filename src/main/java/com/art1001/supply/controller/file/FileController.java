package com.art1001.supply.controller.file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.controller.base.BaseController;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.binding.BindingVo;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.FileVersion;
import com.art1001.supply.entity.file.PublicFile;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.relation.GroupVO;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.PushType;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.file.FileVersionService;
import com.art1001.supply.service.file.PublicFileService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.*;
import io.netty.handler.codec.json.JsonObjectDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 文件相关controller
 */
@Controller
@RequestMapping("file")
@Slf4j
public class FileController extends BaseController {

    /**
     * 标注是不是模型文件夹
     */
    private final String publicName = "公共模型库";

    @Resource
    private RelationService relationService;

    @Resource
    private FileService fileService;

    @Resource
    private ProjectService projectService;

    @Resource
    private TagService tagService;

    @Resource
    private FileVersionService fileVersionService;

    @Resource
    private BindingService bindingService;

    @Resource
    private LogService logService;

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    @Resource
    private PublicCollectService publicCollectService;

    @Resource
    private UserService userService;

    @Resource
    private PublicFileService publicFileService;


    /**
     * 文件列表
     *
     * @param file projectId 项目id
     *             parentId  上级目录id
     *             isDel     删除标识 默认为0
     */
    @GetMapping("/list.html")
    public String list(File file,String currentGroup, Model model) {
        String userId = ShiroAuthenticationManager.getUserId();

        File fileById = new File();
        if(!StringUtils.isEmpty(file.getFileId())){
            fileById = fileService.findFileById(file.getFileId());
            model.addAttribute("fileName",fileById.getFileName());
        }
        UserEntity userEntity = userService.findById(userId);
        // 项目id
        String projectId = file.getProjectId();
        // 上级id
        String parentId = file.getFileId();
        if (StringUtils.isEmpty(file.getFileId())) {
            parentId = "0";
        }

        // 删除标识
        Integer fileDel = file.getFileDel();
        //如果用户点击的 公共模型库 的文件夹 则去文件公共表查询数据
        List<File> fileList = fileService.findChildFile(projectId, parentId, fileDel);
        model.addAttribute("fileList", fileList);
        model.addAttribute("parentId", parentId);
        model.addAttribute("projectId", projectId);
        model.addAttribute("currentGroup",currentGroup);
        model.addAttribute("project", projectService.findProjectByProjectId(projectId));

        //加载该项目下所有任务分组的信息
        List<GroupVO> groups = relationService.loadGroupInfo(projectId);
        model.addAttribute("groups",groups);

        //获取文件的后缀名
        model.addAttribute("exts",FileExt.extMap);
        model.addAttribute("user",userEntity);
        return "file";
    }

    @RequestMapping("/findTopLevel")
    @ResponseBody
    public JSONObject findTopLevel(@RequestParam String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<File> fileList = fileService.findTopLevel(projectId);
            jsonObject.put("result", 1);
            jsonObject.put("data", fileList);
        } catch (Exception e) {
            log.error("获取项目顶级目录异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "获取失败");
        }
        return jsonObject;
    }

    /**
     * 弹出文件选择框
     * @param model
     * @return
     */
    @RequestMapping("selectUpType")
    public String selectUpType(Model model,String projectId,String parentId){
        model.addAttribute("projectId",projectId);
        model.addAttribute("parentId",parentId);
        return "select-up-type";
    }

    /**
     * 弹出普通文件框
     * @param model
     * @return
     */
    @RequestMapping("ordinaryFile")
    public String ordinaryFile(Model model,String projectId,String parentId){
        model.addAttribute("projectId",projectId);
        model.addAttribute("parentId",parentId);
        return "select-ordinary-file-up";
    }

    /**
     * 弹出模型选择框
     * @param model
     * @return
     */
    @RequestMapping("tkModelFile")
    public String tkModelFile(Model model,String projectId,String parentId){
        model.addAttribute("projectId",projectId);
        model.addAttribute("parentId",parentId);
        return "tk-model-file-up";
    }

    /**
     * 打开下拉框
     */
    @GetMapping("/optionFile")
    public String optionFile(@RequestParam String fileId, Model model) {
        model.addAttribute("fileId", fileId);
        return "tk-filemenu";
    }

    /**
     * 打开文件详情
     */
    @GetMapping("/fileDetail.html")
    public String openDownloadFile(@RequestParam String fileId, Model model) {
        File file = fileService.findFileById(fileId);
        String projectId = file.getProjectId();
        String tagIds = file.getTagId();
        List<Tag> tagList = new ArrayList<>();
        if (StringUtils.isNotEmpty(tagIds)) {
            String[] tagIdStrArr = tagIds.split(",");
            Integer[] tagIdArr = new Integer[tagIdStrArr.length];
            for (int i = 0; i < tagIdStrArr.length; i++) {
                tagIdArr[i] = Integer.valueOf(tagIdStrArr[i]);
            }
            tagList = tagService.findByIds(tagIdArr);
        }

        List<FileVersion> fileVersionList = fileVersionService.findByFileId(fileId);
        //查询出任务的关联信息
        BindingVo bindingVo = bindingService.listBindingInfoByPublicId(file.getFileId());

        //查询该文件有没有被当前用户收藏
        model.addAttribute("isCollect",publicCollectService.isCollItem(file.getFileId()));

        //查询出该文件的所有参与者信息
        List<UserEntity> joinInfo = userService.findManyUserById(fileService.findJoinId(file.getFileId()));
        model.addAttribute("joinInfos",joinInfo);
        model.addAttribute("bindingVo",bindingVo);
        model.addAttribute("file", file);
        List<Log> logs = logService.initLog(fileId);
        Collections.reverse(logs);
        model.addAttribute("logs",logs);
        model.addAttribute("projectId", projectId);
        model.addAttribute("tagList", tagList);
        model.addAttribute("fileVersionList", fileVersionList);
        //获取文件的后缀名
        model.addAttribute("exts",FileExt.extMap);
        return "tk-file-download";
    }

    /**
     * 文件目录
     *
     * @param file 文件
     *             projectId 项目id
     *             parentId 上级id
     */
    @GetMapping("/getFolder")
    @ResponseBody
    public JSONObject getFolder(File file) {
        JSONObject jsonObject = new JSONObject();

        try {
            // 设置 catalog 为1，只查询目录
            file.setCatalog(1);
            // 只查询未删除的目录
            file.setFileDel(0);
            if (StringUtils.isEmpty(file.getParentId())) {
                file.setParentId("0");
            }
            List<File> fileList = fileService.findFileList(file);
            jsonObject.put("result", 1);
            jsonObject.put("data", fileList);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("result", 0);
            jsonObject.put("msg", "获取失败");
        }
        return jsonObject;
    }

    /**
     * 文件
     *
     * @param file 文件
     *             projectId 项目id
     *             parentId 上级id
     */
    @GetMapping("/getFile")
    @ResponseBody
    public JSONObject getFile(File file) {
        JSONObject jsonObject = new JSONObject();

        try {
            // 设置 catalog 为1，只查询目录
            // 只查询未删除的目录
            file.setFileDel(0);
            if (StringUtils.isEmpty(file.getParentId())) {
                file.setParentId("0");
            }
            List<File> fileList = fileService.findFileList(file);
            jsonObject.put("result", 1);
            jsonObject.put("data", fileList);
            jsonObject.put("file", file);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("result", 0);
            jsonObject.put("msg", "获取失败");
        }
        return jsonObject;
    }

    /**
     * 获取子目录
     */
    @RequestMapping("/getChildFolder")
    @ResponseBody
    public JSONObject getChildFolder(@RequestParam String fileId) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<File> fileList = fileService.findChildFolder(fileId);
            jsonObject.put("result", 1);
            jsonObject.put("data", fileList);
            jsonObject.put("fileId", fileId);
            jsonObject.put("msg", "获取成功");
        } catch (Exception e) {
            log.error("获取子目录失败， {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "获取失败");
        }
        return jsonObject;
    }

    /**
     * 创建文件夹
     *
     * @param projectId  项目id
     * @param parentId   上一级目录id  默认为0
     * @param folderName 文件夹名称
     */
    @PostMapping("/createFolder")
    @ResponseBody
    public JSONObject createFolder(
            @RequestParam String projectId,
            @RequestParam(required = false, defaultValue = "0") String parentId,
            @RequestParam String folderName
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            //判断当前文件夹的名字是否在库中存在
            int result = fileService.findFolderIsExist(folderName,projectId,parentId);
            if(result > 0){
                jsonObject.put("result",0);
                jsonObject.put("msg","文件夹已经存在！");
                return jsonObject;
            }
            fileService.createFolder(projectId, parentId, folderName);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "创建成功");
        } catch (Exception e) {
            log.error("创建文件夹异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "创建失败");
        }


        return jsonObject;
    }

    /**
     * 上传文件
     *
     * @param projectId 项目id
     * @param parentId  上级目录id
     * @param file      文件
     */
    @PostMapping("/upadFile")
    @ResponseBody
    public JSONObject uploadFile(
            @RequestParam String projectId,
            @RequestParam(required = false, defaultValue = "0") String parentId,
            MultipartFile file
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            File f = fileService.uploadFile(projectId, parentId, file);
            jsonObject.put("result", 1);
            jsonObject.put("data", f);
            jsonObject.put("msg", "上传成功");

        } catch (Exception e) {
            log.error("上传文件异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "上传失败");
        }
        return jsonObject;
    }

    /**
     * 上传文件
     *
     * @param projectId 项目id
     */
    @PostMapping("/upload")
    @ResponseBody
    public JSONObject uploadFile(
            @RequestParam String projectId,
            @RequestParam(value = "files",required = false) String files,
            String parentId
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();

            File fileById = fileService.findFileById(parentId);

            //文件为空则不执行
            if(files==null){
                return jsonObject;
            }
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
                    // 用原本的文件名
                    myFile.setFileName(fileName);
                    myFile.setExt(ext);
                    myFile.setProjectId(projectId);
                    myFile.setFileUrl(fileUrl);
                    // 得到上传文件的大小
                    myFile.setSize(size);
                    myFile.setParentId(parentId);
                    myFile.setCatalog(0);
                    myFile.setFileUids(ShiroAuthenticationManager.getUserId());
                    if(FileExt.extMap.get("images").contains(ext)){
                        myFile.setFileThumbnail(fileUrl);
                    }

                    fileService.saveFile(myFile);
                    FileVersion fileVersion = new FileVersion();
                    fileVersion.setFileId(myFile.getFileId());
                    fileVersion.setFileSize(size);
                    fileVersion.setFileUrl(fileUrl);
                    fileVersion.setIsMaster(1);
                    Date time = Calendar.getInstance().getTime();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String format = simpleDateFormat.format(time);
                    fileVersion.setInfo(userEntity.getUserName() + " 上传于 " + format);
                    fileVersionService.saveFileVersion(fileVersion);
                }
            }
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("上传文件异常, {}", e);
            jsonObject.put("result", 0);
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
    @ResponseBody
    public JSONObject uploadModel(
            @RequestParam String projectId,
            @RequestParam(value = "fileCommon") String fileCommon
            ,String fileModel
            ,String parentId
            ,String filename
    ) {
        JSONObject jsonObject = new JSONObject();
        if(fileModel.equals("{}") || fileCommon.equals("{}")){
            jsonObject.put("result",0);
            jsonObject.put("msg","请补充 模型文件或者缩略图!");
            return jsonObject;
        }
        try {
            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            JSONObject array = JSON.parseObject(fileCommon);
            JSONObject object = JSON.parseObject(fileModel);
            String fileName = object.getString("fileName");
            String fileUrl = object.getString("fileUrl");
            String size = object.getString("size");
            String ext = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            // 写库
            File myFile = new File();
            // 用原本的文件名
            myFile.setFileName(filename+ext);
            myFile.setExt(ext);
            myFile.setProjectId(projectId);
            myFile.setFileUrl(fileUrl);
            // 得到上传文件的大小
            myFile.setSize(size);
            myFile.setParentId(parentId);
            myFile.setCatalog(0);
            myFile.setFileUids(ShiroAuthenticationManager.getUserId());
            myFile.setFileThumbnail(array.getString("fileUrl"));
            fileService.saveFile(myFile);
            FileVersion fileVersion = new FileVersion();
            fileVersion.setFileId(myFile.getFileId());
            fileVersion.setFileSize(size);
            fileVersion.setFileUrl(fileUrl);
            fileVersion.setIsMaster(1);
            Date time = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String format = simpleDateFormat.format(time);
            fileVersion.setInfo(userEntity.getUserName() + " 上传于 " + format);
            fileVersionService.saveFileVersion(fileVersion);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("上传文件异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "上传失败");
        }
        return jsonObject;
    }

    /**
     * 更新文件
     */
    @PostMapping("/updateUploadFile")
    @ResponseBody
    public JSONObject updateUploadFile(
            @RequestParam String fileId,
            MultipartFile file
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            // 得到文件名
            String originalFilename = file.getOriginalFilename();
            // 得到原来的文件
            File f = fileService.findFileById(fileId);
            // 设置文件url
            // 获取要创建文件的上级目录实体
            String parentUrl = fileService.findProjectUrl(f.getProjectId());
            // 重置文件名
            String fileName = System.currentTimeMillis() + originalFilename.substring(originalFilename.lastIndexOf("."));
            // 设置文件url
            String fileUrl = parentUrl + fileName;
            // 上传oss，相同的objectName会覆盖
            AliyunOss.uploadInputStream(fileUrl, file.getInputStream());

            // 设置修改后的文件名
            f.setFileName(originalFilename);
            f.setExt(originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase());
            f.setFileUrl(fileUrl);
            f.setSize(FileUtils.convertFileSize(file.getSize()));

            // 更新数据库
            fileService.updateFile(f);
            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            // 修改文件版本
            FileVersion fileVersion = new FileVersion();
            fileVersion.setFileId(f.getFileId());
            fileVersion.setFileUrl(fileUrl);
            fileVersion.setFileSize(FileUtils.convertFileSize(file.getSize()));
            Date time = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String format = simpleDateFormat.format(time);
            fileVersion.setInfo(userEntity.getUserName() + " 上传于 " + format);
            fileVersionService.saveFileVersion(fileVersion);

            // 设置返回数据
            jsonObject.put("result", 1);
            jsonObject.put("data", fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("result", 0);
            jsonObject.put("data", "更新失败");
        }

        return jsonObject;
    }

    /**
     * 修改文件
     *
     * @param file file
     */
    @PostMapping("/updateFile")
    @ResponseBody
    public JSONObject updateFile(File file) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.updateFile(file);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "修改成功");
        } catch (Exception e) {
            log.error("修改文件异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "修改失败");
        }

        return jsonObject;
    }

    /**
     * 删除
     *
     * @param fileId 文件id
     */
    @RequestMapping("/deleteFile")
    @ResponseBody
    public JSONObject deleteFile(@RequestParam String fileId,String projectId) {
        JSONObject jsonObject = new JSONObject();
        JSONObject pushData = new JSONObject();
        try {
            fileService.deleteFileById(fileId);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "删除成功");

            pushData.put("id",fileId);
            pushData.put("type","删除回收站信息");
            messagingTemplate.convertAndSend("/topic/"+projectId,new ServerMessage(JSON.toJSONString(pushData)));
            messagingTemplate.convertAndSend("/topic/"+projectId+"recycleBin",new ServerMessage(JSON.toJSONString(pushData)));
        } catch (Exception e) {
            log.error("删除文件异常, {}", e);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "删除成功");
        }
        return jsonObject;
    }

    /**
     * 恢复
     *
     * @param fileId 文件id
     */
    @RequestMapping("/recoveryFile")
    @ResponseBody
    public JSONObject recoveryFile(@RequestParam String fileId,String projectId) {
        JSONObject jsonObject = new JSONObject();
        JSONObject pushData = new JSONObject();
        try {
            fileService.recoveryFile(fileId);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "恢复成功");

            pushData.put("file",fileService.findFileById(fileId));
            pushData.put("id",fileId);
            pushData.put("type","恢复了文件");
            messagingTemplate.convertAndSend("/topic/"+projectId,new ServerMessage(JSON.toJSONString(pushData)));
            messagingTemplate.convertAndSend("/topic/"+fileId,new ServerMessage(JSON.toJSONString(pushData)));
            pushData.remove("file");
            pushData.put("type","恢复了信息");
            messagingTemplate.convertAndSend("/topic/"+projectId+"recycleBin",new ServerMessage(JSON.toJSONString(pushData)));
        } catch (Exception e) {
            log.error("删除文件异常, {}", e);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "删除成功");
        }
        return jsonObject;
    }
    /**
     * 下载
     *
     * @param fileId 文件
     * @param isPublic 下载的是不是模型文件
     */
    @RequestMapping("/downloadFile")
    @ResponseBody
    public void downloadFile(@RequestParam String fileId, boolean isPublic, HttpServletResponse response){
        try {

            InputStream inputStream = null;
            File file = null;
            // 获取文件
            if(isPublic){
                file = publicFileService.findPublicFileById(fileId);
            } else{
                file = fileService.findFileById(fileId);
            }
            String fileName = file.getFileName();
            String deleteUrl = "";
            // 如果下载的是目录，则打包成zip
//            if (file.getCatalog() == 1) {
//                // 得到临时下载文件目录
//                String tempPath = FileUtils.getTempPath();
//                // 创建文件夹，加时间戳，区分
//                String path = tempPath + "\\" + System.currentTimeMillis() + "\\" + fileName;
//                java.io.File folder = new java.io.File(path);
//                folder.mkdirs();
//                // 设置查询条件
//                List<File> childFile = fileService.findChildFile(file.getProjectId(), file.getFileId(), 0);
//                if (childFile.size() > 0) {
//                    // 下载到临时文件
//                    this.downloadZip(childFile, path,response);
//                }
//
//                // 把临时文件打包成zip下载
//                String downloadPath = path + ".zip";
//                FileOutputStream fos1 = new FileOutputStream(new java.io.File(downloadPath));
//                FileUtils.toZip(path, fos1, true);
//
//                // 开始下载
//
//                // 以流的形式下载文件。
//                inputStream = new BufferedInputStream(new FileInputStream(downloadPath));
//                fileName += ".zip";
//                // 删除临时文件
//                deleteUrl = downloadPath.substring(0, downloadPath.lastIndexOf("\\"));
//            } else {
//                // 文件  在oss上得到流
//            }
            inputStream = AliyunOss.downloadInputStream(file.getFileUrl(),response);
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
        }catch (Exception e){
            throw new SystemException(e);
        }
    }

    @GetMapping("/copyFile.html")
    public String copyFilePage(@RequestParam String[] fileIds, @RequestParam String url, Model model) {
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

        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        // 获取所有参与的项目
        List<Project> projectList = projectService.findProjectByMemberId(userEntity.getId(),0);

        // 获取项目顶级的文件夹
        File file = new File();
        file.setProjectId(projectId);
        file.setParentId("0");
        file.setCatalog(1);
        List<File> fileList = fileService.findFileList(file);

        model.addAttribute("fileMsg", fileNum + "个文件");
        model.addAttribute("folderMsg", folderNum + "文件夹");
        model.addAttribute("projectId", projectId);
        model.addAttribute("projectList", projectList);
        model.addAttribute("fileList", fileList);
        model.addAttribute("url", url);
        model.addAttribute("fileIds", StringUtils.join(fileIds, ","));
        return "copyFile";
    }

    /**
     * 移动文件
     *
     * @param fileIds ids
     */
    @GetMapping("/moveFile.html")
    public String moveFilePage(@RequestParam String[] fileIds, @RequestParam String url, Model model) {
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

        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        // 获取所有参与的项目
        List<Project> projectList = projectService.findProjectByMemberId(userEntity.getId(),0);

        // 获取项目顶级的文件夹
        File file = new File();
        file.setProjectId(projectId);
        file.setParentId("0");
        file.setCatalog(1);
        List<File> fileList = fileService.findFileList(file);

        model.addAttribute("fileMsg", fileNum + "个文件");
        model.addAttribute("folderMsg", folderNum + "文件夹");
        model.addAttribute("projectId", projectId);
        model.addAttribute("projectList", projectList);
        model.addAttribute("fileList", fileList);
        model.addAttribute("url", url);
        model.addAttribute("fileIds", StringUtils.join(fileIds, ","));
        return "moveFile";
    }

    /**
     * 移动文件
     *
     * @param fileIds  文件id数组
     * @param folderId 目标文件夹id
     */
    @RequestMapping("/moveFile")
    @ResponseBody
    public JSONObject moveFile(
            @RequestParam String[] fileIds,
            @RequestParam(defaultValue = "0") String folderId
    ) {
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isEmpty(folderId)) {
            folderId = "0";
        }
        try {
            fileService.moveFile(fileIds, folderId);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "移动成功");
        } catch (Exception e) {
            log.error("移动文件异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "移动失败");
        }
        return jsonObject;
    }

    /**
     * 复制文件
     *
     * @param fileIds  多个文件id
     * @param folderId 目标文件夹id
     */
    @RequestMapping("/copyFile")
    @ResponseBody
    public void copyFile(
            @RequestParam String[] fileIds,
            @RequestParam String folderId
    ) {

        // 父级id
        if (StringUtils.isEmpty(folderId)) {
            folderId = "0";
        }

        for (String fileId : fileIds) {

            // 获取源文件
            File file = fileService.findFileById(fileId);

            if (file.getCatalog() == 1) { // 文件夹
                file.setParentId(folderId);
                String fId = file.getFileId();
                String projectId = file.getProjectId();
                fileService.saveFile(file);
                List<File> childFile = fileService.findChildFile(projectId, fId, 0);
                if (childFile.size() > 0) {
                    Map<String, List<File>> map = new HashMap<>();
                    map.put(file.getFileId(), childFile);
                    this.copyFolder(map);
                }

            } else { // 文件
                this.copyFileSave(file, folderId);
            }
        }

    }

    /**
     * 回收站
     *
     * @param fileIds ids
     * @param projectId 项目id
     */
    @RequestMapping("/moveToRecycleBin")
    @ResponseBody
    public JSONObject moveToRecycleBin(
            @RequestParam String[] fileIds, String projectId
    ) {
        JSONObject jsonObject = new JSONObject();
        JSONObject pushData = new JSONObject();
        try {
            fileService.moveToRecycleBin(fileIds);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "移入回收站成功");

            pushData.put("fileIds",fileIds);
            pushData.put("type","将文件移入了回收站");
            messagingTemplate.convertAndSend("/topic/"+projectId,new ServerMessage(JSON.toJSONString(pushData)));
            for(int i = 0;i < fileIds.length;i++){
                messagingTemplate.convertAndSend("/topic/"+fileIds[i],new ServerMessage(JSON.toJSONString(pushData)));
            }
        } catch (Exception e) {
            log.error("移入回收站异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "移入回收站失败");
        }
        return jsonObject;
    }

    @RequestMapping("/deleteFileTag")
    @ResponseBody
    public JSONObject deleteFileTag(@RequestParam String fileId, @RequestParam String tagId) {
        JSONObject jsonObject = new JSONObject();
        try {
            File file = fileService.findFileById(fileId);
            String[] tagIdArr = file.getTagId().split(",");
            StringBuilder tagIdSB = new StringBuilder();
            String tagIds = "";
            for (String tId : tagIdArr) {
                if (!tagId.equals(tId)) {
                    tagIdSB.append(tId).append(",");
                }
            }
            if (tagIdSB.length() > 0) {
                tagIds = tagIdSB.deleteCharAt(tagIdSB.length() - 1).toString();
            }
            fileService.updateTagId(fileId, tagIds);
            jsonObject.put("result", 1);
            jsonObject.put("message", "移除成功");

        } catch (Exception e) {
            log.error("移除标签异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("message", "移除失败");
        }
        return jsonObject;
    }

    @RequestMapping("/addTag")
    @ResponseBody
    public JSONObject addTag(
            @RequestParam String fileId, // 文件id
            @RequestParam String tagId   // 标签id
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            File file = fileService.findFileById(fileId);
            if (StringUtils.isNotEmpty(file.getTagId())) {
                String[] tagIdArr = file.getTagId().split(",");
                if (CommonUtils.useList(tagIdArr, tagId)) { // 已经存在，移除
                    StringBuilder tagIds = new StringBuilder();
                    for (String tId : tagIdArr) {
                        if (!tagId.equals(tId)) {
                            tagIds.append(tId).append(",");
                        }
                    }
                    if (StringUtils.isNotEmpty(tagIds)) {
                        tagIds.deleteCharAt(tagIds.length() - 1);
                    }
                    fileService.updateTagId(fileId, tagIds.toString());
                    jsonObject.put("result", 2);
                    jsonObject.put("msg", "移除成功");
                } else { // 不存在添加
                    String tagIds = file.getTagId();
                    if (StringUtils.isNotEmpty(file.getTagId())) {
                        tagIds += "," + tagId;
                    } else {
                        tagIds = tagId;
                    }
                    fileService.updateTagId(fileId, tagIds);
                    Tag tag = tagService.findById(Integer.valueOf(tagId));
                    jsonObject.put("result", 1);
                    jsonObject.put("data", tag);
                    jsonObject.put("msg", "添加成功");
                }
            } else {
                String tagIds = file.getTagId();
                if (StringUtils.isNotEmpty(tagIds)) {
                    tagIds += "," + tagId;
                } else {
                    tagIds = tagId;
                }
                fileService.updateTagId(fileId, tagIds);
                Tag tag = tagService.findById(Integer.valueOf(tagId));
                jsonObject.put("result", 1);
                jsonObject.put("data", tag);
                jsonObject.put("msg", "添加成功");
            }


        } catch (Exception e) {
            log.error("添加标签异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "添加失败");
        }
        return jsonObject;
    }

    @RequestMapping("/deleteTag")
    @ResponseBody
    public JSONObject deleteTag(
            @RequestParam String fileId, // 文件id
            @RequestParam String tagId   // 标签id
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            File file = fileService.findFileById(fileId);
            String[] tagIdArr = file.getTagId().split(",");
            StringBuilder tagIds = new StringBuilder();
            for (String tId : tagIdArr) {
                if (!tagId.equals(tId)) {
                    tagIds.append(tId).append(",");
                }
            }
            if (StringUtils.isNotEmpty(tagIds)) {
                tagIds.deleteCharAt(tagIds.length() - 1);
            }
            fileService.updateTagId(fileId, tagIds.toString());
            jsonObject.put("result", 1);
            jsonObject.put("msg", "移除成功");
        } catch (Exception e) {
            log.error("移除标签异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "移除失败");
        }
        return jsonObject;
    }

    /**
     * 查询用户创建的文件
     */
    @RequestMapping("/findByMember")
    @ResponseBody
    public JSONObject findByMember() {
        JSONObject jsonObject = new JSONObject();
        try {
            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            if (userEntity != null) {
                File file = new File();
                file.setMemberId(userEntity.getId());
                file.setFileDel(0);
                List<File> fileList = fileService.findFileList(file);
                jsonObject.put("result", 1);
                jsonObject.put("fileList", fileList);
                jsonObject.put("msg", "获取成功");
            } else {
                jsonObject.put("result", 0);
                jsonObject.put("msg", "登陆过时");
            }
        } catch (Exception e) {
            log.error("获取用户相关文件异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "获取失败");
        }
        return jsonObject;
    }

    public static void main(String[] args) {
        System.out.println(FileUtils.getTempPath());
        String tempPath = FileUtils.getTempPath();
        String fileName = tempPath + "\\" + "test";
        java.io.File folder = new java.io.File(fileName);
        folder.mkdirs();
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
            if (file.getCatalog() == 1) { // 目录
                String url = path + "\\" + file.getFileName();
                java.io.File folder = new java.io.File(url);
                folder.mkdirs();
                fileUrl.add(file);
            } else { // 文件
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

        if (fileUrl.size() > 0) {
            for (File file : fileUrl) {
                List<File> childFile = fileService.findChildFile(file.getProjectId(), file.getFileId(), 0);
                this.downloadZip(childFile, path + "\\" + file.getFileName(),response);
            }
        }
    }

    /**
     * 复制文件夹
     */
    private void copyFolder(Map<String, List<File>> map) {
        Map<String, List<File>> fileMap = new HashMap<>();
        for (Map.Entry<String, List<File>> entry : map.entrySet()) {
            // 得到键，即parentId
            String parentId = entry.getKey();
            // 得到值，即FileList
            List<File> fileList = entry.getValue();
            for (File file : fileList) {
                if (file.getCatalog() == 1) { // 文件夹
                    // 设置父级id
                    file.setParentId(parentId);
                    String fId = file.getFileId();
                    String projectId = file.getProjectId();
                    // 存库
                    fileService.saveFile(file);
                    // 得到此文件夹下一层的子集
                    List<File> childFile = fileService.findChildFile(projectId, fId, 0);
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

    @RequestMapping(value = "/pdfStreamHandeler")
    @ResponseBody
    public void pdfStreamHandeler(@RequestParam String fileId, HttpServletResponse response) {
        File file = fileService.findFileById(fileId);
        try {
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


    @RequestMapping("/viewer.html")
    public String pdfViewer(String fileId, Model model) {
        model.addAttribute("fileId", fileId);
        return "viewer";
    }

    /**
     * 异步获取文件列表
     *
     * @param projectId projectId 项目id
     */
    @PostMapping("/fileList")
    @ResponseBody
    public JSONObject projectList(String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",fileService.findChildFile(projectId,"0",0));
            jsonObject.put("projectId", projectId);
            jsonObject.put("result",1);
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 获取该文件夹下的 所有文件和子文件夹
     */
    @PostMapping("findChildFile")
    @ResponseBody
    public JSONObject findChildFile(String projectId,String fileId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",fileService.findChildFile(projectId,fileId,0));
            jsonObject.put("result",1);
        } catch (Exception e){
            jsonObject.put("result",0);
            log.error("系统异常,文件拉取失败,{}",e);
        }
        return jsonObject;
    }

    /**
     * 文件的评论
     * @param fileId 文件的id
     * @param content 文件的 评论 内容
     * @return
     */
    @PostMapping("chat")
    @ResponseBody
    public JSONObject chat(String fileId,String content){
        JSONObject jsonObject = new JSONObject();
        try {
            Log log = new Log();
            log.setId(IdGen.uuid());
            log.setContent(ShiroAuthenticationManager.getUserEntity().getUserName()+" 说: "+ content);
            log.setLogType(1);
            log.setMemberId(ShiroAuthenticationManager.getUserId());
            log.setPublicId(fileId);
            log.setLogFlag(2);
            log.setCreateTime(System.currentTimeMillis());
            Log log1 = logService.saveLog(log);
            jsonObject.put("result",1);
            //推送数据
            PushType taskPushType = new PushType(TaskLogFunction.A14.getName());
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("fileLog",log1);
            taskPushType.setObject(map);
            //推送至文件的详情界面
            messagingTemplate.convertAndSend("/topic/"+fileId,new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            jsonObject.put("result",0);
            log.error("系统异常,发送失败,{}",e);
        }
        return jsonObject;
    }

    /**
     * 查询出该文件的参与者 和 项目成员
     * @param fileId 文件id
     * @param  projectId 该项目的id
     * @return
     */
    @PostMapping("findProjectMember")
    @ResponseBody
    public JSONObject findProjectMember(String fileId,String projectId){
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
            e.printStackTrace();
            jsonObject.put("result",0);
            jsonObject.put("msg","数据拉取失败!");
            log.error("系统异常,数据拉取失败!");
        }
        return jsonObject;
    }

    /**
     * 添加或者移除文件的参与者
     * @param fileId 文件id
     * @param newJoin 新的参与者id 数组
     * @return
     */
    @PostMapping("addAndRemoveFileJoin")
    @ResponseBody
    public JSONObject addAndRemoveFileJoin(String fileId, String newJoin){
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.addAndRemoveFileJoin(fileId, newJoin);
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,数据拉取失败,{}",e);
            jsonObject.put("msg","系统异常,数据拉取失败!");
            jsonObject.put("result",0);
        }
        return jsonObject;
    }

    /**
     * 查询我参与的文件
     * @return
     */
    @PostMapping("findJoinFile")
    @ResponseBody
    public JSONObject findJoinFile(){
        JSONObject jsonObject = new JSONObject();
        try {
            List<File> listFile = fileService.findJoinFile();
            jsonObject.put("data",listFile);
        } catch (Exception e){
            log.error("系统异常,数据拉取失败,{}",e);
            jsonObject.put("msg","系统异常,数据拉取失败!");
            jsonObject.put("result",0);
        }
        return jsonObject;
    }



    @PostMapping("/childFile")
    @ResponseBody
    public JSONObject childFile(@RequestParam String projectId,@RequestParam String parentId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<File> fileList = fileService.findChildFile(projectId, parentId, 0);
            if(fileList.size()==0){
                jsonObject.put("result",0);
            }else{
                jsonObject.put("result",1);
                jsonObject.put("data",fileList);
                jsonObject.put("parentId",parentId);
            }

        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    @PostMapping("/fileUpload")
    @ResponseBody
    public JSONObject fileUpload(@RequestParam String projectId,
                                 @RequestParam(defaultValue = "0") String parentId,
                                 @RequestParam(value = "files",required = false) String files){
        JSONObject jsonObject = new JSONObject();
        try {

            JSONObject object = JSON.parseObject(files);
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
            myFile.setFileLabel(1);
            myFile.setParentId(parentId);
            myFile.setFileUids(ShiroAuthenticationManager.getUserId());
            fileService.saveFile(myFile);
            jsonObject.put("result",1);
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    @PostMapping("/hasPermission")
    @ResponseBody
    public JSONObject hasPermission(String fileId){
        JSONObject jsonObject = new JSONObject();
        try {
            String userId = ShiroAuthenticationManager.getUserId();
            File file = fileService.findFileById(fileId);
            jsonObject.put("result",1);
            if(file.getFilePrivacy()==1){
                if(Arrays.asList(file.getFileUids().split(",")).contains(userId)){
                    jsonObject.put("hasPermission",true);
                }else{
                    jsonObject.put("hasPermission",false);
                }
            }else{
                jsonObject.put("hasPermission",true);
            }
        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
    }

}
