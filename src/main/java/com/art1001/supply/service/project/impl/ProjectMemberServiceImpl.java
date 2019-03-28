package com.art1001.supply.service.project.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.project.ProjectMemberMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * projectMemberServiceImpl
 */
@Service
public class ProjectMemberServiceImpl extends ServiceImpl<ProjectMemberMapper,ProjectMember> implements ProjectMemberService {

	/** projectMemberMapper接口*/
	@Resource
	private ProjectMemberMapper projectMemberMapper;

	/**
	 * 任务逻辑层Bean 注入
	 */
	@Resource
	private TaskService taskService;

	/**
	 * 文件层逻辑层Bean
	 */
	@Resource
	private FileService fileService;

	/**
	 * 分享逻辑层Bean
	 */
	@Resource
	private ShareService shareService;

	/**
	 * 日程逻辑层Bean
	 */
	@Resource
	private ScheduleService scheduleService;

	@Resource
	private UserService userService;

	/**
	 * 注入用户的逻辑层bean
	 */
	@Resource
	private UserMapper userMapper;

	@Override
	public List<Project> findProjectByMemberId(String memberId,Integer projectDel) {
		return projectMemberMapper.findProjectByMemberId(memberId,projectDel);
	}

	@Override
	public List<ProjectMember> findByProjectId(String projectId) {
		return projectMemberMapper.findByProjectId(projectId);
	}

	/**
	 * 查询成员是否存在于项目中
	 * @param projectId 项目id
	 * @param id 用户id
	 * @return
	 */
	@Override
	public int findMemberIsExist(String projectId, String id) {
		return projectMemberMapper.findMemberIsExist(projectId,id);
	}

	/**
	 * 根据用户查询出 用户在该项目中的默认分组id
	 * @param projectId 项目id
	 * @param userId 用户id
	 * @return
	 */
	@Override
	public String findDefaultGroup(String projectId, String userId) {
		ProjectMember projectMember = projectMemberMapper.selectOne(new QueryWrapper<ProjectMember>().select("default_group").eq("project_id", projectId).eq("member_id", userId).ne("default_group", "0"));
		if(projectMember == null){
			throw new NullPointerException("该项目不存在!");
		}
		return projectMember.getDefaultGroup();
	}

	@Override
	public void updateDefaultGroup(String projectId, String userId, String groupId) {
		ProjectMember projectMember = new ProjectMember();
		projectMember.setProjectId(projectId);
		projectMember.setMemberId(userId);
		update(projectMember,new UpdateWrapper<ProjectMember>().set("default_group",groupId));
	}

	/**
	 * 获取到模块在当前项目的的参与者信息与非参与者信息
	 * @param type 模块类型
	 * @param id 信息id
	 * @param projectId 所在项目id
	 * @return 该项目成员在当前模块信息中的参与者信息与非参与者信息
	 */
	@Override
	public List<UserEntity> getModelProjectMember(String type, String id, String projectId) {
		List<UserEntity> users = new ArrayList<>();
		if(Constants.TASK.equals(type)){
			Task task = taskService.getOne(new QueryWrapper<Task>().eq("task_id", id).select("task_uids"));
			if(task != null){
				users = userMapper.findManyUserById(task.getTaskUIds());
			}
		}
		if(Constants.FILE.equals(type)){
			File file = fileService.getOne(new QueryWrapper<File>().eq("file_id",id).select("file_uids"));
			if(file != null){
				users = userMapper.findManyUserById(file.getFileUids());
			}
		}
		if(Constants.SHARE.equals(type)){
			Share share = shareService.getOne(new QueryWrapper<Share>().eq("share_id",id).select("uids"));
			if(share != null){
				users = userMapper.findManyUserById(share.getUids());
			}
		}
		if(Constants.SCHEDULE.equals(type)){
			Schedule schedule = scheduleService.getOne(new QueryWrapper<Schedule>().eq("schedule_id",id).select("member_ids"));
			if(schedule != null){
				users = userMapper.findManyUserById(schedule.getMemberIds());
			}
		}
		List<UserEntity> newUserList = users;
		List<UserEntity> projectAllMember = userService.findProjectAllMember(projectId);
		return projectAllMember.stream().filter(item -> !newUserList.contains(item)).collect(Collectors.toList());
	}

	/**
	 * 获取当前用户的星标项目
	 * @param userId 用户id
	 * @return 星标项目
	 */
	@Override
	public List<Project> getStarProject(String userId) {
		return projectMemberMapper.getStarProject(userId);
	}

	/**
	 * 获取当前用户的非星标项目
	 * @param userId 用户id
	 * @return 非星标项目
	 */
	@Override
	public List<Project> getNotStarProject(String userId) {
		return projectMemberMapper.getNotStarProject(userId);
	}
}