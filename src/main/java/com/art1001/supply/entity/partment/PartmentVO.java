package com.art1001.supply.entity.partment;

import lombok.Data;

/**
 * @ClassName PartmentVO
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/4/29 9:50
 * @Discription 部门名称及部门id，用于页面显示
 */
@Data
public class PartmentVO {
    /**
     * 部门id
     */
    private String partmentId;

    /**
     * 部门名称
     */
    private String partmentName;
}
