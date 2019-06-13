package com.art1001.supply.service.recycle.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.recycle.RecycleParams;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.recycle.RecycleBinService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.util.Stringer;
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
        return fileService.findRecycleBin(projectId, fileType);
    }

    @Override
    public Integer recovery(RecycleParams recycleParams) {
        if(Constants.TASK_EN.equals(recycleParams.getPublicType())){
            taskService.recoveryTask(recycleParams.getPublicId(),
                    recycleParams.getProjectId(),
                    recycleParams.getGroupId(),
                    recycleParams.getMenuId());
        }
        if(Constants.FILE_EN.equals(recycleParams.getPublicType())){
            fileService.recoveryFile(recycleParams.getPublicId());
        }
        if(Constants.SHARE_EN.equals(recycleParams.getPublicType())){
            shareService.recoveryShare(recycleParams.getPublicId());
        }
        if(Constants.SCHEDULE_EN.equals(recycleParams.getPublicType())){
            scheduleService.recoverySchedule(recycleParams.getPublicId());
        }
        return 1;
    }
}
