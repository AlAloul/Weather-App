package com.titut.weather.fragment;

/**
 * Created by 429023 on 8/19/2016.
 */
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.titut.weather.R;
import com.titut.weather.model.CurrentWeatherInfo;
import com.titut.weather.model.ForecastWeather;
import com.titut.weather.util.Utils;

import java.util.ArrayList;
import java.util.Date;

public class WeatherDetailFragment extends Fragment {
    CurrentWeatherInfo mCurrentWeatherInfo;
    ArrayList<ForecastWeather> mWeatherList;

    public static WeatherDetailFragment init(int index, CurrentWeatherInfo mCurrentWeatherInfo, ArrayList<ForecastWeather> mWeatherList) {
        WeatherDetailFragment weatherDetailFragment = new WeatherDetailFragment();

        Bundle args = new Bundle();
        args.putSerializable("currentWeatherInfo", mCurrentWeatherInfo);
        args.putSerializable("forecastWeatherList", mWeatherList);
        weatherDetailFragment.setArguments(args);
        return weatherDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mCurrentWeatherInfo = (CurrentWeatherInfo) getArguments().getSerializable("currentWeatherInfo");
            mWeatherList = (ArrayList<ForecastWeather>) getArguments().getSerializable("forecastWeatherList");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_weather_detail, container, false);
        ForecastWeather currentWeather = mWeatherList.get(0);

        View countryView = layoutView.findViewById(R.id.city);
        ((TextView) countryView).setText(mCurrentWeatherInfo.getCityName()+", "+mCurrentWeatherInfo.getCountryName());

        View tempView = layoutView.findViewById(R.id.temp);
        ((TextView) tempView).setText(currentWeather.getTemp()+ (char) 0x00B0+"C");

        View lastUpdatedView = layoutView.findViewById(R.id.updatedAT);
        ((TextView) lastUpdatedView).setText(mCurrentWeatherInfo.getLastUpdatedDate());

        View weatherCondition = layoutView.findViewById(R.id.weatherCondition);
        ((TextView) weatherCondition).setText(currentWeather.getWeatherMain());

        View temperatureValue = layoutView.findViewById(R.id.temperatureValue);
        ((TextView) temperatureValue).setText(currentWeather.getTempMin()+ (char) 0x00B0+"/"+currentWeather.getTempMax()+ (char) 0x00B0);

        View pressureValue = layoutView.findViewById(R.id.pressureValue);
        ((TextView) pressureValue).setText(currentWeather.getPressure()+" hPa");

        View humidityValue = layoutView.findViewById(R.id.humidityValue);
        ((TextView) humidityValue).setText(currentWeather.getHumidity()+"%");

        TextView weatherIconView = (TextView) layoutView.findViewById(R.id.weatherIcon);
        Typeface font = Typeface.createFromAsset( getActivity().getAssets(), "weathericons.ttf" );
        weatherIconView.setTypeface(font);
        Utils.setWeatherIcon(getActivity(), weatherIconView, currentWeather.getWeatherId());
        return layoutView;
    }
}