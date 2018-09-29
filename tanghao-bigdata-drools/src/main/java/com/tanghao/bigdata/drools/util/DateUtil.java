package com.tanghao.bigdata.drools.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Date Utility Class used to convert Strings to Dates and Timestamps
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a> Modified by
 *         <a href="mailto:dan@getrolling.com">Dan Kibler </a> to correct time
 *         pattern. Minutes should be mm not MM (MM is month).
 */
public class DateUtil {

    private static Logger       log                       = LoggerFactory.getLogger(DateUtil.class);
    private static final String TIME_PATTERN              = "HH:mm";
    private static final String TIME_PATTERN_ALL          = "yyyy-MM-dd HH:mm:ss.S";
    private static final String TIME_YYYY_MM_DD_HH_MM_SS  = "yyyy-MM-dd HH:mm:ss";
    private static final String TIME_YYYY_MM_DD           = "yyyy-MM-dd";
    private static final String TIME_FORM_ALL             = "yyyyMMddHHmmss";
    private static final String TIME_YYYY_MMDD_HHMMSS_SSS = "yyyyMMddHHmmssSSS";
    private static final String TIME_FORM                 = "HHmmss";
    private static final String DATE_FORM                 = "yyyyMMdd";

    /**
     * Checkstyle rule: utility classes should not have public constructor
     */
    public DateUtil() {
    }

    /**
     *  Timestamp和String之间转换的函数：
      */
    public static String converTimestampToString(Timestamp obj) {
        return converTimestampToString(TIME_YYYY_MM_DD, obj);
    }

    /**
     * 自定义 转换模式将Timestamp 输出
     */
    public static String converTimestampToString(String formatPattern, Timestamp obj) {
        SimpleDateFormat df = new SimpleDateFormat(formatPattern);
        String str = df.format(obj);
        return str;
    }

    /**
     *  String转化为Timestamp:
     */
    public static Timestamp converStringToTimestamp(String str) {
        Timestamp ts = Timestamp.valueOf(str);
        return ts;
    }

    /**
     * Return default datePattern (MM/dd/yyyy)
     * 
     * @return a string representing the date pattern on the UI
     */
    private static String getDatePattern() {
        String defaultDatePattern = "MM/dd/yyyy";
        // try {
        // defaultDatePattern = ResourceBundle.getBundle(Constants.BUNDLE_KEY,
        // locale).getString(
        // "date.format");
        // } catch (MissingResourceException mse) {
        // defaultDatePattern = "MM/dd/yyyy";
        // }

        return defaultDatePattern;
    }

    public static String getDateTimePattern() {
        return DateUtil.getDatePattern() + " HH:mm:ss.S";
    }

