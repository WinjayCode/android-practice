package com.winjay.scrcpy;

import android.graphics.Rect;
import android.media.MediaCodecInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Locale;

public final class Server {

    private Server() {
        // not instantiable
    }

    private static void scrcpy(Options options) {
        Ln.i("Device: " + Build.MANUFACTURER + " " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")");
        final Device device = new Device(options);
        List<CodecOption> codecOptions = options.getCodecOptions();

//        Thread initThread = startInitThread(options); // whether to delete scrcpy-server.jar

        boolean tunnelForward = options.isTunnelForward();
        boolean control = options.getControl();
        boolean sendDummyByte = options.getSendDummyByte();

        String serverIp = options.getServerIp();
        Ln.i("serverIp=" + serverIp);
        String serverPort = options.getServerPort();
        Ln.i("serverPort=" + serverPort);
        try {
            DroidScreenEncoder screenEncoder = new DroidScreenEncoder(options.getSendFrameMeta(), options.getBitRate(), options.getMaxFps(), codecOptions,
                    options.getEncoderName(), options.getDownsizeOnError());

            DroidSocketClientManager.getInstance().connect(serverIp, serverPort, device, new DroidSocketClient.OnSocketClientListener() {
                @Override
                public void onClose() {
                    screenEncoder.stopStreamScreen();
                }
            });

            if (control) {
                final DroidController controller = new DroidController(device, options.getClipboardAutosync(), options.getPowerOn());
                controller.control();

                DroidSocketClientManager.getInstance().setController(controller);
            }

            try {
                Ln.i("Screen streaming start");
                // synchronous
                screenEncoder.streamScreen(device);
            } catch (Exception e) {
                // this is expected on close
                Ln.e("Screen streaming error, " + e.getMessage());
            } finally {
                Ln.i("Screen streaming stopped");
                screenEncoder.stopStreamScreen();
//                initThread.interrupt();
                DroidSocketClientManager.getInstance().close();
            }
            System.exit(0);
        } catch (Exception e) {
            Ln.e("scrcpy start error=" + e.getMessage());
        }

//        try (DesktopConnection connection = DesktopConnection.open(tunnelForward, control, sendDummyByte)) {
//            Log.d("scrcpy", "1111");
//            if (options.getSendDeviceMeta()) {
//                Log.d("scrcpy", "2222");
//                Size videoSize = device.getScreenInfo().getVideoSize();
//                connection.sendDeviceMeta(Device.getDeviceName(), videoSize.getWidth(), videoSize.getHeight());
//            }
//            Log.d("scrcpy", "3333");
//            ScreenEncoder screenEncoder = new ScreenEncoder(options.getSendFrameMeta(), options.getBitRate(), options.getMaxFps(), codecOptions,
//                    options.getEncoderName(), options.getDownsizeOnError());
//
//            Thread controllerThread = null;
//            Thread deviceMessageSenderThread = null;
//            if (control) {
//                final Controller controller = new Controller(device, connection, options.getClipboardAutosync(), options.getPowerOn());
//
//                // asynchronous
//                controllerThread = startController(controller);
//                deviceMessageSenderThread = startDeviceMessageSender(controller.getSender());
//
//                device.setClipboardListener(new Device.ClipboardListener() {
//                    @Override
//                    public void onClipboardTextChanged(String text) {
//                        controller.getSender().pushClipboardText(text);
//                    }
//                });
//            }
//
//            try {
//                // synchronous
//                screenEncoder.streamScreen(device, connection.getVideoFd());
//            } catch (IOException e) {
//                // this is expected on close
//                Ln.d("Screen streaming stopped");
//            } finally {
//                initThread.interrupt();
//                if (controllerThread != null) {
//                    controllerThread.interrupt();
//                }
//                if (deviceMessageSenderThread != null) {
//                    deviceMessageSenderThread.interrupt();
//                }
//            }
//        }
    }

