package com.art1001.supply.service.user.impl;

import com.art1001.supply.entity.user.LoginInfoEntity;
import com.art1001.supply.mapper.base.BaseMapper;
import com.art1001.supply.mapper.user.LoginInfoMapper;
import com.art1001.supply.service.base.impl.AbstractService;
import com.art1001.supply.service.user.LoginInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class LoginInfoServiceImpl extends ServiceImpl<LoginInfoMapper,LoginInfoEntity> implements LoginInfoService {
	@Resource
	private LoginInfoMapper loginInfoMapper;

	@Override
	public List<LoginInfoEntity> queryListByPage(Map<String, Object> parameter) {
		return null;
	}

	@Override
	public int log(LoginInfoEntity loginInfo) {
		return loginInfoMapper.insert(loginInfo);
	}
	
}
