package com.art1001.supply.mapper.organization;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.project.Project;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * organizationmapper接口
 */
@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {

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
	void saveOrganization(Organization organization);

	/**
	 * 获取所有organization数据
	 * 
	 * @return
	 */
	List<Organization> findOrganizationAllList();

	/**
	 * 查询出 参与的所有企业 以及 项目
	 * @param userId
	 * @return
	 */
	List<Organization> selectJoinOrgProject(String userId);

	/**
	 * 获取和我相关的企业
	 * @param flag 标识
	 * @param userId 用户id
	 * @return 企业列表
	 */
    List<Organization> getMyOrg(@Param("flag") Integer flag, @Param("userId") String userId);

    /**
	 * 获取企业下的项目信息
     * @author heShaoHua
     * @describe 暂无
     * @param orgId 企业id
     * @updateInfo 暂无
     * @date 2019/5/29 10:56
     * @return 企业项目集合
     */
    List<Project> selectProject(@Param("orgId") String orgId);

}