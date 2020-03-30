package com.art1001.supply.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.event.ProgressEvent;
import com.aliyun.oss.event.ProgressEventType;
import com.aliyun.oss.event.ProgressListener;
import com.aliyun.oss.model.*;
import com.art1001.supply.common.Constants;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class AliyunOss {

    // TODO 开发使用
    private static String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = "LTAIP4MyTAbONGJx";
    private static String accessKeySecret = "coCyCStZwTPbfu93a3Ax0WiVg3D4EW";
    private static String bucketName = "art1001-bim-5d";

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

    public static InputStream getInputStream(String key){
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            OSSObject object = ossClient.getObject(bucketName, key);
            return object.getObjectContent();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ossClient.shutdown();
        }
        return null;
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
            //ensureBucket(ossClient, bucketName);
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
            //ensureBucket(ossClient, bucketName);
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
            //ensureBucket(ossClient, bucketName);
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
            //ensureBucket(ossClient, bucketName);
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
    public static InputStream downloadInputStream(String path, HttpServletResponse response) {
        // 创建OSSClient实例
        HttpURLConnection urlcon = null;
        try {
            URL url =  new URL(Constants.OSS_URL + path);
            urlcon=(HttpURLConnection)url.openConnection();
            //根据响应获取文件大小
            response.setContentLengthLong(urlcon.getContentLengthLong());
            return urlcon.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载到本地
     *
     * @param objectName objectName
     * @param path 本地路径
     */
    public static void downloadLocal(String objectName, String path) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 下载OSS文件到本地文件。如果指定的本地文件存在会覆盖，不存在则新建。
            ossClient.getObject(new GetObjectRequest(bucketName, objectName), new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
    }

    public static void rangeDownload(String objectName) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, objectName);
            // 获取0~1000字节范围内的数据，包括0和1000，共1001个字节的数据。如果指定的范围无效（比如开始或结束位置的指定值为负数，或指定值大于文件大小），则下载整个文件。
            getObjectRequest.setRange(-1, -1);
            // 范围下载。
            ossClient.getObject(getObjectRequest, new File("D:/test"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
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

    public static void downloadzip(List<com.art1001.supply.entity.file.File> fileList, HttpServletResponse response, HttpServletRequest request) throws Exception{
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        String fileName = "chat_file.zip";
        File zipFile = File.createTempFile("chat_file", ".zip");
        FileOutputStream f = new FileOutputStream(zipFile);
        /**
         * 作用是为任何OutputStream产生校验和
         * 第一个参数是制定产生校验和的输出流，第二个参数是指定Checksum的类型 （Adler32（较快）和CRC32两种）
         */
        CheckedOutputStream csum = new CheckedOutputStream(f, new Adler32());
        // 用于将数据压缩成Zip文件格式
        ZipOutputStream zos = new ZipOutputStream(csum);
        fileList.forEach(file -> {
            OSSObject ossObject = ossClient.getObject(bucketName, file.getFileUrl());
            InputStream inputStream = ossObject.getObjectContent();
            try {
                zos.putNextEntry(new ZipEntry(file.getFileName()+file.getExt()));
                int bytesRead = 0;
                // 向压缩文件中输出数据
                while((bytesRead=inputStream.read())!=-1){
                    zos.write(bytesRead);
                }
                inputStream.close();
                zos.closeEntry(); // 当前文件写完，定位为写入下一条项目
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        zos.close();
        String header = request.getHeader("User-Agent").toUpperCase();
        if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE")) {
            fileName = URLEncoder.encode(fileName, "utf-8");
            fileName = fileName.replace("+", "%20");    //IE下载文件名空格变+号问题
        } else {
            fileName = new String(fileName.getBytes(), "ISO8859-1");
        }

        response.reset();
        response.setContentType("text/plain");
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Location", fileName);
        response.setHeader("Cache-Control", "max-age=0");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        FileInputStream fis = new FileInputStream(zipFile);
        BufferedInputStream buff = new BufferedInputStream(fis);
        BufferedOutputStream out=new BufferedOutputStream(response.getOutputStream());
        IOUtils.copy(buff,out);
        // 关闭流
        fis.close();
        buff.close();
        out.close();
        ossClient.shutdown();
        // 删除临时文件
        zipFile.delete();
    }

    public static String getRequestBody(InputStream is, int contentLen) {
        if (contentLen > 0) {
            int readLen = 0;
            int readLengthThisTime = 0;
            byte[] message = new byte[contentLen];
            try {
                while (readLen != contentLen) {
                    readLengthThisTime = is.read(message, readLen, contentLen - readLen);
                    if (readLengthThisTime == -1) {// Should not happen.
                        break;
                    }
                    readLen += readLengthThisTime;
                }
                return new String(message);
            } catch (IOException e) {
            }
        }
        return "";
    }


    public static  boolean verifyOSSCallbackRequest(HttpServletRequest request, String ossCallbackBody)
            throws NumberFormatException, IOException {
        String atomizationInput = request.getHeader("Authorization");
        String pubKeyInput = request.getHeader("x-oss-pub-key-url");
        byte[] authorization = BinaryUtil.fromBase64String(atomizationInput);
        byte[] pubKey = BinaryUtil.fromBase64String(pubKeyInput);
        String pubKeyAddr = new String(pubKey);
        System.out.println(pubKeyAddr);
        if (!pubKeyAddr.startsWith("http://gosspublic.alicdn.com/")&&!pubKeyAddr.startsWith("https://gosspublic.alicdn.com/")) {
            System.out.println("pub key addr must be oss addrss");
            return false;
        }
        String retString = executeGet(pubKeyAddr);
        retString = retString.replace("-----BEGIN PUBLIC KEY-----", "");
        retString = retString.replace("-----END PUBLIC KEY-----", "");
        String queryString = request.getQueryString();
        String uri = request.getRequestURI();
        String authStr = java.net.URLDecoder.decode(uri, "UTF-8").replaceFirst("/","/api");
        if (StringUtils.isNotEmpty(queryString)) {
            authStr += "?" + queryString;
        }
        authStr += "\n" + ossCallbackBody;
        return doCheck(authStr, authorization, retString);
    }

    private static boolean doCheck(String content, byte[] sign, String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = BinaryUtil.fromBase64String(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            java.security.Signature signature = java.security.Signature.getInstance("MD5withRSA");
            signature.initVerify(pubKey);
            signature.update(content.getBytes());
            return  signature.verify(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    private static String executeGet(String url) {
        try {
            OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder().url(url).build();
            Call call = client.newCall(request);
            Response response = call.execute();
            return  response.body().string();
        }catch (IOException e){
            e.printStackTrace();
        }
        return "";
    }

    public static void response(HttpServletRequest request, HttpServletResponse response, String results, int status) throws IOException {
        String callbackFunName = request.getParameter("callback");
        response.addHeader("Content-Length", String.valueOf(results.length()));
        if (callbackFunName == null || callbackFunName.equalsIgnoreCase(""))
            response.getWriter().println(results);
        else
            response.getWriter().println(callbackFunName + "( " + results + " )");
        response.setStatus(status);
        response.flushBuffer();
    }

    public static void batchDownLoad(List<com.art1001.supply.entity.file.File> files,HttpServletResponse response, HttpServletRequest request)throws  Exception{
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        String fileName = "chat_file.zip";
        File zipFile = File.createTempFile("chat_file", ".zip");
        FileOutputStream f = new FileOutputStream(zipFile);
        /**
         * 作用是为任何OutputStream产生校验和
         * 第一个参数是制定产生校验和的输出流，第二个参数是指定Checksum的类型 （Adler32（较快）和CRC32两种）
         */
        CheckedOutputStream csum = new CheckedOutputStream(f, new Adler32());
        // 用于将数据压缩成Zip文件格式
        ZipOutputStream zos = new ZipOutputStream(csum);
        files.forEach(file -> {
            OSSObject ossObject = ossClient.getObject(bucketName, file.getFileUrl());
            InputStream inputStream = ossObject.getObjectContent();
            try {
                zos.putNextEntry(new ZipEntry(file.getFileName()+file.getExt()));
                int bytesRead = 0;
                // 向压缩文件中输出数据
                while((bytesRead=inputStream.read())!=-1){
                    zos.write(bytesRead);
                }
                inputStream.close();
                zos.closeEntry(); // 当前文件写完，定位为写入下一条项目
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        zos.close();
        String header = request.getHeader("User-Agent").toUpperCase();
        if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE")) {
            fileName = URLEncoder.encode(fileName, "utf-8");
            fileName = fileName.replace("+", "%20");    //IE下载文件名空格变+号问题
        } else {
            fileName = new String(fileName.getBytes(), "ISO8859-1");
        }

        response.reset();
        response.setContentType("text/plain");
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Location", fileName);
        response.setHeader("Cache-Control", "max-age=0");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        FileInputStream fis = new FileInputStream(zipFile);
        BufferedInputStream buff = new BufferedInputStream(fis);
        BufferedOutputStream out=new BufferedOutputStream(response.getOutputStream());
        byte[] car=new byte[1024];
        int l=0;
        while (l < zipFile.length()) {
            int j = buff.read(car, 0, 1024);
            l += j;
            out.write(car, 0, j);
        }
        // 关闭流
        fis.close();
        buff.close();
        out.close();
        ossClient.shutdown();
        // 删除临时文件
        zipFile.delete();
    }

    //压缩文件夹中的文件
    public static void doZip(com.art1001.supply.entity.file.File inFile, ZipOutputStream out, String dir)  {
        String entryName;
        if (!"".equals(dir)) {
            entryName = dir + "/" + inFile.getFileName() + inFile.getExt();
        } else {
            entryName = inFile.getFileName() + inFile.getExt();
        }
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            ZipEntry entry = new ZipEntry(entryName);
            out.putNextEntry(entry);
            OSSObject ossObject = ossClient.getObject(bucketName, inFile.getFileUrl());
            InputStream in = ossObject.getObjectContent();
            IOUtils.copy(in,out);
            in.close();
            ossObject.close();
            out.closeEntry();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            ossClient.shutdown();
        }
    }

}
