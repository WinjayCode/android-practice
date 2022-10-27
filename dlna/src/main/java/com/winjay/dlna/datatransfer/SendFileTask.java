package com.winjay.dlna.datatransfer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.winjay.dlna.Constants;
import com.winjay.dlna.util.LogUtil;
import com.winjay.dlna.util.Md5Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Author: leavesC
 * @Date: 2019/2/27 23:56
 * @Desc: 客户端发送文件
 * @Github：https://github.com/leavesC
 */
public class SendFileTask extends AsyncTask<Object, Integer, Boolean> {

    private static final String TAG = "SendFileTask";

    private final ProgressDialog progressDialog;

    @SuppressLint("StaticFieldLeak")
    private final Context context;

    public SendFileTask(Context context) {
        this.context = context.getApplicationContext();
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("正在发送文件");
        progressDialog.setMax(100);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        Socket socket = null;
        OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        InputStream inputStream = null;
        try {
            String hostAddress = params[0].toString();
            String outputFilePath = params[1].toString();
            LogUtil.d(TAG, "filePath=" + outputFilePath);

            File outputFile = new File(outputFilePath);

            FileTransfer fileTransfer = new FileTransfer();
            String fileName = outputFile.getName();
            String fileMa5 = Md5Util.getMd5(outputFile);
            long fileLength = outputFile.length();
            fileTransfer.setFileName(fileName);
            fileTransfer.setMd5(fileMa5);
            fileTransfer.setFileLength(fileLength);

            LogUtil.d(TAG, "fileTransfer=" + fileTransfer);

            socket = new Socket();
            socket.bind(null);
            socket.connect((new InetSocketAddress(hostAddress, Constants.PORT)), 10000);
            outputStream = socket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(fileTransfer);
            inputStream = new FileInputStream(outputFile);
            long fileSize = fileTransfer.getFileLength();
            long total = 0;
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
                total += len;
                int progress = (int) ((total * 100) / fileSize);
                publishProgress(progress);
                LogUtil.d(TAG, "文件发送进度：" + progress);
            }
            socket.close();
            inputStream.close();
            outputStream.close();
            objectOutputStream.close();
            socket = null;
            inputStream = null;
            outputStream = null;
            objectOutputStream = null;
            LogUtil.d(TAG, "文件发送成功");
            return true;
        } catch (Exception e) {
            LogUtil.e(TAG, "文件发送异常 Exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        progressDialog.cancel();
        LogUtil.d(TAG, "onPostExecute: " + aBoolean);
    }
}
