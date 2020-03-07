package com.art1001.supply.entity.task;

import com.art1001.supply.entity.user.UserEntity;
import lombok.Data;

import java.util.List;

/**
 * @author shaohua
 * @date 2020/3/6 11:39
 */
@Data
public class MemberViewResult extends UserEntity {

    private List<Task> taskList;


    public static MemberViewResult buildExecutor(List<Task> taskList){
        MemberViewResult memberViewResult = new MemberViewResult();
        memberViewResult.setAccountName("");
        memberViewResult.setImage("");
        memberViewResult.setUserName("待认领");
        memberViewResult.setUserId("0");
        memberViewResult.setTaskList(taskList);
        return memberViewResult;
    }
}
