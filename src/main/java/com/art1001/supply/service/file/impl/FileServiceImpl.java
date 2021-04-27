package com.art1001.supply.service.file.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.*;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.file.FileMapper;
import com.art1001.supply.mapper.file.FileVersionMapper;
import com.art1001.supply.service.file.FileEsService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.file.MemberDownloadService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * fileServiceImpl
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    /**
     * fileMapper接口
     */
    @Resource
    private FileMapper fileMapper;

    @Resource
    private FileVersionMapper fileVersionMapper;

    @Resource
    private UserService userService;

    @Resource
    private LogService logService;

    @Resource
    private MemberDownloadService memberDownloadService;

    @Resource
    private FileEsService fileEsService;

    @Resource
    private EsUtil<File> esUtil;

    /**
     * 公共模型库 常量定义信息
     */
    private final static String PUBLIC_FILE_NAME = "公共模型库";

    private final static String[] company = {"GB", "MB", "KB"};

    @Override
    public List<FileTree> querySubFileList(String fileId) {
        String userId = ShiroAuthenticationManager.getUserId();
        List<FileTree> trees = fileMapper.querySubFileList(fileId);
        Iterator<FileTree> iterator = trees.iterator();
        while (iterator.hasNext()) {
            FileTree file = iterator.next();
            if (file.getFilePrivacy() == 1) {
                if (StringUtils.isEmpty(file.getFileUids()) && !file.getMemberId().equals(userId)) {
                    iterator.remove();
                }
                if (StringUtils.isNotEmpty(file.getFileUids())
                        && !ArrayUtils.contains(file.getFileUids().split(","), userId)
                        && !file.getMemberId().equals(userId)) {
                    iterator.remove();
                }
            }
        }

        trees.forEach(item -> {
            if (checkChildFolder(item.getId()) == 1) {
                item.setIsParent(1);
            } else {
                item.setIsParent(0);
            }
        });
        return trees;
    }

    @Override
    public List<FileTree> queryFileListByUserId(String userId) {
        return fileMapper.queryFileListByUserId(userId);
    }

    @Override
    public List<FileTree> queryFileByUserId(String userId) {
        return fileMapper.queryFileByUserId(userId);
    }

    @Override
    public List<File> queryFileList(String fileId, Integer current, Integer size) {
        String userId = ShiroAuthenticationManager.getUserId();
        List<File> fileList = list(new QueryWrapper<File>().eq("parent_id", fileId).eq("file_del", 0));
        List<File> childFile = fileMapper.findChildFile(fileId);
        fileList = fileList.stream().filter(file -> file.getCatalog() == 1).sorted(Comparator.comparing(File::getCreateTime)).collect(Collectors.toList());
        fileList.addAll(childFile);
        Iterator<File> iterator = fileList.iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            if (StringUtils.isNotEmpty(file.getFileUids())) {
                file.setJoinInfo(userService.findManyUserById(file.getFileUids()));
            } else {
                file.setJoinInfo(new ArrayList<>());
            }
            if (file.getFilePrivacy() == 1) {
                if (file.getCatalog() == 1) {//文件夹处理
                    if (StringUtils.isEmpty(file.getFileUids()) && !file.getMemberId().equals(userId)) {
                        iterator.remove();
                    }
                }
                if (StringUtils.isNotEmpty(file.getFileUids())
                        && !ArrayUtils.contains(file.getFileUids().split(","), userId)
                        && !file.getMemberId().equals(userId)) {
                    iterator.remove();
                }
            }
        }

        return fileList;
    }

    /*  获取素材库树状图数据
     *
     **/
    @Override
    public IPage<File> queryFodderList(String fileId, Integer current, Integer size) {
        Page<File> filePage = new Page<>(current, size);
        File isRoot = getOne(new QueryWrapper<File>().eq("file_id", fileId));
        IPage<File> fileById = null;
        if (isRoot.getCatalog() == 0) {
            fileById = fileMapper.selectPage(filePage, new QueryWrapper<File>().eq("file_id", fileId));
        } else {
            fileById = fileMapper.selectPage(null, new QueryWrapper<File>().eq("file_id", fileId));
        }
        //List<File> childFile = fileMapper.findChildFile(fileId);
        /*if(isRoot.getLevel()==0){
            String userId = ShiroAuthenticationManager.getUserId();
            File file = fileService.getOne(new QueryWrapper<File>().eq("user_id", userId));
            childFile.add(file);
        }*/
        return fileById;
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
     *
     * @param fileId 文件id
     */
    @Override
    public void deleteFileById(String fileId) {
        //删除文件
        removeById(fileId);
        //删除elasticSearch
        fileEsService.deleteFile(fileId);
//        fileRepository.deleteById(fileId);
        //删除文件版本信息
        fileVersionMapper.delete(new QueryWrapper<FileVersion>().eq("file_id", fileId));
    }

    @Override
    public void saveOssFile(File file) {
        //存库
        save(file);
        //写入版本库
        UserEntity userEntity = userService.findById(file.getMemberId());
        FileVersion fileVersion = new FileVersion();
        fileVersion.setFileId(file.getFileId());
        fileVersion.setIsMaster(1);
        fileVersion.setInfo(userEntity.getUserName() + " 上传于 " + DateUtils.getDateStr(new Date(), "yyyy-MM-dd HH:mm"));
        fileVersionMapper.insert(fileVersion);
        fileEsService.saveFile(file);
//        fileRepository.save(file);
        //写入日志
        Log log = new Log();
        //设置logId
        log.setId(IdGen.uuid());
        //0:日志 1:聊天评论
        log.setLogType(0);
        log.setContent(userEntity.getUserName() + " " + "上传了文件");
        //哪个用户操作产生的日志
        log.setMemberId(file.getMemberId());
        //对哪个信息的操作
        log.setPublicId(file.getFileId());
        //创建时间
        log.setCreateTime(System.currentTimeMillis());
        logService.save(log);
    }

    /**
     * 单个普通文件上传
     *
     * @param projectId     项目id
     * @param fileId        文件id
     * @param files 文件
     */
    @Override
    public String uploadFile(String projectId, String fileId,String parentId, String files) {
        String userId = ShiroAuthenticationManager.getUserId();
        FileVersion originalVersion= fileVersionMapper.selectOne(new QueryWrapper<FileVersion>().eq("file_id", fileId));
        UserEntity userEntity = userService.findById(userId);
        if (StringUtils.isNotEmpty(files)) {
            JSONArray array = JSON.parseArray(files);
            JSONObject object = array.getJSONObject(0);
            String fileName = object.getString("fileName");
            String fileUrl = object.getString("fileUrl");
            String size = object.getString("size");
            String ext = object.getString("ext");
            int level = object.getInteger("level");
            // 写库
            File myFile = new File();
            // 用原本的文件名
            myFile.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
            myFile.setExt(ext);
            myFile.setProjectId(projectId);
            myFile.setFileUrl(fileUrl);
            myFile.setSize(size);
            myFile.setIsModel(0);
            //文件的层级
            myFile.setLevel(level);
            myFile.setParentId(parentId);
            myFile.setMemberId(userId);
            myFile.setFileUids(userId);
            myFile.setCreateTime(System.currentTimeMillis());
            if (FileExt.extMap.get("images").contains(ext)) {
                myFile.setFileThumbnail(fileUrl);
            }
            save(myFile);
            fileEsService.saveFile(myFile);
            System.out.println(myFile.getFileName() + " 文件ES上传成功");
            FileVersion fileVersion = new FileVersion();
            fileVersion.setFileId(myFile.getFileId());
            fileVersion.setOriginalFileId(originalVersion.getOriginalFileId());
            fileVersion.setIsMaster(1);
            fileVersion.setInfo(userEntity.getUserName() + " 上传于 " + DateUtils.getDateStr(new Date(), "yyyy-MM-dd HH:mm"));
            fileVersionMapper.insert(fileVersion);
            logService.saveLog(myFile.getFileId(), TaskLogFunction.A36.getName(),2);
            return myFile.getFileId();
        }
        return "";
    }


    /**
     * 保存文件--文件在前端直接传oss
     *
     * @param files
     * @param publicId
     * @param projectId
     */
    @Override
    public void saveFile(String files, String publicId, String projectId) {
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
            myFile.setLevel(1);
            myFile.setIsModel(1);
            // 得到上传文件的大小
            myFile.setSize(size);
            myFile.setMemberId(ShiroAuthenticationManager.getUserId());
            myFile.setPublicId(publicId);
            // 设置操作用户信息
            myFile.setMemberId(ShiroAuthenticationManager.getUserId());
            // 设置时间
            myFile.setCreateTime(System.currentTimeMillis());
            myFile.setUpdateTime(System.currentTimeMillis());
            save(myFile);
            //保存到ElasticSearch
            fileEsService.saveFile(myFile);
//            fileRepository.save(myFile);
            System.out.println(myFile.getFileName() + " 文件ES上传成功");
        }
    }

    @Override
    public File saveModel(String fileModel, String fileCommon, String publicId, String filename, String parentId) {
        UserEntity userEntity = userService.findById(ShiroAuthenticationManager.getUserId());
        JSONObject array = JSON.parseObject(fileCommon);
        JSONObject object = JSON.parseObject(fileModel);
        String fileName = object.getString("fileName");
        String fileUrl = object.getString("fileUrl");
        String size = object.getString("size");
        File modelFile = new File();
        // 用原本的文件名
        modelFile.setFileName(filename);
        //查询出当前文件夹的level
        if (StringUtils.isNotEmpty(parentId)) {
            File file = getOne(new QueryWrapper<File>().eq("file_id", parentId));
            modelFile.setLevel(file.getLevel() + 1);
            modelFile.setProjectId(file.getProjectId());
        }

        modelFile.setSize(size);
        modelFile.setFileUrl(fileUrl);
        modelFile.setParentId(parentId);
        modelFile.setExt(fileName.substring(fileName.lastIndexOf(".")).toLowerCase());
        modelFile.setFileThumbnail(array.getString("fileUrl"));
        modelFile.setMemberId(userEntity.getUserId());
        modelFile.setCreateTime(System.currentTimeMillis());
        modelFile.setFileUids(ShiroAuthenticationManager.getUserId());
        if (StringUtils.isNotEmpty(publicId)) {
            modelFile.setPublicId(publicId);
            modelFile.setPublicLable(1);
        }
        save(modelFile);
//        fileRepository.save(modelFile);
        System.out.println(modelFile.getFileName() + " 文件ES上传成功");
        //版本历史更新
        FileVersion fileVersion = new FileVersion();
        fileVersion.setFileId(modelFile.getFileId());
        fileVersion.setIsMaster(1);
        fileVersion.setInfo(userEntity.getUserName() + " 上传于 " + DateUtils.getDateStr(new Date(), "yyyy-MM-dd HH:mm"));
        fileVersionMapper.insert(fileVersion);
        return modelFile;
    }

    /**
     * 初始化创建项目文件夹
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String initProjectFolder(String projectId) {
        String userId = ShiroAuthenticationManager.getUserId();
        File projectFile = new File();
        projectFile.setFileName("文件库");
        projectFile.setProjectId(projectId);
        projectFile.setMemberId(userId);
        projectFile.setCatalog(1);
        projectFile.setCreateTime(System.currentTimeMillis());
        projectFile.setUpdateTime(System.currentTimeMillis());
        save(projectFile);
        Arrays.asList("图片", "文档", "音频", "视频", "模型").forEach(item -> {
            File file = new File();
            file.setFileName(item);
            file.setProjectId(projectId);
            file.setParentId(projectFile.getFileId());
            file.setMemberId(userId);
            file.setCreateTime(System.currentTimeMillis());
            file.setUpdateTime(System.currentTimeMillis());
            file.setLevel(1);
            file.setCatalog(1);
            save(file);
        });

        return projectFile.getFileId();
    }

    @Override
    public FileTree createFolder(String projectId, String parentId, String fileName) {
        String userId = ShiroAuthenticationManager.getUserId();
        // 存库
        File file = new File();
        file.setFileName(fileName);
        // 设置父级id
        file.setParentId(parentId);
        file.setLevel(getOne(new QueryWrapper<File>().eq("file_id", parentId)).getLevel() + 1);
        // 项目id
        file.setProjectId(projectId);
        file.setMemberId(userId);
        file.setCreateTime(System.currentTimeMillis());
        file.setUpdateTime(System.currentTimeMillis());
        // 设置目录
        file.setCatalog(1);
        save(file);

        FileTree fileTree = new FileTree();
        fileTree.setPId(file.getParentId());
        fileTree.setId(file.getFileId());
        fileTree.setName(file.getFileName());
        fileTree.setOpen(false);
        fileTree.setIsParent(0);
        return fileTree;
    }

    /**
     * 查询出该文件夹下的所有子文件夹及文件
     *
     * @param parentId 父级id，顶级目录为 0
     * @return
     */
    @Override
    public List<File> findChildFile(String parentId, Integer orderType) {
        String userId = ShiroAuthenticationManager.getUserId();
        List<File> childFile = new ArrayList<>();//fileMapper.findChildFile(parentId,1,9999);
        if (isRootFolder(parentId)) {
            File myFolder = this.getMyFolder(ShiroAuthenticationManager.getUserId());
            if (ObjectsUtil.isNotEmpty(myFolder)) {
                childFile.add(myFolder);
            }
        }

        Iterator<File> iterator = childFile.iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            if (file.getFilePrivacy() == 1) {
                if (StringUtils.isNotEmpty(file.getFileUids()) && !Arrays.asList(file.getFileUids().split(",")).contains(ShiroAuthenticationManager.getUserId()) && !file.getMemberId().equals(ShiroAuthenticationManager.getUserId())) {
                    iterator.remove();
                }
            }
        }
        return childFile;
    }

    /**
     * 移动文件/文件夹
     *
     * @param projectId 移动之后的项目Id
     * @param fileIds   源文件id数组
     * @param folderId  目标目录id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveFile(String projectId, List<String> fileIds, String folderId) {
        fileIds.forEach(fileId -> moveSingleFile(folderId, projectId, fileId));
        //搜索引擎采用通知的方式进行数据同步
    }

    /**
     * 移动单个文件/文件夹
     *
     * @param parentId  目标文件夹id
     * @param projectId 目标项目Id
     * @param fileId    要移动的文件/文件夹的Id
     */
    private void moveSingleFile(String parentId, String projectId, String fileId) {
        File file = getOne(new QueryWrapper<File>().eq("file_id", fileId).eq("file_del", 0));
        fileMapper.moveFile(parentId, projectId, fileId);
        // 文件移动后处理文件版本
        if (file.getCatalog() == 0) {
            dealWithFile(file.getFileId(), parentId, 1);
        }
        //文件和文件夹的处理
        if (file.getCatalog() == 1) {
            //递归更新子文件/文件夹的项目id，其他字段不变
            recursiveFile(projectId, fileId);
        }
    }

    private void recursiveFile(String projectId, String fileId) {
        List<File> fileList = fileMapper.selectList(new QueryWrapper<File>().eq("parent_id", fileId).eq("file_del", 0));
        if (fileList != null && fileList.size() > 0) {
            fileList.forEach(file -> {
                File f = new File();
                f.setFileId(file.getFileId());
                f.setProjectId(projectId);
                updateById(f);
                if (file.getCatalog() == 0) {
                    dealWithFile(file.getFileId(), fileId, 1);
                }
                if (file.getCatalog() == 1) {
                    recursiveFile(projectId, file.getFileId());
                }
            });
        }
    }

    /**
     * 复制文件/文件夹
     *
     * @param projectId 复制之后的项目Id
     * @param fileIds   源文件id数组
     * @param folderId  目标目录id
     */
    @Override
    public void copyFile(String projectId, List<String> fileIds, String folderId) {
        fileIds.forEach(fileId -> copySingleFile(fileId, projectId, folderId));

        //搜索引擎采用通知的方式进行数据同步
    }

    private void copySingleFile(String fileId, String projectId, String folderId) {
        File file = getOne(new QueryWrapper<File>().eq("file_id", fileId).eq("file_del", 0));
        file.setFileId(IdGen.uuid());
        file.setParentId(folderId);
        file.setProjectId(projectId);
        //判断是否是同项目->解除绑定关系
        save(file);
        if (file.getCatalog() == 0) {
            dealWithFile(file.getFileId(), folderId, 0);
        }

        if (file.getCatalog() == 1) {
            recursiveCopyFile(projectId, fileId);
        }
    }

    private void recursiveCopyFile(String projectId, String parentId) {
        List<File> fileList = fileMapper.selectList(new QueryWrapper<File>().eq("parent_id", parentId).eq("file_del", 0));
        if (fileList != null && fileList.size() > 0) {
            fileList.forEach(file -> {
                file.setFileId(IdGen.uuid());
                file.setProjectId(projectId);
                file.setParentId(parentId);
                save(file);
                if (file.getCatalog() == 0) {
                    dealWithFile(file.getFileId(), parentId, 0);
                }
                if (file.getCatalog() == 1) {
                    recursiveCopyFile(projectId, file.getFileId());
                }
            });
        }
    }

    /**
     * @param fileId   文件id
     * @param parentId
     * @param flag     移动/复制 1/0
     */
    //处理文件版本信息以及和文件相关的信息
    private void dealWithFile(String fileId, String parentId, Integer flag) {
        UserEntity userEntity = userService.findById(ShiroAuthenticationManager.getUserId());
        if(flag==1){
            File file = getOne(new QueryWrapper<File>().eq("file_id", parentId).eq("file_del", 0));
            FileVersion fileVersion = fileVersionMapper.selectOne(new QueryWrapper<FileVersion>().eq("file_id", fileId).eq("is_master", 1));
            fileVersion.setInfo(userEntity.getUserName() + " 将文件移动到了 " + file.getFileName());
            fileVersionMapper.updateById(fileVersion);
            logService.saveLog(file.getFileId(),userEntity + "将文件复制到了" + file.getFileName() ,2);
        }else{
            FileVersion fileVersion = new FileVersion();
            fileVersion.setFileId(fileId);
            fileVersion.setIsMaster(1);
            fileVersion.setOriginalFileId(fileId);
            File file = getOne(new QueryWrapper<File>().eq("file_id", parentId).eq("file_del", 0));
            fileVersion.setInfo(userEntity.getUserName() + " 将文件复制到了 " + file.getFileName());
            fileVersionMapper.insert(fileVersion);
            logService.saveLog(file.getFileId(),userEntity + "将文件复制到了" + file.getFileName() ,2);
        }
    }


    /**
     * 获取上级url
     *
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
        fileMapper.moveToRecycleBin(fileIds, System.currentTimeMillis());
    }

    @Override
    public List<File> findChildFolder(String fileId) {
        return fileMapper.findChildFolder(fileId);
    }


    /**
     * 查询出该文件的所有参与者id
     *
     * @param fileId 文件id
     * @return 参与者id
     */
    @Override
    public String findJoinId(String fileId) {
        return fileMapper.findJoinId(fileId);
    }

    /**
     * 添加或者移除文件的参与者
     *
     * @param fileId    当前参与者id
     * @param newJoinId 新的参与者id
     * @return 影响行数
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void addAndRemoveFileJoin(String fileId, String newJoinId) {
        //查询出当前文件中的 参与者id
        String joinId = findJoinId(fileId);

        //log日志
        StringBuilder logContent = new StringBuilder();

        //将数组转换成集合
        List<String> oldJoin = Arrays.asList(joinId.split(","));
        List<String> newJoin = Arrays.asList(newJoinId.split(","));

        //比较 oldJoin 和 newJoin 两个集合的差集  (移除)
        List<String> reduce1 = oldJoin.stream().filter(item -> !newJoin.contains(item)).collect(Collectors.toList());
        if (reduce1 != null && reduce1.size() > 0) {
            logContent.append(TaskLogFunction.B.getName()).append(" ");
            for (String uId : reduce1) {
                UserEntity userEntity = userService.findById(uId);
                logContent.append(userEntity.getUserName()).append(" ");
            }
        }

        //比较 newJoin  和 oldJoin 两个集合的差集  (添加)
        List<String> reduce2 = newJoin.stream().filter(item -> !oldJoin.contains(item)).collect(Collectors.toList());
        if (reduce2 != null && reduce2.size() > 0) {
            logContent.append(TaskLogFunction.C.getName()).append(" ");
            for (String uId : reduce2) {
                UserEntity userEntity = userService.findById(uId);
                logContent.append(userEntity.getUserName()).append(" ");
            }
        }

        //如果没有参与者变动直接返回
        if ((reduce1 == null && reduce1.size() == 0) && (reduce2 == null && reduce2.size() == 0)) {
            return;
        } else {
            File file = new File();
            file.setFileId(fileId);
            file.setFileUids(newJoinId);
            file.setUpdateTime(System.currentTimeMillis());
            updateById(file);
            fileEsService.saveFile(file);
//            fileRepository.save(file);
            System.out.println(file.getFileName() + " 文件ES上传成功");
            logService.saveLog(fileId, logContent.toString(), 2);
        }
    }

    /**
     * 根据文件id 查询出文件名
     *
     * @param publicId 文件id
     * @return
     */
    @Override
    public String findFileNameById(String publicId) {
        return fileMapper.findFileNameById(publicId);
    }

    /**
     * 查询出我参与的所有文件
     *
     * @return
     */
    @Override
    public List<File> findJoinFile() {
        return fileMapper.findJoinFile(ShiroAuthenticationManager.getUserId());
    }

    /**
     * 查询出在该项目回收站中的文件
     *
     * @param projectId 项目id
     * @return
     */
    @Override
    public List<RecycleBinVO> findRecycleBin(String projectId, String type) {
        return fileMapper.findRecycleBin(projectId, type);
    }

    /**
     * 恢复文件
     *
     * @param fileId 文件的id
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Integer recoveryFile(String fileId) {
        fileMapper.recoveryFile(fileId, System.currentTimeMillis());
        logService.saveLog(fileId, TaskLogFunction.A28.getName(), 2);
        return 1;
    }

    /**
     * 查询某个文件夹下的公开文件
     *
     * @param parentId 父文件夹id
     * @return
     */
    @Override
    public List<File> findPublicFile(String parentId) {
        return fileMapper.findPublicFile(parentId);
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
     *
     * @param projectId 项目id
     * @param fileId    文件id
     * @return
     */
    @Override
    public List<File> findProjectFile(String projectId, String fileId) {
        List<File> fileList = new ArrayList<>();
        if ("0".equals(fileId)) {
            fileList = findChildFile(fileMapper.selectParentId(projectId), 1);
        } else if (PUBLIC_FILE_NAME.equals(fileMapper.findFileNameById(fileId))) {
            //如果用该文件夹名称是 公共模型库  则去公共文件表中查询数据
            fileList = findPublicFile(findFileId());
        } else {
            fileList = findPublicFile(fileId);
        }
        return fileList;
    }

    /**
     * 插入多条文件信息
     *
     * @param files 文件id
     */
    @Override
    public void saveFileBatch(String projectId, String files, String parentId, String publicId) {
        UserEntity userEntity = userService.findById(ShiroAuthenticationManager.getUserId());
        if (StringUtils.isNotEmpty(files)) {
            JSONArray array = JSON.parseArray(files);
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                String fileName = object.getString("fileName");
                String fileUrl = object.getString("fileUrl");
                String size = object.getString("size");
                String ext = object.getString("ext");
                int level = object.getInteger("level");
                // 写库
                File myFile = new File();
                // 用原本的文件名
                myFile.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
                myFile.setExt(ext);
                myFile.setProjectId(projectId);
                myFile.setFileUrl(fileUrl);
                myFile.setSize(size);
                myFile.setIsModel(0);
                //文件的层级
                myFile.setLevel(level);
                myFile.setParentId(parentId);
                myFile.setMemberId(ShiroAuthenticationManager.getUserId());
                myFile.setFileUids(ShiroAuthenticationManager.getUserId());
                myFile.setCreateTime(System.currentTimeMillis());
                if (FileExt.extMap.get("images").contains(ext)) {
                    myFile.setFileThumbnail(fileUrl);
                }
                if (StringUtils.isNotEmpty(publicId)) {
                    myFile.setPublicId(publicId);
                    myFile.setPublicLable(0);
                }
                save(myFile);
                fileEsService.saveFile(myFile);
                System.out.println(myFile.getFileName() + " 文件ES上传成功");
                FileVersion fileVersion = new FileVersion();
                fileVersion.setFileId(myFile.getFileId());
                fileVersion.setOriginalFileId(myFile.getFileId());
                fileVersion.setIsMaster(1);
                fileVersion.setInfo(userEntity.getUserName() + " 上传于 " + DateUtils.getDateStr(new Date(), "yyyy-MM-dd HH:mm"));
                fileVersionMapper.insert(fileVersion);
                logService.saveLog(myFile.getFileId(), TaskLogFunction.A37.getName(),2);
            }
        }
    }

    /**
     * 查询分享部分信息 (项目名称,文件名称,文件后缀名,文件url)
     *
     * @param id 文件id
     * @return
     */
    @Override
    public FileApiBean findFileApiBean(String id) {
        return fileMapper.selectFileApiBean(id);
    }

    /**
     * 查询出项目下的根文件夹的id
     *
     * @param projectId 项目id
     * @return
     */
    @Override
    public String findParentId(String projectId) {
        return fileMapper.selectParentId(projectId);
    }

    @Override
    public File getProjectParentFolder(String projectId) {
        return fileMapper.selectProjectParentFolder(projectId);
    }

    /**
     * 检查该目录下是否有子文件夹
     *
     * @param fileId 目录id
     */
    @Override
    public int checkChildFolder(String fileId) {
        return fileMapper.selectCount(new QueryWrapper<File>().eq("parent_id", fileId).eq("file_del", "0").eq("catalog", 1)) > 0 ? 1 : 0;
    }

    /**
     * 查看文件夹或者文件是否存在 (true:存在  false:不存在)
     *
     * @param fileId 文件
     * @return 结果
     */
    @Override
    public Boolean checkIsExist(String fileId) {
        //从elasticSearch查询
        //Optional<File> file = fileRepository.findById(fileId);
        //Stringer.isNullOrEmpty(file);
        return fileMapper.selectCount(new QueryWrapper<File>().lambda().eq(File::getFileId, fileId)) > 0;
    }

    /**
     * 根据项目id获取该项目下的根文件夹
     *
     * @param projectId 项目id
     * @return 文件树形图信息
     */
    @Override
    public List<FileTreeShowVO> findTreeFolderByProjectId(String projectId) {
        return fileMapper.selectTreeFolderByProjectId(projectId);
    }

    /**
     * 根据父级id获取该项目下的根文件夹
     *
     * @param parentId 父级id
     * @return 文件树形图信息
     */
    @Override
    public List<FileTreeShowVO> findTreeChildFolder(String parentId) {
        return fileMapper.selectTreeChildFolder(parentId);
    }

    /**
     * 获取一个项目的所有文件夹
     *
     * @param fileId 根目录id
     * @return 文件夹信息
     */
    @Override
    public List<FileTreeShowVO> getProjectAllFolder(String fileId) {
        List<FileTreeShowVO> fileTreeShowVOS = new ArrayList<>();
        List<File> files = fileMapper.selectProjectAllFolder(fileId);
        List<File> root = files.stream().filter(item -> item.getParentId().equals(fileId)).collect(Collectors.toList());
        List<File> sub = files.stream().filter(item -> !item.getParentId().equals(fileId)).collect(Collectors.toList());
        this.folderLayered(sub, root);
        this.chanageToFileTreeVO(root, fileTreeShowVOS);
        return fileTreeShowVOS;
    }

    /**
     * 文件夹分层
     *
     * @param files 文件夹集合
     */
    private void folderLayered(List<File> files, List<File> collect) {
        collect.forEach(parentFile -> {
            files.forEach(file -> {
                if (file.getParentId().equals(parentFile.getFileId())) {
                    if (parentFile.getFiles() == null) {
                        parentFile.setFiles(new ArrayList<File>());
                    }
                    parentFile.getFiles().add(file);
                }
            });
            if (!CollectionUtils.isEmpty(parentFile.getFiles())) {
                folderLayered(files, parentFile.getFiles());
            }
        });
    }

    /**
     * 文件夹类型转化
     */
    private void chanageToFileTreeVO(List<File> files, List<FileTreeShowVO> fileTreeShowVOS) {
        List<File> collect = files.stream().sorted(Comparator.comparing(File::getCreateTime).reversed()).collect(Collectors.toList());
        collect.forEach(file -> {
            FileTreeShowVO fileTreeShowVO = new FileTreeShowVO();
            fileTreeShowVO.setId(file.getFileId());
            fileTreeShowVO.setText(file.getFileName());
            fileTreeShowVO.setOpened(true);
            fileTreeShowVOS.add(fileTreeShowVO);
            if (StringUtils.isNotEmpty(file.getParentId())) {
                fileTreeShowVO.setPId(file.getParentId());
            }
            if (!CollectionUtils.isEmpty(file.getFiles())) {
                fileTreeShowVO.setChildren(new ArrayList<FileTreeShowVO>());
                chanageToFileTreeVO(file.getFiles(), fileTreeShowVO.getChildren());
            }
        });
    }

    //文件向上递归的分层
    private void upLevel(List<File> files) {
        files.forEach(f -> {
            files.forEach(s -> {
                if (s.getParentId().equals(f.getFileId())) {
                    List<File> subs = new ArrayList<>();
                    subs.add(s);
                    f.setFiles(subs);
                }
            });
        });
    }

    //文件向下递归的分层
    private void downLevel(List<File> files) {
        files.forEach(f -> {
            List<File> subs = new ArrayList<>();
            files.forEach(s -> {
                if (s.getParentId().equals(f.getFileId())) {
                    subs.add(s);
                }
            });
            f.setFiles(subs);
        });
    }


    /**
     * 获取文件的绑定信息
     *
     * @param id 父id
     * @return 文件信息
     */
    @Override
    public List<File> getBindInfo(String id) {
        return list(new QueryWrapper<File>().select("file_id fileId", "file_name fileName", "ext ext", "catalog catalog").eq("parent_id", id).eq("file_del", 0));
    }

    /**
     * 获取我创建的文件并且排序
     *
     * @param order 排序规则(名称,大小,创建时间)
     * @return 我创建的文件数据
     */
    @Override
    public List<File> created(String order) {
        List<File> created = new ArrayList<>(30);
        if (StringUtils.isNotEmpty(order) && order.equals("size")) {
            for (String c : company) {
                created = fileMapper.createdBySize(ShiroAuthenticationManager.getUserId(), c);
            }
        } else if ("create".equals(order)) {
            created = fileMapper.created(order, ShiroAuthenticationManager.getUserId());
        } else {
            created = fileMapper.created(null, ShiroAuthenticationManager.getUserId());
        }
        return created;
    }


    /**
     * 获取我参与的文件并且排序
     *
     * @param order 排序规则(名称,大小,创建时间)
     * @return 我参与的文件数据
     */
    @Override
    public List<File> join(String order) {
        List<File> join = new ArrayList<>(30);
        if (StringUtils.isNotEmpty(order) && order.equals("size")) {
            for (String c : company) {
                join = fileMapper.joinBySize(ShiroAuthenticationManager.getUserId(), c);
            }
        } else if ("create".equals(order)) {
            join = fileMapper.join(order, ShiroAuthenticationManager.getUserId());
        } else {
            join = fileMapper.join(null, ShiroAuthenticationManager.getUserId());
        }
        return join;
    }

    /**
     * 从其他信息(任务,文件,日程,分享) 绑定文件信息
     *
     * @param files 文件集合信息
     * @return
     */
    @Override
    public boolean bindFile(String files) {
        JSONArray array = JSON.parseArray(files);
        List<File> fileList = new ArrayList<File>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            String fileName = object.getString("fileName");
            String fileUrl = object.getString("fileUrl");
            String size = object.getString("size");
            String projectId = object.getString("projectId");
            String ext = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            String publicId = object.getString("publicId");
            // 写库
            File myFile = new File();
            myFile.setFileId(IdGen.uuid());
            // 用原本的文件名
            myFile.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
            myFile.setExt(ext);
            myFile.setProjectId(projectId);
            myFile.setFileUrl(fileUrl);
            myFile.setCatalog(0);
            myFile.setProjectId(projectId);
            myFile.setPublicId(publicId);
            // 得到上传文件的大小
            myFile.setSize(size);
            myFile.setLevel(1);
            myFile.setMemberId(ShiroAuthenticationManager.getUserId());
            myFile.setFileUids(ShiroAuthenticationManager.getUserId());
            myFile.setCreateTime(System.currentTimeMillis());
            myFile.setUpdateTime(System.currentTimeMillis());
            if (FileExt.extMap.get("images").contains(ext)) {
                myFile.setFileThumbnail(fileUrl);
            }

            if (StringUtils.isNotEmpty(publicId)) {
                myFile.setPublicId(publicId);
                myFile.setPublicLable(1);
            }
            fileList.add(myFile);
        }
        return this.saveBatch(fileList);
    }

    /**
     * 根据文件名称在项目中进行模糊查询
     *
     * @param fileName  文件名称 (模糊查询)
     * @param projectId 项目id
     * @return
     */
    @Override
    public List<File> seachByName(String fileName, String projectId) {
        return fileMapper.seachByName(fileName, projectId);
    }

    /**
     * 获取一个文件的链接地址
     *
     * @param fileId 文件id
     * @return 链接地址
     */
    @Override
    public String getFileUrl(String fileId) {
        return getOne(new QueryWrapper<File>().select("file_url").eq("file_id", fileId)).getFileUrl();
    }

    @Override
    public File updateFileName(String fileId, String fileName) {
        File file = new File();
        file.setFileId(fileId);
        file.setFileName(fileName);
        file.setUpdateTime(System.currentTimeMillis());
        updateById(file);
        return getOne(new QueryWrapper<File>().eq("file_id", fileId));
    }

    /**
     * 获取绑定该标签的文件信息 (version2.0)
     *
     * @param tagId 标签id
     * @return 文件集合
     */
    @Override
    public List<File> getBindTagInfo(Long tagId) {

        return fileMapper.selectBindTagInfo(tagId);
    }

    @Override
    public Integer updateDownloadCount(String fileId) {
        Integer checkResult = this.checkFile(fileId);
        if (checkResult == 1) {
            //获取到该文件的当前下载数量
            Integer currentDownloadCount = this.getDownloadCountByFileId(fileId);
            File file = new File();
            file.setFileId(fileId);
            file.setFileDownloadCount(currentDownloadCount + 1);
            file.setUpdateTime(System.currentTimeMillis());
            return this.updateById(file) ? 1 : 0;
        }
        return checkResult;
    }

    @Override
    public Integer getDownloadCountByFileId(String fileId) {
        Integer checkResult = this.checkFile(fileId);
        if (checkResult == 1) {
            //构造sql表达式
            LambdaQueryWrapper<File> selectFileDownloadCountQw = new QueryWrapper<File>().lambda()
                    .eq(File::getFileId, fileId)
                    .select(File::getFileDownloadCount);
            return this.getOne(selectFileDownloadCountQw).getFileDownloadCount();
        }
        return checkResult;
    }

    @Override
    public List<FileTreeShowVO> getParentFolders(String fileId, String projectId) {

        List<FileTreeShowVO> fileTreeShowVOS = new ArrayList<>();
        //查询到该目录的上级所有目录的id集合
        List<String> fileIds = Arrays.asList(fileMapper.selectParentFolders(fileId).split(","));

        //生成目录查询条件表达式
        LambdaQueryWrapper<File> selectFileListQw = new QueryWrapper<File>().lambda()
                .select(File::getFileId, File::getFileName, File::getCreateTime, File::getParentId)
                .in(File::getFileId, fileIds);

        List<File> fileList = fileMapper.selectList(selectFileListQw);

        File rootFolder = this.getProjectRootFolderId(fileId);
        if (StringUtils.isNotEmpty(rootFolder.getUserId())) {
            File parentFolder = this.getProjectParentFolder(projectId);
            fileList.forEach(f -> {
                if (f.getFileId().equals(rootFolder.getFileId())) {
                    f.setParentId(parentFolder.getFileId());
                }
            });
            fileList.add(parentFolder);
        }
        //生成目录树
        this.upLevel(fileList);
        this.chanageToFileTreeVO(fileList, fileTreeShowVOS);
        //过滤出第一条数据(该集合除第一条外其他数据无用)
        return fileTreeShowVOS.stream().filter(f -> Constants.ZERO.equals(f.getPId())).collect(Collectors.toList());

    }

    /**
     * 获取一个文件的在项目中最顶级的目录id
     *
     * @param fileId 文件/目录id
     * @return 根目录id
     * @author heShaoHua
     * @describe 暂无
     * @updateInfo 暂无
     * @date 2019/5/30 11:33
     */
    @Override
    public File getProjectRootFolderId(String fileId) {
        return fileMapper.selectProjectRootFolderId(fileId);
    }

    /*
     * 在文件系统创建标签并绑定文件
     * */
    @Override
    public JSONObject addTagBindFile(String fileId, Long tagId) {
        return null;
    }

    @Override
    public boolean isRootFolder(String fileId) {
        File byId = getById(fileId);
        if (byId.getLevel() == 0) {
            return true;
        }
        return false;
    }


    /*
     * 查询文件层级
     * */
    @Override
    public List<File> getPathFolders(String fileId, String projectId) {
        File file = fileMapper.findFileById(fileId);
        List<File> folderPathList = new ArrayList<>();
        //folderPathList.add(fileMapper.findFileTier(projectId));
        if (!ObjectUtils.allNotNull(file)) {
            return null;
        } else {
            folderPathList.add(file);
            return this.getFolderPath(file, folderPathList);
        }
    }

    @Override
    public File getMyFolder(String userId) {
        //构造出查询我的文件夹的sql表达式
        LambdaQueryWrapper<File> getMyFolderQw = new QueryWrapper<File>().lambda().eq(File::getUserId, userId);
        return this.getOne(getMyFolderQw);
    }

    /*
     *  获取root文件的路径
     * */
    @Override
    public File findFileTier(String projectId) {
        return fileMapper.findFileTier(projectId);
    }


    /**
     * 递归查找父级，一直查找到root为止，最终返回包含所有Folder的List，因为是递归，所以不能在方法里new 容器List，那样每次递归都会new一个List
     * 所以容器List必须从外部传入, 终止条件(基础情况)parentId == -1
     */
    private List<File> getFolderPath(File file, List<File> folderPathList) {
        if ("0".equals(file.getParentId())) {
            //root
            return folderPathList;
        } else {
            //找到父文件
            File parentFile = fileMapper.findFileById(file.getParentId());
            folderPathList.add(parentFile);
            return getFolderPath(parentFile, folderPathList);
        }
    }

    @Override
    public Page<File> searchFile(String fileName, String projectId, Integer pageNum) {
        Page<File> page = new Page<>();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("fileDel", "0"));
        boolQueryBuilder.must(QueryBuilders.termQuery("projectId", projectId));
        boolQueryBuilder.must(QueryBuilders.wildcardQuery("fileName", fileName));
        sourceBuilder.query(boolQueryBuilder);
        page = esUtil.searchListByPage(File.class, sourceBuilder, FILES, pageNum);

        if (CollectionUtils.isEmpty(page.getRecords())) {
            List<File> files = fileMapper.selectList(new QueryWrapper<File>().like("file_name", fileName).eq("file_del", 0).eq("project_id",projectId));
            page.setRecords(files);
        }
        return page;
    }

    @Override
    public String getFileParentId(String fileId) {
        //构造sql表达式
        LambdaQueryWrapper<File> selectParentIdQw = new QueryWrapper<File>().lambda()
                .select(File::getParentId)
                .eq(File::getFileId, fileId);

        File file = fileMapper.selectOne(selectParentIdQw);
        if (file != null && StringUtils.isNotEmpty(file.getParentId())) {
            return file.getParentId();
        }
        return null;
    }

    @Override
    public File getMaterialBase() {
        String materialBaseId = Constants.MATERIAL_BASE;
        return fileMapper.selectById(materialBaseId);
    }

    @Override
    public JSONObject getMateriaBaseFile(String folderId, Page pageable, Boolean downloadCount) {
        JSONObject jsonObject = new JSONObject();
        Page<File> materiaBaseFile = fileMapper.findMateriaBaseFile(pageable, folderId, downloadCount);
        materiaBaseFile.setTotal(fileMapper.findMateriaCount(folderId));
        jsonObject.put("data", fileMapper.findMateriaBaseFile(pageable, folderId, downloadCount));
        jsonObject.put("parentId", folderId);
        jsonObject.put("result", 1);
        return jsonObject;
    }


    @Override
    public void updateAll(String userId, String id) {
        fileMapper.updateAll(userId, id);
    }

    /**
     * 素材库查询总条数
     *
     * @param fileName
     * @return
     */
    @Override
    public Integer getSucaiTotle(String fileName) {
//        MatchPhraseQueryBuilder fileName1 = QueryBuilders.matchPhraseQuery("fileName", fileName);
    /*    SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchPhraseQuery("fileName", fileName))
                .withQuery(QueryBuilders.matchPhraseQuery("ext", fileName))
                .build();
        Iterable<File> search1 = fileRepository.search(searchQuery);if (Lists.newArrayList(search1).size()==0) {
            List<File> list = list(new QueryWrapper<File>().eq("catalog", "0").like("file_name", fileName));
            return list.size();
        }*/
//        Integer totalElements = (int) ((org.springframework.data.domain.Page<File>) search1).getTotalElements();

    /*    Iterator it = search1.iterator();
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        return count;*/
//    return totalElements;
        return null;
    }

    /**
     * 查询素材库
     *
     * @param fileName 文件名称
     * @param pageable
     * @return
     */
    @Override
    public ArrayList<File> searchMaterialBaseFile(String fileName, Pageable pageable) {
        PageRequest of = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), new Sort(Sort.Direction.DESC, "createTime"));
    /*    SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(of)
                .withQuery(QueryBuilders.matchPhraseQuery("fileName", fileName))
                .withQuery(QueryBuilders.matchPhraseQuery("ext", fileName))
//                .withQuery(QueryBuilders.multiMatchQuery(fileName,"fileName","ext"))
                .build();

        Iterable<File> byFileNameOrTagNameFiles = fileRepository.search(searchQuery);
        //如果在ES查询不到数据，则再从数据库查询一遍
        if (Lists.newArrayList(byFileNameOrTagNameFiles).size() == 0) {
            PageHelper.startPage(pageable.getPageNumber(),pageable.getPageSize());
            List<File> files = fileService.list(new QueryWrapper<File>().eq("catalog", "0").like("file_name", fileName));
            return Lists.newArrayList(files);
        }
        ArrayList<File> files = Lists.newArrayList(byFileNameOrTagNameFiles);
        if (files.size() == 0) {
            return null;
        }*/
