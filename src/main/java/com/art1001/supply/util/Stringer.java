package com.art1001.supply.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author heshaohua
 * @Title: Stringer
 * @Description: TODO
 * @date 2018/11/19 11:32
 **/
public class Stringer {
    private static Logger logs = LoggerFactory.getLogger(Stringer.class);

    public static final String SPECIAL_CHAR = "[\\\\`~!@#$%^&*+=|{}',:;\"[url=file://\\[\\].<]\\[\\].<>/[/url]?～！＠＃￥％……＆×（）——＋｜｛｝【】［］‘；：＂。，、．＜＞／？]";

    /**
     * @param string
     * @return
     * @author by K2 Aug 21, 2015
     * @desc 判断某字符串是否为空，如果为空，则返回一个空串.
     */
    public static String nullToEmpty(String string) {
        return isNullOrEmpty(string) ? "" : string;
    }

    /**
     * @param string
     * @return
     * @author by K2 Aug 21, 2015
     * @desc 判断某字符串是否为空，如果为空，则返回一个null空对象.
     */
    public static String emptyToNull(String string) {
        return isNullOrEmpty(string) ? null : string;
    }

    /**
     * @param obj
     * @return
     * @author by K2 Aug 21, 2015
     * @desc 判断某对象(String, Collection, Map, obj)是否为空.
     */
    public static boolean isNullOrEmpty(Object obj) {

        boolean result = true;
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            result = (obj.toString().trim().length() == 0) || obj.toString().trim().equals("null");
        } else if (obj instanceof Collection) {
            result = ((Collection<?>) obj).size() == 0;
        } else if (obj instanceof Map) {
            result = ((Map<?, ?>) obj).isEmpty();
        } else {
            result = ((obj == null) || (obj.toString().trim().length() < 1)) ? true : false;
        }
        return result;
    }

    /**
     * 判断某个对象是否为非空
     * @param obj 判断的对象
     * @return 结果
     */
    public static boolean isNotNullOrEmpty(Object obj){
        return !Stringer.isNullOrEmpty(obj);
    }

    /**
     * @param in
     * @return 输出二进制数据
     * @throws IOException
     * @author by K2 Aug 21, 2015
     * @desc 处理读取InputStream数据流.
     */
    public static byte[] readInStream(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len = 0;
        byte[] buffer = new byte[1024];
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        out.close();
        in.close();
        return out.toByteArray();
    }

    /**
     * @param is
     * @return
     * @throws IOException
     * @author by K2 Aug 21, 2015
     * @desc InputStream数据流转换成字符串.
     */
    public static String toInStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }

    /**
     * @param sInputString
     * @return
     * @author by K2 Aug 21, 2015
     * @desc 将字符串转成InputStream流.
     */
    public static InputStream toInStream(String sInputString) {
        ByteArrayInputStream tInputStringStream = null;
        if (sInputString != null && !sInputString.trim().equals("")) {
            tInputStringStream = new ByteArrayInputStream(sInputString.getBytes());
        }
        return tInputStringStream;
    }

    /**
     * @param string
     * @return
     * @author by K2 Aug 18, 2015
     * @desc 小数点数据字符串转成保留两位小数的float数据类型.
     */
    public static float toFloat(String string) {
        float f = Float.parseFloat(string);
        BigDecimal b = new BigDecimal(f);
        float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return f1;
    }

    /**
     * @param f
     * @return
     * @author by K2 Aug 18, 2015
     * @desc 浮点型数据转换成两位小数点的字符串.
     */
    public static String toFloat(float f) {
        DecimalFormat decimalFormat = new DecimalFormat(".00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p = decimalFormat.format(f);// format 返回的是字符串
        return p;
    }

    /**
     * @param string
     * @return
     * @author by K2 Aug 19, 2015
     * @desc 双精度数据转换成两位小数点的字符串.
     */
    public static Double toDouble(String string) {
        float f = Float.parseFloat(string);
        BigDecimal b = new BigDecimal(f);
        Double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    /**
     * @param dd
     * @return
     * @author by K2 Aug 19, 2015
     * @desc 双精度数据转换成两位小数点的字符串.
     */
    public static String toDouble(Double dd) {
        return String.format("%.2f", dd);
    }

    /**
     * @param srcString 原字符串
     * @param regEx     替换的正则表达式，如果为空则使用默认的正则表达式，变量=SPECIAL_CHAR
     * @return 如果有返回true
     * @author by K2 Aug 21, 2015
     * @desc 检查指定字符串中是否包含特殊字符
     */
    public static boolean existSpecialChar(String srcString, String regEx) {
        if (isNullOrEmpty(regEx)) {
            regEx = SPECIAL_CHAR;
        }
        Matcher m = Pattern.compile(regEx).matcher(srcString);
        return m.find();
    }

    /**
     * @Title: nullToEmptyString @Description: TODO @param @param
     * o @param @return @return String @auther yaoyinchu @throws
     */
    public static String nullOrEmptyToString(Object o) {
        if (Stringer.isNullOrEmpty(o)) {
            return "";
        } else {
            return o.toString();
        }
    }

    public static String getObjectValue(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}
