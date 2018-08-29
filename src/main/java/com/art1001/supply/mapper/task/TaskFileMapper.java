package com.art1001.supply.mapper.task;

import java.util.List;
import com.art1001.supply.entity.task.TaskFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * mapper接口
 */
@Mapper
public interface TaskFileMapper {

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