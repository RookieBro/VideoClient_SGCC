package com.telecomyt.videolibrary.ui.fragment;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.VideoClient;
import com.telecomyt.videolibrary.adapter.DefaultRoomAdapter;
import com.telecomyt.videolibrary.base.AdapterBase;
import com.telecomyt.videolibrary.base.BaseFragment;
import com.telecomyt.videolibrary.bean.DefaultVideoRoom;
import com.telecomyt.videolibrary.bean.VideoDefaultMeeting;
import com.telecomyt.videolibrary.callback.HttpPostCallback;
import com.telecomyt.videolibrary.callback.VideoListCallback;
import com.telecomyt.videolibrary.gson.DeleteVideoRoomHelper;
import com.telecomyt.videolibrary.gson.GetOwenTwoLevelID;
import com.telecomyt.videolibrary.gson.GetVideoRoomHelper;
import com.telecomyt.videolibrary.gson.GetVideoRoomRequest;
import com.telecomyt.videolibrary.http.VideoHttpConfig;
import com.telecomyt.videolibrary.manager.VideoRoomJoinClickManager;
import com.telecomyt.videolibrary.ui.activity.VideoHistoryActivity;
import com.telecomyt.videolibrary.utils.DialogUtil;
import com.telecomyt.videolibrary.utils.HttpUtil;
import com.telecomyt.videolibrary.utils.LogUtils;
import com.telecomyt.videolibrary.utils.SpUtil;
import com.telecomyt.videolibrary.utils.TimeUtils;
import com.telecomyt.videolibrary.utils.UIUtils;
import com.telecomyt.videolibrary.view.CircleTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lbx on 2017/9/25.
 */

public class VideoListFragment extends BaseFragment {

    private ListView lv_video_list;
    private Button btn_definition;
    private List<GetVideoRoomRequest.Result.PersonalMeeting> mListNormal;  //普通会议
    //    private List<GetVideoRoomRequest.PersonalMeeting> mListCityDefault;  //市局默认群组
//    private List<GetVideoRoomRequest.PersonalMeeting> mListBranchesDefault;  //分局默认群组
    private List<DefaultVideoRoom> mListDefault;  //默认群组
    private MyAdapter mAdapter;
    private DefaultRoomAdapter<DefaultVideoRoom, VideoDefaultMeeting> mAdapterDefault;
    private Button btn_createVideo, btn_videoHistory;
    private VideoListCallback mVideoListCallback;
    private SwipeRefreshLayout rl_videoList;
    private TextView tv_videoRoomEmpty;
    private LinearLayout ll_createVideo, ll_videoHistory;
    private ExpandableListView lv_defaultRoom;
    public final static int D_SIZE = 0;//默认
    public final static int H_SIZE = 8;//高清
    public static int CURRENT_SIZE = H_SIZE;

