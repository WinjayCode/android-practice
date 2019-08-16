package com.winjay.practice.location;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.winjay.practice.R;
import com.winjay.practice.utils.NoDoubleClickListener;

import java.util.List;

/**
 * 地理位置
 *
 * @author winjay
 * @date 2019-08-15
 */
public class LocationActivity extends AppCompatActivity implements LocationListener {
    private final String TAG = getClass().getSimpleName();

    private String mMockProviderName = LocationManager.GPS_PROVIDER;
    private LocationManager locationManager;

    // 模拟位置
    private double mockLongitude = 109;
    private double mockLatitude = 34;
    /**
     * 经度
     */
    private TextView longitudeTV;
    /**
     * 纬度
     */
    private TextView latitudeTV;
    private Button getLocation;
    private Button startLocationMock;
    private Button stopLocationMock;

    private Thread mThread;

    private EditText mMockLongitudeET;
    private EditText mMockLatitudeET;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);
        findView();
        setListener();
        init();
        mThread = new Thread(new RunnableMockLocation());
    }

    private void findView() {
        longitudeTV = findViewById(R.id.longitude_tv);
        latitudeTV = findViewById(R.id.latitude_tv);
        getLocation = findViewById(R.id.get_location_btn);
        startLocationMock = findViewById(R.id.start_location_mock);
        stopLocationMock = findViewById(R.id.stop_location_mock);
        mMockLongitudeET = findViewById(R.id.mock_longitude_et);
        mMockLatitudeET = findViewById(R.id.mock_latitude_et);
    }

    private void setListener() {
        MyClickListener noDoubleClickListener = new MyClickListener();
        getLocation.setOnClickListener(noDoubleClickListener);
        startLocationMock.setOnClickListener(noDoubleClickListener);
        stopLocationMock.setOnClickListener(noDoubleClickListener);
    }

    private class MyClickListener extends NoDoubleClickListener {

        @Override
        protected void onNoDoubleClick(View v) {
            if (v == getLocation) {
                getLocation();
                return;
            }
            if (v == startLocationMock) {
                if (!TextUtils.isEmpty(mMockLongitudeET.getText().toString()) &&
                        !TextUtils.isEmpty(mMockLatitudeET.getText().toString())) {
                    if (canMockPosition()) {
                        if (!mThread.isAlive()) {
                            mThread.start();
                        }
                    }
                }
                return;
            }
            if (v == stopLocationMock) {
                stopMockLocation();
                return;
            }
        }
    }

    private void init() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void getLocation() {
        String locationProvider = null;
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Intent i = new Intent();
            i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }
        Log.d(TAG, "getLocation()_locationProvider=" + locationProvider);
        //获取Location
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            longitudeTV.setText("经度：" + location.getLongitude());
            latitudeTV.setText("纬度：" + location.getLatitude());
        }

        locationManager.requestLocationUpdates(locationProvider, 2000, 1, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged()_Longitude=" + location.getLongitude() + ">>>Latitude=" + location.getLatitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged()_provider=" + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled()_provider=" + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled()_provider=" + provider);
    }

    private class RunnableMockLocation implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);

                    if (hasAddTestProvider == false) {
                        continue;
                    }

                    try {
                        setMockLocation(Double.valueOf(mMockLongitudeET.getText().toString()),
                                Double.valueOf(mMockLatitudeET.getText().toString()));
                    } catch (Exception e) {
                        // 防止用户在软件运行过程中关闭模拟位置或选择其他应用
                        stopMockLocation();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setMockLocation(double longitude, double latitude) {
        Log.d(TAG, "setMockLocation()_longitude=" + longitude + ",latitude=" + latitude);
        Location location = new Location(mMockProviderName);
        location.setTime(System.currentTimeMillis());
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAltitude(2.0f);
        location.setAccuracy(3.0f);
        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        locationManager.setTestProviderLocation(mMockProviderName, location);
    }

    private boolean hasAddTestProvider = false;

    public void stopMockLocation() {
        Log.d(TAG, "stopMockLocation()");
        if (hasAddTestProvider) {
            try {
                locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
                // 若未成功addTestProvider，或者系统模拟位置已关闭则必然会出错
            }
            hasAddTestProvider = false;
        }
    }

    private boolean canMockPosition() {
        boolean canMockPosition = (Settings.Secure.getInt(getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0)
                || Build.VERSION.SDK_INT > 22;
        if (canMockPosition) {
            try {
                String providerStr = LocationManager.GPS_PROVIDER;
                LocationProvider provider = locationManager.getProvider(providerStr);
                if (provider != null) {
                    locationManager.addTestProvider(
                            provider.getName()
                            , provider.requiresNetwork()
                            , provider.requiresSatellite()
                            , provider.requiresCell()
                            , provider.hasMonetaryCost()
                            , provider.supportsAltitude()
                            , provider.supportsSpeed()
                            , provider.supportsBearing()
                            , provider.getPowerRequirement()
                            , provider.getAccuracy());
                } else {
                    locationManager.addTestProvider(
                            providerStr
                            , true, true, false,
                            false, true, true,
                            true, Criteria.POWER_HIGH, Criteria.ACCURACY_FINE);
                }
                locationManager.setTestProviderEnabled(providerStr, true);
                locationManager.setTestProviderStatus(providerStr, LocationProvider.AVAILABLE, null, System.currentTimeMillis());

                // 模拟位置可用
                hasAddTestProvider = true;
                canMockPosition = true;
            } catch (SecurityException e) {
                canMockPosition = false;
            }
        }
        return canMockPosition;
    }
}