    private static void initAndCleanUp(Options options) {
        boolean mustDisableShowTouchesOnCleanUp = false;
        int restoreStayOn = -1;
        boolean restoreNormalPowerMode = options.getControl(); // only restore power mode if control is enabled
        if (options.getShowTouches() || options.getStayAwake()) {
            Settings settings = Device.getSettings();
            if (options.getShowTouches()) {
                try {
                    String oldValue = settings.getAndPutValue(Settings.TABLE_SYSTEM, "show_touches", "1");
                    // If "show touches" was disabled, it must be disabled back on clean up
                    mustDisableShowTouchesOnCleanUp = !"1".equals(oldValue);
                } catch (SettingsException e) {
                    Ln.e("Could not change \"show_touches\"", e);
                }
            }

            if (options.getStayAwake()) {
                int stayOn = BatteryManager.BATTERY_PLUGGED_AC | BatteryManager.BATTERY_PLUGGED_USB | BatteryManager.BATTERY_PLUGGED_WIRELESS;
                try {
                    String oldValue = settings.getAndPutValue(Settings.TABLE_GLOBAL, "stay_on_while_plugged_in", String.valueOf(stayOn));
                    try {
                        restoreStayOn = Integer.parseInt(oldValue);
                        if (restoreStayOn == stayOn) {
                            // No need to restore
                            restoreStayOn = -1;
                        }
                    } catch (NumberFormatException e) {
                        restoreStayOn = 0;
                    }
                } catch (SettingsException e) {
                    Ln.e("Could not change \"stay_on_while_plugged_in\"", e);
                }
            }
        }

        if (options.getCleanup()) {
            try {
                CleanUp.configure(options.getDisplayId(), restoreStayOn, mustDisableShowTouchesOnCleanUp, restoreNormalPowerMode,
                        options.getPowerOffScreenOnClose());
            } catch (IOException e) {
                Ln.e("Could not configure cleanup", e);
            }
        }
    }

