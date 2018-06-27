package com.art1001.supply.service.file.impl;

import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.file.FileMapper;
import com.art1001.supply.service.file.FileService;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * fileServiceImpl
 */
@Service("fileService1")
public class FileServiceImpl implements FileService {

    /**
     * fileMapper接口
     */
    @Resource
    private FileMapper fileMapper;

    @Resource
    private FileService fileService;

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
    public String uploadFile(String projectId, String parentId, MultipartFile multipartFile) throws Exception {
        // 得到文件名
        String originalFilename = multipartFile.getOriginalFilename();

        // 获取要创建文件的上级目录实体
        String parentUrl = fileService.getPerLevel(projectId, parentId);
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
        return fileUrl;

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
        AliyunOss.createFolder(folderName);
        // 初始化项目
        String[] childFolderNameArr = {"图片", "文档", "视频", "音频"};
        for (String childFolderName : childFolderNameArr) {
            File file = new File();
            // 在OSS上创建目录
            String childFolder = folderName + System.currentTimeMillis() + "/";
            AliyunOss.createFolder(childFolder);
            // 写库
            // 拿到项目的名字作为初始化的文件名
            file.setFileName(childFolderName);
            // 项目id
            file.setProjectId(project.getProjectId());
            // 文件请求路径
            file.setFileUrl(childFolder);

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
    public void createFolder(String projectId, String parentId, String fileName) {
        // 获取父级url
        String parentUrl = fileService.getPerLevel(projectId, parentId);

        // 设置新建目录的url
        String fileUrl = parentUrl + System.currentTimeMillis() + "/";

        // 现在阿里云上创建文件夹
        AliyunOss.createFolder(fileUrl);
        // 存库
        File file = new File();
        // 拿到项目的名字作为初始化的文件名
        file.setFileName(fileName);
        // 设置父级id
        file.setParentId(parentId);
        // 项目id
        file.setProjectId(projectId);
        // 文件请求路径
        file.setFileUrl(fileUrl);

        // 设置是否目录
        file.setCatalog(1);
        fileService.saveFile(file);
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
                    // 设置上级id
                    file.setParentId(folderId);
                    fileService.updateFile(file);
                }
            } else { // 文件
                AliyunOss.moveFile(file.getFileUrl(), folder.getFileUrl());
                // 设置源文件父级id， url
                file.setParentId(folderId);
                file.setFileUrl(folder.getFileUrl() + file.getFileName());
                fileService.updateFile(file);
            }
        }
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
     * @param parentId 当parentId为 0 时，则返回顶级url
     */
    @Override
    public String getPerLevel(String projectId, String parentId) {
        if (parentId.equals("0")) {
            String fileUrl = fileMapper.findTopLevel(projectId);
            return fileUrl.substring(0, fileUrl.indexOf("/") + 1);
        } else {
            File file = fileMapper.findByProjectIdAndFileId(projectId, parentId);
            return file.getFileUrl();
        }
    }

    @Override
    public List<File> findFileList(File file) {
        return fileMapper.findFileList(file);
    }

}