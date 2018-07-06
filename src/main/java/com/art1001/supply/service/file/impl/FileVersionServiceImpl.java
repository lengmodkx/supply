package com.art1001.supply.service.file.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.file.FileVersion;
import com.art1001.supply.mapper.file.FileVersionMapper;
import com.art1001.supply.service.file.FileVersionService;
import com.art1001.supply.util.IdGen;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * fileServiceImpl
 */
@Service
public class FileVersionServiceImpl implements FileVersionService {

	/** fileMapper接口*/
	@Resource
	private FileVersionMapper fileVersionMapper;

	@Resource
	private FileVersionService fileVersionService;
	
	/**
	 * 查询分页file数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<FileVersion> findFileVersionPagerList(Pager pager){
		return fileVersionMapper.findFileVersionPagerList(pager);
	}

	/**
	 * 通过id获取单条file数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public FileVersion findFileVersionById(String id){
		return fileVersionMapper.findFileVersionById(id);
	}

	/**
	 * 通过id删除file数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteFileVersionById(String id){
		fileVersionMapper.deleteFileVersionById(id);
	}

	/**
	 * 修改file数据
	 * 
	 * @param fileVersion
	 */
	@Override
	public void updateFileVersion(FileVersion fileVersion){
		fileVersionMapper.updateFileVersion(fileVersion);
	}
	/**
	 * 保存file数据
	 * 
	 * @param fileVersion
	 */
	@Override
	public void saveFileVersion(FileVersion fileVersion){
		fileVersion.setId(IdGen.uuid());
		fileVersion.setCreateTime(System.currentTimeMillis());
		fileVersion.setUpdateTime(System.currentTimeMillis());
		int count = fileVersionService.findCountByFileId(fileVersion.getFileId());
		if (count > 0) {
			// 设置master
			fileVersionService.updateMasterByFileId(fileVersion.getFileId());
		}
		fileVersionMapper.saveFileVersion(fileVersion);
	}

	@Override
	public void updateMasterByFileId(String fileId) {
		fileVersionMapper.updateMasterByFileId(fileId);
	}

	@Override
	public int findCountByFileId(String fileId) {
		return fileVersionMapper.findCountByFileId(fileId);
	}

	/**
	 * 获取所有file数据
	 * 
	 * @return
	 */
	@Override
	public List<FileVersion> findFileVersionAllList(){
		return fileVersionMapper.findFileVersionAllList();
	}

	@Override
	public List<FileVersion> findByFileId(String fileId) {
		return fileVersionMapper.findByFileId(fileId);
	}

}