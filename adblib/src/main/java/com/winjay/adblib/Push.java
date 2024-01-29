package com.winjay.adblib;

import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xudong on 2/25/14.
 *
 *  adb push协议：
 *   -------------------------------
 *  |          Command ID           |   4 bytes
 *   -------------------------------
 *  |            Package            | 256 bytes
 *   -------------------------------
 *  |        Total Payload Size     |   4 bytes
 *   -------------------------------
 *  |          Chunk Payload        |
 *   |             data              | 实际要传输的数据块。这些数据块将被分成固定大小的块（默认为 64KB）进行传输。
 *   -------------------------------
 *          (Payload Size bytes)
 */
public class Push {
    private static final String TAG = Push.class.getSimpleName();
    private AdbConnection adbConnection;
    private File local;
    private String remotePath;

    public Push(AdbConnection adbConnection, File local, String remotePath) {
        this.adbConnection = adbConnection;
        this.local = local;
        this.remotePath = remotePath;
    }

    public void execute(Handler handler) throws InterruptedException, IOException {

        AdbStream stream = adbConnection.open("sync:");

        String sendId = "SEND";

        String mode = ",33206";

        int length = (remotePath + mode).length();

        stream.write(ByteUtils.concat(sendId.getBytes(), ByteUtils.intToByteArray(length)));

        stream.write(remotePath.getBytes());

        stream.write(mode.getBytes());

        byte[] buff = new byte[adbConnection.getMaxData()];
        InputStream is = new FileInputStream(local);

        long sent = 0;
        long total = local.length();
        int lastProgress = 0;
        while (true) {
            int read = is.read(buff);
            if (read < 0) {
                break;
            }

            stream.write(ByteUtils.concat("DATA".getBytes(), ByteUtils.intToByteArray(read)));

            if (read == buff.length) {
                stream.write(buff);
            } else {
                byte[] tmp = new byte[read];
                System.arraycopy(buff, 0, tmp, 0, read);
                stream.write(tmp);
            }

            sent += read;

            final int progress = (int)(sent * 100 / total);
            if (lastProgress != progress) {
                handler.sendMessage(handler.obtainMessage(Message.INSTALLING_PROGRESS, Message.PUSH_PART, progress));
                lastProgress = progress;
            }

        }

        stream.write(ByteUtils.concat("DONE".getBytes(), ByteUtils.intToByteArray((int) System.currentTimeMillis())));

        byte[] res = stream.read();
        // TODO: test if res contains "OKEY" or "FAIL"
        Log.d(TAG, new String(res));

        stream.write(ByteUtils.concat("QUIT".getBytes(), ByteUtils.intToByteArray(0)));
    }
}
