package com.winjay.practice.architecture_mode.mvc.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.architecture_mode.mvc.model.MVCModel;

import butterknife.BindView;

/**
 * MVC中的View和Controller层
 *
 * @author Winjay
 * @date 2020-01-10
 */
public class MVCActivity extends BaseActivity {
    @BindView(R.id.mvc_btn)
    Button mMVCBtn;

    @BindView(R.id.mvc_data_tv)
    TextView mMVCTV;

    private MVCModel mMVCModel;

    @Override
    protected int getLayoutId() {
        return R.layout.mvc_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMVCModel = new MVCModel();
        mMVCBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMVCModel.getData(new MVCModel.GetDataListener() {
                    @Override
                    public void onComplete(String data) {
                        mMVCTV.setText(data);
                    }
                });
            }
        });
    }
}
