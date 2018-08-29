package com.art1001.supply.service.task.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.mapper.task.TaskFileMapper;
import com.art1001.supply.service.task.TaskFileService;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.task.TaskFile;

/**
 * ServiceImpl
 */
@Service
public class TaskFileServiceImpl implements TaskFileService {

	/** Mapper接口*/
	@Resource
	private TaskFileMapper taskFileMapper;
	

	
}