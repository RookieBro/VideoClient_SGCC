package com.telecomyt.videolibrary.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2017/2/18.
 */

public class TimeUtils {

    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<>();
    private static String defaultFormat = "yyyy-MM-dd HH:mm:ss";

    public static String formatTime(long time, String format) {
        return new SimpleDateFormat(format, Locale.CHINA).format(time);
    }

    public static String formatTime(long time) {
        return formatTime(time, defaultFormat);
    }

    public static String formatNowTime(String format) {
        return new SimpleDateFormat(format, Locale.CHINA).format(System.currentTimeMillis());
    }

    public static String formatNowTime() {
        return formatNowTime(defaultFormat);
    }

    public static boolean isToday(String day) {
        try {
            Calendar pre = Calendar.getInstance();
            Date predate = new Date(System.currentTimeMillis());
            pre.setTime(predate);
            Calendar cal = Calendar.getInstance();
            Date date = getDateFormat().parse(day);
            cal.setTime(date);
            if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
                int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);
                if (diffDay == 0) {
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isToday(long time) {
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time));
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);
            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean isYesterday(String day) {
        try {
            Calendar pre = Calendar.getInstance();
            Date predate = new Date(System.currentTimeMillis());
            pre.setTime(predate);

            Calendar cal = Calendar.getInstance();
            Date date = getDateFormat().parse(day);
            cal.setTime(date);
            if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
                int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);
                if (diffDay == -1) {
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isYesterday(long time) {
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time));
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);
            if (diffDay == -1) {
                return true;
            }
        }
        return false;
    }

    private static SimpleDateFormat getDateFormat() {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat(defaultFormat, Locale.CHINA));
        }
        return DateLocal.get();
    }

    public static String getDayString(long time) {
        return getDayString(time, "HH:mm");
    }

    public static String getDayString(long time, String format) {
        boolean isToday = TimeUtils.isToday(time);
        boolean isYesterday = TimeUtils.isYesterday(time);
        return isToday ? TimeUtils.formatTime(time, format) :
                isYesterday ? "昨天" + TimeUtils.formatTime(time, format) :
                        TimeUtils.formatTime(time, defaultFormat);
    }

    public static long formatTime(String time) {
        return formatTime(time, defaultFormat);
    }

    public static long formatTime(String time, String format) {
        long timeInMillis = 0;
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(new SimpleDateFormat(format, Locale.CHINA).parse(time));
            timeInMillis = c.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMillis;
    }

    public static boolean isBeforeNow(String time, String format) {
        return isBeforeNow(time, format, 0);
    }

    /**
     *
     * @param time
     * @param format
     * @param offset  时间偏移量  毫秒  例如1000*60*5位5分钟 则比现在提前5分钟的为true
     *                例：正数：传入时间是现在时间的五分钟后  也是 true
     * @return
     */
    public static boolean isBeforeNow(String time, String format, long offset) {
        long timeInMillis = 0;
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(new SimpleDateFormat(format, Locale.CHINA).parse(time));
            timeInMillis = c.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMillis - System.currentTimeMillis() <= offset;
    }

    public static boolean isBeforeNow(long time) {
        return time - System.currentTimeMillis() < 0;
    }

    public static boolean isBeforeNow(String time) {
        return isBeforeNow(time, defaultFormat);
    }

    public static boolean isBeforeNow(String time, long offset) {
        return isBeforeNow(time, defaultFormat, offset);
    }
}
