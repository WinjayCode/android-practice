package com.winjay.practice.architecture_mode.mvc.model;

import android.os.AsyncTask;

import com.winjay.practice.utils.LogUtil;

/**
 * MVC中的Model层
 *
 * @author Winjay
 * @date 2020-01-10
 */
public class MVCModel {
    private static final String TAG = "MVCModel";

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
                return "这是MVC架构的数据!";
            }

            @Override
            protected void onPostExecute(String s) {
                LogUtil.d(TAG, "s=" + s);
                getDataListener.onComplete(s);
            }
        }.execute();
    }

    public interface GetDataListener {
        void onComplete(String data);
    }
}
