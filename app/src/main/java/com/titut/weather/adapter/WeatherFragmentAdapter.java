package com.titut.weather.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.titut.weather.fragment.WeatherDetailFragment;
import com.titut.weather.fragment.WeatherListFragment;
import com.titut.weather.model.CurrentWeatherInfo;
import com.titut.weather.model.ForecastWeather;

import java.util.ArrayList;

/**
 * Created by 429023 on 8/19/2016.
 */
public class WeatherFragmentAdapter extends FragmentPagerAdapter {
    static final int ITEMS = 2;
    private CurrentWeatherInfo mCurrentWeatherInfo;
    private ArrayList<ForecastWeather> mWeatherList;

    public WeatherFragmentAdapter(FragmentManager fragmentManager, CurrentWeatherInfo mCurrentWeatherInfo, ArrayList<ForecastWeather> mWeatherList) {
        super(fragmentManager);
        this.mCurrentWeatherInfo = mCurrentWeatherInfo;
        this.mWeatherList = mWeatherList;
    }

    @Override
    public int getCount() {
        return ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return WeatherDetailFragment.init(position, mCurrentWeatherInfo, mWeatherList);
            default:
                return WeatherListFragment.init(position, mCurrentWeatherInfo, mWeatherList);
        }
    }
}
