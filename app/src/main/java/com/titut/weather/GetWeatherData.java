package com.titut.weather;

/**
 * Created by 429023 on 8/17/2016.
 */


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.util.Log;

import com.titut.weather.util.Constants;

public class GetWeatherData {

    public static JSONObject fetchJSONResponse(String lat, String lon){
        try {
            URL url = new URL(String.format(Constants.WEATHER_MAP_API_LAT_LON, lat,lon));
            Log.d("@@##", "full url = \n"+String.format(Constants.WEATHER_MAP_API_LAT_LON, lat,lon));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key", Constants.OPEN_WEATHER_API_KEY);

            BufferedReader buffReader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String str="";
            while((str=buffReader.readLine())!=null)
                json.append(str).append("\n");
            buffReader.close();

            JSONObject responseData = new JSONObject(json.toString());

            if(responseData.getInt("cod") != 200){
                return null;
            }

            return responseData;
        }catch(Exception e){
            return null;
        }
    }
}
