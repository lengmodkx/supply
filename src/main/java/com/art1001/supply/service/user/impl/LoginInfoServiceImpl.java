package com.art1001.supply.service.user.impl;

import com.art1001.supply.entity.user.LoginInfoEntity;
import com.art1001.supply.mapper.base.BaseMapper;
import com.art1001.supply.mapper.user.LoginInfoMapper;
import com.art1001.supply.service.base.impl.AbstractService;
import com.art1001.supply.service.user.LoginInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LoginInfoServiceImpl extends AbstractService<LoginInfoEntity, Long> implements LoginInfoService {

	@Resource
	private LoginInfoMapper loginInfoMapper;

	protected LoginInfoServiceImpl(BaseMapper<LoginInfoEntity, Long> baseMapper) {
		super(baseMapper);
	}

	@Override
	public int log(LoginInfoEntity loginInfo) {
		return loginInfoMapper.insert(loginInfo);
	}
	
}
