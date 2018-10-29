package com.art1001.supply.service.user.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.AliyunOss;
import com.art1001.supply.util.EmailUtil;
import com.art1001.supply.util.ImageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,UserEntity> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private EmailUtil emailUtil;

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
        return getById(id);
    }

    /**
     * 重写用户插入
     */
    @Override
    public int insert(UserEntity userEntity, String password) {
        try {
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
                throw new ServiceException("新增用户: " + userEntity.getUserId() + " 失败");
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public int updatePassword(UserEntity userEntity, String password) throws ServiceException {
        try {
            boolean s = updateById(userEntity);
            //发送邮件
            emailUtil.send126Mail(userEntity.getAccountName(), "系统密码重置", "您好，您的密码已重置，新密码是:" + password);
            return s?1:0;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
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

    @Override
    public List<UserEntity> findByKey(String keyword) {
        return userMapper.findByKey(keyword);
    }

    /**
     * 查询出某个项目下的所有成员信息(去除当前请求者的信息)
     * @param projectId 项目id
     * @return
     */
    @Override
    public List<UserEntity> getProjectMembers(String projectId) {
        List<UserEntity> projectMembers = userMapper.selectProjectMembers(projectId);
        List<UserEntity> after = projectMembers.stream().filter(itme -> !itme.getUserId().equals(ShiroAuthenticationManager.getUserId())).collect(Collectors.toList());
        return after;
    }
}
