package com.winjay.mirrorcast;


import static android.org.apache.commons.codec.binary.Base64.encodeBase64String;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Base64;

import com.tananaev.adblib.AdbBase64;
import com.tananaev.adblib.AdbConnection;
import com.tananaev.adblib.AdbCrypto;
import com.tananaev.adblib.AdbStream;
import com.winjay.mirrorcast.util.LogUtil;
import com.winjay.mirrorcast.util.NetUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Enumeration;


public class SendCommands {
    private static final String TAG = SendCommands.class.getSimpleName();

    private static SendCommands instance;

    private Thread thread = null;
    private Context mContext;
    private int status;

    private SendCommands(Context context) {
        mContext = context;
    }

    public static SendCommands getInstance(Context context) {
        if (instance == null) {
            instance = new SendCommands(context);
        }
        return instance;
    }

    public static AdbBase64 getBase64Impl() {
        return new AdbBase64() {
            @Override
            public String encodeToString(byte[] arg0) {
                return encodeBase64String(arg0);
            }
        };
    }

    private AdbCrypto setupCrypto() throws NoSuchAlgorithmException, IOException {
        AdbCrypto c = null;
        try {
            c = AdbCrypto.loadAdbKeyPair(getBase64Impl(), mContext.getFileStreamPath("priv.key"), mContext.getFileStreamPath("pub.key"));
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException | NullPointerException e) {
            // Failed to read from file
            c = null;
        }

        if (c == null) {
            // We couldn't load a key, so let's generate a new one
            c = AdbCrypto.generateAdbKeyPair(getBase64Impl());
            // Save it
            c.saveAdbKeyPair(mContext.getFileStreamPath("priv.key"), mContext.getFileStreamPath("pub.key"));
            //Generated new keypair
        } else {
            //Loaded existing keypair
        }
        return c;
    }

