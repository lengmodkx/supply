package com.art1001.supply.service.log.impl;

import java.util.List;
import javax.annotation.Resource;
import com.art1001.supply.mapper.log.LogMapper;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.log.Log;

/**
 * ServiceImpl
 */
@Service
public class LogServiceImpl implements LogService {

	/** Mapper接口*/
	@Resource
	private LogMapper logMapper;
	
	/**
	 * 查询分页数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Log> findLogPagerList(Pager pager){
		return logMapper.findLogPagerList(pager);
	}

	/**
	 * 通过id获取单条数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public Log findLogById(String id){
		return logMapper.findLogById(id);
	}

	/**
	 * 通过id删除数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteLogById(String id){
		logMapper.deleteLogById(id);
	}

	/**
	 * 修改数据
	 * 
	 * @param log
	 */
	@Override
	public void updateLog(Log log){
		logMapper.updateLog(log);
	}

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
		log.setContent(ShiroAuthenticationManager.getUserEntity().getUserName() + " " + content);
		//哪个用户操作产生的日志
		log.setMemberId(ShiroAuthenticationManager.getUserId());
		//对哪个信息的操作
		log.setPublicId(publicId);
		//创建时间
		log.setCreateTime(System.currentTimeMillis());
		logMapper.saveLog(log);
		Log returnLog = logMapper.findLogById(log.getId());
		return returnLog;
	}

	/**
	 * 保存log日志 和 聊天
	 * @param log 实体信息
	 * @return
	 */
	@Override
	public Log saveLog(Log log) {
		logMapper.saveLog(log);
		return logMapper.findLogById(log.getId());
	}

	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	@Override
	public List<Log> findLogAllList(Log log){
		return logMapper.findLogAllList(log);
	}

	/**
	 * 查询出最近5条日志记录
	 * @param publicId 哪个信息的日志
	 * @return 日志集合
	 */
	@Override
	public List<Log> initLog(String publicId) {
		return logMapper.initLog(publicId);
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
	 * 初始一个信息下的所有 日志信息 和聊天信息
	 * @param publicId 信息id
	 * @return
	 */
	@Override
	public List<Log> initAllLog(String publicId) {
		return logMapper.initAllLog(publicId);
	}

	/**
	 * 撤回消息
	 * @param id 消息id
	 */
	@Override
	public void withdrawMessage(String id) {
		logMapper.withdrawMessage(id);
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