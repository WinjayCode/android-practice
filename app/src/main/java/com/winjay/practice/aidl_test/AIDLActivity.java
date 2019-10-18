package com.winjay.practice.aidl_test;

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

import java.util.ArrayList;
import java.util.List;

/**
 * AIDL练习
 *
 * @author Winjay
 * @date 2019-09-04
 */
public class AIDLActivity extends BaseActivity {
    private IBookManager bookManager;

    @Override
    protected int getLayoutId() {
        return R.layout.aidl_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.bind_service).setOnClickListener(view -> {
            if (bookManager == null) {
                bindBookManagerService();
            }
        });
        findViewById(R.id.add_book).setOnClickListener(view -> addBook(new Book(1, "书1")));
        findViewById(R.id.get_book_list).setOnClickListener(view -> getBookList());
    }

    private void bindBookManagerService() {
        Intent intent = new Intent("aidl_server.service.BookManagerService");
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bookManager = IBookManager.Stub.asInterface(iBinder);
            // 设置死亡代理
            try {
                iBinder.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bookManager = null;
        }
    };

    private void addBook(Book book) {

    }

    private List<Book> getBookList() {
        List<Book> list = new ArrayList<>();
        return list;
    }

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (bookManager == null) {
                return;
            }
            bookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            bookManager = null;
            // 重新绑定远程Service
            bindBookManagerService();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}
