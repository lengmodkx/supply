package com.art1001.supply.mapper.user;

import java.util.List;
import com.art1001.supply.entity.user.UserNews;
import com.art1001.supply.entity.base.Pager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * mapper接口
 */
public interface UserNewsMapper {

	/**
	 * 查询分页数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<UserNews> findUserNewsPagerList(Pager pager);

	/**
	 * 通过id获取单条数据
	 * 
	 * @param id
	 * @return
	 */
	UserNews findUserNewsById(String id);

	/**
	 * 通过id删除数据
	 * 
	 * @param id
	 */
	void deleteUserNewsById(String id);

	/**
	 * 修改数据
	 * 
	 * @param userNews
	 */
	void updateUserNews(UserNews userNews);

	/**
	 * 保存数据
	 * 
	 * @param userNews
	 */
	void saveUserNews(UserNews userNews);

	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	List<UserNews> findUserNewsAllList();

	/**
	 * 根据 信息id 和用户id  查询 用户有没有这消息的记录
	 * @param publicId 信息id
	 * @param userId 用户id
	 * @return
	 */
	@Select("select count(0) from prm_user_news where news_public_id = #{publicId} and news_to_user = #{userId}")
    int findUserNewsByPublicId(@Param("publicId") String publicId, @Param("userId") String userId);

	/**
	 * 根据信息id 用户id 查询出 这条信息的消息数
	 * @param publicId 信息id
	 * @param userId 用户id
	 * @return 消息数
	 */
	@Select("select news_count from prm_user_news where news_public_id = #{publicId} and news_to_user = #{userId}")
	Integer findNewsCountByPublicId(@Param("publicId")String publicId, @Param("userId")String userId);

	/**
	 * 根据用户的id 查询出用户的未读消息条数
	 * @param userId 用户id
	 * @return
	 */
	@Select("select IFNULL(SUM(news_count),0) from prm_user_news where news_handle = 0 and news_to_user = #{userId}")
    int findUserNewsCount(String userId);

	/**
	 * 根据用户的id 查询出该用户的所有消息
	 * @param userId 用户id
	 * @return
	 */
	List<UserNews> findAllUserNewsByUserId(String userId);

	/**
	 * 修改消息的 状态(已读,未读)  并且将消息条数设为0
	 * @param id 消息id
	 */
	@Update("update prm_user_news set news_handle = 1,news_count = 0 where news_id = #{id}")
    void updateIsRead(String id);
}