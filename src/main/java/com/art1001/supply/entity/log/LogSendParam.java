package com.art1001.supply.entity.log;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author shaohua
 * @date 2020/3/6 15:31
 */
@Data
@Validated
public class LogSendParam extends Log {

    /**
     * 提及者id
     */
    private List<String>  mentionIdList;

    /**
     * 评论的类型 (任务 文件, 分享 日程)
     */
    @NotNull(message = "publicType不能为空")
    private String publicType;

}
