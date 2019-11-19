package com.telecomyt.videolibrary.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.telecomyt.videolibrary.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class LogUtils {
    /**
     * 日志输出级别NONE
     */
    public static final int LEVEL_NONE = 0;
    /**
     * 日志输出级别E
     */
    public static final int LEVEL_ERROR = 1;
    /**
     * 日志输出级别W
     */
    public static final int LEVEL_WARN = 2;
    /**
     * 日志输出级别I
     */
    public static final int LEVEL_INFO = 3;
    /**
     * 日志输出级别D
     */
    public static final int LEVEL_DEBUG = 4;
    /**
     * 日志输出级别V
     */
    public static final int LEVEL_VERBOSE = 5;

    /**
     * 日志输出时的TAG
     */
    private static String mTag = "xys";
    /**
     * 是否允许输出log
     */
    private static int mDebuggable = LEVEL_NONE;//LEVEL_VERBOSE  LEVEL_NONE

    /**
     * 以级别为 d 的形式输出LOG
     */
    public static void v(String msg) {
        if (mDebuggable >= LEVEL_VERBOSE) {
            if (msg.length() < 4000) {
                Log.v(mTag, msg);
            } else {
                String msg1 = msg.substring(0, 4000);
                String msg2 = msg.substring(4000, msg.length());
                Log.v(mTag, msg1);
                Log.v(mTag, msg2);
            }
        }
    }

    /**
     * 以级别为 d 的形式输出LOG
     */
    public static void d(String msg) {
        if (mDebuggable >= LEVEL_DEBUG) {
            if (msg.length() <= 3000) {
                Log.d(mTag, msg);
            } else if (msg.length() < 6000) {
                String msg1 = msg.substring(0, 3000);
                String msg2 = msg.substring(3000, msg.length());
                Log.d(mTag, "长度 6000  开始");
                Log.d(mTag, msg1);
                Log.d(mTag, msg2);
                Log.d(mTag, "长度 6000  结束");
            } else if (msg.length() < 9000) {
                String msg1 = msg.substring(0, 3000);
                String msg2 = msg.substring(3000, 6000);
                String msg3 = msg.substring(6000, msg.length());
                Log.d(mTag, "长度 9000  开始");
                Log.d(mTag, msg1);
                Log.d(mTag, msg2);
                Log.d(mTag, msg3);
                Log.d(mTag, "长度 9000  结束");
            } else if (msg.length() < 12000) {
                String msg1 = msg.substring(0, 3000);
                String msg2 = msg.substring(3000, 6000);
                String msg3 = msg.substring(6000, 9000);
                String msg4 = msg.substring(9000, msg.length());
                Log.d(mTag, "长度 12000  开始");
                Log.d(mTag, msg1);
                Log.d(mTag, msg2);
                Log.d(mTag, msg3);
                Log.d(mTag, msg4);
                Log.d(mTag, "长度 12000  结束");
            } else {
                String msg1 = msg.substring(0, 3000);
                String msg2 = msg.substring(3000, 6000);
                String msg3 = msg.substring(6000, 9000);
                String msg4 = msg.substring(9000, 12000);
                String msg5 = msg.substring(12000, msg.length());
                Log.d(mTag, "长度 12000 以上  开始");
                Log.d(mTag, msg1);
                Log.d(mTag, msg2);
                Log.d(mTag, msg3);
                Log.d(mTag, msg4);
                Log.d(mTag, msg5);
                Log.d(mTag, "长度 12000 以上  结束");
            }
        }
    }

    /**
     * 以级别为 i 的形式输出LOG
     */
    public static void i(String msg) {
        if (mDebuggable >= LEVEL_INFO) {
            if (msg.length() < 4000) {
                Log.i(mTag, msg);
            } else {
                String msg1 = msg.substring(0, 4000);
                String msg2 = msg.substring(4000, msg.length());
                Log.i(mTag, msg1);
                Log.i(mTag, msg2);
            }
        }
    }

    /**
     * 以级别为 w 的形式输出LOG
     */
    public static void w(String msg) {
        if (mDebuggable >= LEVEL_WARN) {
            if (msg.length() < 4000) {
                Log.w(mTag, msg);
            } else {
                String msg1 = msg.substring(0, 4000);
                String msg2 = msg.substring(4000, msg.length());
                Log.w(mTag, msg1);
                Log.w(mTag, msg2);
            }
        }
    }

    /**
     * 以级别为 w 的形式输出Throwable
     */
    public static void w(Throwable tr) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(mTag, "", tr);
        }
    }

    /**
     * 以级别为 w 的形式输出LOG信息和Throwable
     */
    public static void w(String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_WARN && null != msg) {
            if (msg.length() < 4000) {
                Log.w(mTag, msg);
            } else {
                String msg1 = msg.substring(0, 4000);
                String msg2 = msg.substring(4000, msg.length());
                Log.w(mTag, msg1);
                Log.w(mTag, msg2);
            }
        }
    }

    /**
     * 以级别为 e 的形式输出LOG
     */
    public static void e(String msg) {
        if (mDebuggable >= LEVEL_ERROR) {
            if (msg.length() <= 3000) {
                Log.e(mTag, msg);
            } else if (msg.length() < 6000) {
                String msg1 = msg.substring(0, 3000);
                String msg2 = msg.substring(3000, msg.length());
                Log.e(mTag, "长度 6000  开始");
                Log.e(mTag, msg1);
                Log.e(mTag, msg2);
                Log.e(mTag, "长度 6000  结束");
            } else if (msg.length() < 9000) {
                String msg1 = msg.substring(0, 3000);
                String msg2 = msg.substring(3000, 6000);
                String msg3 = msg.substring(6000, msg.length());
                Log.e(mTag, "长度 9000  开始");
                Log.e(mTag, msg1);
                Log.e(mTag, msg2);
                Log.e(mTag, msg3);
                Log.e(mTag, "长度 9000  结束");
            } else if (msg.length() < 12000) {
                String msg1 = msg.substring(0, 3000);
                String msg2 = msg.substring(3000, 6000);
                String msg3 = msg.substring(6000, 9000);
                String msg4 = msg.substring(9000, msg.length());
                Log.e(mTag, "长度 12000  开始");
                Log.e(mTag, msg1);
                Log.e(mTag, msg2);
                Log.e(mTag, msg3);
                Log.e(mTag, msg4);
                Log.e(mTag, "长度 12000  结束");
            } else {
                String msg1 = msg.substring(0, 3000);
                String msg2 = msg.substring(3000, 6000);
                String msg3 = msg.substring(6000, 9000);
                String msg4 = msg.substring(9000, 12000);
                String msg5 = msg.substring(12000, msg.length());
                Log.e(mTag, "长度 12000 以上  开始");
                Log.e(mTag, msg1);
                Log.e(mTag, msg2);
                Log.e(mTag, msg3);
                Log.e(mTag, msg4);
                Log.e(mTag, msg5);
                Log.e(mTag, "长度 12000 以上  结束");
            }
        }
    }

    /**
     * 以级别为 e 的形式输出Throwable
     */
    public static void e(Throwable tr) {
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(mTag, "", tr);
        }
    }

    /**
     * 以级别为 e 的形式输出LOG信息和Throwable
     */
    public static void e(String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_ERROR && null != msg) {
            if (msg.length() < 4000) {
                Log.e(mTag, msg);
            } else {
                String msg1 = msg.substring(0, 4000);
                String msg2 = msg.substring(4000, msg.length());
                Log.e(mTag, msg1);
                Log.e(mTag, msg2);
            }
        }
    }

    public static void writeFileData(Context c, String fileName, String message) {
        try {
            FileOutputStream fout = c.openFileOutput(fileName, MODE_PRIVATE);
            byte[] bytes = message.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String writeSDCardData(String data, String fileName) {
        String path;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return "没有SD卡";
        }
        File file = new File(path, fileName);
        FileOutputStream s = null;
        try {
            s = new FileOutputStream(file);
            s.write(data.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "FileNotFoundException:" + e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException:" + e.toString();
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return "success";
    }

    public static String writeLogFileByDate(String data) {
        return writeLogFileByDate(data, Config.DEFAULT_TAT, Config.DEFAULT_LOG_FILE_PATH);
    }

    public static String writeLogFileByDate(String data, String tag, String path) {
        synchronized (LogUtils.class) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH", Locale.CHINA);
            String name = format.format(new Date());
            File file = new File(path, name);
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String logTime = format.format(new Date());
            String log = logTime + File.separator + tag + File.separator + data;
            Log.e(tag, log);
            FileWriter s = null;
            try {
                s = new FileWriter(file, true);
                s.write("\n" + log);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "FileNotFoundException:" + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "IOException:" + e.toString();
            } finally {
                if (s != null) {
                    try {
                        s.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        return "success";
    }
}
