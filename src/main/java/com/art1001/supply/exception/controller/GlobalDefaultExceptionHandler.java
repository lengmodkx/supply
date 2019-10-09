package com.art1001.supply.exception.controller;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ApiParamsCheckException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.Set;

/**
 * 
 * Web层异常处理器,这里可以根据不同的异常,写多个方法去处理,可以处理跳转页面请求,
 * 跳到异常指定的错误页,也可以处理Ajax请求,根据不同异常,在页面输出不同的提示信息;
 * operateExp 处理普通请求 operateExpAjax 处理Ajax请求
 * @author wangyafeng
 * 2016年7月12日 下午3:14:17
 *
 */
//@EnableWebMvc
@Slf4j
@ControllerAdvice
public class GlobalDefaultExceptionHandler {

	/*
	 * 如果抛出UnauthorizedException，将被该异常处理器截获来显示没有权限信息
	 */
	/*
	@ExceptionHandler({ UnauthorizedException.class })
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ModelAndView unauthenticatedException(NativeWebRequest request,
			UnauthorizedException e) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("exception", e);
		mv.setViewName("base/exception/unauthorized");
		return mv;
	}
	*/

	/**
	 * 全局异常控制，记录日志任何一个方法发生异常，一定会被这个方法拦截到。然后，输出日志。封装Map并返回给页面显示错误信息：
	 * 特别注意：返回给页面错误信息只在开发时才使用，上线后，要把错误页面去掉，只打印log日志即可，防止信息外漏
	 * @param: ex	自定义系统异常
	 * @param: request	HttpServletRequest对象
	 * @return: String	视图信息
	 */
	@ExceptionHandler(SystemException.class)
	public String operateSystemException(SystemException ex, HttpServletRequest request) {
		ex.getCause().printStackTrace();
		log.error(ex.getMessage(), ex);
		return "error/500";
	}

	/**
	 * 记录Ajax异常日志，并将错误Ajax错误信息传递(回写)给前台展示,
	 * 前台的jQuery的Ajax请求error中，可以打印错误提示信息 --
	 * data.responseText : 这里即是后台传递的错误提示
	 * eg: $.ajax({
	 * 	type : 'get',
	 * 	dataType :"json",
	 * 	url : ctx + '/test/test',
	 * 	accept:"application/json",
	 * 	success :function(data) {
	 * 		console.log(data);
	 * 	},
	 * 	error : function(data,errorThrown) {
	 * 		console.log(data);
	 * 		alert("error" + data.responseText);
	 * 	}
	 * });
	 *
	 * @param ex	ajax异常对象
	 * @throws IOException	异常信息
	 */
	@ExceptionHandler(AjaxException.class)
	@ResponseBody
	public JSONObject operateExpAjax(AjaxException ex) {
		log.error(ex.getMessage(), ex);
		// 将Ajax异常信息回写到前台,用于页面的提示
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("msg",ex.getMessage());
		jsonObject.put("result",0);
		return jsonObject;
	}

	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Object handle(ValidationException exception) {
		JSONObject jsonObject = new JSONObject();
		if(exception instanceof ConstraintViolationException){
			ConstraintViolationException exs = (ConstraintViolationException) exception;
			Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
			StringBuilder sb = new StringBuilder();
			for (ConstraintViolation<?> item : violations) {
				sb.append(item.getMessage()).append("/n");
			}
			jsonObject.put("msg", sb);
			jsonObject.put("result", 0);
		}
		return jsonObject;
	}


	@ExceptionHandler(ApiParamsCheckException.class)
    @ResponseBody
	public JSONObject paramsExceptionHandle(ApiParamsCheckException exception){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", 0);
        jsonObject.put("msg", exception.getMessage());
        return jsonObject;
    }

	/**
	 * 记录授权失败异常
	 * @param ex 无权限异常
	 * @return 返回结果
	 */
	@ExceptionHandler(AuthorizationException.class)
	@ResponseBody
	public JSONObject operateAuthorizationException(HttpServletResponse response, AuthorizationException ex) {
		log.error(ex.getMessage(), ex);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("msg","你没有权限执行此操作!");
		jsonObject.put("result",0);
		response.setStatus(203);
		return jsonObject;
	}

	@ExceptionHandler(ServiceException.class)
	@ResponseBody
	public JSONObject operateExp(ServiceException ex) {
		log.error(ex.getMessage(), ex);
		// 将Ajax异常信息回写到前台,用于页面的提示
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("msg",ex.getMessage());
		jsonObject.put("result",0);
		return jsonObject;
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public JSONObject operateExp(Exception ex) {
		log.error(ex.getMessage(), ex);
		// 将Ajax异常信息回写到前台,用于页面的提示
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("msg","系统异常!");
		jsonObject.put("result",0);
		return jsonObject;
	}

}
