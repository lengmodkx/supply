package com.art1001.supply.api;

import com.art1001.supply.entity.Result;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.demand.DemandBidService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName DemandBidApi
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/22 11:43
 * @Discription 需求竞标api
 */
@RestController
@RequestMapping(value = "/demandBid")
public class DemandBidApi {

    @Resource
    private DemandBidService demandBidService;

    /**
     * 参与竞标
     * @param demandId          需求id
     * @param orgId             竞标企业id
     * @param details           服务详情
     * @param detailImages      焦点主图
     * @param bid               出价
     * @return
     */
    @PostMapping("/joinBidding")
    public Result joinBidding(@RequestParam(value = "demandId") String demandId,
                              @RequestParam(value = "orgId") String orgId,
                              @RequestParam(value = "details")String details,
                              @RequestParam(value = "detailImages",required = false)List<String>detailImages,
                              @RequestParam(value = "bid") BigDecimal bid){
        try {
            demandBidService.joinBidding(demandId,orgId,details,detailImages,bid);
            return Result.success("参与竞标成功");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 竞标成功
     * @param demandId     需求id
     * @param id           竞标id
     * @return
     */
    @GetMapping("/successBidding")
    public Result successBidding(@RequestParam(value = "demandId") String demandId ,
                                 @RequestParam(value = "id")String id){
        try {
            String result = demandBidService.successBidding(demandId, id);
            return Result.success(result);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }
    
}
