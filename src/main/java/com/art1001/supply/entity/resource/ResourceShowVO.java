package com.art1001.supply.entity.resource;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 资源展示视图VO类
 * @Date:2019/5/27 14:06
 * @Author heshaohua
 **/
@Data
public class ResourceShowVO {

    /**
     * 资源id
     */
    private Integer id;

    /**
     * 当前父资源名称
     */
    private String group;

    /**
     * 当前资源组内,已被选中的资源名称集合
     */
    private List<String> checkAllGroup = new ArrayList<>();

    /**
     * 全部资源名称集合
     */
    private List<String> resources;

    /**
     * 检查是否全部拥有
     */
    private Boolean checkAll;

    /**
     * 控制权限组复选框样式
     */
    private Boolean indeterminate = false;

    public Boolean getCheckAll() {
        this.checkAll = false;
        return checkAll;
    }

    public Boolean getIndeterminate() {
        this.indeterminate = true;
        return indeterminate;
    }
}
