package com.art1001.supply.api;

import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.article.ArticleClass;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.article.ArticleClassService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName ArticleClassApi
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/12/24 9:55
 * @Discription 文章分类
 */
@RestController
@RequestMapping(value = "articleClass")
public class ArticleClassApi {

    @Resource
    private ArticleClassService articleClassService;

    /**
     * 文章分类列表
     * @return
     */
    @GetMapping("/list")
    public Result list() {
        try {
            List<ArticleClass> list = articleClassService.list(new QueryWrapper<ArticleClass>());
            return Result.success(list);
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 保存文章分类
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody ArticleClass articleClass) {
        try {
            articleClassService.save(articleClass);
            return Result.success("保存成功");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 编辑文章分类
     * @return
     */
    @PostMapping("/edit")
    public Result edit(@RequestBody ArticleClass articleClass) {
        try {
            articleClassService.updateById(articleClass);
            return Result.success("修改成功");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }

    /**
     * 删除文章分类
     * @return
     */
    @GetMapping("/delete")
    public Result delete(@RequestParam String acId) {
        try {
            articleClassService.removeById(acId);
            return Result.success("修改成功");
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }
}
