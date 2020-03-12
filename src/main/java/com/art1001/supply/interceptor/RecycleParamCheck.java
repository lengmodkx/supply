package com.art1001.supply.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.exception.BaseException;
import com.art1001.supply.util.ValidatedUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author heshaohua
 * @version 1.0.0
 * @date 2020年03月12日 17:04:00
 */
@Component
@Slf4j
public class RecycleParamCheck implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String publicType = request.getParameter("publicType");
        try {

            if(StringUtils.isEmpty(publicType)){
                this.backResponse(response, "publicType不能为空！");

                return false;
            }

            if(Constants.TASK_EN.equals(publicType)){
                this.checkTaskParamIsCorrect(request, response);
            }

            if(Constants.FILE_EN.equals(publicType)){
                this.checkFileParamIsCorrect(request, response);
            }

            if(Constants.SHARE_EN.equals(publicType)){
                this.checkFileParamIsCorrect(request, response);
            }
        } catch (BaseException e){
            this.backResponse(response, e.getMessage());
        }

        return false;
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

    private void checkTaskParamIsCorrect(Object ... params){
        ValidatedUtil.filterNullParam(params);
    }

    private void checkFileParamIsCorrect(HttpServletRequest request, HttpServletResponse response){
       String fileId = request.getParameter("fileId");
       String projectId = request.getParameter("projectId");

       try {
           ValidatedUtil.filterNullParam(fileId, projectId);
       } catch (BaseException e){
           try {
               this.backResponse(response, e.getMessage());
           } catch (IOException e1) {
               e1.printStackTrace();
           }
       }
    }


}
