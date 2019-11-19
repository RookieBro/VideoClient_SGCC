package com.telecomyt.videoclient.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.telecomyt.videoclient.R;
import com.telecomyt.videolibrary.VideoClient;
import com.telecomyt.videolibrary.base.BaseActivity;
import com.telecomyt.videolibrary.bean.GzbVideoUser;
import com.telecomyt.videolibrary.callback.CreateVideoCallback;
import com.telecomyt.videolibrary.callback.VideoListCallback;
import com.telecomyt.videolibrary.ui.fragment.CreateVideoFragment;
import com.telecomyt.videolibrary.ui.fragment.VideoListFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements
        CreateVideoCallback,//创建会议页面的回调  CreateVideoFragment的activity必须实现
        VideoListCallback {

    private ViewPager vp_main;
    private List<Fragment> mList;

    public static Intent newInstance(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }


    @Override
    public int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(View view) {
        vp_main = findView(R.id.vp_main);
    }

    @Override
    public void initData() {
        mList = new ArrayList<>();

        mList.add(VideoFragment.newInstance());
        mList.add(CreateVideoFragment.newInstance());
        mList.add(VideoListFragment.newInstance());
        mList.add(TempVideoFragment.newInstance());

        vp_main.setAdapter(new MyAdapter(getSupportFragmentManager()));
        vp_main.setOffscreenPageLimit(100);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void onClick(View view, int id) {

    }

    //---------------------------------------  CreateVideoFragment回调  start  ---------------------------------------

    /**
     * 创建会议页面的选人点击回调（加号“添加”按钮）
     */
    @Override
    public void selectUserClick() {
//        GzbSdkUtils.startPickMemberActivityForResult(DemoMainActivity.this,
//                "选人演示",
//                REQUEST_CODET_PICK_MEMBER);
    }

    /**
     * 创建会议页面确定按钮点击回调,return的数组请参照通讯录sdk的逻辑，获取到具体人员id
     *
     * @param title         会议主题
     * @param dec           会议描述
     * @param videoRoomType 会议类型 0立即召开  1预约会议
     * @param startTime     会议开始时间
     * @param endTime       会议结束时间
     * @return 通讯录选人后的人员人员
     */
    @Override
    public GzbVideoUser[] addVideoRoomUser(String title, String dec, int videoRoomType, long startTime, long endTime) {
        return new GzbVideoUser[]{new GzbVideoUser("132", "132"), new GzbVideoUser("465", "465")};
    }

    /**
     * 创建会议成功的回调
     *
     * @param title         会议主题
     * @param dec           会议描述
     * @param videoRoomType 会议类型 0立即召开  1预约会议
     * @param startTime     会议开始时间
     * @param endTime       会议结束时间
     */
    @Override
    public void createSuccess(String title, String dec, int videoRoomType, long startTime,
                              long endTime) {

    }

    @Override
    public void createVideoRoomClick() {
        Toast.makeText(this, "创建会议", Toast.LENGTH_SHORT).show();
    }


    //----------------------------------------  CreateVideoFragment回调  end  ----------------------------------------

    private class MyAdapter extends FragmentPagerAdapter {

        private MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList.size();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoClient.getInstance().SignOff();
    }
}
