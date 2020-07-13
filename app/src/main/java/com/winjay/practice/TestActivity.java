package com.winjay.practice;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.winjay.ioclibrary.BindView;
import com.winjay.ioclibrary.BindViewUtils;
import com.winjay.ioclibrary.CheckNet;
import com.winjay.ioclibrary.OnClick;
import com.winjay.practice.hook.HookSetOnClickListenerHelper;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.utils.VolumeUtil;
import com.winjay.practice.view.RecognitionView;

import java.io.File;
import java.util.HashMap;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

        BindViewUtils.bind(this);

        mTestSV = findViewById(R.id.test_sv);
        testRL = findViewById(R.id.re_rl);
        skillLL = findViewById(R.id.skill_ll);
        editText = findViewById(R.id.edit);
        testBtn = findViewById(R.id.test_btn);
        testBtn.setOnClickListener(v -> {
            LogUtil.d("HookSetOnClickListener", "111");
            Toast.makeText(TestActivity.this, "测试点击", Toast.LENGTH_SHORT).show();
            LogUtil.d("HookSetOnClickListener", "222");

            HashMap<String, Object> map = new HashMap<>(1);
            map.put("type", "本地视频");
            openPage("com.yhkmedia.yhk", "com.yhkmedia.yhk.activity.VideoPlayActivity", map);
        });
        // Hook
        HookSetOnClickListenerHelper.hook(this, testBtn);

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

        mTestSV.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "222");
                Toast.makeText(TestActivity.this, "3s后", Toast.LENGTH_SHORT).show();
            }
        }, 3000);
        LogUtil.d(TAG, "111");

        // 播放本地歌曲
//        mMediaPlayer = new MediaPlayer();
//        try {
////            mMediaPlayer.setDataSource("system/media/audio/ringtones/Basic_Bell.ogg");
////            mMediaPlayer.setDataSource("system/media/Music/guniang.mp3");
//            mMediaPlayer.setDataSource(this, Uri.parse("system/media/Music/guniang.mp3"));
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



        Intent intent = new Intent();
        intent.setAction("aispeech.intent.action.WAKEUP_SERVICE");
        intent.setComponent(new ComponentName("com.aispeech.kui","com.aispeech.kui.service.WakeupService"));
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


        mHandler = new Handler(Looper.myLooper());
        mHandler.post(myRunnable);
        LogUtil.d(TAG, "after post!");
    }

    @OnClick(R.id.animation)
    @CheckNet
    private void OnClick(View view) {

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
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        unbindService(mConnection);
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

    private void mute(View view) {
        VolumeUtil.muteVolume(this, AudioManager.STREAM_MUSIC);
    }

    private void unmute(View view) {
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
        objectAnimator = ObjectAnimator.ofFloat(testBtn, "translationX", 0, -100, 0);
        objectAnimator.setDuration(1000);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                objectAnimator.setCurrentFraction(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.start();
    }
}
