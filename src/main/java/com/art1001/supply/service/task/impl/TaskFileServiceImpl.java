package com.art1001.supply.service.task.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.mapper.task.TaskFileMapper;
import com.art1001.supply.service.task.TaskFileService;
import com.art1001.supply.util.IdGen;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.task.TaskFile;

/**
 * ServiceImpl
 */
@Service
public class TaskFileServiceImpl extends ServiceImpl<TaskFileMapper,TaskFile> implements TaskFileService {

	/** Mapper接口*/
	@Resource
	private TaskFileMapper taskFileMapper;


	@Override
	public List<TaskFile> findTaskFileAllList(String taskId) {
		return taskFileMapper.findTaskFileAllList(taskId);
	}

	@Override
	public void saveTaskFile(TaskFile taskFile) {
		taskFileMapper.saveTaskFile(taskFile);
	}

	@Override
	public TaskFile findTaskFileById(String id) {
		return taskFileMapper.findTaskFileById(id);
	}
}