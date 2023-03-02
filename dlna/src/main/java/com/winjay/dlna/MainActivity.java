package com.winjay.dlna;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.winjay.dlna.cast.activity.StartActivity;
import com.winjay.dlna.cast.application.BaseApplication;
import com.winjay.dlna.databinding.ActivityMainBinding;
import com.winjay.dlna.datatransfer.FileTransfer;
import com.winjay.dlna.datatransfer.ReceiveFileService;
import com.winjay.dlna.datatransfer.SendFileTask;
import com.winjay.dlna.selectfile.SelectFileActivity;
import com.winjay.dlna.util.LogUtil;
import com.winjay.dlna.util.NetUtil;
import com.winjay.dlna.wifidirect.DeviceAdapter;
import com.winjay.dlna.wifidirect.DirectActionListener;
import com.winjay.dlna.wifidirect.DirectBroadcastReceiver;
import com.winjay.dlna.wifidirect.LoadingDialog;
import com.winjay.dlna.wifidirect.WifiP2pUtils;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Winjay
 * @date 2022-09-09
 */
public class MainActivity extends com.winjay.dlna.BaseActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;

    private WifiManager wifiManager;
    private WifiP2pManager wifiP2pManager;
    // 当前设备channel
    private WifiP2pManager.Channel channel;
    // 当前设备信息
    private WifiP2pInfo mWifiP2pInfo;
    // 设备列表
    private List<WifiP2pDevice> wifiP2pDeviceList;
    // 连接的设备
    private WifiP2pDevice mWifiP2pDevice;

    private DeviceAdapter deviceAdapter;
    private BroadcastReceiver broadcastReceiver;
    private boolean wifiP2pEnabled = false;
    private LoadingDialog loadingDialog;
    private ProgressDialog progressDialog;
    private ReceiveFileService receiveFileService;
    private boolean isReceiveFileServiceAlive;

    public static MutableLiveData<List<String>> mGCIPAddressList = new MutableLiveData<>();
    private com.winjay.dlna.ServerThread serverThread;

    private List<String> selectedGCIPAddressList = new ArrayList<>();

    private final DirectActionListener directActionListener = new DirectActionListener() {

        @Override
        public void wifiP2pEnabled(boolean enabled) {
            LogUtil.d(TAG, "wifiP2pEnabled=" + enabled);
            wifiP2pEnabled = enabled;
            if (enabled) {
//                createGroup();
                search();
            }
        }

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            dismissLoadingDialog();
            wifiP2pDeviceList.clear();
            deviceAdapter.notifyDataSetChanged();
            LogUtil.d(TAG, "groupFormed: " + wifiP2pInfo.groupFormed);
            LogUtil.d(TAG, "isGroupOwner: " + wifiP2pInfo.isGroupOwner);
            LogUtil.d(TAG, "getHostAddress: " + wifiP2pInfo.groupOwnerAddress.getHostAddress());
            StringBuilder stringBuilder = new StringBuilder();
            if (mWifiP2pDevice != null) {
                stringBuilder.append("连接的设备名：");
                stringBuilder.append(mWifiP2pDevice.deviceName);
                stringBuilder.append("\n");
                stringBuilder.append("连接的设备的地址：");
                stringBuilder.append(mWifiP2pDevice.deviceAddress);
            }

//            if (TextUtils.isEmpty(binding.tvStatus.getText().toString())
//                    || !binding.tvStatus.getText().toString().contains("群员IP地址")) {
                stringBuilder.append("\n");
                stringBuilder.append("是否群主：");
                stringBuilder.append(wifiP2pInfo.isGroupOwner ? "是群主" : "非群主");
                stringBuilder.append("\n");
                stringBuilder.append("群主IP地址：");
                stringBuilder.append(wifiP2pInfo.groupOwnerAddress.getHostAddress());
                binding.tvStatus.setText(stringBuilder);
//            }

            mWifiP2pInfo = wifiP2pInfo;

            if (receiveFileService != null) {
                LogUtil.d(TAG, "start ReceiveFileService.");
                startService(new Intent(MainActivity.this, ReceiveFileService.class));
            }

            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                setApplicationInetAddress(wifiP2pInfo.groupOwnerAddress);
            }
            if (wifiP2pInfo.groupFormed && !wifiP2pInfo.isGroupOwner) {
                InetAddress inetAddress = NetUtil.getP2PInetAddress();
                if (inetAddress != null) {
                    setApplicationInetAddress(inetAddress);

                    LogUtil.d(TAG, "show gc ip address:" + inetAddress.getHostAddress());
                    binding.gcInfo.setText(binding.gcInfo.getText().toString()
                            + "群员IP地址："
                            + inetAddress.getHostAddress() + "\n");
                }
                // send ip to GO
                sendGCIP2GO();
            }
        }

        @Override
        public void onDisconnection() {
            LogUtil.d(TAG);
            toast("设备连接断开！");
            wifiP2pDeviceList.clear();
            deviceAdapter.notifyDataSetChanged();
            binding.tvStatus.setText(null);
            binding.gcInfo.setText(null);
            mWifiP2pInfo = null;

            sendBroadcast(new Intent(Constants.ACTION_DEVICE_DISCONNECTED));

            search();
        }

        @Override
        public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {
            LogUtil.d(TAG, "current deviceName: " + wifiP2pDevice.deviceName);
            LogUtil.d(TAG, "current deviceAddress: " + wifiP2pDevice.deviceAddress);
            LogUtil.d(TAG, "current status: " + wifiP2pDevice.status);
            binding.tvMyDeviceName.setText(wifiP2pDevice.deviceName);
            binding.tvMyDeviceAddress.setText(wifiP2pDevice.deviceAddress);
            binding.tvMyDeviceStatus.setText(WifiP2pUtils.getDeviceStatus(wifiP2pDevice.status));
        }

        @Override
        public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList) {
            LogUtil.d(TAG, "wifiP2pDeviceList :" + wifiP2pDeviceList.size());
            MainActivity.this.wifiP2pDeviceList.clear();
            MainActivity.this.wifiP2pDeviceList.addAll(wifiP2pDeviceList);
            deviceAdapter.notifyDataSetChanged();
            loadingDialog.cancel();
        }

        @Override
        public void onChannelDisconnected() {
            LogUtil.d(TAG);
        }
    };

    private void sendGCIP2GO() {
        LogUtil.d(TAG);
        new com.winjay.dlna.ClientThread(mWifiP2pInfo.groupOwnerAddress.getHostAddress()).start();
    }

    private void setApplicationInetAddress(InetAddress inetAddress) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BaseApplication.setLocalIpAddress(inetAddress);
                BaseApplication.setHostName(inetAddress.getHostName());
                BaseApplication.setHostAddress(inetAddress.getHostAddress());
            }
        }).start();
    }

    @Override
    protected String[] permissions() {
        return new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    }

    @Override
    protected View viewBinding() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("选择连接设备");
        initView();
        initEvent();
        openWifi();
        bindReceiveService();

        serverThread = new com.winjay.dlna.ServerThread();
        serverThread.start();

        mGCIPAddressList.observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> addressList) {
                LogUtil.d(TAG, "receive GC IP list=" + Arrays.toString(addressList.toArray()));
                binding.gcInfo.setText(null);
                for (String address : addressList) {
                    binding.gcInfo.setText(binding.gcInfo.getText().toString() + "群员IP地址：" + address + "\n");
                }
            }
        });
    }

    private void openWifi() {
        toast("正在打开WIFI");
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    private void initView() {
        loadingDialog = new LoadingDialog(this);
        wifiP2pDeviceList = new ArrayList<>();
        deviceAdapter = new DeviceAdapter(wifiP2pDeviceList);
        deviceAdapter.setClickListener(position -> {
            mWifiP2pDevice = wifiP2pDeviceList.get(position);
//            toast(mWifiP2pDevice.deviceName);
            connect();
        });
        binding.rvDeviceList.setAdapter(deviceAdapter);
        binding.rvDeviceList.setLayoutManager(new LinearLayoutManager(this));

        // receive view
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("正在接收文件");
        progressDialog.setMax(100);
    }

    private void initEvent() {
        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        if (wifiP2pManager == null) {
            finish();
            return;
        }
        channel = wifiP2pManager.initialize(this, getMainLooper(), directActionListener);
        broadcastReceiver = new DirectBroadcastReceiver(wifiP2pManager, channel, directActionListener);
        registerReceiver(broadcastReceiver, DirectBroadcastReceiver.getIntentFilter());
    }

    private void connect() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            toast("请先授予位置权限");
            return;
        }
        WifiP2pConfig config = new WifiP2pConfig();
        if (config.deviceAddress != null && mWifiP2pDevice != null) {
            config.deviceAddress = mWifiP2pDevice.deviceAddress;
            config.wps.setup = WpsInfo.PBC;
            showLoadingDialog("正在连接 " + mWifiP2pDevice.deviceName);
            wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    LogUtil.d(TAG, "wifi p2p connect success.");
                    toast("连接成功");
                    dismissLoadingDialog();
                }

                @Override
                public void onFailure(int reason) {
                    LogUtil.d(TAG, "connect onFailure=" + reason);
                    toast("连接失败：" + reason);
                    dismissLoadingDialog();
                }
            });
        }
    }

    private void disconnect() {
        LogUtil.d(TAG);
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                LogUtil.e(TAG, "removeGroup onFailure:" + reasonCode);
            }

            @Override
            public void onSuccess() {
                LogUtil.d(TAG, "disconnect onSuccess");
                binding.tvStatus.setText(null);
                binding.gcInfo.setText("");
                mGCIPAddressList.postValue(new ArrayList<>());
            }
        });
    }

    private void search() {
        LogUtil.d(TAG);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            toast("请先授予位置权限");
            return;
        }
        if (!wifiP2pEnabled) {
            toast("需要先打开Wifi");
            return;
        }
        loadingDialog.show("正在搜索附近设备", true, false);
        wifiP2pDeviceList.clear();
        deviceAdapter.notifyDataSetChanged();
        //搜寻附近带有 Wi-Fi P2P 的设备
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
//                toast("搜索完成");
            }

            @Override
            public void onFailure(int reasonCode) {
                toast("搜索失败");
                loadingDialog.cancel();
            }
        });
    }

    /**
     * 默认自己为GO
     */
    private void createGroup() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            toast("请先授予位置权限");
            return;
        }
        wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                LogUtil.d(TAG, "createGroup success!");
            }

            @Override
            public void onFailure(int i) {
                LogUtil.w(TAG, "createGroup failure!");
            }
        });
    }

    private void bindReceiveService() {
        Intent intent = new Intent(MainActivity.this, ReceiveFileService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ReceiveFileService.ReceiveFileBinder binder = (ReceiveFileService.ReceiveFileBinder) service;
            receiveFileService = binder.getService();
            receiveFileService.setProgressChangListener(progressChangListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (receiveFileService != null) {
                receiveFileService.setProgressChangListener(null);
                receiveFileService = null;
            }
            bindReceiveService();
        }
    };

    private final ReceiveFileService.OnProgressChangListener progressChangListener = new ReceiveFileService.OnProgressChangListener() {
        @Override
        public void onProgressChanged(final FileTransfer fileTransfer, final int progress) {
            runOnUiThread(() -> {
                progressDialog.setMessage("文件名： " + fileTransfer.getFileName());
                progressDialog.setProgress(progress);
                progressDialog.show();
            });
        }

        @Override
        public void onTransferFinished(final File file) {
            runOnUiThread(() -> {
                progressDialog.cancel();
                toast("文件接收完成！");
            });
        }
    };

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        menu.add(0, 0, 0, "打开WIFI");
        menu.add(0, 1, 0, "搜索");
        menu.add(0, 2, 0, "断开连接");
        menu.add(0, 3, 0, "传输文件");
        menu.add(0, 4, 0, "投屏");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                if (wifiP2pManager != null && channel != null) {
                    startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                } else {
                    toast("当前设备不支持Wifi Direct!");
                }
                return true;
            case 1:
                search();
                return true;
            case 2:
                disconnect();
                break;
            case 3:
                if (mWifiP2pInfo != null && mWifiP2pInfo.groupFormed && mWifiP2pInfo.isGroupOwner) {
                    if (mGCIPAddressList.getValue() == null || mGCIPAddressList.getValue().isEmpty()) {
                        toast("未收到已连接设备IP地址！");
                        break;
                    } else if (mGCIPAddressList.getValue().size() > 1) {
                        showMultiDeviceChoiceView();
                    } else {
                        selectFile();
                    }
                } else {
                    toast("未连接设备！");
                }
                break;
            case 4:
                startActivity(new Intent(MainActivity.this, StartActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectFile() {
        // 系统文件管理器
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        //设置类型，我这里是任意类型，任意后缀的可以这样写。
//        //intent.setType(“image/*”);//选择图片
//        //intent.setType(“audio/*”); //选择音频
//        //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
//        //intent.setType(“video/*;image/*”);//同时选择视频和图片
//        intent.setType("*/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(intent, 1);

        // 自定义文件管理器
        Intent intent = new Intent(this, SelectFileActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                Uri uri = data.getData();
//                LogUtil.d(TAG, "文件路径：" + uri);
//                if (mWifiP2pInfo != null) {
//                    // GO to GC
//                    if (mWifiP2pInfo.groupFormed && mWifiP2pInfo.isGroupOwner && !TextUtils.isEmpty(mGCIPAddress)) {
//                        LogUtil.d(TAG, "send file form GO to GC.");
//                        new SendFileTask(this).execute(mGCIPAddress, uri);
//                    }
//                    // GC to GO
//                    if (mWifiP2pInfo.groupFormed && !mWifiP2pInfo.isGroupOwner) {
//                        LogUtil.d(TAG, "send file form GC to GO.");
//                        new SendFileTask(this).execute(mWifiP2pInfo.groupOwnerAddress.getHostAddress(), uri);
//                    }
//                }
//            }
//        }

        if (data == null)
            return;
        String select = data.getStringExtra("select");
        if (mWifiP2pInfo != null) {
            for (String selectedGCIPAddress : selectedGCIPAddressList) {
                // GO to GC
                if (mWifiP2pInfo.groupFormed && mWifiP2pInfo.isGroupOwner) {
                    LogUtil.d(TAG, "send file form GO to GC.");
                    new SendFileTask(this).execute(selectedGCIPAddress, select);
                }
            }
            // GC to GO
            if (mWifiP2pInfo.groupFormed && !mWifiP2pInfo.isGroupOwner) {
                LogUtil.d(TAG, "send file form GC to GO.");
                new SendFileTask(this).execute(mWifiP2pInfo.groupOwnerAddress.getHostAddress(), select);
            }
        }
    }

    private void showMultiDeviceChoiceView() {
        selectedGCIPAddressList.clear();
        if (mGCIPAddressList.getValue() == null) {
            return;
        }
        String[] items = mGCIPAddressList.getValue().toArray(new String[0]);
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("选择需要传输文件的群员");
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedGCIPAddressList.clear();
            }
        });
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!selectedGCIPAddressList.isEmpty()) {
                    selectFile();
                }
            }
        });
        dialog.setCancelable(false);
        dialog.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                LogUtil.d(TAG, "which=" + which);
                LogUtil.d(TAG, "isChecked=" + isChecked);
                if (isChecked) {
                    selectedGCIPAddressList.add(mGCIPAddressList.getValue().get(which));
                } else {
                    selectedGCIPAddressList.remove(mGCIPAddressList.getValue().get(which));
                }
            }
        }).create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        disconnect();
        if (receiveFileService != null) {
            receiveFileService.setProgressChangListener(null);
            unbindService(serviceConnection);
        }
        stopService(new Intent(this, ReceiveFileService.class));

        serverThread.close();
    }
}
