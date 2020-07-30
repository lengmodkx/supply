package com.art1001.supply.service.partment.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Resource;

import com.art1001.supply.application.assembler.DepartmentDataAssembler;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.partment.Partment;
import com.art1001.supply.entity.partment.PartmentMember;
import com.art1001.supply.entity.tree.Tree;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.partment.PartmentMapper;
import com.art1001.supply.service.partment.PartmentMemberService;
import com.art1001.supply.service.partment.PartmentService;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.util.ValidatedUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import org.springframework.transaction.annotation.Transactional;

/**
 * partmentServiceImpl
 */
@Service
public class PartmentServiceImpl extends ServiceImpl<PartmentMapper, Partment> implements PartmentService {

    /**
     * partmentMapper接口
     */
    @Resource
    private PartmentMapper partmentMapper;

    @Resource
    private PartmentMemberService partmentMemberService;

    @Resource
    private DepartmentDataAssembler dataAssembler;

    @Resource
    private OrganizationMemberService organizationMemberService;

    /**
     * 查询分页partment数据
     *
     * @param pager 分页对象
     * @return
     */
    @Override
    public List<Partment> findPartmentPagerList(Pager pager) {
        return partmentMapper.findPartmentPagerList(pager);
    }

    /**
     * 通过partmentId获取单条partment数据
     *
     * @param partmentId
     * @return
     */
    @Override
    public Partment findPartmentByPartmentId(String partmentId) {
        return partmentMapper.findPartmentByPartmentId(partmentId);
    }

    /**
     * 通过partmentId删除partment数据
     *
     * @param partmentId 部门id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deletePartmentByPartmentId(String partmentId) {
        //删除部门和成员的关系数据
        List<String> subIds = partmentMapper.selectList(new QueryWrapper<Partment>().eq("parent_id", partmentId).select("partment_id")).stream().map(Partment::getPartmentId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(subIds)) {
            subIds = new ArrayList<String>();
        }
        subIds.add(partmentId);
        partmentMemberService.remove(new QueryWrapper<PartmentMember>().in("partment_id", subIds));
        //删除子部门
        partmentMapper.delete(new QueryWrapper<Partment>().eq("parent_id", partmentId));
        //删除部门
        partmentMapper.deletePartmentByPartmentId(partmentId);
    }

    /**
     * 修改partment数据
     *
     * @param partment
     */
    @Override
    public void updatePartment(Partment partment) {
        partmentMapper.updatePartment(partment);
    }

    /**
     * 保存partment数据
     *
     * @param partment
     */
    @Override
    public void savePartment(Partment partment) {
        if (StringUtils.isEmpty(partment.getParentId())) {
            partment.setParentId("0");
        }
        int maxOrder = partmentMapper.findMaxOrder(partment.getOrganizationId(), partment.getParentId());
        partment.setPartmentOrder(maxOrder + 1);
        partment.setUpdateTime(System.currentTimeMillis());
        partment.setCreateTime(System.currentTimeMillis());
        partmentMapper.insert(partment);

    }

    /**
     * 检查该部门是存在
     *
     * @param partmentId 部门id
     * @return 结果
     */
    @Override
    public boolean checkPartmentIsExist(String partmentId) {
        return partmentMapper.selectCount(new QueryWrapper<Partment>().eq("partment_id", partmentId)) > 0;
    }

    /**
     * 获取某个企业下的部门信息
     *
     * @param orgId 企业id
     * @return 部门信息
     */
    @Override
    public List<Partment> findOrgPartmentInfo(String orgId) {
        List<Partment> partments = partmentMapper.selectOrgPartmentInfo(orgId);
        partments.forEach(item -> {
            List<Partment> subPartment = partmentMapper.findSubPartment(item.getPartmentId());
            if (!CollectionUtils.isEmpty(subPartment)) {
                item.setHasPartment(true);
            } else {
                item.setHasPartment(false);
            }
        });
        return partments;
    }

    /**
     * 排序部门
     *
     * @param partmentIds 排序后的部门id
     */
    @Override
    public Boolean orderPartment(String[] partmentIds) {
        List<Partment> partments = new ArrayList<>();
        int total = partmentIds.length;
        for (String partmentId : partmentIds) {
            Partment partment = new Partment();
            partment.setPartmentId(partmentId);
            partment.setPartmentOrder(total);
            partment.setUpdateTime(System.currentTimeMillis());
            partments.add(partment);
            total--;
        }
        return updateBatchById(partments);
    }

    @Override
    public List<Tree> getTree(String orgId, String departmentId) {
        LambdaQueryWrapper<Partment> eq = new QueryWrapper<Partment>().lambda();
        if (StringUtils.isNotEmpty(orgId)) {
            eq.eq(Partment::getOrganizationId, orgId).eq(Partment::getParentId, "0");
        } else {
            eq.eq(Partment::getParentId, departmentId);
        }

        List<Partment> departmentList = this.list(eq);
        List<Tree> trees = dataAssembler.departmentTransFormTree(departmentList);
        if (CollectionUtils.isEmpty(trees)) {
            return trees;
        }

        for (Tree tree : trees) {
            if (this.getChildCount(tree.getId()) > 0) {
                tree.setIsParent(Boolean.TRUE);
                tree.setOpen(Boolean.FALSE);
            } else {
                tree.setIsParent(Boolean.FALSE);
                tree.setOpen(Boolean.FALSE);
            }

        }
        return trees;
    }

    @Override
    public int getChildCount(String departmentId) {
        ValidatedUtil.filterNullParam(departmentId);

        LambdaQueryWrapper<Partment> eq = new QueryWrapper<Partment>().lambda().eq(Partment::getParentId, departmentId);
        return this.count(eq);
    }

    @Override
    public List<Partment> findSubPartment(String parentId) {
        List<Partment> subPartment = partmentMapper.findSubPartment(parentId);
        subPartment.forEach(item -> {
            List<Partment> subPartment1 = partmentMapper.findSubPartment(item.getPartmentId());
            if (!CollectionUtils.isEmpty(subPartment1)) {
                item.setHasPartment(true);
            } else {
                item.setHasPartment(false);
            }
        });
        return subPartment;
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param:
     * @return:
     * @Description: 获取简单部门信息
     * @create: 10:43 2020/4/24
     */
    @Override
    public Partment getSimpleDeptInfo(String memberId, String orgId) {
        return partmentMapper.getSimpleDeptInfo(memberId, orgId);
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param:
     * @return:
     * @Description: 根据企业id查询下属部门
     * @create: 10:43 2020/4/24
     */
    @Override
    public List<Partment> findOrgParentByOrgId(String orgId) {
        return partmentMapper.findOrgParentByOrgId(orgId);
    }

    @Override
    public List<Tree> searchDeptByKeyWord(String keyWord, String orgId) {
        List<Partment> partments = partmentMapper.searchDeptByKeyWord(keyWord, orgId);
        List<Tree> trees = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(partments)) {
            for (Partment r : partments) {
                Tree tree = new Tree();
                tree.setId(r.getPartmentId());
                tree.setIcon(r.getPartmentLogo());
                tree.setName(r.getPartmentName());
                tree.setOrgId(orgId);
                if (this.getChildCount(tree.getId()) > 0) {
                    tree.setIsParent(Boolean.TRUE);
                    tree.setPId(r.getParentId());
                    tree.setOpen(Boolean.FALSE);
                } else {
                    tree.setIsParent(Boolean.FALSE);
                    tree.setOpen(Boolean.FALSE);
                }
                trees.add(tree);
            }
        }
        return trees;
    }

}