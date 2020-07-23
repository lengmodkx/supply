package com.art1001.supply.service.log.impl;

import com.art1001.supply.entity.log.LogExportRecord;
import com.art1001.supply.mapper.log.LogExportRecordMapper;
import com.art1001.supply.service.log.LogExportRecordService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    @Override
    public Integer saveInfo(String memberId, Long startTime, Long endTime) {

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
        return 1;
    }

    @Override
    public List<LogExportRecord> getAll() {
       return logExportRecordMapper.getList();
    }
}
