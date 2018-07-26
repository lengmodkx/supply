package com.art1001.supply.entity.binding;

import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.Task;
import lombok.Data;

import java.util.List;

/**
 * @author heshaohua
 * @Title: BindingVO
 * @Description: TODO
 * @date 2018/7/14 18:55
 **/
@Data
public class BindingVo {
    private List<Task> taskList;
    private List<Share> shareList;
    private List<File> fileList;
    private List<Schedule> scheduleList;
    private String publicType;
}
