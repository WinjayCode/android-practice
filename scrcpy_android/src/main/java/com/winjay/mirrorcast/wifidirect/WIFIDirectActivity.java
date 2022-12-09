package com.winjay.mirrorcast.wifidirect;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.winjay.mirrorcast.AppApplication;
import com.winjay.mirrorcast.BaseActivity;
import com.winjay.mirrorcast.MainActivity;
import com.winjay.mirrorcast.databinding.ActivityWifiDirectBinding;
import com.winjay.mirrorcast.util.LogUtil;
import com.winjay.mirrorcast.util.NetUtil;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author F2848777
 * @date 2022-11-09
 */
public class WIFIDirectActivity extends BaseActivity {
    private static final String TAG = WIFIDirectActivity.class.getSimpleName();

    private ActivityWifiDirectBinding binding;

    private WifiManager wifiManager;
    private WifiP2pManager wifiP2pManager;
    // 当前设备channel
    private WifiP2pManager.Channel channel;
    // 当前设备信息
    private WifiP2pInfo mWifiP2pInfo;
    // 设备列表
    private List<WifiP2pDevice> mWifiP2pDeviceList;
    // 连接的设备
    private WifiP2pDevice mWifiP2pDevice;

    private DeviceAdapter deviceAdapter;
    private BroadcastReceiver broadcastReceiver;
    private boolean wifiP2pEnabled = false;
    private ProgressDialog progressDialog;

    public static MutableLiveData<List<String>> mGCIPAddressList = new MutableLiveData<>();

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
            mWifiP2pDeviceList.clear();
            deviceAdapter.notifyDataSetChanged();
            LogUtil.d(TAG, "groupFormed: " + wifiP2pInfo.groupFormed);
            LogUtil.d(TAG, "isGroupOwner: " + wifiP2pInfo.isGroupOwner);
            if (wifiP2pInfo.groupOwnerAddress == null) {
                return;
            }
            LogUtil.d(TAG, "getHostAddress: " + wifiP2pInfo.groupOwnerAddress.getHostAddress());
            StringBuilder stringBuilder = new StringBuilder();
            if (mWifiP2pDevice != null) {
                stringBuilder.append("连接的设备名：");
                stringBuilder.append(mWifiP2pDevice.deviceName);
                stringBuilder.append("\n");
                stringBuilder.append("连接的设备的地址：");
                stringBuilder.append(mWifiP2pDevice.deviceAddress);
            }

            stringBuilder.append("\n");
            stringBuilder.append("是否群主：");
            stringBuilder.append(wifiP2pInfo.isGroupOwner ? "是群主" : "非群主");
            stringBuilder.append("\n");
            stringBuilder.append("群主IP地址：");
            stringBuilder.append(wifiP2pInfo.groupOwnerAddress.getHostAddress());
            binding.tvStatus.setText(stringBuilder);

            mWifiP2pInfo = wifiP2pInfo;

            if (!wifiP2pInfo.isGroupOwner) {
                AppApplication.destDeviceIp = wifiP2pInfo.groupOwnerAddress.getHostAddress();
            }


            if (wifiP2pInfo.groupFormed && !wifiP2pInfo.isGroupOwner) {
                InetAddress inetAddress = NetUtil.getP2PInetAddress();
                if (inetAddress != null) {
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
            mWifiP2pDeviceList.clear();
            deviceAdapter.notifyDataSetChanged();
            binding.tvStatus.setText(null);
            binding.gcInfo.setText(null);
            mWifiP2pInfo = null;

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
//            LogUtil.d(TAG, "wifiP2pDeviceList :" + wifiP2pDeviceList.size());
            mWifiP2pDeviceList.clear();
            mWifiP2pDeviceList.addAll(wifiP2pDeviceList);
            deviceAdapter.notifyDataSetChanged();
            cancelLoadingDialog();
        }

        @Override
        public void onChannelDisconnected() {
            LogUtil.d(TAG);
        }
    };

    private void sendGCIP2GO() {
        LogUtil.d(TAG);
        new ClientThread(mWifiP2pInfo.groupOwnerAddress.getHostAddress()).start();
    }

    @Override
    protected View viewBinding() {
        binding = ActivityWifiDirectBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        super.onCreate(savedInstanceState);
        setTitle("选择连接设备");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initView();
        initEvent();
        openWifi();

        mGCIPAddressList.observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> addressList) {
                LogUtil.d(TAG, "receive GC IP list=" + Arrays.toString(addressList.toArray()));
                binding.gcInfo.setText(null);
                for (String address : addressList) {
                    AppApplication.destDeviceIp = address;
                    binding.gcInfo.setText(binding.gcInfo.getText().toString() + "群员IP地址：" + address + "\n");
                }
            }
        });
    }

    private void openWifi() {
//        toast("正在打开WIFI");
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    private void initView() {
        mWifiP2pDeviceList = new ArrayList<>();
        deviceAdapter = new DeviceAdapter(mWifiP2pDeviceList);
        deviceAdapter.setClickListener(position -> {
            mWifiP2pDevice = mWifiP2pDeviceList.get(position);
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


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        wifiP2pManager.requestDeviceInfo(channel, new WifiP2pManager.DeviceInfoListener() {
//            @Override
//            public void onDeviceInfoAvailable(@Nullable WifiP2pDevice wifiP2pDevice) {
//                directActionListener.onSelfDeviceAvailable(wifiP2pDevice);
//            }
//        });
        wifiP2pManager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                if (wifiP2pInfo != null) {
                    directActionListener.onConnectionInfoAvailable(wifiP2pInfo);
                }
            }
        });
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
                AppApplication.destDeviceIp = "";
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
        showLoadingDialog("正在搜索附近设备");
        mWifiP2pDeviceList.clear();
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
                cancelLoadingDialog();
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

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        menu.add(0, 0, 0, "打开WIFI");
        menu.add(0, 1, 0, "搜索");
        menu.add(0, 2, 0, "断开连接");
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
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
//        disconnect();
    }
}
