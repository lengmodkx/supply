package com.art1001.supply.service.task;

import com.art1001.supply.api.request.WorkingHoursRequestParam;
import com.art1001.supply.entity.task.TaskWorkingHours;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author heshaohua
 * @since 2020-03-23
 */
public interface TaskWorkingHoursService extends IService<TaskWorkingHours> {

    /**
     * 根据任务id获取任务的工时列表
     * @param taskId 任务id
     * @return 工时列表
     */
    List<TaskWorkingHours> getWorkingHoursList(String taskId);

    /**
     * 添加任务工时
     * @param workingHoursRequestParam 参数信息
     */
    void addition(WorkingHoursRequestParam workingHoursRequestParam);

    /**
     * 移除一个总工时
     * @param id 工时id
     */
    void removeWorkingHours(String id);
}
