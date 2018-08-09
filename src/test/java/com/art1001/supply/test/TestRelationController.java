package com.art1001.supply.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.dtgrid.util.ExportUtils;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskMenuVO;
import com.art1001.supply.entity.template.TemplateData;
import com.art1001.supply.entity.user.UserInfoEntity;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.template.TemplateDataService;
import com.art1001.supply.util.FileUtils;
import com.art1001.supply.util.IdGen;
import org.junit.Test;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
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

    @Resource
    private TemplateDataService templateDataService;


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
    public void a() throws Exception{
        InputStream stream = getClass().getClassLoader().getResourceAsStream("ff.json");
        String content = FileUtils.readFileContent(stream);
        JSONArray jsonArray = JSON.parseArray(content);


        for (int i=0;i<jsonArray.size();i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            TemplateData templateData = new TemplateData();
            templateData.setId(IdGen.uuid());
            templateData.setMenuName(object.getString("menuName"));
            templateData.setTemplateId("879fd7e79b9d11e8a601c85b76c405c2");
            templateData.setMenuOrder(i);
            templateDataService.saveTemplateData(templateData);

            JSONArray taskList = object.getJSONArray("taskList");
            for(int j=0;j<taskList.size();j++){
                JSONObject object1 = taskList.getJSONObject(j);
                TemplateData templateData1 = new TemplateData();
                templateData1.setId(IdGen.uuid());
                templateData1.setTaskName(object1.getString("taskName"));
                templateData1.setRemarks(object1.getString("remarks"));
                templateData1.setParentId(templateData.getId());

                templateDataService.saveTemplateData(templateData1);
            }
        }


    }






}
