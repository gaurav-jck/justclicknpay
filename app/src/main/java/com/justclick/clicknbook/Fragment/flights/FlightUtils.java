package com.justclick.clicknbook.Fragment.flights;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FlightUtils {

    //TAGS
    public static final String SHARED_PREF_NAME = "flightsharedpref";
    public static final String KEY_LOGIN_TOKEN = "token";

    // date formatter
    public static String getFormatDate(String departureDate) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMM");
        Date date = null;
        try {
            date = originalFormat.parse(departureDate);
        } catch (ParseException ex) {
            return "";
        }
        catch (NullPointerException e){
            return "";
        }

        return targetFormat.format(date);
    }

}
