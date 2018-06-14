package com.art1001.supply.service.file.impl;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.file.FileMapper;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.util.AliyunOss;
import com.art1001.supply.util.IdGen;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * fileServiceImpl
 */
@Service("fileService1")
public class FileServiceImpl implements FileService {

	/** fileMapper接口*/
	@Resource
	private FileMapper fileMapper;
	
	/**
	 * 查询分页file数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<File> findFilePagerList(Pager pager){
		return fileMapper.findFilePagerList(pager);
	}

	/**
	 * 通过id获取单条file数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public File findFileById(String id){
		return fileMapper.findFileById(id);
	}

	/**
	 * 通过id删除file数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteFileById(String id){
		fileMapper.deleteFileById(id);
	}

	/**
	 * 修改file数据
	 * 
	 * @param file
	 */
	@Override
	public void updateFile(File file){
		fileMapper.updateFile(file);
	}
	/**
	 * 保存file数据
	 * 
	 * @param file
	 */
	@Override
	public void saveFile(File file){
		fileMapper.saveFile(file);
	}
	/**
	 * 获取所有file数据
	 * 
	 * @return
	 */
	@Override
	public List<File> findFileAllList(){
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
		String[] childFolderNameArr = {"图片","文档" ,"视频" ,"音频"};
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
    public File fileUpload() {
        return null;
    }

    @Override
    public List<File> findChildFile(String projectId, String parentId) {
        return fileMapper.findChildFile(projectId, parentId);
    }

}