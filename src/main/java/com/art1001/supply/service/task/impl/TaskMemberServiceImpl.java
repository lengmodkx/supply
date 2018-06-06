package com.art1001.supply.service.task.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.task.TaskMember;
import com.art1001.supply.mapper.task.TaskMemberMapper;
import com.art1001.supply.service.task.TaskMemberService;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * taskMemberServiceImpl
 */
@Service
public class TaskMemberServiceImpl implements TaskMemberService {

	/** taskMemberMapper接口*/
	@Resource
	private TaskMemberMapper taskMemberMapper;
	
	/**
	 * 查询分页taskMember数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<TaskMember> findTaskMemberPagerList(Pager pager){
		return taskMemberMapper.findTaskMemberPagerList(pager);
	}

	/**
	 * 通过id获取单条taskMember数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public TaskMember findTaskMemberById(String id){
		return taskMemberMapper.findTaskMemberById(id);
	}

	/**
	 * 通过id删除taskMember数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteTaskMemberById(String id){
		taskMemberMapper.deleteTaskMemberById(id);
	}

	/**
	 * 修改taskMember数据
	 * 
	 * @param taskMember
	 */
	@Override
	public void updateTaskMember(TaskMember taskMember){
		taskMemberMapper.updateTaskMember(taskMember);
	}
	/**
	 * 保存taskMember数据
	 * 
	 * @param taskMember
	 */
	@Override
	public void saveTaskMember(TaskMember taskMember){
		taskMemberMapper.saveTaskMember(taskMember);
	}
	/**
	 * 获取所有taskMember数据
	 * 
	 * @return
	 */
	@Override
	public List<TaskMember> findTaskMemberAllList(){
		return taskMemberMapper.findTaskMemberAllList();
	}
	
}