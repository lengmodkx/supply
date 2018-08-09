package com.art1001.supply.service.file.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.FilePushType;
import com.art1001.supply.entity.file.FileVersion;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.task.TaskPushType;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.mapper.file.FileMapper;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.file.FileVersionService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.AliyunOss;
import com.art1001.supply.util.FileUtils;
import com.art1001.supply.util.IdGen;
import org.apache.catalina.User;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * fileServiceImpl
 */
@Service
public class FileServiceImpl implements FileService {

    /**
     * fileMapper接口
     */
    @Resource
    private FileMapper fileMapper;

    @Resource
    private FileService fileService;

    @Resource
    private FileVersionService fileVersionService;

    @Resource
    private UserService userService;

    @Resource
    private LogService logService;

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 查询分页file数据
     *
     * @param pager 分页对象
     * @return
     */
    @Override
    public List<File> findFilePagerList(Pager pager) {
        return fileMapper.findFilePagerList(pager);
    }

    /**
     * 通过id获取单条file数据
     *
     * @param id
     * @return
     */
    @Override
    public File findFileById(String id) {
        return fileMapper.findFileById(id);
    }

    /**
     * 通过id删除file数据
     *
     * @param id 文件id
     */
    @Override
    public void deleteFileById(String id) {
        File file = fileMapper.findFileById(id);
        // 删除oss数据
        AliyunOss.deleteFile(file.getFileUrl());
        fileMapper.deleteFileById(id);
    }

    @Override
    public File uploadFile(String projectId, String parentId, MultipartFile multipartFile) throws Exception {
        // 得到文件名
        String originalFilename = multipartFile.getOriginalFilename();
        // 获取要创建文件的上级目录实体
        String parentUrl = fileService.findProjectUrl(projectId);
        // 重置文件名
        String fileName = System.currentTimeMillis() + originalFilename.substring(originalFilename.lastIndexOf("."));
        // 设置文件url
        String fileUrl = parentUrl + fileName;
        // 上传oss
        AliyunOss.uploadInputStream(fileUrl, multipartFile.getInputStream());
        // 获取后缀名
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();

        // 写库
        File file = new File();
        // 用原本的文件名
        file.setFileName(originalFilename);
        file.setExt(ext);
        file.setProjectId(projectId);
        file.setFileUrl(fileUrl);

        // 得到上传文件的大小
        long contentLength = multipartFile.getSize();
        file.setSize(FileUtils.convertFileSize(contentLength));
        file.setCatalog(0);
        file.setParentId(parentId);
        file.setFileUids(ShiroAuthenticationManager.getUserId());
        file.setFileLabel(0);
        fileService.saveFile(file);

        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        // 修改文件版本
        FileVersion fileVersion = new FileVersion();
        fileVersion.setFileId(file.getFileId());
        fileVersion.setFileUrl(fileUrl);
        fileVersion.setFileSize(FileUtils.convertFileSize(contentLength));
        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String format = simpleDateFormat.format(time);
        fileVersion.setInfo(userEntity.getUserName() + " 上传于 " + format);
        fileVersionService.saveFileVersion(fileVersion);
        return file;

    }

    @Override
    public void updateFile(File file) {
        // 修改操作用户
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        file.setMemberId(userEntity.getId());
        file.setMemberName(userEntity.getUserName());
        file.setMemberImg(userEntity.getUserInfo().getImage());
        // 修改跟新时间
        file.setUpdateTime(System.currentTimeMillis());
        fileMapper.updateFile(file);
    }

    /**
     * 保存file数据
     *
     * @param file
     */
    @Override
    public void saveFile(File file) {
        // 设置uuid
        file.setFileId(IdGen.uuid());
        // 获取操作用户
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        // 设置操作用户信息
        file.setMemberId(userEntity.getId());
        file.setMemberName(userEntity.getUserName());
        file.setMemberImg(userEntity.getUserInfo().getImage());
        // 设置时间
        file.setCreateTime(System.currentTimeMillis());
        file.setUpdateTime(System.currentTimeMillis());
        fileMapper.saveFile(file);
    }

    /**
     * 获取所有file数据
     *
     * @return
     */
    @Override
    public List<File> findFileAllList() {
        return fileMapper.findFileAllList();
    }

