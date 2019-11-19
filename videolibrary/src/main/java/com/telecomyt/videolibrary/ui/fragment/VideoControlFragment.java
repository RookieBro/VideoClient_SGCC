package com.telecomyt.videolibrary.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.adapter.VideoControlAdapter;
import com.telecomyt.videolibrary.base.BaseFragment;
import com.telecomyt.videolibrary.bean.VideoParticipantBean;
import com.telecomyt.videolibrary.callback.HttpPostCallback;
import com.telecomyt.videolibrary.gson.GetParticipantsResult;
import com.telecomyt.videolibrary.gson.GetVideoParticipantsHelper;
import com.telecomyt.videolibrary.utils.GsonUtils;
import com.telecomyt.videolibrary.utils.HttpUtil;
import com.telecomyt.videolibrary.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lbx
 * @date 2018/1/24.
 * 会控的控件
 */

public class VideoControlFragment extends BaseFragment {

    private RecyclerView rv_video_control_list;
    private VideoControlAdapter mAdapter;
    private List<VideoParticipantBean> mList;

    private String mRoomKey;
    private int mType;

    /**
     * @param type 0市局会议  1分局会议  2个人会议
     */
    public static VideoControlFragment newInstance(String roomKey, int type) {
        Bundle args = new Bundle();
        args.putString("roomKey", roomKey);
        args.putInt("type", type);
        VideoControlFragment fragment = new VideoControlFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_video_control;
    }

    @Override
    public void initArguments(Bundle bundle) {
        mRoomKey = bundle.getString("roomKey");
        mType = bundle.getInt("type");
    }

    @Override
    public void initView(View view) {
        rv_video_control_list = findView(R.id.rv_video_control_list);
        rv_video_control_list.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void initData() {
        mList = new ArrayList<>();
        getVideoParticipants();
    }

    @Override
    public void initListener() {

    }

    @Override
    protected void onResumeData() {

    }

    /**
     * 获取正在参会的人员
     */
    protected void getVideoParticipants() {
        HttpUtil.getInstance().post(new GetVideoParticipantsHelper(mRoomKey), new HttpPostCallback<String>() {
            @Override
            public void onSuccess(String msg) {
                GetParticipantsResult result = GsonUtils.getInstance().fromJson(msg, GetParticipantsResult.class);
                int code = result.code;
                mList.clear();
                if (code == 1) {
                    //成功
                    mList.addAll(result.participantsList);
                } else if (code == 2) {
                    //成功 但没人
                }
                refreshControlList();
                LogUtils.e("会议中的人数:" + (mList == null ? 0 : mList.size()));
            }

            @Override
            public void onFailure(String err) {
                LogUtils.e("查询会议人员失败:" + err.toString());
            }
        });
    }

    protected void refreshControlList() {
        refreshControlList(false);
    }

    protected void refreshControlList(boolean force) {
        if (mAdapter == null || force) {
            mAdapter = new VideoControlAdapter(getContext(), mList == null ? new ArrayList<VideoParticipantBean>() : mList);
            rv_video_control_list.setAdapter(mAdapter);
            mAdapter.setOnVideoControlClickListener(mOnVideoControlClickListener);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 会控的item回调
     */
    private VideoControlAdapter.OnVideoControlClickListener mOnVideoControlClickListener = new VideoControlAdapter.OnVideoControlClickListener() {

        @Override
        public void onHeardViewClick(VideoParticipantBean bean, ImageView view) {
            LogUtils.e("onHeardViewClick:" + bean.getDisplayName());
        }

        @Override
        public void onCameraViewClick(VideoParticipantBean bean, ImageView view) {
            LogUtils.e("onCameraViewClick:" + bean.getDisplayName());
        }

        @Override
        public void onMicViewClick(VideoParticipantBean bean, ImageView view) {
            LogUtils.e("onMicViewClick:" + bean.getDisplayName());
        }

        @Override
        public void onFinishViewClick(VideoParticipantBean bean, ImageView view) {
            LogUtils.e("onFinishViewClick:" + bean.getDisplayName());
        }
    };
}
