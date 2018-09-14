package com.art1001.supply.entity.statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * 模块
 *
 * @author chaiwei
 * @date 2018-09-13 15:19
 */
public class ChartsVO {

    private String taskGroup;

    private String executor;

    private String date;

    private List unfinished;

    private List finishd;


    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List getUnfinished() {
        return unfinished;
    }

    public void setUnfinished(List unfinished) {
        this.unfinished = unfinished;
    }

    public List getFinishd() {
        return finishd;
    }

    public void setFinishd(List finishd) {
        this.finishd = finishd;
    }

    @Override
    public String toString() {
        return "ChartsVO{" +
                "taskGroup='" + taskGroup + '\'' +
                ", executor='" + executor + '\'' +
                ", date='" + date + '\'' +
                ", unfinished=" + unfinished +
                ", finishd=" + finishd +
                '}';
    }
}
