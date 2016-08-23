package com.titut.weather;

import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.titut.weather.WeatherAsyncTask.WeatherTaskListener;
import com.titut.weather.adapter.WeatherFragmentAdapter;
import com.titut.weather.model.CurrentWeatherInfo;
import com.titut.weather.model.ForecastWeather;
import com.titut.weather.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult>, WeatherTaskListener {

    private static final String TAG = "@@##";

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;

    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;

    private ArrayList<ForecastWeather> mWeatherList = new ArrayList<>();
    private CurrentWeatherInfo mCurrentWeatherInfo = new CurrentWeatherInfo();
    private WeatherAsyncTask weatherAsyncTask;
    private WeatherFragmentAdapter mWeatherFragmentAdapter;
    private ViewPager mViewPager;
    private int mPagerDotsCount;
    private ImageView[] mDots;
    private LinearLayout mPagerIndicator;
    private RelativeLayout mLoadingView;
    private String mCityName = "Weather";
    private ActionBar mActionBar;

    /**
     * Time when the location was updated represented as a String.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActionBar = getSupportActionBar();

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mLoadingView = (RelativeLayout) findViewById(R.id.loadingView);
        mPagerIndicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < mPagerDotsCount; i++) {
                    mDots[i].setImageDrawable(getResources().getDrawable(R.drawable.pager_non_selected_dot));
                }
                mDots[position].setImageDrawable(getResources().getDrawable(R.drawable.pager_selected_dot));

                if(position == 1){
                    if(mActionBar != null){
                        mActionBar.setTitle(R.string.next_5_days);
                    }
                } else {
                    if(mActionBar != null){
                        mActionBar.setTitle(mCityName);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mRequestingLocationUpdates = false;

        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    private void setViewPagerIndicator() {
        mPagerDotsCount = mWeatherFragmentAdapter.getCount();
        mDots = new ImageView[mPagerDotsCount];

        mPagerIndicator.removeAllViews();
        for (int i = 0; i < mPagerDotsCount; i++) {
            mDots[i] = new ImageView(this);
            mDots[i].setImageDrawable(getResources().getDrawable(R.drawable.pager_non_selected_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);
            mPagerIndicator.addView(mDots[i], params);
        }

        mDots[0].setImageDrawable(getResources().getDrawable(R.drawable.pager_selected_dot));

        mLoadingView.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
        mPagerIndicator.setVisibility(View.VISIBLE);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.refresh:
                checkLocationSettings();
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;
            }
        });

    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();

        checkLocationSettings();

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

        String lat = String.valueOf(mCurrentLocation.getLatitude());
        String lon = String.valueOf(mCurrentLocation.getLongitude());
        Log.d("@@##", "lat: "+lat+", lang: "+lon);
        if(lat != null && lon != null){
            if(Utils.isNetworkAvailable(getApplicationContext())){
                loadData(lat, lon);
            } else {
                Toast.makeText(getApplicationContext(), R.string.internet_message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadData(String lat, String lon){
        weatherAsyncTask = new WeatherAsyncTask(this, lat, lon);
        weatherAsyncTask.execute();
        stopLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void taskCompleted(JSONObject jsonObject) throws JSONException {
        try {
            Log.i("@@##", " jsonObject = "+jsonObject.toString());
            JSONObject cityObject = jsonObject.getJSONObject("city");
            String cityName = (String) cityObject.get("name");
            String countyrName = (String)cityObject.get("country");
            mCurrentWeatherInfo.setCityName(cityName);
            mCityName = cityName +", "+countyrName;
            if(mActionBar != null){
                mActionBar.setTitle(mCityName);
            }
            mCurrentWeatherInfo.setCountryName(countyrName);
            mCurrentWeatherInfo.setLastUpdatedDate(Utils.getLastUpdatedDate());

            JSONArray list = jsonObject.getJSONArray("list");

            mWeatherList = new ArrayList<>();

            for(int i=0; i<list.length(); i++){
                JSONObject listObject = (JSONObject)list.get(i);
                ForecastWeather forecastWeather = new ForecastWeather();

                int dateTs = (int) listObject.get("dt");
                forecastWeather.setDateTimestamp(dateTs);
                Date date = new Date(dateTs*1000L);

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
                String fullDate = formatter.format(date);


                JSONObject mainObject = listObject.getJSONObject("main");
                forecastWeather.setDate(fullDate);

                try{
                    forecastWeather.setTemp(Utils.getRoundOffTemperature((Double) mainObject.get("temp")));
                } catch (Exception e){
                    forecastWeather.setTemp(String.valueOf((Object) mainObject.get("temp")));
                }

                try{
                    forecastWeather.setTempMin(Utils.getRoundOffTemperature((Double) mainObject.get("temp_min")));
                } catch (Exception e){
                    forecastWeather.setTempMin(String.valueOf((Object) mainObject.get("temp_min")));
                }

                try{
                    forecastWeather.setTempMax(Utils.getRoundOffTemperature((Double) mainObject.get("temp_max")));
                } catch (Exception e){
                    forecastWeather.setTempMax(String.valueOf((Object) mainObject.get("temp_max")));
                }
                forecastWeather.setPressure(String.valueOf((Object) mainObject.get("pressure")));
                forecastWeather.setSeaLevel(String.valueOf((Object) mainObject.get("sea_level")));
                forecastWeather.setGrndLevel(String.valueOf((Object) mainObject.get("grnd_level")));
                forecastWeather.setHumidity(String.valueOf((Object) mainObject.get("humidity")));

                JSONArray weatherArray = listObject.getJSONArray("weather");
                String weatherMain = "NA";
                String weatherDescription = "NA";
                int weatherId = 0;
                String weatherIcon = "";
                if(weatherArray.length()>0){
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    weatherMain = (String)weatherObject.get("main");
                    weatherDescription = (String)weatherObject.get("description");
                    weatherId = (int)weatherObject.get("id");
                    weatherIcon = (String)weatherObject.get("icon");
                }
                forecastWeather.setWeatherMain(weatherMain);
                forecastWeather.setWeatherDescription(weatherDescription);
                forecastWeather.setWeatherId(weatherId);
                forecastWeather.setWeatherIcon(weatherIcon);
                mWeatherList.add(forecastWeather);

                Log.d("@@##", "TS = "+forecastWeather.getDateTimestamp()+"\nweatherId = "+weatherId+", getWeatherMain = "+forecastWeather.getWeatherMain()+", tempMax = "+forecastWeather.getTempMax()+"\nweatherMain = "+weatherMain+", weatherDescription = "+weatherDescription);
            }

            //update fragment adapter
            mWeatherFragmentAdapter = new WeatherFragmentAdapter(getSupportFragmentManager(),mCurrentWeatherInfo, mWeatherList);
            mViewPager.setAdapter(mWeatherFragmentAdapter);
            setViewPagerIndicator();
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }
}