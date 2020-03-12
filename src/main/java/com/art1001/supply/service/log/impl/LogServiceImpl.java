package com.art1001.supply.service.log.impl;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.application.assembler.LogAssembler;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.log.LogSendParam;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.log.LogMapper;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import com.art1001.supply.util.ObjectsUtil;
import com.art1001.supply.util.RedisUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.locationtech.spatial4j.io.ShapeIO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ServiceImpl
 * @author Administrator
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper,Log> implements LogService {

	/** Mapper接口*/
	@Resource
	private LogMapper logMapper;

	@Resource
	private RedisUtil redisUtil;

	@Resource
	private UserService userService;

	@Resource
	private LogAssembler logAssembler;
	/**
	 * 保存数据
	 *
	 * @param publicId 任务,文件,日程,分享的id
	 * @param content 日志的内容
	 */
	@Override
	public Log saveLog(String publicId,String content,int logFlag){
		String userId = ShiroAuthenticationManager.getUserId();
		UserEntity userEntity = userService.findById(userId);
		Log log = new Log();
		//设置logId
		log.setId(IdGen.uuid());
		//0:日志 1:聊天评论
		log.setLogType(0);
		log.setContent(userEntity.getUserName() + " " + content);
		//哪个用户操作产生的日志
		log.setMemberId(userId);
		//对哪个信息的操作
		log.setPublicId(publicId);
		//创建时间
		log.setCreateTime(System.currentTimeMillis());
		logMapper.insert(log);
		return logMapper.findLogById(log.getId());
	}

	/**
	 * 查询出最近5条日志记录
	 * @param publicId 哪个信息的日志
	 * @return 日志集合
	 */
	@Override
	public List<Log> initLog(String publicId) {
		List<Log> logs = logMapper.initLog(publicId);
		//Collections.reverse(logs);
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

	/**
	 * 加载剩余消息数据
	 * @param publicId 公共id
	 * @param surpluscount 剩余消息数
	 * @return 剩余消息数据
	 */
	@Override
	public List<Log> getSurplusMsg(String publicId, Integer surpluscount) {
		List<Log> logs = logMapper.selectSurplusMsg(publicId, surpluscount);
		Collections.reverse(logs);
		return logs;
	}

	@Override
	public Log sendChat(LogSendParam logSendParam) {
		UserEntity byId = userService.getById(ShiroAuthenticationManager.getUserId());
		Log log = logAssembler.logSendParamTransFormLog(logSendParam);
		log.setMemberName(byId.getUserName());
		log.setMemberImg(byId.getImage());

		if(CollectionUtils.isNotEmpty(logSendParam.getMentionIdList())){
			log.setMentions(String.join(",", logSendParam.getMentionIdList()));
		}

		this.save(log);
		return log;
	}
}