package com.winjay.practice.architecture_mode.mvp.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.architecture_mode.mvp.presenter.MVPPresenter;
import com.winjay.practice.utils.LogUtil;

import butterknife.BindView;

/**
 * MVP中View层
 *
 * @author Winjay
 * @date 2020-01-10
 */
public class MVPActivity extends BaseActivity implements IMVPView {
    private static final String TAG = "MVPActivity";

    @BindView(R.id.mvp_btn)
    Button mMVPBtn;

    @BindView(R.id.mvp_data_tv)
    TextView mMVPTV;

    private MVPPresenter mMVPPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.mvp_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMVPPresenter = new MVPPresenter();
        mMVPPresenter.attachView(this);

        //MVP中使用Lifecycle
        getLifecycle().addObserver(mMVPPresenter);

        mMVPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMVPPresenter.getData();
            }
        });
    }

    @Override
    public void showData(String data) {
        LogUtil.d(TAG, "data=" + data);
        mMVPTV.setText(data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMVPPresenter.detachView();
    }
}
