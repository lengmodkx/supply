package com.art1001.supply.service.log.impl;

import com.art1001.supply.entity.log.Log;
import com.art1001.supply.mapper.log.LogMapper;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.util.IdGen;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * ServiceImpl
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper,Log> implements LogService {

	/** Mapper接口*/
	@Resource
	private LogMapper logMapper;
	/**
	 * 保存数据
	 *
	 * @param publicId 任务,文件,日程,分享的id
	 * @param content 日志的内容
	 */
	@Override
	public Log saveLog(String publicId,String content,int logFlag){
		Log log = new Log();
		//设置logId
		log.setId(IdGen.uuid());
		//0:日志 1:聊天评论
		log.setLogType(0);
		//是(1.任务,2.文件,3.日程,4.分享) 哪个的日志
		log.setLogFlag(logFlag);
		log.setContent("何少华" + " " + content);
		//哪个用户操作产生的日志
		log.setMemberId("0ea056a169424185993ff8c6fa832dd5");
		//对哪个信息的操作
		log.setPublicId(publicId);
		//创建时间
		log.setCreateTime(System.currentTimeMillis());
		logMapper.insert(log);
		Log returnLog = logMapper.findLogById(log.getId());
		return returnLog;
	}

	/**
	 * 查询出最近5条日志记录
	 * @param publicId 哪个信息的日志
	 * @return 日志集合
	 */
	@Override
	public List<Log> initLog(String publicId) {
		List<Log> logs = logMapper.initLog(publicId);
		Collections.reverse(logs);
		return logs;
	}

	/**
	 * 删除某个信息的日志信息
	 * @param publicId 信息id 如:(任务id,文件id 等);
	 */
	@Override
	public void deleteByPublicId(String publicId) {
		logMapper.deleteByPublicId(publicId);
	}

	/**
	 * 删除多个任务的日志信息
	 * @param publicId 信息id 如:(任务id,文件id 等);
	 */
	@Override
	public void deleteManyByPublicId(List<String> publicId) {
		logMapper.deleteManyByPublicId(publicId);
	}
}