package com.winjay.practice;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.winjay.annotations.BindView;
import com.winjay.annotations.OnClick;
import com.winjay.bind.BindHelper;
import com.winjay.bind.Unbinder;
import com.winjay.practice.hook.HookSetOnClickListenerHelper;
import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.ui.dialog.MyDialog;
import com.winjay.practice.utils.ByteUtil;
import com.winjay.practice.utils.CountDownTimerUtil;
import com.winjay.practice.utils.FileUtil;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.utils.NetUtil;
import com.winjay.practice.utils.VolumeUtil;
import com.winjay.practice.ui.view.RecognitionView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

/**
 * 测试练习使用
 *
 * @author winjay
 * @date 2019/5/31
 */
public class TestActivity extends AppCompatActivity {
    private static final String TAG = TestActivity.class.getSimpleName();
    private RelativeLayout testRL;
    private LinearLayout skillLL;

    private ScrollView mTestSV;

    private Button downBtn;
    private EditText editText;

    private RecognitionView recognitionView;

    private AudioManager audioManager;

    private Button testBtn;

    private ObjectAnimator objectAnimator;

    private MediaPlayer mMediaPlayer;

    private Messenger mService;

    private Handler mHandler;

    @BindView(R.id.animation)
    Button animation;

    @BindView(R.id.root_rl)
    ConstraintLayout root_rl;

    private Unbinder mUnbinder;

    private boolean isTest = false;

    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 打印线程Looper消息
//        Looper.getMainLooper().setMessageLogging(new Printer() {
//            @Override
//            public void println(String x) {
//                LogUtil.d(TAG, "x" + x);
//            }
//        });
        setContentView(R.layout.test_activity);
        mUnbinder = BindHelper.bind(this);

        registerHDMI();

        mTestSV = findViewById(R.id.test_sv);
        testRL = findViewById(R.id.re_rl);
        skillLL = findViewById(R.id.skill_ll);
        editText = findViewById(R.id.edit);
        testBtn = findViewById(R.id.test_btn);
        testBtn.setOnClickListener(v -> {
            LogUtil.d("HookSetOnClickListener", "111");
            Toast.makeText(TestActivity.this, "测试点击", Toast.LENGTH_SHORT).show();
            LogUtil.d("HookSetOnClickListener", "222");

//            MyDialog dialog = new MyDialog(TestActivity.this);
//            dialog.show();

//            AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
//            builder.setMessage("测试弹窗！");
//            mAlertDialog = builder.create();
//            mAlertDialog.show();

//            if (isTest) {
//                fullscreen(true);
//                isTest = false;
//            } else {
//                fullscreen(false);
//                isTest = true;
//            }

//            Intent intent = new Intent();
//            intent.setClassName("com.mobiledrivetech.car_engineer_mode", "com.mobiledrivetech.car_engineer_mode.MainActivity");
//            startActivity(intent);

//            HashMap<String, Object> map = new HashMap<>(1);
//            map.put("type", "本地视频");
//            openPage("com.yhkmedia.yhk", "com.yhkmedia.yhk.activity.VideoPlayActivity", map);

//            CountDownTimerUtil countDownTimerUtil = new CountDownTimerUtil();
//            countDownTimerUtil.setOnCountDownListener(new CountDownTimerUtil.OnCountDownListener() {
//                @Override
//                public void onTick(long millisUntilFinished) {
//                    LogUtil.i(TAG, "millisUntilFinished=" + millisUntilFinished);
//                }
//
//                @Override
//                public void onFinish() {
//                    LogUtil.i(TAG, "");
//                }
//            });
//            countDownTimerUtil.start();

//            String savedPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//            boolean result = FileUtil.copyFolder(savedPath + File.separator + "sync", savedPath);
//            LogUtil.d(TAG, "result=" + result);

//            boolean result = false;
//            boolean isExists = false;
//            File videoFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/video/");
//            if (videoFolder.exists()) {
//                for (File folder : videoFolder.listFiles()) {
//                    LogUtil.d(TAG, "folder=" + folder.getPath());
//                    File resourceFile = new File(folder.getPath() + File.separator + "3G2.3g2");
//                    LogUtil.d(TAG, "resourceFile=" + resourceFile.getName());
//                    if (resourceFile.exists()) {
//                        isExists = true;
//                        result = resourceFile.delete();
//                    }
//                }
//            }
//            LogUtil.d(TAG, "result=" + result);
//            LogUtil.d(TAG, "isExists=" + isExists);


            // android12版本无法启动
//            Intent intent = new Intent();
//            intent.setComponent(new ComponentName("com.winjay.myapplication", "com.winjay.myapplication.TestService"));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(intent);
//            }
//            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


//            String savedPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//            boolean result = FileUtil.copyFolder(savedPath + File.separator + "test", savedPath + File.separator + "test2");
//            LogUtil.d(TAG, "result=" + result);
        });
        // Hook
        HookSetOnClickListenerHelper.hook(this, testBtn);

