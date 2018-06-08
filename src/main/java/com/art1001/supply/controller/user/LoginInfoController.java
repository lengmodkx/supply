package com.art1001.supply.controller.user;

import com.alibaba.fastjson.JSON;
import com.art1001.supply.dtgrid.model.Pager;
import com.art1001.supply.dtgrid.util.ExportUtils;
import com.art1001.supply.entity.user.LoginInfoEntity;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.user.LoginInfoService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/loginInfo/")
public class LoginInfoController {

	@Autowired
	private LoginInfoService loginInfoService;

	@RequestMapping("listUI.html")
	public String listUI() {
		try
		{
			return "/logininfo/list";
		}catch(Exception e)
		{
			throw new SystemException(e);
		}
	}

	/**
	 * ajax分页动态加载模式
	 * 
	 * @param gridPager
	 *            Pager对象
	 * @param response HttpServletResponse对象
	 * return Object 视图信息
	 * @throws Exception	异常信息
	 */
	@RequestMapping(value = "/list.html", method = RequestMethod.POST)
	@ResponseBody
	public Object list(String gridPager, HttpServletResponse response) throws Exception{
		Map<String, Object> parameters = null;
		// 映射Pager对象
		Pager pager = JSON.parseObject(gridPager, Pager.class);
		// 判断是否包含自定义参数
		parameters = pager.getParameters();
		if (parameters.size() < 0) {
			parameters.put("accountName", null);
		}
		//3、判断是否是导出操作
				if(pager.getIsExport())
				{
					if(pager.getExportAllData())
					{
						//3.1、导出全部数据
						List<LoginInfoEntity> list = loginInfoService.queryListByPage(parameters);
						ExportUtils.exportAll(response, pager, list);
						return null;
					}else
					{
						//3.2、导出当前页数据
						ExportUtils.export(response, pager);
						return null;
					}
				}else
				{
					// 设置分页，page里面包含了分页信息
					Page<Object> page = PageHelper.startPage(pager.getNowPage(),pager.getPageSize(), true);
					List<LoginInfoEntity> list = loginInfoService.queryListByPage(parameters);
					parameters.clear();
					parameters.put("isSuccess", Boolean.TRUE);
					parameters.put("nowPage", pager.getNowPage());
					parameters.put("pageSize", pager.getPageSize());
					parameters.put("pageCount", page.getPages());
					parameters.put("recordCount", page.getTotal());
					parameters.put("startRecord", page.getStartRow());
					parameters.put("exhibitDatas", list);
					return parameters;
				}
		
	}

}
