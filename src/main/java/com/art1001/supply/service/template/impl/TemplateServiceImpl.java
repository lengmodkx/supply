package com.art1001.supply.service.template.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.entity.role.ProRoleUser;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.tag.TagRelation;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.template.*;
import com.art1001.supply.mapper.project.ProjectMapper;
import com.art1001.supply.mapper.project.ProjectMemberMapper;
import com.art1001.supply.mapper.relation.RelationMapper;
import com.art1001.supply.mapper.role.ProRoleMapper;
import com.art1001.supply.mapper.role.ProRoleUserMapper;
import com.art1001.supply.mapper.tag.TagMapper;
import com.art1001.supply.mapper.tagrelation.TagRelationMapper;
import com.art1001.supply.mapper.task.TaskMapper;
import com.art1001.supply.mapper.template.*;
import com.art1001.supply.service.template.TemplateService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * ServiceImpl
 */
@Service
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper,Template> implements TemplateService {

	/** Mapper接口*/
	@Resource
	private TemplateMapper templateMapper;

	@Resource
	private TemplateFileMapper templateFileMapper;

	@Resource
	private ProjectMapper projectMapper;

	@Resource
	private RelationMapper relationMapper;

	@Resource
	private ProjectMemberMapper projectMemberMapper;

	@Resource
	private ProRoleMapper proRoleMapper;

	@Resource
	private ProRoleUserMapper proRoleUserMapper;

	@Resource
	private TaskMapper taskMapper;

	@Resource
	private TagMapper tagMapper;

	@Resource
	private TagRelationMapper tagRelationMapper;

	@Resource
	private TemplateRelationMapper templateRelationMapper;

	@Resource
	private TemplateTaskMapper templateTaskMapper;


	@Resource
	private TemplateTagRelationMapper templateTagRelationMapper;
	@Resource
	private TemplateTagMapper templateTagMapper;

	/**
	 * 查询分页数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Template> findTemplatePagerList(Pager pager){
		return templateMapper.findTemplatePagerList(pager);
	}

	/**
	 * 通过templateId获取单条数据
	 * 
	 * @param templateId
	 * @return
	 */
	@Override 
	public Template findTemplateByTemplateId(String templateId){
		return templateMapper.findTemplateByTemplateId(templateId);
	}

	/**
	 * 通过templateId删除数据
	 * 
	 * @param templateId
	 */
	@Override
	public void deleteTemplateByTemplateId(String templateId){
		templateMapper.deleteTemplateByTemplateId(templateId);
	}

	/**
	 * 修改数据
	 * 
	 * @param template
	 */
	@Override
	public void updateTemplate(Template template){
		templateMapper.updateTemplate(template);
	}
	/**
	 * 保存数据
	 * 
	 * @param template
	 */
	@Override
	public Template saveTemplate(Template template){
		templateMapper.insert(template);
		TemplateRelation parentRelation = new TemplateRelation();
		parentRelation.setRelationName("任务");
		parentRelation.setLable(0);
		parentRelation.setTemplateId(template.getTemplateId());
		parentRelation.setCreator(ShiroAuthenticationManager.getUserId());
		templateRelationMapper.insert(parentRelation);
		//初始化菜单
		String[] menus = new String[]{"待处理", "进行中", "已完成"};
		for (int i = 0; i <menus.length  ; i++) {
			TemplateRelation relation = new TemplateRelation();
			relation.setRelationName(menus[i]);
			relation.setTemplateId(template.getTemplateId());
			relation.setParentId(parentRelation.getRelationId());
			relation.setCreator(ShiroAuthenticationManager.getUserId());
			relation.setLable(1);
			relation.setOrder(i);
			templateRelationMapper.insert(relation);
		}
		return template;
	}
	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	@Override
	public List<Template> findTemplateAllList(){
		return templateMapper.findTemplateAllList();
	}

	@Override
	public void addProject(String templateId,String projectName) {
		Template template = templateMapper.selectById(templateId);
		Project project = new Project();
		project.setOrganizationId(template.getOrgId());
		project.setProjectName(projectName);
		project.setCreateTime(System.currentTimeMillis());
		project.setMemberId(ShiroAuthenticationManager.getUserId());
		project.setProjectCover(template.getTemplateCover());
		//初始化项目功能菜单
		String[] funcs = new String[]{"任务", "分享", "文件", "日程", "群聊", "统计"};
		JSONArray array = new JSONArray();

		Arrays.stream(funcs).forEach(item -> {
			JSONObject object = new JSONObject();
			object.put("funcName", item);
			object.put("isOpen", true);
			array.add(object);
		});

		project.setFunc(array.toString());
		projectMapper.insert(project);

		//初始化分组
		Relation relation = new Relation();
		relation.setRelationName("任务");
		relation.setProjectId(project.getProjectId());
		relation.setCreator(ShiroAuthenticationManager.getUserId());
		relationMapper.saveRelation(relation);


		List<TemplateRelation> relations = templateRelationMapper.selectList(new QueryWrapper<TemplateRelation>().eq("template_id", templateId).eq("lable",1));

		relations.forEach(item -> {
			Relation relation1 = new Relation();
			relation1.setProjectId(project.getProjectId());
			relation1.setCreator(ShiroAuthenticationManager.getUserId());
			relation1.setRelationName(item.getRelationName());
			relation1.setOrder(item.getOrder());
			relation1.setLable(1);
			relation1.setParentId(relation.getRelationId());
			relation1.setCreateTime(System.currentTimeMillis());
			relationMapper.insert(relation1);

			List<TemplateTask> taskList = templateTaskMapper.selectList(new QueryWrapper<TemplateTask>().eq("task_menu_id", item.getRelationId()).eq("parent_id",0));
			taskList.forEach(templateTask -> {
				Task task = new Task();
				task.setTaskName(templateTask.getTaskName());
				task.setOrder(templateTask.getOrder());
				task.setProjectId(project.getProjectId());
				task.setMemberId(ShiroAuthenticationManager.getUserId());
				task.setTaskUIds(ShiroAuthenticationManager.getUserId());
				task.setRemarks(templateTask.getRemarks());
				task.setTaskMenuId(relation1.getRelationId());
				task.setTaskGroupId(relation.getRelationId());
				task.setPrivacyPattern(1);
				task.setParentId("0");
				taskMapper.insert(task);
				List<TemplateTagRelation> tagRelations = templateTagRelationMapper.selectList(new QueryWrapper<TemplateTagRelation>().eq("task_id", templateTask.getTaskId()));
				tagRelations.forEach(templateTagRelation -> {
					TemplateTag templateTag = templateTagMapper.selectById(templateTagRelation.getId());
					Tag tag = new Tag();
					tag.setTagName(templateTag.getTagName());
					tag.setCreateTime(System.currentTimeMillis());
					tag.setBgColor(templateTag.getBgColor());
					tag.setProjectId(project.getProjectId());
					tagMapper.insert(tag);

					TagRelation tagRelation = new TagRelation();
					tagRelation.setTaskId(task.getTaskId());
					tagRelation.setTagId(tag.getTagId());
					tagRelationMapper.insert(tagRelation);
				});




				dealTask(templateTask,task.getTaskId(),relation1.getRelationId(),relation.getRelationId(),project.getProjectId());
			});
		});

		//往项目用户关联表插入数据
		ProjectMember projectMember = new ProjectMember();
		projectMember.setProjectId(project.getProjectId());
		projectMember.setMemberId(ShiroAuthenticationManager.getUserId());
		projectMember.setCreateTime(System.currentTimeMillis());
		projectMember.setUpdateTime(System.currentTimeMillis());
		projectMember.setMemberLabel(1);
		projectMember.setDefaultGroup(relation.getRelationId());
		projectMember.setRoleKey("administrator");
		projectMemberMapper.insert(projectMember);

		//企业中 项目拥有者的角色信息
		ProRole orgAdminRole = proRoleMapper.selectOne(new QueryWrapper<ProRole>().eq("org_id",project.getOrganizationId()).eq("role_key",Constants.OWNER_KEY));
		ProRoleUser proRoleUser = new ProRoleUser();
		if (orgAdminRole != null) {
			proRoleUser.setRoleId(orgAdminRole.getRoleId());
		}
		proRoleUser.setUId(ShiroAuthenticationManager.getUserId());
		proRoleUser.setProjectId(project.getProjectId());
		proRoleUser.setTCreateTime(LocalDateTime.now());
		proRoleUserMapper.insert(proRoleUser);
		Integer userProjectCount = projectMemberMapper.selectCount( new QueryWrapper<ProjectMember>().lambda()
				.eq(ProjectMember::getMemberId, ShiroAuthenticationManager.getUserId()));
		if (userProjectCount == 1) {
			//构造出更新记录的sql表达式
			LambdaUpdateWrapper<ProjectMember> upUserProjectUw = new UpdateWrapper<ProjectMember>().lambda()
					.eq(ProjectMember::getProjectId, project.getProjectId())
					.eq(ProjectMember::getMemberId, ShiroAuthenticationManager.getUserId());
			//给要更新的字段信息赋值
			ProjectMember projectMember1 = new ProjectMember();
			projectMember1.setCurrent(true);
			projectMember1.setUpdateTime(System.currentTimeMillis());
			projectMemberMapper.update(projectMember1, upUserProjectUw);
		}
	}

	private void dealTask(TemplateTask parentTask,String parentId,String menuId,String groupId,String projectId){
		List<TemplateTask> taskList = templateTaskMapper.selectList(new QueryWrapper<TemplateTask>().eq("parent_id",parentTask.getTaskId()));
		if(taskList!=null&&taskList.size()>0){
			taskList.forEach(templateTask -> {
				Task task = new Task();
				task.setTaskName(templateTask.getTaskName());
				task.setOrder(templateTask.getOrder());
				task.setProjectId(projectId);
				task.setMemberId(ShiroAuthenticationManager.getUserId());
				task.setTaskUIds(ShiroAuthenticationManager.getUserId());
				task.setRemarks(templateTask.getRemarks());
				task.setTaskMenuId(menuId);
				task.setTaskGroupId(groupId);
				task.setPrivacyPattern(1);
				task.setParentId(parentId);
				taskMapper.insert(task);

				List<TemplateTagRelation> tagRelations = templateTagRelationMapper.selectList(new QueryWrapper<TemplateTagRelation>().eq("task_id", templateTask.getTaskId()));
				tagRelations.forEach(templateTagRelation -> {
					TemplateTag templateTag = templateTagMapper.selectById(templateTagRelation.getId());
					Tag tag = new Tag();
					tag.setTagName(templateTag.getTagName());
					tag.setCreateTime(System.currentTimeMillis());
					tag.setBgColor(templateTag.getBgColor());
					tag.setProjectId(projectId);
					tagMapper.insert(tag);

					TagRelation tagRelation = new TagRelation();
					tagRelation.setTaskId(task.getTaskId());
					tagRelation.setTagId(tag.getTagId());
					tagRelationMapper.insert(tagRelation);
				});

				dealTask(templateTask,task.getTaskId(),menuId,groupId,parentId);
			});
		}

	}

}