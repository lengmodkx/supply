package com.art1001.supply.entity.file;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Description 树状图文件视图
 * @Date:2019/3/21 09:41
 * @Author heshaohua
 **/
@Data
@ToString
public class FileTreeShowVO {

    /**
     * 文件的id
     */
    private String id;

    /**
     * 文件的名称
     */
    private String text;

    /**
     * 子目录集合
     */
    private List<FileTreeShowVO> childRen;
}
