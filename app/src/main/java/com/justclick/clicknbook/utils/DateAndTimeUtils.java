package com.justclick.clicknbook.utils;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateAndTimeUtils {

    public static SimpleDateFormat getFlightDateTimeFormat2(){
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
    }

    public static SimpleDateFormat getFlightDateTimeFormat(){
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US);
    }
    public static SimpleDateFormat getHotelDateTimeFormat(){
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }

    public static String getSegmentDuration(int duration){
        return duration/60+"h "+duration%60+"m";
    }

    public static String getDateTimeFlight(String time){
        Date date;
        try {
            date = getFlightDateTimeFormat().parse(time);
        } catch (ParseException e) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.US).format(date);
    }

    public static String getSegmentDepArrTime(String time){
        Date date;
        try {
            date = getFlightDateTimeFormat().parse(time);
        } catch (ParseException e) {
            return "";
        }
        return new SimpleDateFormat("HH:mm", Locale.US).format(date);
    }

    public static String getDDMMMYYDate(String time){
        Date date;
        try {
            date = getFlightDateTimeFormat().parse(time);
        } catch (ParseException e) {
            return "";
        }
        return new SimpleDateFormat("dd-MMM-yy", Locale.US).format(date);
    }

    public static String getDayDDMMMYYDate(String time){
        Date date;
        try {
            date = getFlightDateTimeFormat().parse(time);
        } catch (ParseException e) {
            return "";
        }
        return new SimpleDateFormat("EEE, dd MMM", Locale.US).format(date);
    }
    public static String getDayDDMMMYYDateFlightSuccess(String time){
        Date date;
        try {
            date = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US).parse(time);
        } catch (ParseException e) {
            return "";
        }
        return new SimpleDateFormat("EEE, dd MMM, HH:mm", Locale.US).format(date);
    }

    public static String getSegmentDepArrDate(String time){
        Date date;
        try {
            date = getFlightDateTimeFormat().parse(time);
        } catch (ParseException e) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date);
    }

    public static String getDurationBetweenTwoDates(String endDateString, String startDateString){
        Date endDate, startDate;
        try {
            endDate = getFlightDateTimeFormat().parse(endDateString);
            startDate=getFlightDateTimeFormat().parse(startDateString);
        } catch (ParseException e) {
            return "";
        }
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
//        long hoursInMilli = minutesInMilli * 60;
//        long daysInMilli = hoursInMilli * 24;

       /* long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;
*/
        long elapsedMinutes = different / minutesInMilli;
//        different = different % minutesInMilli;

//        long elapsedSeconds = different / secondsInMilli;
        return getSegmentDuration((int) elapsedMinutes);
    }

    public static int getDaysBetweenTwoDates(String endDateString, String startDateString){
        Date endDate, startDate;
        try {
            endDate = getHotelDateTimeFormat().parse(endDateString);
            startDate=getHotelDateTimeFormat().parse(startDateString);
        } catch (ParseException e) {
            return 0;
        }
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;
        long elapsedMinutes = different / daysInMilli;
//        different = different % minutesInMilli;

//        long elapsedSeconds = different / secondsInMilli;
        return Integer.parseInt(String.valueOf(elapsedDays));
    }

    public static int getDurationInMinutesBetweenTwoDates(String endDateString, String startDateString){
        Date endDate, startDate;
        try {
            endDate = getFlightDateTimeFormat().parse(endDateString);
            startDate=getFlightDateTimeFormat().parse(startDateString);
        } catch (ParseException e) {
            return 0;
        }
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
//        long hoursInMilli = minutesInMilli * 60;
//        long daysInMilli = hoursInMilli * 24;

       /* long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;
*/
        long elapsedMinutes = different / minutesInMilli;
//        different = different % minutesInMilli;

//        long elapsedSeconds = different / secondsInMilli;
        return (int) elapsedMinutes;
    }

    public static int getHourFromDate(String dateString){
        Date date;
        try {
            date = getFlightDateTimeFormat().parse(dateString);
        } catch (ParseException e) {
            return 0;
        }

       /* Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR);*/
        return date.getHours();
    }
    public static String getDayMonth(String endDateString){
        Date endDate;
        try {
            endDate = getHotelDateTimeFormat().parse(endDateString);
        } catch (ParseException e) {
            return "";
        }
        String day= (String) DateFormat.format("dd MMM",   endDate);
        return day;
    } public static String getDayMonthYear(String endDateString){
        Date endDate;
        try {
            endDate = getHotelDateTimeFormat().parse(endDateString);
        } catch (ParseException e) {
            return "";
        }
        String day= (String) DateFormat.format("dd MMM yy",   endDate);
        return day;
    }
    public static String getDayOfWeek(String endDateString){
        Date endDate;
        try {
            endDate = getHotelDateTimeFormat().parse(endDateString);
        } catch (ParseException e) {
            return "";
        }
        String day= (String) DateFormat.format("EEE",   endDate);
        return day;
    }
    public static String getDay(String endDateString){
        Date endDate;
        try {
            endDate = getHotelDateTimeFormat().parse(endDateString);
        } catch (ParseException e) {
            return "";
        }
        String day= (String) DateFormat.format("dd",   endDate);
        return day;
    }
    public static String getYear(String endDateString){
        Date endDate;
        try {
            endDate = getHotelDateTimeFormat().parse(endDateString);
        } catch (ParseException e) {
            return "";
        }
        String day= (String) DateFormat.format("yyyy",   endDate);
        return day;
    }
    public static String getHotelMonth(String endDateString){
        Date endDate;
        try {
            endDate = getHotelDateTimeFormat().parse(endDateString);
        } catch (ParseException e) {
            return "";
        }
        String day= (String) DateFormat.format("MMM",   endDate);
        return day;
    }
    public static String getHotelYear(String endDateString){
        Date endDate;
        try {
            endDate = getHotelDateTimeFormat().parse(endDateString);
        } catch (ParseException e) {
            return "";
        }
        String day= (String) DateFormat.format("yyyy",   endDate);
        return day;
    }

    public static SimpleDateFormat getDayFormat(){
        return new SimpleDateFormat("dd", Locale.US);
    }
    public static SimpleDateFormat getDateMonthFormat(){
        return new SimpleDateFormat("MMM", Locale.US);
    }
    public static SimpleDateFormat getYearFormat(){
        return new SimpleDateFormat("yy", Locale.US);
    }
    public static String getDay(Date time) {
        return getDayFormat().format(time.getTime());
    }
    public static String getYear(Date time) {
        return getYearFormat().format(time.getTime());
    }
    public static String getMonth(Date time) {
        return getDateMonthFormat().format(time.getTime());
    }
}
