package com.winjay.practice.ipc.binder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;

import androidx.annotation.Nullable;

import com.winjay.practice.aidl.Book;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.databinding.ActivityBinderStudyBinding;
import com.winjay.practice.utils.LogUtil;

import java.util.List;

/**
 * Binder 手写学习（客户端）
 *
 * @author Winjay
 * @date 2022-10-20
 */
public class BinderStudyClientActivity extends BaseActivity {
    private static final String TAG = BinderStudyClientActivity.class.getSimpleName();
    private ActivityBinderStudyBinding binding;
    private IBinderStudy binderStudy;

    @Override
    public boolean useViewBinding() {
        return true;
    }

    @Override
    public View viewBinding() {
        binding = ActivityBinderStudyBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.bindService.setOnClickListener(v -> {
            if (binderStudy == null) {
                Intent intent = new Intent(this, BinderStudyService.class);
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        });

        binding.addDataBtn.setOnClickListener(v -> {
            if (binderStudy != null) {
                LogUtil.d(TAG, "addData()");
                try {
                    BinderStudyBean bean = new BinderStudyBean("Winjay3", 3);
                    binderStudy.addData(bean);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        binding.getDataBtn.setOnClickListener(v -> {
            if (binderStudy != null) {
                try {
                    List<BinderStudyBean> listData = binderStudy.getData();
                    for (BinderStudyBean bean : listData) {
                        LogUtil.d(TAG, "name=" + bean.getName() + ", age=" + bean.getAge());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d(TAG, "componentName=" + name.getClassName());
            binderStudy = IBinderStudy.IBinderStudyImpl.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d(TAG, "componentName=" + name.getClassName());
            binderStudy = null;
        }
    };
}
