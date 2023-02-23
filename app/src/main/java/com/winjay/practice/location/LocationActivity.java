package com.winjay.practice.location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;

import com.winjay.practice.utils.JsonUtil;
import com.winjay.practice.utils.LogUtil;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.winjay.practice.R;
import com.winjay.practice.utils.NoDoubleClickListener;
import com.winjay.practice.utils.ToastUtil;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 地理位置
 *
 * @author winjay
 * @date 2019-08-15
 */
public class LocationActivity extends AppCompatActivity implements LocationListener, EasyPermissions.PermissionCallbacks {
    private static final String TAG = LocationActivity.class.getSimpleName();

    private final int RC_PERMISSION = 100;

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

    private GPSCallback mGPSCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);
        findView();
        setListener();
        init();
        requiresPermissions();
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
                    } else {
                        ToastUtil.show(LocationActivity.this, "无法模拟定位！");
                    }
                } else {
                    ToastUtil.show(LocationActivity.this, "输入要模拟的经纬度信息！");
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
        mThread = new Thread(new RunnableMockLocation());
        mGPSCallback = new GPSCallback();
    }

    @AfterPermissionGranted(RC_PERMISSION)
    private void requiresPermissions() {
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
//            getLocation();
            getGPS();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "请授予权限，否则影响部分使用功能。", RC_PERMISSION, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        LogUtil.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            String locationProvider = null;
            //获取所有可用的位置提供器
            List<String> providers = locationManager.getProviders(true);
            LogUtil.d(TAG, "providers=" + JsonUtil.getInstance().toJson(providers));
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
            LogUtil.d(TAG, "getLocation()_locationProvider=" + locationProvider);
            if (!TextUtils.isEmpty(locationProvider)) {
                //获取Location
                Location location = locationManager.getLastKnownLocation(locationProvider);
                if (location != null) {
                    longitudeTV.setText("经度：" + location.getLongitude());
                    latitudeTV.setText("纬度：" + location.getLatitude());
                }

                locationManager.requestLocationUpdates(locationProvider, 2000, 1, this);
            } else {
                Toast.makeText(this, "没有可用的定位服务提供", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.registerGnssStatusCallback(mGPSCallback);
        }
    }

    private class GPSCallback extends GnssStatus.Callback {
        @Override
        public void onStarted() {
            super.onStarted();
            LogUtil.d(TAG);
        }

        @Override
        public void onStopped() {
            super.onStopped();
            LogUtil.d(TAG);
        }

        @Override
        public void onFirstFix(int ttffMillis) {
            super.onFirstFix(ttffMillis);
            LogUtil.d(TAG, "ttffMillis=" + ttffMillis);
        }

        @Override
        public void onSatelliteStatusChanged(GnssStatus status) {
            super.onSatelliteStatusChanged(status);
            LogUtil.d(TAG, "SatelliteCount=" + status.getSatelliteCount());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LogUtil.d(TAG, "onLocationChanged()_Longitude=" + location.getLongitude() + ">>>Latitude=" + location.getLatitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LogUtil.d(TAG, "onStatusChanged()_provider=" + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        LogUtil.d(TAG, "onProviderEnabled()_provider=" + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        LogUtil.d(TAG, "onProviderDisabled()_provider=" + provider);
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
                        LogUtil.w(TAG, "mock location error!", e);
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
        LogUtil.d(TAG, "setMockLocation()_longitude=" + longitude + ",latitude=" + latitude);
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
        LogUtil.d(TAG);
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
                LogUtil.w(TAG, "SecurityException=" + e.getMessage());
                canMockPosition = false;
            }
        }
        return canMockPosition;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGPSCallback != null) {
            locationManager.unregisterGnssStatusCallback(mGPSCallback);
            mGPSCallback = null;
        }
    }
}
