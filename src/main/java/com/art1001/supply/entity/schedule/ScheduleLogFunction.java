package com.art1001.supply.entity.schedule;

import lombok.Data;

/**
 * @author heshaohua
 * @Description:
 * @date 2018/8/1 11:20
 */
public enum ScheduleLogFunction {

    A(1,"更新了日程的"),
    B(2,"开始时间"),
    C(3,"结束时间"),
    D(4,"标题"),
    E(5,"重复规则"),
    F(6,"提醒模式"),
    G(7,"地点"),
    H(8,"全天日程"),
    I(9,"非全天日程"),
    J(10,"设置日程为"),
    K(11,"全天模式"),
    L(12,"添加标签"),
    M(13,"移除标签");

    private ScheduleLogFunction(int id, String name){
        this.id = id;
        this.name = name;
    }
    private int id;
    private String name;
    public int getId(){
        return this.id;
    }

    public void setId(String id){
        this.name = id;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }
}
