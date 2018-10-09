package com.art1001.supply.service.collect.impl;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.collect.PublicCollect;
import com.art1001.supply.mapper.collect.PublicCollectMapper;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * collectServiceImpl 收藏任务，日程，文件，分享的service实现
 */
@Service
public class PublicCollectServiceImpl extends ServiceImpl<PublicCollectMapper, PublicCollect> implements PublicCollectService {

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
	 * @param publicCollect
	 */
	@Override
	public int savePublicCollect(PublicCollect publicCollect){
		publicCollect.setId(IdGen.uuid());
		return publicCollectMapper.savePublicCollect(publicCollect);
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

	/**
	 * 更新收藏表的 json 数据信息
	 * @param id 信息id
	 * @param obj 要更新的字段信息
	 * @param type 要更新的信息类型
	 */
	@Override
	public void updateJson(String id, Object obj, String type) {
		publicCollectMapper.updateJson(id,obj,type);
	}
}