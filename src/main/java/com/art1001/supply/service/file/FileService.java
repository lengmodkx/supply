package com.art1001.supply.service.file;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.file.File;


/**
 * fileService接口
 */
public interface FileService {

	/**
	 * 查询分页file数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<File> findFilePagerList(Pager pager);

	/**
	 * 通过id获取单条file数据
	 * 
	 * @param id
	 * @return
	 */
	public File findFileById(String id);

	/**
	 * 通过id删除file数据
	 * 
	 * @param id
	 */
	public void deleteFileById(String id);

	/**
	 * 修改file数据
	 * 
	 * @param file
	 */
	public void updateFile(File file);

	/**
	 * 保存file数据
	 * 
	 * @param file
	 */
	public void saveFile(File file);

	/**
	 * 获取所有file数据
	 * 
	 * @return
	 */
	public List<File> findFileAllList();
	
}