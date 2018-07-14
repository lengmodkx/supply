package com.art1001.supply.entity.binding;

import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.Task;
import lombok.Data;

/**
 * @author heshaohua
 * @Title: BindingVO
 * @Description: TODO
 * @date 2018/7/14 18:55
 **/
@Data
public class BindingVO {
    private Task task;
    private Share share;
    private File file;
    private Schedule schedule;
    private String publicType;
}
