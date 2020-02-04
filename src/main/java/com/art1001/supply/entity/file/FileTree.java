package com.art1001.supply.entity.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FileTree {

    private String id;

    @JsonProperty(value = "pId")
    private String pId;

    private String name;

    private boolean open = true;

    private String icon="https://art1001-bim-5d.oss-cn-beijing.aliyuncs.com/upload/tree-icon/tree3.png";

    private List<FileTree> childTree;

    public FileTree(String id, String pId, String name, boolean open, String icon) {
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.open = open;
        this.icon = icon;
    }
}
