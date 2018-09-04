package com.art1001.supply.service.share;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.share.Share;

/**
 * shareService接口
 */
public interface ShareService {

	/**
	 * 查询项目的分享
	 *
	 * @param projectId 项目id
	 * @param isDel 是否删除  0：未删除  1：已删除
	 */
	List<Share> findByProjectId(String projectId, Integer isDel);

	/**
	 * 根据id查询
	 */
	Share findById(String id);

	/**
	 * 保存
	 */
	Share saveShare(Share share);

	/**
	 * 更新
	 */
	Share updateShare(Share share);

	/**
	 * 删除分享
	 */
	void deleteById(String id);

	/**
	 * 移除标签
	 */
    void deleteTag(String shareId, String tagIds);

	/**
	 * 根据项目id 查询分享
	 * @param projectId 项目id
	 * @return 分享的实体集合
	 */
	List<Share> shareByProjectId(String projectId);

	/**
	 * 查询出分享的参与人员
	 * @param shareId 分享的id
	 * @return 参与者的信息
	 */
    List<ProjectMember> shareJoinInfo(String shareId);

	/**
	 * 查询出项目的成员信息 排除 分享的参与者
	 * @param projectId 项目id
	 * @param shareId 分享id
	 * @return
	 */
	List<ProjectMember> findProjectMemberNotShareJoin(String projectId, String shareId);

	/**
	 * 添加或者移除分享的成员信息
	 * @param shareId 分享的id
	 * @param addUserEntity 要添加的成员id
	 */
    void addAndRemoveShareMember(String shareId, String addUserEntity);

	/**
	 * 清空分享的标签
	 * @param shareId 分享的id
	 */
	void shareClearTag(String shareId);

	/**
	 * 根据id 查询出该分享的标题
	 * @param publicId 分享id
	 */
    String findShareNameById(String publicId);

	/**
	 * 查询出在回收站中的分享
	 * @param projectId 项目id
	 * @return 该项目下所有在回收站的分享集合
	 */
	List<RecycleBinVO> findRecycleBin(String projectId);

	/**
	 * 恢复分享内容
	 * @param shareId 分享的id
	 */
	void recoveryShare(String shareId);

	/**
	 * 移入回收站
	 * @param shareId 分享id
	 */
	void moveToRecycleBin(String shareId);

	/**
	 * 根据分享id 查询出分享的 参与者id
	 * @param shareId 分享id
	 */
    String findUidsByShareId(String shareId);
}