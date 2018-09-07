package com.suzei.racoon.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class TimeUtil {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static String formatToYesterday(Date date) {
        DateTime dateTime = new DateTime(date);
        DateTime today = new DateTime();
        DateTime yesterday = today.minusDays(1);
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("hh:mma");

        if (dateTime.toLocalDate().equals(today.toLocalDate())) {
            return "Today " + timeFormatter.print(dateTime);
        } else if (dateTime.toLocalDate().equals(yesterday.toLocalDate())) {
            return "Yesterday " + timeFormatter.print(dateTime);
        } else {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM dd', 'YYYY' at 'HH:mm");
            return formatter.print(dateTime);
        }
    }
}
