package com.titut.weather.fragment;

/**
 * Created by 429023 on 8/19/2016.
 */
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.titut.weather.R;
import com.titut.weather.adapter.WeatherListArrayAdapter;
import com.titut.weather.model.CurrentWeatherInfo;
import com.titut.weather.model.ForecastWeather;

import java.util.ArrayList;

public class WeatherListFragment extends ListFragment {
    private WeatherListArrayAdapter mWeatherListArrayAdapter;
    int fragNum;
    CurrentWeatherInfo mCurrentWeatherInfo;
    ArrayList<ForecastWeather> mWeatherList;

    public static WeatherListFragment init(int index, CurrentWeatherInfo mCurrentWeatherInfo, ArrayList<ForecastWeather> mWeatherList) {
        WeatherListFragment weatherListFragment = new WeatherListFragment();

        Bundle args = new Bundle();
        args.putSerializable("currentWeatherInfo", mCurrentWeatherInfo);
        args.putSerializable("forecastWeatherList", mWeatherList);
        weatherListFragment.setArguments(args);

        return weatherListFragment;
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
        View layoutView = inflater.inflate(R.layout.fragment_weather_list, container, false);
        return layoutView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mWeatherListArrayAdapter = new WeatherListArrayAdapter(getActivity(), R.layout.weather_list_row, mWeatherList);
        setListAdapter(mWeatherListArrayAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

    }
}
