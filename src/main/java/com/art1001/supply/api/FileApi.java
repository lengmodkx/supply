package com.art1001.supply.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Log;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushName;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.common.CommonPage;
import com.art1001.supply.common.CommonResult;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.file.*;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.entity.role.ProRoleUser;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.file.FileVersionService;
import com.art1001.supply.service.file.MemberDownloadService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.notice.NoticeService;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.role.ProRoleService;
import com.art1001.supply.service.role.ProRoleUserService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.AliyunOss;
import com.art1001.supply.util.CommonUtils;
import com.art1001.supply.util.DateUtils;
import com.art1001.supply.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author heshaohua
 * @Title: FileApi
 * @Description: TODO 文件API接口
 * @date 2018/9/17 14:34
 **/
@Slf4j
@Validated
@RequestMapping("files")
@RestController
public class FileApi extends BaseController {

    /**
     * 文件逻辑层接口
     */
    @Resource
    private FileService fileService;

    @Resource
    private TaskService taskService;

    /**
     * 用于订阅推送消息
     */
    @Resource
    private NoticeService noticeService;

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


    @Resource
    private TagService tagService;

    /**
     * ElasticSearch 查询接口
     */
    @Autowired
    private FileRepository fileRepository;

    @Resource
    private MemberDownloadService memberDownloadService;


    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ProRoleService proRoleService;

    @Resource
    private ProRoleUserService proRoleUserService;

    /**
     * 加载项目下文件列表数据
     *
     * @param fileId    文件id
     * @param orderType 排序规则 1：创建时间-降序  2：下载量-降序
     */
    @GetMapping
    public JSONObject fileList(@RequestParam(value = "fileId") String fileId,
                               @RequestParam(defaultValue = "1", required = false) Integer orderType) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<File> fileList = fileService.findChildFile(fileId, orderType);
            jsonObject.put("data", fileList);
            jsonObject.put("parentId", fileId);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 改版之后使用
     *
     * @param fileId
     * @return
     */
    @GetMapping("{fileId}")
    public Result<List<File>> fileList1(@PathVariable String fileId,
                                        @RequestParam(defaultValue = "1") Integer current,
                                        @RequestParam(defaultValue = "99999") Integer size) {
        try {
            List<File> fileList = fileService.queryFileList(fileId, current, size);
            return Result.success(fileList);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }


    /**
     * 获取文件树
     *
     * @param fileId
     * @return
     */
    @GetMapping("tree/{fileId}")
    public Result<List<FileTree>> getTree(@PathVariable String fileId) {
        String userId = ShiroAuthenticationManager.getUserId();
        List<FileTree> fileTrees = new ArrayList<>();
        FileTree root = new FileTree(fileId, "0", "项目文件夹", true, "https://art1001-bim-5d.oss-cn-beijing.aliyuncs.com/wx_app_icon/004e879c347daab8eb60e00a938f7dc.png", 1);
        fileTrees.add(0, root);
        //查询项目文件夹
        List<FileTree> trees = fileService.querySubFileList(fileId);
        fileTrees.addAll(trees);

//        File file = fileService.getOne(new QueryWrapper<File>().eq("user_id", userId));
//
//        List<FileTree> fileList = fileService.querySubFileList(file.getFileId());
//        if(fileList!=null && fileList.size()>0){
//            FileTree userRoot = new FileTree(file.getFileId(),fileId,"我的文件夹",false,"https://art1001-bim-5d.oss-cn-beijing.aliyuncs.com/upload/tree-icon/tree3.png",1);
//            fileTrees.add(trees.size()+1,userRoot);
//            fileTrees.addAll(fileList);
//        }else{
//            FileTree userRoot = new FileTree(file.getFileId(),fileId,"我的文件夹",false,"https://art1001-bim-5d.oss-cn-beijing.aliyuncs.com/upload/tree-icon/tree3.png",0);
//            fileTrees.add(trees.size()+1,userRoot);
//        }
        return Result.success(fileTrees);
    }

    /**
     * 获取素材库文件树
     *
     * @return
     */
    @GetMapping("/material/tree")
    public Result<List<FileTree>> getFodderTree() {
        //素材库id
        String fileId = "ef6ba5f0e3584e58a8cc0b2d28286c93";
        List<FileTree> fileTrees = new ArrayList<>();
        FileTree root = new FileTree(fileId, "0", "素材库", true, "https://art1001-bim-5d.oss-cn-beijing.aliyuncs.com/wx_app_icon/004e879c347daab8eb60e00a938f7dc.png", 1);
        fileTrees.add(0, root);
        //查询素材库下的文件夹
        List<FileTree> trees = fileService.querySubFileList(fileId);
        fileTrees.addAll(trees);
        return Result.success(fileTrees);
    }

    /**
     * 获取文件树
     *
     * @param
     * @return
     */
    @GetMapping("/treenode")
    public Result<List<FileTree>> getTreeNode(@RequestParam(value = "id", required = false) String id) {
        //查询项目文件夹
        List<FileTree> trees = fileService.querySubFileList(id);
        return Result.success(trees);
    }

    /**
     * 获取一个项目的所有文件夹
     *
     * @return 文件夹信息
     */
    @GetMapping("/{projectId}/folder/all")
    public JSONObject getProjectAllFolder(@PathVariable String projectId) {
        try {
            File one = fileService.getOne(new QueryWrapper<File>().select("file_id").eq("project_id", projectId).eq("parent_id", "0"));
            List<FileTree> fileTrees = new ArrayList<>();
            FileTree root = new FileTree(one.getFileId(), "0", "项目文件夹", true, "https://art1001-bim-5d.oss-cn-beijing.aliyuncs.com/wx_app_icon/004e879c347daab8eb60e00a938f7dc.png", 1);
            fileTrees.add(0, root);
            List<FileTree> trees = fileService.querySubFileList(one.getFileId());
            fileTrees.addAll(trees);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", fileTrees);
            jsonObject.put("result", 1);
            return jsonObject;
            // return success(fileService.getProjectAllFolder(one.getFileId()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常,数据获取失败!", e);
        }
    }

    /**
     * 获取该文件的所有上级目录的文件id和文件名称(一直到最顶端目录)
     *
     * @param fileId 文件id
     * @return 文件数据结果集
     */
    @GetMapping("/{fileId}/parent/folders")
    public JSONObject getParentFolders(@PathVariable String fileId,
                                       @NotBlank(message = "projectId不能为空!") String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!fileService.checkIsExist(fileId)) {
                jsonObject.put("result", 0);
                jsonObject.put("msg", "目录不存在!");
                return jsonObject;
            }
            jsonObject.put("result", 1);
            jsonObject.put("data", fileService.getParentFolders(fileId, projectId));
            List<File> pathFolders = fileService.getPathFolders(fileId, projectId);
            if (pathFolders.size() > 0 && !"文件库".equals(pathFolders.get(0).getFileName()) && !"公共模型库".equals(pathFolders.get(0).getFileName())) {
                pathFolders.add(fileService.findFileTier(projectId));
            }
            jsonObject.put("data2", pathFolders);
            return jsonObject;
        } catch (ServiceException e) {
            throw new AjaxException(e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常,获取目录列表失败!");
        }
    }

