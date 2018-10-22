package com.art1001.supply.mapper.log;

import com.art1001.supply.entity.log.Log;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * mapper接口
 */
@Mapper
public interface LogMapper extends BaseMapper<Log> {

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

	/**
	 * 初始一个信息下的所有 日志信息 和聊天信息
	 * @param publicId 信息id
	 * @return
	 */
    List<Log> initAllLog(String publicId);

	/**
	 * 撤回消息
	 * @param id 消息id
	 */
	@Update("update prm_log set content = null,file_ids = null,log_is_withdraw = 1 where id = #{id}")
    void withdrawMessage(String id);

	/**
	 * 删除多个任务的日志信息
	 * @param publicId 信息id 如:(任务id,文件id 等);
	 */
    void deleteManyByPublicId(List<String> publicId);
}