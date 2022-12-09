package com.winjay.scrcpy;

import android.graphics.Rect;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.view.Surface;

import com.winjay.scrcpy.wrappers.ServiceManager;
import com.winjay.scrcpy.wrappers.SurfaceControl;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DroidScreenEncoder implements Device.RotationListener {

    private static final int DEFAULT_I_FRAME_INTERVAL = 1; // seconds
    private static final int REPEAT_FRAME_DELAY_US = 100_000; // repeat after 100ms
    private static final String KEY_MAX_FPS_TO_ENCODER = "max-fps-to-encoder";

    // Keep the values in descending order
    private static final int[] MAX_SIZE_FALLBACK = {2560, 1920, 1600, 1280, 1024, 800};

    private static final long PACKET_FLAG_CONFIG = 1L << 63;
    private static final long PACKET_FLAG_KEY_FRAME = 1L << 62;

    private final AtomicBoolean rotationChanged = new AtomicBoolean();
    private final ByteBuffer headerBuffer = ByteBuffer.allocate(12);

    private final String encoderName;
    private final List<CodecOption> codecOptions;
    private final int bitRate;
    private final int maxFps;
    private final boolean sendFrameMeta;
    private final boolean downsizeOnError;
    private long ptsOrigin;

    private boolean firstFrameSent;

    public DroidScreenEncoder(boolean sendFrameMeta, int bitRate, int maxFps, List<CodecOption> codecOptions, String encoderName,
                              boolean downsizeOnError) {
        this.sendFrameMeta = sendFrameMeta;
        this.bitRate = bitRate;
        this.maxFps = maxFps;
        this.codecOptions = codecOptions;
        this.encoderName = encoderName;
        this.downsizeOnError = downsizeOnError;
    }

    @Override
    public void onRotationChanged(int rotation) {
        Ln.i("onRotationChanged:" + rotation);
        rotationChanged.set(true);
    }

    public boolean consumeRotationChange() {
        return rotationChanged.getAndSet(false);
    }

    public void streamScreen(Device device) throws IOException {
        Workarounds.prepareMainLooper();
        if (Build.BRAND.equalsIgnoreCase("meizu")) {
            // <https://github.com/Genymobile/scrcpy/issues/240>
            // <https://github.com/Genymobile/scrcpy/issues/2656>
            Workarounds.fillAppInfo();
        }

//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    Looper.prepare();
////                    Workarounds.fillAppInfo();
//                    ServiceManager serviceManager = new ServiceManager();
////                    serviceManager.getDisplayManager().fillAppInfo();
//                    serviceManager.getDisplayManager().createVirtualDisplay();
//                    Looper.loop();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();


        internalStreamScreen(device);
    }

    private void internalStreamScreen(Device device) throws IOException {
        MediaFormat format = createFormat(bitRate, maxFps, codecOptions);
        device.setRotationListener(this);
        boolean alive;
        try {
            do {
                MediaCodec codec = createCodec(encoderName);
                IBinder display = createDisplay();
                ScreenInfo screenInfo = device.getScreenInfo();
                Rect contentRect = screenInfo.getContentRect();
                // include the locked video orientation
                Rect videoRect = screenInfo.getVideoSize().toRect();
                // does not include the locked video orientation
                Rect unlockedVideoRect = screenInfo.getUnlockedVideoSize().toRect();
                int videoRotation = screenInfo.getVideoRotation();
                int layerStack = device.getLayerStack();
                Ln.i("videoRect.width=" + videoRect.width() + ", videoRect.height=" + videoRect.height());
                setSize(format, videoRect.width(), videoRect.height());

                format.setInteger(MediaFormat.KEY_BIT_RATE, videoRect.width() * videoRect.height() / 2);

                Surface surface = null;
                try {
                    configure(codec, format);
                    surface = codec.createInputSurface();
                    setDisplaySurface(display, surface, videoRotation, contentRect, unlockedVideoRect, layerStack);
                    codec.start();

//                    alive = encode(codec);
                    alive = encode2(codec);

                    // do not call stop() on exception, it would trigger an IllegalStateException
                    codec.stop();
                } catch (IllegalStateException | IllegalArgumentException e) {
                    Ln.e("Encoding error: " + e.getClass().getName() + ": " + e.getMessage());
                    if (!downsizeOnError || firstFrameSent) {
                        // Fail immediately
                        throw e;
                    }

                    int newMaxSize = chooseMaxSizeFallback(screenInfo.getVideoSize());
                    if (newMaxSize == 0) {
                        // Definitively fail
                        throw e;
                    }

                    // Retry with a smaller device size
                    Ln.i("Retrying with -m" + newMaxSize + "...");
                    device.setMaxSize(newMaxSize);
                    alive = true;
                } finally {
                    destroyDisplay(display);
                    codec.release();
                    if (surface != null) {
                        surface.release();
                    }
                }
            } while (alive);
        } finally {
            device.setRotationListener(null);
        }
    }

    private static int chooseMaxSizeFallback(Size failedSize) {
        int currentMaxSize = Math.max(failedSize.getWidth(), failedSize.getHeight());
        for (int value : MAX_SIZE_FALLBACK) {
            if (value < currentMaxSize) {
                // We found a smaller value to reduce the video size
                return value;
            }
        }
        // No fallback, fail definitively
        return 0;
    }

    private boolean isEncoding = true;

    public void stopStreamScreen() {
        isEncoding = false;
    }

    private boolean encode2(MediaCodec codec) {
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        while (isEncoding) {
            try {
                int outputBufferId = codec.dequeueOutputBuffer(bufferInfo, 10000);
                if (outputBufferId >= 0) {
                    ByteBuffer byteBuffer = codec.getOutputBuffer(outputBufferId);
                    // 拿到每一帧 如果是I帧 则在I帧前面插入 sps pps
                    dealFrame(byteBuffer, bufferInfo);
                    codec.releaseOutputBuffer(outputBufferId, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        return false;
    }

    public static final int NAL_I = 5;
    public static final int NAL_SPS = 7;
    private byte[] sps_pps_buf;

    /**
     * 绘制每一帧，因为录屏 只有第一帧有 sps 、pps 和 vps，所以我们需要在每一 I 帧 之前插入 sps 、pps 和 vps 的内容
     *
     * @param byteBuffer
     * @param bufferInfo
     */
    private void dealFrame(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        int offset = 4;
        if (byteBuffer.get(2) == 0x01) {
            offset = 3;
        }

        int type = byteBuffer.get(offset) & 0x1f;
        // sps_pps_buf 帧记录下来
        if (type == NAL_SPS) {
            sps_pps_buf = new byte[bufferInfo.size];
            byteBuffer.get(sps_pps_buf);
        } else if (type == NAL_I) {
            // I 帧 ，把 vps_sps_pps 帧塞到 I帧之前一起发出去
            final byte[] bytes = new byte[bufferInfo.size];
            byteBuffer.get(bytes);

            byte[] newBuf = new byte[sps_pps_buf.length + bytes.length];
            System.arraycopy(sps_pps_buf, 0, newBuf, 0, sps_pps_buf.length);
            System.arraycopy(bytes, 0, newBuf, sps_pps_buf.length, bytes.length);
            DroidSocketClientManager.getInstance().sendData(newBuf);
//            Ln.i("I帧 视频数据  " + bytes.length);
        } else {
            // B 帧 P 帧 直接发送
            final byte[] bytes = new byte[bufferInfo.size];
            byteBuffer.get(bytes);
            DroidSocketClientManager.getInstance().sendData(bytes);
//            Ln.i("B帧 P帧 视频数据  " + bytes.length);
        }
    }

    private boolean encode(MediaCodec codec) throws IOException {
        boolean eof = false;
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

        while (!consumeRotationChange() && !eof) {
            int outputBufferId = codec.dequeueOutputBuffer(bufferInfo, -1);
            Ln.i("outputBufferId=" + outputBufferId);
            eof = (bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0;
            Ln.i("eof=" + eof);
            try {
                if (consumeRotationChange()) {
                    // must restart encoding with new size
                    break;
                }
                if (outputBufferId >= 0) {
                    ByteBuffer codecBuffer = codec.getOutputBuffer(outputBufferId);

                    if (sendFrameMeta) {
//                        writeFrameMeta(bufferInfo, codecBuffer.remaining());
                    }

                    byte[] b = new byte[codecBuffer.remaining()];
                    codecBuffer.get(b, 0, b.length);
                    Ln.i("send buffer.size=" + b.length);
                    DroidSocketClientManager.getInstance().sendData(b);

//                    DroidConnection.sendData(b);

//                    DroidConnection.getOutputStream().write(b);
//                    DroidConnection.getOutputStream().flush();
//                    DroidConnection.getSocket().shutdownOutput();

                    if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) == 0) {
                        // If this is not a config packet, then it contains a frame
                        firstFrameSent = true;
                    }
                }
            } catch (Exception e) {
                Ln.i("encode error=" + e.getMessage());
            } finally {
                if (outputBufferId >= 0) {
                    Ln.i("releaseOutputBuffer");
                    codec.releaseOutputBuffer(outputBufferId, false);
                }
            }
        }

        return !eof;
    }

    private void writeFrameMeta(MediaCodec.BufferInfo bufferInfo, int packetSize) throws IOException {
        headerBuffer.clear();

        long pts;
        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            pts = PACKET_FLAG_CONFIG; // non-media data packet
        } else {
            if (ptsOrigin == 0) {
                ptsOrigin = bufferInfo.presentationTimeUs;
            }
            pts = bufferInfo.presentationTimeUs - ptsOrigin;
            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0) {
                pts |= PACKET_FLAG_KEY_FRAME;
            }
        }

        headerBuffer.putLong(pts);
        headerBuffer.putInt(packetSize);
        headerBuffer.flip();

        byte[] b = new byte[headerBuffer.remaining()];
        headerBuffer.get(b, 0, b.length);
//        Ln.i("buffer.size=" + b.length);
        DroidSocketClientManager.getInstance().sendData(b);

//        DroidConnection.sendData(b);
//        DroidConnection.getOutputStream().write(b);
//        DroidConnection.getOutputStream().flush();
//        DroidConnection.getSocket().shutdownOutput();
    }

    private static MediaCodecInfo[] listEncoders() {
        List<MediaCodecInfo> result = new ArrayList<>();
        MediaCodecList list = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
        for (MediaCodecInfo codecInfo : list.getCodecInfos()) {
            if (codecInfo.isEncoder() && Arrays.asList(codecInfo.getSupportedTypes()).contains(MediaFormat.MIMETYPE_VIDEO_AVC)) {
                result.add(codecInfo);
            }
        }
        return result.toArray(new MediaCodecInfo[result.size()]);
    }

    private static MediaCodec createCodec(String encoderName) throws IOException {
        if (encoderName != null) {
            Ln.d("Creating encoder by name: '" + encoderName + "'");
            try {
                return MediaCodec.createByCodecName(encoderName);
            } catch (IllegalArgumentException e) {
                MediaCodecInfo[] encoders = listEncoders();
                throw new InvalidEncoderException(encoderName, encoders);
            }
        }
        MediaCodec codec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        Ln.d("Using encoder: '" + codec.getName() + "'");
        return codec;
    }

    private static void setCodecOption(MediaFormat format, CodecOption codecOption) {
        String key = codecOption.getKey();
        Object value = codecOption.getValue();

        if (value instanceof Integer) {
            format.setInteger(key, (Integer) value);
        } else if (value instanceof Long) {
            format.setLong(key, (Long) value);
        } else if (value instanceof Float) {
            format.setFloat(key, (Float) value);
        } else if (value instanceof String) {
            format.setString(key, (String) value);
        }

        Ln.d("Codec option set: " + key + " (" + value.getClass().getSimpleName() + ") = " + value);
    }

    private static MediaFormat createFormat(int bitRate, int maxFps, List<CodecOption> codecOptions) {
        MediaFormat format = new MediaFormat();
        format.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_VIDEO_AVC);
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        // must be present to configure the encoder, but does not impact the actual frame rate, which is variable
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, DEFAULT_I_FRAME_INTERVAL);
        // display the very first frame, and recover from bad quality when no new frames
        format.setLong(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, REPEAT_FRAME_DELAY_US); // µs
        if (maxFps > 0) {
            // The key existed privately before Android 10:
            // <https://android.googlesource.com/platform/frameworks/base/+/625f0aad9f7a259b6881006ad8710adce57d1384%5E%21/>
            // <https://github.com/Genymobile/scrcpy/issues/488#issuecomment-567321437>
            format.setFloat(KEY_MAX_FPS_TO_ENCODER, maxFps);
        }

        if (codecOptions != null) {
            for (CodecOption option : codecOptions) {
                setCodecOption(format, option);
            }
        }

        return format;
    }

    private static IBinder createDisplay() {
        // Since Android 12 (preview), secure displays could not be created with shell permissions anymore.
        // On Android 12 preview, SDK_INT is still R (not S), but CODENAME is "S".
        boolean secure = Build.VERSION.SDK_INT < Build.VERSION_CODES.R || (Build.VERSION.SDK_INT == Build.VERSION_CODES.R && !"S"
                .equals(Build.VERSION.CODENAME));
        return SurfaceControl.createDisplay("scrcpy", secure);
    }

    private static void configure(MediaCodec codec, MediaFormat format) {
        codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
    }

    private static void setSize(MediaFormat format, int width, int height) {
        format.setInteger(MediaFormat.KEY_WIDTH, width);
        format.setInteger(MediaFormat.KEY_HEIGHT, height);
    }

    private static void setDisplaySurface(IBinder display, Surface surface, int orientation, Rect deviceRect, Rect displayRect, int layerStack) {
        SurfaceControl.openTransaction();
        try {
            SurfaceControl.setDisplaySurface(display, surface);
            SurfaceControl.setDisplayProjection(display, orientation, deviceRect, displayRect);
            SurfaceControl.setDisplayLayerStack(display, layerStack);
        } finally {
            SurfaceControl.closeTransaction();
        }
    }

    private static void destroyDisplay(IBinder display) {
        SurfaceControl.destroyDisplay(display);
    }
}
