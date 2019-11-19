package com.telecomyt.videolibrary.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.view.watermark.WaterMarkFragment;

public abstract class BaseFragment extends Fragment {

    private View view;
    private int mReplaceLayoutId;
    private WaterMarkFragment mWaterFragment;
    private Bundle mArguments;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(getLayoutID(), container, false);
        getBaseArguments();
        initView(view);
        if (Config.showWaterView) {
            mReplaceLayoutId = addWaterMarkView();
            if (mReplaceLayoutId != 0 && mWaterFragment == null) {
                mWaterFragment = new WaterMarkFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(mReplaceLayoutId, mWaterFragment).commitAllowingStateLoss();
            }
        }
        initData();
        initListener();
        return this.view;
    }

    private void getBaseArguments() {
        mArguments = getArguments();
        if (mArguments != null) {
            initArguments(mArguments);
        }
    }

    public void initArguments(Bundle bundle) {

    }

    @Override
    public void onResume() {
        super.onResume();
        onResumeData();
        if (mWaterFragment != null) {
            mWaterFragment.refreshWmtv();
        }
    }


    public abstract int getLayoutID();

    public abstract void initView(View view);

    public int addWaterMarkView() {
        return 0;
    }

    public abstract void initData();

    public abstract void initListener();

    protected abstract void onResumeData();

    protected <T> T findView(int viewID) {
        return (T) view.findViewById(viewID);
    }
}
