package com.art1001.supply.util;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.joda.time.DateTime;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FileUtils {

    static final String success = "success";
    static final String result = "result";
    static final String filename = "filename";

    // 最大文件大小
    private static long maxSize = 500000000;

    // 定义允许上传的文件扩展名
    private static final Map<String, String> extMap = new HashMap<String, String>();

    static {
        // 其中images,flash,medias,files,对应文件夹名称,对应dirName
        // key文件夹名称
        // value该文件夹内可以上传文件的后缀名
        extMap.put("images", "gif,GIF,jpg,JPG,jpeg,JPEG,png,PNG,bmp,BMP");
        extMap.put("flashs", "swf,SWF,flv,FLV");
        extMap.put("medias", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb,,mp4,SWF,FLV,MP3,WAV,WMA,WMV,MID,AVI,MPG,ASF,RM,RMVB,MP4");
        extMap.put("files", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2,DOC,DOCX,XLS,XLSX,PPT,HTM,HTML,TXT,ZIP,RAR,GZ,BZ2");
        extMap.put("sensitive", "txt,TXT");
    }

    /**
     * 云服务器 上传文件-单图上传
     *
     * @param myFile 文件
     * @param imgDir 图片存储目录
     * @param type   文件格式类型
     */
    public static Map<String, Object> ossfileUpload(MultipartFile myFile, String imgDir, HttpServletRequest request, String type) throws IOException, NullPointerException {

        Map<String, Object> map = Maps.newHashMap();
        String originalFilename;
        map.put(success, false);
        // boolean errorFlag = true;
        // 获取内容类型
        String contentType = request.getContentType();
        int contentLength = request.getContentLength();
        String fileExt = myFile.getOriginalFilename().substring(myFile.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        originalFilename = String.valueOf(new DateTime().getMillis()) + myFile.getOriginalFilename().substring(myFile.getOriginalFilename().indexOf("."));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e1) {
        }
        if (myFile.isEmpty()) {
            //上传图片为空
            map.put(result, "请选择文件后上传");
        } else if (!Arrays.asList(extMap.get(type).split(",")).contains(fileExt)) {
            // 检查扩展名
            map.put(result, "上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(type) + "格式。");
        } else if (contentType == null || !contentType.startsWith("multipart")) {
            log.error("请求不包含multipart/form-data流");
            map.put(result, "请求不包含multipart/form-data流");
        } else if (maxSize < contentLength) {
            log.error("上传文件大小超出文件最大大小");
            map.put(result, "上传文件大小超出文件最大大小[" + convertFileSize(maxSize) + "]");
        } else if (!ServletFileUpload.isMultipartContent(request)) {
            map.put(result, "请选择文件");
        } else {
            String ossPath = imgDir.substring(1);
            String path = ossPath + "/" + originalFilename;
            AliyunOss.uploadInputStream(path, myFile.getInputStream());
            map.put(result, imgDir);
            map.put(filename, originalFilename);
            map.put("originalFilename", myFile.getOriginalFilename());
            map.put(success, true);
        }
        return map;
    }

    /**
     * 文件大小转换为字符串格式
     *
     * @param size 文件大小(单位B)
     */
    private static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }
}
