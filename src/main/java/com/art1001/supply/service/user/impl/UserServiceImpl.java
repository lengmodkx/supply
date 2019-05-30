package com.art1001.supply.service.user.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.file.FileMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.util.AliyunOss;
import com.art1001.supply.util.EmailUtil;
import com.art1001.supply.util.ImageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,UserEntity> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private EmailUtil emailUtil;

    @Resource
    FileMapper fileMapper;
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
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insert(UserEntity userEntity, String password) throws AjaxException {
        if (userMapper.selectCount(new QueryWrapper<UserEntity>().lambda().eq(UserEntity::getAccountName,userEntity.getAccountName())) > 0){
            throw new ServiceException("用户已存在!");
        }
        // 图片byte数组
        byte[] bytes = ImageUtil.generateImg(userEntity.getUserName());
        // oss上传
        String fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
        AliyunOss.uploadByte(Constants.MEMBER_IMAGE_URL + fileName, bytes);
        userEntity.setImage(Constants.MEMBER_IMAGE_URL + fileName);
        userEntity.setDefaultImage(Constants.MEMBER_IMAGE_URL + fileName);
        //发送邮件
        //emailUtil.send126Mail(userEntity.getAccountName(), "系统消息通知", "您好,您的账户已创建,账户名:" + userEntity.getAccountName() + " ,密码:" + password);
        userEntity.setCreateTime(new Date());
        userEntity.setUpdateTime(new Date());
        userMapper.insert(userEntity);
        File file = new File();
        // 写库
        file.setFileName("我的文件夹");
        file.setUserId(userEntity.getUserId());
        file.setMemberId(userEntity.getUserId());
        file.setCreateTime(System.currentTimeMillis());
        file.setUpdateTime(System.currentTimeMillis());
        file.setLevel(1);
        file.setCatalog(1);
        file.setFilePrivacy(2);
        file.setFileLabel(1);
        // 设置是否目录
        fileMapper.insert(file);
    }

    @Override
    public int updatePassword(UserEntity userEntity, String password){
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
        //List<UserEntity> after = projectMembers.stream().filter(itme -> !itme.getUserId().equals(ShiroAuthenticationManager.getUserId())).collect(Collectors.toList());
        return projectMembers;
    }
}
