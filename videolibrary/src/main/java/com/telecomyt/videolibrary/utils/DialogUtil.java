package com.telecomyt.videolibrary.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.telecomyt.videolibrary.R;

/**
 * Created by lbx on 2017/9/28.
 */

public class DialogUtil {

    public static Dialog normalBuild(Activity ac, String title, String dec, View.OnClickListener sureClick) {
        final Dialog dialog = new Dialog(ac, R.style.Theme_MyDialog);
        View view = View.inflate(ac, R.layout.dialog_normal, null);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        TextView tv_dialog_title = (TextView) view.findViewById(R.id.tv_dialog_title);
        TextView tv_dialog_dec = (TextView) view.findViewById(R.id.tv_dialog_dec);
        Button btn_dialog_cancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
        Button btn_dialog_sure = (Button) view.findViewById(R.id.btn_dialog_sure);
        tv_dialog_title.setText(title);
        tv_dialog_dec.setText(dec);
        btn_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        btn_dialog_sure.setOnClickListener(sureClick);
        dialog.show();
        return dialog;
    }

}
