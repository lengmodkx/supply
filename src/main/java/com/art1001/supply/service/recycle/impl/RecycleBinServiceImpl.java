package com.art1001.supply.service.recycle.impl;

import com.art1001.supply.api.request.RecycleBinParamDTO;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.recycle.RecycleBinService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description 回收站接口的业务实现类
 * @Date:2019/5/14 10:51
 * @Author heshaohua
 **/
@Service
public class RecycleBinServiceImpl implements RecycleBinService {

    /**
     * 注入任务逻辑层Bean
     */
    @Resource
    private TaskService taskService;

    /**
     * 注入文件逻辑层Bean
     */
    @Resource
    private FileService fileService;

    /**
     * 注入日程逻辑层Bean
     */
    @Resource
    private ShareService shareService;

    /**
     * 注入 分组/菜单的逻辑层bean
     */
    @Resource
    private RelationService relationService;

    /**
     * 注入分享逻辑层Bean
     */
    @Resource
    private ScheduleService scheduleService;

    /**
     * 注入标签逻辑层Bean
     */
    @Resource
    private TagService tagService;

    /**
     * 获取项目中回收站的指定类型的信息
     * @param projectId 项目id
     * @param type 类型
     * @param fileType 0:文件 1:文件夹
     * @return 信息
     */
    @Override
    public List<RecycleBinVO> getRecycleBinItem(String projectId, String type, String fileType) {
        if(Constants.TASK_EN.equals(type)){
           return taskService.findRecycleBin(projectId);
        }
        if(Constants.SCHEDULE_EN.equals(type)){
            return scheduleService.findRecycleBin(projectId);
        }
        if(Constants.SHARE_EN.equals(type)){
            return shareService.findRecycleBin(projectId);
        }
        if(Constants.GROUP_EN.equals(type)){
            return relationService.findRecycleBin(projectId);
        }
        if(Constants.TAG_EN.equals(type)){
            return tagService.findRecycleBin(projectId);
        }

        if(Constants.FILE_EN.equals(type)){
            return fileService.findRecycleBin(projectId, fileType);
        }
        return null;
    }

    @Override
    public void recovery(RecycleBinParamDTO recycleParamsDto) {
        if(Constants.TASK_EN.equals(recycleParamsDto.getPublicType())){
            taskService.recoveryTask(recycleParamsDto.getPublicId(),
                    recycleParamsDto.getProjectId(),
                    recycleParamsDto.getGroupId(),
                    recycleParamsDto.getMenuId());
        }
        if(Constants.FILE_EN.equals(recycleParamsDto.getPublicType())){
            fileService.recoveryFile(recycleParamsDto.getPublicId());
        }
        if(Constants.SHARE_EN.equals(recycleParamsDto.getPublicType())){
            shareService.recoveryShare(recycleParamsDto.getPublicId());
        }
        if(Constants.SCHEDULE_EN.equals(recycleParamsDto.getPublicType())){
            scheduleService.recoverySchedule(recycleParamsDto.getPublicId());
        }
        if(Constants.TAG_EN.equals(recycleParamsDto.getPublicType())){
            tagService.recoveryTag(recycleParamsDto.getPublicId());
        }
        if(Constants.GROUP_EN.equals(recycleParamsDto.getPublicType())){

        }
    }
}
