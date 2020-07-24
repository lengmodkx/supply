package com.art1001.supply.mapper.partment;

import com.art1001.supply.entity.partment.PartmentMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DindDangMao
 * @since 2019-04-29
 */
@Mapper
public interface PartmentMemberMapper extends BaseMapper<PartmentMember> {

    /**
     * 获取一个部门成员的信息
     * @param memberId 成员信息
     * @return 部门成员信息
     */
    PartmentMember selectPartmentMemberInfo(@Param("partmentId")String partmentId, @Param("memberId") String memberId);

    PartmentMember getSimplePartmentMemberInfo(@Param("partmentId")String partmentId,@Param("memberId") String memberId);

    /**
     * 根据企业id获取部门成员信息
     * @param orgId
     * @return
     */
    List<PartmentMember> getPartmentByOrgId(@Param("orgId") String orgId);

    PartmentMember getpartmentMemberByOrgId(@Param("orgId")String orgId,@Param("memberId") String memberId);

    void updatePartMentMaster(@Param("partmentId") String partmentId, @Param("memberId")String memberId);
}
