package com.art1001.supply.entity.task.vo;

import com.art1001.supply.entity.relation.Relation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName MenuVo
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/7/30 17:47
 * @Discription 任务列表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuVo {
    /**
     * 名称
     */
    private String name;
    /**
     * 任务分组列表
     */
    private List<Relation> taskMenu;
}
