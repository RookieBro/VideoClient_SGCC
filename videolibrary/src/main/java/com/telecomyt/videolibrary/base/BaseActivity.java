package com.telecomyt.videolibrary.base;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.utils.ActivityUtil;
import com.telecomyt.videolibrary.view.watermark.WaterMarkFragment;

/**
 * Created by lbx on 2017/9/7.
 * activity基类
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {


    private int mReplaceLayoutId;
    private WaterMarkFragment mWaterFragment;
    protected Intent baseIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        ActivityUtil.addActivity(this);
        setContentView(getLayoutID());
        View view = findViewById(android.R.id.content);
        getBaseIntent();
        initView(view);
        if (Config.showWaterView) {
            mReplaceLayoutId = addWaterMarkView();
            if (mReplaceLayoutId != 0 && mWaterFragment == null) {
                mWaterFragment = new WaterMarkFragment();
                getSupportFragmentManager().beginTransaction().replace(mReplaceLayoutId, mWaterFragment).commitAllowingStateLoss();
            }
        }
        initData();
        initListener();
    }

    protected <T> T findView(int viewID) {
        return (T) findViewById(viewID);
    }

    public abstract int getLayoutID();

    private void getBaseIntent() {
        baseIntent = getIntent();
        if (baseIntent != null) {
            initIntent(baseIntent);
        }
    }

    public void initIntent(Intent intent) {

    }

    public abstract void initView(View view);

    public int addWaterMarkView() {
        return 0;
    }

    public abstract void initData();

    public void initListener() {

    }

    public void onClick(View view, int id) {

    }

    @Override
    public void onClick(View v) {
        onClick(v, v.getId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityUtil.removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWaterFragment != null) {
            mWaterFragment.refreshWmtv();
        }
    }
}
