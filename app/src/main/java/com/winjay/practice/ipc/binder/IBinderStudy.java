package com.winjay.practice.ipc.binder;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 手动实现 Binder
 *
 * @author Winjay
 * @date 2022-10-20
 */
public interface IBinderStudy extends IInterface {
    public static final int TRANSACTION_addData = IBinder.FIRST_CALL_TRANSACTION + 0;
    public static final int TRANSACTION_getData = IBinder.FIRST_CALL_TRANSACTION + 1;

    public void addData(BinderStudyBean bean) throws RemoteException;

    public List<BinderStudyBean> getData() throws RemoteException;

    public abstract class IBinderStudyImpl extends Binder implements IBinderStudy {
        private static final String DESCRIPTION = "com.winjay.practice.ipc.binder.IBinderStudy";

        public IBinderStudyImpl() {
            attachInterface(this, DESCRIPTION);
        }

        public static IBinderStudy asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iInterface = obj.queryLocalInterface(DESCRIPTION);
            if (iInterface != null && iInterface instanceof IBinderStudy) {
                return (IBinderStudy) iInterface;
            }
            return new Proxy(obj);
        }

        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION:
                    reply.writeString(DESCRIPTION);
                    return true;
                case TRANSACTION_addData:
                    data.enforceInterface(DESCRIPTION);
                    BinderStudyBean bean;
                    if (0 != data.readInt()) {
                        bean = BinderStudyBean.CREATOR.createFromParcel(data);
                    } else {
                        bean = null;
                    }
                    addData(bean);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getData:
                    data.enforceInterface(DESCRIPTION);
                    List<BinderStudyBean> beanList = getData();
                    reply.writeNoException();
                    reply.writeTypedList(beanList);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        private static class Proxy implements IBinderStudy {
            IBinder mRemote;

            public Proxy(IBinder remote) {
                mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            @Override
            public void addData(BinderStudyBean bean) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTION);
                    if (bean != null) {
                        data.writeInt(1);
                        bean.writeToParcel(data, 0);
                    } else {
                        data.writeInt(0);
                    }
                    mRemote.transact(TRANSACTION_addData, data, reply, 0);
                    reply.readException();
                } finally {
                    data.recycle();
                    reply.recycle();
                }
            }

            @Override
            public List<BinderStudyBean> getData() throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                List<BinderStudyBean> beanList;
                try {
                    data.writeInterfaceToken(DESCRIPTION);
                    mRemote.transact(TRANSACTION_getData, data, reply, 0);
                    reply.readException();
                    beanList = reply.createTypedArrayList(BinderStudyBean.CREATOR);
                } finally {
                    data.recycle();
                    reply.recycle();
                }
                return beanList;
            }
        }
    }
}
