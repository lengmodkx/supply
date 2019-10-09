package com.art1001.supply.api;


import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.service.relation.GroupUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author heshaohua
 * @since 2019-10-09
 */
@Slf4j
@RestController
@RequestMapping("/group_user")
public class GroupUserApi extends BaseController {

    @Resource
    private GroupUserService groupUserService;

    @PostMapping("/addition_group_user/{groupId}")
    public JSONObject additionGroupUser(@PathVariable
                                        @Validated @NotNull(message = "分组id不能为空！") String groupId,

                                        @RequestParam
                                        @Validated @NotNull(message = "用户id不能不空！") String userId){

        log.info("Addition groupUser info. [{},{}]",groupId,userId);

        groupUserService.additionGroupUser(groupId, userId);

        return success();
    }

}

