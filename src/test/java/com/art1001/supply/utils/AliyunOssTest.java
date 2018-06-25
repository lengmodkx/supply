package com.art1001.supply.utils;

import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.art1001.supply.ApplicationTests;
import com.art1001.supply.util.AliyunOss;
import org.junit.Test;

import java.util.List;

public class AliyunOssTest extends ApplicationTests {

    @Test
    public void moveFileTest() {
        AliyunOss.moveFile("source/bj.png", "destination/");
    }

    @Test
    public void copyFileTest() {
        AliyunOss.copyFile("source/bj.png", "destination/bj.png");
    }

    public static void main(String[] args) {
        // 目标文件路径
        String destinationFolderName ="destination/";
        ObjectListing listing = AliyunOss.fileList("source/");
        assert listing != null;
        // 得到所有的文件夹，
        for (String commonPrefix : listing.getCommonPrefixes()) {
            String destinationObjectName = destinationFolderName + commonPrefix;
            AliyunOss.createFolder(destinationObjectName);
            // 
        }
        // 得到所有的文件
        for (OSSObjectSummary ossObjectSummary : listing.getObjectSummaries()) {
            String destinationObjectName = destinationFolderName + ossObjectSummary.getKey();
            AliyunOss.moveFile(ossObjectSummary.getKey(), destinationObjectName);
        }
    }
}
