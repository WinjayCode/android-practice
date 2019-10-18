package com.winjay.practice.content_provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.winjay.practice.utils.LogUtil;

/**
 * @author Winjay
 * @date 2019/5/11
 */
public class UserinfoProvider extends ContentProvider {
    private static final String TAG = UserinfoProvider.class.getSimpleName();

    private static final String CONTENT = "content://";
    public static final String AUTHORIY = "com.winjay.info";
    /**
     * 该ContentProvider所返回的数据类型定义、数据集合
     */
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORIY;
    public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd." + AUTHORIY;

    public static final Uri USERINFO_URI = Uri.parse(CONTENT + AUTHORIY + "/" + UserInfoDbHelper.TABLE_USER_INFO);
    public static final Uri COMPANY_URI = Uri.parse(CONTENT + AUTHORIY + "/" + UserInfoDbHelper.TABLE_COMPANY);

    private SQLiteDatabase mDatabase;

    static final int USER_INFOS = 1;
    static final int USER_INFO_ITEM = 2;
    static final int COMPANY = 3;
    static final int COMPANY_ITEM = 4;

    static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORIY, "userinfo", USER_INFOS);
        uriMatcher.addURI(AUTHORIY, "userinfo/*", USER_INFO_ITEM);
        uriMatcher.addURI(AUTHORIY, "company", COMPANY);
        uriMatcher.addURI(AUTHORIY, "company/#", COMPANY_ITEM);
    }

    @Override
    public boolean onCreate() {
        LogUtil.d(TAG, "onCreate()");
        mDatabase = new UserInfoDbHelper(getContext()).getWritableDatabase();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        LogUtil.d(TAG, "getType()_type=" + uriMatcher.match(uri));
        switch (uriMatcher.match(uri)) {
            case USER_INFOS:
            case COMPANY:
                return CONTENT_TYPE;
            case USER_INFO_ITEM:
            case COMPANY_ITEM:
                return CONTENT_TYPE_ITEM;
            default:
                throw new RuntimeException("错误的Uri");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long newId = 0;
        Uri newUri = null;
        switch (uriMatcher.match(uri)) {
            case USER_INFOS:
                newId = mDatabase.insert(UserInfoDbHelper.TABLE_USER_INFO, null, contentValues);
                newUri = Uri.parse(CONTENT + AUTHORIY + "/" + UserInfoDbHelper.TABLE_USER_INFO + "/" + newId);
                break;
            case COMPANY:
                newId = mDatabase.insert(UserInfoDbHelper.TABLE_COMPANY, null, contentValues);
                newUri = Uri.parse(CONTENT + AUTHORIY + "/" + UserInfoDbHelper.TABLE_COMPANY + "/" + newId);
                break;
        }
        if (newId > 0) {
            LogUtil.d(TAG, "insert()_newUri=" + newUri);
            return newUri;
        }
        throw new IllegalArgumentException("Failed to insert row into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        LogUtil.d(TAG, "query()_type=" + uriMatcher.match(uri));
        switch (uriMatcher.match(uri)) {
            case USER_INFOS:
                cursor = mDatabase.query(UserInfoDbHelper.TABLE_USER_INFO, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case USER_INFO_ITEM:
                String tel = uri.getPathSegments().get(1);
                cursor = mDatabase.query(UserInfoDbHelper.TABLE_USER_INFO, projection, "tel_num = ?", new String[]{tel}, null, null, sortOrder);
                break;
            case COMPANY:
                cursor = mDatabase.query(UserInfoDbHelper.TABLE_COMPANY, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case COMPANY_ITEM:
                String cid = uri.getPathSegments().get(1);
                cursor = mDatabase.query(UserInfoDbHelper.TABLE_USER_INFO, projection, "id = ?", new String[]{cid}, null, null, sortOrder);
                break;
        }
        return cursor;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
