package com.art1001.supply.controller.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.util.crypto.EndecryptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户列表
     */
    @GetMapping("/list.html")
    @ResponseBody
    public JSONObject list(
            @RequestParam(required = false, defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, defaultValue = "2") Integer pageSize
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            Pager pager = new Pager();
            pager.setPageNo(pageNo);
            pager.setPageSize(pageSize);
            Map<String, Object> map = new HashMap<>();
            map.put("start", pager.getStart());
            map.put("pageSize", pager.getPageSize());
            // 获取总条数
            int count = userService.findCount();
            List<UserEntity> userList = userService.queryListByPage(map);
            jsonObject.put("result", 1);
            jsonObject.put("data", JSON.toJSON(userList));
            jsonObject.put("count", count);
            jsonObject.put("msg", "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("result", 0);
            jsonObject.put("msg", "获取失败");
        }
        return jsonObject;
    }

    /**
     * 锁定账户
     */
    @RequestMapping("lock")
    @ResponseBody
    public JSONObject lock(UserEntity userEntity) throws AjaxException {
        JSONObject jsonObject = new JSONObject();
        try {
            userEntity.setLocked(1);
            int result = userService.updateOnly(userEntity);
            if (result == 1) {
                jsonObject.put("result", 1);
                jsonObject.put("msg", "账户已锁定");
            } else {
                jsonObject.put("result", 0);
                jsonObject.put("msg", "账户锁定失败");
            }
        } catch (Exception e) {
            log.error("账户锁定异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "账户锁定失败");
        }
        return jsonObject;
    }


    /**
     * 解锁账户
     */
    @RequestMapping("unlock")
    @ResponseBody
    public JSONObject unlock(UserEntity userEntity) throws AjaxException {
        JSONObject jsonObject = new JSONObject();
        try {
            userEntity.setLocked(0);
            int result = userService.updateOnly(userEntity);
            if (result == 1) {
                jsonObject.put("result", 1);
                jsonObject.put("msg", "账户已解锁");
            } else {
                jsonObject.put("result", 0);
                jsonObject.put("msg", "账户解锁失败");
            }
        } catch (Exception e) {
            log.error("账户解锁异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "账户解锁失败");
        }
        return jsonObject;
    }

    /**
     * 修改密码
     */
    @RequestMapping("updatePassword")
    @ResponseBody
    public JSONObject updatePassword(UserEntity userEntity) {
        JSONObject jsonObject = new JSONObject();
        try {
            String password = userEntity.getPassword();
            //加密用户输入的密码，得到密码和加密盐，保存到数据库
            UserEntity user = EndecryptUtils.md5Password(userEntity.getAccountName(), userEntity.getPassword(), 2);
            //设置添加用户的密码和加密盐
            userEntity.setPassword(user.getPassword());
            userEntity.setCredentialsSalt(user.getCredentialsSalt());
            int cnt = userService.updatePassword(userEntity, password);
            if (cnt > 0) {
                jsonObject.put("result", 1);
                jsonObject.put("msg", "密码修改成功,请重新登录");
            } else {
                jsonObject.put("result", 0);
                jsonObject.put("msg", "密码修改失败");
            }
        } catch (Exception e) {
            log.error("修改密码异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "密码修改失败");
        }
        return jsonObject;
    }

}
