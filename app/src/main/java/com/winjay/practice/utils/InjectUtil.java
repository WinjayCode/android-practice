package com.winjay.practice.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 模拟事件（只能操作本应用，不能操作其他应用！！！）
 *
 * @author Winjay
 * @date 2022-11-02
 */
public class InjectUtil {
    private static final String TAG = InjectUtil.class.getSimpleName();

    /**
     * 模拟屏幕点击事件，点击坐标（x,y）
     *
     * @param x
     * @param y
     */
    public static int[] click(int x, int y) {
        int[] xy = {x, y};
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        List<String> commands = new ArrayList<String>();
        commands.add("input");
        commands.add("tap");
        commands.add("" + x);
        commands.add("" + y);
        ProcessBuilder pb = new ProcessBuilder(commands);
        try {
            Process prs = pb.start();
            is = prs.getInputStream();
            byte[] b = new byte[1024];
            int size = 0;
            baos = new ByteArrayOutputStream();
            while ((size = is.read(b)) != -1) {
                baos.write(b, 0, size);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (baos != null) baos.close();
            } catch (Exception ex) {
            }
        }
        return xy;
    }

    /**
     * 模拟屏幕滑动事件，从（x,y）滑动到（newX，newY）
     *
     * @param x
     * @param y
     * @param newX
     * @param newY
     */
    public static void slide(int x, int y, int newX, int newY) {
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        List<String> commands = new ArrayList<String>();
        commands.add("input");
        commands.add("swip");
        commands.add("" + x);
        commands.add("" + y);
        commands.add("" + newX);
        commands.add("" + newY);
        ProcessBuilder pb = new ProcessBuilder(commands);
        try {
            Process prs = pb.start();
            is = prs.getInputStream();
            byte[] b = new byte[1024];
            int size = 0;
            baos = new ByteArrayOutputStream();
            while ((size = is.read(b)) != -1) {
                baos.write(b, 0, size);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.w(TAG, "slide: " + e.getMessage());
        } finally {
            try {
                if (is != null) is.close();
                if (baos != null) baos.close();
            } catch (Exception ex) {
                LogUtil.w(TAG, "slide: " + ex.getMessage());
            }
        }
    }
}
