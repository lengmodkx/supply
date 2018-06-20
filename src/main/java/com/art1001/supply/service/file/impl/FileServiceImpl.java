package com.art1001.supply.service.file.impl;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.file.FileMapper;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.util.AliyunOss;
import com.art1001.supply.util.FileUtils;
import com.art1001.supply.util.IdGen;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    public String uploadFile(String projectId, String parentId, MultipartFile multipartFile, HttpServletRequest request) throws Exception {
        // 得到文件名
        String fileName = multipartFile.getOriginalFilename();
        // 查询本层目录是否有此文件名存在
        int byParentIdAndFileName = fileMapper.findByParentIdAndFileName(parentId, fileName);
        if (byParentIdAndFileName > 0) {
            // 名字重复加时间戳
            // fileName = fileName.substring(0, fileName.indexOf(".")) + "-" + System.currentTimeMillis() + fileName.substring(fileName.indexOf("."));
            StringBuilder sb = new StringBuilder(fileName);
            sb.insert(fileName.indexOf("."), "-" + System.currentTimeMillis());
            fileName = sb.toString();
        }

        // 获取要创建文件的上级目录实体
        File parentFile = fileMapper.findFileById(parentId);
        Map<String, Object> map = FileUtils.ossFileUpload(multipartFile, parentFile.getFileUrl(), fileName);

        // 得到文件的访问路径
        String fileUrl = (String) map.get("fileUrl");

        // 写库
        File file = new File();
        file.setFileId(IdGen.uuid());
        file.setFileName(fileName);
        file.setProjectId(parentFile.getProjectId());
        file.setFileUrl(fileUrl);
        // 获取用户信息
        // TODO: 2018/6/14 在缓存中得到用户
        // UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        UserEntity userEntity = new UserEntity();
        userEntity.setId("6cb972b67e8a4a3980fb9cd5d5a89cd2");
        userEntity.setUserName("飞哥");
        userEntity.getUserInfo().setImage("www.baudu.com/image");

        file.setMemberId(userEntity.getId());
        file.setMemberName(userEntity.getUserName());
        file.setMemberImg(userEntity.getUserInfo().getImage());
        // 得到上传文件的大小
        int contentLength = request.getContentLength();
        file.setSize(FileUtils.convertFileSize(contentLength));
        file.setCatalog(0);
        file.setParentId(parentId);
        file.setCreateTime(System.currentTimeMillis());
        file.setUpdateTime(System.currentTimeMillis());
        fileMapper.saveFile(file);
        return fileUrl;

    }

    @Override
    public void updateFile(File file) {
        fileMapper.updateFile(file);
    }

    /**
     * 保存file数据
     *
     * @param file
     */
    @Override
    public void saveFile(File file) {
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

    @Override
    @Transactional
    public void initProjectFolder(Project project) {
        // 在OSS上创建目录
        String folderName = project.getProjectName() + "-" + System.currentTimeMillis() + "/";
        AliyunOss.createFolder(folderName);
        // 写库
        File file = new File();
        file.setFileId(IdGen.uuid());
        file.setFileName(folderName.replace("/", ""));
        file.setProjectId(project.getProjectId());
        file.setFileUrl(folderName);
        // 获取用户信息
        // TODO: 2018/6/14 在缓存中得到用户
//		UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        UserEntity userEntity = new UserEntity();
        userEntity.setId("6cb972b67e8a4a3980fb9cd5d5a89cd2");
        userEntity.setUserName("飞哥");
        userEntity.getUserInfo().setImage("www.baudu.com/image");

        file.setMemberId(userEntity.getId());
        file.setMemberName(userEntity.getUserName());
        file.setMemberImg(userEntity.getUserInfo().getImage());
        file.setCatalog(1);
        file.setCreateTime(System.currentTimeMillis());
        file.setUpdateTime(System.currentTimeMillis());
        fileMapper.saveFile(file);
        // 创建成功后创建子目录
        String parentId = file.getFileId();
        String[] childFolderNameArr = {"图片", "文档", "视频", "音频"};
        for (String childFolderName : childFolderNameArr) {
            // 在OSS上创建目录
            String childFolder = folderName + childFolderName + "/";
            AliyunOss.createFolder(childFolder);
            file.setFileId(IdGen.uuid());
            file.setFileName(childFolderName);
            file.setFileUrl(childFolder);
            file.setParentId(parentId);
            fileMapper.saveFile(file);
        }
    }

    @Override
    public int findByParentIdAndFileName(String parentId, String fileName) {
        return fileMapper.findByParentIdAndFileName(parentId, fileName);
    }

    @Override
    public void createFolder(String parentId, String fileName) {
        // 获取要创建目录的目录实体
        File parentFile = fileMapper.findFileById(parentId);

        // 设置目录的url
        String folderName = parentFile.getFileUrl() + fileName + "/";
        // 在oss中创建
        AliyunOss.createFolder(folderName);

        // 写库
        File file = new File();
        file.setFileId(IdGen.uuid());
        file.setFileName(fileName);
        file.setProjectId(parentFile.getProjectId());
        file.setFileUrl(folderName);
        // 获取用户信息
        // TODO: 2018/6/14 在缓存中得到用户
        // UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        UserEntity userEntity = new UserEntity();
        userEntity.setId("6cb972b67e8a4a3980fb9cd5d5a89cd2");
        userEntity.setUserName("飞哥");
        userEntity.getUserInfo().setImage("www.baudu.com/image");

        file.setMemberId(userEntity.getId());
        file.setMemberName(userEntity.getUserName());
        file.setMemberImg(userEntity.getUserInfo().getImage());
        file.setCatalog(1);
        file.setParentId(parentId);
        file.setCreateTime(System.currentTimeMillis());
        file.setUpdateTime(System.currentTimeMillis());
        fileMapper.saveFile(file);
    }

    @Override
    public void uploadFile(File file) {
        file.setFileId(IdGen.uuid());
        // 获取用户信息
        // TODO: 2018/6/14 在缓存中得到用户
        // UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        UserEntity userEntity = new UserEntity();
        userEntity.setId("6cb972b67e8a4a3980fb9cd5d5a89cd2");
        userEntity.setUserName("飞哥");
        userEntity.getUserInfo().setImage("www.baudu.com/image");

        file.setMemberId(userEntity.getId());
        file.setMemberName(userEntity.getUserName());
        file.setMemberImg(userEntity.getUserInfo().getImage());
        file.setCatalog(0);
        file.setCreateTime(System.currentTimeMillis());
        file.setUpdateTime(System.currentTimeMillis());
        fileMapper.saveFile(file);
    }

    @Override
    public List<File> findChildFile(String projectId, String parentId, Integer isDel) {
        return fileMapper.findChildFile(projectId, parentId, isDel);
    }

}