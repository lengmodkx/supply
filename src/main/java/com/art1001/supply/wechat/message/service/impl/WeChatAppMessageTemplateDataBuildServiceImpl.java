package com.art1001.supply.wechat.message.service.impl;

import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.util.ValidatedUtil;
import com.art1001.supply.wechat.message.service.WeChatAppMessageTemplateDataBuildService;
import com.art1001.supply.wechat.message.template.TemplateData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author heshaohua
 * @date 2019/12/5 14:45
 **/
@Configuration
public class WeChatAppMessageTemplateDataBuildServiceImpl {

    @Resource
    private UserService userService;

    private static final SimpleDateFormat YY_MM_DD = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


    @Bean
    public WeChatAppMessageTemplateDataBuildService updateTaskJoinInfo() {
        return (userId) -> {
            ValidatedUtil.filterNullParam(userId);

            UserEntity byId = userService.getById(userId);
            Map<String, TemplateData> map = new HashMap(8);
            map.put("keyword1", new TemplateData(byId.getUserName()));
            map.put("keyword2", new TemplateData(byId.getAccountName()));
            map.put("keyword3", new TemplateData(YY_MM_DD.format(new Date())));
            map.put("keyword4", new TemplateData(byId.getUserName()));
            map.put("keyword5", new TemplateData(byId.getJob()));
            return map;
        };
    }
}
