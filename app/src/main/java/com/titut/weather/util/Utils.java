package com.titut.weather.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.TextView;

import com.titut.weather.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 429023 on 8/19/2016.
 */
public class Utils {
    public static void setWeatherIcon(Activity activity, TextView weatherIconView, int actualId){

        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            Calendar c = Calendar.getInstance();
            int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
            if(timeOfDay >= 6 && timeOfDay < 19){ //morning time
                icon = activity.getString(R.string.weather_sunny);
            }else {//night time
                icon = activity.getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = activity.getString(R.string.weather_thunder);
                    break;
                case 3 : icon = activity.getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = activity.getString(R.string.weather_foggy);
                    break;
                case 8 : icon = activity.getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = activity.getString(R.string.weather_snowy);
                    break;
                case 5 : icon = activity.getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIconView.setText(icon);
    }

    public static String getRoundOffTemperature(Double number){
        return String.valueOf(Math.round(number));
    }

    public static String getDayFromTS(int dateTs){
        SimpleDateFormat formatShort = new SimpleDateFormat("EEE");
        Date date = new Date(dateTs*1000L);
        return formatShort.format(date);
    }

    public static String getTimeFromTS(int dateTs){
        SimpleDateFormat formatShort = new SimpleDateFormat("hh:mm aa", Locale.US);
        Date date = new Date(dateTs*1000L);
        return formatShort.format(date);
    }

    public static String getLastUpdatedDate(){
        SimpleDateFormat formatShort = new SimpleDateFormat("MMM dd, hh:mm aa", Locale.US);
        Date date = new Date();
        return "Last updated at "+formatShort.format(date);
    }

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
