package com.art1001.supply.util;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.joda.time.DateTime;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
     * @return {@link Map}
     * map:
     * success: true false
     * result: 提示信息
     * filename: 文件名
     * originalFilename: 原始文件名
     */
    public static Map<String, Object> ossfileUpload(MultipartFile myFile, String imgDir, HttpServletRequest request, String type) throws IOException, NullPointerException {

        Map<String, Object> map = Maps.newHashMap();
        String originalFilename;
        map.put(success, false);
        // boolean errorFlag = true;
        // 获取内容类型
        String contentType = request.getContentType();
        // 得到上传文件的大小
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
     * 云服务器 上传文件-单图上传
     *
     * @param myFile 文件
     * @param imgDir 图片存储目录
     * @return {@link Map}
     * map:
     * success: true false
     * result: 提示信息
     * filename: 文件名
     */
    public static Map<String, Object> ossFileUpload(MultipartFile myFile, String imgDir, String fileName) throws IOException, NullPointerException, InterruptedException {
        Map<String, Object> map = Maps.newHashMap();

        Thread.sleep(100);

        // 文件路径
        fileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("."));
        String fileUrl = imgDir + fileName;
        AliyunOss.uploadInputStream(fileUrl, myFile.getInputStream());
        map.put("fileUrl", fileUrl);
        map.put("filename", fileName);
        return map;
    }

    /**
     * 获取临时下载文件目录
     */
    public static String getTempPath() {
        // 获取根路径
        String path = System.getProperty("user.dir");
        // 下载目录为/temp，可以如下获取：
        File upload = new File(path, "temp");
        if (!upload.exists()) upload.mkdirs();
        return upload.getAbsolutePath();
    }

    /**
     * 删除文件夹
     */
    public static void delFolder(String folderPath) {
        try {
            // 删除完里面所有内容
            boolean b = delAllFile(folderPath);

            File myFilePath = new File(folderPath);
            // 删除空文件夹
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除指定文件夹下的所有文件
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        if (!file.isDirectory()) {
            return false;
        }
        String[] tempList = file.list();
        File temp = null;
        assert tempList != null;
        for (String aTempList : tempList) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + aTempList);
            } else {
                temp = new File(path + File.separator + aTempList);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                // 先删除文件夹里面的文件
                delAllFile(path + "\\" + aTempList);
                // 再删除空文件夹
                delFolder(path + "\\" + aTempList);
                flag = true;
            }
        }
        return flag;
    }

    private static final int BUFFER_SIZE = 2 * 1024;

    /**
     * 压缩成ZIP 方法1
     *
     * @param srcDir           压缩文件夹路径
     * @param out              压缩文件输出流
     * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure)
            throws RuntimeException {

        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 压缩成ZIP 方法2
     *
     * @param srcFiles 需要压缩的文件列表
     * @param out      压缩文件输出流
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(List<File> srcFiles, OutputStream out) throws RuntimeException {
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[BUFFER_SIZE];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 递归压缩方法
     *
     * @param sourceFile       源文件
     * @param zos              zip输出流
     * @param name             压缩后的名称
     * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean KeepDirStructure) throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if (KeepDirStructure) {
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }

            } else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), KeepDirStructure);
                    }

                }
            }
        }
    }

    /**
     * 文件大小转换为字符串格式
     *
     * @param size 文件大小(单位B)
     */
    public static String convertFileSize(long size) {
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

    //删除文件夹和文件夹里面的文件
    public static void deleteDir(final String pPath) {
        File dir = new File(pPath);
        deleteDirWihtFile(dir);
    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    public static String readFileContent(InputStream stream) throws Exception{
        BufferedReader bufferReader = null;
        InputStreamReader inputStreamReader = null;
        try {
            java.io.File targetFile = new java.io.File("ff.json");
            org.apache.commons.io.FileUtils.copyInputStreamToFile(stream, targetFile);
            StringBuilder localStrBulider = new StringBuilder();
            inputStreamReader = new InputStreamReader(new FileInputStream(targetFile), "utf-8");
            bufferReader = new BufferedReader(inputStreamReader);
            String lineStr;
            while((lineStr = bufferReader.readLine()) != null) {
                localStrBulider.append(lineStr);
            }
            return localStrBulider.toString();

        }finally {
            if(bufferReader!=null){
                bufferReader.close();
            }
            if(inputStreamReader!=null){
                inputStreamReader.close();
            }
        }
    }






    public static void main(String[] args) {
        // 删除文件夹
        FileUtils.delFolder("D:\\project\\supply\\temp\\1530522000382");
    }


}
