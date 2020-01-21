package com.art1001.supply.service.organization;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.project.Project;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * organizationService接口
 */
public interface OrganizationService extends IService<Organization> {

	/**
	 * 查询分页organization数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<Organization> findOrganizationPagerList(Pager pager);

	/**
	 * 通过organizationId获取单条organization数据
	 * 
	 * @param organizationId
	 * @return
	 */
	Organization findOrganizationByOrganizationId(String organizationId);

	/**
	 * 通过organizationId删除organization数据
	 * 
	 * @param organizationId
	 */
	void deleteOrganizationByOrganizationId(String organizationId);

	/**
	 * 修改organization数据
	 * 
	 * @param organization
	 */
	void updateOrganization(Organization organization);

	/**
	 * 保存organization数据
	 * 
	 * @param organization
	 */
	Integer saveOrganization(Organization organization);

	/**
	 * 获取所有organization数据
	 * 
	 * @return
	 */
	List<Organization> findOrganizationAllList();

	/**
	 * 获取用户参与的所有企业 以及 企业项目
	 * @param userId
	 * @return
	 */
    List<Organization> findJoinOrgProject(String userId);

	/**
	 * 获取和我相关的企业
	 * @param flag 标识
	 * @return 企业列表
	 */
	List<Organization> getMyOrg(Integer flag);

	/**
	 * 判断企业是否存在
	 * @param organizationId 企业id
	 * @return 结果
	 */
    Boolean checkOrgIsExist(String organizationId);

    /**
	 * 获取企业下的项目信息
     * @author heShaoHua
     * @describe 暂无
     * @param orgId 企业id
     * @updateInfo 暂无
     * @date 2019/5/29 10:37
     * @return 项目信息集合
     */
    List<Project> getProject(String orgId);

	/**
	 * 修改默认企业为个人项目
	 * @param userId 用户id
	 */
	void personalProject(String userId);
}