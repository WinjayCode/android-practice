package com.winjay.practice.bluetooth;

import android.os.ParcelUuid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * @author Winjay
 * @date 2021-06-08
 */
public class BluetoothUuid {
    /* See Bluetooth Assigned Numbers document - SDP section, to get the values of UUIDs
     * for the various services.
     *
     * The following 128 bit values are calculated as:
     *  uuid * 2^96 + BASE_UUID
     */

    public static final ParcelUuid AudioSink =
            ParcelUuid.fromString("0000110B-0000-1000-8000-00805F9B34FB");

    public static final ParcelUuid AudioSource =
            ParcelUuid.fromString("0000110A-0000-1000-8000-00805F9B34FB");

    public static final ParcelUuid AdvAudioDist =
            ParcelUuid.fromString("0000110D-0000-1000-8000-00805F9B34FB");

    public static final ParcelUuid HSP =
            ParcelUuid.fromString("00001108-0000-1000-8000-00805F9B34FB");

    public static final ParcelUuid HSP_AG =
            ParcelUuid.fromString("00001112-0000-1000-8000-00805F9B34FB");

    public static final ParcelUuid Handsfree =
            ParcelUuid.fromString("0000111E-0000-1000-8000-00805F9B34FB");

    public static final ParcelUuid Handsfree_AG =
            ParcelUuid.fromString("0000111F-0000-1000-8000-00805F9B34FB");

    public static final ParcelUuid AvrcpController =
            ParcelUuid.fromString("0000110E-0000-1000-8000-00805F9B34FB");

    public static final ParcelUuid AvrcpTarget =
            ParcelUuid.fromString("0000110C-0000-1000-8000-00805F9B34FB");

    public static final ParcelUuid ObexObjectPush =
            ParcelUuid.fromString("00001105-0000-1000-8000-00805f9b34fb");

    public static final ParcelUuid Hid =
            ParcelUuid.fromString("00001124-0000-1000-8000-00805f9b34fb");

    public static final ParcelUuid Hogp =
            ParcelUuid.fromString("00001812-0000-1000-8000-00805f9b34fb");

    public static final ParcelUuid PANU =
            ParcelUuid.fromString("00001115-0000-1000-8000-00805F9B34FB");


    public static final ParcelUuid NAP =
            ParcelUuid.fromString("00001116-0000-1000-8000-00805F9B34FB");

    public static final ParcelUuid BNEP =
            ParcelUuid.fromString("0000000f-0000-1000-8000-00805F9B34FB");

    public static final ParcelUuid PBAP_PCE =
            ParcelUuid.fromString("0000112e-0000-1000-8000-00805F9B34FB");


    public static final ParcelUuid PBAP_PSE =
            ParcelUuid.fromString("0000112f-0000-1000-8000-00805F9B34FB");

    public static final ParcelUuid MAP =
            ParcelUuid.fromString("00001134-0000-1000-8000-00805F9B34FB");

    public static final ParcelUuid MNS =
            ParcelUuid.fromString("00001133-0000-1000-8000-00805F9B34FB");

    public static final ParcelUuid MAS =
            ParcelUuid.fromString("00001132-0000-1000-8000-00805F9B34FB");

    public static final ParcelUuid SAP =
            ParcelUuid.fromString("0000112D-0000-1000-8000-00805F9B34FB");

    public static final ParcelUuid HearingAid =
            ParcelUuid.fromString("0000FDF0-0000-1000-8000-00805f9b34fb");


    public static final ParcelUuid BASE_UUID =
            ParcelUuid.fromString("00000000-0000-1000-8000-00805F9B34FB");

    /**
     * Length of bytes for 16 bit UUID
     */

    public static final int UUID_BYTES_16_BIT = 2;
    /**
     * Length of bytes for 32 bit UUID
     */

    public static final int UUID_BYTES_32_BIT = 4;
    /**
     * Length of bytes for 128 bit UUID
     */

    public static final int UUID_BYTES_128_BIT = 16;

    public static final ParcelUuid[] RESERVED_UUIDS = {
            AudioSink, AudioSource, AdvAudioDist, HSP, Handsfree, AvrcpController, AvrcpTarget,
            ObexObjectPush, PANU, NAP, MAP, MNS, MAS, SAP};


    public static boolean is16BitUuid(ParcelUuid parcelUuid) {
        UUID uuid = parcelUuid.getUuid();
        if (uuid.getLeastSignificantBits() != BASE_UUID.getUuid().getLeastSignificantBits()) {
            return false;
        }
        return ((uuid.getMostSignificantBits() & 0xFFFF0000FFFFFFFFL) == 0x1000L);
    }

    /**
     * Check whether the given parcelUuid can be converted to 32 bit bluetooth uuid.
     *
     * @param parcelUuid
     * @return true if the parcelUuid can be converted to 32 bit uuid, false otherwise.
     */
    public static boolean is32BitUuid(ParcelUuid parcelUuid) {
        UUID uuid = parcelUuid.getUuid();
        if (uuid.getLeastSignificantBits() != BASE_UUID.getUuid().getLeastSignificantBits()) {
            return false;
        }
        if (is16BitUuid(parcelUuid)) {
            return false;
        }
        return ((uuid.getMostSignificantBits() & 0xFFFFFFFFL) == 0x1000L);
    }

    public static byte[] uuidToBytes(ParcelUuid uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("uuid cannot be null");
        }

        if (is16BitUuid(uuid)) {
            byte[] uuidBytes = new byte[UUID_BYTES_16_BIT];
            int uuidVal = getServiceIdentifierFromParcelUuid(uuid);
            uuidBytes[0] = (byte) (uuidVal & 0xFF);
            uuidBytes[1] = (byte) ((uuidVal & 0xFF00) >> 8);
            return uuidBytes;
        }

        if (is32BitUuid(uuid)) {
            byte[] uuidBytes = new byte[UUID_BYTES_32_BIT];
            int uuidVal = getServiceIdentifierFromParcelUuid(uuid);
            uuidBytes[0] = (byte) (uuidVal & 0xFF);
            uuidBytes[1] = (byte) ((uuidVal & 0xFF00) >> 8);
            uuidBytes[2] = (byte) ((uuidVal & 0xFF0000) >> 16);
            uuidBytes[3] = (byte) ((uuidVal & 0xFF000000) >> 24);
            return uuidBytes;
        }

        // Construct a 128 bit UUID.
        long msb = uuid.getUuid().getMostSignificantBits();
        long lsb = uuid.getUuid().getLeastSignificantBits();

        byte[] uuidBytes = new byte[UUID_BYTES_128_BIT];
        ByteBuffer buf = ByteBuffer.wrap(uuidBytes).order(ByteOrder.LITTLE_ENDIAN);
        buf.putLong(8, msb);
        buf.putLong(0, lsb);
        return uuidBytes;
    }

    public static int getServiceIdentifierFromParcelUuid(ParcelUuid parcelUuid) {
        UUID uuid = parcelUuid.getUuid();
        long value = (uuid.getMostSignificantBits() & 0xFFFFFFFF00000000L) >>> 32;
        return (int) value;
    }
}
