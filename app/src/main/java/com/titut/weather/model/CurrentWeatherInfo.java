package com.titut.weather.model;

import java.io.Serializable;

/**
 * Created by 429023 on 8/17/2016.
 */
public class CurrentWeatherInfo implements Serializable {

    String cityName;
    String countryName;

    String lastUpdatedDate;

    public CurrentWeatherInfo() {
    }
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

}
