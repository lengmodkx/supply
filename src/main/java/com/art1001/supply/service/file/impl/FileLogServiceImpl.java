package com.art1001.supply.service.file.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.file.FileLog;
import com.art1001.supply.mapper.file.FileLogMapper;
import com.art1001.supply.service.file.FileLogService;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * fileServiceImpl
 */
@Service
public class FileLogServiceImpl implements FileLogService {

	/** fileMapper接口*/
	@Resource
	private FileLogMapper fileLogMapper;
	
	/**
	 * 查询分页file数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<FileLog> findFileLogPagerList(Pager pager){
		return fileLogMapper.findFileLogPagerList(pager);
	}

	/**
	 * 通过id获取单条file数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public FileLog findFileLogById(String id){
		return fileLogMapper.findFileLogById(id);
	}

	/**
	 * 通过id删除file数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteFileLogById(String id){
		fileLogMapper.deleteFileLogById(id);
	}

	/**
	 * 修改file数据
	 * 
	 * @param fileLog
	 */
	@Override
	public void updateFileLog(FileLog fileLog){
		fileLogMapper.updateFileLog(fileLog);
	}
	/**
	 * 保存file数据
	 * 
	 * @param fileLog
	 */
	@Override
	public void saveFileLog(FileLog fileLog){
		fileLogMapper.saveFileLog(fileLog);
	}
	/**
	 * 获取所有file数据
	 * 
	 * @return
	 */
	@Override
	public List<FileLog> findFileLogAllList(){
		return fileLogMapper.findFileLogAllList();
	}
	
}