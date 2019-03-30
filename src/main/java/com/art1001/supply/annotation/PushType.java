package com.art1001.supply.annotation;
public enum PushType {
    Default(0,""),
//    A(1,"移除了执行者"),
//    B(2,"移除了参与者"),
//    C(3,"添加了参与者"),
//    D(4,"更新了重复规则"),
//    E(5,"更新了备注"),
//    F(6,"更新任务优先级为"),
//    G(7,"更新了其他"),
//    H(8,"添加了子任务"),
//    I(9,"重做了子任务"),
//    J(10,"清除了开始时间"),
//    K(11,"清除了截止时间"),
//    L(12,"更新截止时间为"),
//    M(13,"更新开始时间为"),
//    N(14,"关联了任务"),
//    O(15,"恢复了任务"),
//    P(16,"把任务移入了回收站"),
//    Q(17,"重做了任务"),
//    S(19,"完成了任务"),
//    T(20,"更新了内容"),
//    U(21,"把任务执行者指派给了"),
//    V(22,"将任务从"),
//    W(23,"移动到了"),
//    X(24,"将任务移动到了"),
//    Y(25,"将此任务转为了顶级任务"),
//    R(26,"关联了分享"),
//    A222(27,"关联了日程"),
//    A3(28,"关联了文件"),
//    A4(29,"取消了关联分享"),
//    A5(30,"取消了关联日程"),
//    A6(31,"取消了关联文件"),
//    A7(32,"取消了关联任务"),
//    A8(33,"转换了子任务"),
//    A9(34,"为任务"),
//    A10(35,"创建了标签"),
//    A11(36,"删除了标签"),
//    A12(37,"完成了子任务"),
//    A13(38,"更新提醒模式为"),
//    A14(39,"发送消息"),
//    A15(40,"在父任务"),
//    A16(41,"下创建了任务"),
//    A17(42,"取消了关联"),
//    A18(43,"更新任务名称为"),
//    A19(44,"更新了参与者"),
//    A20(45,"添加了标签"),
//    A21(46,"更新任务的重复"),
//    A22(47,"更新了执行者"),
//    A23(48,"任务发送文件"),
//    A24(49,"移除标签"),
//    A25(50,"更新任务开始结束时间"),
//    A26(51,"清空了备注"),
//    A27(52,"恢复了分享"),
//    A28(53,"恢复了文件"),
//    A29(54,"恢复了日程"),
//    A30(55,"更新标签"),
//    A31(56,"添加附件"),
//    A32(57,"添加普通附件"),

    B1(58,"添加分享"),
    B2(58,"更新分享"),

    A1(1,"创建任务"),
    A2(2,"删除任务"),
    A3(3,"完成任务"),
    A4(4,"重做任务"),
    A5(5,"更新任务名称"),
    A6(6,"更新任务执行者"),
    A7(7,"更新任务开始时间"),
    A8(8,"更新任务结束时间"),
    A9(9,"更新任务重复性"),
    A10(10,"更新任务提醒"),
    A11(11,"更新任务备注"),
    A12(12,"更新任务优先级"),
    A13(13,"新增子任务"),
    A14(14,"更新任务参与者"),
    A15(15,"复制任务"),
    A16(16,"移动任务"),
    A17(17,"将任务移到回收站"),
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

    D1(1,"修改了日程标题"),

    C1(1,"创建了文件夹"),
    C2(2,"上传文件"),
    C3(3,"上传模型文件"),
    C4(4,"更新文件版本"),
    C5(5,"更新模型文件版本"),
    C6(6,"删除文件"),
    C7(7,"从回收站中恢复文件"),

    E1(1,"添加了标签"),
    E2(2,"移除了标签");

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
