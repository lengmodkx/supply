package com.art1001.supply.service.user.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.aliyun.message.enums.KeyWord;
import com.art1001.supply.aliyun.message.exception.CodeMismatchException;
import com.art1001.supply.aliyun.message.exception.CodeNotFoundException;
import com.art1001.supply.application.assembler.WeChatUserInfoAssembler;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.user.*;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.file.FileMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.service.user.WechatAppIdInfoService;
import com.art1001.supply.shiro.util.JwtUtil;
import com.art1001.supply.util.*;
import com.art1001.supply.wechat.login.dto.WeChatDecryptResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,UserEntity> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private EmailUtil emailUtil;

    @Resource
    private WeChatUserInfoAssembler assembler;

    @Resource
    private OrganizationMemberService organizationMemberService;

    @Resource
    FileMapper fileMapper;
    @Override
    public List<UserEntity> queryListByPage(Map<String, Object> parameter) {
        return null;
    }

    @Override
    public UserEntity findByName(String accountName) {
        Optional.ofNullable(accountName).orElseThrow(() -> new ServiceException("accountName 不能为空！"));

        LambdaQueryWrapper<UserEntity> getSingleUserByAccountName = new QueryWrapper<UserEntity>().lambda().eq(UserEntity::getAccountName, accountName);

        return this.getOne(getSingleUserByAccountName);
    }

    @Override
    public UserInfo findInfo(String accountName){
        UserInfo info = userMapper.findInfo(accountName);
        String orgByUserId = organizationMemberService.findOrgByUserId(info.getUserId());
        info.setOrgId(orgByUserId);
        return info;
    }


    @Override
    public UserEntity findById(String id) {
        return getById(id);
    }

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private WechatAppIdInfoService wechatAppIdInfoService;

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
        String fileName = System.currentTimeMillis() + ".jpg";
        AliyunOss.uploadByte(Constants.MEMBER_IMAGE_URL + fileName, bytes);
        userEntity.setImage(Constants.OSS_URL+Constants.MEMBER_IMAGE_URL + fileName);
        userEntity.setDefaultImage(Constants.OSS_URL+Constants.MEMBER_IMAGE_URL + fileName);
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
        file.setFilePrivacy(1);
        file.setFileLabel(1);
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
    public UserInfo saveWeChatUserInfo(String code) {
        Oauth2Token oauth2AccessToken = getOauth2AccessToken(code);
        WeChatUser info = getSNSUserInfo(oauth2AccessToken.getAccessToken(), oauth2AccessToken.getOpenId());
        UserInfo userInfo = new UserInfo();
        UserEntity userEntity = getOne(new QueryWrapper<UserEntity>().eq("wx_open_id",info.getOpenId()));
        if(userEntity == null){//微信用户不存在，保存微信返回的信息
            UserEntity user = new UserEntity();
            user.setUserName(info.getNickname());
            user.setWxOpenId(info.getOpenId());
            user.setWxUnionId(info.getUnionid());
            user.setCredentialsSalt(IdGen.uuid());
            user.setUpdateTime(new Date());
            user.setCreateTime(new Date());
            user.setAddress(info.getCity());
            user.setUserId(IdGen.uuid());
            user.setSex(info.getSex());
            user.setDefaultImage(info.getHeadImgUrl());
            user.setImage(info.getHeadImgUrl());
            userMapper.insert(user);
            userInfo.setUserId(user.getUserId());
            userInfo.setUserName(user.getUserName());
            userInfo.setBindPhone(true);//微信信息存储完毕表明微信已经绑定
        } else {
            BeanUtils.copyProperties(userEntity,userInfo);
            userInfo.setBindPhone(false);
            String orgByUserId = organizationMemberService.findOrgByUserId(userEntity.getUserId());
            userInfo.setOrgId(orgByUserId);
            userInfo.setAccessToken(JwtUtil.sign(userEntity.getUserId(), "1qaz2wsx#EDC"));
        }
        return userInfo;
    }

    @Override
    public Boolean checkUserIsExistByAccountName(String accountName) {
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
    public void bindPhone(String phone, String code, String userId, String nickName) {

        if(!redisUtil.exists(KeyWord.PREFIX.getCodePrefix() + userId)){
            throw new CodeNotFoundException("验证码已经失效");
        }

        String redisCode = redisUtil.get(KeyWord.PREFIX.getCodePrefix() + userId);
        if(!Objects.equals(code, redisCode)){
            throw new CodeMismatchException("验证码错误！");
        }

        UserEntity byId = this.getById(userId);

        UserEntity userEntity = new UserEntity();
        if(this.checkUserIsExistByAccountName(phone)){
            UserEntity byName = this.findByName(phone);
            userEntity.setUserId(byName.getUserId());
            userEntity.setWxUnionId(byId.getWxUnionId());
            userEntity.setWxOpenId(byId.getWxOpenId());
            userEntity.setUpdateTime(new Date());
            this.updateById(userEntity);
            removeById(userId);
        } else {
            userEntity.setUserId(userId);
            userEntity.setWxUnionId(byId.getWxUnionId());
            userEntity.setWxOpenId(byId.getWxOpenId());
            userEntity.setUpdateTime(new Date());
            userEntity.setAccountName(phone);
            userEntity.setUserName(nickName);
            this.updateById(userEntity);
        }
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


    @Override
    public UserEntity saveWeChatAppUserInfo(WeChatDecryptResponse res) {

        LambdaQueryWrapper<UserEntity> getSingleUserByWxUnionId = new QueryWrapper<UserEntity>()
                .lambda()
                .eq(UserEntity::getWxUnionId, res.getUnionId());

        UserEntity one = this.getOne(getSingleUserByWxUnionId);

        //如果pc微信已经注册
        if(ObjectsUtil.isNotEmpty(one)){
            UserEntity saveUserInfo = new UserEntity();
            saveUserInfo.setUserId(one.getUserId());
            saveUserInfo.setWxOpenId(res.getOpenId());
            saveUserInfo.setUpdateTime(new Date());
            this.updateById(saveUserInfo);
            return one;
        }

        UserEntity userEntity = assembler.weChatUserTransUserEntity(res);
        this.save(userEntity);
        userEntity.setWxUnionId(null);
        return userEntity;
    }

    @Override
    public void bindWeChat(String code,String userId) {
        Oauth2Token oauth2AccessToken = getOauth2AccessToken(code);
        WeChatUser info = getSNSUserInfo(oauth2AccessToken.getAccessToken(), oauth2AccessToken.getOpenId());
        LambdaQueryWrapper<UserEntity> queryWrapper = new QueryWrapper<UserEntity>()
                .lambda().eq(UserEntity::getWxOpenId, info.getOpenId());
        if(this.getOne(queryWrapper) != null){
            throw new ServiceException("该微信号已经被其他手机号绑定，请更换微信号重试！");
        }

        UserEntity updateEntity = new UserEntity();
        updateEntity.setUpdateTime(new Date());
        updateEntity.setWxOpenId(info.getOpenId());
        updateEntity.setWxUnionId(info.getUnionid());
        updateEntity.setUserId(userId);
        updateById(updateEntity);
    }

    @Override
    public String getAppOpenIdByUserId(String userId) {
        ValidatedUtil.filterNullParam(userId);

        final String wxAppOpenid = "wxAppOpenid";

        LambdaQueryWrapper<UserEntity> getWxAppOpenIdByUserId = new QueryWrapper<UserEntity>()
                .lambda()
                .eq(UserEntity::getUserId, userId)
                .select(UserEntity::getWxAppOpenId);

        Map<String, Object> map = this.getMap(getWxAppOpenIdByUserId);

        if(map.containsKey(wxAppOpenid)){
            return String.valueOf(map.get(wxAppOpenid));
        }

        return null;
    }

    @Override
    public void changePasswordByUserId(String oldPassword, String newPassword, String userId) {
        Optional.ofNullable(userId).orElseThrow(() -> new ServiceException("用户id不能为空！"));

        UserEntity byId = Optional.ofNullable(this.getById(userId)).orElseThrow(() -> new ServiceException("用户不存在!"));

        String oldEncryptionPassword = new Md5Hash(oldPassword,byId.getAccountName() + byId.getCredentialsSalt(),2).toBase64();

        if(!oldEncryptionPassword.equals(byId.getPassword())){
            throw new ServiceException("原密码错误！");
        }
        UserEntity upd = new UserEntity();

        upd.setUserId(userId);
        upd.setUpdateTime(new Date());
        upd.setPassword(new Md5Hash(newPassword, byId.getAccountName() + byId.getCredentialsSalt(), 2).toBase64());

        this.updateById(upd);
    }




    /**
     * 获取网页授权凭证
     * @param code
     * @return WeixinAouth2Token
     */
    private Oauth2Token getOauth2AccessToken(String code) {
        Oauth2Token wat = null;
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        requestUrl = requestUrl.replace("APPID", ConstansWeChat.APPID);
        requestUrl = requestUrl.replace("SECRET", ConstansWeChat.SECRET);
        requestUrl = requestUrl.replace("CODE", code);
        // 获取网页授权凭证
        JSONObject jsonObject = null;
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(requestUrl);
            CloseableHttpResponse execute = httpClient.execute(httpGet);
            HttpEntity entity = execute.getEntity();
            jsonObject = JSONObject.parseObject(EntityUtils.toString(entity));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null != jsonObject) {
            try {
                wat = new Oauth2Token();
                wat.setAccessToken(jsonObject.getString("access_token"));
                wat.setExpiresIn(jsonObject.getInteger("expires_in"));
                wat.setRefreshToken(jsonObject.getString("refresh_token"));
                wat.setOpenId(jsonObject.getString("openid"));
                wat.setScope(jsonObject.getString("scope"));
            } catch (Exception e) {
                wat = null;
                int errorCode = jsonObject.getInteger("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                log.error("获取网页授权凭证失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }
        return wat;
    }

    /**
     * 通过网页授权获取用户信息
     *
     * @param accessToken 网页授权接口调用凭证
     * @param openId 用户标识
     * @return SNSUserInfo
     */
    private WeChatUser getSNSUserInfo(String accessToken, String openId) {
        WeChatUser snsUserInfo = null;
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
        // 通过网页授权获取用户信息
        JSONObject jsonObject = null;
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(requestUrl);
            CloseableHttpResponse execute = null;
            execute = httpClient.execute(httpGet);
            HttpEntity entity = execute.getEntity();
            jsonObject = JSONObject.parseObject(new String(EntityUtils.toString(entity).getBytes(),"utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null != jsonObject) {
            try {
                snsUserInfo = new WeChatUser();
                // 用户的标识
                snsUserInfo.setOpenId(jsonObject.getString("openid"));
                // 昵称
                snsUserInfo.setNickname(new String(jsonObject.getString("nickname").getBytes("ISO-8859-1"),"utf-8"));
                // 性别（1是男性，2是女性，0是未知）
                snsUserInfo.setSex(jsonObject.getInteger("sex"));
                // 用户所在国家
                snsUserInfo.setCountry(jsonObject.getString("country"));
                // 用户所在省份
                snsUserInfo.setProvince(jsonObject.getString("province"));
                // 用户所在城市
                snsUserInfo.setCity(jsonObject.getString("city"));
                // 用户头像
                snsUserInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
                // 用户特权信息
                List<String> list = JSON.parseArray(jsonObject.getString("privilege"),String.class);
                snsUserInfo.setPrivilegeList(list);
                //与开放平台共用的唯一标识，只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
                snsUserInfo.setUnionid(jsonObject.getString("unionid"));
            } catch (Exception e) {
                snsUserInfo = null;
                int errorCode = jsonObject.getInteger("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                log.error("获取用户信息失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }
        return snsUserInfo;
    }

    //通过电话或姓名搜索所有企业员工
    @Override
    public List<UserEntity> getUserByOrgId(String phone, String orgId) {
        return userMapper.getUserByOrgId(phone,orgId);
    }



}
