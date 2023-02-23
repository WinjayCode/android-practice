package com.winjay.practice.ipc.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.winjay.practice.aidl.Book;
import com.winjay.practice.aidl.IBookManager;
import com.winjay.practice.utils.JsonUtil;
import com.winjay.practice.utils.LogUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * IPC-AIDL-服务端
 *
 * @author Winjay
 * @date 2020-02-21
 */
public class BookManagerService extends Service {
    private static final String TAG = BookManagerService.class.getSimpleName();

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    private final Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            LogUtil.d(TAG, "BookList.size=" + mBookList.size());
            return mBookList;
        }

        @Override
        public int getSize() throws RemoteException {
            return mBookList.size();
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            if (book != null) {
                LogUtil.d(TAG, "book=" + JsonUtil.getInstance().toJson(book));
                mBookList.add(book);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG);
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "IOS"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
