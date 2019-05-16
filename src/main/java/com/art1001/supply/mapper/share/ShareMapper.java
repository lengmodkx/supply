package com.art1001.supply.mapper.share;

import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.share.ShareApiBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * sharemapper接口
 */
@Mapper
public interface ShareMapper extends BaseMapper<Share> {

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
     * 根据项目id 返回所有的分享
     * @param projectId 项目id
     * @return 分享信息集合
     */
    List<Share> shareByProjectId(String projectId);

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

    /**
     * 查询分享部分信息 (项目名称,分享名称,执行者头像,标题,内容)
     * @param id 分享id
     * @return
     */
    ShareApiBean selectShareApiBean(String id);

    /**
     * 获取分享的绑定信息
     * @param projectId 项目id
     * @return 分享信息
     */
    List<Share> getBindInfo(@Param("projectId") String projectId);

    /**
     * 获取所有绑定到该标签的分享信息
     * @param tagId 标签id
     * @return 分享集合
     */
    List<Share> selectBindTagInfo(@Param("tagId") Long tagId);
}