package com.art1001.supply.api;

import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.demand.Demand;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.demand.DemandService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName DemandApi
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/12/29 14:26
 * @Discription 需求api
 */
@RestController
@RequestMapping(value = "/demand")
public class DemandApi {

    @Resource
    private DemandService demandService;

    /**
     * 保存需求
     *
     * @param demandName    需求名称
     * @param demandDetails 需求详情
     * @param demandFiles   需求附件
     * @param bid  出价
          * @return
     */
    @PostMapping("/add")
    public Result add(@RequestParam(value = "demandName") String demandName,
                      @RequestParam(value = "demandDetails") String demandDetails,
                      @RequestParam(value = "orgId") String orgId,
                      @RequestParam(value = "demandFiles",required = false) List<String> demandFiles,
                      @RequestParam(value = "bid") BigDecimal bid) {

        try {
            demandService.add(demandName, demandDetails,orgId, demandFiles, bid);
            return Result.success("保存成功");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }


    /**
     * 修改需求
     * @param demandId      需求id
     * @param demandName    需求名称
     * @param demandDetails 需求详情
     * @param demandFiles   需求附件
     * @param bid           需求预算
     * @return
     */
    @PostMapping("/edit")
    public Result edit(@RequestParam(value = "demandId") String demandId,
                       @RequestParam(value = "demandName", required = false) String demandName,
                       @RequestParam(value = "demandDetails", required = false) String demandDetails,
                       @RequestParam(value = "demandFiles", required = false) List<String> demandFiles,
                       @RequestParam(value = "bid", required = false) BigDecimal bid) {

        try {
            demandService.edit(demandId,demandName, demandDetails, demandFiles,bid);
            return Result.success("保存成功");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 移除需求
     * @param demandId
     * @return
     */
    @GetMapping("/remove")
    public Result remove(@RequestParam(value = "demandId") String demandId){
        try {
            demandService.update(new UpdateWrapper<Demand>().set("is_del",1).eq("demand_id",demandId));
            return Result.success("删除成功");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 需求列表
     * @param pageNum      页数
     * @param pageSize     条数
     * @param type         1我发布的 2我竞标的 3我中标的 4所有需求
     * @param state        1 审核过的 2未审核的
     * @return  Page<Demand>
     */
    @GetMapping("/list")
    public Result list(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                       @RequestParam(value = "pageSize",defaultValue = "20") Integer pageSize,
                       @RequestParam(value = "type",defaultValue = "1") Integer type,
                       @RequestParam(value = "state",defaultValue = "1") Integer state,
                       @RequestParam(value = "userId")String userId){
        try {
            IPage<Demand> page=demandService.getList(pageNum,pageSize,type,state,userId);
            return Result.success(page);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }


    /**
     * 审核/发布需求
     * @param demandId
     * @param isCheck   审核是否通过 0否1是
     * @param reason    审核失败原因
     * @return
     */
    @GetMapping("/checkDemand")
    public Result checkDemand(@RequestParam(value = "demandId") String demandId,
                              @RequestParam(value = "isCheck",defaultValue = "1") Integer isCheck,
                              @RequestParam(value = "reason",required = false) String reason){
        try {
            demandService.checkDemand(demandId,isCheck,reason);
            return Result.success("审核成功");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 获取需求详情
     * @param demandId  需求id
     * @return Demand
     */
    @GetMapping("/demandInfo")
    public Result demandInfo(@RequestParam(value = "demandId") String demandId){
        try {
            Demand demand=demandService.demandInfo(demandId);
            return Result.success(demand);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }
}
