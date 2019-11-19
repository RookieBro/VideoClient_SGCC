package com.telecomyt.videolibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Administrator on 2017/1/8.
 */

public class SoftInputUtil {

//    android:windowSoftInputMode="stateHidden|stateUnchanged"

    //隐藏软件盘
    public static void hintSoftInput(Activity a) {
        View mv = a.getWindow().peekDecorView();
        if (mv != null) {
            InputMethodManager inputmanger = (InputMethodManager) a
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(mv.getWindowToken(), 0);
        }
    }

    public static void showSoftInput(EditText editText) {
        //弹出软键盘
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }
}
