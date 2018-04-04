package cn.com.sgcc.gdt.opc.client.utils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * TODO
 *
 * @author ck
 */
public class DateUtil {

    /**
     * 生成最近的时刻，即分钟是15的倍数
     * <pre> 如
     *  1. 当前9：22,则返回9：15
     *  2. 当前9：23，则返回9：30
     * </pre>
     * @return
     */
    public static Date getRecentMoment() {
        Calendar calendar = Calendar.getInstance();
        BigDecimal minute = BigDecimal.valueOf(calendar.get(Calendar.MINUTE));
        BigDecimal tag = BigDecimal.valueOf(15);
        BigDecimal resultMinute = minute.divide(tag, 0, BigDecimal.ROUND_HALF_UP).multiply(tag);
        calendar.set(Calendar.MINUTE, resultMinute.intValue());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当前时间
     * @return
     */
    public static String getCurrentTime(){
        return DateFmt.formateTime(Calendar.getInstance().getTime());
    }
}
