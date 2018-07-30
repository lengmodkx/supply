package com.art1001.supply.service.share.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.task.TaskMember;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.share.ShareMapper;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.share.Share;

/**
 * shareServiceImpl
 */
@Service
public class ShareServiceImpl implements ShareService {

	/** shareMapper接口*/
	@Resource
	private ShareMapper shareMapper;
	
	/**
	 * 查询分页share数据
	 */
	@Override
	public List<Share> findByProjectId(String projectId, Integer isDel){
		return shareMapper.findByProjectId(projectId, isDel);
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
	 * 通过id删除share数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteById(String id){
		shareMapper.deleteById(id);
	}

	@Override
	public void deleteTag(String shareId, String tagIds) {
		shareMapper.deleteTag(shareId, tagIds);
	}

	/**
	 * 修改share数据
	 * 
	 * @param share
	 */
	@Override
	public Share updateShare(Share share){
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();

        share.setMemberId(userEntity.getId());
        share.setMemberName(userEntity.getUserName());
        share.setMemberImg(userEntity.getUserInfo().getImage());

        share.setUpdateTime(System.currentTimeMillis());
		shareMapper.updateShare(share);
		return share;
	}
	/**
	 * 保存share数据
	 * 
	 * @param share
	 */
	@Override
	public Share saveShare(Share share){
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();

	    share.setId(IdGen.uuid());
        share.setMemberId(userEntity.getId());
        share.setMemberName(userEntity.getUserName());
        share.setMemberImg(userEntity.getUserInfo().getImage());

        share.setCreateTime(System.currentTimeMillis());
        share.setUpdateTime(System.currentTimeMillis());

        shareMapper.saveShare(share);

        return share;
	}

	/**
	 * 根据项目id 查询出项目下的所有分享信息
	 * @param projectId 项目id
	 * @return 返回分享信息  集合
	 */
	@Override
	public List<Share> shareByProjectId(String projectId) {
		return shareMapper.shareByProjectId(projectId);
	}

	/**
	 * 查询出分享的参与人员
	 * @param shareId 分享的id
	 * @return 参与者的信息
	 */
	@Override
	public List<ProjectMember> shareJoinInfo(String shareId) {
		return shareMapper.shareJoinInfo(shareId);
	}

	/**
	 * 查询出项目的成员信息 排除 分享的参与者
	 * @param projectId 项目id
	 * @param shareId 分享id
	 * @return
	 */
	@Override
	public List<ProjectMember> findProjectMemberNotShareJoin(String projectId, String shareId) {
		return shareMapper.findProjectMemberShareJoin(projectId,shareId);
	}
}