    /**
     * 设置文件的隐私模式
     *
     * @param id      文件id
     * @param privacy 隐私模式
     * @return
     */
    @Push(value = PushType.C8, name = PushName.FILE,type = 1)
    @PutMapping("/{id}/privacy/{privacy}")
    public JSONObject privacy(@PathVariable String id,
                              @PathVariable int privacy,
                              @RequestParam String projectId,
                              @RequestParam String parentId) {
        JSONObject jsonObject = new JSONObject();
        try {
            File file = new File();
            file.setFileId(id);
            file.setFilePrivacy(privacy);
            fileService.updateById(file);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "设置成功");
            jsonObject.put("msgId", projectId);
            jsonObject.put("data", parentId);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常，隐私模式设置失败！", e);
        }
    }

    /**
     * 打开文件详情
     */
    @GetMapping("/{fileId}/details")
    public JSONObject fileDetail(@PathVariable(value = "fileId") String fileId) {
        JSONObject jsonObject = new JSONObject();
        try {
            File file = fileService.findFileById(fileId);
            List<FileVersion> versions = fileVersionService.list(new QueryWrapper<FileVersion>().eq("file_id", fileId));
            file.setVersions(versions);
            file.setLogs(logService.initLog(fileId));
            file.setIsCollect(publicCollectService.isCollItem(file.getFileId()));
            //设置关联信息
            bindingService.setBindingInfo(fileId, file, null, null, null);
            jsonObject.put("data", file);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("系统异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 新建文件夹
     *
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
            fileService.createFolder(projectId, parentId, folderName);
            jsonObject.put("result", 1);
            jsonObject.put("msgId", projectId);
            jsonObject.put("data", parentId);
            return jsonObject;
        } catch (ServiceException e) {
            log.error("文件夹已存在!", e);
            throw new AjaxException(e);
        } catch (Exception e) {
            log.error("创建文件夹失败:", e);
            throw new AjaxException(e);
        }
    }

    /**
     * 上传文件
     *
     * @param projectId 项目id
     */
//    @Log(PushType.C2)
    @Push(value = PushType.C2, name = PushName.FILE,type = 1)
    @PostMapping("/{parentId}/upload")
    public JSONObject uploadFile(
            @PathVariable(value = "parentId") String parentId,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "files") String files,
            @RequestParam(required = false) String publicId

    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.saveFileBatch(projectId, files, parentId, publicId);
            jsonObject.put("result", 1);
            jsonObject.put("msgId", projectId);
            jsonObject.put("data", parentId);
        } catch (Exception e) {
            log.error("上传文件异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    @Push(value = PushType.C2,name = PushName.FILE, type = 1)
    @PostMapping("/uploadFile")
    public JSONObject uploadFile1(
            @RequestParam(value = "parentId") String parentId,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "files") String files,
            @RequestParam(required = false) String publicId

    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.saveFileBatch(projectId, files, parentId, publicId);
            jsonObject.put("result", 1);
            jsonObject.put("msgId", projectId);
            jsonObject.put("data", parentId);
        } catch (Exception e) {
            log.error("上传文件异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    @Push(value = PushType.C3)
    @PostMapping("/uploadModel")
    public JSONObject uploadModel1(
            @PathVariable(value = "parentId") String parentId,
            @RequestParam(value = "fileCommon") String fileCommon,
            @RequestParam(value = "fileModel") String fileModel,
            @RequestParam(value = "filename") String filename
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            File modelFile = fileService.saveModel(fileModel, fileCommon, null, filename, parentId);
            jsonObject.put("result", 1);
            jsonObject.put("msgId", modelFile.getProjectId());
            jsonObject.put("data", new JSONObject().fluentPut("parentId", parentId));
            jsonObject.put("id", modelFile.getFileId());
        } catch (Exception e) {
            log.error("上传文件异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 上传模型文件
     */
    //@Log(PushType.C3)
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
            File modelFile = fileService.saveModel(fileModel, fileCommon, null, filename, parentId);
            jsonObject.put("result", 1);
            jsonObject.put("msgId", modelFile.getProjectId());
            jsonObject.put("data", new JSONObject().fluentPut("parentId", parentId));
            jsonObject.put("id", modelFile.getFileId());
        } catch (Exception e) {
            log.error("上传文件异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更新模型图的缩略图r
     *
     * @param fileId 文件id
     * @param url    略缩图地址
     * @return 结果
     */
    @RequestMapping("/update/thumbnail")
    public Result updateModelThumbnail(@NotNull(message = "模型图文件不能为空") String fileId,
                                       @NotNull(message = "新缩略图地址不能为空") String url) {
        log.info("Update model thumbnail. [{},{}]", fileId, url);

        fileService.updateModelThumbnail(fileId, url);

        return Result.success();
    }

    /**
     * 模糊查询文件
     *
     * @param fileName  文件名称
     * @param projectId 项目id
     * @return
     */
    @GetMapping("/{fileName}/seach")
    public JSONObject scachFiles(@PathVariable String fileName, @RequestParam String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", fileService.seachByName(fileName, projectId));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,搜索失败!", e);
        }
    }


    /**
     * 更新普通文件版本
     *
     * @param fileId    文件id
     * @param projectId 项目id
     * @param mfile     文件对象
     * @return
     */
    @Log(PushType.C4)
    @Push(value = PushType.C4, name = PushName.FILE,type = 1)
    @PostMapping("/{fileId}/version")
    public JSONObject updateUploadFile(
            @PathVariable(value = "fileId") String fileId,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam("mfile") MultipartFile mfile

    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            //现将数据库中文件的主版本都置成0，然后再插入主版本
            FileVersion fileVersion = new FileVersion();
            fileVersion.setFileId(fileId);
            fileVersion.setIsMaster(0);
            fileVersionService.updateById(fileVersion);

            fileService.uploadFile(projectId, fileId, mfile);
            // 设置返回数据
            jsonObject.put("msg", "更新成功");
            jsonObject.put("result", 1);
            jsonObject.put("msgId", projectId);
        } catch (ServiceException e) {
            log.error("文件版本更新失败:", e);
            throw new AjaxException(e);
        } catch (Exception e) {
            log.error("系统异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 更新模型文件
     *
     * @param fileId 文件id
     */
    @Log(PushType.C5)
    @Push(value = PushType.C5, name = PushName.FILE,type = 1)
    @PostMapping("/{parentId}/update_model")
    public JSONObject updateModel(
            @PathVariable(value = "parentId") String fileId,
            @RequestParam(value = "fileCommon") String fileCommon,
            @RequestParam(value = "fileModel") String fileModel,
            @RequestParam(value = "filename") String filename
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileVersionService.update(new FileVersion(), new UpdateWrapper<FileVersion>().set("is_master", "0").eq("file_id", fileId));
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
            modelFile.setIsModel(1);
            fileService.save(modelFile);
            //版本历史更新
            FileVersion fileVersion = new FileVersion();
            fileVersion.setFileId(modelFile.getFileId());
            fileVersion.setIsMaster(1);
            fileVersion.setInfo(userEntity.getUserName() + " 上传于 " + DateUtils.getDateStr(new Date(), "yyyy-MM-dd HH:mm"));
            fileVersionService.save(fileVersion);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "更新成功");
        } catch (Exception e) {
            log.error("上传文件异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 判断该文件夹下有没有子文件夹
     *
     * @return
     */
    @GetMapping("/{fileId}/check_folder")
    public JSONObject checkChildFolder(@PathVariable(value = "fileId") String fileId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", fileService.checkChildFolder(fileId));
        } catch (Exception e) {
            log.error("系统异常,查询失败!", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    /**
     * 删除文件
     *
     * @param fileId    文件id
     * @param projectId
     * @return
     */
    @DeleteMapping("/{fileId}")
    public Result deleteFile(@PathVariable(value = "fileId") String fileId, @RequestParam(value = "projectId") String projectId) {
        try {
            fileService.deleteFileById(fileId);
            return Result.success();
        } catch (Exception e) {
            log.error("删除文件异常:", e);
            throw new AjaxException(e);
        }
    }

    @DeleteMapping("/deletefile")
    public Result deleteFile1(@RequestParam(value = "fileId") String fileId) {
        try {
            fileService.deleteFileById(fileId);
            return Result.success();
        } catch (Exception e) {
            log.error("删除文件异常:", e);
            throw new AjaxException(e);
        }
    }

    /**
     * 从回收站中恢复文件
     *
     * @param fileId    文件id
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
            jsonObject.put("msgId", projectId);
            jsonObject.put("data", new JSONObject().fluentPut("fileId", fileId));
        } catch (Exception e) {
            log.error("文件恢复失败:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 复制和移动文件时 获取弹框数据
     *
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
            List<Project> projectList = projectService.findProjectByMemberId(ShiroAuthenticationManager.getUserId(), 0);

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
        } catch (Exception e) {
            log.error("系统异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 移动文件/文件夹
     *
     * @param folderId    目标文件夹id
     * @param parentId    当前文件/文件夹父id
     * @param projectId   当前项目id
     * @param fileIds     要移动的文件id数组
     * @param toProjectId 目标项目id
     * @return
     */
    @Push(value = PushType.C12, name = PushName.FILE,type = 2)
    @PutMapping("/{folderId}/m_move")
    public JSONObject moveFile(
            @PathVariable String folderId,
            @RequestParam String parentId,
            @RequestParam String projectId,
            @RequestParam String fileIds,
            @RequestParam String toProjectId
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.moveFile(toProjectId, Arrays.asList(fileIds.split(",")), folderId);
            Map<String, Object> maps = new HashMap<>();
            maps.put(projectId, parentId);
            if (Objects.equals(toProjectId, projectId)) {
                maps.put(projectId, folderId);
            } else {
                maps.put(toProjectId, folderId);
            }
            jsonObject.put("data", maps);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("移动文件异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 复制文件/文件夹
     *
     * @param folderId    目标文件夹id
     * @param projectId   当前项目id
     * @param fileIds     要移动的文件id数组
     * @param toProjectId 目标项目id
     * @return
     */
    @Push(value = PushType.C10, name = PushName.FILE,type = 1)
    @PostMapping("/{folderId}/copy")
    public JSONObject copyFile(
            @PathVariable String folderId,
            @RequestParam String projectId,
            @RequestParam String fileIds,
            @RequestParam String toProjectId
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.copyFile(projectId, Arrays.asList(fileIds.split(",")), folderId);
            Map<String, Object> maps = new HashMap<>();

            if (Objects.equals(toProjectId, projectId)) {
                maps.put(projectId, folderId);
            } else {
                maps.put(toProjectId, folderId);
            }
            jsonObject.put("data", maps);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("系统异常:", e);
            throw new AjaxException(e);
        }

        return jsonObject;
    }

    /**
     * 将文件移至回收站
     *
     * @param fileIds   ids
     * @param projectId 项目id
     */
    @Push(value = PushType.C13, name = PushName.FILE,type = 1)
    @PutMapping("/{fileIds}/m_recycle")
    public JSONObject moveToRecycleBin(
            @PathVariable(value = "fileIds") String fileIds,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam String parentId
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.moveToRecycleBin(fileIds.split(","));
            jsonObject.put("result", 1);
            jsonObject.put("msgId", projectId);
            jsonObject.put("data", parentId);
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
                                    @RequestParam(value = "fileId", required = false) String fileId) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (StringUtils.isEmpty(fileId)) {
                fileId = fileService.findParentId(projectId);
            }
            List<File> fileList = fileService.list(new QueryWrapper<File>().eq("parent_id", fileId).eq("catalog", "1").orderByDesc("create_time"));
            jsonObject.put("data", fileList);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("系统异常!", e);
            throw new SystemException(e);
        }
        return jsonObject;
    }

    /**
     * 查询出该文件的参与者 和 项目成员
     *
     * @param fileId    文件id
     * @param projectId 该项目的id
     * @return
     */
    @GetMapping("/{fileId}/join_info")
    public JSONObject findProjectMember(@PathVariable(value = "fileId") String fileId, @RequestParam(value = "projectId") String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            //查询出该文件的所有参与者id
            String uids = fileService.findJoinId(fileId);
            List<UserEntity> joinInfo = userService.list(new QueryWrapper<UserEntity>().in("u_id", (Object[]) uids.split(",")));
            List<UserEntity> projectMembers = userService.findProjectAllMember(projectId);
            //比较项目全部成员集合 和 文件参与者集合的差集
            List<UserEntity> reduce1 = projectMembers.stream().filter(item -> !joinInfo.contains(item)).collect(Collectors.toList());
            jsonObject.put("joinInfo", joinInfo);
            jsonObject.put("projectMember", reduce1);
        } catch (Exception e) {
            log.error("系统异常!", e);
            throw new SystemException(e);
        }
        return jsonObject;
    }

    /**
     * 添加或者移除文件的参与者
     *
     * @param fileId  文件id
     * @param newJoin 新的参与者id 数组
     * @return
     */
    @Push(value = PushType.C9, name = PushName.FILE,type = 1)
    @PutMapping("/{fileId}/add_remove_join")
    public JSONObject addAndRemoveFileJoin(@PathVariable(value = "fileId") String fileId,
                                           @RequestParam(value = "newJoin") String newJoin) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.addAndRemoveFileJoin(fileId, newJoin);
            jsonObject.put("msg", getProjectId(fileId));
            jsonObject.put("data", userService.listByIds(Arrays.asList(newJoin.split(","))));
            jsonObject.put("result", 1);
            jsonObject.put("msgId", getProjectId(fileId));
        } catch (Exception e) {
            log.error("系统异常,数据拉取失败:{}", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 查询我参与的文件
     *
     * @return
     */
    @GetMapping("/my_join")
    public JSONObject findJoinFile() {
        JSONObject jsonObject = new JSONObject();
        try {
            List<File> listFile = fileService.findJoinFile();
            if (CommonUtils.listIsEmpty(listFile)) {
                jsonObject.put("result", 1);
                jsonObject.put("data", "无数据");
                return jsonObject;
            }
            jsonObject.put("data", listFile);
        } catch (Exception e) {
            log.error("系统异常,数据拉取失败,{}", e);
            throw new SystemException(e);
        }
        return jsonObject;
    }

    /**
     * 修改文件名称
     *
     * @param fileName 文件名称
     * @param fileId   文件id
     * @return
     */
    @Push(value = PushType.C11)
    @PutMapping("/{fileId}/name")
    public JSONObject changeFileName(@RequestParam(value = "fileName") @NotBlank(message = "文件名称不能为空！") String fileName,
                                     @PathVariable(value = "fileId") @NotBlank(message = "fileId不能为空！") String fileId) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.updateFileName(fileId, fileName);
            jsonObject.put("result", 1);
            jsonObject.put("msgId", getProjectId(fileId));
            jsonObject.put("data", new JSONObject().fluentPut("fileName", fileName).fluentPut("fileId", fileId));
        } catch (Exception e) {
            log.error("系统异常,文件名称更新失败:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 获取项目中的所有文件夹树形图数据
     *
     * @param fileId 父文件夹id
     * @return 子文件夹数据
     */
    @GetMapping("/folder/child")
    public JSONObject getFolderByParentId(@RequestParam String fileId) {
        try {
            return success(fileService.findTreeChildFolder(fileId));
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException(e);
        }
    }

    /**
     * 获取文件绑定信息
     *
     * @param id 父级id
     * @return 文件信息 (file_id,file_name,ext,catalog)
     */
    @GetMapping("/{id}/bind")
    public JSONObject getBindInfo(@PathVariable String id) {
        JSONObject jsonObject = new JSONObject();
        try {
            String parentId = checkIsProjectId(id);
            if (parentId == null) {
                jsonObject.put("data", fileService.getBindInfo(id));
            } else {
                jsonObject.put("data", fileService.getBindInfo(parentId));
            }
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,获取文件信息失败!", e);
        }
    }

    /**
     * 从其他信息绑定文件
     *
     * @param files 文件信息
     * @return
     */
    @Push(value = PushType.A30, name = PushName.FILE,type = 1)
    @PostMapping("/bind_files")
    public JSONObject bindFile(@RequestParam String files, @RequestParam String publicId, @RequestParam String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.bindFile(files);
            String executorId = taskService.getExecutorByTaskId(publicId);
            if (StringUtils.isNotEmpty(executorId)) {
                noticeService.toUser(executorId, PushType.Y1.getName(), taskService.getTaskNameById(publicId) + "中有人上传了新文件。");
            }
            jsonObject.put("data", publicId);
            jsonObject.put("result", 1);
            jsonObject.put("id", publicId);
            jsonObject.put("msgId", projectId);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,文件绑定失败!", e);
        }
    }

    @GetMapping("/{fileId}/url")
    public JSONObject getFileUrl(@PathVariable String fileId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", fileService.getFileUrl(fileId));
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,获取地址失败!", e);
        }
    }

    /**
     * 校验传入的参数是文件的id还是项目的id
     * 如果是项目的id那么就返回该项目的根目录id
     *
     * @param id id
     * @return 根目录id 没有为null
     */
    private String checkIsProjectId(String id) {
        if (fileService.getOne(new QueryWrapper<File>().select("file_id as fileId").eq("file_id", id)) == null) {
            return fileService.findParentId(id);
        } else {
            return null;
        }
    }

    /**
     * 获取文件的项目id
     *
     * @param fileId 文件id
     * @return
     */
    private String getProjectId(String fileId) {
        return fileService.getOne(new QueryWrapper<File>().eq("file_id", fileId).select("project_id")).getProjectId();
    }

    /*
     * 在文件系统创建标签并绑定文件
     * */
    @Push(value = PushType.E3, name = PushName.FILE,type = 1)
    @PostMapping("/addTagBindFile")
    public JSONObject addTagBindFile(@RequestParam(value = "tagName") String tagName,
                                     @RequestParam(value = "bgColor") String bgColor,
                                     @RequestParam(value = "publicId") String publicId,
                                     @RequestParam(value = "publicType") String publicType,
                                     @RequestParam(value = "projectId") String projectId
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            Tag tag = new Tag();
            tag.setTagName(tagName);
            tag.setBgColor(bgColor);
            tag.setProjectId(projectId);
            if (tagService.addAndBind(tag, publicId, publicType)) {
                jsonObject.put("result", 1);
                jsonObject.put("data", new JSONObject().fluentPut("publicType", publicType).fluentPut("publicId", publicId));
                jsonObject.put("msgId", projectId);
            }
        } catch (ServiceException e) {
            throw new AjaxException(e.getMessage(), e);
        } catch (Exception e) {
            throw new AjaxException("系统异常,添加标签失败!", e);
        }
        return jsonObject;
    }

    /**
     * 文件搜索
     *
     * @param pageable 分页
     */
    @GetMapping("/{fileName}/search_file")
    public JSONObject elSearch(@NotBlank(message = "搜索名称不能为空!") @PathVariable String fileName,
                               @RequestParam @NotBlank(message = "projectId不能为空!") String projectId, Pageable pageable) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", 1);
        jsonObject.put("data", fileService.searchFile(fileName, projectId, pageable));
        return jsonObject;
    }


    /**
     * 搜索素材库
     *
     * @param fileName 文件名
     * @return 信息
     */
    @GetMapping("/{fileName}/material_base_search")
    public JSONObject materialBaseSearch(@NotBlank(message = "搜索名称不能为空!") @PathVariable String fileName, Pageable pageable) {
        JSONObject jsonObject = new JSONObject();
        //将数据库数据更新到ES
        //this.getFileToElastic();
        jsonObject.put("result", 1);
        jsonObject.put("totle", fileService.getSucaiTotle(fileName));
        jsonObject.put("data", fileService.searchMaterialBaseFile(fileName, pageable));

        jsonObject.put("page", pageable.getPageNumber());

        return jsonObject;
    }

    /**
     * 获取素材库数据
     *
     * @param folderId 目录id
     * @return 信息
     */
    @GetMapping("{folderId}/material")
    public JSONObject getMaterialBaseFile(@PathVariable String folderId, Page pageable, @RequestParam(required = false) Boolean downloadCount) {

        return fileService.getMateriaBaseFile(folderId, pageable, downloadCount);
    }


    /**
     * 标记或者取消 重要文件标识
     *
     * @param fileId 文件id
     * @param label  标识(1.标记   0.取消)
     * @return 是否成功
     */
    @Push(value = PushType.C14)
    @PutMapping("/sing_cancel/important_label")
    public JSONObject signOrCancelImportantLabel(@RequestParam @NotBlank(message = "fileId不能为空!") String fileId,
                                                 @RequestParam @Range(max = 1, message = "参数值范围错误!") Integer label) {
        JSONObject jsonObject = new JSONObject();
        fileService.signImportant(fileId, label);
        jsonObject.put("result", 1);
        jsonObject.put("msgId", fileService.findFileById(fileId).getProjectId());
        return jsonObject;
    }

    /**
     * 更新ElasticSearch
     */
    @GetMapping("/getFileToElastic/to")
    public void getFileToElastic() {
        try {
            List<File> allFile = fileService.findList();
            for (File f : allFile) {
                //保存到ElasticSearch
                fileRepository.save(f);
            }
        } catch (Exception e) {
            throw new AjaxException("系统异常,更新elasticSearch失败!", e);
        }
    }


    @RequestMapping("/aaa")
    public Result getSuCai() {
        return Result.success(fileService.getSucaiId("ef6ba5f0e3584e58a8cc0b2d28286c93"));
    }

    @RequestMapping("/batch/download")
    public void batchDownLoad(@RequestParam(value = "fileIds") String fileIds, HttpServletResponse response, HttpServletRequest request) {
        String[] ids = fileIds.split(",");
        List<File> files = fileService.findByIds(ids);
        try {
            //处理单个文件的情况
            if (files.size() == 1) {
                if (files.get(0).getCatalog() == 0) {
                    fileService.downloadSingleFile(files.get(0), response);
                } else {
                    fileService.downloadSingleFolder(files.get(0), response);
                }
            } else {//处理多个文件及文件夹
                fileService.batchDownLoad(files, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载
     *
     * @param fileId 文件id
     */
    @GetMapping("/{fileId}/download")
    public void downloadFile(@PathVariable(value = "fileId") String fileId, HttpServletResponse response) {
        try {
            File file = fileService.getOne(new QueryWrapper<File>().eq("file_id", fileId));

            String fileName = file.getFileName();
            fileService.updateDownloadCount(fileId);
            InputStream inputStream = AliyunOss.downloadInputStream(file.getFileUrl(), response);
            // 设置响应类型
            //response.setContentType("multipart/form-data");
            // 设置头信息
            // 设置fileName的编码
            fileName = URLEncoder.encode(fileName + file.getExt(), "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setContentType("application/octet-stream");
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copy(inputStream, outputStream);

            memberDownloadService.save(MemberDownload.builder()
                    .memberId(ShiroAuthenticationManager.getUserId())
                    .fileId(fileId).isDelete(file.getFileDel())
                    .downloadTime(String.valueOf(System.currentTimeMillis())).build());

            outputStream.close();
            inputStream.close();
        } catch (NullPointerException e) {
            log.error("系统异常,文件不存在:", e);
            throw new AjaxException("文件不存在");
        } catch (Exception e) {
            log.error("系统异常:", e);
            throw new AjaxException(e);
        }
    }

    //文件设置隐私模式
    @Push(value = PushType.C8, name = PushName.FILE,type = 1)
    @PutMapping("/folder/look")
    public JSONObject setFolderLook(@RequestParam String folderId,
                                    @RequestParam String userIds,
                                    @RequestParam String projectId,
                                    @RequestParam String parentId) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (StringUtils.isNotEmpty(userIds)) {
                File file = fileService.findFileById(folderId);
                if (file.getFileUids() == null) {
                    file.setFileUids(userIds);
                } else {
                    file.setFileUids(file.getFileUids() + "," + userIds);
                }
                fileService.updateById(file);
            }
            jsonObject.put("result", 1);
            jsonObject.put("msgId", projectId);
            jsonObject.put("data", parentId);
            return jsonObject;
        } catch (Exception e) {
            throw new SystemException(e);
        }
    }

    @Push(value = PushType.C8, name = PushName.FILE,type = 1)
    @PutMapping("/folder/delUser")
    public JSONObject setFolderDelUser(@RequestParam String folderId,
                                       @RequestParam String userIds,
                                       @RequestParam String projectId,
                                       @RequestParam String parentId) {
        JSONObject jsonObject = new JSONObject();
        try {

            File file = fileService.findFileById(folderId);
            if (!StringUtils.isEmpty(file.getFileUids())) {
                List<String> ids = Arrays.asList(file.getFileUids().split(","));
                List arrList = new ArrayList(ids);
                for (int i = 0; i < arrList.size(); i++) {
                    if (arrList.get(i).equals(userIds)) {
                        arrList.remove(i);
                        i--;
                    }
                }
//                    file.setFileUids(StringUtils.join(ids, ","));
                file.setFileUids(StringUtils.join(arrList, ","));
            }
            fileService.updateById(file);

            jsonObject.put("result", 1);
            jsonObject.put("msgId", projectId);
            jsonObject.put("data", parentId);
            return jsonObject;
        } catch (Exception e) {
            throw new SystemException(e);
        }
    }

    /**
     * 获取可以看某个文件夹的所有可见成员
     *
     * @param folderId 文件id
     * @return
     */
    @GetMapping("/{folderId}/member")
    public JSONObject getFileMembers(@PathVariable("folderId") String folderId) {
        JSONObject jsonObject = new JSONObject();
        try {
            File file = fileService.findFileById(folderId);
            List<UserEntity> fileId = new ArrayList<>();
            if (StringUtils.isNotEmpty(file.getFileUids())) {
                List<String> ids = Arrays.asList(file.getFileUids().split(","));
                ids.forEach(id -> {
                    fileId.add(userService.findById(id));
                });
            }
            jsonObject.put("data", fileId);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "获取成功!");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return jsonObject;
    }


    //更改模型文件路径
    @PostMapping("model/change")
    public JSONObject modelChange(@RequestParam String fileId) {
        JSONObject jsonObject = new JSONObject();
        File file = fileService.getById(fileId);
        if (StringUtils.isNotEmpty(file.getModelUrl())) {
            jsonObject.put("result", 1);
            return jsonObject;
        }
        String url = "http://www.aldbim.com/model/files/model/change?fileId=" + fileId;
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@org.jetbrains.annotations.NotNull Call call, @org.jetbrains.annotations.NotNull IOException e) {

            }

            @Override
            public void onResponse(@org.jetbrains.annotations.NotNull Call call, @org.jetbrains.annotations.NotNull Response response) throws IOException {

            }
        });
        jsonObject.put("result", 1);
        return jsonObject;
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param:
     * @return:
     * @Description: 获取用户已下载信息
     * @create: 10:15 2020/5/14
     */
    @GetMapping("/getIsDownload/{memberId}")
    public JSONObject getIsDownload(@PathVariable String memberId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", memberDownloadService.getIsDownload(memberId));
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常，请稍后再试");
        }
    }

}
