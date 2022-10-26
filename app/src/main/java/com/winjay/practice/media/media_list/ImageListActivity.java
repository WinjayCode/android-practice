package com.winjay.practice.media.media_list;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.MediaCollectionHelper;
import com.winjay.practice.media.MediaCollectionHelper.ImageBean;
import com.winjay.practice.media.image.ImagePlayActivity;

import java.util.List;

import butterknife.BindView;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 图片列表
 *
 * @author Winjay
 * @date 2022/10/26
 */
public class ImageListActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = ImageListActivity.class.getSimpleName();

    @BindView(R.id.image_rv)
    RecyclerView mImageRV;

    private ImageListAdapter mImageListAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        MediaCollectionHelper mediaCollectionHelper = new MediaCollectionHelper();
        List<ImageBean> imageBeans = mediaCollectionHelper.queryImageCollection(this);
        mImageListAdapter.setData(imageBeans);
    }

    private void initView() {
        mImageRV.setLayoutManager(new GridLayoutManager(this, 3));
        mImageListAdapter = new ImageListAdapter(this);
        mImageRV.setAdapter(mImageListAdapter);
        mImageListAdapter.setOnItemClickListener(new ImageListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ImageBean imageBean) {
                Intent intent = new Intent(ImageListActivity.this, ImagePlayActivity.class);
                intent.putExtra("image", imageBean);
                startActivity(intent);
            }
        });
    }
}
