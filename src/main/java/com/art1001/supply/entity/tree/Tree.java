package com.art1001.supply.entity.tree;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author shaohua
 * @date 2020/2/28 10:59
 */
@Data
public class Tree {

    private String id;

    @JsonProperty(value = "pId")
    private String pId;

    private String name;

    private Boolean open;

    private Boolean isParent;

    private String orgId;

    private String icon ;

}
