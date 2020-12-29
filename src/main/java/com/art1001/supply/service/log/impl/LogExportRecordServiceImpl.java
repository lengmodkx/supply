package com.art1001.supply.service.log.impl;

import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.log.LogExportRecord;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.log.LogExportRecordMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.log.LogExportRecordService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;

/**
 * @ClassName LogExportRecordServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/7/17 17:40
 * @Discription
 */
@Service
public class LogExportRecordServiceImpl extends ServiceImpl<LogExportRecordMapper, LogExportRecord> implements LogExportRecordService {

    @Resource
    private LogExportRecordMapper logExportRecordMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private LogService logService;

    @Override
    public Integer saveInfo(String memberId, Long startTime, Long endTime,String orgId) {

        List<Log> logs = logService.selectLogByCondition(orgId, memberId, startTime, endTime);

        if (CollectionUtils.isNotEmpty(logs)) {
            LogExportRecord logExportRecord = new LogExportRecord();
            logExportRecord.setCommitTime(System.currentTimeMillis());
            logExportRecord.setCommitMemberId(ShiroAuthenticationManager.getUserId());
            if (StringUtils.isNotEmpty(memberId)) {
                logExportRecord.setExportMemberId(memberId);
            }
            logExportRecord.setStatus(1);
            if (startTime != null) {
                logExportRecord.setConditionStart(startTime);
            }
            if (endTime != null) {
                logExportRecord.setConditionEnd(endTime);
            }
            logExportRecord.setCompleteTime(System.currentTimeMillis());
            logExportRecordMapper.insert(logExportRecord);
        }
        return 1;
    }

    @Override
    public List<LogExportRecord> getAll() {
        List<LogExportRecord> logs = logExportRecordMapper.getList();
        if (CollectionUtils.isNotEmpty(logs)) {
            logs.stream().forEach(r->{
                UserEntity byId = userMapper.findById(r.getCommitMemberId());
                r.setCommitImg(byId.getImage());
                r.setCommitName(byId.getUserName());
                if (StringUtils.isNotEmpty(r.getExportMemberId())) {
                    UserEntity byId1 = userMapper.findById(r.getExportMemberId());
                    r.setExportName(byId1.getUserName());
                }
            });
        }
        return logs;
    }
}
