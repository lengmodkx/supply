package com.art1001.supply.application.assembler.recyclebin;

import com.art1001.supply.api.request.RecycleBinParamDTO;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.util.ValidatedUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author heshaohua
 * @version 1.0.0
 * @date 2020年03月12日 13:19:00
 */
@Component
public class RecycleBinParamDTOAssembler {

    public Task recycleBinParamTransFormTask(RecycleBinParamDTO recycleBinParamDTO){
        ValidatedUtil.filterNullParam(recycleBinParamDTO);
        Task task = new Task();
        task.setTaskId(recycleBinParamDTO.getPublicId());
        task.setTaskGroupId(recycleBinParamDTO.getGroupId());
        task.setTaskMenuId(recycleBinParamDTO.getMenuId());
        task.setUpdateTime(System.currentTimeMillis());
        task.setProjectId(recycleBinParamDTO.getProjectId());
        return task;
    }


    public List<File> recycleBinParamTransFormFileList(RecycleBinParamDTO recycleBinParamDTO){
        ValidatedUtil.filterNullParam(recycleBinParamDTO);

        List<String> fileIdList = recycleBinParamDTO.getFileIdList();
        if(CollectionUtils.isEmpty(fileIdList)){
            return new LinkedList<>();
        }

        List<File> fileList = new ArrayList<>();

        fileIdList.forEach(id -> {
            File file = new File();
            file.setFileId(id);
            file.setUpdateTime(System.currentTimeMillis());
            file.setFileDel(1);
            fileList.add(file);
        });

        return fileList;
    }

    public File recycleBinParamTransFormFile(RecycleBinParamDTO recycleBinParamDTO){
        ValidatedUtil.filterNullParam(recycleBinParamDTO);

        File file = new File();
        file.setFileId(recycleBinParamDTO.getPublicId());
        file.setUpdateTime(System.currentTimeMillis());


        return file;
    }

    public Share recycleBinParamTransFormShare(RecycleBinParamDTO recycleBinParamDTO){
        ValidatedUtil.filterNullParam(recycleBinParamDTO);

        Share share = new Share();

        share.setId(recycleBinParamDTO.getPublicId());
        share.setUpdateTime(System.currentTimeMillis());

        return share;
    }

    public Schedule recycleBinParamTransFormSchedule(RecycleBinParamDTO recycleBinParamDTO){
        ValidatedUtil.filterNullParam(recycleBinParamDTO);

        Schedule schedule = new Schedule();

        schedule.setScheduleId(recycleBinParamDTO.getPublicId());
        schedule.setUpdateTime(System.currentTimeMillis());

        return schedule;
    }

    public Tag recycleBinParamTransFormTag(RecycleBinParamDTO recycleBinParamDTO){
        ValidatedUtil.filterNullParam(recycleBinParamDTO);

        Tag tag = new Tag();
        tag.setTagId(Long.valueOf(recycleBinParamDTO.getPublicId()));
        tag.setUpdateTime(System.currentTimeMillis());

        return tag;
    }
}