    public static VideoListFragment newInstance() {
        Bundle args = new Bundle();
        VideoListFragment fragment = new VideoListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutID() {
        EventBus.getDefault().register(this);
        return R.layout.fragment_video_list;
    }

    @Override
    public void initView(View view) {
        btn_createVideo = findView(R.id.btn_createVideo);
        btn_videoHistory = findView(R.id.btn_videoHistory);
        lv_video_list = findView(R.id.lv_video_list);
        rl_videoList = findView(R.id.rl_videoList);
        tv_videoRoomEmpty = findView(R.id.tv_videoRoomEmpty);
        ll_createVideo = findView(R.id.ll_createVideo);
        ll_videoHistory = findView(R.id.ll_videoHistory);
        lv_defaultRoom = findView(R.id.lv_defaultRoom);
        btn_definition = findView(R.id.btn_definition);

        rl_videoList.setColorSchemeResources(R.color.blue_light, R.color.blue);
        //清晰度
        CURRENT_SIZE = SpUtil.getInt(Config.MEETING_SIZE, H_SIZE);
        //设置清晰度图片
        btn_definition.setText(getDefinitionText(CURRENT_SIZE));
        VideoClient.getInstance().SetVideoResolution(CURRENT_SIZE);
    }

    @Override
    public int addWaterMarkView() {
        return R.id.fl_watermark_videoList;
    }

    @Override
    public void initData() {
        FragmentActivity activity = getActivity();
        if (activity instanceof VideoListCallback) {
            mVideoListCallback = (VideoListCallback) activity;
        }
        mListNormal = new ArrayList<>();
        mListDefault = new ArrayList<>();
        mAdapter = new MyAdapter(activity, mListNormal, R.layout.item_join_conference, new ViewHolder());
        lv_video_list.setAdapter(mAdapter);

        mAdapterDefault = new DefaultRoomAdapter<>(mListDefault, getActivity());
        lv_defaultRoom.setAdapter(mAdapterDefault);

        ObjectAnimator animatorX = ObjectAnimator
                .ofFloat(btn_definition, "scaleX", 0.2f, 1.0f)
                .setDuration(300);
        animatorX.setInterpolator(new BounceInterpolator());
        animatorX.start();
        ObjectAnimator animatorY = ObjectAnimator
                .ofFloat(btn_definition, "scaleY", 0.2f, 1.0f)
                .setDuration(300);
        animatorY.setInterpolator(new BounceInterpolator());
        animatorY.start();
    }

    @Override
    public void initListener() {

        ll_videoHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoHistoryActivity.start(getActivity());
            }
        });
        btn_videoHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoHistoryActivity.start(getActivity());
            }
        });
        btn_createVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mVideoListCallback != null) {
                    mVideoListCallback.createVideoRoomClick();
                }
            }
        });
        ll_createVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mVideoListCallback != null) {
                    mVideoListCallback.createVideoRoomClick();
                }
            }
        });

        rl_videoList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshInfo();
            }
        });
        lv_video_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GetVideoRoomRequest.Result.PersonalMeeting personalMeeting = mAdapter.getItem(i);
                Config.currentVideoRoom = personalMeeting;
                TextView textView = (TextView) view.findViewById(R.id.tv_state);
                if (getString(R.string.appointment).equals(textView.getText().toString().trim())) {
                    UIUtils.showToast("会议还没有开始");
                    return;
                }
                VideoRoomJoinClickManager clickManager = VideoClient.getVideoRoomJoinClickManager();
                if (clickManager != null) {
                    clickManager.onPersonalVideoClick(getActivity(), personalMeeting);
                }
            }
        });

        lv_video_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final GetVideoRoomRequest.Result.PersonalMeeting personalMeeting = mAdapter.getItem(i);
                final Dialog[] dialogs = new Dialog[1];
                final Dialog dialog = DialogUtil.normalBuild(getActivity(), "删除会议", "确定要删除会议吗？", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteVideoRoom(personalMeeting);
                        if (dialogs[0] != null) {
                            dialogs[0].dismiss();
                        }
                    }
                });
                dialogs[0] = dialog;
                return true;
            }
        });

        lv_defaultRoom.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int g, int c, long l) {
                VideoDefaultMeeting info = (VideoDefaultMeeting) mAdapterDefault.getListDefault().get(g).getList().get(c);
                VideoRoomJoinClickManager clickManager = VideoClient.getVideoRoomJoinClickManager();
                if (clickManager != null) {
                    clickManager.onDefaultVideoClick(getActivity(), info);
                }
                return true;
            }
        });
        btn_definition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CURRENT_SIZE == D_SIZE) {
                    CURRENT_SIZE = H_SIZE;
                } else if (CURRENT_SIZE == H_SIZE) {
                    CURRENT_SIZE = D_SIZE;
                }
                SpUtil.putInt(Config.MEETING_SIZE, CURRENT_SIZE);
                //设置清晰度图片
                btn_definition.setText(getDefinitionText(CURRENT_SIZE));
                VideoClient.getInstance().SetVideoResolution(CURRENT_SIZE);
            }
        });
    }

    private String getDefinitionText(int definition) {
        if (definition == D_SIZE) {
            return "流畅";
        } else if (definition == H_SIZE) {
            return "高清";
        }
        CURRENT_SIZE = H_SIZE;
        return "高清";
    }

    private void deleteVideoRoom(GetVideoRoomRequest.Result.PersonalMeeting personalMeeting) {
        rl_videoList.setRefreshing(true);
        HttpUtil.getInstance().post(new DeleteVideoRoomHelper(personalMeeting.meetingID), new HttpPostCallback<String>() {
            @Override
            public void onSuccess(String msg) {
                try {
                    JSONObject object = new JSONObject(msg);
                    int code = object.getInt("code");
                    String message = object.getString("message");
                    if (code == 1) {
                        UIUtils.showToast(getString(R.string.delete_room_success));
                        refreshInfo();
                    } else {
                        UIUtils.showToast(getString(R.string.delete_room_fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String err) {
                UIUtils.showToast(err);
            }

            @Override
            public void onFinished(boolean isSuccess) {
                super.onFinished(isSuccess);
                rl_videoList.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResumeData() {
        LogUtils.e("onResumeData");
        if (TextUtils.isEmpty(Config.twoLevelAreaID)) {
            getOwenTwoLevelID();
        } else {
            refreshInfo();
        }
    }

    private void getOwenTwoLevelID() {
        rl_videoList.setRefreshing(true);
        HttpUtil.getInstance().post(new GetOwenTwoLevelID(), new HttpPostCallback<String>() {
            @Override
            public void onSuccess(String msg) {
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    int code = jsonObject.getInt("code");
                    if (code == 1) {
                        JSONObject object = jsonObject.getJSONObject("result");
                        Config.twoLevelAreaID = object.getString("twoLevelAreaID");
                        refreshInfo();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String err) {
                rl_videoList.setRefreshing(false);
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshVideoRoom(String s) {
        if (VideoHttpConfig.VIDEO_ROOM_REFRESH.equals(s)) {
            refreshInfo();
        }
    }

    /**
     * 添加列表数据信息
     */
    private void refreshInfo() {
        if (TextUtils.isEmpty(Config.twoLevelAreaID)) {
            getOwenTwoLevelID();
            return;
        }
        rl_videoList.setRefreshing(true);
        HttpUtil.getInstance().post(new GetVideoRoomHelper(0), new HttpPostCallback<String>() {
            @Override
            public void onSuccess(String msg) {
                //TODO
//                msg = Test.json;
                GetVideoRoomRequest meeting = new Gson().fromJson(msg, GetVideoRoomRequest.class);
                if (meeting.code == 1) {
                    List<GetVideoRoomRequest.Result.PersonalMeeting> listAll = meeting.result.personalMeeting;
                    mListNormal.clear();
                    mListDefault.clear();

                    //添加普通会议
                    if (listAll != null) {
                        for (GetVideoRoomRequest.Result.PersonalMeeting info : listAll) {
                            mListNormal.add(info);
                        }
                    }

                    //添加一级默认会议
                    List<GetVideoRoomRequest.Result.OneLevelMeeting> listOne = meeting.result.oneLevelMeeting;
                    if (listOne == null) {
                        listOne = new ArrayList<>();
                    }
                    DefaultVideoRoom<GetVideoRoomRequest.Result.OneLevelMeeting> one =
                            new DefaultVideoRoom<>(meeting.result.oneLevelName, listOne);
                    mListDefault.add(one);

                    //添加二级默认会议
                    List<GetVideoRoomRequest.Result.TwoLevelMeeting> listTwo = meeting.result.twoLevelMeeting;
                    if (listTwo == null) {
                        listTwo = new ArrayList<>();
                    }
                    DefaultVideoRoom<GetVideoRoomRequest.Result.TwoLevelMeeting> two =
                            new DefaultVideoRoom<>(meeting.result.twoLevelName, listTwo);
                    mListDefault.add(two);

                    //添加领导组默认会议
                    List<GetVideoRoomRequest.Result.LeaderLevelMeeting> listLeader = meeting.result.leaderLevelMeeting;
                    if (listLeader == null) {
                        listLeader = new ArrayList<>();
                    }
                    DefaultVideoRoom<GetVideoRoomRequest.Result.LeaderLevelMeeting> leader =
                            new DefaultVideoRoom<>(meeting.result.leaderLevelName, listLeader);
                    mListDefault.add(leader);

                    //刷新
                    mAdapter.setData(mListNormal);
                    mAdapterDefault.setList(mListDefault);
                } else {
//                    UIUtils.showToast(meeting.message);
                }
            }

            @Override
            public void onFailure(String err) {
                UIUtils.showToast(err);
            }

            @Override
            public void onFinished(boolean isSuccess) {
                super.onFinished(isSuccess);
                refreshEmptyView();
                rl_videoList.setRefreshing(false);
            }
        });
    }

    private void refreshEmptyView() {
        if (mAdapter != null) {
            tv_videoRoomEmpty.setVisibility(mAdapter.getCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    private class MyAdapter extends AdapterBase<GetVideoRoomRequest.Result.PersonalMeeting, ViewHolder> {

        private MyAdapter(Context context, List<GetVideoRoomRequest.Result.PersonalMeeting> list, int layoutId, ViewHolder holder) {
            super(context, list, layoutId, holder);
        }

        @Override
        public void bindView(View convertView, ViewHolder holder) {
            holder.tv_Author = (TextView) convertView.findViewById(R.id.tv_Author);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_conference);
            holder.tv_state = (TextView) convertView.findViewById(R.id.tv_state);
            holder.iv_state = (ImageView) convertView.findViewById(R.id.iv_state);
            holder.ctv_head = (CircleTextView) convertView.findViewById(R.id.ctv_head);
        }

        @Override
        public void setData(ViewHolder holder, GetVideoRoomRequest.Result.PersonalMeeting data, List<GetVideoRoomRequest.Result.PersonalMeeting> list, int pos) {

            holder.tv_title.setText(data.meetingName);

            int soonColor = Color.parseColor("#000000");
            int waitColor = Color.parseColor("#acacac");

            boolean meetingStarting = TimeUtils.isBeforeNow(data.meetingStartTime, 1000 * 60 * 5);
            String meetingStartTime = data.meetingStartTime.substring(0, data.meetingEndTime.lastIndexOf(":"));
            if (meetingStartTime.substring(0, 5).endsWith(TimeUtils.formatNowTime().substring(0, 5))) {
                meetingStartTime = meetingStartTime.substring(5, meetingStartTime.length());
            }

            holder.tv_title.setTextColor(meetingStarting ? soonColor : waitColor);
            holder.tv_time.setText(meetingStartTime);
            holder.tv_time.setTextColor(meetingStarting ? soonColor : waitColor);
            holder.tv_Author.setText(data.meetingAuthor);
            holder.tv_Author.setTextColor(meetingStarting ? soonColor : waitColor);
            holder.tv_state.setText(meetingStarting ? getString(R.string.starting) : getString(R.string.appointment));
            holder.tv_state.setTextColor(meetingStarting ? soonColor : waitColor);
            holder.iv_state.setBackgroundResource(meetingStarting ?
                    R.drawable.meeting_start : R.drawable.meeting_wait);
            String author = data.meetingAuthor;
            holder.ctv_head.setTextWithColorAndSize(author.length() <= 2 ? author :
                    author.substring(author.length() - 2, author.length()), R.color.white_main, 18);
        }
    }

    private static class ViewHolder extends AdapterBase.BaseHolder {
        private TextView tv_title, tv_time, tv_Author, tv_state;
        private ImageView iv_state;
        private CircleTextView ctv_head;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
