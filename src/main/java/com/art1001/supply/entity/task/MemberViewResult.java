package com.art1001.supply.entity.task;

import lombok.Data;

import java.util.List;

/**
 * @author shaohua
 * @date 2020/3/6 11:39
 */
@Data
public class MemberViewResult {

    private String userId;

    private String userName;
    private String accountName;
    private String image;
    private List<Task> taskList;

}
