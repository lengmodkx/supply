package com.art1001.supply.service.file;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.file.FileLog;


/**
 * fileService接口
 */
public interface FileLogService {

	/**
	 * 查询分页file数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<FileLog> findFileLogPagerList(Pager pager);

	/**
	 * 通过id获取单条file数据
	 * 
	 * @param id
	 * @return
	 */
	public FileLog findFileLogById(String id);

	/**
	 * 通过id删除file数据
	 * 
	 * @param id
	 */
	public void deleteFileLogById(String id);

	/**
	 * 修改file数据
	 * 
	 * @param fileLog
	 */
	public void updateFileLog(FileLog fileLog);

	/**
	 * 保存file数据
	 * 
	 * @param fileLog
	 */
	public void saveFileLog(FileLog fileLog);

	/**
	 * 获取所有file数据
	 * 
	 * @return
	 */
	public List<FileLog> findFileLogAllList();
	
}