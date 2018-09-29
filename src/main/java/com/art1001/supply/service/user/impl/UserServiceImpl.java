package com.art1001.supply.service.user.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.util.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,UserEntity> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private EmailUtil emailUtil;

    /** 任务的逻辑层接口 */
    @Resource
    private TaskService taskService;


    @Override
    public List<UserEntity> queryListByPage(Map<String, Object> parameter) {
        return null;
    }

    @Override
    public UserEntity findByName(String accountName) {
        return null;
    }

    @Override
    public UserEntity findById(String id) {
        return null;
    }

    /**
     * 重写用户插入
     */
    @Override
    public int insert(UserEntity userEntity, String password) {
        try {
            // 生成用户id
            userEntity.setId(IdGen.uuid());
            // 图片byte数组
            byte[] bytes = ImageUtil.generateImg(userEntity.getUserName());
            // oss上传
            String fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
            AliyunOss.uploadByte(Constants.MEMBER_IMAGE_URL + fileName, bytes);
            userEntity.setImage(Constants.MEMBER_IMAGE_URL + fileName);
            userEntity.setDefaultImage(Constants.MEMBER_IMAGE_URL + fileName);
            //发送邮件
            //emailUtil.send126Mail(userEntity.getAccountName(), "系统消息通知", "您好,您的账户已创建,账户名:" + userEntity.getAccountName() + " ,密码:" + password);
            int cnt = userMapper.insert(userEntity);
            if(cnt == 1){
                return cnt;
            } else{
                throw new ServiceException("新增用户: " + userEntity.getId() + " 失败");
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }


    /**
     * 重写用户更新逻辑：
     * 1、更新用户
     * 2、更新用户和角色的对应关系
     * 3、更新用户个人资料信息
     */
    @Override
    public int update(UserEntity userEntity) {
        try {
//            if (userMapper.update(userEntity,) == 1) {
//                if (userMapper.updateUserRole(userEntity) == 1) {
//                    int result = userMapper.updateUserInfo(userEntity);
//                    ShiroAuthenticationManager.clearUserAuthByUserId(userEntity.getId());
//                    return result;
//                } else {
//                    return 0;
//                }
//            } else {
//                return 0;
//            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return 0;
    }

    /**
     * 重写用户删除逻辑：
     * 1、删除用户和角色的对应关系
     * 2、删除用户
     */
    @Override
    public int deleteBatchById(List<String> userIds) {
        try {
            int result = userMapper.deleteBatchUserRole(userIds);
            if (result == userIds.size()) {
            } else {
                return 0;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return 0;
    }

    @Override
    public int updateOnly(UserEntity userEntity) throws ServiceException {
        try {
            //int cnt = userMapper.update(userEntity,userEntity);
            return 0;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public int updatePassword(UserEntity userEntity, String password) throws ServiceException {
        try {
            int cnt = updateOnly(userEntity);
            //发送邮件
            emailUtil.send126Mail(userEntity.getAccountName(), "系统密码重置", "您好，您的密码已重置，新密码是:" + password);
            return cnt;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public int findCount() {
        return userMapper.findCount();
    }

    /**
     * 根据用户id查询多条用户记录
     * @param memberIds 用户id的数组
     * @return
     */
    @Override
    public List<UserEntity> findManyUserById(String memberIds) {
        return userMapper.findManyUserById(memberIds);
    }

    /**
     * 查询该项目下所有的成员信息
     * @param projectId 项目编号
     * @return
     */
    @Override
    public List<UserEntity> findProjectAllMember(String projectId) {
        return userMapper.findProjectAllMember(projectId);
    }

    /**
     * 查询存在该任务的成员信息  和  不存在该任务中的成员信息
     * @param task 任务实体信息
     * @return
     */
    @Override
    public Map<String, List<UserEntity>> findUserByIsExistTask(Task task) {
        //查询出该任务的创建者id
        String taskMemberIdByTaskId = taskService.findTaskMemberIdByTaskId(task.getTaskId());
        Map<String,List<UserEntity>> map = new HashMap<String,List<UserEntity>>(3);
        //查询存在于该任务中的成员信息
        List<UserEntity> existList = userMapper.findUserByExistTask(task);
        //查询不存在于该任务中的成员信息
        List<UserEntity> notExistList = userMapper.findUserByNotExistTask(task);
        //把集合中任务的创建者删除掉
        if(notExistList != null && notExistList.size() > 0){
            for (UserEntity userEntity : notExistList) {
                if(userEntity.getId().equals(taskMemberIdByTaskId)){
                    notExistList.remove(userEntity);
                    break;
                }
            }
        }
        map.put("existList",existList);
        map.put("notExistList",notExistList);
        return map;
    }

    /**
     * 查询该任务下 除执行者外所有的参与者的详细信息
     * @param taskId 任务id
     * @param status 身份
     * @return
     */
    @Override
    public List<UserEntity> findTaskMemberInfo(String taskId, String status) {
        return userMapper.findTaskMemberInfo(taskId,status);
    }

    /**
     * 根据任务id 查询出该任务的 执行者信息
     * @param taskId 任务id
     * @return
     */
    @Override
    public UserEntity findExecutorByTask(String taskId) {
        return userMapper.findExecutorByTask(taskId);
    }

    /**
     * 根据用户id 查信息
     * @param executor 用户id
     * @return
     */
    @Override
    public UserEntity findUserInfoById(String executor) {
        return userMapper.findUserInfoById(executor);
    }

    /**
     * 查询出任务的创建者信息
     * @param taskId 任务的id
     * @return
     */
    @Override
    public UserEntity findTaskCreate(String taskId) {
        return userMapper.findTaskCreate(taskId);
    }

    /**
     * 根据id查询出用户的信息
     * @param uId 用户id
     * @return
     */
    @Override
    public UserEntity findUserById(String uId) {
        return userMapper.findUserById(uId);
    }

    /**
     * 根据用户的id 反向选择用户
     * @param projectId 项目id
     * @param uId 用户的id
     * @return
     */
    @Override
    public List<UserEntity> reverseFindUser(String projectId, String[] uId) {
        Map map = new HashMap();
        map.put("projectId",projectId);
        map.put("uId",uId);
        return userMapper.reverseFindUser(map);
    }

    @Override
    public List<UserEntity> findByKey(String keyword) {
        return userMapper.findByKey(keyword);
    }

    /**
     * 根据用户id 查询出当前用户的名称
     * @param uId 用户id
     * @return 用户名称
     */
    @Override
    public String findUserNameById(String uId) {
        return userMapper.findUserNameById(uId);
    }
}
