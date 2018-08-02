package com.art1001.supply.mapper.share;

import java.util.List;

import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.base.Pager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * sharemapper接口
 */
@Mapper
public interface ShareMapper {

    /**
     * 查询项目的分享
     *
     * @param projectId 项目id
     * @param isDel 是否删除  0：未删除  1：已删除
     */
    List<Share> findByProjectId(@Param("projectId") String projectId, @Param("isDel") Integer isDel);

    /**
     * 根据id查询
     */
    Share findById(String id);

    /**
     * 保存
     */
    void saveShare(Share share);

    /**
     * 更新
     */
    void updateShare(Share share);

    /**
     * 删除分享
     */
    void deleteById(String id);

    /**
     * 移除标签
     */
    void deleteTag(@Param("id") String id, @Param("tagIds") String tagIds);

    /**
     * 根据项目id 返回所有的分享
     * @param projectId 项目id
     * @return 分享信息集合
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
    List<ProjectMember> findProjectMemberShareJoin(String projectId, String shareId);

    /**
     * 清空分享的标签
     * @param shareId 分享的id
     */
    @Update("udpate prm_share set tag_ids = '' where id = #{shareId}")
    void shareClearTag(String shareId);
}