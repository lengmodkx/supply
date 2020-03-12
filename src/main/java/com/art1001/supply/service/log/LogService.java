package com.art1001.supply.service.log;

import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.log.LogSendParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * Service接口
 */
public interface LogService extends IService<Log> {

	/**
	 * 保存数据
	 *
	 * @param publicId 任务,文件,日程,分享的id
	 * @param content 日志的内容
	 * @param logFlag 用来标识 是 1.任务 2.文件 3.日程 4.分享 谁的日志
	 */
	public Log saveLog(String publicId,String content,int logFlag);


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

	/**
	 * 删除多个任务的日志信息
	 * @param publicId 信息id 如:(任务id,文件id 等);
	 */
	void deleteManyByPublicId(List<String> publicId);

	/**
	 * 加载剩余消息数据
	 * @param publicId 公共id
	 * @param surpluscount 剩余消息数
	 * @return 剩余消息的数据
	 */
	List<Log> getSurplusMsg(String publicId, Integer surpluscount);

	/**
	 * 评论,发送消息
	 * @param logSendParam 消息参数
	 * @return 消息信息
	 */
    Log sendChat(LogSendParam logSendParam);
}