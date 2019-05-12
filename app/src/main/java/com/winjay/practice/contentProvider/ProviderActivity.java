package com.winjay.practice.contentProvider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.winjay.practice.R;

/**
 * ContentProvider学习
 *
 * @author Winjay
 * @date 2019/5/11
 */
public class ProviderActivity extends AppCompatActivity {
    /**
     * 用户描述信息
     */
    private EditText mUserDescEditText;
    /**
     * 电话号码
     */
    private EditText mUserTelEditText;
    /**
     * 用户所属的公司id
     */
    private EditText mUserCompIdEditText;
    /**
     * 提交按钮
     */
    private Button mSubmitBtn;

    /**
     * 公司id
     */
    private EditText mCompIdEdittext;
    /**
     * 公司业务
     */
    private EditText mCompBussinessEdittext;
    /**
     * 公司地址
     */
    private EditText mCompAddrEdittext;
    /**
     * 提交按钮
     */
    private Button mCompSubmitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);
        initWidgets();
    }

    private void initWidgets() {
        mUserDescEditText = findViewById(R.id.desc_edit);
        mUserTelEditText = findViewById(R.id.tel_edit);
        mUserCompIdEditText = findViewById(R.id.comp_edit);
        mSubmitBtn = findViewById(R.id.submit_btn);
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInfoRecord();
                mSubmitBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        queryPostCode();
                    }
                }, 1000);
            }
        });

        mCompAddrEdittext = findViewById(R.id.comp_addr_edit);
        mCompIdEdittext = findViewById(R.id.comp_id_edit);
        mCompBussinessEdittext = findViewById(R.id.comp_business_edit);
        mCompSubmitBtn = findViewById(R.id.submit_comp_btn);
        mCompSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCompanyRecord();
            }
        });
    }

    /**
     * 存储用户信息到ContentProvider
     */
    private void saveUserInfoRecord() {
        ContentValues newRecord = new ContentValues();
        newRecord.put(UserInfoDbHelper.DESC_COLUMN, mUserDescEditText.getText().toString());
        newRecord.put(UserInfoDbHelper.TEL_COLUMN, mUserTelEditText.getText().toString());
        newRecord.put(UserInfoDbHelper.COMP_ID_COLUMN, mUserCompIdEditText.getText().toString());
        getContentResolver().insert(UserinfoProvider.USERINFO_URI, newRecord);
    }

    /**
     * 通过电话号码查询相关信息
     */
    private void queryPostCode() {
        Uri queryUri = Uri.parse("content://com.winjay.info/userinfo/123456");
        Cursor cursor = getContentResolver().query(queryUri, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            Toast.makeText(this, "电话来自：" + cursor.getString(2), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 存储公司信息到ContentProvider中
     */
    private void saveCompanyRecord() {
        ContentValues newRecord = new ContentValues();
        newRecord.put(UserInfoDbHelper.ADDR_COLUMN, mCompAddrEdittext.getText().toString());
        newRecord.put(UserInfoDbHelper.BUSINESS_COLUMN, mCompBussinessEdittext.getText().toString());
        newRecord.put(UserInfoDbHelper.ID_COLUMN, mCompIdEdittext.getText().toString());
        getContentResolver().insert(UserinfoProvider.COMPANY_URI, newRecord);
    }
}
