package com.winjay.practice.storage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.DocumentsContract
import android.text.TextUtils
import com.winjay.practice.utils.LogUtil
import java.io.*

class MyDownloadCollection {

    /**
     * 访问目录
     */
    fun openDirectory(activity: Activity, requestCode: Int) {
        LogUtil.d(TAG, "openDirectory() called")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
        }
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * 访问文件
     */
    fun openDocument(activity: Activity, fileType: String, requestCode: Int) {
        LogUtil.d(
            TAG,
            "openDocument() called with: activity = $activity, fileType = $fileType, requestCode = $requestCode"
        )
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = fileType
        }
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * 创建文档
     */
    fun createDocument(activity: Activity, fileType: String, title: String, requestCode: Int) {
        LogUtil.d(
            TAG,
            "createDocument() called with: activity = $activity, fileType = $fileType, title = $title, requestCode = $requestCode"
        )
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = fileType
            putExtra(Intent.EXTRA_TITLE, title)
        }
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * 写入数据
     */
    fun writeDataToDocument(context: Context, uri: Uri, content: String) {
        LogUtil.d(
            TAG,
            "writeDataToDocument() called with: context = $context, uri = $uri, content = $content"
        )
        try {
            context.contentResolver.openFileDescriptor(uri, "w").use { parcelFileDescriptor ->
                FileOutputStream(parcelFileDescriptor?.fileDescriptor).use { fos ->
                    fos.write(content.toByteArray())
                    fos.close()
                    parcelFileDescriptor?.close()
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 删除文档
     */
    fun deleteDocument(context: Context, uri: Uri) {
        LogUtil.d(TAG, "deleteDocument() called with: context = $context, uri = $uri")
        val result = DocumentsContract.deleteDocument(context.contentResolver,uri)
        LogUtil.d(TAG, "deleteDocument() result = $result")
    }

    /***
     * 从 Uri中返回文本
     */
    private fun readTextFromUri(context: Context, uri: Uri): String {
        val stringBuilder = StringBuilder()
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }

    /**
     * 从 Uri中返回Bitmap
     */
    @Throws(IOException::class)
    private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
        return BitmapFactory.decodeFileDescriptor(parcelFileDescriptor?.fileDescriptor)
    }

    /**
     * 永久保存获取的目录权限
     * APP申请到目录的永久权限后，用户可以在该APP的设置页面（清除缓存页面下）取消目录的访问权限
     */
    fun keepDocumentPermission(context: Context, uri: Uri, intent: Intent) {
        val takeFlags: Int = intent.flags and
                (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        // 检查最新数据：文件是否被删除
        context.contentResolver.takePersistableUriPermission(uri, takeFlags)
        // 通过sharePreference保存该目录Uri
        // --------- 省略 代码 --------
    }

    /**
     * 使用获得永久保存获取的目录权限
     */
    fun useKeepDocument(context: Context,saveUri: String,intent: Intent){
        if (TextUtils.isEmpty(saveUri)) {
            // 打开目录, 重新请求永久保存获取的目录权限
            // --------- 省略 代码 --------
        } else {
            try {
                val uri = Uri.parse(saveUri)
                val takeFlags: Int = intent.flags and
                        (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)
                //Uri 授予，执行下一步操作
                // --------- 省略 代码 --------
            } catch (e: SecurityException) {
                //Uri 未被授予, 打开目录, 重新请求永久保存获取的目录权限
                // --------- 省略 代码 --------
            }
        }
    }

    companion object {
        private const val TAG = "MyDownloadCollection"
    }
}