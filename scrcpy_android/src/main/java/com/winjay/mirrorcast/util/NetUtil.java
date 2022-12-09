package com.winjay.mirrorcast.util;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author F2848777
 * @date 2022-09-19
 */
public class NetUtil {
    private static final String TAG = "NetUtil";

    public static InetAddress getP2PInetAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (!intf.getName().contains("p2p")) {
                    continue;
                }
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        LogUtil.d(TAG, "p2p ip=" + inetAddress.getHostAddress());
                        return inetAddress;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String wifiIpAddress() {
        try {
            InetAddress ipv4 = null;
            InetAddress ipv6 = null;
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface int_f = en.nextElement();
                if (!int_f.getName().contains("p2p")) {
                    continue;
                }
                for (Enumeration<InetAddress> enumIpAddr = int_f.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet6Address) {
                        ipv6 = inetAddress;
                        continue;
                    }
                    if (inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        ipv4 = inetAddress;
                        continue;
                    }
                    return inetAddress.getHostAddress();
                }
            }
            if (ipv6 != null) {
                return ipv6.getHostAddress();
            }
            if (ipv4 != null) {
                return ipv4.getHostAddress();
            }
            return null;
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