    public int sendServerJar(String serverIp) {
        status = 1;

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AssetManager assetManager = mContext.getAssets();
                    InputStream input_Stream = assetManager.open("scrcpy-server.jar");
                    byte[] buffer = new byte[input_Stream.available()];
                    input_Stream.read(buffer);
                    byte[] fileBase64 = Base64.encode(buffer, Base64.NO_WRAP);

                    adbWrite(serverIp, fileBase64);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        int count = 0;
        while (status == 1 && count < 1000) {
            LogUtil.d(TAG, "Connecting...");
            try {
                Thread.sleep(1000);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (count == 1000) {
            status = 2;
        }
        return status;
    }

    private void adbWrite(String serverIp, byte[] fileBase64) throws IOException {
        Socket sock = null;
        AdbCrypto crypto;
        AdbConnection adb = null;
        AdbStream stream = null;
        try {
            crypto = setupCrypto();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Couldn't read/write keys");
        }

        try {
            sock = new Socket(serverIp, 5555);
            LogUtil.d(TAG, " ADB socket connection successful");
        } catch (UnknownHostException e) {
            status = 2;
            throw new UnknownHostException(serverIp + " is no valid ip address");
        } catch (ConnectException e) {
            status = 2;
            throw new ConnectException("Device at " + serverIp + ":" + 5555 + " has no adb enabled or connection is refused");
        } catch (NoRouteToHostException e) {
            status = 2;
            throw new NoRouteToHostException("Couldn't find adb device at " + serverIp + ":" + 5555);
        } catch (IOException e) {
            e.printStackTrace();
            status = 2;
        }

        if (sock != null && status == 1) {
            try {
                adb = AdbConnection.create(sock, crypto);
                adb.connect();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }

        if (adb != null && status == 1) {
            try {
                stream = adb.open("shell:");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                status = 2;
                return;
            }
        }

        if (stream != null && status == 1) {
            try {
                stream.write(" " + '\n');
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }

        String responses = "";
        boolean done = false;
        while (!done && stream != null && status == 1) {
            try {
                byte[] responseBytes = stream.read();
                String response = new String(responseBytes, StandardCharsets.US_ASCII);
                if (response.substring(response.length() - 2).equals("$ ") ||
                        response.substring(response.length() - 2).equals("# ")) {
                    done = true;
//                    Log.e("ADB_Shell","Prompt ready");
                    responses += response;
                    break;
                } else {
                    responses += response;
                }
            } catch (InterruptedException | IOException e) {
                status = 2;
                e.printStackTrace();
            }
        }

        if (stream != null && status == 1) {
            int len = fileBase64.length;
            byte[] filePart = new byte[4056];
            int sourceOffset = 0;
            try {
                stream.write(" cd /data/local/tmp " + '\n');
                while (sourceOffset < len) {
                    if (len - sourceOffset >= 4056) {
                        System.arraycopy(fileBase64, sourceOffset, filePart, 0, 4056);  //Writing in 4KB pieces. 4096-40  ---> 40 Bytes for actual command text.
                        sourceOffset = sourceOffset + 4056;
                        String ServerBase64part = new String(filePart, StandardCharsets.US_ASCII);
                        stream.write(" echo " + ServerBase64part + " >> serverBase64" + '\n');
                        done = false;
                        while (!done) {
                            byte[] responseBytes = stream.read();
                            String response = new String(responseBytes, StandardCharsets.US_ASCII);
                            if (response.endsWith("$ ") || response.endsWith("# ")) {
                                done = true;
                            }
                        }
                    } else {
                        int rem = len - sourceOffset;
                        byte[] remPart = new byte[rem];
                        System.arraycopy(fileBase64, sourceOffset, remPart, 0, rem);
                        sourceOffset = sourceOffset + rem;
                        String ServerBase64part = new String(remPart, StandardCharsets.US_ASCII);
                        stream.write(" echo " + ServerBase64part + " >> serverBase64" + '\n');
                        done = false;
                        while (!done) {
                            byte[] responseBytes = stream.read();
                            String response = new String(responseBytes, StandardCharsets.US_ASCII);
                            if (response.endsWith("$ ") || response.endsWith("# ")) {
                                done = true;
                            }
                        }
                    }
                }
                stream.write(" base64 -d < serverBase64 > scrcpy-server.jar && rm serverBase64" + '\n');
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                status = 2;
                return;
            }
        }
        if (status == 1) {
            status = 0;
        }
    }

    public boolean startMirrorCast(String serverIp, int serverPort, int bitrate, int maxSize, String displayId) {
        LogUtil.d(TAG);
        StringBuilder command = new StringBuilder();
        command.append(" CLASSPATH=/data/local/tmp/scrcpy-server.jar app_process / com.winjay.scrcpy.Server ");
        command.append("1.24" + " server_port=" + serverPort + " local_ip=" + NetUtil.wifiIpAddress() + " max_size=" + maxSize + " max_fps=30" + (TextUtils.isEmpty(displayId) ? "" : " display_id=" + displayId)); // + " display_id=10" + " bit_rate=" + bitrate
        LogUtil.d(TAG, "command=" + command);

        Socket sock = null;
        AdbCrypto crypto;
        AdbConnection adb = null;
        AdbStream stream = null;
        try {
            crypto = setupCrypto();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "Couldn't read/write keys");
            return false;
        }

        try {
            sock = new Socket(serverIp, 5555);
            LogUtil.d(TAG, " ADB socket connection successful");
        } catch (UnknownHostException e) {
            LogUtil.e(TAG, serverIp + " is no valid ip address");
            return false;
        } catch (ConnectException e) {
            LogUtil.e(TAG, "Device at " + serverIp + ":" + 5555 + " has no adb enabled or connection is refused");
            return false;
        } catch (NoRouteToHostException e) {
            LogUtil.e(TAG, "Couldn't find adb device at " + serverIp + ":" + 5555);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
            return false;
        }

        if (sock != null) {
            try {
                adb = AdbConnection.create(sock, crypto);
                adb.connect();
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, e.getMessage());
                return false;
            }
        }

        if (adb != null) {
            try {
                stream = adb.open("shell:");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                LogUtil.e(TAG, e.getMessage());
                return false;
            }
        }

        if (stream != null) {
            try {
                stream.write(" " + '\n');
                stream.write(" cd /data/local/tmp " + '\n');
                stream.write(command.toString() + '\n');
                return true;
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                LogUtil.e(TAG, e.getMessage());
                return false;
            }
        }
        return false;
    }

//    public boolean stopMirrorCast() {
//        StringBuilder command = new StringBuilder();
//        command.append(" CLASSPATH=/data/local/tmp/scrcpy-server.jar app_process / com.winjay.scrcpy.CleanUp ");
//        if (stream != null) {
//            try {
//                stream.write(command.toString() + '\n');
//                return true;
//            } catch (IOException | InterruptedException e) {
//                LogUtil.e(TAG, "stop mirror cast error:" + e.getMessage());
//                e.printStackTrace();
//                return false;
//            }
//        }
//        return false;
//    }


    /*public int SendAdbCommands(Context context, final byte[] fileBase64, final String serverIp, String serverPort, String localIp, int bitrate, int maxSize, String displayId) {
        this.mContext = context;
        status = 1;
        final StringBuilder command = new StringBuilder();
        command.append(" CLASSPATH=/data/local/tmp/scrcpy-server.jar app_process / com.winjay.scrcpy.Server ");
        command.append("1.24" + " server_port=" + serverPort + " local_ip=" + localIp + " max_size=" + maxSize + " max_fps=30" + (TextUtils.isEmpty(displayId) ? "" : " display_id=" + displayId)); // + " display_id=10" + " bit_rate=" + bitrate

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    adbWrite(serverIp, fileBase64, command.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        int count = 0;
        while (status == 1 && count < 1000) {
            LogUtil.d(TAG, "Connecting...");
            try {
                Thread.sleep(100);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (count == 1000) {
            status = 2;
        }
        return status;
    }


    private void adbWrite(String serverIp, byte[] fileBase64, String command) throws IOException {
        AdbConnection adb = null;
        Socket sock = null;
        AdbCrypto crypto;
        AdbStream stream = null;

        try {
            crypto = setupCrypto();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Couldn't read/write keys");
        }

        try {
            sock = new Socket(serverIp, 5555);
            LogUtil.d(TAG, " ADB socket connection successful");
        } catch (UnknownHostException e) {
            status = 2;
            throw new UnknownHostException(serverIp + " is no valid ip address");
        } catch (ConnectException e) {
            status = 2;
            throw new ConnectException("Device at " + serverIp + ":" + 5555 + " has no adb enabled or connection is refused");
        } catch (NoRouteToHostException e) {
            status = 2;
            throw new NoRouteToHostException("Couldn't find adb device at " + serverIp + ":" + 5555);
        } catch (IOException e) {
            e.printStackTrace();
            status = 2;
        }

        if (sock != null && status == 1) {
            try {
                adb = AdbConnection.create(sock, crypto);
                adb.connect();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }

        if (adb != null && status == 1) {

            try {
                stream = adb.open("shell:");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                status = 2;
                return;
            }
        }

        if (stream != null && status == 1) {
            try {
                stream.write(" " + '\n');
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }

        String responses = "";
        boolean done = false;
        while (!done && stream != null && status == 1) {
            try {
                byte[] responseBytes = stream.read();
                String response = new String(responseBytes, StandardCharsets.US_ASCII);
                if (response.substring(response.length() - 2).equals("$ ") ||
                        response.substring(response.length() - 2).equals("# ")) {
                    done = true;
//                    Log.e("ADB_Shell","Prompt ready");
                    responses += response;
                    break;
                } else {
                    responses += response;
                }
            } catch (InterruptedException | IOException e) {
                status = 2;
                e.printStackTrace();
            }
        }

        if (stream != null && status == 1) {
            int len = fileBase64.length;
            byte[] filePart = new byte[4056];
            int sourceOffset = 0;
            try {
                stream.write(" cd /data/local/tmp " + '\n');
                while (sourceOffset < len) {
                    if (len - sourceOffset >= 4056) {
                        System.arraycopy(fileBase64, sourceOffset, filePart, 0, 4056);  //Writing in 4KB pieces. 4096-40  ---> 40 Bytes for actual command text.
                        sourceOffset = sourceOffset + 4056;
                        String ServerBase64part = new String(filePart, StandardCharsets.US_ASCII);
                        stream.write(" echo " + ServerBase64part + " >> serverBase64" + '\n');
                        done = false;
                        while (!done) {
                            byte[] responseBytes = stream.read();
                            String response = new String(responseBytes, StandardCharsets.US_ASCII);
                            if (response.endsWith("$ ") || response.endsWith("# ")) {
                                done = true;
                            }
                        }
                    } else {
                        int rem = len - sourceOffset;
                        byte[] remPart = new byte[rem];
                        System.arraycopy(fileBase64, sourceOffset, remPart, 0, rem);
                        sourceOffset = sourceOffset + rem;
                        String ServerBase64part = new String(remPart, StandardCharsets.US_ASCII);
                        stream.write(" echo " + ServerBase64part + " >> serverBase64" + '\n');
                        done = false;
                        while (!done) {
                            byte[] responseBytes = stream.read();
                            String response = new String(responseBytes, StandardCharsets.US_ASCII);
                            if (response.endsWith("$ ") || response.endsWith("# ")) {
                                done = true;
                            }
                        }
                    }
                }
                stream.write(" base64 -d < serverBase64 > scrcpy-server.jar && rm serverBase64" + '\n');
                Thread.sleep(100);
                stream.write(command + '\n');
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                status = 2;
                return;
            }
        }
        if (status == 1) ;
        status = 0;
    }*/

}
