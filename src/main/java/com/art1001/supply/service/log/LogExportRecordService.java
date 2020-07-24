package com.art1001.supply.service.log;

import com.art1001.supply.entity.log.LogExportRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface LogExportRecordService extends IService<LogExportRecord> {

    Integer saveInfo(String memberId, Long startTime, Long endTime,String orgId);

    List<LogExportRecord> getAll();

}