    /**
     * 初始化创建项目文件夹
     */
    @Override
    @Transactional
    public void initProjectFolder(Project project) {
        // 在OSS上创建根目录，此目录的名字用时间戳，库中不存此项，此文件夹用来归类。此目的用来辨别相同文件名的不同内容
        String folderName = System.currentTimeMillis() + "/";
        // 在oss中为每个项目创建一个目录
        AliyunOss.createFolder(folderName);
        File projectFile = new File();
        projectFile.setFileName(project.getProjectName());
        projectFile.setProjectId(project.getProjectId());
        projectFile.setFileUrl(folderName);
        projectFile.setParentId("1");
        projectFile.setCatalog(1);
        fileService.saveFile(projectFile);
        // 初始化项目
        String[] childFolderNameArr = {"图片", "文档", "视频", "音频"};
        for (String childFolderName : childFolderNameArr) {
            File file = new File();
            // 写库
            file.setFileName(childFolderName);
            // 项目id
            file.setProjectId(project.getProjectId());
            // 设置是否目录
            file.setCatalog(1);
            fileService.saveFile(file);
        }
    }

    @Override
    public int findByParentIdAndFileName(String parentId, String fileName) {
        return fileMapper.findByParentIdAndFileName(parentId, fileName);
    }

    @Override
    public File createFolder(String projectId, String parentId, String fileName) {
        // 存库
        File file = new File();
        // 拿到项目的名字作为初始化的文件名
        file.setFileName(fileName);
        // 设置父级id
        file.setParentId(parentId);
        // 项目id
        file.setProjectId(projectId);

        // 设置目录
        file.setCatalog(1);
        fileService.saveFile(file);
        return file;
    }

    @Override
    public List<File> findChildFile(String projectId, String parentId, Integer isDel) {
        return fileMapper.findChildFile(projectId, parentId, isDel);
    }

    /**
     * 移动文件
     *
     * @param fileIds   源文件id数组
     * @param folderId 目标目录id
     */
    @Override
    @Transactional
    public void moveFile(String[] fileIds, String folderId) {
        Map<String, Object> map = new HashMap<>();
        map.put("fileIds", fileIds);
        map.put("folderId", folderId);
        fileMapper.moveFile(map);
    }

    /**
     * 复制文件
     *
     * @param fileIds   源文件id数组
     * @param folderId 目标目录id
     */
    @Override
    public void copyFile(String[] fileIds, String folderId) {
        // 获取目录信息
        File folder = fileMapper.findFileById(folderId);
        String destinationFolderName = folder.getFileUrl();
        for (String fileId : fileIds) {
            // 获取源文件
            File file = fileMapper.findFileById(fileId);

            // 修改oss上的路径，成功后改库
            if (file.getCatalog() == 1) { // 文件夹
                ObjectListing listing = AliyunOss.fileList(file.getFileUrl());

                assert listing != null;
                // 得到所有的文件夹，
                for (String commonPrefix : listing.getCommonPrefixes()) {
                    // 得到文件名
                    String destinationObjectName = destinationFolderName + commonPrefix;
                    // 移动oss上的文件夹
                    AliyunOss.createFolder(destinationObjectName);
                }
                // 得到所有的文件
                for (OSSObjectSummary ossObjectSummary : listing.getObjectSummaries()) {
                    // 得到文件名
                    String destinationObjectName = destinationFolderName + ossObjectSummary.getKey();
                    // 移动oss上的文件夹
                    AliyunOss.moveFile(ossObjectSummary.getKey(), destinationObjectName);
                    // 修改库中路径
                    file.setFileUrl(destinationFolderName);
                    // 设置文件上级id
                    file.setParentId(folderId);
                    fileService.saveFile(file);
                }
            } else { // 文件
                AliyunOss.moveFile(file.getFileUrl(), folder.getFileUrl());
                // 设置源文件父级id， url
                file.setParentId(folder.getFileId());
                file.setFileUrl(folder.getFileUrl() + file.getFileName());
                fileService.saveFile(file);
            }
        }
    }

    /**
     * 获取上级url
     * @param projectId 项目id
     */
    @Override
    public String findProjectUrl(String projectId) {
        return fileMapper.findProjectUrl(projectId);
    }

    @Override
    public List<File> findFileList(File file) {
        return fileMapper.findFileList(file);
    }

