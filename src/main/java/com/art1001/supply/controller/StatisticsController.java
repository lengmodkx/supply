package com.art1001.supply.controller;

import com.art1001.supply.controller.base.BaseController;
import com.art1001.supply.entity.statistics.Statistics;
import com.art1001.supply.entity.statistics.TaskCondition;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.task.TaskInfoService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/statistics")
public class StatisticsController extends BaseController {
    @Resource
    private ProjectService projectService;

    @Resource
    private TaskService taskService;

    @Resource
    private UserService userService;

    @Resource
    private TaskInfoService taskInfoService;
    /**
     * 加载任务统计页面的信息
     * @param projectId 项目id
     * @return
     */
    @RequestMapping("statistics.html")
    public String statistics(@RequestParam String projectId, Model model){
        List<Statistics> overViewList = taskService.findTaskCountOverView(projectId);
        String userId = ShiroAuthenticationManager.getUserId();
        UserEntity userEntity = userService.findById(userId);
        model.addAttribute("user",userEntity);
        model.addAttribute("project",projectService.findProjectByProjectId(projectId));
        model.addAttribute("overViewList",overViewList);
        return "statistics";
    }

    @RequestMapping("/statisticsDetail.html")
    public String statisticsDetail(){
        return "tk-statistics-details";
    }

    /*
     *总量概览
     **/

    @RequestMapping("/chart/{chartId}")
    @ResponseBody
    public Map statisticsChart(@PathVariable String chartId, TaskCondition taskCondition){
        Map map=new HashMap<>(16);

        if (chartId!=null && !"".equals(chartId)){
            try {
                map = this.taskInfoService.selectTask(taskCondition,chartId);
            } catch (Exception e) {
                map=null;
                e.printStackTrace();
            }
        }
        return  map;
    }


}
