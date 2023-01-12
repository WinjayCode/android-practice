package com.winjay.practice.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

/**
 * 执行adb指令
 */
public final class CommandUtil {
    private CommandUtil() {
        // not instantiable
    }

    public static void exec(String... cmd) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(cmd);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Command " + Arrays.toString(cmd) + " returned with value " + exitCode);
        }
    }

    public static String execReadLine(String... cmd) throws IOException, InterruptedException {
        String result = null;
        Process process = Runtime.getRuntime().exec(cmd);
        Scanner scanner = new Scanner(process.getInputStream());
        if (scanner.hasNextLine()) {
            result = scanner.nextLine();
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Command " + Arrays.toString(cmd) + " returned with value " + exitCode);
        }
        return result;
    }

    public static String runCMD(String cmd) throws Exception {
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
}