    /**
     * This method attempts to convert an Oracle-formatted date in the form
     * dd-MMM-yyyy to mm/dd/yyyy.
     * 
     * @param aDate
     *            date from database as a string
     * @return formatted string for the ui
     */
    public static String getDate(Date aDate) {
        SimpleDateFormat df;
        String returnValue = "";

        if (aDate != null) {
            df = new SimpleDateFormat(getDatePattern());
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }

    /**
     * This method generates a string representation of a date/time in the
     * format you specify on input
     * 
     * @param aMask
     *            the date pattern the string is in
     * @param strDate
     *            a string representation of a date
     * @return a converted Date object
     * @see SimpleDateFormat
     * @throws ParseException
     *             when String doesn't match the expected format
     */
    public static Date convertStringToDate(String aMask, String strDate) {
        SimpleDateFormat df;
        Date date = null;
        df = new SimpleDateFormat(aMask);

        if (log.isDebugEnabled()) {
            log.debug("converting '" + strDate + "' to date with mask '" + aMask + "'");
        }
        try {
            date = df.parse(strDate);
        } catch (ParseException pe) {
            log.error("Could not convert '" + strDate + "' to a date, throwing exception");
            log.error("", pe);
        }

        return date;
    }

    /**
     * This method returns the current date time in the format: MM/dd/yyyy HH:MM
     * a
     *
     * @param theTime
     *            the current time
     * @return the current date/time
     */
    public static String getTimeNow(Date theTime) {
        return getDateTime(TIME_PATTERN, theTime);
    }

    /**
     * This method returns the current date in the format: MM/dd/yyyy
     *
     * @return the current date
     * @throws ParseException
     *             when String doesn't match the expected format
     */
    public static Calendar getCalendarToday() {
        Calendar cal = Calendar.getInstance();
        return cal;
    }

    /**
     * This method generates a string representation of a date's date/time in
     * the format you specify on input
     *
     * @param aMask
     *            the date pattern the string is in
     * @param aDate
     *            a date object
     * @return a formatted string representation of the date
     *
     * @see SimpleDateFormat
     */
    private static String getDateTime(String aMask, Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate == null) {
            log.info("aDate is null!");
        } else {
            df = new SimpleDateFormat(aMask);
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }

    /**
     * This method generates a string representation of a date based on the
     * System Property 'dateFormat' in the format you specify on input
     *
     * @param aDate
     *            A date to convert
     * @return a string representation of the date
     */
    public static String convertDateToString(Date aDate) {
        return getDateTime(getDatePattern(), aDate);
    }

    /**
     * This method converts a String to a date using the datePattern
     *
     * @param strDate
     *            the date to convert (in format MM/dd/yyyy)
     * @return a date object
     */
    public static Date convertStringToDate(String strDate) {
        return convertStringToDate(getDatePattern(), strDate);
    }

    /**
     *
     * @param aDate
     * @return string YYYY_MM_DD
     */
    public static String convertDateToString(String pattern, Date aDate) {
        return getDateTime(pattern, aDate);
    }

    /**
     * 取得从startDate开始的前(正)/后(负)day天
     *
     * @param startDate
     * @param day
     * @return date
     */
    public static Date getRelativeDate(Date startDate, int day) {
        long myTime = (startDate.getTime() / 1000) + day * 24 * 60 * 60;
        startDate.setTime(myTime * 1000);
        return startDate;
    }

    /**
     * 取得从startDate开始的前(正)/后(负)minute分
     *
     * @param startDate
     * @param minute
     * @return date
     */
    public static Date getRelativeMinute(Date startDate, int minute) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(startDate);
            calendar.add(Calendar.MINUTE, minute);
            return calendar.getTime();
        } catch (Exception e) {
            log.error("", e);
            return startDate;
        }
    }

    /**
     * 根据日期获取星期几
     *
     * @param date
     *            java.util.Date对象,不能为null
     * @return int
     */
    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 统计两个时间差，返回的是天数(即24小时算一天，少于24小时就为0，用这个的时候最好把小时、分钟等去掉)
     *
     * @param beginStr
     *            开始时间
     * @param endStr
     * @param format
     * @return day
     */
    public static int countDays(String beginStr, String endStr, String format) {
        Date end = convertStringToDate(endStr, format);
        Date begin = convertStringToDate(beginStr, format);
        long times = end.getTime() - begin.getTime();
        return (int) (times / 60 / 60 / 1000 / 24);
    }

    /**
     * 获取8位数字的今天日期格式
     * @return
     */
    public static String getTodayYYYYMMDD() {
        return convertDateToString(DATE_FORM, new Date());
    }

    /**
     * 计算当前日期到结束日期的天小时分钟
     *
     * @param endDate
     * @return string
     */
    public static String getCountDays(Date endDate) {
        Date now = new Date();
        String returnString = "";
        try {
            long l = endDate.getTime() - now.getTime();
            if (l < 0) {
                return "00天00小时00分";
            }
            long day = l / (24 * 60 * 60 * 1000);
            if (day != 0) {
                returnString = day + "天";
            }
            long hour = (l / (60 * 60 * 1000) - day * 24);
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            returnString = returnString + hour + "小时" + min + "分";
        } catch (Exception e) {
            return returnString;
        }
        return returnString;
    }

    /**
     * 判断当前时间是否大于传入时间加传入天数
     *
     * @param date
     *            传入日期
     * @param day
     *            增加天数
     * @return true or false
     */
    public static boolean compareDate(Date date, int day) {
        Date newDate = getRelativeDate(date, day);
        return new Date().after(newDate);
    }

    public static int countDays(Date begin, Date end) {
        if (begin == null || end == null) {
            return -9999;
        }
        long times = end.getTime() - begin.getTime();
        return (int) (times / 60 / 60 / 1000 / 24);
    }

    /**
     *
     * 功能：天数向上取整，velocity使用<br>
     *
     * @param in
     * @return int
     */
    public static int getWholeDay(String in) {
        Double double1 = Double.parseDouble(in);
        if (double1 < 0) {
            return 0;
        }
        return (int) Math.ceil(double1);
    }

    public static Timestamp getCurrentTimestamp() {
        Date today = new Date();
        return new Timestamp(today.getTime());
    }

    /**
     * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
     * @param nowdate yyyyMMdd
     * @param delay
     * @return
     */
    public static String getNextDay(String nowdate, int delay) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORM);
            Date d = format.parse(nowdate);
            return getNextDay(d, delay);
        } catch (ParseException e) {
            log.error("", e);
            return null;
        }
    }

    public static String getNextDay(Date nowdate, int delay) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORM);
        return format.format(getRelativeDate(nowdate, delay));
    }

    /**
     * 
     * 当期时间的前后几小时
     * 
     * @param hour
     * @return
     */
    public static String getTimeByHour(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + hour);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
        // return new SimpleDateFormat("HH:mm:ss").format(calendar.getTime());
    }

    /**
     * 根据字符串日期获取对应的星期
     * 
     * @param time
     * @return
     * @throws ParseException
     */
    public static String getWeek(String time) throws ParseException {
        final String[] dayNames = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
        SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        date = sdfInput.parse(time);
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek < 0) {
            dayOfWeek = 0;
        }
        return dayNames[dayOfWeek];
    }
}
