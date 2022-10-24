/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.winjay.practice.aidl;

/**
 * Binder工作机制
 * 1.当客户端发起远程请求时，由于当前线程会被挂起直至服务端进程返回数据，所以如果一个方法是很耗时的，
 * 那么不能在UI线程中发起此远程请求；
 * 2.由于服务端的 Binder 方法运行在 Binder 的线程池中，所以 Binder 方法不管是否耗时都应该采用同步
 * 的方式去实现，因为它已经运行在一个线程中了。
 *
 * @author Winjay
 * @date 2022-10-20
 */
public interface IBookManager_study extends android.os.IInterface {
    /**
     * Default implementation for IBookManager.
     */
    public static class Default implements com.winjay.practice.aidl.IBookManager_study {
        @Override
        public java.util.List<com.winjay.practice.aidl.Book> getBookList() throws android.os.RemoteException {
            return null;
        }

        @Override
        public void addBook(com.winjay.practice.aidl.Book book) throws android.os.RemoteException {
        }

        @Override
        public android.os.IBinder asBinder() {
            return null;
        }
    }

    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements com.winjay.practice.aidl.IBookManager_study {
        // Binder的唯一标识，一般用当前 Binder 的类名表示
        private static final java.lang.String DESCRIPTOR = "com.winjay.practice.aidl.IBookManager";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an com.winjay.practice.aidl.IBookManager interface,
         * generating a proxy if needed.
         * 将服务端的 Binder 对象转换成客户端所需的 AIDL 接口类型的对象
         */
        public static com.winjay.practice.aidl.IBookManager_study asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            // 如果客户端和服务端是在同一个进程中，那么此方法返回的就是服务端的 Stub 对象本身
            if (((iin != null) && (iin instanceof com.winjay.practice.aidl.IBookManager))) {
                return ((com.winjay.practice.aidl.IBookManager_study) iin);
            }
            // 如果客户端和服务端不在同一个进程中，则返回系统封装后的 Stub.Proxy 对象
            return new com.winjay.practice.aidl.IBookManager_study.Stub.Proxy(obj);
        }

        /**
         * 返回当前 Binder 对象
         *
         * @return 当前 Binder 对象
         */
        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        /**
         * 运行在服务端的 Binder 线程池中，当客户端发起跨进程请求时，远程请求会通过系统底层封装后交由此方法处理
         *
         * @param code  Stub 中声明的方法id，在客户端发起请求时会带上
         * @param data  客户端发起请求时携带的序列化数据
         * @param reply 用来给写入返回值的（如果目标方法有返回值）
         * @param flags 0
         * @return 返回客户端请求结果 true or false
         * @throws android.os.RemoteException
         */
        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            java.lang.String descriptor = DESCRIPTOR;
            // 根据客户端请求时传入的方法id来判断请求服务端的哪个方法
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_getBookList: {
                    data.enforceInterface(descriptor);
                    // 调用服务端的已经实现的方法
                    java.util.List<com.winjay.practice.aidl.Book> _result = this.getBookList();
                    reply.writeNoException();
                    // 向客户端写入方法返回结果
                    reply.writeTypedList(_result);
                    return true;
                }
                case TRANSACTION_addBook: {
                    data.enforceInterface(descriptor);
                    com.winjay.practice.aidl.Book _arg0;
                    if ((0 != data.readInt())) {
                        // 得到客户端传入的序列化数据后，服务端进行反序列化
                        _arg0 = com.winjay.practice.aidl.Book.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    // 调用服务端的已经实现的方法
                    this.addBook(_arg0);
                    reply.writeNoException();
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        /**
         * 运行在客户端
         */
        private static class Proxy implements com.winjay.practice.aidl.IBookManager_study {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public java.util.List<com.winjay.practice.aidl.Book> getBookList() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                java.util.List<com.winjay.practice.aidl.Book> _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    // 发起 RPC（远程过程调用）请求，同时当前线程挂起；然后服务端的 onTransact 方法会被调用，
                    // 直到 RPC 过程返回后，当前线程继续执行。
                    boolean _status = mRemote.transact(Stub.TRANSACTION_getBookList, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().getBookList();
                    }
                    _reply.readException();
                    // 从 _reply 中取出 RPC 过程的返回结果，并将其反序列化
                    _result = _reply.createTypedArrayList(com.winjay.practice.aidl.Book.CREATOR);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                // 最后返回 _reply 中的数据
                return _result;
            }

            @Override
            public void addBook(com.winjay.practice.aidl.Book book) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((book != null)) {
                        _data.writeInt(1);
                        // 客户端将数据序列化
                        book.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    // 发起 RPC（远程过程调用）请求，同时当前线程挂起；然后服务端的 onTransact 方法会被调用，
                    // 直到 RPC 过程返回后，当前线程继续执行。
                    boolean _status = mRemote.transact(Stub.TRANSACTION_addBook, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().addBook(book);
                        return;
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public static com.winjay.practice.aidl.IBookManager sDefaultImpl;
        }

        // 用于标识方法的 code
        static final int TRANSACTION_getBookList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_addBook = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);

        public static boolean setDefaultImpl(com.winjay.practice.aidl.IBookManager impl) {
            // Only one user of this interface can use this function
            // at a time. This is a heuristic to detect if two different
            // users in the same process use this function.
            if (Stub.Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Stub.Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static com.winjay.practice.aidl.IBookManager getDefaultImpl() {
            return Stub.Proxy.sDefaultImpl;
        }
    }

    public java.util.List<com.winjay.practice.aidl.Book> getBookList() throws android.os.RemoteException;

    public void addBook(com.winjay.practice.aidl.Book book) throws android.os.RemoteException;
}
