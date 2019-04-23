package com.art1001.supply.entity.project;
import lombok.Data;

/**
 * @Description 甘特图VO类
 * @Date:2019/4/23 18:55
 * @Author heshaohua
 **/
@Data
public class GantChartVO {

    /**
     * 序号
     */
    private int id;

    /**
     * 描述
     */
    private String label;

    /**
     * 创建人名称
     */
    private String user;

    /**
     * 开始时间
     */
    private Long start;


    /**
     * 结束时间
     */
    private Long end;

    /**
     * 父级id 指向
     */
    private int parentId;
}
