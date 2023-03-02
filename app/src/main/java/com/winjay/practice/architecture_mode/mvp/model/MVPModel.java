package com.winjay.practice.architecture_mode.mvp.model;

import android.os.AsyncTask;

import com.winjay.practice.utils.LogUtil;

/**
 * MVP中的Model层
 *
 * @author Winjay
 * @date 2020-01-13
 */
public class MVPModel implements IMVPModel {
    private static final String TAG = "MVPModel";

    @Override
    public void getData(GetDataListener getDataListener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                LogUtil.d(TAG);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "这是MVP架构的数据!";
            }

            @Override
            protected void onPostExecute(String s) {
                LogUtil.d(TAG, "s=" + s);
                getDataListener.onComplete(s);
            }
        }.execute();
    }
}
