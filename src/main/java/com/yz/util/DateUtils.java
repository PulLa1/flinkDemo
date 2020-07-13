/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.yz.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 *
 * @author ThinkGem
 * @version 2014-4-15
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM",
            "yyyy-MM-dd'T'HH:mm:ss'.'SS'Z'","yyyy-MM-dd'T'HH:mm:ss'Z'","yyyy-MM-dd'T'HH:mm:ss'.'SSSZZ"};



    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String getDate(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    public static String getMonthBegin() {
        return getYear() + "-" + getMonth() + "-01";
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        }
        return formatDate;
    }

    /**
     * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前时间字符串 格式（HH:mm:ss）
     */
    public static String getTime() {
        return formatDate(new Date(), "HH:mm:ss");
    }

    /**
     * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String getDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前年份季度符串
     */
    public static Integer getQuarter() {
        Calendar cal = Calendar.getInstance();
        int m = cal.get(Calendar.MONTH) + 1;
        Integer quarter = 1;
        if (m >= 1 && m == 3) {
            quarter = 1;
        }
        if (m >= 4 && m <= 6) {
            quarter = 2;
        }
        if (m >= 7 && m <= 9) {
            quarter = 3;
        }
        if (m >= 10 && m <= 12) {
            quarter = 4;
        }
        return quarter;
    }

    /**
     * 得到当前年份字符串 格式（yyyy）
     */
    public static String getYear() {
        return formatDate(new Date(), "yyyy");
    }

    /**
     * 得到当前月份字符串 格式（MM）
     */
    public static String getMonth() {
        return formatDate(new Date(), "MM");
    }

    /**
     * 得到当天字符串 格式（dd）
     */
    public static String getDay() {
        return formatDate(new Date(), "dd");
    }

    /**
     * 得到当前星期字符串 格式（E）星期几
     */
    public static String getWeek() {
        return formatDate(new Date(), "E");
    }

    /**
     * * 根据日期 找到对应日期的 星期
     *     
     */
    public static String getDayOfWeekByDate(String date) {
        String dayOfweek = "-1";
        try {
            SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Date myDate = myFormatter.parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat("E");
            String str = formatter.format(myDate);
            dayOfweek = str;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return dayOfweek;
    }

    /**
     * 日期型字符串转化为日期 格式
     * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
     * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
     * "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(),parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 转换为时间（天,时:分:秒.毫秒）
     *
     * @param timeMillis
     * @return
     */
    public static String formatDateTime(long timeMillis) {
        long day = timeMillis / (24 * 60 * 60 * 1000);
        long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
        long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
        return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
    }

    /**
     * 获取两个日期之间的天数
     *
     * @param staDd
     * @param endDd
     * @return
     */
    public static Integer getDistanceOfTwoDate(String staDd, String endDd ) {
        //设置转换的日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = sdf.parse(staDd);
            Date endDate  = sdf.parse(endDd );
            //得到相差的天数 betweenDate
            long betweenDate = (endDate.getTime() - startDate.getTime())/(60*60*24*1000);
            return (int)betweenDate;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 得到几天前/后的日期
     *
     * @param d
     * @param day
     * @return
     */
    public static String getDateAfter(String d, int day) {
        String ppttern = "yyyy-MM-dd";
        Calendar now = Calendar.getInstance();
        now.setTime(parseDate(d));
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);//+后 -前
        return formatDate(now.getTime(), ppttern);
    }

    /**
     * 得到几天前/后的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static String getDateTimeAfter(String d, int day) {
        String ppttern = "yyyy-MM-dd HH:mm";
        Calendar now = Calendar.getInstance();
        now.setTime(parseDate(d));
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);//+后 -前
        return formatDate(now.getTime(), ppttern);
    }

    /**
     * 获取某一时间 下一 天/周/月/年的时间
     *
     * @param d
     * @param rate
     * @return
     */
    public static String getNextTime(String d, String rate) {
        String ppttern = "yyyy-MM-dd HH:mm";
        Calendar now = Calendar.getInstance();
        now.setTime(parseDate(d));
        if ("天".equals(rate)) {
            now.set(Calendar.DATE, now.get(Calendar.DATE) + 1);//+后 -前
        } else if ("周".equals(rate)) {
            now.set(Calendar.DATE, now.get(Calendar.DATE) + 7);//+后 -前
        } else if ("月".equals(rate)) {
            now.add(Calendar.MONTH, 1);
        } else if ("年".equals(rate)) {
            now.add(Calendar.YEAR, 1);
        }
        return formatDate(now.getTime(), ppttern);
    }

    // 获取本周开始时间
    public static String getBeginDayOfWeek(Date day) {
        String ppttern = "yyyy-MM-dd";
        return formatDate(_getBeginDayOfWeek(day), ppttern);
    }

    private static Date _getBeginDayOfWeek(Date day) {
        Date date = new Date();
        if (day != null) {
            date = day;
        }
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return cal.getTime();
    }

    // 获取本周结束时间
    public static String getEndDayOfWeek(Date day) {
        String ppttern = "yyyy-MM-dd";
        Calendar cal = Calendar.getInstance();
        cal.setTime(_getBeginDayOfWeek(day));
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return formatDate(weekEndSta, ppttern);
    }

    // 判断当前日期属于当年第几周, 传入日期格式
    public static int getWeekOfYear(Date day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(day);

        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    // 判断当前日期属于当年第几周, yyyy-MM-dd
    public static int getWeekOfYear(String day) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(day);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);

        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 将年与月日转为  时间格式的字符串
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String formatDate(int year, int month, int day){
        String strDate = year+"-"+month+"-"+day;
        Date date = parseDate(strDate);
        return formatDate(date, "yyyy-MM-dd");
    }

    /**
     * 判断时间是否在时间段内
     *
     * @param strNowTime
     * @param strBeginTime
     * @param strEndTime
     * @return
     */
    public static boolean belongCalendar(String strNowTime, String strBeginTime, String strEndTime) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        Date now = null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(strNowTime);
            beginTime = df.parse(strBeginTime);
            endTime = df.parse(strEndTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Calendar date = Calendar.getInstance();
        date.setTime(now);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * @param args
     * @throws ParseException
     */
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 1);    //得到下一个月
        c.set(Calendar.DATE, c.get(Calendar.DATE) + 7);
        String start = format.format(c.getTime()) + " 00:00:00";
        System.out.println(getNextTime("2018-11-30 13:44", "天"));
    }
}
