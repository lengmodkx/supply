package com.art1001.supply.quartz;

import org.quartz.Job;

public interface QuartzService {


    /**
     * 修改任务JobDateMap
     *
     * @param cls  任务类
     * @param bJob 任务类属性
     * @return 是否修改成功
     */
    boolean modifyJobDateMap(Class<? extends Job> cls, MyJob bJob);

    /**
     * 添加定时任务
     *
     * @param cls  任务类
     * @param bJob 任务类属性
     * @return 是否添加成功
     */
    boolean addJobByCronTrigger(Class<? extends Job> cls, MyJob bJob);
}
