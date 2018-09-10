package com.art1001.supply.service.project.impl;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.mapper.project.ProjectMapper;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.util.IdGen;
import org.springframework.stereotype.Service;

/**
 * projectServiceImpl
 */
@Service
public class ProjectServiceImpl implements ProjectService {

	/** projectMapper接口*/
	@Resource
	private ProjectMapper projectMapper;

	@Resource
	private TaskService taskService;

	@Resource
	private ShareService shareService;

	@Resource
	private ScheduleService scheduleService;

	@Resource
	private TagService tagService;

	@Resource
	private FileService fileService;

	@Resource
	private RelationService relationService;
	
	/**
	 * 查询分页project数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Project> findProjectPagerList(Pager pager){
		return projectMapper.findProjectPagerList(pager);
	}

	/**
	 * 通过projectId获取单条project数据
	 * 
	 * @param projectId
	 * @return
	 */
	@Override 
	public Project findProjectByProjectId(String projectId){
		return projectMapper.findProjectByProjectId(projectId);
	}

	/**
	 * 通过projectId删除project数据
	 * 
	 * @param projectId
	 */
	@Override
	public void deleteProjectByProjectId(String projectId){
		projectMapper.deleteProjectByProjectId(projectId);
	}

	/**
	 * 修改project数据
	 * 
	 * @param project
	 */
	@Override
	public void updateProject(Project project){
		projectMapper.updateProject(project);
	}
	/**
	 * 保存project数据
	 * 
	 * @param project
	 */
	@Override
	public void saveProject(Project project){
		project.setProjectId(IdGen.uuid());

		projectMapper.saveProject(project);
	}
	/**
	 * 获取所有project数据
	 * 
	 * @return
	 */
	@Override
	public List<Project> findProjectAllList(){
		return projectMapper.findProjectAllList();
	}

	/**
	 * 获取项目创建人的项目
	 *
	 * @return
	 */
	@Override
	public List<Project> findProjectByMemberId(String memberId,int projectDel) {
		return projectMapper.findProjectByMemberId(memberId,projectDel);
	}

	/**
	 * 查询出当前用户所执行的任务的 任务信息 和 项目信息
	 * @param id 当前用户id
	 * @return
	 */
	@Override
	public List<Project> findProjectAndTaskByExecutorId(String id) {
		return projectMapper.findProjectAndTaskByExecutorId(id);
	}

    /**
     * 查询出当前用户所参与的任务的 任务信息 和 项目信息
     * @param id 当前用户id
     * @return
     */
    @Override
    public List<Project> findProjectAndTaskByUserId(String id) {
        return projectMapper.findProjectAndTaskByUserId(id);
    }

    /**
     * 查询出当前用户所创建的任务的 任务信息 和 项目信息
     * @param id 当前用户id
     * @return
     */
    @Override
    public List<Project> findProjectAndTaskByCreateMember(String id) {
        return projectMapper.findProjectAndTaskByCreateMember(id);
    }

	/**
	 * 查询出用户参与的所有项目信息
	 * @param uId 用户id
	 * @return 项目实体集合
	 */
	@Override
	public List<Project> listProjectByUid(String uId) {
		return projectMapper.listProjectByUid(uId);
	}

	/**
	 * 数据:查询出当前用户收藏的所有项目
	 * 功能:添加关联页面展示 星标项目
	 * @param uId 用户id
	 * @return 用户收藏的所有项目信息
	 */
	@Override
	public List<Project> listProjectByUserCollect(String uId) {
		return projectMapper.listProjectByUserCollect(uId);
	}

	/**
	 *  根据用户id查询所有项目，包含我创建的，我参与的，星标项目，回收站的项目
	 * @param userId 用户id
	 * @return
	 */
	@Override
	public List<Project> findProjectByUserId(String userId,int collect) {
		return projectMapper.findProjectByUserId(userId,collect);
	}

	/**
	 * 用于展示回收站的数据
	 * @param projectId 项目id
	 * @param type 选项卡的类型
	 */
	@Override
	public List<RecycleBinVO> recycleBinInfo(String projectId, String type) {

		List<RecycleBinVO> recycleBin = new ArrayList<RecycleBinVO>();
		if(BindingConstants.BINDING_TASK_NAME.equals(type)){
			recycleBin = taskService.findRecycleBin(projectId);
		}
		if(BindingConstants.BINDING_FILE_NAME.equals(type)){
			recycleBin = fileService.findRecycleBin(projectId,type);
		}
		if(BindingConstants.BINDING_SHARE_NAME.equals(type)){
			recycleBin = shareService.findRecycleBin(projectId);
		}
		if(BindingConstants.BINDING_SCHEDULE_NAME.equals(type)){
			recycleBin = scheduleService.findRecycleBin(projectId);
		}
		if(BindingConstants.BINDING_TAG_NAME.equals(type)){
			recycleBin = tagService.findRecycleBin(projectId);
		}
		if(BindingConstants.TASK_GROUP.equals(type)){
			recycleBin = relationService.findRecycleBin(projectId);
		}
		if(BindingConstants.FOLDER.equals(type)){
			recycleBin = fileService.findRecycleBin(projectId,type);
		}
		return recycleBin;
	}
}