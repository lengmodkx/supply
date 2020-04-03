package com.art1001.supply.application.assembler;

import com.art1001.supply.api.request.WorkingHoursRequestParam;
import com.art1001.supply.entity.task.TaskWorkingHours;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author heshaohua
 * @version 1.0.0
 * @date 2020年03月23日 15:43:00
 */
@Component
public class WorkingRequestParamAssembler {

    @Resource
    private UserService userService;

    public TaskWorkingHours workingParamTransFormWorkingHours(WorkingHoursRequestParam workingHoursRequestParam){
        UserEntity byId = userService.getById(ShiroAuthenticationManager.getUserId());
        TaskWorkingHours taskWorkingHours = new TaskWorkingHours();
        taskWorkingHours.setId(IdGen.uuid());
        taskWorkingHours.setHours(workingHoursRequestParam.getHours());
        taskWorkingHours.setCreateTime(System.currentTimeMillis());
        taskWorkingHours.setCreateName(byId.getUserName());
        taskWorkingHours.setHoursDate(workingHoursRequestParam.getHoursDate());
        taskWorkingHours.setTaskId(workingHoursRequestParam.getTaskId());
        return taskWorkingHours;
    }
}
