package com.art1001.supply.service.partment;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.partment.Partment;
import com.art1001.supply.entity.partment.PartmentMember;
import com.art1001.supply.entity.tree.Tree;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * partmentService接口
 */
public interface PartmentService extends IService<Partment> {

	/**
	 * 查询分页partment数据
	 *
	 * @param pager 分页对象
	 * @return
	 */
	List<Partment> findPartmentPagerList(Pager pager);

	/**
	 * 通过partmentId获取单条partment数据
	 *
	 * @param partmentId
	 * @return
	 */
	Partment findPartmentByPartmentId(String partmentId);

	/**
	 * 通过partmentId删除partment数据
	 *
	 * @param partmentId
	 */
	void deletePartmentByPartmentId(String partmentId);

	/**
	 * 修改partment数据
	 *
	 * @param partment
	 */
	void updatePartment(Partment partment);

	/**
	 * 保存partment数据
	 *
	 * @param partment
	 */
	void savePartment(Partment partment);

	/**
	 * 检查该部门是存在
	 *
	 * @param partmentId 部门id
	 * @return 结果
	 */
	boolean checkPartmentIsExist(String partmentId);

	/**
	 * 排序部门
	 *
	 * @param partmentIds 排序后的部门id
	 */
	Boolean orderPartment(String[] partmentIds);

	/**
	 * 获取某个企业下的部门信息
	 *
	 * @param orgId 企业id
	 * @return 部门信息
	 */
	List<Partment> findOrgPartmentInfo(String orgId);

	/**
	 * @param orgId        企业id
	 * @param departmentId 部门id
	 * @return 数据列表
	 */
	List<Tree> getTree(String orgId, String departmentId);

	/**
	 * 根据id获取该部门的子部门数量
	 *
	 * @param departmentId 部门id
	 * @return 子部门数量
	 */
	int getChildCount(String departmentId);

	List<Partment> findSubPartment(String parentId);

	/**
	 * 获取部门简单信息
	 * @param memberId
	 * @return
	 */
    Partment getSimpleDeptInfo(String memberId,String orgId);

	List<Partment> findOrgParentByOrgId(String orgId);

}