    private static Thread startInitThread(final Options options) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                initAndCleanUp(options);
            }
        });
        thread.start();
        return thread;
    }

    private static Thread startController(final Controller controller) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    controller.control();
                } catch (IOException e) {
                    // this is expected on close
                    Ln.d("Controller stopped");
                }
            }
        });
        thread.start();
        return thread;
    }

    private static Thread startDeviceMessageSender(final DeviceMessageSender sender) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sender.loop();
                } catch (IOException | InterruptedException e) {
                    // this is expected on close
                    Ln.d("Device message sender stopped");
                }
            }
        });
        thread.start();
        return thread;
    }

    private static Options createOptions(String... args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Missing client version");
        }

        String clientVersion = args[0];
        if (!clientVersion.equals(BuildConfig.VERSION_NAME)) {
            throw new IllegalArgumentException(
                    "The server version (" + BuildConfig.VERSION_NAME + ") does not match the client " + "(" + clientVersion + ")");
        }

        Options options = new Options();

        for (int i = 1; i < args.length; ++i) {
            String arg = args[i];
            int equalIndex = arg.indexOf('=');
            if (equalIndex == -1) {
                throw new IllegalArgumentException("Invalid key=value pair: \"" + arg + "\"");
            }
            String key = arg.substring(0, equalIndex);
            String value = arg.substring(equalIndex + 1);
            Ln.i("key: " + key + " value：" + value);
            switch (key) {
                case "log_level":
                    Ln.Level level = Ln.Level.valueOf(value.toUpperCase(Locale.ENGLISH));
                    options.setLogLevel(level);
                    break;
                case "max_size":
                    int maxSize = Integer.parseInt(value) & ~7; // multiple of 8
                    options.setMaxSize(maxSize);
                    break;
                case "bit_rate":
                    int bitRate = Integer.parseInt(value);
                    options.setBitRate(bitRate);
                    break;
                case "max_fps":
                    int maxFps = Integer.parseInt(value);
                    options.setMaxFps(maxFps);
                    break;
                case "lock_video_orientation":
                    int lockVideoOrientation = Integer.parseInt(value);
                    options.setLockVideoOrientation(lockVideoOrientation);
                    break;
                case "tunnel_forward":
                    boolean tunnelForward = Boolean.parseBoolean(value);
                    options.setTunnelForward(tunnelForward);
                    break;
                case "crop":
                    Rect crop = parseCrop(value);
                    options.setCrop(crop);
                    break;
                case "control":
                    boolean control = Boolean.parseBoolean(value);
                    options.setControl(control);
                    break;
                case "display_id":
                    int displayId = Integer.parseInt(value);
                    options.setDisplayId(displayId);
                    break;
                case "show_touches":
                    boolean showTouches = Boolean.parseBoolean(value);
                    options.setShowTouches(showTouches);
                    break;
                case "stay_awake":
                    boolean stayAwake = Boolean.parseBoolean(value);
                    options.setStayAwake(stayAwake);
                    break;
                case "codec_options":
                    List<CodecOption> codecOptions = CodecOption.parse(value);
                    options.setCodecOptions(codecOptions);
                    break;
                case "encoder_name":
                    if (!value.isEmpty()) {
                        options.setEncoderName(value);
                    }
                    break;
                case "power_off_on_close":
                    boolean powerOffScreenOnClose = Boolean.parseBoolean(value);
                    options.setPowerOffScreenOnClose(powerOffScreenOnClose);
                    break;
                case "clipboard_autosync":
                    boolean clipboardAutosync = Boolean.parseBoolean(value);
                    options.setClipboardAutosync(clipboardAutosync);
                    break;
                case "downsize_on_error":
                    boolean downsizeOnError = Boolean.parseBoolean(value);
                    options.setDownsizeOnError(downsizeOnError);
                    break;
                case "cleanup":
                    boolean cleanup = Boolean.parseBoolean(value);
                    options.setCleanup(cleanup);
                    break;
                case "power_on":
                    boolean powerOn = Boolean.parseBoolean(value);
                    options.setPowerOn(powerOn);
                    break;
                case "send_device_meta":
                    boolean sendDeviceMeta = Boolean.parseBoolean(value);
                    options.setSendDeviceMeta(sendDeviceMeta);
                    break;
                case "send_frame_meta":
                    boolean sendFrameMeta = Boolean.parseBoolean(value);
                    options.setSendFrameMeta(sendFrameMeta);
                    break;
                case "send_dummy_byte":
                    boolean sendDummyByte = Boolean.parseBoolean(value);
                    options.setSendDummyByte(sendDummyByte);
                    break;
                case "raw_video_stream":
                    boolean rawVideoStream = Boolean.parseBoolean(value);
                    if (rawVideoStream) {
                        options.setSendDeviceMeta(false);
                        options.setSendFrameMeta(false);
                        options.setSendDummyByte(false);
                    }
                    break;
                case "server_ip":
                    if (!value.isEmpty()) {
                        Ln.i("server_ip=" + value);
                        options.setServerIp(value);
                    }
                    break;
                case "server_port":
                    if (!value.isEmpty()) {
                        Ln.i("server_port=" + value);
                        options.setServerPort(value);
                    }
                    break;
                default:
                    Ln.w("Unknown server option: " + key);
                    break;
            }
        }

        return options;
    }

    private static Rect parseCrop(String crop) {
        if (crop.isEmpty()) {
            return null;
        }
        // input format: "width:height:x:y"
        String[] tokens = crop.split(":");
        if (tokens.length != 4) {
            throw new IllegalArgumentException("Crop must contains 4 values separated by colons: \"" + crop + "\"");
        }
        int width = Integer.parseInt(tokens[0]);
        int height = Integer.parseInt(tokens[1]);
        int x = Integer.parseInt(tokens[2]);
        int y = Integer.parseInt(tokens[3]);
        return new Rect(x, y, x + width, y + height);
    }

    private static void suggestFix(Throwable e) {
        if (e instanceof InvalidDisplayIdException) {
            InvalidDisplayIdException idie = (InvalidDisplayIdException) e;
            int[] displayIds = idie.getAvailableDisplayIds();
            if (displayIds != null && displayIds.length > 0) {
                Ln.e("Try to use one of the available display ids:");
                for (int id : displayIds) {
                    Ln.e("    scrcpy --display " + id);
                }
            }
        } else if (e instanceof InvalidEncoderException) {
            InvalidEncoderException iee = (InvalidEncoderException) e;
            MediaCodecInfo[] encoders = iee.getAvailableEncoders();
            if (encoders != null && encoders.length > 0) {
                Ln.e("Try to use one of the available encoders:");
                for (MediaCodecInfo encoder : encoders) {
                    Ln.e("    scrcpy --encoder '" + encoder.getName() + "'");
                }
            }
        }
    }

    public static void main(String... args) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Ln.e("Exception on thread " + t, e);
                suggestFix(e);
            }
        });

        Options options = createOptions(args);

        Ln.initLogLevel(options.getLogLevel());

        scrcpy(options);
    }
}
