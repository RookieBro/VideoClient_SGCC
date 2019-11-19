package com.telecomyt.videolibrary.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.VideoClient;
import com.telecomyt.videolibrary.base.BaseFragment;
import com.telecomyt.videolibrary.bean.GzbVideoUser;
import com.telecomyt.videolibrary.callback.CreateVideoCallback;
import com.telecomyt.videolibrary.callback.HttpPostCallback;
import com.telecomyt.videolibrary.gson.CreateVideoRoomHelper;
import com.telecomyt.videolibrary.gson.CreateVideoRoomRequest;
import com.telecomyt.videolibrary.gson.GetVideoRoomRequest;
import com.telecomyt.videolibrary.http.VideoHttpConfig;
import com.telecomyt.videolibrary.manager.VideoRoomJoinClickManager;
import com.telecomyt.videolibrary.utils.GsonUtils;
import com.telecomyt.videolibrary.utils.HttpUtil;
import com.telecomyt.videolibrary.utils.LogUtils;
import com.telecomyt.videolibrary.utils.TimeUtils;
import com.telecomyt.videolibrary.utils.UIUtils;
import com.telecomyt.videolibrary.view.PersonView;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author lbx
 * @date 2017/9/25
 * 创建会议
 */

public class CreateVideoFragment extends BaseFragment {

    private TextView tv_title, tv_meeting_1, tv_meeting_2, tv_startTime, tv_endTime;
    private PersonView addPersonView;
    private LinearLayout ll_addPerson, ll_meetingInfo, ll_meeting_1, ll_meeting_2;
    private RelativeLayout rl_con_main;
    private EditText et_meetingName, et_dec;
    private ImageView iv_meeting_1, iv_meeting_2;
    private TimePickerDialog timeDialog;
    private Button btn_sure;
    private CreateVideoCallback mCreateVideoCallback;

    /**
     * 召开会议模式
     * 立即召开
     */
    private final int MEETING_CHOOSE_CREATE = 0;
    /**
     * 召开会议模式
     * 预约会议
     */
    private final int MEETING_CHOOSE_DELAYED = 1;
    private int MEETING_CHOOSE_CURRENT = MEETING_CHOOSE_CREATE;

    /**
     * 会议开始时间
     */
    private long meetingStartTime = -1;
    /**
     * 会议结束时间
     */
    private long meetingEndTime = -1;
    private GzbVideoUser[] mUsers = new GzbVideoUser[0];
    private Map<String, GzbVideoUser> mUserMap;


    public static CreateVideoFragment newInstance() {
        Bundle args = new Bundle();
        CreateVideoFragment fragment = new CreateVideoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_video_create;
    }

    @Override
    public void initView(View view) {
        ll_addPerson = findView(R.id.ll_addPerson);
        et_meetingName = findView(R.id.et_meetingName);
        et_dec = findView(R.id.et_dec);
        ll_meetingInfo = findView(R.id.ll_meetingInfo);
        ll_meeting_1 = findView(R.id.ll_meeting_1);
        ll_meeting_2 = findView(R.id.ll_meeting_2);
        iv_meeting_1 = findView(R.id.iv_meeting_1);
        iv_meeting_2 = findView(R.id.iv_meeting_2);
        tv_meeting_1 = findView(R.id.tv_meeting_1);
        tv_meeting_2 = findView(R.id.tv_meeting_2);
        tv_startTime = findView(R.id.tv_startTime);
        btn_sure = findView(R.id.btn_sure);
        tv_endTime = findView(R.id.tv_endTime);
        initAddBtn();
    }

    @Override
    public int addWaterMarkView() {
        return R.id.fl_watermark_createVideo;
    }

    @Override
    public void initData() {
        mUserMap = new HashMap<>();
        FragmentActivity activity = getActivity();
        if (activity instanceof CreateVideoCallback) {
            mCreateVideoCallback = (CreateVideoCallback) activity;
        }
    }

