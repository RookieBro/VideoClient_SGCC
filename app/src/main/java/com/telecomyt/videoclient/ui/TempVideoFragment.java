package com.telecomyt.videoclient.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.telecomyt.videoclient.R;
import com.telecomyt.videolibrary.VideoClient;
import com.telecomyt.videolibrary.callback.TempVideoCallback;
import com.telecomyt.videolibrary.gson.CreateTempVideoHelper;
import com.telecomyt.videolibrary.utils.UIUtils;


/**
 * 临时会议   一般用于第三方开会用
 */
public class TempVideoFragment extends Fragment {

    public static TempVideoFragment newInstance() {
        TempVideoFragment fragment = new TempVideoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private Button btn_temp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_temp_video, container, false);
        btn_temp = (Button) inflate.findViewById(R.id.btn_temp);

        btn_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UIUtils.showProgressDialog(getActivity());//显示一个人性化的进度条

                /**
                 * @param meetingString  第三方app传过来的唯一标识 同一个标识必定进入的是同一个会议室
                 */
                CreateTempVideoHelper helper = new CreateTempVideoHelper("threeAppMeetingId");
                VideoClient.getInstance().startTempVideo(getActivity(), helper, new TempVideoCallback() {

                    @Override
                    public void success() {
                        //创建成功
                        UIUtils.closeProgressDialog();
                    }

                    @Override
                    public void err() {
                        //创建失败
                        UIUtils.closeProgressDialog();//关闭等待的进度条
                    }
                });
            }
        });
        return inflate;
    }

}
