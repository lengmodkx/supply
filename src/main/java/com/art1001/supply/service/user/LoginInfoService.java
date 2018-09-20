package com.art1001.supply.service.user;


import com.art1001.supply.entity.user.LoginInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface LoginInfoService extends IService<LoginInfoEntity> {

	public int log(LoginInfoEntity loginInfo);
	
	public List<LoginInfoEntity> queryListByPage(Map<String, Object> parameter);
}
