package com.art1001.supply.enums;

/**
 * @author heshaohua
 * @Description: 日志的功能名称
 * @date 2018/6/11 16:36
 */
public enum TaskLogFunction {
    A("移除了执行者"),
    B("移除了参与者"),
    C("添加了参与者"),
    D("更新了重复规则"),
    E("更新了备注"),
    F("更新任务优先级为"),
    G("更新了其他"),
    H("添加了子任务"),
    I("重做了子任务"),
    J("清除了开始时间"),
    K("清除了截止时间"),
    L("更新截止时间为"),
    M("更新开始时间为"),
    N("关联了任务"),
    O("恢复了任务"),
    P("把任务移入了回收站"),
    Q("重做了任务"),
    R("创建了任务"),
    S("完成了任务"),
    T("更新了内容"),
    U("指派给了"),
    V("将任务从"),
    W("移动到了"),
    X("将任务移动到了");

    private String name;
    private TaskLogFunction(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }
}
