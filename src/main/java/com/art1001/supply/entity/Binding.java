package com.art1001.supply.entity;

import com.art1001.supply.entity.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class Binding extends BaseEntity implements Serializable {

    /**
     * id
     */
    private String id;

    /**
     * 任务，日程，文件，分享id
     */
    private String public_id;

    /**
     * 任务，日程，文件，分享id
     */
    private String bind_id;

    /**
     * 任务，日程，文件，分享
     */
    private String public_type;


}
