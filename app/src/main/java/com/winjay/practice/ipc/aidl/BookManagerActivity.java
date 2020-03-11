package com.winjay.practice.ipc.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.aidl.Book;
import com.winjay.practice.aidl.IBookManager;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;

import java.util.List;

/**
 * IPC-AIDL-客户端
 *
 * @author Winjay
 * @date 2020-02-21
 */
public class BookManagerActivity extends BaseActivity {
    private static final String TAG = BookManagerActivity.class.getSimpleName();

    @Override
    protected int getLayoutId() {
        return R.layout.ipc_activity_aidl;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IBookManager bookManager = IBookManager.Stub.asInterface(service);

            try {
                List<Book> list = bookManager.getBookList();
                LogUtil.d(TAG, "query book list, list type:" + list.getClass().getCanonicalName());
                LogUtil.d(TAG, "query book list:" + list.toString());

                Book newBook = new Book(3, "Android开发艺术探索");
                bookManager.addBook(newBook);
                LogUtil.d(TAG, "add book:" + newBook);
                List<Book> newList = bookManager.getBookList();
                LogUtil.d(TAG, "query book list:" + newList.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }
}
