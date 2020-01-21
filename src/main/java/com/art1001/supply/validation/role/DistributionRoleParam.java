package com.art1001.supply.validation.role;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class DistributionRoleParam {

    @NotNull(message = "用户id不能为空")
    private String userId;

    @NotNull(message = "角色id不能为空")
    @Min(message = "roleId不规范", value = 1)
    private String roleId;

    @NotNull(message = "项目id不能为空")
    private String projectId;

}
