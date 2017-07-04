package tk.mybatis.springboot.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Created by Yakami on 17/7/4.
 * Emm...
 */
public class TimeUtils {
    public static String timeToDateStr(long time) {
        Instant i = Instant.ofEpochSecond(time / 1000);
        ZonedDateTime z = ZonedDateTime.ofInstant( i, ZoneId.of("Asia/Shanghai"));
//        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        return String.format("%02d-%02d-%02d", z.getYear(), z.getMonthValue(), z.getDayOfMonth());
    }
}
