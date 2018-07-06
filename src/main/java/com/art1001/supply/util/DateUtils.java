package com.art1001.supply.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author LH
 * @date 2014-3-13
 *
 */

@Slf4j
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	public static final String DEFAULT_FORMAT = "yyyyMMddHHmmssSSS";
	public static final String DEFAULT_FORMAT_STRING = "yyyyMMddHHmmss";
	public static final String DEFAULT_FORMAT_YYYYMMDD = "yyyyMMdd";
	public static final String DEFAULT_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
	public static final String DEFAULT_YEAR_MON_DAY = "yyyy-MM-dd HH:mm:ss";
	public static final String DEFAULT_YEAR_MON_DAY2 = "yyyy/MM/dd HH:mm:ss";
	private static String[] parsePatterns = {
			"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
			"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
			"yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};


	/**
	 * 获取32位的UUID 编码
	 *
	 * @return String
	 *
	 */
	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		String newuuid = String.valueOf(uuid).replace("-", "");
		return newuuid;
	}
	/**
	 * 获取过去的分钟
	 * @param date
	 * @return
	 */
	public static long pastMinutes(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(60*1000);
	}
	/**
	 * 获取当前时间
	 *
	 * @return Timestamp
	 *
	 */
	@Deprecated
	public static Timestamp getTimestamp() {
		return new Timestamp(System.currentTimeMillis());
	}

	/**
	 * Date转string 获取时间yyyyMMddHHmmss 获取当前时间
	 *
	 * @return String
	 *
	 */
	@Deprecated
	public static String getDateString() {
		return DateTime.now().toString(DEFAULT_FORMAT_STRING);
	}

	/**
	 * Date转string 获取时间yyyyMMddHHmmss 获取当前时间
	 *
	 * @return String
	 *
	 */
	@Deprecated
	public static String getDateStringFFF() {
		return DateTime.now().toString(DEFAULT_FORMAT);
	}

	/**
	 * String 日期转DATE
	 *
	 * @return DATE
	 *
	 */
	@Deprecated
	public static Date parse(String strDate) throws Exception {
		DateTimeFormatter format = DateTimeFormat
				.forPattern(DEFAULT_FORMAT_YYYY_MM_DD);
		return DateTime.parse(strDate, format).toDate();
	}

	/**
	 * 获取当前时间 new Date()
	 *
	 * @return String date
	 */
	@Deprecated
	public static String getDate() {
		SimpleDateFormat df = new SimpleDateFormat(DEFAULT_FORMAT_YYYYMMDD);
		String date = df.format(new Date());
		return date;
	}

	/**
	 * 获取当前时间 new Date() yyyy-MM-dd
	 *
	 * @return String date
	 */
	@Deprecated
	public static String getDate24() {
		SimpleDateFormat df = new SimpleDateFormat(DEFAULT_FORMAT_YYYY_MM_DD);
		String date = df.format(new Date());
		return date;
	}

	/**
	 * 获取月最后一天
	 *
	 * @return
	 */
	@Deprecated
	public static String lastDayOfMonth(String str) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date date = df.parse(str);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.roll(Calendar.DAY_OF_MONTH, -1);
			String monthDay = new SimpleDateFormat("dd").format(cal.getTime());
			return monthDay;
		} catch (ParseException e) {
			log.error("获取月最后一天异常！");
		}
		return "30";
	}

	/**
	 * 判断字符串是否为空
	 *
	 * @param param
	 *            (param != null && param.split(",").length > 1 ) ? true : false
	 * @return boolean
	 */
	public static boolean paramLength(String param) {
		return (param != null && param.split(",").length > 1) ? true : false;
	}

	/**
	 *
	 * @param strDate
	 *            参数日期 20140404
	 * @param t
	 *            日期的加减算法
	 * @return String
	 */
	public static String getNextDay_YYYYMMDD(String strDate, int t) {

		SimpleDateFormat format = new SimpleDateFormat(DEFAULT_FORMAT_YYYYMMDD);
		Date newDate = null;
		try {
			Date date = format.parse(strDate);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.get(Calendar.DAY_OF_MONTH) + t);// 让日期加1
			newDate = calendar.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return format.format(newDate);
	}

	/**
	 * 字符串去重 例如（1,1,1,2,2,2,2,3,4,5,5,6,6,7,8,8,8,） return 1,2,3,4,5,6,7,8,
	 * return String
	 */
	public static String quchong(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		} else {
			String[] s = str.split(",");
			String string = new String();
			for (int i = 0; i < s.length; i++) {
				if (!string.contains(String.valueOf(s[i]))) {
					string += String.valueOf(s[i]) + ",";
				}
			}
			string = string.substring(0, string.length() - 1);
			return string;
		}
	}

	/**
	 * 根据日期 获取 月份
	 *
	 * @Title: getMonth
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param date （20141111）
	 * @param @param t 数字 正负
	 * @param @return
	 * @param @throws Exception 设定文件
	 * @return String 返回类型
	 * @throws Exception
	 */
	@Deprecated
	public static String getMonth(String date, int t) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			Date dt = sdf.parse(date);
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(dt);
			rightNow.add(Calendar.MONTH, -1);// 日期的计算
			Date dt1 = rightNow.getTime();
			String reStr = sdf.format(dt1);
			return reStr;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public enum queryStr {
		// 利用构造函数传参
		RED(1), GREEN(3), YELLOW(2);

		// 定义私有变量
		private int code;

		// 构造函数，枚举类型只能为私有
		private queryStr(int _code) {
			this.code = _code;
		}

		@Override
		public String toString() {
			return String.valueOf(this.code);
		}
	}

	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * 获取当前日期字符串
	 *
	 * @return
	 */
	public static String getDateStr() {
		return getDateStr(DEFAULT_FORMAT);
	}

	/**
	 * 取得当前时间字符串
	 *
	 * @return
	 */
	public static String getDateStr(String pattern) {
		return DateTime.now().toString(pattern);
	}

	/**
	 * 格式化日期
	 *
	 * @param date
	 *            日期实例
	 * @param pattern
	 *            格式
	 * @return
	 */
	public static String getDateStr(Date date, String pattern) {
		return DateFormatUtils.format(date, pattern);
	}

	/**
	 * 格式化日期
	 *
	 * @param date
	 *            日期实例
	 * @return
	 */
	public static String getDateStr(Date date) {
		return DateFormatUtils.format(date, DEFAULT_FORMAT);
	}

	/**
	 * 获取当前日期的年月
	 *
	 * @return
	 */
	public static String getDateYYYYMM() {
		return getDateStr("yyyyMM");
	}

	/**
	 * 日期字符串转换成Date
	 *
	 * @param dateStr
	 *            日期字符串
	 * @param pattern
	 *            日期格式
	 * @return
	 * @throws Exception
	 */
	public static Date parse(String dateStr, String pattern) {
		DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
		return DateTime.parse(dateStr, format).toDate();
	}

	/**
	 * 取得下一天
	 *
	 * @param dateStr
	 *            日期字符串
	 * @param sourcePattern
	 *            传入的日期格式
	 * @param resultPattern
	 *            返回之后的日期格式
	 * @return
	 */
	public static String getNextDay(String dateStr, String sourcePattern,
									String resultPattern) {
		return getAfterDay(dateStr, 1, sourcePattern, resultPattern);
	}

	/**
	 * 取得下一天
	 *
	 * @param dateStr
	 *            日期字符串
	 * @return
	 */
	public static String getNextDay(String dateStr) {
		return getAfterDay(dateStr, 1, DEFAULT_FORMAT, DEFAULT_FORMAT);
	}

	/**
	 * 取得下一天
	 *
	 * @param dateStr
	 *            日期字符串
	 * @param days
	 *            天数
	 * @param sourcePattern
	 *            传入的日期格式
	 * @param resultPattern
	 *            返回之后的日期格式
	 * @return
	 */
	public static String getAfterDay(String dateStr, int days,
									 String sourcePattern, String resultPattern) {
		DateTimeFormatter format = DateTimeFormat.forPattern(sourcePattern);
		DateTime dateTime = DateTime.parse(dateStr, format);
		return dateTime.plusDays(days).toString(resultPattern);
	}

	/**
	 * 取得前一天
	 *
	 * @param dateStr
	 *            日期字符串
	 * @return
	 */
	public static String getBeforeDay(String dateStr) {
		return getBeforeDays(dateStr, 1, DEFAULT_FORMAT, DEFAULT_FORMAT);
	}

	/**
	 * 取得前一天
	 *
	 * @param dateStr
	 *            日期字符串
	 * @param days
	 *            天数
	 * @return
	 */
	public static String getBeforeDays(String dateStr, int days) {
		return getBeforeDays(dateStr, days, DEFAULT_FORMAT, DEFAULT_FORMAT);
	}

	/**
	 * 取得前一天
	 *
	 * @param dateStr
	 *            日期字符串
	 * @param days
	 *            天数
	 * @param sourcePattern
	 *            传入的日期格式
	 * @param resultPattern
	 *            返回之后的日期格式
	 * @return
	 */
	public static String getBeforeDays(String dateStr, int days,
									   String sourcePattern, String resultPattern) {
		return getAfterDay(dateStr, -days, sourcePattern, resultPattern);
	}

	/**
	 *
	 * @param dateStr
	 *            日期字符串
	 * @param sourcePattern
	 *            传入的日期格式
	 * @param resultPattern
	 *            返回之后的日期格式
	 * @return
	 */
	public static String lastDayOfMonth(String dateStr, String sourcePattern,
										String resultPattern) {
		DateTimeFormatter format = DateTimeFormat.forPattern(sourcePattern);
		DateTime dateTime = DateTime.parse(dateStr, format);
		return dateTime.dayOfMonth().withMaximumValue().toString(resultPattern);
	}

	public static String firstDayOfMonth(String dateStr) {
		DateTimeFormatter format = DateTimeFormat.forPattern(DEFAULT_FORMAT);
		DateTime dateTime = DateTime.parse(dateStr, format);
		return dateTime.dayOfMonth().withMinimumValue()
				.toString(DEFAULT_FORMAT);
	}

	/**
	 * 获取上个月的第一天(不加时分秒)
	 * @return
	 */
	public static String firstDayOfLastMonth(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(calendar.getTime());
	}

	/**
	 * 获取上个月的最后一天(不加时分秒)
	 * @return
	 */
	public static String lastDayOfLastMonth(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.DATE, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(calendar.getTime());
	}

	/**
	 * 获取当前月第一天
	 * @param pattern 时间格式,传""默认为"yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public static String firstDayOfCurrentMonth(String pattern){
		if(pattern==""){
			pattern = DEFAULT_YEAR_MON_DAY;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(calendar.getTime());
	}

	/**
	 * 获取当前月最后一天
	 * @param pattern 时间格式,传""默认为"yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public static String lastDayOfCurrentMonth(String pattern){
		if(pattern==""){
			pattern = DEFAULT_YEAR_MON_DAY;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(calendar.getTime());
	}

	/**
	 * 获得指定月
	 *
	 * @param dateStr
	 *            日期字符串
	 * @param sourcePattern
	 *            传入的日期格式
	 * @param resultPattern
	 *            返回之后的日期格式
	 * @param month
	 *            　往前或往后几个月
	 * @return
	 */
	public static String getMonth(String dateStr, String sourcePattern,
								  String resultPattern, int month) {
		DateTimeFormatter format = DateTimeFormat.forPattern(sourcePattern);
		DateTime dateTime = DateTime.parse(dateStr, format);
		return dateTime.plusMonths(month).toString(resultPattern);
	}

	/**
	 * 获取当前时间的timestamp
	 * @return
	 */
	public static Timestamp getNowTimesTamp(){
		return new Timestamp(System.currentTimeMillis());
	}

	/**
	 * 获取指定时间的timestamp
	 * @param time
	 * @return
	 */
	public static Timestamp getTimestampByLong(long time){
		return new Timestamp(time);
	}

	/**
	 * 将一个字符串转换成日期格式, 字符串类型必须于格式化对应
	 * 例如：2015-09-01对应yyyy-MM-dd
	 * 例如：2015-09-01 00:00:00对应yyyy-MM-dd HH:mm:ss
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static Date toDate(String date, String pattern) {
		if((""+date).equals("")){
			return null;
		}
		if(pattern == null){
			pattern = DEFAULT_YEAR_MON_DAY;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date newDate = new Date();
		try {
			newDate = sdf.parse(date);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return newDate;
	}

	/**
	 * 字符串转为long型 必须带时、分、秒
	 * 例如：2015-09-01对应yyyy-MM-dd
	 * 例如：2015-09-01 00:00:00对应yyyy-MM-dd HH:mm:ss
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static long strToLong(String dateStr, String pattern){
		Date date = toDate(dateStr, pattern);
		return date.getTime();
	}

	/**
	 * 字符串转为long型
	 * @param dateStr  必须带时、分、秒
	 * @return
	 */
	public static long strToLong(String dateStr){
		Date date = toDate(dateStr, DEFAULT_YEAR_MON_DAY);
		return date.getTime();
	}


	/**
	 * 获取增加月数以后的日期
	 * */
	public static String getDateAddMonths(int months) {
		try {
			Calendar date = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					DEFAULT_YEAR_MON_DAY);
			date.add(Calendar.MONTH, months);
			return dateFormat.format(date.getTime());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取增加天数以后的日期
	 * */
	public static String getDateAddDays(int days) {
		try {
			Calendar date = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					DEFAULT_YEAR_MON_DAY);
			date.add(Calendar.DAY_OF_MONTH, days);
			return dateFormat.format(date.getTime());
		} catch (Exception e) {
			return null;
		}
	}



	/**
	 *  获取一个星期之前的时间戳
	 *  @param weeknum
	 *  传入几个星期
	 * @return
	 */
	public static Long getweektime(Integer weeknum){
		Calendar curr = Calendar.getInstance();
		curr.set(Calendar.WEEK_OF_MONTH,curr.get(Calendar.WEEK_OF_MONTH)-weeknum);
		Date date=curr.getTime();
		return date.getTime();
	}
	/**获取一个月之前的时间戳
	 * @param monthnum
	 *  传入几个月
	 * @return
	 */
	public static Long getmonthtime(Integer monthnum){
		Calendar curr = Calendar.getInstance();
		curr.set(Calendar.MONTH,curr.get(Calendar.MONTH)-monthnum);
		Date date=curr.getTime();
		return date.getTime();
	}

	/**获取一年之前的时间戳
	 * @param yearnum
	 *  传入几年
	 * @return
	 */
	public static Long getyeartime(Integer yearnum){
		Calendar curr = Calendar.getInstance();
		curr.set(Calendar.YEAR,curr.get(Calendar.YEAR)-yearnum);
		Date date=curr.getTime();
		return date.getTime();
	}

	/**
	 * 转换为默认的时间格式
	 * @param times
	 * @return
	 */
	public static Long getlongtimes(String times){
		String viewtime =times;
		Date time = new Date();
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			time = sdf.parse(viewtime);
		} catch (ParseException e) {
			e.printStackTrace();
			try {
				time = 	sdf2.parse(viewtime);
			} catch (ParseException e1) {
				e.printStackTrace();
			}
		}
		viewtime = sdf2.format(time);
		System.out.println(DateUtils.strToLong(viewtime));
		return DateUtils.strToLong(viewtime);
	}

	/**
	 * 获取当天开始时间
	 * @return
	 */
	public static Long getStartTime(){
		Calendar todayStart = Calendar.getInstance();
		todayStart.set(Calendar.HOUR, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		return todayStart.getTime().getTime();
	}

	/**
	 * 获取当天结束时间
	 * @return
	 */
	public static Long getEndTime(){
		Calendar todayEnd = Calendar.getInstance();
		todayEnd.set(Calendar.HOUR, 23);
		todayEnd.set(Calendar.MINUTE, 59);
		todayEnd.set(Calendar.SECOND, 59);
		todayEnd.set(Calendar.MILLISECOND, 999);
		return todayEnd.getTime().getTime();
	}

	/**
	 * 获取当天星期数,星期天为0
	 * @return
	 */
	public static int getWeek(){
		Calendar cal=Calendar.getInstance();
		int week=cal.get(Calendar.DAY_OF_WEEK)-1;
		return week;
	}

	/**
	 * 获取当天日期为几号
	 * @return
	 */
	public static int getDayOfMonth(){
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取上周一时间(不加时分秒)
	 * @return
	 */
	public static String getLastMonday(){
		Calendar cal =Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat(DEFAULT_FORMAT_YYYY_MM_DD);
		cal.add(Calendar.WEEK_OF_MONTH, -1);
		cal.set(Calendar.DAY_OF_WEEK, 2);
		return df.format(cal.getTime());
	}

	/**
	 * 获取上周天时间(不加时分秒)
	 * @return
	 */
	public static String getLastSunday(){
		Calendar cal =Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat(DEFAULT_FORMAT_YYYY_MM_DD);
		cal.set(Calendar.DAY_OF_WEEK, 1);
		return df.format(cal.getTime());
	}

	/**
	 * 日期型字符串转化为日期 格式
	 * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
	 *   "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
	 *   "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
	 */
	public static Date parseDate(Object str) {
		if (str == null){
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取当天时间 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getnowDate(){
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date=sdf2.format(new Date());
		return date;
	}

	/**
	 * timestamp转换long
	 * @param time
	 * @return
	 */
	public static Long getLongToTimestamp(Timestamp time){
		Long longTime = time.getTime();
		return longTime;
	}

	public static void main(String[] args) throws Exception {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		// String str = "201411";
		// Date dt = sdf.parse(str);
		// Calendar rightNow = Calendar.getInstance();
		// rightNow.setTime(dt);
		// // rightNow.add(Calendar.YEAR,-1);//日期减1年
		// rightNow.add(Calendar.MONTH, -1);// 日期加3个月
		// // rightNow.add(Calendar.DAY_OF_YEAR,10);//日期加10天
		// Date dt1 = rightNow.getTime();
		// String reStr = sdf.format(dt1);
		// System.out.println(queryStr.RED);
		//
		// System.out.println(DataUtil.quchong("1111,22,1,2,11,1"));
		// System.out.println(getNextDay_YYYYMMDD("20140409"));
		//getRandomString(8);

		//getMonth("2015-11-01 00:00:00","yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss",6);
//		System.out.println(strToLong("2015-09-14", DEFAULT_FORMAT_YYYY_MM_DD));
//		System.out.println(System.currentTimeMillis());
//		System.out.println("2015-09-14 00:00:00".length());
//		System.out.println(formatLongToStr(System.currentTimeMillis(), null));
//		Long aa = Long.parseLong("1442232291269");
//		Date date = new Date(aa);
//		System.out.println("date"+new Date());
//
//		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		System.out.println("dated"+DateUtils.getDateStr("yyyyMMddHHmmssSSS")+NumberUtils.getRandomNumber());
//		System.out.println(new Timestamp(aa));
//		System.out.println(System.currentTimeMillis());
		//System.out.println(formatLongToStr(System.currentTimeMillis(),""));
		//System.out.println("时间："+strToLong((getMonth(formatLongToStr(System.currentTimeMillis(),"") ,"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss",6))));
		//gettimes("2015/11/26 6:23:45");
		//System.out.println("month"+getmonthtime(6));
//		System.out.println(getTimestampByLong(1448967358546l));
//		System.out.println(getTimestampByLong(getweektime(1)));
//		System.out.println(getweektime(1));
//		System.out.println(getmonthtime(1));

		Date date  = new Date();
//		System.out.println(getDateAddDays(date,7,""));


		System.out.println(firstDayOfCurrentMonth("yyyy-MM-dd"));
	}

	/**
	 * 时间差计算
	 */
	public static Map<String, Double> timeDifference(Long starTime, Long endTime) {
		//时间差的毫秒数
		Long date3 = endTime - starTime;
		//计算出相差天数
		Double allDays =Math.ceil(date3 / (24 * 3600 * 1000));
		Double days =Math.floor(date3 / (24 * 3600 * 1000));
		//计算出小时数
		Long leave1 = date3 % (24 * 3600 * 1000);
		//计算天数后剩余的毫秒数
		Double hours = Math.floor(leave1 / (3600 * 1000));
		//计算相差分钟数
		Long leave2 = leave1 % (3600 * 1000);
		//计算小时数后剩余的毫秒数
		Double minutes = Math.floor(leave2 / (60 * 1000));
		//计算相差秒数
		Long leave3 = leave2 % (60 * 1000);
		//计算分钟数后剩余的毫秒数
		Double seconds = Math.floor(leave3 / 1000);

		Map<String, Double> map = new HashMap<>();
		map.put("allDays", allDays);
		map.put("days", days);
		map.put("hours", hours);
		map.put("minutes", minutes);
		map.put("seconds", seconds);

		return map;
	}
}
