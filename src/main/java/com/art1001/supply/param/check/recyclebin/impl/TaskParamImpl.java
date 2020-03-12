package com.art1001.supply.param.check.recyclebin.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.param.check.recyclebin.RecycleBinParamCheck;
import com.art1001.supply.util.ValidatedUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shaohua
 * @date 2020/3/12 22:54
 */
@Configuration
public class TaskParamImpl {

    @Bean
    public RecycleBinParamCheck checkTaskParam(){
        return param -> {
            if(Constants.MOVE.equals(param.getAction())){
                String projectId = param.getProjectId();
                String taskId = param.getPublicId();
                ValidatedUtil.filterNullParam(projectId, taskId);
            }

            if(Constants.RECOVERY.equals(param.getAction())){
                ValidatedUtil.filterNullParam(
                        param.getProjectId(), param.getPublicId(),
                        param.getGroupId(), param.getMenuId()
                );
            }
        };
    }

    @Bean
    public RecycleBinParamCheck checkFileParam(){
        return param -> {
            String projectId = param.getProjectId();
            String fileId = param.getPublicId();
            ValidatedUtil.filterNullParam(projectId, fileId);
        };
    }

    @Bean
    public RecycleBinParamCheck checkShareParam(){
        return param -> {
            String projectId = param.getProjectId();
            String shareId = param.getPublicId();
            ValidatedUtil.filterNullParam(projectId, shareId);
        };
    }

    @Bean
    public RecycleBinParamCheck checkScheduleParam(){
        return param -> {
            String projectId = param.getProjectId();
            String scheduleId = param.getPublicId();
            ValidatedUtil.filterNullParam(projectId, scheduleId);
        };
    }

    @Bean
    public RecycleBinParamCheck checkTagParam(){
        return param -> {
            String projectId = param.getProjectId();
            String tagId = param.getPublicId();
            ValidatedUtil.filterNullParam(projectId, tagId);
        };
    }
}
