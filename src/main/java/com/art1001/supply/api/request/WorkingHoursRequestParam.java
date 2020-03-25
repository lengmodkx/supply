package com.art1001.supply.api.request;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

/**
 * @author heshaohua
 * @version 1.0.0
 * @date 2020年03月23日 15:26:00
 */
@Data
public class WorkingHoursRequestParam {

    /**
     * 小时数
     */
    @Range(min = 1, max = 1000)
    private Double hours;

    /**
     * 任务id
     */
    @NotBlank(message = "任务id 不能为空！")
    private String taskId;

}
