package com.android.scansantri.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    public static String longToDateString(long dateInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return sdf.format(new Date(dateInMillis));
    }

    public static long dateStringToLong(String dateString) {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            Date date = sdf.parse(dateString);
            if (date != null) {
                return date.getTime();
            } else {
                return  0L;
            }
        }catch (Exception e){
            return 0L;
        }
    }
}
