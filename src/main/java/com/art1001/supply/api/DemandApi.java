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
     * @param dcId          需求分类
     * @param demandDetails 需求详情
     * @param demandFiles   需求附件
     * @param demandBudget  需求预算
     * @param solveWay      解决方式 1招标 2比稿
     * @param memberTel     发布人电话
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestParam(value = "demandName") String demandName,
                      @RequestParam(value = "dcId") String dcId,
                      @RequestParam(value = "demandDetails") String demandDetails,
                      @RequestParam(value = "demandFiles",required = false) List<String> demandFiles,
                      @RequestParam(value = "demandBudget") BigDecimal demandBudget,
                      @RequestParam(value = "solveWay") Integer solveWay,
                      @RequestParam(value = "memberTel") String memberTel) {

        try {
            demandService.add(demandName, dcId, demandDetails, demandFiles, demandBudget, solveWay, memberTel);
            return Result.success("保存成功");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }


    /**
     * 修改需求
     * @param demandId      需求id
     * @param demandName    需求名称
     * @param dcId          需求分类id
     * @param demandDetails 需求详情
     * @param demandFiles   需求附件
     * @param demandBudget  需求预算
     * @param memberTel     发布人电话
     * @param isExpedited   是否加急
     * @param isTop         是否置顶
     * @return
     */
    @PostMapping("/edit")
    public Result edit(@RequestParam(value = "demandId") String demandId,
                       @RequestParam(value = "demandName", required = false) String demandName,
                       @RequestParam(value = "dcId", required = false) String dcId,
                       @RequestParam(value = "demandDetails", required = false) String demandDetails,
                       @RequestParam(value = "demandFiles", required = false) List<String> demandFiles,
                       @RequestParam(value = "demandBudget", required = false) BigDecimal demandBudget,
                       @RequestParam(value = "memberTel", required = false) String memberTel,
                       @RequestParam(value = "isExpedited", required = false) Integer isExpedited,
                       @RequestParam(value = "isTop", required = false) Integer isTop) {

        try {
            demandService.edit(demandId,demandName, dcId, demandDetails, demandFiles, demandBudget, memberTel, isExpedited, isTop);
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
     * @param pageNum
     * @param pageSize
     * @param dcId
     * @param type 1查看除了我提出的所有的需求  2查看我提出的所有的需求
     * @return
     */
    @GetMapping("/list")
    public Result list(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                       @RequestParam(value = "pageSize",defaultValue = "20") Integer pageSize,
                       @RequestParam(value = "dcId",required = false) String dcId,
                       @RequestParam(value = "type",defaultValue = "1")Integer type){
        try {
            IPage<Demand> page=demandService.getList(pageNum,pageSize,dcId,type);
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
                              @RequestParam(value = "isCheck") Integer isCheck,
                              @RequestParam(value = "reason",required = false) String reason){
        try {
            demandService.checkDemand(demandId,isCheck,reason);
            return Result.success("审核成功");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }
}
