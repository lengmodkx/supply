package com.art1001.supply.service.user.impl;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.base.BaseMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.service.base.impl.AbstractService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl extends AbstractService<UserEntity, Long> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private EmailUtil emailUtil;

    protected UserServiceImpl(UserMapper userMapper) {
        super(userMapper);
    }

    /**
     * 重写用户插入，逻辑：
     * 1、插入用户
     * 2、插入用户和角色的对应关系
     * 3、插入用户的个人资料信息
     */
    @Override
    public int insert(UserEntity userEntity, String password) {
        try {
            if (userMapper.insert(userEntity) == 1) {
                if (userMapper.insertUserRole(userEntity) == 1) {
                    userEntity.getUserInfo().setId(userEntity.getId());
                    int cnt = userMapper.insertUserInfo(userEntity);
                    //发送邮件
//                    emailUtil.send126Mail(userEntity.getAccountName(), "系统消息通知", "您好,您的账户已创建,账户名:" + userEntity.getAccountName() + " ,密码:" + password);
                    return cnt;
                } else {
                    throw new ServiceException("更新用户: " + userEntity.getId() + " 的权限信息失败");
                }
            } else {
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
            if (userMapper.update(userEntity) == 1) {
                if (userMapper.updateUserRole(userEntity) == 1) {
                    int result = userMapper.updateUserInfo(userEntity);
                    ShiroAuthenticationManager.clearUserAuthByUserId(userEntity.getId());
                    return result;
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 重写用户删除逻辑：
     * 1、删除用户和角色的对应关系
     * 2、删除用户
     */
    @Override
    public int deleteBatchById(List<Long> userIds) {
        try {
            int result = userMapper.deleteBatchUserRole(userIds);
            if (result == userIds.size()) {
                return userMapper.deleteBatchById(userIds);
            } else {
                return 0;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public int updateOnly(UserEntity userEntity) throws ServiceException {
        try {
            int cnt = userMapper.update(userEntity);
            return cnt;
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
    public List<UserEntity> findListPager(Pager pager) {
        return userMapper.findListPager(pager);
    }

    @Override
    public int findCount() {
        return userMapper.findCount();
    }


}
