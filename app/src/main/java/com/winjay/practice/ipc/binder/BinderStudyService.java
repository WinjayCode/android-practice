package com.winjay.practice.ipc.binder;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.winjay.practice.utils.JsonUtil;
import com.winjay.practice.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Binder 手写学习（服务端）
 *
 * @author Winjay
 * @date 2022-10-20
 */
public class BinderStudyService extends Service {
    private static final String TAG = BinderStudyService.class.getSimpleName();
    private List<BinderStudyBean> beanList = new ArrayList<>();

    private Binder binderStudy = new IBinderStudy.IBinderStudyImpl() {
        @Override
        public void addData(BinderStudyBean bean) throws RemoteException {
            if (bean != null) {
                LogUtil.d(TAG, "bean=" + JsonUtil.getInstance().toJson(bean));
                beanList.add(bean);
            }
        }

        @Override
        public List<BinderStudyBean> getData() throws RemoteException {
            LogUtil.d(TAG, "size=" + beanList.size());
            return beanList;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        beanList.add(new BinderStudyBean("Winjay1", 1));
        beanList.add(new BinderStudyBean("Winjay2", 2));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binderStudy;
    }
}
