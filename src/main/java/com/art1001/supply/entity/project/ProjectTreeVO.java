package com.art1001.supply.entity.project;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Date:2019/7/11 10:20
 * @Author heshaohua
 **/
@Data
public class ProjectTreeVO {

    /**
     * 项目id
     */
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 是否展开直子节点
     */
    private Boolean expand;

    /**
     * 禁掉响应
     */
    private Boolean disabled;

    /**
     * 禁掉checkbox
     */
    private Boolean disableCheckbox;

    /**
     * 是否选中子节点
     */
    private Boolean selected;

    /**
     * 是否勾选
     */
    private Boolean checked;

    /**
     * 是否显示展开图标
     */
    private Boolean loading = false;

    /**
     * 子节点属性数组
     */
    private List<ProjectTreeVO> children = new ArrayList<>();


}
