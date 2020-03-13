package com.art1001.supply.service.recycle.impl.item;

import com.art1001.supply.api.request.RecycleBinParamDTO;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.service.recycle.AbstractRecycleBin;
import com.art1001.supply.service.schedule.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author heshaohua
 * @version 1.0.0
 * @date 2020年03月12日 14:28:00
 */
@Slf4j
@Service
public class ScheduleRecycleBinImpl extends AbstractRecycleBin {

    @Resource
    private ScheduleService scheduleService;

    @Override
    public void moveToRecycleBin(RecycleBinParamDTO recycleBinParamDTO) {
        Schedule schedule = recycleBinAssembler.recycleBinParamTransFormSchedule(recycleBinParamDTO);
        schedule.setIsDel(1);

        scheduleService.updateById(schedule);
    }

    @Override
    public void recoveryItem(RecycleBinParamDTO recycleBinParamDTO) {
        Schedule schedule = recycleBinAssembler.recycleBinParamTransFormSchedule(recycleBinParamDTO);
        schedule.setIsDel(0);

        scheduleService.updateById(schedule);
        this.saveLog(schedule.getScheduleId());
    }

    @Override
    public void saveLog(String publicId) {
        if(StringUtils.isEmpty(publicId)){
            log.error("恢复日程成功后，日程id为空，无法保存操作日志。 [{}]", publicId);
            return;
        }
        logService.saveLog(publicId, TaskLogFunction.A29.getName(), 1);
    }
}
