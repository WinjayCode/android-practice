package com.winjay.scrcpy;

import android.text.TextUtils;
import android.view.MotionEvent;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.DecimalFormat;

/**
 * @author F2848777
 * @date 2022-12-02
 */
public class DroidSocketClient extends WebSocketClient {
    private static final String TAG = DroidSocketClient.class.getSimpleName();

    private Device mDevice;
    private DroidController mDroidController;
    private OnSocketClientListener mOnSocketClientListener;

    public DroidSocketClient(URI serverUri, Device device, OnSocketClientListener listener) {
        super(serverUri);
        Ln.d("DroidSocketClient()");
        mDevice = device;
        mOnSocketClientListener = listener;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Ln.i("DroidSocketClient onOpen()");

        Size videoSize = mDevice.getScreenInfo().getVideoSize();
        Size deviceSize = mDevice.getDeviceSize();
        Ln.i("mDevice.getMaxSize()=" + mDevice.getMaxSize());
        Ln.i("deviceSize.getWidth()=" + deviceSize.getWidth());
        Ln.i("deviceSize.getHeight()=" + deviceSize.getHeight());

        String widthRatio = "1";
        String heightRatio = "1";
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        widthRatio = decimalFormat.format((float) deviceSize.getWidth() / (float) videoSize.getWidth());
        heightRatio = decimalFormat.format((float) deviceSize.getHeight() / (float) videoSize.getHeight());

        Ln.i("sendDeviceMeta() videoWidth=" + videoSize.getWidth()
                + ", videoHeight=" + videoSize.getHeight()
                + ", widthRatio=" + widthRatio
                + ", heightRatio=" + heightRatio);
        send(Constants.SCRCPY_REPLY_VIDEO_SIZE + Constants.COMMAND_SPLIT
                + videoSize.getWidth() + Constants.COMMAND_SPLIT
                + videoSize.getHeight() + Constants.COMMAND_SPLIT
                + widthRatio + Constants.COMMAND_SPLIT
                + heightRatio);
    }

    @Override
    public void onMessage(String message) {
        // 移栈方式
        if (message.startsWith(Constants.SCRCPY_COMMAND_MOVE_PHONE_APP_STACK_MIRROR_CAST)) {
            String[] split = message.split(Constants.COMMAND_SPLIT);
            Ln.i("app mirror displayId=" + split[1]);
            try {
//                Command.exec("sh");
                String result = runCMD("am stack list");
                Ln.i("stack list result=" + result);
                int start = result.indexOf("taskId=");
                Ln.i("start=" + start);

                if (start == -1) {
                    return;
                }

                int end = result.indexOf(":");
                Ln.i("end=" + end);
                int taskId = Integer.parseInt(result.substring(start + 7, end));
                Ln.i("top task id=" + taskId);

                String command = "am display move-stack " + taskId + " " + split[1];
                Ln.i("move-stack command=" + command);

                runCMD(command);
            } catch (Exception e) {
                Ln.i("app mirror error " + e.getMessage());
            }
        }
        // 启动app到虚拟屏上
        else if (message.startsWith(Constants.SCRCPY_COMMAND_START_PHONE_APP_MIRROR_CAST)) {
            Ln.i("start app on specified display.");
            String[] split = message.split(Constants.COMMAND_SPLIT);
            if (split.length < 4) {
                Ln.e("miss parameters.");
                return;
            }
            if (TextUtils.isEmpty(split[1])) {
                Ln.e("miss app package name.");
                return;
            }
            if (TextUtils.isEmpty(split[2])) {
                Ln.e("miss app enter class.");
                return;
            }
            if (TextUtils.isEmpty(split[3])) {
                Ln.e("start app on main display.");
                split[2] = "0";
            }
            String command = "am start -W -n " + split[1] + Constants.COMMAND_SPLIT + split[2] + " --display " + split[3];
            try {
                runCMDNoResult(command);
            } catch (Exception e) {
                Ln.i("doCommand() error " + e.getMessage());
                e.printStackTrace();
            }
        }
        // 反控
        else if (message.startsWith(Constants.SCRCPY_COMMAND_MOTION_EVENT)) {
//            if (mDroidController != null) {
//                mDroidController.handleEvent(message);
//            }

            String[] split = message.split(Constants.COMMAND_SPLIT);
            String motionevent = "";
            int action = Integer.parseInt(split[1]);
            if (action == MotionEvent.ACTION_DOWN) {
                motionevent = "DOWN";
            } else if (action == MotionEvent.ACTION_UP) {
                motionevent = "UP";
            } else if (action == MotionEvent.ACTION_MOVE) {
                motionevent = "MOVE";
            } else if (action == MotionEvent.ACTION_CANCEL) {
                motionevent = "CANCEL";
            }

//            Ln.i("x=" + split[1] + " y=" + split[2]);

            String command;
            if (mDevice.getDisplayId() != 0) {
                command = "input -d " + mDevice.getDisplayId() + " motionevent " + motionevent + " " + Integer.parseInt(split[2]) + " " + Integer.parseInt(split[3]);
            } else {
                command = "input motionevent " + motionevent + " " + Integer.parseInt(split[2]) + " " + Integer.parseInt(split[3]);
            }
//            Ln.i("motionevent command=" + command);

//            try {
//                runCMDNoResult(command);
////                Command.exec(command);
//            } catch (Exception e) {
//                Ln.i("doCommand() error " + e.getMessage());
//                e.printStackTrace();
//            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        runCMDNoResult(command);
//                        runCMD(command);
                    } catch (Exception e) {
                        Ln.i("doCommand() error " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void runCMDNoResult(String cmd) throws Exception {
        Process process = Runtime.getRuntime().exec(cmd);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            Ln.i("Command " + cmd + " returned with value " + exitCode);
        }
//        process.destroy();
    }

    private String runCMD(String cmd) throws Exception {
        String result = "";
        Process p = Runtime.getRuntime().exec(cmd);
        InputStream is = p.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null) {
            result += line + "\n";
        }
        p.waitFor();
        is.close();
        reader.close();
        p.destroy();
        return result;
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Ln.i("DroidSocketClient onClose()");
        if (mOnSocketClientListener != null) {
            mOnSocketClientListener.onClose();
        }
    }

    @Override
    public void onError(Exception ex) {
        Ln.i("DroidSocketClient onError() " + ex.getMessage());
    }

    public void setController(DroidController controller) {
        mDroidController = controller;
    }

    public interface OnSocketClientListener {
        void onClose();
    }
}
