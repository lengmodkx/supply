package com.art1001.supply.service.recycle.impl.item;

import com.art1001.supply.api.request.RecycleBinParamDTO;
import com.art1001.supply.application.assembler.recyclebin.RecycleBinParamDTOAssembler;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.recycle.AbstractRecycleBin;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.util.ValidatedUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author heshaohua
 */
@Slf4j
@Service
public class TaskRecycleBinImpl extends AbstractRecycleBin {

    @Resource
    private TaskService taskService;

    @Override
    public void moveToRecycleBin(RecycleBinParamDTO recycleBinParamDTO) {
        ValidatedUtil.filterNullParam(recycleBinParamDTO);

        String taskId = recycleBinParamDTO.getPublicId();
        Task task = new Task();
        task.setTaskId(taskId);
        task.setUpdateTime(System.currentTimeMillis());
        task.setTaskDel(1);

        //更新库
        taskService.updateById(task);


    }

    @Override
    public void recoveryItem(RecycleBinParamDTO recycleBinParamDTO) {
        ValidatedUtil.filterNullParam(recycleBinParamDTO);

        Task task = recycleBinAssembler.recycleBinParamTransFormTask(recycleBinParamDTO);
        task.setTaskDel(0);
        taskService.updateById(task);
    }

    @Override
    public void saveLog(String publicId) {

        if(StringUtils.isEmpty(publicId)){
            log.error("任务恢复后，信息id为空，无法保存操作日志。[{}]", publicId);
            return;
        }
        //记录操作日志
        logService.saveLog(publicId, TaskLogFunction.P.getName(),1);
    }
}
