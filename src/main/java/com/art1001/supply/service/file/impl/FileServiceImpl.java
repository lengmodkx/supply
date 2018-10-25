package com.art1001.supply.service.file.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.FileApiBean;
import com.art1001.supply.entity.file.FileVersion;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.mapper.file.FileMapper;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.file.FileVersionService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.DateUtils;
import com.art1001.supply.util.FileExt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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

    /**
     * 公共模型库 常量定义信息
     */
    private final static String PUBLIC_FILE_NAME = "公共模型库";

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
     * @param fileId 文件id
     */
    @Override
    public void deleteFileById(String fileId) {
        //删除文件
        removeById(fileId);
        //删除文件版本信息
        fileVersionService.remove(new QueryWrapper<FileVersion>().eq("file_id",fileId));
    }


    /**
     * 保存文件--文件在前端直接传oss
     * @param files
     * @param publicId
     * @param projectId
     */
    @Override
    public void saveFile(String files,String publicId,String projectId){
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
            myFile.setPublicId(publicId);
            // 设置操作用户信息
            myFile.setMemberId(ShiroAuthenticationManager.getUserId());
            // 设置时间
            myFile.setCreateTime(System.currentTimeMillis());
            myFile.setUpdateTime(System.currentTimeMillis());
            save(myFile);
        }
    }

    @Override
    public File saveModel(String fileModel, String fileCommon, String projectId, String publicId, String filename, String parentId) {
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        JSONObject array = JSON.parseObject(fileCommon);
        JSONObject object = JSON.parseObject(fileModel);
        String fileName = object.getString("fileName");
        String fileUrl = object.getString("fileUrl");
        String size = object.getString("size");
        File modelFile = new File();
        // 用原本的文件名
        modelFile.setFileName(filename);
        //查询出当前文件夹的level
        if(StringUtils.isNotEmpty(parentId)){
            int parentLevel = fileService.getOne(new QueryWrapper<File>().select("level").eq("file_id",parentId)).getLevel();
            modelFile.setLevel(parentLevel+1);
        }

        modelFile.setSize(size);
        modelFile.setFileUrl(fileUrl);
        modelFile.setParentId(parentId);
        modelFile.setExt(fileName.substring(fileName.lastIndexOf(".")).toLowerCase());
        modelFile.setProjectId(projectId);
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
        return modelFile;
    }

    /**
     * 初始化创建项目文件夹
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String initProjectFolder(String projectId) {
        File projectFile = new File();
        projectFile.setFileName(DateUtils.getDateStr());
        projectFile.setProjectId(projectId);
        projectFile.setMemberId(ShiroAuthenticationManager.getUserId());
        projectFile.setCatalog(1);
        save(projectFile);
        File file = new File();
        // 写库
        file.setFileName("公共模型库");
        // 项目id
        file.setProjectId(projectId);
        file.setParentId(projectFile.getFileId());
        file.setMemberId(ShiroAuthenticationManager.getUserId());
        file.setLevel(1);
        file.setCatalog(1);
        // 设置是否目录
        save(file);
        return projectFile.getFileId();
    }

    @Override
    public File createFolder(String projectId, String parentId, String fileName) {
        // 存库
        File file = new File();
        file.setFileName(fileName);
        // 设置父级id
        file.setParentId(parentId);
        // 项目id
        file.setProjectId(projectId);
        file.setMemberId(ShiroAuthenticationManager.getUserId());
        file.setCreateTime(System.currentTimeMillis());
        // 设置目录
        file.setCatalog(1);
        save(file);
        return file;
    }

    /**
     * 查询出该文件夹下的所有子文件夹及文件
     * @param parentId 父级id，顶级目录为 0
     * @return
     */
    @Override
    public List<File> findChildFile(String parentId) {
        return fileMapper.findChildFile(parentId);
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
                UserEntity userEntity = userService.findById(uId);
                logContent.append(userEntity.getUserName()).append(" ");
            }
        }

        //比较 newJoin  和 oldJoin 两个集合的差集  (添加)
        List<String> reduce2 = newJoin.stream().filter(item -> !oldJoin.contains(item)).collect(Collectors.toList());
        if(reduce2 != null && reduce2.size() > 0){
            logContent.append(TaskLogFunction.C.getName()).append(" ");
            for (String uId : reduce2) {
                UserEntity userEntity = userService.findById(uId);
                logContent.append(userEntity.getUserName()).append(" ");
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
            updateById(file);
            logService.saveLog(fileId,logContent.toString(),2);
        }
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
        List<File> fileList = new ArrayList<>();
        if("0".equals(fileId)){
            fileList = fileService.findChildFile(fileMapper.selectParentId(projectId));
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
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveFileBatch(String projectId, String files, String parentId,String publicId) {
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        List<File> fileList = new ArrayList<>();
        List<FileVersion> versionList = new ArrayList<>();
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
                myFile.setCatalog(0);
                // 得到上传文件的大小
                myFile.setSize(size);
                //文件的层级
                if(StringUtils.isNotEmpty(parentId)){
                    //查询出当前文件夹的level
                    int parentLevel = fileService.getOne(new QueryWrapper<File>().select("level").eq("file_id",parentId)).getLevel();
                    myFile.setLevel(parentLevel+1);
                }
                myFile.setParentId(parentId);
                myFile.setMemberId(ShiroAuthenticationManager.getUserId());
                myFile.setFileUids(ShiroAuthenticationManager.getUserId());
                myFile.setCreateTime(System.currentTimeMillis());
                if(FileExt.extMap.get("images").contains(ext)){
                    myFile.setFileThumbnail(fileUrl);
                }
                if(StringUtils.isNotEmpty(publicId)){
                    myFile.setPublicId(publicId);
                    myFile.setPublicLable(1);
                }
                fileList.add(myFile);
                FileVersion fileVersion = new FileVersion();
                fileVersion.setFileId(myFile.getFileId());
                fileVersion.setIsMaster(1);
                fileVersion.setInfo(userEntity.getUserName() + " 上传于 " + DateUtils.getDateStr(new Date(),"yyyy-MM-dd HH:mm"));
                versionList.add(fileVersion);
            }
            saveBatch(fileList);
            fileVersionService.saveBatch(versionList);
        }
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

    /**
     * 查询出项目下的根文件夹的id
     * @param projectId 项目id
     * @return
     */
    @Override
    public String findParentId(String projectId) {
        return fileMapper.selectParentId(projectId);
    }
}