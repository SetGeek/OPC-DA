package cn.com.sgcc.gdt.opc.client.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期转换工具类
 *
 * @author Joy
 */
public class DateFmt {

    public static final String date_long = "yyyy-MM-dd HH:mm:ss";
    public static final String date_short = "yyyy-MM-dd";
    public static final String date_minute = "yyyyMMddHHmm";


    public static SimpleDateFormat sdfDay = new SimpleDateFormat(date_short);
    public static SimpleDateFormat sdfSecond = new SimpleDateFormat(date_long);

    public static String getCountDate(String date, String patton) {
        SimpleDateFormat sdf = new SimpleDateFormat(patton);
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            try {
                cal.setTime(sdf.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return sdf.format(cal.getTime());
    }

    /**
     * 格式化为：年-月-日 小时:分钟:秒
     * @param date
     * @return
     */
    public static String formateTime(Date date){
        return sdfSecond.format(date);
    }

    public static String getCountDate(String date, String patton, int step) {
        SimpleDateFormat sdf = new SimpleDateFormat(patton);
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            try {
                cal.setTime(sdf.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        cal.add(Calendar.DAY_OF_MONTH, step);
        return sdf.format(cal.getTime());
    }

    public static Date parseDate(String dateStr) throws Exception {
        try {
            return sdfDay.parse(dateStr);
        } catch (Exception e) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(dateStr));
            return calendar.getTime();
        }
    }

    public static void main(String[] args) throws Exception {

//		System.out.println(DateFmt.getCountDate("2014-03-01 12:13:14", DateFmt.date_short));
        System.out.println(parseDate("2014-05-02").after(parseDate("2014-05-01")));
    }

    /**
     * 将秒以下单位值为零
     * @param date
     * @return
     */
    public static Date resetSecond(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
