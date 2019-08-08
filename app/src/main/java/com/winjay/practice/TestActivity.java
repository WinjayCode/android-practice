package com.winjay.practice;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.winjay.practice.utils.VolumeUtil;
import com.winjay.practice.view.RecognitionView;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mTestSV = findViewById(R.id.test_sv);
        testRL = findViewById(R.id.re_rl);
        skillLL = findViewById(R.id.skill_ll);
        editText = findViewById(R.id.edit);

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
//                        recognitionView.setTTSContent("李白（701年－762年） ，字太白，号青莲居士，又号“谪仙人”，是唐代伟大的浪漫主义诗人，被后人誉为“诗仙”，与杜甫并称为“李杜”，为了与另两位诗人李商隐与杜牧即“小李杜” 区别，杜甫与李白又合称“大李杜” 。其人爽朗大方，爱饮酒作诗，喜交友。李白深受黄老列庄思想影响，有《李太白集》传世，诗作中多以醉时写的，代表作有《望庐山瀑布》、《行路难》、《蜀道难》、《将进酒》、《梁甫吟》、《早发白帝城》等多首。李白所作词赋，宋人已有传记（如文莹《湘山野录》卷上），就其开创意义及艺术成");
//                    }
//                }, 2000);
//            }
//        }, 2000);
    }

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
}
