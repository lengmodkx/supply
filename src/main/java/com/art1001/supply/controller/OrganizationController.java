package com.art1001.supply.controller;


import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 组织成员管理控制器
 */
@Controller
@Slf4j
@RequestMapping("/organization")
public class OrganizationController {

    @Resource
    private UserService userService;

    @RequestMapping("/members")
    public void members(@RequestParam(value = "pageNo",defaultValue = "1") Integer pageNo,
                        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                        @RequestParam(value = "flag",defaultValue = "1") Integer flag){
        JSONObject jsonObject = new JSONObject();
        List<UserEntity> userEntityList = new ArrayList<>();
        try {
            UserEntity userEntity = new UserEntity();
            Pager pager = new Pager();
            if(flag==1){
                //所有成员
                pager.setPageNo(pageNo);
                pager.setPageSize(pageSize);
                pager.setCondition(userEntity);
                userEntityList = userService.findListPager(pager);
            }else if (flag==2){
                //新加入的成员

            }else if (flag==3){

            }else {

            }







            jsonObject.put("result",1);
            jsonObject.put("msg","获取成功");
            jsonObject.put("data",userEntityList);

        }catch (Exception e){
            throw new AjaxException(e);
        }



    }













}
