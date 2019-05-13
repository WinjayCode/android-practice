package com.winjay.practice.contentProvider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Winjay
 * @date 2019/5/11
 */
public class UserInfoDbHelper extends SQLiteOpenHelper {
    private static final String TAG = UserInfoDbHelper.class.getSimpleName();

    private static final String DB_NAME = "userinfo.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_USER_INFO = "userinfo";
    public static final String TABLE_COMPANY = "company";

    public static final String TEL_COLUMN = "tel_num";
    public static final String DESC_COLUMN = "describe";
    public static final String COMP_ID_COLUMN = "comp_id";
    public static final String ID_COLUMN = "id";
    public static final String BUSINESS_COLUMN = "business";
    public static final String ADDR_COLUMN = "addr";

    private static final String USERINFO_TABLE_SQL = "CREATE TABLE " + TABLE_USER_INFO + " ("
            + TEL_COLUMN + " TEXT ,"
            + COMP_ID_COLUMN + " TEXT , "
            + DESC_COLUMN + " TEXT "
            + ")";

    private static final String COMPANY_TABLE_SQL = "CREATE TABLE " + TABLE_COMPANY + " ("
            + ID_COLUMN + " TEXT PRIMARY KEY, "
            + BUSINESS_COLUMN + " TEXT,"
            + ADDR_COLUMN + " TEXT"
            + ")";

    public UserInfoDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate()");
        sqLiteDatabase.execSQL(USERINFO_TABLE_SQL);
        sqLiteDatabase.execSQL(COMPANY_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
