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
    private List<Integer> checkAllGroup = new ArrayList<>();

    /**
     * 全部资源名称集合
     */
    private List<SimpleResource> resources;

}
