package com.art1001.supply.service.task.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Resource;

import com.art1001.supply.entity.task.TaskMember;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.task.TaskMemberMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.task.TaskMemberService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.util.IdGen;
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

	/** userService 接口*/
	@Resource
	private UserService userService;
	
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

	/**
	 * 保存任务和参与者的关系
	 * @param memberId 参与者们的id
	 * @param uid 当前操作用户的id 用来确认是否是该任务的创建人
	 */
	@Override
	public void saveManyTaskeMmber(String[] memberId,String uid) {
		//查询该任务的参与者信息
		List<UserEntity> userList = userService.findManyUserById(memberId);
		//循环参与者信息把信息放到任务和参与者的实体类对象中
		for (UserEntity userEntity : userList){
			TaskMember taskMember = new TaskMember();
			//设置id
			taskMember.setId(IdGen.uuid());
			//设置参与者姓名
			taskMember.setMemberName(userEntity.getUserName());
			//设置参与者id
			taskMember.setMemberId(userEntity.getId());
			//设置这条关系的创建时间
			taskMember.setCreateTime(System.currentTimeMillis());
			//设置关联类型 (1.任务 2.分享 3.日程 4.文件)
			taskMember.setPublicType("1");
			//如果当前用户的id是该任务的创建者 则把该条关系的任务角色设置为创建者
			if(userEntity.getId().equals(uid)){
				taskMember.setType("3");
			} else{
				//否则设置为参与者
				taskMember.setType("1");
			}
			//该条关系的更新时间
			taskMember.setUpdateTime(System.currentTimeMillis());
			//将该关系对象 保存至数据库
			taskMemberMapper.saveTaskMember(taskMember);
		}

	}

}