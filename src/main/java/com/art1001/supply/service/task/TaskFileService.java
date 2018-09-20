package com.art1001.supply.service.task;

import com.art1001.supply.entity.task.TaskFile;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * Service接口
 */
public interface TaskFileService extends IService<TaskFile> {
    /**
     * 查询任务关联的文件
     * @param taskId
     * @return
     */
    List<TaskFile> findTaskFileAllList(String  taskId);

    /**
     * 保存任务的文件
     * @param taskFile
     */
    void saveTaskFile(TaskFile taskFile);

    TaskFile findTaskFileById(String id);
}