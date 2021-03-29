package com.winjay.practice.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CMDUtil {

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
