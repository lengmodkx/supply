package com.art1001.supply.service.log.impl;

import com.art1001.supply.application.assembler.LogAssembler;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.log.LogExportRecord;
import com.art1001.supply.entity.log.LogSendParam;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.log.LogMapper;
import com.art1001.supply.service.log.LogExportRecordService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import com.art1001.supply.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ServiceImpl
 *
 * @author Administrator
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogService {

    /**
     * Mapper接口
     */
    @Resource
    private LogMapper logMapper;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private UserService userService;

    @Resource
    private LogAssembler logAssembler;

    @Resource
    private OrganizationService organizationService;

    @Resource
    private ProjectService projectService;

    @Resource
    private LogExportRecordService logExportRecordService;

    /**
     * 保存数据
     *
     * @param publicId 任务,文件,日程,分享的id
     * @param content  日志的内容
     */
    @Override
    public Log saveLog(String publicId, String content, int logFlag) {
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
     *
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
     *
     * @param publicId 信息id 如:(任务id,文件id 等);
     */
    @Override
    public void deleteByPublicId(String publicId) {
        logMapper.deleteByPublicId(publicId);
    }

    /**
     * 删除多个任务的日志信息
     *
     * @param publicId 信息id 如:(任务id,文件id 等);
     */
    @Override
    public void deleteManyByPublicId(List<String> publicId) {
        logMapper.deleteManyByPublicId(publicId);
    }

    /**
     * 加载剩余消息数据
     *
     * @param publicId     公共id
     * @param surpluscount 剩余消息数
     * @return 剩余消息数据
     */
    @Override
    public List<Log> getSurplusMsg(String publicId, Integer surpluscount) {
        List<Log> logs = logMapper.selectSurplusMsg(publicId, surpluscount);
        //Collections.reverse(logs);
        return logs;
    }

    @Override
    public Log sendChat(LogSendParam logSendParam) {
        UserEntity byId = userService.getById(ShiroAuthenticationManager.getUserId());
        Log log = logAssembler.logSendParamTransFormLog(logSendParam);
        log.setMemberName(byId.getUserName());
        log.setMemberImg(byId.getImage());

        if (CollectionUtils.isNotEmpty(logSendParam.getMentionIdList())) {
            log.setMentions(String.join(",", logSendParam.getMentionIdList()));
        }

        this.save(log);
        return log;
    }

    @Override
    public List<Log> getMyDynamic(String userId) {
        return logMapper.getMyDynamic(userId);
    }

    /**
     * @param orgId
     * @param memberId
     * @param startTime
     * @param endTime
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @return:
     * @Description: 根据条件查询日志
     * @create: 16:38 2020/7/13
     */
    @Override
    public List<Log> selectLogByCondition(String orgId, String memberId, Long startTime, Long endTime) {
        List<String> projectIds = organizationService.getProject(orgId).stream().map(Project::getProjectId).collect(Collectors.toList());
        List<Log> logs = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(projectIds)) {
            logs = logMapper.selectLogByCondition(projectIds, memberId, startTime, endTime);

            Optional.ofNullable(logs).ifPresent(log -> {
                log.stream().forEach(r -> {
                    r.setProjectName(projectService.getById(r.getProjectId()).getProjectName());
                    if (r.getContent().contains(r.getMemberName())) {
                        r.setContent(r.getContent().replaceAll(r.getMemberName(), ""));
                    }
                });
            });
        }
        return logs;
    }

    /**
     * 处理导出的日志数据
     *
     * @return
     */
    @Override
    public List<Log> getMyLog() {
        return logMapper.getMyLog(ShiroAuthenticationManager.getUserId());
    }

    @Override
    public List<Log> exportLogByExcel(String orgId, String id) {
        LogExportRecord exportRecord = logExportRecordService.getById(id);
        List<Log> logs = this.selectLogByCondition(orgId, exportRecord.getExportMemberId(), exportRecord.getConditionStart(), exportRecord.getConditionEnd());
        Optional.ofNullable(logs).ifPresent(logList -> {
            logList.stream().forEach(log -> {
                Assert.notNull(log.getCreateTime(), "time is null");
                String format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.ofInstant(Instant.ofEpochMilli(log.getCreateTime()), ZoneId.systemDefault()));
                log.setOutPutTime(format);

            });
        });
        return logs;
    }
}