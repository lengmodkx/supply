package com.art1001.supply.mapper.log;

import com.art1001.supply.entity.log.LogExportRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LogExportRecordMapper extends BaseMapper<LogExportRecord> {
    @Select(" select id,commit_time,complete_time,commit_member_id,export_member_id,condition_start,condition_end,status from prm_log_export_record order by commit_time desc")
    List<LogExportRecord> getList();
}
