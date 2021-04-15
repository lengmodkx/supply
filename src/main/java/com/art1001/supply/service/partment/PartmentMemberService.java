package com.art1001.supply.service.partment;

import com.art1001.supply.entity.partment.PartmentMember;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author shaohua
 * @since 2019-04-29
 */
public interface PartmentMemberService extends IService<PartmentMember> {

    /**
     * 添加部门成员
     * @param partmentMember 成员信息
     * @return 是否成功
     */
    Boolean addPartmentMember(PartmentMember partmentMember);

    /**
     * 部门中是否已经存在了该成员
     * 存在返回true
     * @param partmentId 部门id
     * @param memberId 成员id
     * @return 结果
     */
    Boolean checkMemberIsExist(String partmentId, String memberId);

    /**
     * 获取一个部门某个成员的信息
     * @param memberId 成员id
     * @param partmentId 部门id
     * @return 部门成员信息
     */
//    PartmentMember getPartmentMemberInfo(String partmentId,String memberId);

    /**
     * 获取一个部门的全部成员信息
     * @param partmentId 部门id
     * @return 成员信息
     */
    List<PartmentMember> getMemberByPartmentId(String partmentId);

    /**
     * 获取部门的部门id，部门名称及是否负责人信息
     * @param partmentId

     * @return
     */
    String getSimplePartmentMemberInfo(String partmentId);

    /**
     * 根据企业id获取部门成员信息
     * @param orgId
     * @return
     */
    List<PartmentMember> getPartmentByOrgId(String orgId);

    List<PartmentMember> getMemberInfoByPartmentId(String partmentId);

    /**
     * 根据企业id获取部门成员id
     * @param orgId
     * @param memberId
     * @return
     */
    PartmentMember getpartmentMemberByOrgId(String orgId, String memberId);

    Integer addDeptMember(String partmentId, String orgId, List<String> memberId);


    void savePartmentMember(String partmentId, String memberId);

    void updatePartMentMaster(String partmentId, String memberId);

    Integer countPartMentMember( String orgId, List<String> memberId);


    void removePartmentMember(String orgId, List<String> memberId);
}
