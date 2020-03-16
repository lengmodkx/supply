package com.art1001.supply.service.share.impl;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.share.ShareApiBean;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.mapper.share.ShareMapper;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.tagrelation.TagRelationService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.util.DateUtil;
import com.art1001.supply.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * shareServiceImpl
 */
@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper,Share> implements ShareService {

	/** shareMapper接口*/
	@Resource
	private ShareMapper shareMapper;

	@Resource
    private UserService userService;

	@Resource
    private LogService logService;

	@Resource
	private TagRelationService tagRelationService;

	@Resource
	private BindingService bindingService;
	/**
	 * 查询分页share数据
	 */
	@Override
	public List<Share> findByProjectId(String projectId, Integer isDel){
		List<Share> byProjectId = shareMapper.findByProjectId(projectId, isDel);
		byProjectId.forEach(s -> {
			s.setCreateTimeStr(DateUtils.getDateStr(new Date(s.getCreateTime()), "yyyy-MM-dd HH:mm:ss"));
		});
		return byProjectId;
	}

	/**
	 * 通过id获取单条share数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public Share findById(String id){
		return shareMapper.findById(id);
	}

	/**
	 * 更新分享的参与者信息
	 * @param shareId 分享的id
	 * @param ids 要添加的成员id
	 */
	@Override
	public Boolean updateMembers(String shareId, String ids) {
		Share share = new Share();
		share.setId(shareId);
		share.setUids(ids);
		return shareMapper.updateById(share) > 0;
	}

	/**
	 * 根据id 查询出该分享的标题
	 * @param publicId 分享id
	 * @return
	 */
	@Override
	public String findShareNameById(String publicId) {
		return shareMapper.findShareNameById(publicId);
	}

	/**
	 * 查询出在回收站中的分享
	 * @param projectId 项目id
	 * @return 该项目下所有在回收站的分享集合
	 */
	@Override
	public List<RecycleBinVO> findRecycleBin(String projectId) {
		return shareMapper.findRecycleBin(projectId);
	}

	/**
	 * 恢复分享内容
	 * @param shareId 分享的id
	 */
	@Override
	public void recoveryShare(String shareId) {
		shareMapper.recoveryShare(shareId);
		logService.saveLog(shareId,TaskLogFunction.A27.getName(),1);
	}

	/**
	 * 移入回收站
	 * @param shareId 分享id
	 */
	@Override
	public void moveToRecycleBin(String shareId) {
		shareMapper.moveToRecycleBin(shareId,System.currentTimeMillis());
	}

	/**
	 * 复制分享
	 * @param shareId 分享id
	 * @param projectId 项目id
	 */
	@Override
	public void copyShare(String shareId, String projectId) {
		Share share = shareMapper.findById(shareId);
		share.setId("");
		share.setProjectId(projectId);
		save(share);
	}

	@Override
	public void moveShare(String shareId, String projectId) {
		Share share = shareMapper.findById(shareId);
		share.setUids("");
		share.setProjectId(projectId);
		updateById(share);
		tagRelationService.deleteItemTagRelation(shareId,"分享");
		bindingService.deleteByPublicId(shareId);
	}

	@Override
	public void updatePrivacy(String shareId) {
		Share share = shareMapper.findById(shareId);
		if(share.getIsPrivacy()==0) share.setIsPrivacy(1);
		else share.setIsPrivacy(0);
		updateById(share);
	}

	/**
	 * 根据id 查询出分享信息
	 * @param shareId 分享id
	 * @return
	 */
	@Override
	public Share findByIdAllInfo(String shareId) {
		Share share = shareMapper.findById(shareId);
		//获取该任务的未读消息数
		int unMsgCount = logService.count(new QueryWrapper<Log>().eq("public_id", share.getId())) - 10;
		share.setUnReadMsg(unMsgCount > 0 ? unMsgCount : 0);
		bindingService.setBindingInfo(share.getId(),null,null,share,null);
		return share;
	}

	/**
	 * 查询分享部分信息 (项目名称,分享名称,执行者头像,标题,内容)
	 * @param id 分享id
	 * @return
	 */
	@Override
	public ShareApiBean findShareApiBean(String id) {
		return shareMapper.selectShareApiBean(id);
	}

	/**
	 * 获取分享信息
	 * @param projectId  项目id
	 * @return 分享信息
	 */
	@Override
	public List<Share> getBindInfo(String projectId) {
		return shareMapper.getBindInfo(projectId);
	}

	/**
	 * 获取所有绑定到该标签的分享信息
	 * @param tagId 标签id
	 * @return 分享集合
	 */
	@Override
	public List<Share> getBindTagInfo(Long tagId) {
		return shareMapper.selectBindTagInfo(tagId);
	}

	/**
	 * 根据分享id 获取到分享的项目id
	 *
	 * @param shareId 分享id
	 * @return 项目id
	 */
	@Override
	public String getProjectIdByShareId(String shareId) {
		Share share = shareMapper.selectOne(new QueryWrapper<Share>().lambda().eq(Share::getId, shareId).select(Share::getProjectId));
		return  share == null ? null : share.getProjectId();
	}

	@Override
	public String[] getJoinAndCreatorId(String publicId) {
		LambdaQueryWrapper<Share> select = new QueryWrapper<Share>().lambda()
				.select(Share::getUids, Share::getMemberId)
				.eq(Share::getId, publicId);

		Share one = this.getOne(select);
		if(one != null){
			if(one.getUids() != null){
				StringBuilder userIds = new StringBuilder(one.getUids());
				if(StringUtils.isNotEmpty(one.getMemberId())){
					userIds.append(",").append(one.getMemberId());
				}
				return userIds.toString().split(",");
			} else if(one.getMemberId() != null){
				return new String[]{one.getMemberId()};
			}
		}
		return new String[0];
	}
}