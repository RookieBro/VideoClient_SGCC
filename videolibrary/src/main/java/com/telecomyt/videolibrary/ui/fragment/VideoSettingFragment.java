package com.telecomyt.videolibrary.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.base.BaseFragment;
import com.telecomyt.videolibrary.bean.GzbVideoUser;
import com.telecomyt.videolibrary.callback.HttpPostCallback;
import com.telecomyt.videolibrary.callback.VideoSettingCallback;
import com.telecomyt.videolibrary.gson.GetVideoRoomRequest;
import com.telecomyt.videolibrary.gson.UpdateVideoRoomHelper;
import com.telecomyt.videolibrary.utils.HttpUtil;
import com.telecomyt.videolibrary.utils.LogUtils;
import com.telecomyt.videolibrary.utils.SoftInputUtil;
import com.telecomyt.videolibrary.utils.UIUtils;
import com.telecomyt.videolibrary.view.PersonView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lbx on 2017/9/25.
 * 创建会议
 */

public class VideoSettingFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout ll_changeName;
    private EditText et_name;
    private Button btn_outMeeting;
    private RelativeLayout rl_progress;
    private LinearLayout ll_addPerson;
    private PersonView addPersonView;
    private PersonView minPersonView;
    private RelativeLayout rl_setting;

    private String chatName = null;
    private boolean isEditName;
    private GetVideoRoomRequest.Result.PersonalMeeting mRoom;
    private Map<String, GzbVideoUser> mUserMap;
    private List<GetVideoRoomRequest.Result.PersonalMeeting.User> meetingUser = new ArrayList<>();

    private static final int ADD_PERSON = 0;
    private static final int MIN_PERSON = 1;
    private static final int CHANGE_NAME = 2;

    private static VideoSettingCallback mVideoSettingCallback;

    @Override
    public int getLayoutID() {
        //当前会议数据
        mRoom = Config.currentVideoRoom;
        LogUtils.e("mRoom = " + mRoom.toString());
        return R.layout.fragment_video_setting;
    }


    @Override
    public void initView(View view) {
        btn_outMeeting = findView(R.id.btn_finishMeeting);
        rl_progress = findView(R.id.rl_progress);
        et_name = findView(R.id.et_name);
        ll_changeName = findView(R.id.ll_change_name);
        ll_addPerson = findView(R.id.ll_addPerson);
        rl_setting = findView(R.id.rl_roomSetting);
    }

    @Override
    public int addWaterMarkView() {
        return R.id.fl_watermark_video_setting;
    }

    public static void setVideoSettingCallBack(VideoSettingCallback callback) {
        mVideoSettingCallback = callback;
    }

    @Override
    public void initData() {
        mUserMap = new HashMap<>();

        if (mRoom != null && !TextUtils.isEmpty(mRoom.meetingName)) {
            et_name.setText(mRoom.meetingName);
        }

        if (mRoom != null && mRoom.meetingUidInfos != null && mRoom.meetingUidInfos.size() > 0) {
            meetingUser = mRoom.meetingUidInfos;
            if (!(mRoom.meetingAuthorCardId).equals(Config.UserInfo.getVideoUserName())) {
                if (meetingUser != null) {
                    for (GetVideoRoomRequest.Result.PersonalMeeting.User user : meetingUser) {
                        PersonView personView = new PersonView(getActivity());
                        if (user != null && !TextUtils.isEmpty(user.userNickName) && !TextUtils.isEmpty(user.userId)) {
                            personView.setPersonInfo(getActivity(), "", user.userNickName, user.userId, false);
                            if ((mRoom.meetingAuthorGzbId).equals(user.userId)) {
                                ll_addPerson.addView(personView, 0);
                            } else {
                                ll_addPerson.addView(personView);
                            }
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) personView.getLayoutParams();
                            params.rightMargin = UIUtils.dip2px(10);
                        }
                    }
                }
            } else {
                setUsers(meetingUser);
                refresh();
            }

        }
    }

    public void setUsers(List<GetVideoRoomRequest.Result.PersonalMeeting.User> users) {
        mUserMap.clear();
        if (users != null) {
            for (GetVideoRoomRequest.Result.PersonalMeeting.User u : users) {
                GzbVideoUser user = new GzbVideoUser(u.userNickName, u.userId);
                mUserMap.put(user.id, user);
                LogUtils.e("add：" + u.toString());
            }
        }
    }

    public void addUsers(List<GzbVideoUser> users) {
        for (GzbVideoUser u : users) {
            mUserMap.put(u.id, u);
            LogUtils.e("add：" + u.toString());
        }
    }

    /**
     * 刷新人员列表
     */
    public void refresh() {
        LogUtils.e("refresh user list");
        ll_addPerson.removeAllViews();
        Set<String> keys = mUserMap.keySet();
        for (final String key : keys) {
            final GzbVideoUser info = mUserMap.get(key);
            final PersonView personView = new PersonView(getActivity());
            if (info != null && !TextUtils.isEmpty(info.name) && !TextUtils.isEmpty(info.id)) {
                personView.setPersonInfo(getActivity(), "", info.name, info.id, false);
                if ((mRoom.meetingAuthorGzbId).equals(info.id)) {
                    ll_addPerson.addView(personView, 0);
                } else {
                    ll_addPerson.addView(personView);
                }
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) personView.getLayoutParams();
                params.rightMargin = UIUtils.dip2px(10);
                personView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((mRoom.meetingAuthorGzbId).equals(info.id)) {
                            //creator
                            UIUtils.showToast(getString(R.string.creator_yourself));
                            return;
                        }
                        String[] ids = new String[1];
                        ids[0] = key;
                        updateRoom("", MIN_PERSON, mRoom.meetingID, ids, personView);
//                    }
                    }
                });
            }
        }
        initAddBtn();
    }

    @Override
    public void initListener() {
        rl_progress.setOnClickListener(this);
        et_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(mRoom.meetingAuthorCardId).equals(Config.UserInfo.getVideoUserName())) {
                    return;
                }
                changeName();
            }
        });

        rl_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEditName) {
                    chatName = et_name.getText().toString().trim();
                    if (TextUtils.isEmpty(chatName)) {
                        UIUtils.showToast(getString(R.string.name_can_not_empty));
                        return;
                    }
                    if (chatName.equals(mRoom.meetingName)) {
                        SoftInputUtil.hintSoftInput(getActivity());
                        et_name.setFocusable(false);
                        et_name.setFocusableInTouchMode(false);
                        isEditName = false;
                        return;
                    }
                    SoftInputUtil.hintSoftInput(getActivity());
                    updateRoom(chatName, CHANGE_NAME, mRoom.meetingID, null, null);
                }
            }
        });
    }

    @Override
    protected void onResumeData() {

    }

    private void initAddBtn() {
        //“添加”按钮
        addPersonView = new PersonView(getActivity());
        addPersonView.setHaveW(false);
        addPersonView.setPersonInfo(getActivity(), null, getString(R.string.add_user), null, true);
        addPersonView.setId(R.id.add_person);
        ll_addPerson.addView(addPersonView);
        addPersonView.getLayoutParams();

//        minPersonView = new PersonView(getActivity());
//        minPersonView.setHaveW(false);
//        minPersonView.setPersonInfo(getActivity(), null, "减少", null, true);
//        minPersonView.setId(R.id.min_person);
//        ll_addPerson.addView(minPersonView);
//        minPersonView.getLayoutParams();

        addPersonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mVideoSettingCallback != null) {
                    mVideoSettingCallback.selectAddUserClick();
                }
            }
        });
    }

    private void changeName() {
        isEditName = true;
        et_name.setFocusableInTouchMode(true);
        et_name.setFocusable(true);
        SoftInputUtil.showSoftInput(et_name);
    }

    public void updateUser(List<GzbVideoUser> users) {
        //new data
        for (int i = 0; i < meetingUser.size(); i++) {
            if (mUserMap.containsKey(meetingUser.get(i))) {
                //inclue
                mUserMap.remove(meetingUser.get(i));
            }
        }
        Set<String> keys = mUserMap.keySet();

        List<String> gzbIds = new ArrayList<>();
        for (String key : keys) {
            GzbVideoUser user = mUserMap.get(key);
            gzbIds.add(user.id);
        }
        String[] ids = new String[gzbIds.size()];

        for (int i = 0; i < ids.length; i++) {
            ids[i] = gzbIds.get(i);
        }
        updateRoom("", ADD_PERSON, mRoom.meetingID, ids, null);
    }

    private void updateRoom(String name, final int operationType, String meetingID, String[] gzbIds, final PersonView personView) {
        rl_progress.setVisibility(View.VISIBLE);
        if (gzbIds == null) {
            gzbIds = new String[]{};
        }
        UpdateVideoRoomHelper updateVideoRoomHelper;
        if (operationType == CHANGE_NAME) {
            updateVideoRoomHelper = new UpdateVideoRoomHelper(name, CHANGE_NAME, meetingID, gzbIds);
        } else {
            updateVideoRoomHelper = new UpdateVideoRoomHelper("", operationType, meetingID, gzbIds);
        }
        HttpUtil.getInstance().post(updateVideoRoomHelper, new HttpPostCallback<String>() {
            @Override
            public void onSuccess(String json) {
                try {
                    JSONObject object = new JSONObject(json);
                    String message = object.getString("message");
                    int code = object.getInt("code");
                    if (operationType == ADD_PERSON && code == 1) {
                        //update data
                        List<GetVideoRoomRequest.Result.PersonalMeeting.User> users = new ArrayList<>();
                        Set<String> keys = mUserMap.keySet();
                        for (final String key : keys) {
                            final GzbVideoUser info = mUserMap.get(key);
                            GetVideoRoomRequest.Result.PersonalMeeting.User user = new GetVideoRoomRequest.Result.PersonalMeeting.User("", info.name, info.id);
                            users.add(user);
                        }
                        Config.currentVideoRoom.meetingUidInfos = users;
                        refresh();
                    } else if (operationType == CHANGE_NAME && code == 1) {
                        et_name.setFocusableInTouchMode(false);
                        et_name.setFocusable(false);
                        isEditName = false;
                    } else if (operationType == MIN_PERSON && code == 1) {
                        ll_addPerson.removeView(personView);
//                    int index = getIndex(personView.entityId);
//                    if (index != -1) {
                        mUserMap.remove(personView.entityId);
                        List<GetVideoRoomRequest.Result.PersonalMeeting.User> cache = Config.currentVideoRoom.meetingUidInfos;
                        if (cache == null) {
                            cache = new ArrayList<>();
                        }
                        int i;
                        for (i = 0; i < cache.size(); i++) {
                            GetVideoRoomRequest.Result.PersonalMeeting.User user = cache.get(i);
                            if (!TextUtils.isEmpty(user.userId) && user.userId.equals(personView.entityId)) {
                                break;
                            }
                        }
                        Config.currentVideoRoom.meetingUidInfos.remove(i);
                        LogUtils.e("remove：" + personView.entityId + "   " + personView.name + "  " + mUserMap.size());
                    }
                    UIUtils.showToast(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                rl_progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String err) {
                UIUtils.showToast(getString(R.string.network_error));
                rl_progress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
