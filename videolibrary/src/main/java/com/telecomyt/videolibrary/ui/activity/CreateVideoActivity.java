package com.telecomyt.videolibrary.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.VideoClient;
import com.telecomyt.videolibrary.base.BaseActivity;
import com.telecomyt.videolibrary.callback.CreateRoomCallBack;
import com.telecomyt.videolibrary.gson.GzbVideoRoom;
import com.telecomyt.videolibrary.gson.JoinGzbVideoRoomRequest;
import com.telecomyt.videolibrary.utils.UIUtils;

public class CreateVideoActivity extends BaseActivity {

    private View ll_dialog;
    private TextView tv_dialog_title;
    private TextView tv_dialog_dec;
    private Button btn_dialog_cancel;
    private Button btn_dialog_sure;
    private ProgressBar pb_create;
    private String mGzbId;
    private String[] mIds;
    private CreateRoomCallBack mCallBack;
    private EditText et_videoName;

    public static void start(Context context, String gzbRoomId, String[] mIds) {
        Intent intent = new Intent(context, CreateVideoActivity.class);
        intent.putExtra("gzbRoomId", gzbRoomId);
        intent.putExtra("mIds", mIds);
//        intent.putExtra("callBack", callBack);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutID() {
        Intent intent = getIntent();
        if (intent != null) {
            mGzbId = intent.getStringExtra("gzbRoomId");
            mIds = intent.getStringArrayExtra("mIds");
//            mCallBack = (CreateRoomCallBack) intent.getSerializableExtra("callBack");
        }
        return R.layout.activity_create_video;
    }

    @Override
    public void initView(View view) {
        ll_dialog = findView(R.id.ll_dialog);

        tv_dialog_title = findView(R.id.tv_dialog_title);
        tv_dialog_dec = findView(R.id.tv_dialog_dec);
        btn_dialog_cancel = findView(R.id.btn_dialog_cancel);
        et_videoName = findView(R.id.et_videoName);
        pb_create = findView(R.id.pb_create);

        et_videoName.setVisibility(View.VISIBLE);
        tv_dialog_dec.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        tv_dialog_title.setText(UIUtils.getString(R.string.create_videoRoom));
    }

    @Override
    public void initListener() {
        super.initListener();
        btn_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_dialog_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String trim = et_videoName.getText().toString().trim();
                if (UIUtils.textIsEmpty(trim, UIUtils.getString(R.string.create_videoName))) return;
                pb_create.setVisibility(View.VISIBLE);
                ll_dialog.setVisibility(View.GONE);
                /**
                 * 第三步 进入/创建并进入会议
                 * @param gzbChatRoomId   IM聊天室的roomId
                 * @param callUserGzbName  IM聊天室的名称
                 * @param joinUids  IM聊天室所有人员的id数组
                 */
                GzbVideoRoom info = new GzbVideoRoom(mGzbId, trim, mIds);
                VideoClient.getInstance().joinVideoRoom(getBaseContext(), info, new CreateRoomCallBack() {
                    @Override
                    public void success(JoinGzbVideoRoomRequest joinGzbVideoRoomRequest) {
                        //成功
                        if (mCallBack != null)
                            mCallBack.success(joinGzbVideoRoomRequest);
                        finish();
                        //发送msg到聊天页面  其他人可以点击消息进入视频会议  逻辑：ATypeContentMsgViewHolder里处理
//                        GzbUtil.sendVideoRoomMessage(VideoState.VIDEO_ROOM_MSG, GsonUtils.getInstance().toJson(joinGzbVideoRoomRequest),
//                                chatWithId, GzbConversationType.CHATROOM);
                    }

                    @Override
                    public void err(String s) {
                        //失败
                        if (mCallBack != null)
                            mCallBack.err(s);
                        finish();
                    }
                });
            }
        });
    }
}
