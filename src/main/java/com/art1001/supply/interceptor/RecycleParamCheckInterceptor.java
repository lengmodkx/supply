package com.art1001.supply.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.api.request.RecycleBinParamDTO;
import com.art1001.supply.common.Constants;
import com.art1001.supply.exception.BaseException;
import com.art1001.supply.param.check.recyclebin.RecycleBinParamCheck;
import com.art1001.supply.service.recycle.RecycleBinParamCheckEnum;
import com.art1001.supply.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author heshaohua
 * @version 1.0.0
 * @date 2020年03月12日 17:04:00
 */
@Component
@Slf4j
public class RecycleParamCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String publicType = request.getParameter("publicType");
        String action = request.getParameter("action");
        if(StringUtils.isEmpty(publicType) && StringUtils.isEmpty(action)){
            this.backResponse(response, "必要参数不能为空！");
            return false;
        }

        if(!Constants.RECOVERY.equals(action) && !Constants.MOVE.equals(action)){
            this.backResponse(response, "action参数只能传递[move]和[recovery]");
            return false;
        }

        //获取需要检验参数Bean的名称
        String beanName = RecycleBinParamCheckEnum.getBeanName(publicType);
        if(StringUtils.isEmpty(beanName)){
            this.backResponse(response, "publicType参数信息不准确.");
            return false;
        }
        RecycleBinParamCheck paramCheck = SpringContextUtil.getBean(beanName, RecycleBinParamCheck.class);

        try {
            paramCheck.checkParam(this.setParam(request));
        } catch (BaseException e){
            this.backResponse(response, e.getMessage());
            return false;
        }
        return true;
    }


    private void backResponse(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", msg);
        PrintWriter writer = response.getWriter();
        writer.print(jsonObject.toJSONString());
        writer.close();
    }


    /**
     * 将请求中的参数封装好
     * @param request http请求对象
     */
    private RecycleBinParamDTO setParam(HttpServletRequest request){
        RecycleBinParamDTO recycleBinParamDTO = new RecycleBinParamDTO();
        recycleBinParamDTO.setPublicType(request.getParameter("publicType"));
        recycleBinParamDTO.setPublicId(request.getParameter("publicId"));
        recycleBinParamDTO.setGroupId(request.getParameter("groupId"));
        recycleBinParamDTO.setProjectId(request.getParameter("projectId"));
        recycleBinParamDTO.setMenuId(request.getParameter("menuId"));
        recycleBinParamDTO.setAction(request.getParameter("action"));
        String fileIds = request.getParameter("fileIdList");
        if(StringUtils.isNotEmpty(fileIds)){
            recycleBinParamDTO.setFileIdList(Arrays.asList(fileIds.split(",")));
        } else {
            recycleBinParamDTO.setFileIdList(new LinkedList<>());
        }

        return recycleBinParamDTO;

    }


}
