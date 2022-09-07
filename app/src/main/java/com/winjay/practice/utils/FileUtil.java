package com.winjay.practice.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class FileUtil {

    private static String TAG = "FileUtil";
    private static String pattern = "null\\/.*";
    private static String APP_DIR = "Winjay";

    private FileUtil() {
        throw new UnsupportedOperationException("can't instantiate class" + TAG);
    }

    /**
     * 根据路径名获取文件对象
     *
     * @param filePath 文件路径
     * @return 返回一个文件对象
     */
    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    /**
     * 判断String字符串中是否有空白字符（空格，tab键，换行符）
     *
     * @param s 一个String类型的字符串
     * @return {@code true}: 参数为空或者一个不含空白字符的字符串 <br>{@code false}: otherwise
     */
    public static boolean isSpace(final String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            } else {
                throw new NumberFormatException("路径名包含空白字符");
            }
        }
        return true;
    }

    /**
     * 如果目录不存在，创建目录
     *
     * @param dirPath 目录路径
     * @return {@code true}:路径存在或者创建路径成功 <br>{@code false}:otherwise
     */
    public static boolean createOrExistsDir(final String dirPath) {
        return createOrExistsDir(getFileByPath(dirPath));
    }

    /**
     * 如果目录不存在，创建目录
     *
     * @param file 根据目录路径创建的File对象
     * @return {@code true}:路径存在或者创建路径成功 <br>{@code false}:otherwise
     */
    public static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 判断文件夹是否存在
     *
     * @param dirPath 文件夹路径
     * @return {@code true}:文件夹存在<br>{@code false}:otherwise
     */
    public static boolean isDirExists(final String dirPath) {
        return isDirExists(getFileByPath(dirPath));
    }

    /**
     * 判断文件夹是否存在
     *
     * @param file 根据目录路径创建的File对象
     * @return {@code true}:文件夹存在<br>{@code false}:otherwise
     */
    public static boolean isDirExists(final File file) {
        return file != null && file.isDirectory();
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return {@code true}:文件存在<br>{@code false}:otherwise
     */
    public static boolean isFileExists(final String filePath) {
        return isFileExists(getFileByPath(filePath));
    }

    /**
     * 判断文件是否存在
     *
     * @param file 根据文件路径创建的File对象
     * @return {@code true}:文件存在<br>{@code false}:otherwise
     */
    public static boolean isFileExists(final File file) {
        return file != null && file.exists();
    }

    /**
     * 如果文件不存在创建文件
     * Create a file if it doesn't exist, otherwise do nothing.
     *
     * @param filePath 文件路径.
     * @return {@code true}: 存在或者创建成功<br>{@code false}: otherwise
     */
    public static boolean createOrExistsFile(final String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }

    /**
     * 如果文件不存在创建文件
     * Create a file if it doesn't exist, otherwise do nothing.
     *
     * @param file 根据文件路径创建的文件对象.
     * @return {@code true}: 存在或者创建成功 <br>{@code false}: otherwise
     */
    public static boolean createOrExistsFile(final File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取设备存储路径
     *
     * @param context Context对象
     * @return 返回String类型的存储路径
     */
    public static String getCacheAbsolutePath(Context context) {
        // Check if media is mounted or storage is built-in, if so, try and use
        // external cache dir
        // otherwise use internal cache dir
        boolean shouldUseExternalCache = Environment.MEDIA_MOUNTED
                .equals(Environment.getExternalStorageState())
                || !isExternalStorageRemovable();

        File fileCacheDir = shouldUseExternalCache ? getExternalCacheDir(context)
                : context.getCacheDir();

        if (fileCacheDir == null) {
            fileCacheDir = context.getCacheDir();
        }

        final String cachePath = fileCacheDir.getPath();

        return cachePath + File.separator;
    }

    /**
     * Check if external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false
     * otherwise.
     */
    @TargetApi(9)
    private static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    /**
     * Get the external app cache directory.
     *
     * @param context The context to use
     * @return The external cache dir
     */
    @TargetApi(8)
    private static File getExternalCacheDir(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            return context.getExternalCacheDir();
        }

        return null;
    }

    /**
     * 将文本写入到文件中 每次调用，文件内容会先清空再写入新的内容
     *
     * @param fileName 文件路径名
     * @param content  要写入文件的内容
     */
    public static synchronized void saveStringToFile(String fileName, String content) {
        if (TextUtils.isEmpty(fileName) || fileName.matches(pattern)) {
            throw new RuntimeException("fileName 不能为null 或者文件名不能包含null");
        }

        File file = new File(fileName);
        FileWriter fw = null;
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            // fixed HuaWei C8813D file not found exception
            if (parent == null || !parent.exists()) {
                return;
            }
            fw = new FileWriter(file);
            fw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取文本文件内容
     *
     * @param filename 文件路径
     * @return 返回一个String类型的字符串内容
     */
    public static synchronized String readFileContent(String filename) {
        File file = new File(filename);
        if (!file.exists() || !file.isFile()) {
            return null;
        }

        FileReader fr = null;
        BufferedReader br = null;
        StringBuilder content = new StringBuilder();
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line = null;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fr != null) {
                try {
                    fr.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return content.toString();
    }

    /**
     * 将文本写入到文件中
     *
     * @param filePath 文件路径名
     * @param content  要写入文件的内容
     * @param isAppend 是否追加 {@code true}:写入新内容时不清空以前的内容 <br>{@code false}:otherwise
     */
    public static synchronized void saveStringToFile(final String filePath, final String content, final boolean isAppend) {
        if (TextUtils.isEmpty(filePath) || filePath.matches(pattern)) {
            throw new RuntimeException("filePath 不能为null 或者文件名不能为空");
        }
        FileWriter fw = null;
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            fw = new FileWriter(file, isAppend);
            fw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean copyFile(String srcPath, String destPath) {
        if (!new File(srcPath).exists()) {
            return false;
        }
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(srcPath);
            fileOutputStream = new FileOutputStream(destPath);
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 复制文件
     *
     * @param srcFile 源文件路径
     * @param destDir 目标目录
     * @return {@code true}:复制文件成功 <br>{@code false}:otherwise
     */
    public static boolean copyFileToDir(String srcFile, String destDir) {
        if (!createOrExistsDir(destDir)) {
//            throw new UnknownError("创建目录失败");
            return false;
        }
        String destFile = destDir + "/" + new File(srcFile).getName();
        try {
            InputStream streamFrom = new FileInputStream(srcFile);
            OutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[] = new byte[1024];
            int len;
            while ((len = streamFrom.read(buffer)) > 0) {
                streamTo.write(buffer, 0, len);
            }
            streamFrom.close();
            streamTo.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 获取指定文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小
     */
    public static long getFileSize(String filePath) {
        return getFileSize(getFileByPath(filePath));
    }

    /**
     * 获取指定文件大小
     *
     * @param file 根据文件路径获取的文件对象
     * @return 返回文件的大小
     * @throws Exception 抛出IO异常
     */
    public static long getFileSize(File file) {
        long size = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                size = fis.available();
                LogUtil.e(TAG, "获取文件大小:" + size);
            } else {
                throw new IOException("文件不存在!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return size;
    }

    /**
     * 小文件MD5加密
     *
     * @param filePath 文件路径
     * @return 加密之后的结果
     * @throws IOException 抛出IO异常
     */
    public static String FileMd5Encryption(String filePath) throws IOException {
        try {
            return Md5Util.getMD5(input2byte(new FileInputStream(filePath)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * InputStream转字节数组
     *
     * @param inStream 输入流对象
     * @return 字节数组
     * @throws IOException 抛出IO异常
     */
    public static byte[] input2byte(InputStream inStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }


    /**
     * 大文件加密方式
     *
     * @param filePath 文件路径
     * @return 返回加密之后的字符串
     */
    public static String BigFileMd5Encryption(String filePath) {
        //缓冲区大小
        int bufferSize = 256 * 1024;
        FileInputStream fileInputStream = null;
        DigestInputStream digestInputStream = null;
        MessageDigest messageDigest = null;
        try {
            //拿到MD5转换器
            messageDigest = MessageDigest.getInstance("MD5");
            //使用DigestInputStream
            fileInputStream = new FileInputStream(filePath);
            digestInputStream = new DigestInputStream(fileInputStream, messageDigest);
            //read的过程中进行MD5处理，直到读完文件
            byte[] buffer = new byte[bufferSize];
            while (digestInputStream.read(buffer) > 0) {
                //获取最终的MessageDigest
                messageDigest = digestInputStream.getMessageDigest();
            }
            //拿到结果，也就是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            //把字节数据转换成字符串
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                //关闭流对象
                digestInputStream.close();
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 字节数组转换成字符串
     *
     * @param byteArray 字节数组
     * @return 返回字符串
     */
    public static String byteArrayToHex(byte[] byteArray) {
        //首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        //new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符）
        char[] resultCharArray = new char[byteArray.length * 2];
        //遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        //字符数组组合成字符串返回
        return new String(resultCharArray);
    }

    /**
     * 判断文件是否存在
     *
     * @param context
     * @param path
     * @param apkName
     * @return
     */
    public static boolean isApkExist(Context context, String path, String apkName) {
        File file = new File(path, apkName);
        return file.exists();
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName 要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            LogUtil.d(TAG, "删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile()) {
                return deleteFile(fileName);
            } else {
                return deleteDirectory(fileName);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    private static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                LogUtil.d(TAG, "删除单个文件" + fileName + "成功！");
                return true;
            } else {
                LogUtil.d(TAG, "删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            LogUtil.d(TAG, "删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    private static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            LogUtil.d(TAG, "删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            LogUtil.d(TAG, "删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            LogUtil.d(TAG, "删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }


    /**
     * 计算Sdcard的剩余大小
     *
     * @return MB
     */
    public static long getAvailableSize() {
        //sd卡大小相关变量
        StatFs statFs;
        File file = Environment.getExternalStorageDirectory();
        statFs = new StatFs(file.getPath());
        //获得Sdcard上每个block的size
        long blockSize = statFs.getBlockSize();
        //获取可供程序使用的Block数量
        long blockavailable = statFs.getAvailableBlocks();
        //计算标准大小使用：1024，当然使用1000也可以
        long blockavailableTotal = blockSize * blockavailable / 1024 / 1024;
        return blockavailableTotal;
    }

    /**
     * SDCard 总容量大小
     *
     * @return MB
     */
    public static long getTotalSize() {
        StatFs statFs;
        File file = Environment.getExternalStorageDirectory();
        statFs = new StatFs(file.getPath());
        //获得sdcard上 block的总数
        long blockCount = statFs.getBlockCount();
        //获得sdcard上每个block 的大小
        long blockSize = statFs.getBlockSize();
        //计算标准大小使用：1024，当然使用1000也可以
        long bookTotalSize = blockCount * blockSize / 1024 / 1024;
        return bookTotalSize;
    }

    /**
     * 保存bitmap到本地
     *
     * @param bitmap
     * @return
     */
    public static File saveBitmap(Bitmap bitmap, String savePath) {
        if (TextUtils.isEmpty(savePath) || bitmap == null) {
            return null;
        }
        File filePic;
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + File.separator;
//        } else {
//            LogUtil.d("saveBitmap: 1return");
//            return null;
//        }
        try {
            filePic = new File(savePath + "Pic_" + System.currentTimeMillis() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        LogUtil.d(TAG, "saveBitmap: " + filePic.getAbsolutePath());
        return filePic;
    }

    /**
     * 文件夹删除
     */
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 压缩图片文件
     */
//    public static void compressPic(Context context, final File picFile, OnCompressListener listener) {
//        String savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//                .toString()
//                + File.separator;
//        Luban.with(context)
//                .load(picFile.getPath())
//                .ignoreBy(100)
//                .setTargetDir(savePath)
//                .filter(new CompressionPredicate() {
//                    @Override
//                    public boolean apply(String path) {
//                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
//                    }
//                })
//                .setCompressListener(listener).launch();
//    }
    public static boolean copyFolder(String oldPath, String newPath) {
        File newFile = new File(newPath);
        if (!newFile.exists()) {
            if (!newFile.mkdirs()) {
                Log.e(TAG, "copyFolder: cannot create directory.");
                return false;
            }
        }
        File oldFile = new File(oldPath);
        String[] files = oldFile.list();
        if (files == null || files.length == 0) {
            Log.w(TAG, "oldPath has no file!");
            return true;
        }
        File temp;
        for (String file : files) {
            if (oldPath.endsWith(File.separator)) {
                temp = new File(oldPath + file);
            } else {
                temp = new File(oldPath + File.separator + file);
            }
            Log.d(TAG, "temp=" + temp.getName());
            if (temp.isDirectory()) {   //如果是子文件夹
                boolean result = copyFolder(oldPath + "/" + file, newPath + "/" + file);
                if (!result) {
                    Log.e(TAG, "copy subfolder error!");
                    return false;
                }
            } else if (!temp.exists()) {
                Log.e(TAG, "copyFolder:  oldFile not exist.");
                return false;
            } else if (!temp.isFile()) {
                Log.e(TAG, "copyFolder:  oldFile not file.");
                return false;
            } else if (!temp.canRead()) {
                Log.e(TAG, "copyFolder:  oldFile cannot read.");
                return false;
            } else {
                try {
                    FileInputStream fileInputStream = new FileInputStream(temp);
                    FileOutputStream fileOutputStream = new FileOutputStream(newPath + "/" + temp.getName());
                    byte[] buffer = new byte[1024];
                    int byteRead;
                    while ((byteRead = fileInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, byteRead);
                    }
                    fileInputStream.close();
                    // 防止文件流因为拷贝结束立即拔出U盘导致的未完全写入文件的问题
                    fileOutputStream.getFD().sync();
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "copy error!");
                    return false;
                }
            }
        }
        Log.d(TAG, "copy success!");
        return true;
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /****
     * 计算文件大小
     *
     * @param length
     * @return
     */
    public String showLongFileSize(Long length) {
        if (length >= 1048576) {
            return (length / 1048576) + "MB";
        } else if (length >= 1024) {
            return (length / 1024) + "KB";
        } else if (length < 1024) {
            return length + "B";
        } else {
            return "0KB";
        }
    }

    /**
     * 计算U盘剩余空间大小
     *
     * @return B
     */
    public static long getAvailableSize(String path) {
        //sd卡大小相关变量
        StatFs statFs;
        File file = new File(path);
        statFs = new StatFs(file.getPath());
        //获得Sdcard上每个block的size
        long blockSize = statFs.getBlockSize();
        //获取可供程序使用的Block数量
        long blockavailable = statFs.getAvailableBlocks();
        //计算标准大小使用：1024，当然使用1000也可以
//        long blockavailableTotal = blockSize * blockavailable / 1024 / 1024;
        long blockavailableTotal = blockSize * blockavailable;
        return blockavailableTotal;
    }

//    /**
//     * 删除单个文件
//     * @param   filePath    被删除文件的文件名
//     * @return 文件删除成功返回true，否则返回false
//     */
//    public static boolean deleteFile(String filePath) {
//        File file = new File(filePath);
//        if (file.isFile() && file.exists()) {
//            return file.delete();
//        }
//        return false;
//    }
//
//    /**
//     * 删除文件夹以及目录下的文件
//     * @param   filePath 被删除目录的文件路径
//     * @return  目录删除成功返回true，否则返回false
//     */
//    public static boolean deleteDirectory(String filePath) {
//        boolean flag = false;
//        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
//        if (!filePath.endsWith(File.separator)) {
//            filePath = filePath + File.separator;
//        }
//        File dirFile = new File(filePath);
//        if (!dirFile.exists() || !dirFile.isDirectory()) {
//            return false;
//        }
//        flag = true;
//        File[] files = dirFile.listFiles();
//        //遍历删除文件夹下的所有文件(包括子目录)
//        for (int i = 0; i < files.length; i++) {
//            if (files[i].isFile()) {
//                //删除子文件
//                flag = deleteFile(files[i].getAbsolutePath());
//                if (!flag) break;
//            } else {
//                //删除子目录
//                flag = deleteDirectory(files[i].getAbsolutePath());
//                if (!flag) break;
//            }
//        }
//        if (!flag) return false;
//        //删除当前空目录
//        return dirFile.delete();
//    }
//
//    /**
//     *  根据路径删除指定的目录或文件，无论存在与否
//     *@param filePath  要删除的目录或文件
//     *@return 删除成功返回 true，否则返回 false。
//     */
//    public static boolean deleteFolder(String filePath) {
//        File file = new File(filePath);
//        if (!file.exists()) {
//            return false;
//        } else {
//            if (file.isFile()) {
//                // 为文件时调用删除文件方法
//                return deleteFile(filePath);
//            } else {
//                // 为目录时调用删除目录方法
//                return deleteDirectory(filePath);
//            }
//        }
//    }

    /**
     * 拷贝assets资源
     *
     * @param context
     * @param srcPath
     * @param dstPath
     */
    public static void copyAssetsToDst(Context context, String srcPath, String dstPath) {
        try {
            String fileNames[] = context.getAssets().list(srcPath);
            if (fileNames.length > 0) {
                File file = new File(dstPath);
                if (!file.exists()) file.mkdirs();
                for (String fileName : fileNames) {
                    if (!srcPath.equals("")) { // assets 文件夹下的目录
                        copyAssetsToDst(context, srcPath + File.separator + fileName, dstPath + File.separator + fileName);
                    } else { // assets 文件夹
                        copyAssetsToDst(context, fileName, dstPath + File.separator + fileName);
                    }
                }
            } else {
                File outFile = new File(dstPath);
                InputStream is = context.getAssets().open(srcPath);
                FileOutputStream fos = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            Log.w(TAG, "is not file!");
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "md5 calculate error!");
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    //过滤在mac上压缩时自动生成的__MACOSX文件夹
    private static final String MAC_IGNORE = "__MACOSX/";

    public static boolean upZipFile(String zipFile, String folderPath) {
        ZipFile zfile = null;
        try {
            // 转码为GBK格式，支持中文
            zfile = new ZipFile(zipFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.getName().contains(MAC_IGNORE)) {
                continue;
            }
            // 列举的压缩文件里面的各个文件，判断是否为目录
            if (ze.isDirectory()) {
                String dirstr = folderPath + File.separator + ze.getName();
//                Log.d(TAG, "dirstr=" + dirstr);
                dirstr.trim();
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            OutputStream os = null;
            FileOutputStream fos = null;
            // ze.getName()会返回 script/start.script这样的，是为了返回实体的File
            File realFile = getRealFileName(folderPath, ze.getName());
//            Log.d(TAG, "realFile=" + realFile.getAbsolutePath());
            try {
                fos = new FileOutputStream(realFile);
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
            os = new BufferedOutputStream(fos);
            InputStream is = null;
            try {
                is = new BufferedInputStream(zfile.getInputStream(ze));
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
            int readLen = 0;
            // 进行一些内容复制操作
            try {
                while ((readLen = is.read(buf, 0, 1024)) != -1) {
                    os.write(buf, 0, readLen);
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
        }
        try {
            zfile.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName) {
//        Log.d(TAG, "baseDir=" + baseDir + "------absFileName=" + absFileName);
        absFileName = absFileName.replace("\\", "/");
//        Log.d(TAG, "absFileName=" + absFileName);
        String[] dirs = absFileName.split("/");
//        Log.d(TAG, "dirs=" + Arrays.toString(dirs));
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                ret = new File(ret, substr);
            }

            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            ret = new File(ret, substr);
            return ret;
        } else {
            ret = new File(ret, absFileName);
        }
        return ret;
    }


    /////////////////////////////////////////// copy start ///////////////////////////////////////////
    public static final int CPU_SIZE = Runtime.getRuntime().availableProcessors();
    // 核心线程数为当前设备的CPU核心数加1
    public static final int CORE_POOL_SIZE = CPU_SIZE + 1;
    // 最大容量为CPU核心数的2倍加1
    public static final int MAX_POLL_SIZE = CPU_SIZE * 2 + 1;
    // 线程闲置超时时长60s
    private static final long KEEP_ALIVE = 60L;
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private AtomicInteger index = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "LogCopy-thread-" + index.getAndIncrement());
        }
    };
    private static final int DEFAULT_THREAD_NUM = 1;
    private static final ExecutorService sThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAX_POLL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(128), THREAD_FACTORY);

    private static final ExecutorService mExecutorService = Executors.newFixedThreadPool(ioIntensivePoolSize());

    /**
     * Each tasks blocks 90% of the time, and works only 10% of its
     * lifetime. That is, I/O intensive pool
     *
     * @return io intesive Thread pool size
     */

    public static int ioIntensivePoolSize() {
        double blockingCoefficient = 0.9;
        return poolSize(blockingCoefficient);
    }

    /**
     * Number of threads = Number of Available Cores / (1 - BlockingCoefficient) where the blocking
     * coefficient is between 0 and 1.
     * A computation-intensive task has a blocking coefficient of 0, whereas an IO-intensive task
     * has a value close to 1, so we don't have to worry about the value reaching 1.
     *
     * @param blockingCoefficient the coefficient
     * @return Thread pool size
     */
    public static int poolSize(double blockingCoefficient) {
        return (int) (CPU_SIZE / (1 - blockingCoefficient));
    }

    /**
     * 单线程拷贝文件
     *
     * @param sourcePath
     * @param targetPath
     */
    public static void copyFileRandom(String sourcePath, String targetPath) {
        copyFileRandom(sourcePath, targetPath, ioIntensivePoolSize());
    }

    /**
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @param threadNums 设定的线程数
     */
    @WorkerThread
    public static void copyFileRandom(String sourcePath, String targetPath, int threadNums) {
        if (threadNums <= 0) {
            threadNums = DEFAULT_THREAD_NUM;
        }

        //单线程拷贝
        if (threadNums == 1) {
            try {
                copyFileRandomSingleThread(sourcePath, targetPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //多线程拷贝
        } else {
            copyFileRandomMultiThread(sourcePath, targetPath, threadNums);
        }
    }

    /**
     * 单线程拷贝文件（随机读取，支持断点读写）
     *
     * @param src 源文件
     * @param dst 目标文件
     * @throws IOException
     */
    @WorkerThread
    public static void copyFileRandomSingleThread(String src, String dst) throws IOException {
        RandomAccessFile srcFile = new RandomAccessFile(src, "rw");
        RandomAccessFile dstFile = new RandomAccessFile(dst, "rw");

        try {
            long currentDstLength = dstFile.length();
            srcFile.seek(currentDstLength);
            dstFile.seek(currentDstLength);
            LogUtil.d(TAG, "Read " + src + " and Write " + dst + " from " + currentDstLength);

            byte[] buffer = new byte[1024];
            int read = -1;
            while ((read = srcFile.read(buffer)) != -1) {
                dstFile.write(buffer, 0, read);
            }
        } catch (IOException e) {
            LogUtil.w(TAG, "copyFileRandom exception, ", e);
        } finally {
            try {
                srcFile.close();
            } catch (IOException e) {
                LogUtil.w(TAG, "copyFileRandom close exception, ", e);
            }
            try {
                dstFile.close();
            } catch (IOException e) {
                LogUtil.w(TAG, "copyFileRandom close exception, ", e);
            }
        }
    }

    /**
     * 多线程拷贝文件
     *
     * @param sourcePath
     * @param targetPath
     * @param threadNums
     */
    private static void copyFileRandomMultiThread(String sourcePath, String targetPath, int threadNums) {
        long totalFileLength = new File(sourcePath).length();
        long currentTargetLength = new File(targetPath).length();

        long segmentLength = (totalFileLength - currentTargetLength) / (threadNums);
        //同步锁，全部写完，才能继续下一步处理
        CountDownLatch mCountDownLatch;
        //如果不可以整除,则需要多一个线程
        if (totalFileLength % threadNums != 0) {
            mCountDownLatch = new CountDownLatch(threadNums + 1);
        } else {
            mCountDownLatch = new CountDownLatch(threadNums);
        }

        int i;
        long start = System.currentTimeMillis();
        for (i = 0; i < threadNums; i++) {
            mExecutorService.execute(new CopyFileRunnable(mCountDownLatch, sourcePath, targetPath, i * segmentLength + currentTargetLength, (i + 1) * segmentLength));
        }

        if (totalFileLength % threadNums != 0) {
            mExecutorService.execute(new CopyFileRunnable(mCountDownLatch, sourcePath, targetPath, i * segmentLength + currentTargetLength, totalFileLength));
        }
        try {
            mCountDownLatch.await();
            LogUtil.d(TAG, "multi thread copyFile costs-->" + (System.currentTimeMillis() - start));
        } catch (InterruptedException e) {
            LogUtil.w(TAG, "countDownLatch exception: ", e);
        }
    }

    private static class CopyFileRunnable implements Runnable {
        private RandomAccessFile in;
        private RandomAccessFile out;
        private long start;
        private long end;
        private CountDownLatch latch;

        /**
         * @param countDownLatch 同步锁
         * @param in             源文件地址
         * @param out            目标文件地址
         * @param start          分段复制的开始位置
         * @param end            分段复制的结束位置
         */
        CopyFileRunnable(CountDownLatch countDownLatch, String in, String out,
                         long start, long end) {
            this.start = start;
            this.end = end;
            try {
                this.in = new RandomAccessFile(in, "rw");
                this.out = new RandomAccessFile(out, "rw");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            this.latch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                if (in != null && out != null) {
                    in.seek(start);
                    out.seek(start);
                    int hasRead = 0;
                    byte[] buff = new byte[1024];
                    while (start < end && (hasRead = in.read(buff)) != -1) {
                        start += hasRead;
                        out.write(buff, 0, hasRead);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                latch.countDown();
            }
        }
    }
    /////////////////////////////////////////// copy end ///////////////////////////////////////////
}
