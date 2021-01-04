package com.art1001.supply.api;

import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.demand.DemandClass;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.demand.DemandClassService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName DemandClassApi
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/12/29 14:25
 * @Discription 需求分类api
 */
@RestController
@RequestMapping(value = "/demandClass")
public class DemandClassApi {

    @Resource
    private DemandClassService demandClassService;

    /**
     * 分类列表
     * @return
     */
    @GetMapping("/list")
    public Result list() {
        try {
            List<DemandClass> list = demandClassService.demandClassList();
            return Result.success(list);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 新增分类
     *
     * @param dcName
     * @param parentId
     */
    @PostMapping("/add")
    public Result add(@RequestParam(value = "dcName") String dcName,
                      @RequestParam(value = "parentId") String parentId) {
        try {
            demandClassService.add(dcName, parentId);
            return Result.success("保存成功");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 修改分类
     *
     * @param dcName
     * @param parentId
     */
    @PostMapping("/edit")
    public Result edit(@RequestParam("dcId") String dcId,
                       @RequestParam(value = "dcName") String dcName,
                       @RequestParam(value = "parentId") String parentId) {
        try {
            demandClassService.edit(dcId,dcName, parentId);
            return Result.success("修改成功");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 删除分类
     * @param dcId
     * @return
     */
    @GetMapping("/delete")
    public Result delete(String dcId) {
        try {
             demandClassService.removeById(dcId);
            return Result.success("删除成功");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

}
