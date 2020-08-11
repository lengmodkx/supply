package com.art1001.supply.communication.service.impl;


import com.art1001.supply.communication.comm.TokenUtil;
import com.art1001.supply.communication.service.AuthTokenAPI;
import org.springframework.stereotype.Service;

@Service
public class EasemobAuthToken implements AuthTokenAPI {

	@Override
	public Object getAuthToken(){
		return TokenUtil.getAccessToken();
	}
}
