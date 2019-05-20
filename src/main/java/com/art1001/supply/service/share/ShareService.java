package com.art1001.supply.service.share;

import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.share.ShareApiBean;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * shareService接口
 */
public interface ShareService extends IService<Share> {

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
	 * 添加或者移除分享的成员信息
	 * @param shareId 分享的id
	 * @param addUserEntity 要添加的成员id
	 */
    Boolean updateMembers(String shareId, String addUserEntity);

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
	 * 复制分享
	 * @param shareId 分享id
	 * @param projectId 项目id
	 */
	void copyShare(String shareId, String projectId);

	/**
	 * 移动分享
	 * @param shareId 分享id
	 * @param projectId 项目id
	 */
	void moveShare(String shareId, String projectId);

	/**
	 * 更换分享的隐私模式
	 * @param shareId 分享id
	 */
	void updatePrivacy(String shareId);

	/**
	 * 根据id 查询出分享信息 以及 所有关联信息
	 * @param shareId 分享id
	 * @return
	 */
	Share findByIdAllInfo(String shareId);

	/**
	 * 查询分享部分信息 (项目名称,分享名称,执行者头像,标题,内容)
	 * @param id 分享id
	 * @return
	 */
	ShareApiBean findShareApiBean(String id);

	/**
	 * 获取项目下的分享信息
	 * 注:用于绑定
	 * 因为分享信息只包含 id,title
	 * @param projectId  项目id
	 * @return 分享信息
	 */
    List<Share> getBindInfo(String projectId);

	/**
	 * 获取所有绑定到该标签的分享信息
	 * @param tagId 标签id
	 * @return 分享集合
	 */
	List<Share> getBindTagInfo(Long tagId);

	/**
	 * 根据分享id 获取到分享的项目id
	 * @param shareId 分享id
	 * @return 项目id
	 */
	String getProjectIdByShareId(String shareId);
}