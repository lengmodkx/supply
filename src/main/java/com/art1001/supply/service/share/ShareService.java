package com.art1001.supply.service.share;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
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
}