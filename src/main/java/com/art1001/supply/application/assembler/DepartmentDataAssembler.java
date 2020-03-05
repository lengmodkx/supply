package com.art1001.supply.application.assembler;

import com.art1001.supply.entity.partment.Partment;
import com.art1001.supply.entity.tree.Tree;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author shaohua
 * @date 2020/2/28 11:28
 */
@Component
public class DepartmentDataAssembler {


    public List<Tree> departmentTransFormTree(List<Partment> partments){
        if(CollectionUtils.isEmpty(partments)){
            return new LinkedList<>();
        }

        List<Tree> treeList = new ArrayList<>();
        for (Partment partment : partments) {
            Tree tree = new Tree();
            tree.setId(partment.getPartmentId());
            tree.setName(partment.getPartmentName());
            tree.setOrgId(partment.getOrganizationId());

            treeList.add(tree);
        }
        return treeList;
    }
}