        Button testBtn2 = findViewById(R.id.test_btn2);
        testBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String savedPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String oldPath = savedPath + File.separator + "test" + File.separator;
                String newPath = savedPath + File.separator + "test2" + File.separator;
                LogUtil.d(TAG, "oldPath=" + oldPath);
                LogUtil.d(TAG, "newPath=" + newPath);
                boolean result = copyFolder(oldPath, newPath);
                LogUtil.d(TAG, "result=" + result);
            }
        });

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

//        recognitionView = new RecognitionView(this);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        testRL.addView(recognitionView, layoutParams);
//
//        recognitionView.setTTSContent("我在，有什么可以帮你");
//        recognitionView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                recognitionView.setRecognitionContent("李白是谁");
//                recognitionView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        recognitionView.setTTSContent("李白（701年－762年） ，字太白，号青莲居士，又号“谪仙人”，是唐代伟大的浪漫主义诗人，被后人誉为“诗仙”，与杜甫并称为“李杜”，为了与另两位诗人李商隐与杜牧即“小李杜” 区别，杜甫与李白又合称“大李杜” 。其人爽朗大方，爱饮酒作诗，喜交友。李白深受黄老列庄思想影响，有《李太白集》传世，诗作中多以醉时写的，代表作有《望庐山瀑布》、《行路难》、《蜀道难》、《将进酒》、《梁甫吟》、《早发白帝城》等多首。李白所作词赋，宋人已有传记（如文莹《湘山野录》卷上），就其开创意义及艺术成就而言，“李白词”享有极为崇高的地位。");
//                    }
//                }, 2000);
//            }
//        }, 2000);


        //监听屏幕亮度变化
//        getContentResolver().registerContentObserver(
//                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
//                true,
//                mBrightnessObserver);
//        setActivityBrightness(1.0f);

//        mTestSV.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                LogUtil.d(TAG, "222");
//                Toast.makeText(TestActivity.this, "3s后", Toast.LENGTH_SHORT).show();
//            }
//        }, 3000);
//        LogUtil.d(TAG, "111");

        // 播放本地歌曲
//        mMediaPlayer = new MediaPlayer();
//        try {
////            mMediaPlayer.setDataSource("system/media/audio/ringtones/Basic_Bell.ogg");
////            mMediaPlayer.setDataSource("system/media/Music/guniang.mp3");
////            mMediaPlayer.setDataSource(this, Uri.parse("system/media/Music/guniang.mp3"));
//            AssetFileDescriptor afd = getAssets().openFd("audio/chengdu.mp3");
//            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mMediaPlayer.prepareAsync();
//        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                LogUtil.d(TAG, "duration=" + mMediaPlayer.getDuration());
//                mMediaPlayer.start();
//            }
//        });
//
//        getFilesAllName("system/media/Music");


//        Intent intent = new Intent();
//        intent.setAction("aispeech.intent.action.WAKEUP_SERVICE");
//        intent.setComponent(new ComponentName("com.aispeech.kui","com.aispeech.kui.service.WakeupService"));
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


//        mHandler = new Handler(Looper.myLooper());
//        mHandler.post(myRunnable);
//        LogUtil.d(TAG, "after post!");

//        SoundPoolUtil.getInstance(this).playSoundFromAssets("audio/0.mp3");

//        HandlerManager.getInstance().postDelayedOnSubThread(new Runnable() {
//            @Override
//            public void run() {
//                LogUtil.d(TAG, "send broadcast");
////                sendBroadcast(new Intent("test"));
//                sendBroadcast(new Intent("test"), "haha");
//            }
//        }, 2000);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        LogUtil.d(TAG, "onUserInteraction()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume()");

//        LogUtil.d(TAG, "" + ByteUtil.hexToByte("0xff"));

