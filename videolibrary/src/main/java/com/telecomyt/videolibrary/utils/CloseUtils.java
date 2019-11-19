package com.telecomyt.videolibrary.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by lbx on 2017/9/7.
 */

public class CloseUtils {

    public static void close(Closeable stream) {
        try {
            if (stream != null)
                stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
