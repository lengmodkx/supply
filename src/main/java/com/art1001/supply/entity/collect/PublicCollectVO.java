package com.art1001.supply.entity.collect;

import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.Task;
import lombok.Data;

/**
 * @author heshaohua
 * @Title: PublicCollectVO
 * @Description: 收藏实体类的包装类
 * @date 2018/7/16 11:15
 **/
@Data
public class PublicCollectVO {
    private String id;
    private Task task;
    private Share share;
    private File file;
    private Schedule schedule;
    private String collectType;
}
