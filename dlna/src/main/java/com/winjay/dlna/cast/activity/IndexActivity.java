package com.winjay.dlna.cast.activity;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import com.winjay.dlna.Constants;
import com.winjay.dlna.R;
import com.winjay.dlna.util.LogUtil;


@SuppressWarnings("deprecation")
public class IndexActivity extends TabActivity {
    private static final String TAG = "IndexActivity";
    public static TabHost mTabHost;

    private static RadioButton mDeviceRb;

    private static RadioButton mContentRb;

    private static RadioButton mControlRb;

    private static RadioButton mSettingsRb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        findViews();
        registerBroadcastReceiver();
    }

    private void findViews() {

        mDeviceRb = (RadioButton) findViewById(R.id.main_tab_devices);
        mContentRb = (RadioButton) findViewById(R.id.main_tab_content);
        mControlRb = (RadioButton) findViewById(R.id.main_tab_control);
        mSettingsRb = (RadioButton) findViewById(R.id.main_tab_settings);

        mTabHost = this.getTabHost();

        TabHost.TabSpec spec;
        Intent intent;

        intent = new Intent().setClass(this, DevicesActivity.class);
        spec = mTabHost.newTabSpec(getString(R.string.device)).setIndicator(getString(R.string.device)).setContent(intent);
        mTabHost.addTab(spec);

        intent = new Intent().setClass(this, ContentActivity.class);
        spec = mTabHost.newTabSpec(getString(R.string.content)).setIndicator(getString(R.string.content)).setContent(intent);
        mTabHost.addTab(spec);

        intent = new Intent().setClass(this, ControlActivity.class);
        spec = mTabHost.newTabSpec(getString(R.string.control)).setIndicator(getString(R.string.control)).setContent(intent);
        mTabHost.addTab(spec);

        intent = new Intent().setClass(this, SettingActivity.class);
        spec = mTabHost.newTabSpec(getString(R.string.setting)).setIndicator(getString(R.string.setting)).setContent(intent);
        mTabHost.addTab(spec);

        mTabHost.setCurrentTab(0);

        RadioGroup radioGroup = (RadioGroup) this.findViewById(R.id.main_tab_group);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (checkedId == R.id.main_tab_devices) {
                    mTabHost.setCurrentTabByTag(getString(R.string.device));
                } else if (checkedId == R.id.main_tab_content) {
                    mTabHost.setCurrentTabByTag(getString(R.string.content));
                } else if (checkedId == R.id.main_tab_control) {
                    mTabHost.setCurrentTabByTag(getString(R.string.control));
                } else if (checkedId == R.id.main_tab_settings) {
                    mTabHost.setCurrentTabByTag(getString(R.string.setting));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, R.string.menu_exit).setIcon(
                android.R.drawable.ic_menu_close_clear_cancel);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                finish();
//			System.exit(0);
                break;
        }
        return false;
    }

    public static void setSelect() {
        mControlRb.setChecked(true);
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_DEVICE_DISCONNECTED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void unregisterBroadcastReceiver() {
        unregisterReceiver(broadcastReceiver);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.ACTION_DEVICE_DISCONNECTED.equals(intent.getAction())) {
                LogUtil.d(TAG, "finish activity because device disconnected.");
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceiver();
    }
}
