package com.art1001.supply.service.task.impl;

import com.art1001.supply.api.request.WorkingHoursRequestParam;
import com.art1001.supply.application.assembler.WorkingRequestParamAssembler;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskWorkingHours;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.task.TaskWorkingHoursMapper;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.task.TaskWorkingHoursService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heshaohua
 * @since 2020-03-23
 */
@Service
public class TaskWorkingHoursServiceImpl extends ServiceImpl<TaskWorkingHoursMapper, TaskWorkingHours> implements TaskWorkingHoursService {

    @Resource
    private WorkingRequestParamAssembler workingRequestParamAssembler;

    @Resource
    private TaskService taskService;


    @Override
    public List<TaskWorkingHours> getWorkingHoursList(String taskId) {

        LambdaQueryWrapper<TaskWorkingHours> eq = new QueryWrapper<TaskWorkingHours>()
                .lambda().eq(TaskWorkingHours::getTaskId, taskId)
                .orderByAsc(TaskWorkingHours::getCreateTime);

        return this.list(eq);
    }

    @Override
    public void addition(WorkingHoursRequestParam workingHoursRequestParam) {
        TaskWorkingHours taskWorkingHours = workingRequestParamAssembler.workingParamTransFormWorkingHours(workingHoursRequestParam);
        this.save(taskWorkingHours);

        this.calculationHoursAndSave(taskWorkingHours.getTaskId());
    }

    @Override
    public void removeWorkingHours(String id) {
        TaskWorkingHours taskWorkingHours = Optional.ofNullable(this.getById(id)).orElseThrow(() -> new ServiceException("工时信息不存在！"));
        //移除该工时信息
        this.removeById(id);
        //重新计算任务总工时并且更新
        this.calculationHoursAndSave(taskWorkingHours.getTaskId());
    }

    /**
     * 重新计算任务的总工时并且更新
     * @param taskId 任务id
     */
    private void calculationHoursAndSave(String taskId){
        Optional.ofNullable(taskId).orElseThrow(() -> new ServiceException("taskId为空，不能重新计算工时。"));
        List<TaskWorkingHours> workingHoursList = this.getWorkingHoursList(taskId);

        //计算出新的总工时
        double total = workingHoursList.stream().mapToDouble(TaskWorkingHours::getHours).sum();

        //更新总工时
        Task task = new Task();
        task.setTaskId(taskId);
        task.setUpdateTime(System.currentTimeMillis());
        task.setTotalWorkHours(total);
        taskService.updateById(task);
    }
}
