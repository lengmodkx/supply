package com.art1001.supply.service.file.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.file.*;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.tag.TagRelation;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.file.FileMapper;
import com.art1001.supply.mapper.tagrelation.TagRelationMapper;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.file.FileVersionService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.DateUtils;
import com.art1001.supply.util.FileExt;
import com.art1001.supply.util.IdGen;
import com.art1001.supply.util.Stringer;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
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

    @Resource
    private TagRelationMapper tagRelationMapper;

    /**
     * ElasticSearch 查询接口
     */
    @Autowired
    private FileRepository fileRepository;


    /**
     * 公共模型库 常量定义信息
     */
    private final static String PUBLIC_FILE_NAME = "公共模型库";

    private final static String[] company =  {"GB","MB","KB"};

    /**
     * 通过id获取单条file数据
     *
     * @param id
     * @return
     */
    @Override
    public File findFileById(String id) {
       /* //从elasticSearch查询数据
        Optional<File> fileById = fileRepository.findById(id);
        if (Stringer.isNotNullOrEmpty(fileById.get())){
            return fileById.get();
        }else{*/
            return fileMapper.findFileById(id);
        /*}*/
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
        //删除elasticSearch
        fileRepository.deleteById(fileId);
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
            fileRepository.save(myFile);
        }
    }

    @Override
    public File saveModel(String fileModel, String fileCommon, String publicId, String filename, String parentId) {
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
            File file = fileService.getOne(new QueryWrapper<File>().eq("file_id",parentId));
            modelFile.setLevel(file.getLevel()+1);
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
        if(StringUtils.isNotEmpty(publicId)){
            modelFile.setPublicId(publicId);
            modelFile.setPublicLable(1);
        }
        fileService.save(modelFile);
        fileRepository.save(modelFile);
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
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        File projectFile = new File();
        projectFile.setFileName("文件库");
        projectFile.setProjectId(projectId);
        projectFile.setMemberId(userEntity.getUserId());
        projectFile.setCatalog(1);
        projectFile.setCreateTime(System.currentTimeMillis());
        projectFile.setUpdateTime(System.currentTimeMillis());
        save(projectFile);
        File file = new File();
        // 写库
        file.setFileName("公共模型库");
        // 项目id
        file.setProjectId(projectId);
        file.setParentId(projectFile.getFileId());
        file.setMemberId(userEntity.getUserId());
        file.setCreateTime(System.currentTimeMillis());
        file.setUpdateTime(System.currentTimeMillis());
        file.setLevel(1);
        file.setCatalog(1);
        // 设置是否目录
        save(file);
        //保存到ElasticSearch
        fileRepository.save(file);
        return projectFile.getFileId();
    }

    @Override
    public File createFolder(String projectId, String parentId, String fileName) {
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        // 存库
        File file = new File();
        file.setFileName(fileName);
        // 设置父级id
        file.setParentId(parentId);
        file.setLevel(getOne(new QueryWrapper<File>().eq("file_id",parentId)).getLevel()+1);
        // 项目id
        file.setProjectId(projectId);
        file.setMemberId(userEntity.getUserId());
        file.setCreateTime(System.currentTimeMillis());
        file.setUpdateTime(System.currentTimeMillis());
        // 设置目录
        file.setCatalog(1);
        save(file);

        //保存到ElasticSearch
        fileRepository.save(file);

        FileVersion fileVersion = new FileVersion();
        fileVersion.setFileId(file.getFileId());
        fileVersion.setIsMaster(1);
        fileVersion.setInfo(userEntity.getUserName() + " 创建于 " + DateUtils.getDateStr(new Date(),"yyyy-MM-dd HH:mm"));
        fileVersionService.save(fileVersion);
        return file;
    }

    /**
     * 查询出该文件夹下的所有子文件夹及文件
     * @param parentId 父级id，顶级目录为 0
     * @return
     */
    @Override
    public List<File> findChildFile(String parentId,Integer orderType) {
        String userId = ShiroAuthenticationManager.getUserId();
        List<File> childFile = fileMapper.findChildFile(parentId,userId,orderType);
        if(fileService.isRootFolder(parentId)){
            childFile.add(this.getMyFolder(ShiroAuthenticationManager.getUserId()));
        }
        Iterator<File> iterator = childFile.iterator();
        while(iterator.hasNext()){
            File file = iterator.next();
            if(file.getFilePrivacy() == 1){
                if(!Stringer.isNullOrEmpty(file.getFileUids()) && !Arrays.asList(file.getFileUids().split(",")).contains(ShiroAuthenticationManager.getUserId()) && !file.getMemberId().equals(ShiroAuthenticationManager.getUserId())){
                    iterator.remove();
                }
            }

            if(file.getFilePrivacy()==2){
                if(!file.getMemberId().equals(userId)){
                    iterator.remove();
                }
            }
        }
//        //此处判断parentId代表的文件夹是否是该项目的根目录如果是根目录则需要在childFile中添加一条素材库的数据
//        String currFileIdParentId = this.getFileParentId(parentId);
//        if(Stringer.isNotNullOrEmpty(currFileIdParentId)){
//            //校验是否是项目文件根目录
//            boolean isProjectRootFolder = currFileIdParentId.equals(Constants.ZERO);
//            if(isProjectRootFolder){
//                File materialBase = this.getMaterialBase();
//                childFile.add(0, materialBase);
//            }
//        }
        return childFile;
    }

    /**
     * 移动文件
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
        //将更改保存到elasticSearch
        if (Stringer.isNotNullOrEmpty(fileIds)){
            File file=new File();
            for(int i = 0; i < fileIds.length;i++){
               file.setFileId(fileIds[i]);
               file.setParentId(folderId);
               fileRepository.save(file);
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
            fileRepository.save(file);
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
    public Integer recoveryFile(String fileId) {

        String projectId = fileMapper.selectOne(new QueryWrapper<File>().lambda().eq(File::getFileId, fileId).select(File::getProjectId)).getProjectId();
        String parentId = this.findParentId(projectId);
        fileMapper.recoveryFile(fileId,parentId,System.currentTimeMillis());
        Log log = logService.saveLog(fileId,TaskLogFunction.A28.getName(),1);
        return 1;
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
            fileList = fileService.findChildFile(fileMapper.selectParentId(projectId),1);
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
                myFile.setFileId(IdGen.uuid());
                // 用原本的文件名
                myFile.setFileName(fileName.substring(0,fileName.lastIndexOf(".")));
                myFile.setExt(ext);
                myFile.setProjectId(projectId);
                myFile.setFileUrl(fileUrl);
                myFile.setCatalog(0);
                // 得到上传文件的大小
                myFile.setSize(size);
                myFile.setIsModel(0);
                //文件的层级
                if(StringUtils.isNotEmpty(parentId)){
                    //查询出当前文件夹的level
                    int parentLevel = fileService.getOne(new QueryWrapper<File>().select("level").eq("file_id",parentId)).getLevel();
                    myFile.setLevel(parentLevel+1);
                    myFile.setParentId(parentId);
                }
                myFile.setMemberImg(userEntity.getImage());
                myFile.setMemberName(userEntity.getUserName());
                myFile.setMemberId(ShiroAuthenticationManager.getUserId());
                myFile.setFileUids(ShiroAuthenticationManager.getUserId());
                myFile.setCreateTime(System.currentTimeMillis());
                myFile.setUpdateTime(System.currentTimeMillis());
                if(FileExt.extMap.get("images").contains(ext)){
                    myFile.setFileThumbnail(fileUrl);
                }
                if(StringUtils.isNotEmpty(publicId)){
                    myFile.setPublicId(publicId);
                    myFile.setPublicLable(0);
                }
                fileList.add(myFile);
                fileRepository.save(myFile);
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

    @Override
    public File getProjectParentFolder(String projectId) {
        if(Stringer.isNullOrEmpty(projectId)){
            return null;
        }
        return fileMapper.selectProjectParentFolder(projectId);
    }

    /**
     * 检查该目录下是否有子文件夹
     * @param fileId 目录id
     */
    @Override
    public int checkChildFolder(String fileId) {
        return fileMapper.selectCount(new QueryWrapper<File>().eq("parent_id",fileId).eq("catalog",1)) > 0 ? 1 : 0;
    }

    /**
     * 查看文件夹或者文件是否存在 (true:存在  false:不存在)
     * @param fileId 文件
     * @return 结果
     */
    @Override
    public Boolean checkIsExist(String fileId) {
        if(Stringer.isNullOrEmpty(fileId)){
            throw new ServiceException("fileId 不能为空!");
        }

        //从elasticSearch查询
        //Optional<File> file = fileRepository.findById(fileId);
        //Stringer.isNullOrEmpty(file);
        return fileMapper.selectCount(new QueryWrapper<File>().lambda().eq(File::getFileId, fileId)) > 0;
    }

    /**
     * 根据项目id获取该项目下的根文件夹
     * @param projectId 项目id
     * @return 文件树形图信息
     */
    @Override
    public List<FileTreeShowVO> findTreeFolderByProjectId(String projectId) {
        return fileMapper.selectTreeFolderByProjectId(projectId);
    }

    /**
     * 根据父级id获取该项目下的根文件夹
     * @param parentId 父级id
     * @return 文件树形图信息
     */
    @Override
    public List<FileTreeShowVO> findTreeChildFolder(String parentId) {
        return fileMapper.selectTreeChildFolder(parentId);
    }

    /**
     * 获取一个项目的所有文件夹
     * @param fileId 根目录id
     * @return 文件夹信息
     */
    @Override
    public List<FileTreeShowVO> getProjectAllFolder(String fileId) {
        List<FileTreeShowVO> fileTreeShowVOS = new ArrayList<>();
        List<File> files = fileMapper.selectProjectAllFolder(fileId);
        List<File> root = files.stream().filter(item -> item.getParentId().equals(fileId)).collect(Collectors.toList());
        List<File> sub = files.stream().filter(item -> !item.getParentId().equals(fileId)).collect(Collectors.toList());
        this.folderLayered(sub,root);
        this.chanageToFileTreeVO(root,fileTreeShowVOS);
        return fileTreeShowVOS;
    }

    /**
     * 文件夹分层
     * @param files 文件夹集合
     */
    private void folderLayered(List<File> files, List<File> collect){
        collect.forEach(parentFile -> {
            files.forEach(file -> {
                if(file.getParentId().equals(parentFile.getFileId())){
                    if(parentFile.getFiles() == null){
                        parentFile.setFiles(new ArrayList<File>());
                    }
                    parentFile.getFiles().add(file);
                }
            });
            if(!CollectionUtils.isEmpty(parentFile.getFiles())){
                folderLayered(files,parentFile.getFiles());
            }
        });
    }

    /**
     * 文件夹类型转化
     */
    private void chanageToFileTreeVO(List<File> files,List<FileTreeShowVO> fileTreeShowVOS){
        List<File> collect = files.stream().sorted(Comparator.comparing(File::getCreateTime).reversed()).collect(Collectors.toList());
        collect.forEach(file -> {
            FileTreeShowVO fileTreeShowVO = new FileTreeShowVO();
            fileTreeShowVO.setId(file.getFileId());
            fileTreeShowVO.setText(file.getFileName());
            fileTreeShowVO.setOpened(true);
            fileTreeShowVOS.add(fileTreeShowVO);
            if(!Stringer.isNullOrEmpty(file.getParentId())){
                fileTreeShowVO.setParentId(file.getParentId());
            }
            if(!CollectionUtils.isEmpty(file.getFiles())){
                fileTreeShowVO.setChildren(new ArrayList<FileTreeShowVO>());
                chanageToFileTreeVO(file.getFiles(),fileTreeShowVO.getChildren());
            }
        });
    }

    //文件向上递归的分层
    private void upLevel(List<File> files){
        files.forEach(f -> {
            files.forEach(s -> {
                if(s.getParentId().equals(f.getFileId())){
                    List<File> subs = new ArrayList<>();
                    subs.add(s);
                    f.setFiles(subs);
                }
            });
        });
    }

    //文件向下递归的分层
    private void downLevel(List<File> files){
        files.forEach(f -> {
            List<File> subs = new ArrayList<>();
            files.forEach(s -> {
                if(s.getParentId().equals(f.getFileId())){
                    subs.add(s);
                }
            });
            f.setFiles(subs);
        });
    }



    /**
     * 获取文件的绑定信息
     * @param id 父id
     * @return 文件信息
     */
    @Override
    public List<File> getBindInfo(String id) {
        return fileService.list(new QueryWrapper<File>().select("file_id fileId","file_name fileName","ext ext","catalog catalog").eq("parent_id",id));
    }

    /**
     * 获取我创建的文件并且排序
     * @param order 排序规则(名称,大小,创建时间)
     * @return 我创建的文件数据
     */
    @Override
    public List<File> created(String order) {
        List<File> created = new ArrayList<>(30);
        if(StringUtils.isNotEmpty(order) && order.equals("size")){
            for (String c : company) {
                created.addAll(fileMapper.createdBySize(ShiroAuthenticationManager.getUserId(),c));
            }
        } else{
            created = fileMapper.created(order, ShiroAuthenticationManager.getUserId());
        }
        return created;
    }

    /**
     * 从其他信息(任务,文件,日程,分享) 绑定文件信息
     * @param files 文件集合信息
     * @return
     */
    @Override
    public boolean bindFile(String files) {
        JSONArray array = JSON.parseArray(files);
        List<File> fileList = new ArrayList<File>();
        for (int i=0;i<array.size();i++) {
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
            myFile.setFileName(fileName.substring(0,fileName.lastIndexOf(".")));
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
            if(FileExt.extMap.get("images").contains(ext)){
                myFile.setFileThumbnail(fileUrl);
            }

            if(StringUtils.isNotEmpty(publicId)){
                myFile.setPublicId(publicId);
                myFile.setPublicLable(1);
            }
            fileList.add(myFile);
        }
       return this.saveBatch(fileList);
    }

    /**
     * 根据文件名称在项目中进行模糊查询
     * @param fileName 文件名称 (模糊查询)
     * @param projectId 项目id
     * @return
     */
    @Override
    public List<File> seachByName(String fileName, String projectId) {
        return fileMapper.seachByName(fileName,projectId);
    }

    /**
     * 获取一个文件的链接地址
     * @param fileId 文件id
     * @return 链接地址
     */
    @Override
    public String getFileUrl(String fileId) {
        return fileService.getOne(new QueryWrapper<File>().select("file_url").eq("file_id", fileId)).getFileUrl();
    }

    @Override
    public Integer updateFileName(String fileId, String fileName) {
        if(this.checkIsExist(fileId)){
            File file = new File();
            file.setFileId(fileId);
            file.setFileName(fileName);
            file.setUpdateTime(System.currentTimeMillis());
            return updateById(file) ? 1:0;
        }
        return -1;
    }

    /**
     * 获取绑定该标签的文件信息 (version2.0)
     * @param tagId 标签id
     * @return 文件集合
     */
    @Override
    public List<File> getBindTagInfo(Long tagId) {
        List<File> files=new ArrayList<File>();
        List<TagRelation> tagRelation = tagRelationMapper.findTagRelationByTagId(tagId);
        for (TagRelation t:tagRelation) {
            if (t.getFileId()!=null){
                files.add(fileMapper.findFileById(t.getFileId()));
            }
        }
        return files;
    }

    @Override
    public Integer updateDownloadCount(String fileId) {
        Integer checkResult = this.checkFile(fileId);
        if(checkResult == 1){
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
        if(checkResult == 1){
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
                .select(File::getFileId, File::getFileName, File::getCreateTime,File::getParentId)
                .in(File::getFileId, fileIds);

        List<File> fileList = fileMapper.selectList(selectFileListQw);

        File rootFolder = this.getProjectRootFolderId(fileId);
        if(Stringer.isNotNullOrEmpty(rootFolder.getUserId())){
            File parentFolder = this.getProjectParentFolder(projectId);
            fileList.forEach(f -> {
                if(f.getFileId().equals(rootFolder.getFileId())){
                    f.setParentId(parentFolder.getFileId());
                }
            });
            fileList.add(parentFolder);
        }
        //生成目录树
        this.upLevel(fileList);
        this.chanageToFileTreeVO(fileList, fileTreeShowVOS);
        //过滤出第一条数据(该集合除第一条外其他数据无用)
        return fileTreeShowVOS.stream().filter(f -> Constants.ZERO.equals(f.getParentId())) .collect(Collectors.toList());

    }

    /**
     * 获取一个文件的在项目中最顶级的目录id
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
        if(Stringer.isNullOrEmpty(fileId)){
            return false;
        }
        File byId = fileService.getById(fileId);
        if(byId.getLevel() == 0){
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
        List<File> folderPathList=new ArrayList<>();
        //folderPathList.add(fileMapper.findFileTier(projectId));
        if (Stringer.isNullOrEmpty(file)){
            return  null;
        }else {
            folderPathList.add(file);
            return this.getFolderPath(file,folderPathList);
        }
    }

    @Override
    public File getMyFolder(String userId) {
        if(Stringer.isNullOrEmpty(userId)){
            return null;
        }

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
     *
     * 递归查找父级，一直查找到root为止，最终返回包含所有Folder的List，因为是递归，所以不能在方法里new 容器List，那样每次递归都会new一个List
     * 所以容器List必须从外部传入, 终止条件(基础情况)parentId == -1
     */
    private List<File> getFolderPath( File file , List<File> folderPathList) {
        if ("0".equals(file.getParentId())) {
            //root
            return folderPathList;
        }else {
            //找到父文件
            File parentFile = fileMapper.findFileById(file.getParentId());
            folderPathList.add(parentFile);
            return getFolderPath(parentFile, folderPathList);
        }
    }

    @Override
    public List<File> searchFile(String fileName, String projectId) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(fileName, "fileName","tagName"))
                .withQuery(QueryBuilders.wildcardQuery("fileName", "*" + fileName + "*"))
                .withFilter(QueryBuilders.termQuery("projectId", projectId)).build();
        Iterable<File> byFileNameOrTagNameFiles = fileRepository.search(searchQuery);
        return Lists.newArrayList(byFileNameOrTagNameFiles);
    }

    @Override
    public String getFileParentId(String fileId) {
        if(Stringer.isNullOrEmpty(fileId)){
            return null;
        }

        //构造sql表达式
        LambdaQueryWrapper<File> selectParentIdQw = new QueryWrapper<File>().lambda()
                .select(File::getParentId)
                .eq(File::getFileId, fileId);

        File file = fileMapper.selectOne(selectParentIdQw);
        if(file != null && Stringer.isNotNullOrEmpty(file.getParentId())){
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
    public JSONObject getMateriaBaseFile(String folderId, Pageable pageable) {
        JSONObject jsonObject = new JSONObject();
        Page<File> page = new Page<>(pageable.getPageNumber()+1,pageable.getPageSize());
        jsonObject.put("data",fileMapper.findMateriaBaseFile(page, folderId));
        jsonObject.put("parentId",folderId);
        jsonObject.put("result",1);
        return jsonObject;
    }

    @Override
    public List<FileTreeShowVO> getAllFolderTree(String parentId) {
        //获取一个目录的所有子级目录id
        String[] childFolderIds = this.getChildFolderIds(parentId);
        if(childFolderIds.length == 0){
            return new ArrayList<>();
        }

        //构造sql表达式
        LambdaQueryWrapper<File> selectFolderQw = new QueryWrapper<File>().lambda()
                .eq(File::getCatalog,1)
                .select(File::getFileId, File::getFileName, File::getParentId, File::getLevel,File::getCreateTime)
                .in(File::getFileId, Arrays.asList(childFolderIds));

        File myFolder = this.getMyFolder(ShiroAuthenticationManager.getUserId());
        myFolder.setParentId(parentId);
        List<File> childFolders = this.list(selectFolderQw);
        childFolders.add(myFolder);
        List<FileTreeShowVO> fileTreeShowVOS = new ArrayList<>();
        //生成目录树
        this.downLevel(childFolders);
        this.chanageToFileTreeVO(childFolders, fileTreeShowVOS);
        return fileTreeShowVOS.stream().filter(f -> Constants.ZERO.equals(f.getParentId()) || f.getId().equals(Constants.MATERIAL_BASE)).collect(Collectors.toList());
    }

    @Override
    public String[] getChildFolderIds(String folderId) {
        if(Stringer.isNullOrEmpty(folderId)){
            return null;
        }

        String subIds = fileMapper.selectChildFolderIds(folderId);
        if(Stringer.isNullOrEmpty(subIds)){
            return new String[0];
        }
        return subIds.split(",");
    }

    /**
     * 素材库查询总条数
     * @param fileName
     * @return
     */
    @Override
    public Integer getSucaiTotle(String fileName) {
        MatchPhraseQueryBuilder fileName1 = QueryBuilders.matchPhraseQuery("fileName",fileName);
        Iterable<File> search1 = fileRepository.search(fileName1);
        Iterator it = search1.iterator();
        int count=0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        return count;
    }

    /**
     * 查询素材库
     * @param fileName 文件名称
     * @param pageable
     * @return
     */
    @Override
    public ArrayList<File> searchMaterialBaseFile(String fileName, Pageable pageable) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
                //.withQuery(QueryBuilders.wildcardQuery("fileName.keyword", "*" + fileName + "*"))
                .withQuery(QueryBuilders.matchPhraseQuery("fileName", fileName))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .build();
                //.withFilter(QueryBuilders.termQuery("fileName", fileName)).build();
        Iterable<File> byFileNameOrTagNameFiles = fileRepository.search(searchQuery);
        return Lists.newArrayList(byFileNameOrTagNameFiles);
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
     * @param fileId 文件id
     * @return
     */
    private Integer checkFile(String fileId){
        if(Stringer.isNullOrEmpty(fileId)){
            return -1;
        }

        //文件不存在返回-2
        boolean fileNotExist = !this.checkIsExist(fileId);
        if(fileNotExist){
            return -2;
        }
        return 1;
    }



}