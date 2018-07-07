package com.art1001.supply.service.file.impl;

import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.FileVersion;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.file.FileMapper;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.file.FileVersionService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.AliyunOss;
import com.art1001.supply.util.FileUtils;
import com.art1001.supply.util.IdGen;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

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
        String fileName = System.currentTimeMillis() + originalFilename.substring(originalFilename.indexOf("."));
        // 设置文件url
        String fileUrl = parentUrl + fileName;
        // 上传oss
        AliyunOss.uploadInputStream(fileUrl, multipartFile.getInputStream());

        // 写库
        File file = new File();
        // 用原本的文件名
        file.setFileName(originalFilename);
        file.setProjectId(projectId);
        file.setFileUrl(fileUrl);

        // 得到上传文件的大小
        long contentLength = multipartFile.getSize();
        file.setSize(FileUtils.convertFileSize(contentLength));
        file.setCatalog(0);
        file.setParentId(parentId);
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
            // 在OSS上创建目录
            // String childFolder = folderName + System.currentTimeMillis() + "/";
            // AliyunOss.createFolder(childFolder);
            // 写库
            // 拿到项目的名字作为初始化的文件名
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

}