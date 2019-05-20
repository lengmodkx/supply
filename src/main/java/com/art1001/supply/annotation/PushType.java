package com.art1001.supply.annotation;

public enum PushType {
    Default(0,""),
    B1(1,"添加分享"),
    B2(2,"更新分享"),
    B3(3,"置顶分享"),
    B4(4,"复制分享"),
    B5(5,"移动分享"),
    B6(6,"移到回收站"),
    B7(7,"恢复分享"),
    B8(8,"更新隐私模式"),
    B9(9,"删除分享"),
    B10(10,"更新了分享的参与者"),


    A1(1,"创建任务"),
    A2(2,"删除任务"),
    A3(3,"完成了任务"),
    A4(4,"重做了任务"),
    A5(5,"更新了任务名称"),
    A6(6,"更新任务执行者"),
    A7(7,"更新了任务开始时间"),
    A8(8,"更新了任务结束时间"),
    A9(9,"更新任务重复性"),
    A10(10,"更新任务提醒"),
    A11(11,"更新了任务备注"),
    A12(12,"更新任务优先级"),
    A13(13,"新增子任务"),
    A14(14,"更新任务参与者"),
    A15(15,"复制任务"),
    A16(16,"移动任务"),
    A17(17,"将任务移到了回收站"),
    A18(18,"更新任务隐私模式"),
    A19(19,"将子任务转顶级任务"),
    A20(20,"对任务点赞"),
    A21(21,"上传文件"),
    A22(22,"上传模型文件"),
    A23(23,"移除任务提醒规则"),
    A24(24,"更新任务提醒规则"),
    A25(25,"获取所有任务提醒"),
    A26(26,"更新提醒的成员信息"),
    A27(27,"任务排序"),
    A28(28,"关联了信息"),
    A29(29,"取消了关联"),
    A30(30,"添加了附件"),

    D1(1,"修改了日程标题"),

    C1(1,"创建了文件夹"),
    C2(2,"上传文件"),
    C3(3,"上传模型文件"),
    C4(4,"更新文件版本"),
    C5(5,"更新模型文件版本"),
    C6(6,"删除文件"),
    C7(7,"从回收站中恢复文件"),
    C8(8,"设置了隐私模式"),
    C9(9,"更新了参与者"),
    C10(10,"复制了文件"),
    C11(11,"修改文件名称"),
    C12(12,"移动了文件"),
    C13(13,"将文件移入回收站"),

    E1(1,"添加了标签"),
    E2(2,"移除了标签"),

    F1(1,"发送消息"),
    G1(1,"群聊消息"),
    G2(2,"撤回消息"),

    H1(1,"编辑列表名称"),
    H2(2,"添加新列表"),
    H3(3,"设置此列表下所有任务的截止时间"),
    H4(4,"移动此列表下所有任务"),
    H5(5,"复制此列表下所有任务"),
    H6(6,"此列表所有任务移至回收站"),
    H7(7,"设置此列表所有任务的执行者"),
    H8(8,"删除列表"),
    I1(1,"项目移至回收站");

    PushType(int id,String name){
        this.name = name;
        this.id = id;
    }
    private int id;
    private String name;

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }
}