    @Override
    public void initListener() {
        ll_meeting_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //召开会议
                MEETING_CHOOSE_CURRENT = MEETING_CHOOSE_CREATE;
                changeMeetingClick();
                dismissTimeDialog();
            }
        });
        ll_meeting_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //预约会议
                MEETING_CHOOSE_CURRENT = MEETING_CHOOSE_DELAYED;
                changeMeetingClick();
                showTimeDialog();
            }
        });
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCreateVideoCallback != null) {
                    String title = et_meetingName.getText().toString().trim();
                    String dec = et_dec.getText().toString().trim();
                    mUsers = mCreateVideoCallback.addVideoRoomUser(title, dec, MEETING_CHOOSE_CURRENT, meetingStartTime, meetingEndTime);
                    if (mUsers == null) {
                        mUsers = new GzbVideoUser[0];
                    }
                    initCreateVideo(title, dec, mUsers, tv_startTime.getText().toString(), tv_endTime.getText().toString());
                }
            }
        });
    }

    private void initCreateVideo(String title, String dec, GzbVideoUser[] ids, String startTime, String endTime) {
        if (isEmpty(ids)) {
            return;
        }
        if (!Config.VIDEO_LOGIN) {
            LogUtils.writeLogFileByDate("正在登陆，请稍后再试！");
            LogUtils.writeLogFileByDate("initCreateVideo Config.VIDEO_LOGIN:" + Config.VIDEO_LOGIN);
            return;
        }
        //立即召开
        if (MEETING_CHOOSE_CURRENT == MEETING_CHOOSE_CREATE) {
            LogUtils.e("立即召开");
            LogUtils.writeLogFileByDate("initCreateVideo MEETING_CHOOSE_CREATE");
            createVideoRoom(title, dec, mUsers, startTime, endTime);
            //预约会议
        } else if (MEETING_CHOOSE_CURRENT == MEETING_CHOOSE_DELAYED) {
            LogUtils.writeLogFileByDate("initCreateVideo MEETING_CHOOSE_DELAYED");
            String s = tv_startTime.getText().toString().trim();
            LogUtils.e("预约会议");
            if (s.contains("开始时间")) {
                s = s.replace("开始时间", "");
            }
            boolean b = UIUtils.textIsEmpty(s, "请先选择会议时间");
            if (b) {
                LogUtils.writeLogFileByDate("initCreateVideo MEETING_CHOOSE_DELAYED time empty");
                return;
            }
            createVideoRoom(title, dec, mUsers, startTime, endTime);
        }
        LogUtils.writeLogFileByDate("initCreateVideo else empty error");
    }

    public void addUsers(GzbVideoUser[] users) {
        for (GzbVideoUser u : users) {
            mUserMap.put(u.id, u);
            LogUtils.e("添加了：" + u.toString());
        }
        refreshPerson();
    }

    public void setUsers(GzbVideoUser[] users) {
        mUserMap.clear();
        for (GzbVideoUser u : users) {
            mUserMap.put(u.id, u);
            LogUtils.e("添加了：" + u.toString());
        }
        refreshPerson();
    }

    private void createVideoRoom(final String title, final String dec, final GzbVideoUser[] users, final String startTime, final String endTime) {
        btn_sure.setEnabled(false);
        btn_sure.setText("请稍后...");
        String[] ids;
        if (users != null) {
            ids = new String[users.length];
            int i = 0;
            for (GzbVideoUser u : users) {
                ids[i++] = u.id;
            }
        } else {
            ids = new String[0];
        }
        //立即召开
        final boolean b = MEETING_CHOOSE_CURRENT == MEETING_CHOOSE_CREATE;
        LogUtils.writeLogFileByDate("createVideoRoom MEETING_CHOOSE_CURRENT == MEETING_CHOOSE_CREATE? :" + b);
        String meetingStartTime = b ? TimeUtils.formatNowTime() : TimeUtils.formatTime(this.meetingStartTime);
        String meetingEndTime = b ? TimeUtils.formatTime(System.currentTimeMillis() + 1000 * 60 * 60) :
                TimeUtils.formatTime(this.meetingEndTime);
        CreateVideoRoomHelper createVideoRoomHelper = new CreateVideoRoomHelper(title, TextUtils.isEmpty(dec) ? "" :
                dec, meetingStartTime, meetingEndTime, ids);
        LogUtils.writeLogFileByDate("createVideoRoom CreateVideoRoomHelper:" + createVideoRoomHelper.toString());
        HttpUtil.getInstance().post(createVideoRoomHelper, new HttpPostCallback<String>() {
            @Override
            public void onSuccess(String msg) {
                CreateVideoRoomRequest roomRequest = GsonUtils.getInstance().fromJson(msg, CreateVideoRoomRequest.class);
                if (roomRequest == null) {
                    roomRequest = new CreateVideoRoomRequest();
                }
                CreateVideoRoomRequest room = roomRequest;
                CreateVideoRoomRequest.Result result = room.result;
                if (result == null) {
                    result = new CreateVideoRoomRequest.Result();
                }
                String roomKey = result.roomKey;
                if (!TextUtils.isEmpty(roomKey) && b) {
                    LogUtils.writeLogFileByDate("createVideoRoom VideoActivity start begin");
                    Config.currentVideoRoom = GetVideoRoomRequest.Result.PersonalMeeting.create(room);
                    VideoRoomJoinClickManager clickManager = VideoClient.getVideoRoomJoinClickManager();
                    if (clickManager != null) {
                        clickManager.onCreateVideoClick(getContext(), roomKey, title);
                    }
                } else {
                    LogUtils.writeLogFileByDate("createVideoRoom VideoActivity start error");
                }
                if (mCreateVideoCallback != null) {
                    mCreateVideoCallback.createSuccess(title, dec, MEETING_CHOOSE_CURRENT, CreateVideoFragment.this.meetingStartTime, CreateVideoFragment.this.meetingEndTime);
                }
            }

            @Override
            public void onFailure(String err) {
                LogUtils.writeLogFileByDate("createVideoRoom onFailure:" + err);
                UIUtils.showToast(err);
            }

            @Override
            public void onFinished(boolean isSuccess) {
                super.onFinished(isSuccess);
                LogUtils.writeLogFileByDate("createVideoRoom onFinished:" + isSuccess);
                btn_sure.setEnabled(true);
                btn_sure.setText("确定");
                EventBus.getDefault().post(VideoHttpConfig.VIDEO_ROOM_REFRESH);
            }
        });
    }

    /**
     * 判断是否为空
     *
     * @param ids ...
     */
    private boolean isEmpty(GzbVideoUser[] ids) {
        String name = et_meetingName.getText().toString();
        String name1 = name.replace(" ", "");
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(name1)) {
            UIUtils.showToast("没有输入会议主题！");
            return true;
        }
        if (name1.length() > 20) {
            UIUtils.showToast("会议主题超过字数限制");
            return true;
        }
        String str = et_dec.getText().toString();
        String str1 = str.replace(" ", "");
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str1)) {
            str1 = "";
        }
        if (str1.length() > 70) {
            UIUtils.showToast("会议描述超过字数限制");
            return true;
        }
        if (ids.length <= 0) {
            UIUtils.showToast("没有添加联系人!");
            return true;
        }
        return false;
    }

    @Override
    protected void onResumeData() {

    }

    private void initAddBtn() {
        //“添加”按钮
        addPersonView = new PersonView(getActivity());
        addPersonView.setHaveW(false);
        addPersonView.setPersonInfo(getActivity(), null, "添加", null, true);
        addPersonView.setId(R.id.add_person);
        ll_addPerson.addView(addPersonView);
        addPersonView.getLayoutParams();
        addPersonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCreateVideoCallback != null) {
                    mCreateVideoCallback.selectUserClick();
                }
            }
        });
    }

    private void changeMeetingClick() {
        boolean b = MEETING_CHOOSE_CURRENT == MEETING_CHOOSE_CREATE;
        iv_meeting_1.setBackgroundResource(b ? R.drawable.meeting_style_true : R.drawable.meeting_style_false);
        iv_meeting_2.setBackgroundResource(b ? R.drawable.meeting_style_false : R.drawable.meeting_style_true);
        tv_meeting_1.setTextColor(b ? Color.parseColor("#03a9f4") : Color.parseColor("#9a9a9a"));
        tv_meeting_2.setTextColor(b ? Color.parseColor("#9a9a9a") : Color.parseColor("#03a9f4"));
        tv_startTime.setVisibility(b ? View.GONE : View.VISIBLE);
        tv_endTime.setVisibility(b ? View.GONE : View.VISIBLE);
    }

    /**
     * 时间选择弹窗
     */
    private TimePickerDialog showTimeDialog() {
        timeDialog = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        meetingStartTime = millseconds;
                        tv_startTime.setText("开始时间  " + new SimpleDateFormat("yyyy年MM月dd日 HH:mm")
                                .format(new Date(meetingStartTime)));
                        meetingEndTime = millseconds + 1000 * 60 * 60;
                        tv_endTime.setText("结束时间  " + new SimpleDateFormat("yyyy年MM月dd日 HH:mm")
                                .format(new Date(meetingEndTime)));
                    }
                }).setMinMillseconds(System.currentTimeMillis() + 1000 * 30 * 60)
//                        .setCurrentMillseconds(System.currentTimeMillis())
//                .setThemeColor(R.color.red)
                .setTitleStringId("")
                .build();
        timeDialog.show(getActivity().getSupportFragmentManager(), "start");
        return timeDialog;
    }

    private void dismissTimeDialog() {
        if (timeDialog != null) {
            timeDialog.dismiss();
            timeDialog = null;
        }
    }

    /**
     * 刷新人员
     */
    private void refreshPerson() {
        ll_addPerson.removeAllViews();
        initAddBtn();
        Set<String> keys = mUserMap.keySet();
        for (String key : keys) {
            GzbVideoUser info = mUserMap.get(key);
            final PersonView personView = new PersonView(getActivity());
            personView.setPersonInfo(getActivity(), "", info.name, info.id, false);
            ll_addPerson.addView(personView, 0);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) personView.getLayoutParams();
            params.rightMargin = UIUtils.dip2px(10);
            personView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_addPerson.removeView(personView);
                    mUserMap.remove(personView.entityId);
                    LogUtils.e("移除了：" + personView.entityId + "   " + personView.name);
                }
            });
        }
    }
}