//        return Lists.newArrayList(files);
        return null;
    }

    @Override
    public Integer signImportant(String fileId, Integer label) {
        File file = new File();
        file.setUpdateTime(System.currentTimeMillis());
        file.setFileId(fileId);
        file.setImportant(label);

        //更新库
        this.updateById(file);
        return 1;
    }

    /**
     * 校验fileId是否合法 fileId为空返回-1  fileId文件不存在返回-2, 正确返回1
     *
     * @param fileId 文件id
     * @return
     */
    private Integer checkFile(String fileId) {
        //文件不存在返回-2
        boolean fileNotExist = !this.checkIsExist(fileId);
        if (fileNotExist) {
            return -2;
        }
        return 1;
    }

    @Override
    public void updateAllUser(String userId, String id) {
        fileMapper.updateAllUser(userId, id);
    }

    @Override
    public List<String> getSucaiId(String id) {
        ArrayList<String> strings = new ArrayList<>();
        this.ids(strings, id);
        return strings;
    }

    @Override
    public void updateModelThumbnail(String fileId, String url) {
        File file = Optional.ofNullable(this.getById(fileId)).orElseThrow(() -> new ServiceException("该文件不存在!"));

        file.setFileId(fileId);
        file.setFileThumbnail(url);
        file.setUpdateTime(System.currentTimeMillis());

        this.updateById(file);
    }

    public void ids(List<String> ids, String id) {
        LambdaQueryWrapper<File> a = new QueryWrapper<File>().lambda().eq(File::getParentId, id);
        List<File> list = list(a);
        list.forEach(f -> {
            ids.add(f.getFileId());
            LambdaQueryWrapper<File> subCount = new QueryWrapper<File>().lambda().eq(File::getParentId, f.getFileId());
            int count = count(subCount);
            if (count > 0) {
                this.ids(ids, f.getFileId());
            }
        });
    }

    @Override
    public String[] getJoinAndCreatorId(String fileId) {
        LambdaQueryWrapper<File> select = new QueryWrapper<File>().lambda()
                .select(File::getFileUids, File::getMemberId)
                .eq(File::getFileId, fileId);

        File one = this.getOne(select);
        if (one != null) {
            if (one.getFileUids() != null) {
                StringBuilder userIds = new StringBuilder(one.getFileUids());
                if (StringUtils.isNotEmpty(one.getMemberId())) {
                    userIds.append(",").append(one.getMemberId());
                }
                return userIds.toString().split(",");
            } else if (one.getMemberId() != null) {
                return new String[]{one.getMemberId()};
            }
        }
        return new String[0];
    }

    @Override
    public void downloadSingleFile(File file, HttpServletResponse response) {
        saveDownloadFileInfo(file);
        if (file.getFilePrivacy().equals(Constants.B_ZERO) || (file.getFilePrivacy().equals(Constants.B_ONE) && file.getFileUids().contains(ShiroAuthenticationManager.getUserId()))) {
            InputStream inputStream = AliyunOss.downloadInputStream(file.getFileUrl(), response);
            // 设置头信息
            // 设置fileName的编码
            try {
                String fileName = URLEncoder.encode(file.getFileName() + file.getExt(), "UTF-8");
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
                response.setContentType("application/octet-stream");
                ServletOutputStream outputStream = response.getOutputStream();
                assert inputStream != null;
                IOUtils.copy(inputStream, outputStream);
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void downloadSingleFolder(File file, HttpServletResponse response) {
        response.reset(); // 非常重要
        // 设置fileName的编码
        String filename = file.getFileName();
        try {
            String fileName = URLEncoder.encode(filename + ".zip", "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setContentType("application/octet-stream");
            java.io.File zipFile = java.io.File.createTempFile("ald-bim-design", ".zip");
            ZipOutputStream zos = new ZipOutputStream(new CheckedOutputStream(new FileOutputStream(zipFile), new Adler32()));
            compress(file, zos, filename);
            zos.close();
            FileInputStream fis = new FileInputStream(zipFile);
            response.addHeader("Content-Length", String.valueOf(fis.available()));
            BufferedInputStream in = new BufferedInputStream(fis);
            ServletOutputStream out = response.getOutputStream();
            IOUtils.copy(in, out);
            out.close();
            in.close();
            fis.close();
            // 删除临时文件
            zipFile.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compress(File folder, ZipOutputStream out, String dir) throws IOException {

        List<File> files = list(new QueryWrapper<File>().eq("parent_id", folder.getFileId()));
        if (files != null && files.size() > 0) {


            //过滤出符合下载条件的文件
            List<File> fileList = files.stream().filter(f -> f.getFilePrivacy().equals(Constants.B_ZERO)
                    || (f.getFilePrivacy().equals(Constants.B_ONE) &&
                    (StringUtils.isNotEmpty(f.getFileUids()) && f.getFileUids().contains(ShiroAuthenticationManager.getUserId()))))
                    .collect(Collectors.toList());

            for (File inFile : fileList) {
                if (inFile.getCatalog() == 1) {
                    String name = inFile.getFileName();
                    if (!"".equals(dir)) {
                        name = dir + "/" + name;
                    }
                    compress(inFile, out, name);
                } else {
                    saveDownloadFileInfo(inFile);
                    AliyunOss.doZip(inFile, out, dir);
                }
            }
        } else {
            // 空文件夹的处理
            out.putNextEntry(new ZipEntry(dir + "/"));
            // 没有文件，不需要文件的copy
            out.closeEntry();
        }
    }

    /**
     * 保存下载文件信息
     *
     * @param inFile
     */
    private void saveDownloadFileInfo(File inFile) {
        memberDownloadService.save(
                MemberDownload.builder().fileId(inFile.getFileId())
                        .memberId(ShiroAuthenticationManager.getUserId())
                        .isDelete(inFile.getFileDel())
                        .downloadTime(String.valueOf(System.currentTimeMillis())).build());
    }

    @Override
    public void batchDownLoad(List<File> files, HttpServletResponse response) {
        try {
            String filename = files.get(0).getFileName() + "等" + (files.size() - 1) + "项_aldbim";
            String fileName = URLEncoder.encode(filename + ".zip", "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setContentType("application/octet-stream");
            java.io.File zipFile = java.io.File.createTempFile("ald-bim-design", ".zip");
            ZipOutputStream zos = new ZipOutputStream(new CheckedOutputStream(new FileOutputStream(zipFile), new Adler32()));
            for (File file : files) {
                //将数据存储到成员下载表
                saveDownloadFileInfo(file);
                if (file.getCatalog() == 0) {
                    AliyunOss.doZip(file, zos, "");
                } else {
                    compress(file, zos, file.getFileName());
                }
            }
            zos.close();
            FileInputStream fis = new FileInputStream(zipFile);
            response.addHeader("Content-Length", String.valueOf(fis.available()));
            BufferedInputStream in = new BufferedInputStream(fis);
            ServletOutputStream out = response.getOutputStream();
            IOUtils.copy(in, out);
            out.close();
            in.close();
            fis.close();
            // 删除临时文件
            zipFile.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<File> findList() {
        List<File> list = fileMapper.findList();
        return list;
    }

    /**
     * 删除文件夹
     */
    @Override
    public void deleteFileFolder(String fileId) {
        List<File> files = list(new QueryWrapper<File>().eq("parent_id", fileId));
        if (CollectionUtils.isNotEmpty(files)) {
            List<String> fileIds = files.stream().map(File::getFileId).collect(Collectors.toList());
            for (String f : fileIds) {
                List<File> f1 = list(new QueryWrapper<File>().eq("parent_id", f));
                if (CollectionUtils.isNotEmpty(f1)) {
                    for (File file : f1) {
                        deleteFileFolder(file.getFileId());
                    }
                }
            }
            fileMapper.deleteByIds(fileIds);
            fileMapper.deleteById(fileId);
        }

    }

    @Override
    public void dateToEs() {
        esUtil.createIndex(FILES);
        List<File> list = list(new QueryWrapper<File>());
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(r -> esUtil.save(FILES, DOCS, r, "fileId"));
        }
    }

    @Override
    public List<File> initFileMenu(String projectId) {

        List<File> list = list(new QueryWrapper<File>().eq("project_id", projectId).eq("file_del", 0).eq("catalog", 1));
        if (CollectionUtils.isNotEmpty(list)) {
            list=list.stream().filter(f->!"0".equals(f.getParentId())).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public Page<File> materialBaseSearch(String fileName, Integer pageNum) {
        Page<File> page = new Page<>();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("fileDel", "0"));
        boolQueryBuilder.must(QueryBuilders.matchQuery("fileName", fileName));
        sourceBuilder.query(boolQueryBuilder);
        page = esUtil.searchListByPage(File.class, sourceBuilder, FILES, pageNum);
        return page;
    }


}