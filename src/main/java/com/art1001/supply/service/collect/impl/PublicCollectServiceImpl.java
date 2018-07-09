package com.art1001.supply.service.collect.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.collect.PublicCollect;
import com.art1001.supply.mapper.collect.PublicCollectMapper;
import com.art1001.supply.service.collect.PublicCollectService;
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

	@Override
	public int judgeCollectPublic(String memberId, String publicId, String collectType) {
		return publicCollectMapper.judgeCollectPublic(memberId,publicId,collectType);
	}

}