    @Override
    public List<File> findByIds(String[] fileIds) {
        return fileMapper.findByIds(fileIds);
    }

    @Override
    public void recoveryFile(String[] fileIds) {
        fileMapper.recoveryFile(fileIds);
    }

    @Override
    public List<File> findChildFolder(String fileId) {
        return fileMapper.findChildFolder(fileId);
    }

    @Override
    public List<File> findTopLevel(String projectId) {
        return fileMapper.findByProjectIdAndParentId(projectId, "0");
    }

    @Override
    public void updateTagId(String fileId, String tagIds) {
        fileMapper.updateTagId(fileId, tagIds);
    }

    /**
     * 功能:根据项目id 查询出项目下的所有文件数据
     * @param projectId 项目的id
     * @return 返回该项目下的所有文件数据
     */
    @Override
    public List<File> findFileByProjectId(String projectId) {
        return fileMapper.findFileByProjectId(projectId);
    }

    /**
     * 查询出该文件的所有参与者id
     * @param fileId 文件id
     * @return 参与者id
     */
    @Override
    public String findJoinId(String fileId) {
        return fileMapper.findJoinId(fileId);
    }

    /**
     * 添加或者移除文件的参与者
     * @param fileId 当前参与者id
     * @param newJoinId 新的参与者id
     * @return 影响行数
     */
    @Override
    public void addAndRemoveFileJoin(String fileId, String newJoinId) {
        //查询出当前文件中的 参与者id
        String joinId = fileService.findJoinId(fileId);

        //log日志
        Log log = new Log();
        StringBuilder logContent = new StringBuilder();

        //将数组转换成集合
        List<String> oldJoin = Arrays.asList(joinId.split(","));
        List<String> newJoin = Arrays.asList(newJoinId.split(","));

        //比较 oldJoin 和 newJoin 两个集合的差集  (移除)
        List<String> reduce1 = oldJoin.stream().filter(item -> !newJoin.contains(item)).collect(Collectors.toList());
        if(reduce1 != null && reduce1.size() > 0){
            logContent.append(TaskLogFunction.B.getName()).append(" ");
            for (String uId : reduce1) {
                String userName = userService.findUserNameById(uId);
                logContent.append(userName).append(" ");
            }
        }

        //比较 newJoin  和 oldJoin 两个集合的差集  (添加)
        List<String> reduce2 = newJoin.stream().filter(item -> !oldJoin.contains(item)).collect(Collectors.toList());
        if(reduce2 != null && reduce2.size() > 0){
            logContent.append(TaskLogFunction.C.getName()).append(" ");
            for (String uId : reduce2) {
                String userName = userService.findUserNameById(uId);
                logContent.append(userName).append(" ");
            }
        }

        //如果没有参与者变动直接返回
        if((reduce1 == null && reduce1.size() == 0) && (reduce2 == null && reduce2.size() == 0)){
            return;
        } else{
            File file = new File();
            file.setFileId(fileId);
            file.setFileUids(newJoinId);
            file.setUpdateTime(System.currentTimeMillis());
            fileService.updateFile(file);
            log = logService.saveLog(fileId,logContent.toString(),2);

            //推送信息
            FilePushType filePushType = new FilePushType(TaskLogFunction.A19.getName());
            Map<String,Object> map = new HashMap<String,Object>();
            List<UserEntity> adduser = new ArrayList<UserEntity>();
            map.put("log",log);
            for (String id : reduce2) {
                adduser.add(userService.findById(id));
            }
            map.put("reduce2",reduce2);
            map.put("reduce1",reduce1);
            map.put("adduser",adduser);
            filePushType.setObject(map);
            //推送至文件的详情界面
            messagingTemplate.convertAndSend("/topic/"+fileId,new ServerMessage(JSON.toJSONString(filePushType)));
        }
    }

    /**
     * 清空文件的标签
     * @param fileId 文件的id
     */
    @Override
    public void fileClearTag(String fileId) {
        fileMapper.fileClearTag(fileId);
    }

    /**
     * 根据文件id 查询出文件名
     * @param publicId 文件id
     * @return
     */
    @Override
    public String findFileNameById(String publicId) {
        return fileMapper.findFileNameById(publicId);
    }
}