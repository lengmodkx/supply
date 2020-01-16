package com.art1001.supply.entity.file;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FileTree {

    private String id;

    private String pId;

    private String name;

    private boolean open = true;

    private String icon="https://art1001-bim-5d.oss-cn-beijing.aliyuncs.com/upload/tree-icon/tree3.png";

}
