package com.art1001.supply.service.user.impl;

import com.art1001.supply.aliyun.message.enums.KeyWord;
import com.art1001.supply.aliyun.message.exception.CodeMismatchException;
import com.art1001.supply.aliyun.message.exception.CodeNotFoundException;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.WeChatUser;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.file.FileMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.*;
import com.art1001.supply.util.crypto.EndecryptUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.web.subject.support.DefaultWebSubjectContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;

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

    @Resource
    private RedisUtil redisUtil;

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

    @Override
    public UserEntity saveWeChatUserInfo(WeChatUser snsUserInfo) {
        UserEntity userEntity;
        LambdaQueryWrapper<UserEntity> selectUserIsExistQw = new QueryWrapper<UserEntity>().lambda().eq(UserEntity::getUserId, snsUserInfo.getOpenId());
        userEntity = this.getOne(selectUserIsExistQw);
        if(userEntity == null){
            userEntity.setUserName(snsUserInfo.getNickname());
            userEntity.setPassword("123456");
            userEntity.setAccountName(snsUserInfo.getOpenId());
            UserEntity md5UserEntity = EndecryptUtils.md5Password(userEntity.getAccountName(), userEntity.getPassword(), 2);
            userEntity.setCredentialsSalt(md5UserEntity.getCredentialsSalt());
            userEntity.setPassword(md5UserEntity.getPassword());
            userEntity.setUpdateTime(new Date());
            userEntity.setCreateTime(new Date());
            userEntity.setAddress(snsUserInfo.getCity());
            userEntity.setUserId(snsUserInfo.getOpenId());
            userEntity.setSex(snsUserInfo.getSex());
            userEntity.setDefaultImage(snsUserInfo.getHeadImgUrl());
            userMapper.insert(userEntity);
        }
        return userEntity;
    }

    @Override
    public Boolean checkUserIsExistByAccountName(String accountName) {
        if(Stringer.isNullOrEmpty(accountName)){
            return false;
        }
        //构造出查询用户是否存在的条件表达式
        LambdaQueryWrapper<UserEntity> selectUserIsExistQw = new QueryWrapper<UserEntity>().lambda().eq(UserEntity::getAccountName, accountName);
        return userMapper.selectCount(selectUserIsExistQw) > 0;
    }

    @Override
    public List<UserEntity> getUserListByIdList(Collection<String> idList) {
        if(CollectionUtils.isEmpty(idList)){
            return new ArrayList();
        }

        //构造出sql表达式
        LambdaQueryWrapper<UserEntity> selectUserByUserIdList = new QueryWrapper<UserEntity>().lambda()
                .in(UserEntity::getUserId, idList)
                .select(UserEntity::getUserId, UserEntity::getUserName, UserEntity::getImage, UserEntity::getAccountName);
        return userMapper.selectList(selectUserByUserIdList);
    }

    @Override
    public void resetPassword(String accountname, String newPassword) {
        if(!this.checkUserIsExistByAccountName(accountname)){
            throw new ServiceException("用户名不存在！");
        }

        LambdaQueryWrapper<UserEntity> eq = new QueryWrapper<UserEntity>().lambda().eq(UserEntity::getAccountName, accountname);

        UserEntity one = this.getOne(eq);

        //组合username,两次迭代，对密码进行加密
        String password_cryto = new Md5Hash(newPassword,accountname+one.getCredentialsSalt(),2).toBase64();
        UserEntity user=new UserEntity();
        user.setPassword(password_cryto);
        user.setUserName(accountname);

        this.update(user, eq);
    }

    @Override
    public void bindPhone(String phone, String code) {

        int count = this.checkUserIsExist(phone);

        if(count > 0){
            throw new ServiceException("用户应绑定手机号，不能重复绑定");
        }

        if(!redisUtil.exists(KeyWord.PREFIX + ShiroAuthenticationManager.getUserId())){
            throw new CodeNotFoundException("验证码已经失效");
        }

        String redisCode = redisUtil.get(KeyWord.PREFIX + ShiroAuthenticationManager.getUserId());
        if(!Objects.equals(code, redisCode)){
            throw new CodeMismatchException("验证码错误！");
        }

        LambdaQueryWrapper<UserEntity> eq = new QueryWrapper<UserEntity>().lambda()
                .eq(UserEntity::getUserId, ShiroAuthenticationManager.getUserId());

        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(ShiroAuthenticationManager.getUserId());
        userEntity.setUpdateTime(new Date());
        userEntity.setAccountName(phone);

        this.update(userEntity, eq);


    }

    @Override
    public int checkUserIsExist(String keyword) {
        Optional.ofNullable(keyword).orElseThrow(() -> new ServiceException("keyword 不能为空！"));

        LambdaQueryWrapper<UserEntity> selectUserEntityCountQw = new QueryWrapper<UserEntity>().lambda()
                .eq(UserEntity::getUserId, keyword)
                .or()
                .eq(UserEntity::getAccountName, keyword);

        return this.count(selectUserEntityCountQw);
    }

    @Override
    public List<String> getAllUserId() {
        return userMapper.getAllUserId();
    }

    @Override
    public List<String> getPhoneList() {
        return userMapper.getPhoneList();
    }
}
