package com.art1001.supply.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author heshaohua
 * @Title: FileExt
 * @Description: TODO
 * @date 2018/8/27 14:02
 **/
public class FileExt {

    /**
     * 所能接受的文件类型
     */
    public static final Map<String, List<String>> extMap = new HashMap<String, List<String>>();

    static {
        // 其中images,flash,medias,files,对应文件夹名称,对应dirName
        // key文件夹名称
        // value该文件夹内可以上传文件的后缀名
        extMap.put("images", Arrays.asList(".gif,.GIF,.jpg,.JPG,.jpeg,.JPEG,.png,.PNG,.bmp,.BMP".split(",")));
        extMap.put("flashs", Arrays.asList("swf,SWF,flv,FLV".split(",")));
        extMap.put("medias", Arrays.asList(".swf,.flv,.mp3,.wav,.wma,.wmv,.mid,.avi,.mpg,.asf,.rm,.rmvb,.mp4,.SWF,.FLV,.MP3,.WAV,.WMA,.WMV,.MID,.AVI,.MPG,.ASF,.RM,.RMVB,.MP4".split(",")));
        extMap.put("files", Arrays.asList(".pdf,.doc,.docx,.xls,.xlsx,.ppt,.htm,.html,.txt,.zip,.rar,.gz,.bz2,.DOC,.DOCX,.XLS,.XLSX,.PPT,.HTM,.HTML,.TXT,.ZIP,.RAR,.GZ,.BZ2".split(",")));
        extMap.put("sensitive", Arrays.asList(".txt,.TXT".split(",")));
        extMap.put("model",Arrays.asList(".pln,.skp,.dwg,.dxf,.dae,.gsm,.tpl,.3ds,.ifc,.obj".split(",")));
    }

}
