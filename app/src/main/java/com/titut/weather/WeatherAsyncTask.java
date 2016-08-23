package com.titut.weather;

import android.os.AsyncTask;
import android.os.SystemClock;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 429023 on 8/17/2016.
 */
public class WeatherAsyncTask extends AsyncTask<Void, JSONObject, JSONObject> {

    interface WeatherTaskListener {
        void taskCompleted(JSONObject result) throws JSONException;
    }

    WeatherTaskListener mWeatherTaskListener;
    String mLatitude;
    String mLongitude;

    WeatherAsyncTask(WeatherTaskListener callback, String lat, String lon){
        mWeatherTaskListener = callback;
        mLatitude = lat;
        mLongitude = lon;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        final JSONObject json = GetWeatherData.fetchJSONResponse(mLatitude, mLongitude);
        return json;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        try {
            mWeatherTaskListener.taskCompleted(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

