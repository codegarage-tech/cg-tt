package tech.codegarage.tidetwist.util;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class DateManager {

    public static final String TAG = DateManager.class.getSimpleName();

    //Date time format
    public static final String yyyy_MM_dd_hh_mm_ss = "yyyy-MM-dd hh:mm:ss";
    public static final String dd_MM_yyyy_hh_mm_ss = "dd-MM-yyyy hh:mm:ss";
    public static final String yyyy_MM_dd_hh_mm = "yyyy-MM-dd hh:mm";

    public static String convertDateTime(String date, String originalFormat, String outputFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(originalFormat, Locale.US);
        try {
            Date mDate = formatter.parse(date);
            SimpleDateFormat dateFormat = new SimpleDateFormat(outputFormat, new Locale("US"));
            return dateFormat.format(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatCurrentDateTime(String format) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static final CharSequence getFormattedTimestamp(long timestamp) {
        return DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

    public static long convertDateTimeToMillisecond(String dateTime, String format) {
//        String givenDateString = "Tue Apr 23 16:08:28 GMT+05:30 2013";
//        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        long timeInMilliseconds = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date mDate = simpleDateFormat.parse(dateTime);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            timeInMilliseconds = 0;
        }
        Log.d(TAG, TAG + ">> " + "DateTimeToMillisecond: " + timeInMilliseconds);

        return timeInMilliseconds;
    }
}