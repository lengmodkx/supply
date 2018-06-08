package com.art1001.supply.service.user;


import com.art1001.supply.entity.user.LoginInfoEntity;

import java.util.List;
import java.util.Map;

public interface LoginInfoService {

	public int log(LoginInfoEntity loginInfo);
	
	public List<LoginInfoEntity> queryListByPage(Map<String, Object> parameter);
}
