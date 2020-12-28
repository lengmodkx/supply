package com.art1001.supply.entity.user;

import com.art1001.supply.util.LongToDeteSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * @ClassName WorkBenchInfoVo
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/12/23 15:15
 * @Discription 工作台信息Vo
 */
@Data
public class WorkBenchInfoVo {
    private String roleKey;
    private String roleName;
    private String image;
    private String nickName;
    private String loginIp;
    @JsonSerialize(using = LongToDeteSerializer.class)
    private Long loginTime;
}
