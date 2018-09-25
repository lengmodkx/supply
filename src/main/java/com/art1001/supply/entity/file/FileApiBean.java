package com.art1001.supply.entity.file;

import lombok.Data;

/**
 * @author heshaohua
 * @Title: FileApiBean
 * @Description: TODO
 * @date 2018/9/25 11:43
 **/
@Data
public class FileApiBean {
    /**
     * 文件id
     */
    private String fileId;

    /**
     * 任务名称
     */
    private String fileName;

    /**
     * 文件所在项目名称
     */
    private String projectName;

    /**
     * 文件后缀名
     */
    private String ext;

    /**
     * 创建者头像
     */
    private String userImage;

    /**
     * 文件的url
     */
    private String fileUrl;
}
