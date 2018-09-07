package com.art1001.supply.service.collect.impl;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.collect.PublicCollect;
import com.art1001.supply.entity.collect.PublicCollectVO;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.mapper.collect.PublicCollectMapper;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * collectServiceImpl 收藏任务，日程，文件，分享的service实现
 */
@Service
public class PublicCollectServiceImpl implements PublicCollectService {

	/** collectMapper接口*/
	@Resource
	private PublicCollectMapper publicCollectMapper;

	/** 任务的逻辑层接口 */
	@Resource
    private TaskService taskService;

	/** 文件的逻辑层接口 */
	@Resource
    private FileService fileService;

	/** 分享的逻辑层接口 */
	@Resource
    private ShareService shareService;

	/** 日程的逻辑层接口 */
    @Resource
    private ScheduleService scheduleService;

	/**
	 * 查询分页collect数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<PublicCollect> findPublicCollectPagerList(Pager pager){
		return publicCollectMapper.findPublicCollectPagerList(pager);
	}

	/**
	 * 通过id获取单条collect数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public PublicCollect findPublicCollectById(String id){
		return publicCollectMapper.findPublicCollectById(id);
	}

	/**
	 * 通过id删除collect数据
	 *
	 * @param memberId 当前用户id
	 * @param taskId 任务id
	 */
	@Override
	public int deletePublicCollectById(String memberId,String taskId){
		return publicCollectMapper.deletePublicCollectById(taskId);
	}

	/**
	 * 修改collect数据
	 * 
	 * @param taskCollect
	 */
	@Override
	public void updatePublicCollect(PublicCollect taskCollect){
		publicCollectMapper.updatePublicCollect(taskCollect);
	}

	/**
	 * 保存collect数据
	 *
	 * @param taskCollect
	 */
	@Override
	public int savePublicCollect(PublicCollect taskCollect){
		return publicCollectMapper.savePublicCollect(taskCollect);
	}
	/**
	 * 获取所有collect数据
	 * 
	 * @return
	 */
	@Override
	public List<PublicCollect> findPublicCollectAllList(){
		return publicCollectMapper.findPublicCollectAllList();
	}

	/**
	 * 判断当前用户有没有
	 * @param memberId 当前登录用户id
	 * @param publicId 任务/日程/文件/分享的id
	 * @param collectType 收藏的类型 任务/日程/文件/分享
	 * @return
	 */
	@Override
	public int judgeCollectPublic(String memberId, String publicId, String collectType) {
		return publicCollectMapper.judgeCollectPublic(memberId,publicId,collectType);
	}

	/**
	 * 重写接口方法
	 * 数据: 根据收藏类型查询该用户的所有收藏
	 * 功能: 查看我的收藏
	 * 逻辑处理:
	 * @param memberId 用户id
	 * @return 返回收藏实体类集合信息
	 */
	@Override
	public List<PublicCollect> findMyCollect(String memberId,String type) {
		return publicCollectMapper.findMyCollect(memberId,type);
	}

	/**
	 * 重写接口 取消收藏的方法
	 * 数据: 根据收藏记录的id 删除一条收藏记录
	 * 功能: 取消收藏
	 * 数据处理:
	 * @param publicCollectId 该条收藏记录id
	 * @return
	 */
	@Override
	public int cancelCollect(String publicCollectId) {
		return publicCollectMapper.cancelCollect(publicCollectId);
	}

	/**
	 * 重写接口 查询收藏数据的方法
	 * 数据: 1.根据前台传过来的收藏类型查询数据 2.根据出的收藏数据id 去查询对应的收藏信息(任务,日程,文件,分享)
	 * 功能: 在页面个上根据用户点击的收藏类型 查询出想对应的收藏数据
	 * @param memberId 当前用户id
	 * @param type 收藏的类型 (任务,文件,日程,分享)
	 * @return 收藏实体信息的集合
	 */
	@Override
	public List<PublicCollectVO> listMyCollect(String memberId, String type) {
	    //查出收藏信息的id集合
        List<PublicCollect> publicCollects = publicCollectMapper.listMyCollect(memberId, type);
        List<PublicCollectVO> publicCollectVOs = new ArrayList<PublicCollectVO>();
        for (PublicCollect p : publicCollects) {
            PublicCollectVO publicCollectVO = new PublicCollectVO();
            publicCollectVO.setId(p.getId());
            if(BindingConstants.BINDING_TASK_NAME.equals(p.getCollectType())){
                publicCollectVO.setTask(taskService.findTaskByTaskId(p.getPublicId()));
                publicCollectVO.setCollectType(p.getCollectType());
            }
            if(BindingConstants.BINDING_FILE_NAME.equals(p.getCollectType())){
                publicCollectVO.setFile(fileService.findFileById(p.getPublicId()));
                publicCollectVO.setCollectType(p.getCollectType());
            }
            if(BindingConstants.BINDING_SHARE_NAME.equals(p.getCollectType())){
                publicCollectVO.setShare(shareService.findById(p.getPublicId()));
                publicCollectVO.setCollectType(p.getCollectType());
            }
            if(BindingConstants.BINDING_SCHEDULE_NAME.equals(p.getCollectType())){
                publicCollectVO.setSchedule(scheduleService.findScheduleById(p.getPublicId()));
                publicCollectVO.setCollectType(p.getCollectType());
            }
            publicCollectVOs.add(publicCollectVO);
        }
        return publicCollectVOs;
	}

	/**
	 * 收藏项 (文件,日程,分享)
	 * @param publicId 项id
	 * @param publicType 项的类型 (文件,日程,分享)
	 * @return
	 */
	@Override
	public void collectItem(String publicId, String publicType) {
		PublicCollect publicCollect = new PublicCollect();
		//设置收藏的id
		publicCollect.setId(IdGen.uuid());
		publicCollect.setMemberId(ShiroAuthenticationManager.getUserEntity().getId());
		//设置收藏的任务id
		publicCollect.setPublicId(publicId);
		//设置收藏的类型
		publicCollect.setCollectType(publicType);
		//设置这条收藏的创建时间
		publicCollect.setCreateTime(System.currentTimeMillis());
		//设置这条收藏的更新时间
		publicCollect.setUpdateTime(System.currentTimeMillis());
		publicCollectMapper.savePublicCollect(publicCollect);
	}

	/**
	 * 判断一下该用户是否收藏 当前信息
	 * @param publicId 信息id
	 * @return
	 */
	@Override
	public boolean isCollItem(String publicId) {
		return publicCollectMapper.isCollItem(publicId,ShiroAuthenticationManager.getUserId()) > 0 ? false : true;
	}

	/**
	 * 根据用户取消收藏信息
	 * @param publicId 信息id
	 * @return
	 */
	@Override
	public int cancelCollectByUser(String publicId) {
		return publicCollectMapper.cancelCollectByUser(publicId, ShiroAuthenticationManager.getUserId());
	}

	/**
	 * 删除关于 此项的所有收藏记录
	 */
	@Override
	public void deleteCollectByItemId(String publicId) {
		publicCollectMapper.deleteCollectByItemId(publicId);
	}

	/**
	 * 删除关于多个此项的所有收藏记录
	 */
	@Override
	public void deleteManyCollectByItemId(List<String> publicId) {
		publicCollectMapper.deleteManyCollectByItemId(publicId);
	}
}