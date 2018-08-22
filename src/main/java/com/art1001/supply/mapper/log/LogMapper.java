package com.art1001.supply.mapper.log;

import java.util.List;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.base.Pager;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * mapper接口
 */
@Mapper
public interface LogMapper {

	/**
	 * 查询分页数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<Log> findLogPagerList(Pager pager);

	/**
	 * 通过id获取单条数据
	 * 
	 * @param id
	 * @return
	 */
	Log findLogById(String id);

	/**
	 * 通过id删除数据
	 * 
	 * @param id
	 */
	void deleteLogById(String id);

	/**
	 * 修改数据
	 * 
	 * @param log
	 */
	void updateLog(Log log);

	/**
	 * 保存数据
	 * 
	 * @param log
	 */
	void saveLog(Log log);

	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	List<Log> findLogAllList(Log log);

	/**
	 * 查询出5条最近的日志记录
	 * @param publicId
	 * @return 日志集合
	 */
    List<Log> initLog(String publicId);

    List<Log> findLogByProjectId(String projectId);

	/**
	 * 删除某个信息的所有日志和聊天信息
	 * @param publicId 信息id
	 */
	@Delete("delete from prm_log where public_id = #{publicId}")
	void deleteByPublicId(String publicId);
}