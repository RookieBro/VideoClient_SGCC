package com.telecomyt.videolibrary.ui.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.adapter.VideoParticipantsAdapter;
import com.telecomyt.videolibrary.base.BaseActivity;
import com.telecomyt.videolibrary.bean.VideoParticipantBean;
import com.telecomyt.videolibrary.callback.HttpPostCallback;
import com.telecomyt.videolibrary.event.VideoEvent;
import com.telecomyt.videolibrary.gson.GetParticipantsResult;
import com.telecomyt.videolibrary.gson.GetVideoParticipantsHelper;
import com.telecomyt.videolibrary.utils.GsonUtils;
import com.telecomyt.videolibrary.utils.HttpUtil;
import com.telecomyt.videolibrary.utils.LogUtils;
import com.telecomyt.videolibrary.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.telecomyt.videolibrary.utils.UIUtils.getContext;

/**
 * @author lbx
 */
public class VideoParticipantListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView mVideoParticipantListView;
    private LinearLayout videoParticipantLayout;
    private FrameLayout videoParticipantMainLayout;
    private List<VideoParticipantBean> mList;
    private VideoParticipantsAdapter mAdapter;

    private String mRoomKey;
    private int mOrientation;

    public static Intent start(Context context, String roomKey, int orientation) {
        Intent intent = new Intent(context, VideoParticipantListActivity.class);
        intent.putExtra("roomKey", roomKey);
        intent.putExtra("orientation", orientation);
        context.startActivity(intent);
        return intent;
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_video_participant_list;
    }

    @Override
    public void initIntent(Intent intent) {
        mRoomKey = intent.getStringExtra("roomKey");
        mOrientation = intent.getIntExtra("orientation", ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setRequestedOrientation(mOrientation == 0 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void initView(View view) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }
        mVideoParticipantListView = findView(R.id.lv_video_participant);
        videoParticipantLayout = findView(R.id.ll_video_participant);
        videoParticipantMainLayout = findView(R.id.fl_video_participant_main);
        FrameLayout.MarginLayoutParams layoutParams = (FrameLayout.MarginLayoutParams) videoParticipantLayout.getLayoutParams();
        layoutParams.width = UIUtils.dip2px(200);
        layoutParams.height = UIUtils.getScreenHeight() / 2;
    }

    @Override
    public void initData() {
        mList = new ArrayList<>();
        mAdapter = new VideoParticipantsAdapter(getContext(), mList == null ? new ArrayList<VideoParticipantBean>() : mList);
        mVideoParticipantListView.setAdapter(mAdapter);
        UIUtils.closeProgressDialog();
        getVideoParticipants();
    }

    @Override
    public void initListener() {
        mVideoParticipantListView.setOnItemClickListener(this);
        videoParticipantMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
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
                refreshList();
                LogUtils.e("会议中的人数:" + (mList == null ? 0 : mList.size()));
            }

            @Override
            public void onFailure(String err) {
                LogUtils.e("查询会议人员失败:" + err.toString());
            }

            @Override
            public void onFinished(boolean isSuccess) {
                super.onFinished(isSuccess);
                refreshList();
            }
        });
    }

    protected void refreshList() {
        refreshList(false);
    }

    protected void refreshList(boolean force) {
        if (mAdapter == null || force) {
            mAdapter = new VideoParticipantsAdapter(getContext(), mList == null ? new ArrayList<VideoParticipantBean>() : mList);
            mVideoParticipantListView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VideoParticipantBean videoParticipantBean = mList.get(position);
        EventBus.getDefault().post(new VideoEvent.VideoParticipantItemClick(videoParticipantBean, position));
        finish();
    }
}
