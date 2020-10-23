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

import butterknife.OnClick;

/**
 * IPC-AIDL-客户端
 *
 * @author Winjay
 * @date 2020-02-21
 */
public class BookManagerActivity extends BaseActivity {
    private static final String TAG = BookManagerActivity.class.getSimpleName();
    private IBookManager bookManager;

    @Override
    protected int getLayoutId() {
        return R.layout.ipc_activity_aidl;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void bindBookManagerService() {
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @OnClick(R.id.bind_service)
    void bindService() {
        if (bookManager == null) {
            bindBookManagerService();
        }
    }

    @OnClick(R.id.add_book)
    void addBook() {
        if (bookManager != null) {
            try {
                Book newBook = new Book(3, "Android开发艺术探索");
                bookManager.addBook(newBook);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.get_book_list)
    void getBookList() {
        if (bookManager != null) {
            try {
                List<Book> list = bookManager.getBookList();
                for (Book book : list) {
                    LogUtil.d(TAG, "bookName=" + book.bookName + ", bookId=" + book.bookId);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d(TAG, "onServiceConnected:name=" + name.getClassName());
            bookManager = IBookManager.Stub.asInterface(service);
            // 设置死亡代理
            try {
                service.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d(TAG, "onServiceDisconnected:name=" + name.getClassName());
            bookManager = null;
        }

        @Override
        public void onBindingDied(ComponentName name) {
            LogUtil.d(TAG, "onBindingDied:name=" + name.getClassName());
        }
    };

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
        bookManager = null;
    }
}
