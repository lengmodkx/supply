package com.art1001.supply.api;


import com.art1001.supply.entity.Result;
import com.art1001.supply.service.role.ProRoleUserService;
import com.art1001.supply.validation.role.DistributionRoleParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 用户角色映射表 前端控制器
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
@Slf4j
@RestController
@RequestMapping("/proRoleUser")
public class ProRoleUserApi {

    @Resource
    private ProRoleUserService proRoleUserService;

    @PostMapping
    public Result distributionRole(@Validated DistributionRoleParam param){
        log.info("Distribution role to user. [{}]", param);

        proRoleUserService.distributionRoleToUser(Integer.valueOf(param.getRoleId()), param.getUserId(), param.getProjectId());
        return Result.success();

    }

}