//        boolean result = copyFileToDir(Environment.getExternalStorageDirectory() + File.separator + "test.wav",
//                Environment.getExternalStorageDirectory() + File.separator + "test2.wav");
//        LogUtil.d(TAG, "result=" + result);

        LogUtil.d(TAG, "isOnline=" + NetUtil.isOnline());
        LogUtil.d(TAG, "isEthernetConnected=" + NetUtil.isEthernetConnected(this));
        LogUtil.d(TAG, "networkAvailable=" + NetUtil.networkAvailable(this));
    }

    /**
     * 根据包名类名，传参数启动
     *
     * @param packageName
     * @param activity
     * @param data
     * @return
     */
    private void openPage(String packageName, String activity, HashMap<String, Object> data) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (data != null) {
            for (String key : data.keySet()) {
                Object value = data.get(key);
                if (value instanceof Integer) {
                    intent.putExtra(key, Integer.parseInt(value.toString()));
                } else {
                    intent.putExtra(key, value.toString());
                }
            }
        }
        intent.setComponent(new ComponentName(packageName, activity));
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
//            ToastUtil.showToastLong(ctx, "还没有这个应用哦，请联系管理员。");
            LogUtil.d(TAG, "没有这个应用");
        }
    }

    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtil.d(TAG, "myRunnable:run()");
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d(TAG, "onServiceConnected()");
            mService = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d(TAG, "onServiceDisconnected()");
        }
    };

    private Messenger mGetReplyMessenger = new Messenger(new MessengerHandler());

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    LogUtil.d(TAG, "result=" + msg.getData().getInt("result"));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void getFilesAllName(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) {
            LogUtil.d("error", "空目录");
            return;
        }
//        List<String> s = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            LogUtil.d(TAG, "name=" + files[i].getAbsolutePath());
//            s.add(files[i].getAbsolutePath());
        }
//        return s;
    }

    @Override
    protected void onPause() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
