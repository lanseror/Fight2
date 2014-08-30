package com.fight2.util;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("00");

    public static String formatRemainTime(final int seconds) {
        final StringBuilder sb = new StringBuilder(15);
        int testSeconds = seconds;
        final long days = TimeUnit.SECONDS.toDays(testSeconds);
        if (days > 0) {
            testSeconds -= TimeUnit.DAYS.toSeconds(days);
            sb.append(days).append("天 ");
        }
        final long hours = TimeUnit.SECONDS.toHours(testSeconds);
        testSeconds -= TimeUnit.HOURS.toSeconds(hours);
        sb.append(DECIMAL_FORMAT.format(hours)).append(":");
        final long minutes = TimeUnit.SECONDS.toMinutes(testSeconds);
        sb.append(DECIMAL_FORMAT.format(minutes)).append(":");
        testSeconds -= TimeUnit.MINUTES.toSeconds(minutes);
        sb.append(DECIMAL_FORMAT.format(testSeconds));

        return sb.toString();
    }

    public static int getRemainTimeInSecond(final Date endDate) {
        final long millis = endDate.getTime() - System.currentTimeMillis();
        final int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(millis);
        return seconds;
    }
}
