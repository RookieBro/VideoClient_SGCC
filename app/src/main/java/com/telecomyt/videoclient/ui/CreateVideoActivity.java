package com.telecomyt.videoclient.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.telecomyt.videoclient.R;
import com.telecomyt.videolibrary.VideoClient;
import com.telecomyt.videolibrary.bean.GzbVideoUser;
import com.telecomyt.videolibrary.callback.CreateVideoCallback;
import com.telecomyt.videolibrary.ui.fragment.CreateVideoFragment;

public class CreateVideoActivity extends AppCompatActivity  implements CreateVideoCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_video);

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_create, CreateVideoFragment.newInstance()).commit();
    }

    @Override
    public void selectUserClick() {

    }

    @Override
    public GzbVideoUser[] addVideoRoomUser(String title, String dec, int videoRoomType, long startTime, long endTime) {
        return new GzbVideoUser[0];
    }

    @Override
    public void createSuccess(String title, String dec, int videoRoomType, long startTime, long endTime) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoClient.getInstance().refreshWaterMark();
    }
}
