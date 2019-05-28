package com.art1001.supply.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.art1001.supply.annotation.Log;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.FileVersion;
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
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

//import com.art1001.supply.service.ItemRepository;

/**
 * @author heshaohua
 * @Title: FileApi
 * @Description: TODO 文件API接口
 * @date 2018/9/17 14:34
 **/
@Slf4j
@RequestMapping("files")
@RestController
public class FileApi extends BaseController {

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

    @Resource
    private LogService logService;

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
     * 用户信息的逻辑层接口
     */
    @Resource
    private UserService userService;


  /*  @Autowired
    private ItemRepository itemRepository;*/
    /**
     * 加载项目下文件列表数据
     * @param fileId 文件id
     * @return
     */
    @GetMapping
    public JSONObject fileList(@RequestParam(value = "fileId") String fileId) {
        JSONObject jsonObject = new JSONObject();
        try {

            List<File> fileList = fileService.findChildFile(fileId);
           /* for (File o:fileList
                 ) {
                System.out.print("保存的数据是：：："+o);
                itemRepository.save(o);

            }*/
            jsonObject.put("data", fileList);
            jsonObject.put("parentId",fileId);
            jsonObject.put("result",1);
        } catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 获取一个项目的所有文件夹
     * @return 文件夹信息
     */
    @GetMapping("/{projectId}/folder/all")
    public JSONObject getProjectAllFolder(@PathVariable String projectId){
        try {
            File one = fileService.getOne(new QueryWrapper<File>().select("file_id").eq("project_id", projectId).eq("parent_id", "0"));
            if(one == null){
                return error("该项目不存在!");
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data",fileService.getProjectAllFolder(one.getFileId()));
            jsonObject.put("result",1);
            return jsonObject;
           // return success(fileService.getProjectAllFolder(one.getFileId()));
        } catch (Exception e){
            e.printStackTrace();
            throw new AjaxException("系统异常,数据获取失败!",e);
        }
    }

    /**
     * 获取该文件的所有上级目录的文件id和文件名称(一直到最顶端目录)
     * @param fileId 文件id
     * @return 文件数据结果集
     */
    @GetMapping("/{fileId}/parent/folders")
    public JSONObject getParentFolders(@PathVariable String fileId){
        JSONObject jsonObject = new JSONObject();
        try {
            if(!fileService.checkIsExist(fileId)){
                jsonObject.put("result", 0);
                jsonObject.put("msg", "该文件不存在!");
                return jsonObject;
            }
            jsonObject.put("result", 1);
            jsonObject.put("data", fileService.getParentFolders(fileId));
            return jsonObject;
        } catch (ServiceException e){
            throw new AjaxException(e.getMessage(),e);
        } catch (Exception e){
            e.printStackTrace();
            throw new AjaxException("系统异常,获取目录列表失败!");
        }
    }

    /**
     * 设置文件的隐私模式
     * @param id 文件id
     * @param privacy 隐私模式
     * @return
     */
    @Push(value = PushType.C8,type = 1)
    @PutMapping("/{id}/privacy/{privacy}")
    public JSONObject privacy(@PathVariable String id, @PathVariable int privacy){
        JSONObject jsonObject = new JSONObject();
        try {
            File file = new File();
            file.setFileId(id);
            file.setFilePrivacy(privacy);
            fileService.updateById(file);
            jsonObject.put("result", 1);
            jsonObject.put("msgId", this.getProjectId(id));
            jsonObject.put("data",fileService.getById(id).getParentId());
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常，隐私模式设置失败！",e);
        }
    }

    /**
     * 打开文件详情
     */
    @GetMapping("/{fileId}/details")
    public JSONObject openDownloadFile(@PathVariable(value = "fileId") String fileId) {
        JSONObject jsonObject = new JSONObject();
        try {
            File file = fileService.findFileById(fileId);
            if(file == null){
                jsonObject.put("msg","文件不存在!");
                jsonObject.put("result",1);
                return jsonObject;
            }
            List<FileVersion> fileList = fileVersionService.list(new QueryWrapper<FileVersion>().eq("file_id",fileId));
            //设置关联信息
            bindingService.setBindingInfo(fileId,file,null,null,null);
            jsonObject.put("data",file);
            jsonObject.put("version",fileList);
            //查询出任务的关联信息
            //jsonObject.put("bindings",bindingService.list(new QueryWrapper<Binding>().eq("public_id", fileId)));
            //查询该文件有没有被当前用户收藏
            jsonObject.put("isCollect",publicCollectService.isCollItem(file.getFileId()));
            jsonObject.put("logs",logService.initLog(fileId));
            jsonObject.put("result",1);
        } catch (Exception e){
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
    @Log(PushType.C1)
    @Push(value = PushType.C1)
    @PostMapping("/{parentId}/add")
    public JSONObject createFolder(
            @RequestParam String projectId,
            @PathVariable String parentId,
            @RequestParam String folderName
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.createFolder(projectId,parentId,folderName);
            jsonObject.put("result",1);
            jsonObject.put("msgId",projectId);
            jsonObject.put("data",fileService.findChildFile(parentId));
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
    @Log(PushType.C2)
    @Push(value = PushType.C2,type = 1)
    @PostMapping("/{parentId}/upload")
    public JSONObject uploadFile(
            @PathVariable(value = "parentId") String parentId,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "files") String files,
            @RequestParam(required = false) String publicId

    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.saveFileBatch(projectId,files,parentId,publicId);
            jsonObject.put("result", 1);
            jsonObject.put("msgId",projectId);
            jsonObject.put("data",parentId);
        } catch (Exception e) {
            log.error("上传文件异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 上传模型文件
     */
    @Log(PushType.C3)
    @Push(value = PushType.C3)
    @PostMapping("/{parentId}/model")
    public JSONObject uploadModel(
            @PathVariable(value = "parentId") String parentId,
            @RequestParam(value = "fileCommon") String fileCommon,
            @RequestParam(value = "fileModel") String fileModel,
            @RequestParam(value = "filename") String filename
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            File modelFile = fileService.saveModel(fileModel,fileCommon,null,filename,parentId);
            jsonObject.put("result",1);
            jsonObject.put("msgId",modelFile.getProjectId());
            jsonObject.put("data",new JSONObject().fluentPut("parentId",parentId));
            jsonObject.put("id",modelFile.getFileId());
        } catch (Exception e) {
            log.error("上传文件异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 模糊查询文件
     * @param fileName 文件名称
     * @param projectId 项目id
     * @return
     */
    @GetMapping("/{fileName}/seach")
    public JSONObject scachFiles(@PathVariable String fileName, @RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", fileService.seachByName(fileName,projectId));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,搜索失败!",e);
        }
    }



    /**
     * 更新普通文件版本
     * @param fileId 文件id
     * @param fileObj 文件对象
     * @return
     */
    @Log(PushType.C4)
    @Push(value = PushType.C4,type = 2)
    @PostMapping("/{fileId}/version")
    public JSONObject updateUploadFile(
            @PathVariable(value = "fileId") String fileId,
            @RequestParam(value = "fileObj") String fileObj
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            fileVersionService.update(new FileVersion(),new UpdateWrapper<FileVersion>().set("is_master","0").eq("file_id",fileId));
            File file = fileService.getOne(new QueryWrapper<File>().eq("file_id",fileId));
            JSONObject object = JSON.parseObject(fileObj);
            String fileName = object.getString("fileName");
            String fileUrl = object.getString("fileUrl");
            String size = object.getString("size");
            String ext = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            File myFile = new File();
            // 用原本的文件名
            myFile.setFileName(fileName);
            myFile.setLevel(file.getLevel());
            myFile.setSize(size);
            myFile.setFileUrl(fileUrl);
            myFile.setParentId(file.getParentId());
            myFile.setProjectId(file.getProjectId());
            myFile.setExt(ext);
            if(FileExt.extMap.get("images").contains(ext)){
                myFile.setFileThumbnail(fileUrl);
            }

            fileService.save(myFile);
            //版本历史更新
            FileVersion fileVersion = new FileVersion();
            fileVersion.setFileId(myFile.getFileId());
            fileVersion.setIsMaster(1);
            fileVersion.setInfo(userEntity.getUserName() + " 上传于 " + DateUtils.getDateStr(new Date(),"yyyy-MM-dd HH:mm"));
            fileVersionService.save(fileVersion);
            // 设置返回数据
            jsonObject.put("msg","更新成功");
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
     * 更新模型文件
     * @param fileId 文件id
     */
    @Log(PushType.C5)
    @Push(value = PushType.C5,type = 2)
    @PostMapping("/{fileId}/update_model")
    public JSONObject updateModel(
            @PathVariable(value = "fileId") String fileId,
            @RequestParam(value = "fileCommon") String fileCommon,
            @RequestParam(value = "fileModel") String fileModel,
            @RequestParam(value = "filename") String filename,
            @RequestParam(value = "publicId",required = false) String publicId
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileVersionService.update(new FileVersion(),new UpdateWrapper<FileVersion>().set("is_master","0").eq("file_id",fileId));
            //查询出当前文件
            File file = fileService.getById(fileId);

            UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
            JSONObject array = JSON.parseObject(fileCommon);
            JSONObject object = JSON.parseObject(fileModel);
            String fileName = object.getString("fileName");
            String fileUrl = object.getString("fileUrl");
            String size = object.getString("size");
            File modelFile = new File();
            // 用原本的文件名
            modelFile.setFileName(filename);
            modelFile.setLevel(file.getLevel());
            modelFile.setSize(size);
            modelFile.setFileUrl(fileUrl);
            modelFile.setParentId(file.getParentId());
            modelFile.setExt(fileName.substring(fileName.lastIndexOf(".")).toLowerCase());
            modelFile.setProjectId(file.getProjectId());
            modelFile.setFileThumbnail(array.getString("fileUrl"));
            if(StringUtils.isNotEmpty(publicId)){
                modelFile.setPublicId(publicId);
                modelFile.setPublicLable(1);
            }
            fileService.save(modelFile);
            //版本历史更新
            FileVersion fileVersion = new FileVersion();
            fileVersion.setFileId(modelFile.getFileId());
            fileVersion.setIsMaster(1);
            fileVersion.setInfo(userEntity.getUserName() + " 上传于 " + DateUtils.getDateStr(new Date(),"yyyy-MM-dd HH:mm"));
            fileVersionService.save(fileVersion);
            jsonObject.put("result",1);
            jsonObject.put("msg","更新成功");
        } catch (Exception e) {
            log.error("上传文件异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 判断该文件夹下有没有子文件夹
     * @return
     */
    @GetMapping("/{fileId}/check_folder")
    public JSONObject checkChildFolder(@PathVariable(value = "fileId") String fileId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result",fileService.checkChildFolder(fileId));
        } catch (Exception e) {
            log.error("系统异常,查询失败!", e);
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
    @Log(PushType.C6)
    @Push(value = PushType.C6)
    @DeleteMapping("/{fileId}")
    public JSONObject deleteFile(@PathVariable(value = "fileId") String fileId, @RequestParam(value = "projectId") String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.deleteFileById(fileId);
            jsonObject.put("result", 1);
            jsonObject.put("msgId",projectId+"/recyclebin");
            jsonObject.put("data",new JSONObject().fluentPut("fileId",fileId));
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
    @Log(PushType.C7)
    @Push(value = PushType.C7)
    @PutMapping("/{fileId}/recovery")
    public JSONObject recoveryFile(@PathVariable(value = "fileId") String fileId, @RequestParam(value = "projectId") String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            File file = new File();
            file.setFileId(fileId);
            file.setFileDel(0);
            file.setUpdateTime(System.currentTimeMillis());
            fileService.updateById(file);
            jsonObject.put("result", 1);
            jsonObject.put("msgId",projectId+"/recyclebin");
            jsonObject.put("data",new JSONObject().fluentPut("fileId",fileId));
        } catch (Exception e) {
            log.error("文件恢复失败:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 下载
     * @param fileId 文件id
     */
    @GetMapping("/{fileId}/download")
    public void downloadFile(@PathVariable(value = "fileId") String fileId, HttpServletResponse response){
        try {
            File file = fileService.findFileById(fileId);
            String fileName = file.getFileName();
            InputStream inputStream = AliyunOss.downloadInputStream(file.getFileUrl(),response);
            // 设置响应类型
            //response.setContentType("multipart/form-data");
            // 设置头信息
            // 设置fileName的编码
            fileName = URLEncoder.encode(fileName+file.getExt(), "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setContentType("application/octet-stream");
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] bytes = new byte[1024*1024];
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
     * @param fileIds  文件id数组
     * @param folderId 目标文件夹id
     */
    @Push(value = PushType.C12,type = 2)
    @PutMapping("/{folderId}/m_move")
    public JSONObject moveFile(
            @PathVariable String folderId,
            @RequestParam String[] fileIds,
            @RequestParam String toProjectId
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.moveFile(fileIds, folderId);
            Map<String,Object> maps = new HashMap<>();
            String projectId = getProjectId(fileIds[0]);
            if(Objects.equals(toProjectId,projectId)){
                maps.put(toProjectId,fileService.listByIds(Arrays.asList(fileIds)));
            } else{
                maps.put(projectId,fileIds);
                maps.put(toProjectId, fileService.listByIds(Arrays.asList(fileIds)));
            }
            jsonObject.put("data",maps);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("移动文件异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 复制文件
     * @param fileIds  多个文件id
     * @param folderId 目标文件夹id
     */
    @Push(value = PushType.C10,type = 1)
    @PostMapping("/copy")
    public JSONObject copyFile(
            @RequestParam(value = "fileIds") String[] fileIds,
            @RequestParam(value = "folderId") String folderId
    ) {
        JSONObject jsonObject = new JSONObject();
        List<File> files = new ArrayList<File>();
        try {
            for (String fileId : fileIds) {
                // 获取源文件
                File file = fileService.findFileById(fileId);
                files.add(file);
                jsonObject.put("msgId", file.getProjectId());
                //文件夹处理
                if (file.getCatalog() == 1) {
                    file.setParentId(folderId);
                    String fId = file.getFileId();
                    fileService.save(file);
                    List<File> childFile = fileService.findChildFile(fId);
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
        jsonObject.put("data",files);
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
                    fileService.save(file);
                    // 得到此文件夹下一层的子集
                    List<File> childFile = fileService.findChildFile(fId);
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
        //String ext = fileName.substring(fileName.lastIndexOf("."), fileName.length() - 1);
        String ext = file.getExt();
        // 设置现在文件名
        String newFileName = System.currentTimeMillis() + ext;
        // 设置现在文件objectName
        String newFileUrl = fileUrl.substring(0, fileUrl.lastIndexOf("/")) + "/"+ newFileName;

        // 在oss上复制
        AliyunOss.copyFile(fileUrl, newFileUrl);

        file.setFileId(IdGen.uuid());

        // 设置文件名
        file.setFileName(fileName);
        // 设置url
        file.setFileUrl(newFileUrl);
        // 设置父级id
        file.setParentId(parentId);

        fileService.save(file);

        FileVersion fileVersion = new FileVersion();
        fileVersion.setFileId(file.getFileId());
        fileVersion.setIsMaster(1);
        fileVersion.setInfo(ShiroAuthenticationManager.getUserEntity().getUserName() + " 上传于 " + DateUtils.getDateStr(new Date(),"yyyy-MM-dd HH:mm"));
        fileVersionService.save(fileVersion);
    }

    /**
     * 将文件移至回收站
     * @param fileIds ids
     * @param projectId 项目id
     */
    @Push(value = PushType.C13,type = 1)
    @PutMapping("/{fileIds}/m_recycle")
    public JSONObject moveToRecycleBin(
            @PathVariable(value = "fileIds") String[] fileIds,
            @RequestParam(value = "projectId") String projectId
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.moveToRecycleBin(fileIds);
            jsonObject.put("result", 1);
            jsonObject.put("msgId", projectId);
            jsonObject.put("data", fileIds);
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
     * 获取所有文件夹/子文件夹
     */
    @GetMapping("/{projectId}/folder")
    public JSONObject findChildFile(@PathVariable(value = "projectId") String projectId,
                                    @RequestParam(value = "fileId",required = false) String fileId){
        JSONObject jsonObject = new JSONObject();
        try {
            if(StringUtils.isEmpty(fileId)){
                fileId = fileService.findParentId(projectId);
            }
            List<File> fileList = fileService.list(new QueryWrapper<File>().eq("parent_id", fileId).eq("catalog", "1").orderByDesc("create_time"));
            jsonObject.put("data",fileList);
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
    @GetMapping("/{fileId}/join_info")
    public JSONObject findProjectMember(@PathVariable(value = "fileId") String fileId, @RequestParam(value = "projectId") String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            //查询出该文件的所有参与者id
            String uids = fileService.findJoinId(fileId);
            List<UserEntity> joinInfo = userService.list(new QueryWrapper<UserEntity>().in("u_id", (Object[]) uids.split(",")));
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
    @Push(value = PushType.C9,type = 1)
    @PutMapping("/{fileId}/add_remove_join")
    public JSONObject addAndRemoveFileJoin(@PathVariable(value = "fileId") String fileId, @RequestParam(value = "newJoin") String newJoin){
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.addAndRemoveFileJoin(fileId, newJoin);
            jsonObject.put("msg", getProjectId(fileId));
            jsonObject.put("data",userService.listByIds(Arrays.asList(newJoin.split(","))));
            jsonObject.put("result",1);
            jsonObject.put("msgId", getProjectId(fileId));
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
    @Push(value = PushType.C11)
    @PutMapping("/{fileId}/name")
    public JSONObject changeFileName(@RequestParam(value = "fileName") String fileName, @PathVariable(value = "fileId") String fileId){
        JSONObject jsonObject = new JSONObject();
        try {
            File file = new File();
            file.setFileId(fileId);
            file.setFileName(fileName);
            fileService.updateById(file);
            jsonObject.put("result",1);
            jsonObject.put("msgId",getProjectId(fileId));
            //jsonObject.put("data",fileName);
            jsonObject.put("data",new JSONObject().fluentPut("fileName",fileName).fluentPut("fileId",fileId));
        }catch (Exception e){
            log.error("系统异常,文件名称更新失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }
     /**
     * 根据父文件夹id获取子文件夹的树形图数据
     * @param fileId 父文件夹id
     * @return 子文件夹数据
     */
    @GetMapping("/folder/child")
    public JSONObject getFolderByParentId(@RequestParam String fileId){
        try {
            return success(fileService.findTreeChildFolder(fileId));
        } catch (Exception e){
            e.printStackTrace();
            throw new AjaxException(e);
        }
    }

    /**
     * 获取文件绑定信息
     * @param id 父级id
     * @return 文件信息 (file_id,file_name,ext,catalog)
     */
    @GetMapping("/{id}/bind")
    public JSONObject getBindInfo(@PathVariable String id){
        JSONObject jsonObject = new JSONObject();
        try {
            String parentId = checkIsProjectId(id);
            if(parentId == null){
                jsonObject.put("data",fileService.getBindInfo(id));
            } else {
                jsonObject.put("data",fileService.getBindInfo(parentId));
            }
            jsonObject.put("result",1);
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,获取文件信息失败!",e);
        }
    }

    /**
     * 从其他信息绑定文件
     * @param files 文件信息
     * @return
     */
    @Push(value = PushType.A30,type = 1)
    @PostMapping("/bind_files")
    public JSONObject bindFile(@RequestParam String files,@RequestParam String publicId,@RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
             fileService.bindFile(files);
             jsonObject.put("data", publicId);
             jsonObject.put("result", 1);
             jsonObject.put("id",publicId);
             jsonObject.put("msgId", projectId);
             return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,文件绑定失败!",e);
        }
    }

    @GetMapping("/{fileId}/url")
    public JSONObject getFileUrl(@PathVariable String fileId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result",1 );
            jsonObject.put("data",fileService.getFileUrl(fileId));
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,获取地址失败!",e);
        }
    }

    @GetMapping("/oss/sign")
    public JSONObject ossSign(@RequestParam String dir,HttpServletResponse response){
        JSONObject jsonObject = new JSONObject();
        String accessId = "LTAIP4MyTAbONGJx"; // 请填写您的AccessKeyId。
        String accessKey = "coCyCStZwTPbfu93a3Ax0WiVg3D4EW"; // 请填写您的AccessKeySecret。
        String endpoint = "oss-cn-beijing.aliyuncs.com/"; // 请填写您的 endpoint。
        String bucket = "art1001-bim-5d"; // 请填写您的 bucketname 。
        String host = "https://" + bucket + "." + endpoint; // host的格式为 bucketname.endpoint
        // callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
        String callbackUrl = "";
        //String dir = "user-dir-prefix/"; // 用户上传文件时指定的前缀。

        OSSClient client = new OSSClient(endpoint, accessId, accessKey);
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, String> respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            // respMap.put("expire", formatISO8601Date(expiration));

//            JSONObject jasonCallback = new JSONObject();
//            jasonCallback.put("callbackUrl", callbackUrl);
//            jasonCallback.put("callbackBody",
//                    "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
//            jasonCallback.put("callbackBodyType", "application/x-www-form-urlencoded");
//            String base64CallbackBody = BinaryUtil.toBase64String(jasonCallback.toString().getBytes());
//            respMap.put("callback", base64CallbackBody);

            //JSONObject ja1 = JSONObject.fromObject(respMap);
            // System.out.println(ja1.toString());
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST");
            //response(request, response, ja1.toString());
            jsonObject.put("data", respMap);
            return jsonObject;
        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
            return jsonObject;
        }
    }


    /**
     * 校验传入的参数是文件的id还是项目的id
     * 如果是项目的id那么就返回该项目的根目录id
     * @param id id
     * @return 根目录id 没有为null
     */
    private String checkIsProjectId(String id){
        if(fileService.getOne(new QueryWrapper<File>().select("file_id as fileId").eq("file_id",id)) == null){
            return fileService.findParentId(id);
        } else {
            return null;
        }
    }

    /**
     * 获取文件的项目id
     * @param fileId 文件id
     * @return
     */
    private String getProjectId(String fileId){
        return fileService.getOne(new QueryWrapper<File>().eq("file_id", fileId).select("project_id")).getProjectId();
    }
}
