package com.telecomyt.videolibrary.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.base.BaseActivity;
import com.telecomyt.videolibrary.http.VideoHttpConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ProgressActivity extends BaseActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, ProgressActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutID() {
        EventBus.getDefault().register(this);
        return R.layout.activity_progress;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finishMyself(String s) {
        if (VideoHttpConfig.FINISH_PROGRESS.equals(s))
            finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {

    }
}