//        unbindService(mConnection);

        if (mUnbinder != null) {
            mUnbinder.unbind();
        }

        unregisterHDMI();
    }

    /*
     * 调整当前Activity的亮度，仅作用于当前调整当前Activity的亮度范围0-1f
     * */
    public void setActivityBrightness(float paramFloat) {
        Window localWindow = this.getWindow();
        WindowManager.LayoutParams params = localWindow.getAttributes();
        params.screenBrightness = paramFloat;
        localWindow.setAttributes(params);
    }

    /*
     * 屏幕亮度变化监听的回调
     * */
    private ContentObserver mBrightnessObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            // 当前系统的屏幕亮度（当用户改变了系统亮度后，会回调到该方法）
            try {
                int currentValue = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                LogUtil.d(TAG, "currentValue=" + currentValue);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            // 作用是使刚才设置的Activity的亮度失效，值只要大于1.（为什么？）
            setActivityBrightness(2.0f);
        }
    };

    @OnClick(R.id.mute)
    void mute(View view) {
        VolumeUtil.muteVolume(this, AudioManager.STREAM_MUSIC);
    }

    @OnClick(R.id.unmute)
    void unmute(View view) {
        VolumeUtil.unMuteVolume(this, AudioManager.STREAM_MUSIC);
    }

    public void up(View view) {
//        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(downBtn, "translationY", 200, -50, 50, 0);
//        objectAnimator.setDuration(1000);
//        objectAnimator.setInterpolator(new AccelerateInterpolator());
//        objectAnimator.start();

//        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(downBtn, "translationY", 0, 50, 0, 500);
//        objectAnimator.setDuration(1000);
//        objectAnimator.setInterpolator(new DecelerateInterpolator());
//        objectAnimator.start();

        VolumeUtil.volumeUp(this, AudioManager.STREAM_MUSIC);
        getVolume(view);
    }

    public void down(View view) {
        VolumeUtil.volumeDown(this, AudioManager.STREAM_MUSIC);
        getVolume(view);
    }

    public void getVolume(View view) {
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Toast.makeText(this, "" + volume, Toast.LENGTH_SHORT).show();
    }

    public void setVolume(View view) {
        if (!TextUtils.isEmpty(editText.getText().toString()) && TextUtils.isDigitsOnly(editText.getText().toString())) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Integer.valueOf(editText.getText().toString()), AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_PLAY_SOUND);
        }
    }

    public void startDialog(View view) {
        Intent intent = new Intent();
        intent.setAction("aispeech.intent.action.START_DIALOG");
        sendBroadcast(intent);
    }

    public void setting(View view) {
        //系统设置界面
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startActivity(intent);
    }

    public void enableWakeup(View view) {
        Message msg = Message.obtain(null, 1);
        msg.replyTo = mGetReplyMessenger;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void disableWakeup(View view) {
        Message msg = Message.obtain(null, 2);
        msg.replyTo = mGetReplyMessenger;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void animation(View view) {
        LogUtil.d(TAG, "animation()");
        // 补间动画
        // 只是改变了view的显示位置，但是没有改变view的属性，点击view在屏幕上的显示位置，是不会有事件响应的
        TranslateAnimation translateAnimation = new TranslateAnimation(0, -200, 0, 0);
        translateAnimation.setDuration(1000);
        translateAnimation.setFillAfter(true);
        translateAnimation.setInterpolator(new LinearInterpolator());
        testBtn.startAnimation(translateAnimation);

        // 属性动画
        // 会改变view的真实物理位置，点击view以前的位置是不会响应点击事件的
//        objectAnimator = ObjectAnimator.ofFloat(testBtn, "translationX", 0, -100, 0);
//        objectAnimator.setDuration(1000);
//        objectAnimator.setInterpolator(new LinearInterpolator());
//        objectAnimator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                // 使动画回到最初状态
//                objectAnimator.setCurrentFraction(0);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//        objectAnimator.start();
    }

    private void fullscreen(boolean isFullScreen) {
        if (isFullScreen) { //隐藏状态栏
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        } else { //显示状态栏
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(lp);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.d(TAG, "keyCode=" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_MEDIA_NEXT:
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
            case KeyEvent.KEYCODE_VOLUME_MUTE:
            case KeyEvent.KEYCODE_CALL:
            case KeyEvent.KEYCODE_VOICE_ASSIST:
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    // ------------------------------ HDMI-IN ------------------------------
    private static final String ACTION_HDMI_PLUGGED = "android.intent.action.HDMI_PLUGGED";
    private static final String EXTRA_HDMI_PLUGGED_STATE = "state";
    private boolean mIsHDMIPlugged = false;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("test".equals(action)) {
                LogUtil.d(TAG, "test!!!");
            }
            if (ACTION_HDMI_PLUGGED.equals(action)) {
                mIsHDMIPlugged = intent.getBooleanExtra(EXTRA_HDMI_PLUGGED_STATE, false);
                LogUtil.d(TAG, "onReceive:mIsHDMIPlugged =  " + mIsHDMIPlugged);
            }
        }
    };

    private void registerHDMI() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_HDMI_PLUGGED);
        intentFilter.addAction("test");
//        registerReceiver(mReceiver, intentFilter);
        registerReceiver(mReceiver, intentFilter, "haha", HandlerManager.getInstance().getSubHandler());
    }

    private void unregisterHDMI() {
        unregisterReceiver(mReceiver);
    }

    /**
     * Checks device switch files to see if an HDMI device/MHL device is plugged in, returning true if so.
     */
    private boolean isHdmiSwitchSet() {
        // The file '/sys/devices/virtual/switch/hdmi/state' holds an int -- if it's 1 then an HDMI device is connected.
        // An alternative file to check is '/sys/class/switch/hdmi/state' which exists instead on certain devices.
        File switchFile = new File("/sys/devices/virtual/switch/hdmi/state");
        if (!switchFile.exists()) {
            switchFile = new File("/sys/class/switch/hdmi/state");
        }
        try {
            Scanner switchFileScanner = new Scanner(switchFile);
            int switchValue = switchFileScanner.nextInt();
            switchFileScanner.close();
            return switchValue > 0;
        } catch (Exception e) {
            return false;
        }
    }


    public static boolean copyFolder(String oldPath, String newPath) {
        File newFile = new File(newPath);
        if (!newFile.exists()) {
            if (!newFile.mkdirs()) {
                LogUtil.e(TAG, "copyFolder: cannot create directory.");
                return false;
            }
        }
        File oldFile = new File(oldPath);
        String[] files = oldFile.list();
        if (files == null || files.length == 0) {
            LogUtil.w(TAG, "oldPath has no file.");
            return true;
        }
        File temp;
        for (String file : files) {
            if (oldPath.endsWith(File.separator)) {
                temp = new File(oldPath + file);
            } else {
                temp = new File(oldPath + File.separator + file);
            }

            if (temp.isDirectory()) {   //如果是子文件夹
                boolean result = copyFolder(oldPath + "/" + file, newPath + "/" + file);
                if (!result) {
                    LogUtil.e(TAG, "copy subfolder error!");
                    return false;
                }
            } else if (!temp.exists()) {
                LogUtil.e(TAG, "copyFolder:  oldFile not exist.");
                return false;
            } else if (!temp.isFile()) {
                LogUtil.e(TAG, "copyFolder:  oldFile not file.");
                return false;
            } else if (!temp.canRead()) {
                LogUtil.e(TAG, "copyFolder:  oldFile cannot read.");
                return false;
            } else {
                FileInputStream fileInputStream = null;
                FileOutputStream fileOutputStream = null;
                try {
                    fileInputStream = new FileInputStream(temp);
                    fileOutputStream = new FileOutputStream(newPath + "/" + temp.getName());
                    byte[] buffer = new byte[1024];
                    int byteRead;
                    while ((byteRead = fileInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, byteRead);
                    }
                    // 防止文件流因为拷贝结束立即拔出U盘导致的未完全写入文件的问题
                    fileOutputStream.getFD().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "copy error!");
                    return false;
                } finally {
                    try {
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
        LogUtil.d(TAG, "copy success!");
        return true;
    }
}
