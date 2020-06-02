package com.art1001.supply.communication.service.impl;

import com.art1001.supply.communication.comm.EasemobAPI;
import com.art1001.supply.communication.comm.OrgInfo;
import com.art1001.supply.communication.comm.ResponseHandler;
import com.art1001.supply.communication.comm.TokenUtil;
import com.art1001.supply.communication.service.IMUserService;
import io.swagger.client.ApiException;
import io.swagger.client.api.UsersApi;
import io.swagger.client.model.RegisterUsers;
import org.springframework.stereotype.Service;

/**
 * @ClassName IMUserServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/6/2 16:17
 * @Discription
 */
@Service
public class IMUserServiceImpl implements IMUserService {

    private UsersApi api = new UsersApi();
    private ResponseHandler responseHandler = new ResponseHandler();

    @Override
    public Object createNewIMUserSingle(Object payload) {
        return responseHandler.handle(() -> api.orgNameAppNameUsersPost(OrgInfo.ORG_NAME,OrgInfo.APP_NAME, (RegisterUsers) payload, TokenUtil.getAccessToken()));
    }

    @Override
    public Object createNewIMUserBatch(Object payload) {
        return responseHandler.handle(() -> api.orgNameAppNameUsersPost(OrgInfo.ORG_NAME,OrgInfo.APP_NAME, (RegisterUsers) payload,TokenUtil.getAccessToken()));
    }
}
