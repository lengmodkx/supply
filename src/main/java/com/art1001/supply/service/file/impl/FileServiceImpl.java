package com.art1001.supply.service.file.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.file.File;
import com.art1001.supply.mapper.file.FileMapper;
import com.art1001.supply.service.file.FileService;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

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
	
}