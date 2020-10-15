package com.winjay.g2048.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winjay.g2048.R;
import com.winjay.g2048.view.GameView;


public class Game2048Activity extends Activity implements OnClickListener {
    /**
     * SP对象
     */
    public static SharedPreferences mSp;

    /**
     * Game2048Activity Goal
     */
    public static int mGameGoal;

    /**
     * GameView行列数
     */
    public static int mGameLines;

    /**
     * Item宽高
     */
    public static int mItemSize;

    /**
     * 记录分数
     */
    public static int SCROE = 0;

    public static String SP_HIGH_SCROE = "SP_HIGHSCROE";

    public static String KEY_HIGH_SCROE = "KEY_HighScore";

    public static String KEY_GAME_LINES = "KEY_GAMELINES";

    public static String KEY_GAME_GOAL = "KEY_GameGoal";

    // Activity的引用
    private static Game2048Activity mGame2048Activity;
    // 记录分数
    private TextView mTvScore;
    // 历史记录分数
    private TextView mTvHighScore;
    private int mHighScore;
    // 目标分数
    private TextView mTvGoal;
    private int mGoal;
    // 重新开始按钮
    private Button mBtnRestart;
    // 撤销按钮
    private Button mBtnRevert;
    // 选项按钮
    private Button mBtnOptions;
    // 游戏面板
    private GameView mGameView;

    public Game2048Activity() {
        mGame2048Activity = this;
    }

    /**
     * 获取当前Activity的引用
     *
     * @return Activity.this
     */
    public static Game2048Activity getGameActivity() {
        return mGame2048Activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initConfig();
        // 初始化View
        initView();
        mGameView = new GameView(this);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.game_panel);
        // 为了GameView能居中
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.game_panel_rl);
        relativeLayout.addView(mGameView);
    }

    private void initConfig() {
        mSp = getSharedPreferences(SP_HIGH_SCROE, 0);
        mGameLines = mSp.getInt(KEY_GAME_LINES, 4);
        mGameGoal = mSp.getInt(KEY_GAME_GOAL, 2048);
        mItemSize = 0;
    }

    /**
     * 初始化View
     */
    private void initView() {
        mTvScore = (TextView) findViewById(R.id.scroe);
        mTvGoal = (TextView) findViewById(R.id.tv_Goal);
        mTvHighScore = (TextView) findViewById(R.id.record);
        mBtnRestart = (Button) findViewById(R.id.btn_restart);
        mBtnRevert = (Button) findViewById(R.id.btn_revert);
        mBtnOptions = (Button) findViewById(R.id.btn_option);
        mBtnRestart.setOnClickListener(this);
        mBtnRevert.setOnClickListener(this);
        mBtnOptions.setOnClickListener(this);
        mHighScore = mSp.getInt(KEY_HIGH_SCROE, 0);
        mGoal = mSp.getInt(KEY_GAME_GOAL, 2048);
        mTvHighScore.setText("" + mHighScore);
        mTvGoal.setText("" + mGoal);
        mTvScore.setText("0");
        setScore(0, 0);
    }

    public void setGoal(int num) {
        mTvGoal.setText(String.valueOf(num));
    }

    /**
     * 修改得分
     *
     * @param score score
     * @param flag  0 : score 1 : high score
     */
    public void setScore(int score, int flag) {
        switch (flag) {
            case 0:
                mTvScore.setText("" + score);
                break;
            case 1:
                mTvHighScore.setText("" + score);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_restart) {
            mGameView.startGame();
            setScore(0, 0);
        } else if (id == R.id.btn_revert) {
            mGameView.revertGame();
        } else if (id == R.id.btn_option) {
            Intent intent = new Intent(Game2048Activity.this, ConfigActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mGoal = mSp.getInt(KEY_GAME_GOAL, 2048);
            mTvGoal.setText("" + mGoal);
            getHighScore();
            mGameView.startGame();
        }
    }

    /**
     * 获取最高记录
     */
    private void getHighScore() {
        int score = mSp.getInt(KEY_HIGH_SCROE, 0);
        setScore(score, 1);
    }
}
