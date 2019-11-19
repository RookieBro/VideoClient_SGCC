package com.telecomyt.videolibrary;

import android.app.Activity;
import android.content.Context;

import com.telecomyt.videolibrary.utils.FileUtils;
import com.telecomyt.videolibrary.utils.LogUtils;
import com.telecomyt.videolibrary.utils.UIUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lbx on 2017/9/7.
 */

public class VideoInit {

    public static String getAndroidCacheDir(Activity activity) throws IOException {
        File cacheDir = activity.getCacheDir();
        if (cacheDir != null) {
            return cacheDir.toString() + "/";
        }
        return null;
    }

    public static String getAndroidInternalMemDir(Activity activity) throws IOException {
        //crashing
        File fileDir = activity.getFilesDir();
        if (fileDir != null) {
            String filedir = fileDir.toString() + "/";
            LogUtils.d("file directory = " + filedir);
            return filedir;
        } else {
            LogUtils.e("Something went wrong, filesDir is null");
        }
        return null;
    }

    public static void fontName() {
        Context context = UIUtils.getContext();
        File dir = context.getFilesDir();
        String fileName = "System.vyf";
        String fileDir = dir + File.separator + fileName;
        File filesDir = new File(dir, "");
        FileUtils.CopyFileUtils.copyRawFile2FilesDir(context, filesDir, R.raw.system, fileName);
        VideoClient.getInstance().fontName(fileDir);
    }

    /**
     * 获取证书
     */
    public static String writeCaCertificates(Activity activity) {
        try {
            InputStream caCertStream = activity.getResources().openRawResource(R.raw.ca_certificates);
//			OnlyFile caCertFileName;
//			caCertFileName = getFileStreamPath("ca-certificates.crt");

            File caCertDirectory;
            try {
                String pathDir = VideoInit.getAndroidInternalMemDir(activity);
                caCertDirectory = new File(pathDir);
            } catch (Exception e) {
                caCertDirectory = activity.getDir("marina", 0);
            }
            File cafile = new File(caCertDirectory, "ca-certificates.crt");

            FileOutputStream caCertFile = new FileOutputStream(cafile);
            byte buf[] = new byte[1024];
            int len;
            while ((len = caCertStream.read(buf)) != -1) {
                caCertFile.write(buf, 0, len);
            }
            caCertStream.close();
            caCertFile.close();

            return cafile.getPath();
        } catch (Exception e) {
            return null;
        }
    }

}
