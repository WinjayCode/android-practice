package com.winjay.practice.architecture_mode.mvvm.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.R;
import com.winjay.practice.architecture_mode.mvvm.model.MVVMDataBean;
import com.winjay.practice.architecture_mode.mvvm.viewmodel.MVVMViewModel;
import com.winjay.practice.utils.JsonUtil;
import com.winjay.practice.utils.LogUtil;

import java.util.List;

/**
 * MVVM 的View层（含xml）
 */
public class MVVMActivity extends AppCompatActivity {
    private static final String TAG = "UserListActivity";
    private MVVMViewModel mUserListViewModel;
    private ProgressBar mProgressBar;
    private RecyclerView mRvUserList;
    private MVVMAdapter mUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG);

        setContentView(R.layout.mvvm_activity);

        initView();

        initViewModel();

        getData();

        observeLivaData();
    }

    private void initView() {
        mProgressBar = findViewById(R.id.pb_loading_users);
        mRvUserList = findViewById(R.id.rv_user_list);

        mRvUserList.setLayoutManager(new LinearLayoutManager(this));

    }

    private void initViewModel() {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        // 即使Activity被重新创建时，ViewModel对象却始终是相同的
        mUserListViewModel = viewModelProvider.get(MVVMViewModel.class);
        LogUtil.d(TAG, "mUserListViewModel=" + mUserListViewModel.toString());
    }

    /**
     * 获取数据，调用ViewModel的方法获取
     */
    private void getData() {
        mUserListViewModel.getUserInfo();
    }

    /**
     * 观察ViewModel的数据，且此数据 是 View 直接需要的，不需要再做逻辑处理
     */
    private void observeLivaData() {
        mUserListViewModel.getUserListLiveData().observe(this, new Observer<List<MVVMDataBean>>() {
            @Override
            public void onChanged(List<MVVMDataBean> users) {
                LogUtil.d(TAG, "users=" + JsonUtil.getInstance().toJson(users));
                if (users == null) {
                    Toast.makeText(MVVMActivity.this, "获取user失败！", Toast.LENGTH_SHORT).show();
                    return;
                }
                mUserAdapter = new MVVMAdapter(users);
                mRvUserList.setAdapter(mUserAdapter);
            }
        });

        mUserListViewModel.getLoadingLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                LogUtil.d(TAG, "isLoading=" + aBoolean);
                mProgressBar.setVisibility(aBoolean? View.VISIBLE:View.GONE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);
    }
}
