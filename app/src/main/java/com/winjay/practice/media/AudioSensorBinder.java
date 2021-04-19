package com.winjay.practice.media;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.winjay.practice.utils.LogUtil;

import java.lang.ref.WeakReference;

/**
 * 监听是否靠近耳机听筒
 *
 * @author Winjay
 * @date 2021-04-01
 */
public class AudioSensorBinder implements LifecycleObserver, SensorEventListener {
    private static final String TAG = AudioSensorBinder.class.getSimpleName();

    private final AudioManager audioManager;
    private final PowerManager mPowerManager;
    @Nullable
    private WeakReference<AppCompatActivity> mActivity;
    private SensorManager sensorManager;
    private Sensor sensor;
    private PowerManager.WakeLock wakeLock;

    public AudioSensorBinder(@NonNull AppCompatActivity activity) {
        mActivity = new WeakReference<>(activity);
        //可以监听生命周期
        if (getActivity() != null) {
            getActivity().getLifecycle().addObserver(this);
        }
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        mPowerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        registerProximitySensorListener();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        sensorManager = null;
        wakeLock = null;
        mActivity = null;
    }

    /**
     * 注册距离感应器监听器，监测用户是否靠近手机听筒
     */
    private void registerProximitySensorListener() {
        if (getActivity() == null) {
            return;
        }
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null) {
            return;
        }
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private AppCompatActivity getActivity() {
        if (mActivity != null) {
            return mActivity.get();
        }
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (audioManager == null) {
            return;
        }
        if (isHeadphonesPlugged()) {
            // 如果耳机已插入，设置距离传感器失效
            return;
        }
//        LogUtil.d(TAG, "onSensorChanged: " + MediaManager.isPlaying() + " event.values[0]: " + event.values[0]);
        if (true /** MediaManager.isPlaying()*/) {
            // 如果音频正在播放
            float distance = event.values[0];
            if (distance >= sensor.getMaximumRange()) {
                // 用户远离听筒，音频外放，亮屏
                changeToSpeaker();
                LogUtil.d(TAG, "onSensorChanged: 外放");
            } else {
//                MediaManager.reStart();
                // 用户贴近听筒，切换音频到听筒输出，并且熄屏防误触
                changeToReceiver();
                LogUtil.d(TAG, "onSensorChanged: 听筒");
                audioManager.setSpeakerphoneOn(false);
            }
        } else {
            // 音频播放完了
            changeToSpeaker();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private boolean isHeadphonesPlugged() {
        if (audioManager == null) {
            return false;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            AudioDeviceInfo[] audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
            for (AudioDeviceInfo deviceInfo : audioDevices) {
                // 判断有线耳机类型
                if (deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                        || deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET) {
                    return true;
                }
            }
            return false;
        } else {
            return audioManager.isWiredHeadsetOn();
        }
    }


    /**
     * 切换到外放
     */
    public void changeToSpeaker() {
        setScreenOn();
        if (audioManager == null) {
            return;
        }
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);
    }

    /**
     * 切换到耳机模式
     */
    public void changeToHeadset() {
        if (audioManager == null) {
            return;
        }
        audioManager.setSpeakerphoneOn(false);
    }

    /**
     * 切换到听筒
     */
    public void changeToReceiver() {
        setScreenOff();
        if (audioManager == null) {
            return;
        }
        audioManager.setSpeakerphoneOn(false);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    private void setScreenOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LogUtil.i(TAG, "setScreenOff: 熄灭屏幕");
            if (wakeLock == null) {
                wakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG);
            }
            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        }
    }

    private void setScreenOn() {
        if (wakeLock != null) {
            wakeLock.setReferenceCounted(false);
            wakeLock.release();
            wakeLock = null;
        }
    }

}
