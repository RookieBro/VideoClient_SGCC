package com.telecomyt.videolibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * Created by lbx on 2017/09/07.
 */
public class FileUtils {
    public static String getDefaultPath() {
        String path = getSDCardPath();
        if (!TextUtils.isEmpty(path))
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        return getFilePath();
    }

    public static String getSDCardPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        return null;
    }

    private static String getFilePath() {
        return UIUtils.getContext().getFilesDir().getAbsolutePath();
    }

    public static String makeDirs(String path) {
        File savePathFile = new File(path);
        if (!savePathFile.exists() || !savePathFile.isDirectory()) {
            savePathFile.mkdirs();
        }
        return path;
    }

    public static class FileSizeUtils {

        public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
        public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
        public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
        public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值

        /**
         * 获取文件指定文件的指定单位的大小
         *
         * @param filePath 文件路径
         * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
         * @return double值的大小
         */
        public static double getFileOrFilesSize(String filePath, int sizeType) {
            File file = new File(filePath);
            long blockSize = 0;
            try {
                blockSize = file.isDirectory() ? getFileSizes(file) : getFileSize(file);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("获取文件大小", "获取失败!");
            }
            return FormatFileSize(blockSize, sizeType);
        }

        /**
         * 转换文件大小,指定转换的类型
         *
         * @param fileS
         * @param sizeType
         * @return
         */
        private static double FormatFileSize(long fileS, int sizeType) {
            DecimalFormat df = new DecimalFormat("#.00");
            double fileSizeLong = 0;
            switch (sizeType) {
                case SIZETYPE_B:
                    fileSizeLong = Double.valueOf(df.format((double) fileS));
                    break;
                case SIZETYPE_KB:
                    fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                    break;
                case SIZETYPE_MB:
                    fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                    break;
                case SIZETYPE_GB:
                    fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                    break;
                default:
                    break;
            }
            return fileSizeLong;
        }

        public static String getAutoFileOrFilesSize(String filePath) {
            File file = new File(filePath);
            long blockSize = 0;
            try {
                blockSize = file.isDirectory() ? getFileSizes(file) : getFileSize(file);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("获取文件大小", "获取失败!");
            }
            return FormatFileSize(blockSize);
        }

        private static String FormatFileSize(long fileS) {
            String size;
            String end;
            Double fileSizeString;
            if (fileS == 0) {
                return "0 KB";
            }
            if (fileS < 1024) {
                fileSizeString = (double) fileS;
                end = " B";
            } else if (fileS < 1048576) {
                fileSizeString = (double) fileS / 1024;
                end = " KB";
            } else if (fileS < 1073741824) {
                fileSizeString = (double) fileS / 1048576;
                end = " MB";
            } else {
                fileSizeString = (double) fileS / 1073741824;
                end = " GB";
            }
            fileSizeString = (double) Math.round(fileSizeString * 100) / 100;
            size = fileSizeString + end;
            return size;
        }

        /**
         * 转换文件大小
         *
         * @param fileS
         * @return
         */
        public static String FormatFileSize2String(long fileS) {
            DecimalFormat df = new DecimalFormat("######0.00");
            String fileSizeString = "";
            String wrongSize = "0";
            if (fileS == 0) {
                return wrongSize;
            }
            if (fileS < 1024) {
                fileSizeString = df.format((double) fileS) + "B";
            } else if (fileS < 1048576) {
                fileSizeString = df.format((double) fileS / 1024) + "KB";
            } else if (fileS < 1073741824) {
                fileSizeString = df.format((double) fileS / 1048576) + "MB";
            } else {
                fileSizeString = df.format((double) fileS / 1073741824) + "G";
            }
            return fileSizeString;
        }

        /**
         * 获取指定文件大小
         *
         * @param file
         * @return
         * @throws Exception
         */
        public static long getFileSize(File file) throws Exception {
            long size = 0;
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
            } else {
                file.createNewFile();
                Log.e("获取文件大小", "文件不存在!");
            }
            return size;
        }

        /**
         * 获取指定文件夹
         *
         * @param f
         * @return
         * @throws Exception
         */
        private static long getFileSizes(File f) throws Exception {
            long size = 0;
            File flist[] = f.listFiles();
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getFileSizes(flist[i]);
                } else {
                    size = size + getFileSize(flist[i]);
                }
            }
            return size;
        }
    }

    public static class OpenFileUtil {

        /**
         * 打开文件
         *
         * @param
         */
        public static void openFile(String path, Context c) {
            File file = new File(path);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //设置intent的Action属性
            intent.setAction(Intent.ACTION_VIEW);
            //获取文件file的MIME类型
            String type = getMIMEType(file);
            //设置intent的data和Type属性。
            intent.setDataAndType(/**uri*/Uri.fromFile(file), type);
            //跳转
            c.startActivity(intent);
        }

        /**
         * 根据文件后缀名获得对应的MIME类型。
         *
         * @param file
         */
        private static String getMIMEType(File file) {
            String type = "*/*";
            String fName = file.getName();
            //获取后缀名前的分隔符"."在fName中的位置。
            int dotIndex = fName.lastIndexOf(".");
            if (dotIndex < 0) {
                return type;
            }
           /* 获取文件的后缀名*/
            String end = fName.substring(dotIndex, fName.length()).toLowerCase();
            if (TextUtils.isEmpty(end)) {
                return type;
            }
            //在MIME和文件类型的匹配表中找到对应的MIME类型。
            for (int i = 0; i < MIME_MapTable.length; i++) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
                if (end.equals(MIME_MapTable[i][0])) {
                    type = MIME_MapTable[i][1];
                }
            }
            return type;
        }

        private static final String[][] MIME_MapTable = {
                //{后缀名，MIME类型}
                {".3gp", "video/3gpp"},
                {".apk", "application/vnd.android.package-archive"},
                {".asf", "video/x-ms-asf"},
                {".avi", "video/x-msvideo"},
                {".bin", "application/octet-stream"},
                {".bmp", "image/bmp"},
                {".c", "text/plain"},
                {".class", "application/octet-stream"},
                {".conf", "text/plain"},
                {".cpp", "text/plain"},
                {".doc", "application/msword"},
                {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
                {".xls", "application/vnd.ms-excel"},
                {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
                {".exe", "application/octet-stream"},
                {".gif", "image/gif"},
                {".gtar", "application/x-gtar"},
                {".gz", "application/x-gzip"},
                {".h", "text/plain"},
                {".htm", "text/html"},
                {".html", "text/html"},
                {".jar", "application/java-archive"},
                {".java", "text/plain"},
                {".jpeg", "image/jpeg"},
                {".jpg", "image/jpeg"},
                {".js", "application/x-javascript"},
                {".log", "text/plain"},
                {".m3u", "audio/x-mpegurl"},
                {".m4a", "audio/mp4a-latm"},
                {".m4b", "audio/mp4a-latm"},
                {".m4p", "audio/mp4a-latm"},
                {".m4u", "video/vnd.mpegurl"},
                {".m4v", "video/x-m4v"},
                {".mov", "video/quicktime"},
                {".mp2", "audio/x-mpeg"},
                {".mp3", "audio/x-mpeg"},
                {".mp4", "video/mp4"},
                {".mpc", "application/vnd.mpohun.certificate"},
                {".mpe", "video/mpeg"},
                {".mpeg", "video/mpeg"},
                {".mpg", "video/mpeg"},
                {".mpg4", "video/mp4"},
                {".mpga", "audio/mpeg"},
                {".msg", "application/vnd.ms-outlook"},
                {".ogg", "audio/ogg"},
                {".pdf", "application/pdf"},
                {".png", "image/png"},
                {".pps", "application/vnd.ms-powerpoint"},
                {".ppt", "application/vnd.ms-powerpoint"},
                {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
                {".prop", "text/plain"},
                {".rc", "text/plain"},
                {".rmvb", "audio/x-pn-realaudio"},
                {".rtf", "application/rtf"},
                {".sh", "text/plain"},
                {".tar", "application/x-tar"},
                {".tgz", "application/x-compressed"},
                {".txt", "text/plain"},
                {".wav", "audio/x-wav"},
                {".wma", "audio/x-ms-wma"},
                {".wmv", "audio/x-ms-wmv"},
                {".wps", "application/vnd.ms-works"},
                {".xml", "text/plain"},
                {".z", "application/x-compress"},
                {".zip", "application/x-zip-compressed"},
                {"", "*/*"}
        };
    }

    public static class CopyFileUtils {

        public static void copyFile2FilesDir(Context context, String fileName) {
            File filesDir = context.getFilesDir();
            File targetFile = new File(filesDir, fileName);
            //    /data/data/com.lunry.jwmeeting/files
            LogUtils.e("filesDir =" + filesDir);
            // 先判断文件是否存在,如果存在,无需拷贝
            if (targetFile.exists()) {
                LogUtils.e(fileName + "文件已存在");
                return;
            }
            InputStream in = null;
            FileOutputStream out = null;
            try {
                AssetManager assets = context.getAssets();
                in = assets.open(fileName);
//            getResources().getAssets()
                out = new FileOutputStream(targetFile);
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                LogUtils.e(fileName + "拷贝完成");
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.e("没有文件：" + fileName + "   " + e.toString());
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public static String copyRawFile2FilesDir(Context context, File filesDir, int fileId, String saveName) {
            File targetFile = new File(filesDir, saveName);
            //    /data/data/com.lunry.jwmeeting/files
            // 先判断文件是否存在,如果存在,无需拷贝
            if (targetFile.exists()) {
                LogUtils.e(targetFile.getAbsolutePath() + "文件已存在");
                return targetFile.getAbsolutePath();
            }
            InputStream in = null;
            FileOutputStream out = null;
            try {
                in = context.getResources().openRawResource(fileId);
//            getResources().getAssets()
                out = new FileOutputStream(targetFile);
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                LogUtils.e(saveName + "拷贝完成");
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.e("没有文件：" + fileId + "   " + e.toString());
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LogUtils.e("targetFile.getAbsolutePath() = " + targetFile.getAbsolutePath());
            return targetFile.getAbsolutePath();
        }

        public static String copyRawFile2FilesDir(Context context, int fileId, String saveName) {
            return copyRawFile2FilesDir(context, context.getFilesDir(), fileId, saveName);
        }

        public static boolean copyFile(String oldPath, String newPath, String newName) {
            return copyFile(oldPath, newPath, newName, null, -1);
        }

        public static boolean copyFile(String oldPath, String newPath, String newName, Handler handler, int msgWht) {
            FileUtils.makeDirs(newPath);
            try {
                int bytesum = 0;
                int byteread = 0;
                File oldfile = new File(oldPath);
                if (oldfile.exists()) { //文件存在时
                    double maxSize = FileUtils.FileSizeUtils.getFileOrFilesSize(oldPath, FileUtils.FileSizeUtils.SIZETYPE_MB);
                    InputStream inStream = new FileInputStream(oldPath); //读入原文件
                    FileOutputStream fs = new FileOutputStream(newPath + "/" + newName);
                    byte[] buffer = new byte[1024 * 103];//102.4进103
                    float length = 0.0f;
                    while ((byteread = inStream.read(buffer)) != -1) {
                        bytesum += byteread; //字节数 文件大小
                        System.out.println(bytesum);
                        fs.write(buffer, 0, byteread);
                        length += 0.1f;
                        if (handler != null) {
                            Message message = Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putFloat("finishSize", length);//0.1M
                            bundle.putDouble("maxSize", maxSize);//文件总大小
                            message.what = msgWht;
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                    inStream.close();
                } else {
                    return false;
                }
            } catch (Exception e) {
                System.out.println("复制单个文件操作出错");
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}