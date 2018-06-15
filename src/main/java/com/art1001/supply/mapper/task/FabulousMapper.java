package com.art1001.supply.mapper.task;

import com.art1001.supply.entity.task.Fabulous;
import com.art1001.supply.entity.task.Task;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author heshaohua
 * @Title:
 * @Description: 得赞数的dao接口
 * @date 2018/6/14 11:40
 */
@Mapper
public interface FabulousMapper {


    /**
     * 用户赞后添加至该关系表
     * @param fabulous 的实体信息
     * @return
     */
    int addFabulous(Fabulous fabulous);

    /**
     * 判断当前用户有没有给当前任务点赞
     * @param taskId 当前任务id
     * @param memberId 当前登录用户id
     * @return
     */
    int judgeFabulous(@Param("taskId") String taskId, @Param("memberId") String memberId);

    /**
     * 用户取消对当前任务的赞
     * @param taskId 当前任务id
     * @param memberId 当前用户id
     * @return
     */
    @Delete("delete from prm_fabulous where task_id = #{taskId} and member_id = #{memberId}")
    int cancelFabulous(@Param("taskId") String taskId,@Param("memberId") String memberId);
}
