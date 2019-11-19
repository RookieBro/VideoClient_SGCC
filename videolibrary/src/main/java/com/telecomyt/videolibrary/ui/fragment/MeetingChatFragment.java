package com.telecomyt.videolibrary.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.VideoClient;
import com.telecomyt.videolibrary.base.BaseFragment;
import com.telecomyt.videolibrary.bean.VidyoMessage;
import com.telecomyt.videolibrary.utils.SoftInputUtil;
import com.telecomyt.videolibrary.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频会议聊天界面
 */
public class MeetingChatFragment extends BaseFragment implements View.OnClickListener, View.OnTouchListener, TextView.OnEditorActionListener {

    private ListView iv_v_chat;
    private List<VidyoMessage> list = new ArrayList<>();
    private Button btn_send;
    private EditText et_message;
    private MyAdapter mAdapter;

    @Override
    public int getLayoutID() {
        EventBus.getDefault().register(this);
        return R.layout.fragment_meeting_chat;
    }

    @Override
    public void initView(View view) {
        iv_v_chat = findView(R.id.iv_v_chat);
        btn_send = findView(R.id.btn_send);
        et_message = findView(R.id.et_message);
    }

    @Override
    public void initData() {
        mAdapter = new MyAdapter();
        iv_v_chat.setAdapter(mAdapter);
    }

    @Override
    public void initListener() {
        btn_send.setOnClickListener(this);
        et_message.setOnClickListener(this);
//        iv_v_chat.setOnTouchListener(this);
        et_message.setOnEditorActionListener(this);
    }

    @Override
    protected void onResumeData() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        SoftInputUtil.hintSoftInput(getActivity());
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_send) {
            sendTextMessage();
        }
    }

    private void sendTextMessage() {
        String msg = et_message.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) {
            UIUtils.showToast("请输入消息");
            return;
        } else {
            VideoClient.getInstance().SendMsgG(msg);
            list.add(new VidyoMessage("我", msg, System.currentTimeMillis(), 1));
            et_message.setText("");
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
                iv_v_chat.setSelection(Integer.MAX_VALUE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMsgG(VidyoMessage vidyoMessage) {
        list.add(vidyoMessage);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            iv_v_chat.setSelection(Integer.MAX_VALUE);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int i = v.getId();
        if (i == R.id.iv_v_chat) {
            if (event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_CANCEL)
                SoftInputUtil.hintSoftInput(getActivity());
            return true;
        } else {

        }
        return false;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendTextMessage();
            return true;
        }
        return false;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public VidyoMessage getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                textView = getTextView();
                convertView = textView;
            } else {
                textView = (TextView) convertView;
            }
            VidyoMessage message = list.get(position);
            boolean isMine = Config.UserInfo.getNickName().equals(message.name);
            textView.setTextColor(isMine ? Color.parseColor("#619EFF") : Color.parseColor("#CF46BD"));
            textView.setText(message.name + ":" + message.msg);
            return convertView;
        }
    }

    private TextView getTextView() {
        TextView textView = new TextView(getActivity());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        return textView;
    }
}
