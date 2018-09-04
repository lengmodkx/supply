package com.art1001.supply.mapper.share;

import java.util.List;

import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.base.Pager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;

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
    @Update("update prm_share set tag_ids = '' where id = #{shareId}")
    void shareClearTag(String shareId);

    /**
     * 根据id 查询出该分享的标题
     * @param publicId 分享id
     * @return
     */
    @Select("select title from prm_share where id = #{publicId}")
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
    @Update("update prm_share set is_del = 0 where id = #{shareId}")
    void recoveryShare(String shareId);

    /**
     * 移入回收站
     * @param shareId 分享id
     */
    @Update("update prm_share set is_del = 1,update_time = #{currTime} where id = #{shareId}")
    void moveToRecycleBin(@Param("shareId") String shareId, @Param("currTime")long currTime);

    /**
     * 根据分享id 查询出分享的 参与者id
     * @param shareId 分享id
     */
    @Select("select uids from prm_share where id = #{shareId}")
    String findUidsByShareId(String shareId);
}