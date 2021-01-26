package com.art1001.supply.api;

import com.art1001.supply.entity.Result;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.order.OrderDemandService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

/**
 * @ClassName OrderDemandApi
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/26 14:27
 * @Discription 订单api
 */
@Controller
@RequestMapping("/orderDemand")
public class OrderDemandApi {

    @Resource
    private OrderDemandService orderDemandService;

    /**
     * 添加订单留言
     * @param orderId             订单id
     * @param leaveMessage        订单留言
     * @return
     */
    @PostMapping("/addLeaveMessage")
    public Result addLeaveMessage(@RequestParam(value = "orderId") String orderId,
                       @RequestParam(value = "leaveMessage") String leaveMessage){
        try {
            orderDemandService.addLeaveMessage(orderId, leaveMessage);
            return Result.success("修改成功");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }
}
