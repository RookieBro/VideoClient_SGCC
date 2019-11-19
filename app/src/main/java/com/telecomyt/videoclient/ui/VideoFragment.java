package com.telecomyt.videoclient.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.telecomyt.videoclient.R;
import com.telecomyt.videolibrary.VideoClient;
import com.telecomyt.videolibrary.base.BaseFragment;
import com.telecomyt.videolibrary.bean.GzbVideoDirect;
import com.telecomyt.videolibrary.gson.GzbVideoRoom;

/**
 * Created by lbx on 2017/9/25.
 * 点呼与创建会议室逻辑
 */

public class VideoFragment extends BaseFragment implements View.OnClickListener {

    private Button btn_direct, btn_room, btn_control, btn_change_waterMark;

    public static VideoFragment newInstance() {
        Bundle args = new Bundle();
        VideoFragment fragment = new VideoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_video;
    }

    @Override
    public void initView(View view) {
        btn_room = findView(R.id.btn_room);
        btn_direct = findView(R.id.btn_direct);
        btn_control = findView(R.id.btn_control);
        btn_change_waterMark = findView(R.id.btn_change_waterMark);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        btn_room.setOnClickListener(this);
        btn_direct.setOnClickListener(this);
        btn_control.setOnClickListener(this);
        btn_change_waterMark.setOnClickListener(this);
    }

    @Override
    protected void onResumeData() {
        VideoClient.getInstance().refreshWaterMark();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_room:
                String ids = "u175225,u175228";
                //通讯录获取uids的流程，因本demo没有集成通讯录sdk，固暂时注释掉
//                List<ChatRoomMember> members = GzbIMClient.getInstance().chatRoomModule().getChatRoomMemberList(chatWithId.getId());
//                for (int i = 0; i < members.size(); i++) {
//                    ChatRoomMember member = members.get(i);
//                    String id = member.getMemberJid().getId();
//                    ids += id + ",";
//                }
//                ids = ids.endsWith(",") ? ids.substring(0, ids.length() - 1) : ids;
                /**
                 * 第三步 进入/创建并进入会议
                 * @param gzbChatRoomId   IM聊天室的roomId
                 * @param callUserGzbName  IM聊天室的名称
                 * @param joinUids  IM聊天室所有人员的id数组
                 */
//                GzbVideoRoom info = new GzbVideoRoom("99999999999", "测试1", ids.split(","));
//                VideoClient.getInstance().joinVideoRoom(getActivity(), info);
                break;
            case R.id.btn_direct:
                /**
                 * 第三步 呼叫通讯录id为u175228的人员（点对点呼叫）
                 * @param callUserGzbName   被呼叫者的昵称
                 * @param callUserGzbID   被呼叫者的通讯录id
                 */
                GzbVideoDirect info1 = new GzbVideoDirect("哈哈哈", "u221501");//u221501  //u221509
                VideoClient.getInstance().directCall(getActivity(), info1);
                break;
            case R.id.btn_control:
                GzbVideoRoom room = new GzbVideoRoom("","",null);
                room.setRoomKey("gCD8xGuMKe");
                VideoClient.getInstance().joinVideoRoom(getActivity(),room,null);
                break;
            //切换水印数字
            case R.id.btn_change_waterMark:
                VideoClient.getInstance().refreshWaterMark("" + Math.random() * 10000);
                break;
            default:
                break;

        }
    }
}
