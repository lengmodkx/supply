package com.art1001.supply.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.event.ProgressEvent;
import com.aliyun.oss.event.ProgressEventType;
import com.aliyun.oss.event.ProgressListener;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

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

    /**
     * 删除bucket
     */
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
     * 上传字符串
     *
     * @param key     前缀全路径
     * @param content 字符串内容
     */
    public static void uploadString(String key, String content) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 创建Bucket
            ensureBucket(ossClient, bucketName);
            // 上传文件流
            ossClient.putObject(bucketName, key, new ByteArrayInputStream(content.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 上传byte数组
     *
     * @param objectName 文件名全路径
     * @param bytes      byte数组
     */
    public static void uploadByte(String objectName, byte[] bytes) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 创建Bucket
            ensureBucket(ossClient, bucketName);
            // 上传文件流
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
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
     * 返回要下载的文件流
     *
     * @param path 文件的完整路径
     * @return InputStream
     */
    public static InputStream downloadInputStream(String path) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            return new URL(path).openStream();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
        return null;
    }

    /**
     * 列举指定目录下的所有文件和文件夹
     * @param folder 目录
     */
    public static ObjectListing fileList(String folder) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 构造ListObjectsRequest请求。
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
            // 设置正斜线（/）为文件夹的分隔符。
//            listObjectsRequest.setDelimiter("/");
            // 列出fun目录下的所有文件和文件夹。
            listObjectsRequest.setPrefix(folder);
            return ossClient.listObjects(listObjectsRequest);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
        return null;
    }

    /**
     * 删除单个文件
     *
     * @param key 文件路径名
     */
    public static void deleteFile(String key) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 下载object到文件
            ossClient.deleteObject(bucketName, key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 移动文件
     * 原理：先复制，再删除
     *
     * @param sourceObjectName 源对象名称
     * @param destinationObjectName 目标对象名称
     */
    public static void moveFile(String sourceObjectName, String destinationObjectName) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 拷贝文件。
            CopyObjectResult result = ossClient.copyObject(bucketName, sourceObjectName, bucketName, destinationObjectName);
            System.out.println("ETag: " + result.getETag() + " LastModified: " + result.getLastModified());
            deleteFile(sourceObjectName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 复制文件
     *
     * @param sourceObjectName 源对象名称
     * @param destinationObjectName 目标对象名称
     */
    public static void copyFile(String sourceObjectName, String destinationObjectName) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 拷贝文件。
            CopyObjectResult result = ossClient.copyObject(bucketName, sourceObjectName, bucketName, destinationObjectName);
            System.out.println("ETag: " + result.getETag() + " LastModified: " + result.getLastModified());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 下载进度条
     */
    static class GetObjectProgressListener implements ProgressListener {
        private long bytesRead = 0;
        private long totalBytes = -1;
        private boolean succeed = false;

        @Override
        public void progressChanged(ProgressEvent progressEvent) {
            long bytes = progressEvent.getBytes();
            ProgressEventType eventType = progressEvent.getEventType();
            switch (eventType) {
                case TRANSFER_STARTED_EVENT:
                    log.info("开始下载......");
                    break;
                case RESPONSE_CONTENT_LENGTH_EVENT:
                    this.totalBytes = bytes;
                    log.info(this.totalBytes + " 字节总数将被下载到本地文件");
                    break;
                case RESPONSE_BYTE_TRANSFER_EVENT:
                    this.bytesRead += bytes;
                    if (this.totalBytes != -1) {
                        int percent = (int) (this.bytesRead * 100.0 / this.totalBytes);
                        log.info(bytes + " 此时字节已被读取，下载进度: " + percent + "%(" + this.bytesRead + "/" + this.totalBytes + ")");
                    } else {
                        log.info(bytes + " 此时字节已被读取，下载比例：未知" + "(" + this.bytesRead + "/...)");
                    }
                    break;
                case TRANSFER_COMPLETED_EVENT:
                    this.succeed = true;
                    log.info("成功下载，" + this.bytesRead + " 字节总数已被传输。");
                    break;
                case TRANSFER_FAILED_EVENT:
                    log.error("下载失败， " + this.bytesRead + " 字节已被传输");
                    break;
                default:
                    break;
            }
        }

        public boolean isSucceed() {
            return succeed;
        }
    }

    public static void main(String[] args) {

        String objectName = "第一个项目-1529401007925/测试.xlsx";

        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 带进度条的下载。
            ossClient.getObject(
                    new GetObjectRequest(bucketName, objectName).withProgressListener(new GetObjectProgressListener()),
                    new File("D:\\image\\test.jpg")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 关闭Client。
        ossClient.shutdown();
    }
}
