package com.art1001.supply.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AliyunOss {

    // TODO 开发使用
    private static String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = "LTAIqk28Y76sRtVQ";
    private static String accessKeySecret = "95Gjw4otcqgADeixghuOcDU2oqTLrU";
    private static String bucketName = "faydan";

    // TODO 正式使用
//    private static String endpoint = "https://oss-cn-beijing.aliyuncs.com";
//    private static String accessKeyId = "LTAIP4MyTAbONGJx";
//    private static String accessKeySecret = "coCyCStZwTPbfu93a3Ax0WiVg3D4EW";
//    private static String bucketName = "art1001-bim-5d";

    // Object是OSS存储数据的基本单元，称为OSS的对象，也被称为OSS的文件。详细描述请参看“开发人员指南 > 基本概念 > OSS基本概念介绍”。
    // Object命名规范如下：使用UTF-8编码，长度必须在1-1023字节之间，不能以“/”或者“\”字符开头。
    private static String firstKey = "my-first-key";


    /**
     * 创建Bucket, 如果不存在创建
     */
    private static void ensureBucket(OSSClient ossClient, String bucketName) {
        if (ossClient.doesBucketExist(bucketName)) {
            log.error("您已经创建Bucket：" + bucketName + "。");
        } else {
            log.info("您的Bucket不存在，创建Bucket：" + bucketName + "。");
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
            // 设置bucket权限
            createBucketRequest.setCannedACL(CannedAccessControlList.PublicReadWrite);
            ossClient.createBucket(createBucketRequest);
        }

        // 查看Bucket信息。
        BucketInfo info = ossClient.getBucketInfo(bucketName);
        log.info("Bucket " + bucketName + "的信息如下：");
        log.info("\t数据中心：" + info.getBucket().getLocation());
        log.info("\t创建时间：" + info.getBucket().getCreationDate());
        log.info("\t用户标志：" + info.getBucket().getOwner());
    }

    public static void deleteBucket(OSSClient ossClient, String bucketName) {
        if (ossClient.doesBucketExist(bucketName)) {
            ossClient.deleteBucket(bucketName);
        } else {
            log.warn("您的Bucket不存在，创建Bucket：" + bucketName + "。");
        }
    }

    /**
     * 上传文件流
     *
     * @param key         文件的键
     * @param inputStream 文件流
     */
    public static void uploadInputStream(String key, InputStream inputStream) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 创建Bucket
            ensureBucket(ossClient, bucketName);
            // 上传文件流
            ossClient.putObject(bucketName, key, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 创建文件夹
     * 根据项目名称创建文件夹
     *
     * @param folderName 项目名称 + 时间戳生成的文件夹名称 例如：BIM5D-1528784486494
     */
    public static void createFolder(String folderName) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 创建Bucket
            ensureBucket(ossClient, bucketName);
            ossClient.putObject(bucketName, folderName, new ByteArrayInputStream(new byte[0]));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 获取当前文件夹下的文件夹和文件
     * @param folderName 目录的全路径
     * @return 文件列表
     */
    public static List<String> fileList(String folderName) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        List<String> fileList = new ArrayList<>();
        try {
            // 构造ListObjectsRequest请求。
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
            // "/" 为文件夹的分隔符。
            listObjectsRequest.setDelimiter("/");
            // 列出fun目录下的所有文件和文件夹。
            listObjectsRequest.setPrefix(folderName);
            ObjectListing listing = ossClient.listObjects(listObjectsRequest);

            if (listing.getCommonPrefixes().size() > 0) {
                //CommonPrefixs列表中给出的是fun目录下的所有子文件夹。fun/movie/001.avi 和 fun/movie/007.avi 两个文件并没有被列出来，因为它们属于fun文件夹下的movie目录。
                for (String commonPrefix : listing.getCommonPrefixes()) {
                    // 去掉前缀
                    fileList.add(commonPrefix.replace(folderName, ""));
                }
            }

            listing.getObjectSummaries().remove(0);
            if (listing.getObjectSummaries().size() > 0) {
                //ObjectSummaries 的列表中给出的是fun目录下的文件。
                for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
                    // 去掉前缀
                    fileList.add(objectSummary.getKey().replace(folderName, ""));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }

        return fileList;
    }

    public static void main(String[] args) {
        List<String> fileList = fileList("第一个项目-1528941522377/视频/");
        fileList.forEach(System.out::println);
    }
}
