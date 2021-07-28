package com.phoosop.reactive.util;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class DateTimeUtils {

    public static final String DATETIME_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT_YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";

    public static Optional<Date> parse(final String source, final String pattern) {
        if (StringUtils.isEmpty(source)) {
            return Optional.empty();
        }
        try {
            return Optional.of(new SimpleDateFormat(pattern).parse(source));
        } catch (ParseException e) {
            return Optional.empty();
        }
    }

    public static String convertDateToString(Date date, String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

}
