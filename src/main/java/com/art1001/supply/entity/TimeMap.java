package com.art1001.supply.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName TimeMap
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/4/30 17:15
 * @Discription 存放时间戳和年月信息的实体类
 */
@Data
public class TimeMap {
    private Long timeStamp;
    private String yearOfMonth;
}
