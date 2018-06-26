package com.art1001.supply.test;

import com.art1001.supply.dtgrid.util.ExportUtils;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskMenuVO;
import com.art1001.supply.entity.user.UserInfoEntity;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.util.IdGen;
import org.junit.Test;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author heshaohua
 * @Title: TestRelationController
 * @Description: TODO
 * @date 2018/6/20 17:29
 **/
public class TestRelationController extends TestBase {

    @Resource
    private RelationService relationService;

    @Resource
    private TaskService taskService;

    @Test
    public void addRelation(){
        Relation relation = new Relation();
        relation.setCreateTime(System.currentTimeMillis());
        relation.setRelationDel(0);
        relation.setLable(0);
        relation.setRelationName("测试分组");
        relation.setRelationId(IdGen.uuid());
        relation.setProjectId("1");

        try {
            relationService.saveRelation(relation);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void addMenu(){
        String parentId = "2";
        Relation relation = new Relation();
        String relationName = "测试菜单A";
        relation.setRelationName(relationName);
        try {
            relationService.addMenu(parentId,relation);
        } catch (Exception e){
        }
    }

    @Test
    public void editMenu(){
        Relation relation = new Relation();
        relation.setCreateTime(System.currentTimeMillis());
        relation.setRelationDel(0);
        relation.setLable(0);
        relation.setRelationName("测试修改菜单A");
        relation.setRelationId("73abeb7129344fb0b053956ee0c0cff0");
        relation.setProjectId("1");
        try {
            relationService.editMenu(relation);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

//    @Test
//    public void menuSort(){
//        String relationId = "73abeb7129344fb0b053956ee0c0cff0";
//        try {
//            //Relation relation = relationService.menuSort(relationId);
//            for (Task task: relation.getTaskList()) {
//                System.out.println(task.getTaskId());
//            }
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }

//    @Test
//    public void Sort(){
//        String id = "e7986babf33347c09d00bc1459d2d2c1";
//        try {
//            List<Relation> relationList = relationService.menuSort(id);
//            for (Relation re: relationList) {
//                System.out.println(re.getRelationId()+ "\t"+ re.getty);
//            }
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }

    @Test
    public void setMenuAllTaskExecutor(){
        String relationId = "d229c5f3a73749829b0d9b32ade26ce6";
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setId("123");
        userInfoEntity.setImage("测试头像");
        String uName = "何少华";
        relationService.setMenuAllTaskExecutor(relationId,userInfoEntity,uName);
    }

    @Test
    public void setMenuAllTaskEndTime(){
        String relationId = "d229c5f3a73749829b0d9b32ade26ce6";
        Long endTime = System.currentTimeMillis();
        try {
            relationService.setMenuAllTaskEndTime(relationId,endTime);
        } catch (Exception e){

        }
    }

    @Test
    public void moveMenuAllTask(){
        TaskMenuVO oldTaskMenuVO = new TaskMenuVO();
        oldTaskMenuVO.setProjectId("2");
        oldTaskMenuVO.setTaskMenuId("d229c5f3a73749829b0d9b32ade26ce6");
        TaskMenuVO newTaskMenuVO = new TaskMenuVO();
        newTaskMenuVO.setProjectId("1");
        newTaskMenuVO.setTaskMenuId("d229c5f3a73749829b0d9b32ade26ce6");


        relationService.moveMenuAllTask(oldTaskMenuVO,newTaskMenuVO);
    }

    @Test
    public void copyMenuAllTask(){
        TaskMenuVO oldTaskMenuVO = new TaskMenuVO();
        oldTaskMenuVO.setProjectId("2");
        oldTaskMenuVO.setTaskMenuId("d229c5f3a73749829b0d9b32ade26ce6");
        TaskMenuVO newTaskMenuVO = new TaskMenuVO();
        newTaskMenuVO.setProjectId("1");
        newTaskMenuVO.setTaskMenuId("d229c5f3a73749829b0d9b32ade26ce6");
        relationService.copyMenuAllTask(oldTaskMenuVO,newTaskMenuVO);

    }

    @Test
    public void menuAllTaskToRecycleBin(){
        String relationId = "d229c5f3a73749829b0d9b32ade26ce6";
        try {
            relationService.menuAllTaskToRecycleBin(relationId);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void a(){
        Relation menuInfoByTaskId = relationService.findMenuInfoByTaskId("13af45c74f41410da431fdf46c8a7aa0");
        System.out.println(menuInfoByTaskId.getRelationName()+"\t"+ menuInfoByTaskId.getRelationId());
        TaskMenuVO projectAndGroupInfoByMenuId = relationService.findProjectAndGroupInfoByMenuId(menuInfoByTaskId.getRelationId());
        System.out.println(projectAndGroupInfoByMenuId.getProjectId()+"\t"+ projectAndGroupInfoByMenuId.getProjectName());
        System.out.println(projectAndGroupInfoByMenuId.getTaskGroupId()+"\t"+projectAndGroupInfoByMenuId.getTaskGroupName());


    }



}
