package com.art1001.supply.api;

import com.art1001.supply.entity.resource.ProResourcesRole;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.service.resource.ProResourcesRoleService;
import com.art1001.supply.service.role.ProRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.collections.CollectionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName RoleResourceTest
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/8/20 10:08
 * @Discription 修改资源
 */

public class RoleResourceTest {

    @Resource
    private ProRoleService  proRoleService;

    @Resource
    private ProResourcesRoleService proResourcesRoleService;


    public void updateResource(){
        List<ProRole> administratorList = proRoleService.list(new QueryWrapper<ProRole>().select("role_id").eq("role_key", "administrator"));
        administratorList.forEach(a->{
            ProResourcesRole one = proResourcesRoleService.getOne(new QueryWrapper<ProResourcesRole>().eq("r_id", a.getRoleId()));
            one.setSId("2,3,4,5,6,8,9,10,11,12,13,15,16,18,19,20,22,23,25,27,28,29,30,31,32,33,35,36,38,39,40,41,42,44,45,47,48,49,50,51,52,53,54,55,57,58,59,60,61,64,65,66,67,68,69,71,72,74,75,76,77,78,79,81");
            proResourcesRoleService.updateById(one);
        });

        List<ProRole> adminList = proRoleService.list(new QueryWrapper<ProRole>().select("role_id").eq("role_key", "admin"));
        adminList.forEach(a->{
            ProResourcesRole one = proResourcesRoleService.getOne(new QueryWrapper<ProResourcesRole>().eq("r_id", a.getRoleId()));
            one.setSId("2,3,4,5,6,8,9,10,11,12,13,15,16,18,19,20,22,23,25,27,28,29,30,31,32,33,35,36,38,39,40,41,42,44,45,47,48,49,50,51,52,53,54,55,57,58,59,60,61,64,65,66,67,68,69,71,72,74,75,76,77,78,79,81");
            proResourcesRoleService.updateById(one);
        });

    }

}
