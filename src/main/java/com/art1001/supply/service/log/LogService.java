package com.art1001.supply.service.log;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.log.Log;

/**
 * Service接口
 */
public interface LogService {

	/**
	 * 查询分页数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<Log> findLogPagerList(Pager pager);

	/**
	 * 通过id获取单条数据
	 * 
	 * @param id
	 * @return
	 */
	public Log findLogById(String id);

	/**
	 * 通过id删除数据
	 * 
	 * @param id
	 */
	public void deleteLogById(String id);

	/**
	 * 修改数据
	 * 
	 * @param log
	 */
	public void updateLog(Log log);

	/**
	 * 保存数据
	 *
	 * @param publicId 任务,文件,日程,分享的id
	 * @param content 日志的内容
	 * @param logFlag 用来标识 是 1.任务 2.文件 3.日程 4.分享 谁的日志
	 */
	public Log saveLog(String publicId,String content,int logFlag);

	/**
	 * 保存数据
	 *
	 * @param log 实体信息
	 */
	public Log saveLog(Log log);

	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	public List<Log> findLogAllList(Log log);

	/**
	 * 查询出五条最近的记录
	 * @param publicId 哪个信息的日志
	 * @return 日志集合
	 */
    List<Log> initLog(String publicId);

	/**
	 * 删除某个信息的日志信息
	 * @param publicId 信息id 如:(任务id,文件id 等);
	 */
	void deleteByPublicId(String publicId);
}