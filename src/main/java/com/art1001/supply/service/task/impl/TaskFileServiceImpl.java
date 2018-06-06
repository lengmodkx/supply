package com.art1001.supply.service.task.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.task.TaskFile;
import com.art1001.supply.mapper.task.TaskFileMapper;
import com.art1001.supply.service.task.TaskFileService;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * taskFileServiceImpl
 */
@Service
public class TaskFileServiceImpl implements TaskFileService {

	/** taskFileMapper接口*/
	@Resource
	private TaskFileMapper taskFileMapper;
	
	/**
	 * 查询分页taskFile数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<TaskFile> findTaskFilePagerList(Pager pager){
		return taskFileMapper.findTaskFilePagerList(pager);
	}

	/**
	 * 通过id获取单条taskFile数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public TaskFile findTaskFileById(String id){
		return taskFileMapper.findTaskFileById(id);
	}

	/**
	 * 通过id删除taskFile数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteTaskFileById(String id){
		taskFileMapper.deleteTaskFileById(id);
	}

	/**
	 * 修改taskFile数据
	 * 
	 * @param taskFile
	 */
	@Override
	public void updateTaskFile(TaskFile taskFile){
		taskFileMapper.updateTaskFile(taskFile);
	}
	/**
	 * 保存taskFile数据
	 * 
	 * @param taskFile
	 */
	@Override
	public void saveTaskFile(TaskFile taskFile){
		taskFileMapper.saveTaskFile(taskFile);
	}
	/**
	 * 获取所有taskFile数据
	 * 
	 * @return
	 */
	@Override
	public List<TaskFile> findTaskFileAllList(){
		return taskFileMapper.findTaskFileAllList();
	}
	
}