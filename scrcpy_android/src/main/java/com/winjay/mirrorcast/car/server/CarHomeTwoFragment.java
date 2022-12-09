package com.winjay.mirrorcast.car.server;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import com.winjay.mirrorcast.BaseFragment;
import com.winjay.mirrorcast.Constants;
import com.winjay.mirrorcast.R;
import com.winjay.mirrorcast.app_mirror.AppSocketClientManager;
import com.winjay.mirrorcast.databinding.FragmentCarHomeTwoBinding;
import com.winjay.mirrorcast.util.ActivityListUtil;
import com.winjay.mirrorcast.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class CarHomeTwoFragment extends BaseFragment<FragmentCarHomeTwoBinding> {
    private static final String TAG = CarHomeTwoFragment.class.getSimpleName();

    private AppListAdapter mAppListAdapter;
    private List<AppBean> mAppListData;

    @Override
    protected FragmentCarHomeTwoBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentCarHomeTwoBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    public void onResume() {
        super.onResume();
        initAppItemView();
    }

    private void initAppItemView() {
        mAppListData = new ArrayList<>();

        AppBean navigationBean = new AppBean();
        navigationBean.setAppName("导航");
        navigationBean.setAppIcon(getResources().getDrawable(R.drawable.car_navigation_drawable));
        mAppListData.add(navigationBean);

        AppBean phoneBean = new AppBean();
        phoneBean.setAppName("通话");
        phoneBean.setAppIcon(getResources().getDrawable(R.drawable.car_phone_drawable));
        mAppListData.add(phoneBean);

        AppBean localMusic = new AppBean();
        localMusic.setAppName("本地音乐");
        localMusic.setAppIcon(getResources().getDrawable(R.drawable.local_music_drawable));
        mAppListData.add(localMusic);

        AppBean onlineRadio = new AppBean();
        onlineRadio.setAppName("在线电台");
        onlineRadio.setAppIcon(getResources().getDrawable(R.drawable.online_radio_drawable));
        mAppListData.add(onlineRadio);

        AppBean weatherBean = new AppBean();
        weatherBean.setAppName("天气");
        weatherBean.setAppIcon(getResources().getDrawable(R.drawable.weather_drawable));
        mAppListData.add(weatherBean);

        AppBean castBean = new AppBean();
        castBean.setAppName("镜像投屏");
        castBean.setAppIcon(getResources().getDrawable(R.drawable.cast_screen_drawable));
        mAppListData.add(castBean);

        AppBean appCastBean = new AppBean();
        appCastBean.setAppName("应用投屏");
        appCastBean.setAppIcon(getResources().getDrawable(R.drawable.app_list_drawable));
        mAppListData.add(appCastBean);

        AppBean settingBean = new AppBean();
        settingBean.setAppName("设置");
        settingBean.setAppIcon(getResources().getDrawable(R.drawable.setting_drawable));
        mAppListData.add(settingBean);

        AppBean returnBean = new AppBean();
        returnBean.setAppName("返回车机系统");
        returnBean.setAppIcon(getResources().getDrawable(R.drawable.return_car_system_drawable));
        mAppListData.add(returnBean);

        getBinding().appItemRv.setLayoutManager(new GridLayoutManager(getContext(), 4));
//        getBinding().appItemRv.addItemDecoration(new MyItemDecoration(4, 50, 10));
        mAppListAdapter = new AppListAdapter(getContext());
        getBinding().appItemRv.setAdapter(mAppListAdapter);
        mAppListAdapter.setOnItemClickListener(new AppListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AppBean appBean) {
                if (appBean.getAppName().equals("镜像投屏")) {
                    AppSocketClientManager.getInstance().sendMessage(Constants.APP_COMMAND_PHONE_MAIN_SCREEN_MIRROR_CAST);
                } else if (appBean.getAppName().equals("应用投屏")) {
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.content_rl, new CarAppFragment());
                    transaction.addToBackStack("CarAppFragment");
                    transaction.commit();
                } else if (appBean.getAppName().equals("返回车机系统")) {
                    LogUtil.d(TAG, "return car system.");
                    AppSocketClientManager.getInstance().sendMessage(Constants.APP_COMMAND_RETURN_CAR_SYSTEM);
                    // close TipsActivity and CarLauncherActivity
                    LogUtil.d(TAG, "activity count=" + ActivityListUtil.getActivityCount());
                    for (int i = ActivityListUtil.getActivityCount() - 2; i < ActivityListUtil.getActivityCount(); i++) {
                        if (i < 0) {
                            break;
                        }
                        if (ActivityListUtil.getActivityByIndex(i) != null) {
                            ActivityListUtil.getActivityByIndex(i).finish();
                        }
                    }
                } else {
                    toast("功能完善中！");
                }
            }
        });

        mAppListAdapter.setData(mAppListData);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
