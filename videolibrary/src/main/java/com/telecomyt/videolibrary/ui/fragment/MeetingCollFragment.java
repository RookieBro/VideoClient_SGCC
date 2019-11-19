package com.telecomyt.videolibrary.ui.fragment;

import android.view.View;
import android.widget.RelativeLayout;

import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.base.BaseFragment;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017/7/13.
 * 会议控制界面（暂时为空）
 */

public class MeetingCollFragment extends BaseFragment {


    @Override
    public int getLayoutID() {
        return R.layout.fragment_meeting_coll;
    }

    @Override
    public void initView(View view) {
        RelativeLayout rl_meeting_coll = findView(R.id.rl_meeting_coll);
        rl_meeting_coll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(Config.MEETING_MENU);
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }

    @Override
    protected void onResumeData() {

    }
}
