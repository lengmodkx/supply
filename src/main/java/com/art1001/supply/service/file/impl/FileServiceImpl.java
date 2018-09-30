package com.art1001.supply.service.file.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ServiceException;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.art1001.supply.base.Base;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.FileApiBean;
import com.art1001.supply.entity.file.FileVersion;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.mapper.file.FileMapper;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.file.FileVersionService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * fileServiceImpl
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper,File> implements FileService {

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
    private Base base;

    /**
     * 公共模型库 常量定义信息
     */
    private final static String PUBLIC_FILE_NAME = "公共模型库";

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
     * 何少华
     * 通过id删除file数据
     * @param id 文件id
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    @Override
    public void deleteFileById(String id) {
        //删除和该文件相关的所有信息
        base.deleteItemOther(id,BindingConstants.BINDING_FILE_NAME);

        File file = fileMapper.findFileById(id);
        List<FileVersion> byFileId = fileVersionService.findByFileId(id);

        //删除文件的oss版本记录
        byFileId.forEach(item -> {
            AliyunOss.deleteFile(item.getFileUrl());
        });
        // 删除oss数据
        AliyunOss.deleteFile(file.getFileUrl());

        //删除库中的文件版本信息
        fileVersionService.deleteVersionInfoByFileId(id);
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
//        file.setCatalog(0);
//        file.setParentId(parentId);
//        file.setFileUids(ShiroAuthenticationManager.getUserId());
//        file.setFileLabel(0);
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
        file.setFileId(IdGen.uuid());
        // 获取操作用户
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        // 设置操作用户信息
        file.setMemberId(userEntity.getId());
        // 设置时间
        file.setCreateTime(System.currentTimeMillis());
        file.setUpdateTime(System.currentTimeMillis());
        fileMapper.saveFile(file);
    }

    /**
     * 保存文件--文件在前端直接传oss
     * @param files
     * @param chatId
     * @param projectId
     */
    @Override
    public void saveFile(String files,String chatId,String projectId){
        JSONArray array = JSON.parseArray(files);
        for (int i = 0; i < array.size(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            String fileName = jsonObject.getString("fileName");
            String fileUrl = jsonObject.getString("fileUrl");
            String size = jsonObject.getString("size");
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
            //myFile.setCatalog(0);
            myFile.setMemberId(ShiroAuthenticationManager.getUserId());
            myFile.setPublicId(chatId);
            saveFile(myFile);
        }
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
    @Transactional(rollbackFor = Exception.class)
    public String initProjectFolder(Project project) {
        File projectFile = new File();
        projectFile.setFileId(IdGen.uuid());
        projectFile.setFileName(project.getProjectName());
        projectFile.setProjectId(project.getProjectId());
        projectFile.setFileUrl(DateUtils.getDateStr("yyyy-MM-dd hh:mm:ss"));
        projectFile.setParentId("1");
        projectFile.setCatalog(1);
        fileService.saveFile(projectFile);
        // 初始化项目
        String[] childFolderNameArr = {"图片", "文档","模型文件","公共模型库"};
        for (String childFolderName : childFolderNameArr) {
            File file = new File();
            // 写库
            file.setFileName(childFolderName);
            // 项目id
            file.setProjectId(project.getProjectId());
            file.setParentId(projectFile.getFileId());
            file.setLevel(2);
            file.setCatalog(1);
            // 设置是否目录
            fileService.saveFile(file);
        }
        return projectFile.getFileId();
    }

    @Override
    public int findByParentIdAndFileName(String parentId, String fileName) {
        return fileMapper.findByParentIdAndFileName(parentId, fileName);
    }

    @Override
    public File createFolder(String projectId, String parentId, String fileName) {
        //判断当前文件夹的名字是否在库中存在
        int result = fileService.findFolderIsExist(fileName,projectId,parentId);
        if(result > 0){
            throw new ServiceException();
        }
        // 存库
        File file = new File();
        file.setFileId(IdGen.uuid());
        // 拿到项目的名字作为初始化的文件名
        file.setFileName(fileName);
        // 设置父级id
        file.setParentId(parentId);
        // 项目id
        file.setProjectId(projectId);

        // 设置目录
//        file.setCatalog(1);
        fileService.saveFile(file);
        return file;
    }

    /**
     * 查询出该文件夹下的所有子文件夹及文件
     * @param projectId 关联项目id
     * @param parentId 父级id，顶级目录为 0
     * @return
     */
    @Override
    public List<File> findChildFile(String projectId, String parentId) {
        return fileMapper.findChildFile(projectId, parentId);
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
            if (file.getCatalog()==1) { // 文件夹
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
    public void moveToRecycleBin(String[] fileIds) {
        fileMapper.moveToRecycleBin(fileIds);
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
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
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

    /**
     * 查询出我参与的所有文件
     * @return
     */
    @Override
    public List<File> findJoinFile() {
        return fileMapper.findJoinFile(ShiroAuthenticationManager.getUserId());
    }

    /**
     * 查询出在该项目回收站中的文件
     * @param projectId 项目id
     * @return
     */
    @Override
    public List<RecycleBinVO> findRecycleBin(String projectId,String type) {
        return fileMapper.findRecycleBin(projectId,type);
    }

    /**
     * 恢复文件
     * @param fileId 文件的id
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    @Override
    public void recoveryFile(String fileId) {
        fileMapper.recoveryFile(fileId);
        Log log = logService.saveLog(fileId,TaskLogFunction.A28.getName(),1);
    }

    /**
     * 查询某个文件夹下的公开文件
     * @param parentId 父文件夹id
     * @return
     */
    @Override
    public List<File> findPublicFile(String parentId) {
        return fileMapper.findPublicFile(parentId);
    }

    /**
     * 上传文件到公开的文件库
     * @param projectId 项目Id
     * @param parentId
     * @param multipartFile
     */
    @Override
    public File uploadPublicFile(String projectId, String parentId, MultipartFile multipartFile) {
        // 得到文件名
        String originalFilename = multipartFile.getOriginalFilename();
        // 获取要创建文件的上级目录实体
        String parentUrl = fileService.findProjectUrl(projectId);
        // 重置文件名
        String fileName = System.currentTimeMillis() + originalFilename.substring(originalFilename.lastIndexOf("."));
        // 设置文件url
        String fileUrl = parentUrl + fileName;
        // 上传oss
        try {
            AliyunOss.uploadInputStream(fileUrl, multipartFile.getInputStream());
        } catch (Exception e){
            throw new ServiceException(e);
        }
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
        //file.setCatalog(0);
        file.setParentId(parentId);
        file.setFileUids(ShiroAuthenticationManager.getUserId());
        //file.setFileLabel(0);
        fileService.savePublicFile(file);

        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();

        return file;
    }

    /**
     * 保存文件信息到公开文件表
     * @param file 文件信息
     */
    @Override
    public void savePublicFile(File file) {
        file.setFileId(IdGen.uuid());
        fileMapper.savePublicFile(file);
    }

    /**
     * 根据文件id 查询出该文件的 ids
     * @param fileId 文件id
     * @return
     */
    @Override
    public String findUidsByFileId(String fileId) {
        return fileMapper.findUidsByFileId(fileId);
    }

    /**
     * 判断文件夹的名字 是否存在
     * @param folderName 文件夹名字
     * @param projectId 项目id
     * @param parentId 当前目录id
     * @return
     */
    @Override
    public int findFolderIsExist(String folderName, String projectId,String parentId) {
        return fileMapper.findFolderIsExist(folderName,projectId,parentId);
    }

    @Override
    public void deleteFileByPublicId(String publicId) {
        fileMapper.deleteFileByPublicId(publicId);
    }

    @Override
    public List<File> findFileByPublicId(String publicId) {
        return fileMapper.findFileByPublicId(publicId);
    }

    @Override
    public String findFileId() {
        return fileMapper.findFileId();
    }

    /**
     * 加载出该项目的所有文件数据
     * @param projectId 项目id
     * @param fileId 文件id
     * @return
     */
    @Override
    public List<File> findProjectFile(String projectId, String fileId) {
        List<File> fileList = new ArrayList<File>();
        if("0".equals(fileId)){
            fileList = fileService.findChildFile(projectId, fileId);
        } else if(PUBLIC_FILE_NAME.equals(fileMapper.findFileNameById(fileId))){
            //如果用该文件夹名称是 公共模型库  则去公共文件表中查询数据
            fileList = fileService.findPublicFile(fileService.findFileId());
        } else{
            fileList = fileService.findPublicFile(fileId);
        }
        return fileList;
    }

    /**
     * 插入多条文件信息
     * @param files 文件id
     */
    @Override
    public void saveFileBatch(String projectId, String files, String parentId) {
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        List<File> fileList = new ArrayList<File>();
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
                myFile.setFileName(fileName.substring(0,fileName.lastIndexOf(".")));
                myFile.setExt(ext);
                myFile.setProjectId(projectId);
                myFile.setFileUrl(fileUrl);
                // 得到上传文件的大小
                myFile.setSize(size);
                myFile.setParentId(parentId);
               // myFile.setCatalog(0);
                myFile.setMemberId(ShiroAuthenticationManager.getUserId());
                myFile.setFileUids(ShiroAuthenticationManager.getUserId());
                myFile.setCreateTime(System.currentTimeMillis());
                myFile.setFileId(IdGen.uuid());
                if(FileExt.extMap.get("images").contains(ext)){
                    myFile.setFileThumbnail(fileUrl);
                }
                fileList.add(myFile);
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
            fileMapper.saveFileBatch(fileList);
        }
    }

    /**
     * 更新文件版本信息
     * @param file 文件对象
     * @param fileId 更新的文件id
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    @Override
    public String updateVersion(MultipartFile file, String fileId) {
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
            f.setFileName(originalFilename.substring(0,originalFilename.lastIndexOf(".")));
            f.setExt(originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase());
            f.setFileUrl(fileUrl);
            f.setSize(FileUtils.convertFileSize(file.getSize()));

            // 更新数据库
            fileService.updateFile(f);
            // 修改文件版本
            fileVersionService.saveFileVersion(setFileVersion(f));
            return fileUrl;
        } catch (IOException e){
            throw new ServiceException(e);
        }
    }

    /**
     * 封装文件版本信息
     * @param f 文件信息
     * @return 文件版本实体
     */
    public FileVersion setFileVersion(File f){
        FileVersion fileVersion = new FileVersion();
        fileVersion.setFileId(f.getFileId());
        fileVersion.setFileUrl(f.getFileUrl());
        fileVersion.setFileSize(f.getSize());
        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String format = simpleDateFormat.format(time);
        fileVersion.setInfo(ShiroAuthenticationManager.getUserEntity().getUserName() + " 上传于 " + format);
        return fileVersion;
    }

    /**
     * 查询分享部分信息 (项目名称,文件名称,文件后缀名,文件url)
     * @param id 文件id
     * @return
     */
    @Override
    public FileApiBean findFileApiBean(String id) {
        return fileMapper.selectFileApiBean(id);
    }
}