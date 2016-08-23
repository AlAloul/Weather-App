package com.titut.weather.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.titut.weather.R;
import com.titut.weather.model.ForecastWeather;
import com.titut.weather.util.Utils;

import java.util.ArrayList;


/**
 * Created by 429023 on 8/19/2016.
 */
public class WeatherListArrayAdapter extends ArrayAdapter<ForecastWeather> {

    Context context;

    public WeatherListArrayAdapter(Context context, int textViewResourceId, ArrayList<ForecastWeather> mWeatherList) {
        super(context, textViewResourceId, mWeatherList);
        this.context = context;
    }

    private class ViewHolder {
        TextView dateView;
        TextView timeView;
        TextView statusView;
        TextView weatherIconView;
        TextView temperatureView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ForecastWeather rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.weather_list_row, null);
            holder = new ViewHolder();
            holder.dateView = (TextView) convertView.findViewById(R.id.date);
            holder.timeView = (TextView) convertView.findViewById(R.id.time);
            holder.weatherIconView = (TextView) convertView.findViewById(R.id.weatherIcon);
            holder.statusView = (TextView) convertView.findViewById(R.id.status);
            holder.temperatureView = (TextView) convertView.findViewById(R.id.temperature);

            Typeface font = Typeface.createFromAsset( context.getAssets(), "weathericons.ttf" );
            holder.weatherIconView.setTypeface(font);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.statusView.setText(rowItem.getWeatherMain());
        holder.dateView.setText(Utils.getDayFromTS(rowItem.getDateTimestamp()));
        holder.timeView.setText(Utils.getTimeFromTS(rowItem.getDateTimestamp()));
        holder.temperatureView.setText(rowItem.getTemp()+""+(char) 0x00B0);

        Utils.setWeatherIcon((Activity) context, holder.weatherIconView, rowItem.getWeatherId());

        return convertView;
    }

}
