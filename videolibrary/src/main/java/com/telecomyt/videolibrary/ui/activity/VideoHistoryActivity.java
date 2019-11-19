package com.telecomyt.videolibrary.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.base.AdapterBase;
import com.telecomyt.videolibrary.base.BaseActivity;
import com.telecomyt.videolibrary.callback.HttpPostCallback;
import com.telecomyt.videolibrary.gson.GetVideoRoomHelper;
import com.telecomyt.videolibrary.gson.GetVideoRoomRequest;
import com.telecomyt.videolibrary.utils.HttpUtil;
import com.telecomyt.videolibrary.utils.LogUtils;
import com.telecomyt.videolibrary.utils.TimeUtils;
import com.telecomyt.videolibrary.utils.UIUtils;
import com.telecomyt.videolibrary.view.CircleTextView;
import com.telecomyt.videolibrary.view.MyListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lbx.xvideoimagelib.ImageBuilder;
import lbx.xvideoimagelib.VideoImageLoader;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;

public class VideoHistoryActivity extends BaseActivity {


    private ListView lv_record;
    private MyListView lv_more;
    private LinearLayout ll_content;
    private CoordinatorLayout coorLayout;
    private RelativeLayout bottomLayout, topLayout;
    private TextView tv_bottom, mEmptyViewHistory, mEmptyViewRecent, tv_open_title;

    private List<GetVideoRoomRequest.Result.PersonalMeeting> mListRecentMeeting = new ArrayList<>();//近期会议
    private List<GetVideoRoomRequest.Result.PersonalMeeting> mListHistorical = new ArrayList<>();//时间久的会议
    int mWeekTime = 1000 * 60 * 60 * 24 * 7;//一周
    private MyAdapter mAdapter;
    private VideoImageLoader mImageLoader;
    private MyAdapter mAdapterMore;
    private boolean isSetBottomSheetHeight;
    private BottomSheetBehavior mBehavior;
    private boolean isShowBottom;
    private SwipeRefreshLayout srl_history;
    private int mActionBarHeight;
    private int offset;

    public static void start(Context c) {
        Intent intent = new Intent(c, VideoHistoryActivity.class);
        c.startActivity(intent);
    }

    @Override
    public int getLayoutID() {
        Intent intent = getIntent();
        return R.layout.activity_video_history;
    }

    @Override
    public void initView(View view) {
        lv_record = findView(R.id.lv_record);
        tv_open_title = findView(R.id.tv_open_title);
        topLayout = findView(R.id.topLayout);
        tv_bottom = findView(R.id.tv_bottom);
        ll_content = findView(R.id.ll_content);
        lv_more = findView(R.id.lv_more);
        bottomLayout = findView(R.id.bottomLayout);
        coorLayout = findView(R.id.coorLayout);
        srl_history = findView(R.id.srl_history);
        mBehavior = BottomSheetBehavior.from(bottomLayout);

        mEmptyViewRecent = emptyTextView("暂无7天内会议", UIUtils.getDimen(R.dimen.title_height) + UIUtils.dip2px(20));
        mEmptyViewHistory = emptyTextView("暂无更多会议", UIUtils.getDimen(R.dimen.title_height) + UIUtils.dip2px(20));

        lv_record.setEmptyView(mEmptyViewRecent);
        ((ViewGroup) lv_record.getParent()).addView(mEmptyViewRecent);
        lv_record.getEmptyView().setVisibility(View.GONE);

        lv_more.setEmptyView(mEmptyViewHistory);
        ((ViewGroup) lv_more.getParent()).addView(mEmptyViewHistory);
        lv_more.getEmptyView().setVisibility(View.GONE);

        srl_history.setColorSchemeResources(R.color.blue_light, R.color.blue);
        srl_history.setRefreshing(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(UIUtils.getColor(Config.ACTION_BAR_COLOR)));
            actionBar.setTitle("");
            actionBar.setCustomView(R.layout.actionbar_title);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.getCustomView().findViewById(R.id.fl_finish_hostory).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    @Override
    public int addWaterMarkView() {
        return R.id.fl_water_videoHistory;
    }

    @Override
    public void initData() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            mActionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());

            initImageLoader();
            getData();
            mAdapter = new MyAdapter(this, mListRecentMeeting, R.layout.item_meetingrecord, new ViewHolder(), mImageLoader);
            mAdapterMore = new MyAdapter(this, mListHistorical, R.layout.item_meetingrecord, new ViewHolder(), mImageLoader);
            lv_record.setAdapter(mAdapter);
            lv_more.setAdapter(mAdapterMore);
        }
    }

    private void getData() {
        HttpUtil.getInstance().post(new GetVideoRoomHelper(1), new HttpPostCallback<String>() {
            @Override
            public void onSuccess(String json) {
                GetVideoRoomRequest roomRequest = new Gson().fromJson(json, GetVideoRoomRequest.class);
                if (roomRequest.code == 1) {
//                    mListRecentMeeting = mAdapter.setData(roomRequest.result);
                    List<GetVideoRoomRequest.Result.PersonalMeeting> result = roomRequest.result.personalMeeting;
                    mListRecentMeeting.clear();
                    mListHistorical.clear();
                    long now = System.currentTimeMillis();
                    if (result == null) result = new ArrayList<>();
                    for (GetVideoRoomRequest.Result.PersonalMeeting r : result) {
                        long startTime = TimeUtils.formatTime(r.meetingStartTime, "yyyy-MM-dd HH:mm:ss");
                        r.myStartTime = startTime;
                        //一周内的会议
                        if (now - startTime < mWeekTime)
                            mListRecentMeeting.add(r);
                        else
                            mListHistorical.add(r);
                    }
                    LogUtils.e("7天内的会议 = " + mListRecentMeeting.size());
                    LogUtils.e("7天前的会议 = " + mListHistorical.size());
                    Collections.sort(mListRecentMeeting, comp);
                    Collections.sort(mListHistorical, comp);
                    mAdapter.setData(mListRecentMeeting);
                    mAdapterMore.setData(mListHistorical);
                } else {
                    UIUtils.showToast(roomRequest.message);
                }
            }

            @Override
            public void onFailure(String err) {
                UIUtils.showToast("查询历史会议失败：" + err);
            }

            @Override
            public void onFinished(boolean isSuccess) {
                super.onFinished(isSuccess);
                srl_history.setRefreshing(false);
            }
        });
    }

    private void initImageLoader() {
        String path;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AirTalkee/RecordCache";
        else
            path = getFilesDir().getAbsolutePath() + "/AirTalkee/RecordCache";
        int i = Runtime.getRuntime().availableProcessors() - 1;
        ImageBuilder builder = new ImageBuilder()
                .setImgSize(200, 200)
                .setPath(path)
                .setThreadNum(i)
                .setCatchType(ImageBuilder.CatchType.MemoryAndFile)
                .setImgErrorId(R.drawable.easy_icon);
        mImageLoader = new VideoImageLoader(this, builder);
    }

    @Override
    public void initListener() {
        srl_history.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        lv_record.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GetVideoRoomRequest.Result.PersonalMeeting info = null;
                info = mListRecentMeeting.get(i);
                if (info != null)
                    playVideo(info);
            }
        });
        lv_more.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GetVideoRoomRequest.Result.PersonalMeeting info = null;
                info = mListHistorical.get(i);
                if (info != null)
                    playVideo(info);
            }
        });
        mImageLoader.setOnImgDownloadFinish(new VideoImageLoader.OnImgDownloadFinish() {
            @Override
            public void imgDownloadFinish(String url, Bitmap b) {
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
            }

            @Override
            public void imgDownloadErr(String err) {
                LogUtils.e("加载img错误：" + err);
            }
        });
        topLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBehavior.setState(STATE_COLLAPSED);
            }
        });
        bottomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBehavior.setState(STATE_EXPANDED);
            }
        });
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState != STATE_COLLAPSED && tv_bottom.getVisibility() == View.VISIBLE) {
                    tv_bottom.setVisibility(View.GONE);
                    lv_more.setTranslationY(0);
                    lv_more.setVisibility(View.VISIBLE);
                    LogUtils.e("STATE_COLLAPSED 1");
                } else if (newState == STATE_COLLAPSED && tv_bottom.getVisibility() == View.GONE) {
                    tv_bottom.setVisibility(View.VISIBLE);
                    lv_more.setVisibility(View.GONE);
                    LogUtils.e("STATE_COLLAPSED 2");
                    isShowBottom = false;
                } else if (newState == STATE_EXPANDED) {
//                    if (lv_more.getAdapter() == null) {
//                        lv_more.setAdapter(mAdapterMore);
//                        mAdapterMore.notifyDataSetChanged();
//                    }
                    lv_more.setVisibility(View.VISIBLE);
                    isShowBottom = true;
                    LogUtils.e("STATE_EXPANDED");
                    offset = bottomSheet.getTop() - topLayout.getBottom();
                    topLayout.setTranslationY(offset);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (bottomSheet.getTop() < 3 * topLayout.getHeight()) {
                    topLayout.setVisibility(View.VISIBLE);
                    topLayout.setAlpha(slideOffset);
                    lv_more.setAlpha(slideOffset);
                    int offset = Math.min(mActionBarHeight, bottomSheet.getTop());
                    topLayout.setTranslationY(bottomSheet.getTop() - 2 * topLayout.getHeight() + offset);
                } else {
                    topLayout.setVisibility(View.INVISIBLE);
                }
                lv_more.setAlpha(slideOffset);
            }
        });
    }

    private void playVideo(GetVideoRoomRequest.Result.PersonalMeeting info) {
        String replayUrl = info.replayUrl;
        String[] urls;
        if (TextUtils.isEmpty(replayUrl)) {
            urls = new String[0];
        } else {
            if (replayUrl.contains(","))
                urls = replayUrl.split(",");
            else
                urls = new String[]{replayUrl};
        }
//        startActivity(VideoActivity.getMyIntent(this, info.chatName, info.meetingMessage,
//                info.meetingStartTime, info.meetingAuthor, urls, getUser(info.join_users)));
    }

    private TextView emptyTextView(String s, int topMargin) {
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2px(30));
        TextView textView = new TextView(this);
        textView.setText(s);
        textView.setTextColor(UIUtils.getColor(android.R.color.black));
        textView.setTextSize(14);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(params);
        if (topMargin != 0 && topMargin != -1)
            textView.setTranslationY(topMargin);
        return textView;
    }

    private final Comparator comp = new SortComparator();

    private class SortComparator implements Comparator<GetVideoRoomRequest.Result.PersonalMeeting> {
        @Override
        public int compare(GetVideoRoomRequest.Result.PersonalMeeting lhs, GetVideoRoomRequest.Result.PersonalMeeting rhs) {
            return (int) (rhs.myStartTime - lhs.myStartTime);
        }
    }

    /**
     * 获取参会人员
     *
     * @param join_users
     */

    private String getUser(String[] join_users) {
        String users = "";
        if (join_users.length == 0)
            return "";
        int maxUsers = Math.min(join_users.length, 10);
        int i;
        for (i = 0; i < maxUsers; i++) {
            users += join_users[i] + ",";
        }
        while (!TextUtils.isEmpty(users) && users.endsWith(",")) {
            users = users.substring(0, users.length() - 1);
        }
        if (i < join_users.length)
            users += " 等" + join_users.length + "人";
        return users;
    }

    class MyAdapter extends AdapterBase<GetVideoRoomRequest.Result.PersonalMeeting, ViewHolder> {

        private VideoImageLoader imageLoader;

        public MyAdapter(Context context, List<GetVideoRoomRequest.Result.PersonalMeeting> list,
                         int layoutId, ViewHolder holder, VideoImageLoader imageLoader) {
            super(context, list, layoutId, holder);
            this.imageLoader = imageLoader;
        }

        @Override
        public void bindView(View convertView, ViewHolder holder) {
            holder.ctv_pic = (CircleTextView) convertView.findViewById(R.id.ctv_pic);
            holder.iv_play = (ImageView) convertView.findViewById(R.id.iv_play);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_creator = (TextView) convertView.findViewById(R.id.tv_creator);
            holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
            holder.tv_recordTime = (TextView) convertView.findViewById(R.id.tv_recordTime);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
        }

        @Override
        public void setData(ViewHolder holder, GetVideoRoomRequest.Result.PersonalMeeting data,
                            List<GetVideoRoomRequest.Result.PersonalMeeting> list, int pos) {
//            imageLoader.displayUrl(holder.iv_pic, data.creatorImg);
//            BitmapUtil.getInstance(VideoHistoryActivity.this).display(holder.iv_pic, data.creatorImg);
            String author = data.meetingAuthor;
            holder.ctv_pic.setTextWithColorAndSize(author.length() <= 2 ? author :
                    author.substring(author.length() - 2, author.length()), R.color.white_main, 18);
//            holder.iv_play.setVisibility(noVideo ? View.GONE : View.VISIBLE);
            holder.tv_title.setText(data.meetingName);
            holder.tv_creator.setText(author);

            holder.tv_num.setText(String.valueOf(getMemberNum(data.meetingUidInfos)));
            String duration = data.duration;
            boolean noVideo = TextUtils.isEmpty(duration);
            holder.tv_recordTime.setText(noVideo ? "00:00" : duration);
            holder.tv_time.setText(data.meetingStartTime);
        }
    }

    private int getMemberNum(List<GetVideoRoomRequest.Result.PersonalMeeting.User> join_users) {
        return join_users.size();
    }

    static class ViewHolder extends AdapterBase.BaseHolder {
        private TextView tv_title, tv_creator, tv_num, tv_recordTime, tv_time;
        private CircleTextView ctv_pic;
        private ImageView iv_play;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!isSetBottomSheetHeight) {
            CoordinatorLayout.LayoutParams linearParams = (CoordinatorLayout.LayoutParams) bottomLayout.getLayoutParams();
//            linearParams.height = coorLayout.getHeight() - topLayout.getHeight();
            int measuredHeight = topLayout.getMeasuredHeight();
            linearParams.height = UIUtils.getScreenHeight() - UIUtils.getDimen(R.dimen.title_height) - measuredHeight
                    - UIUtils.dip2px(50);
            bottomLayout.setLayoutParams(linearParams);
            isSetBottomSheetHeight = true;
        }
    }

    @Override
    public void onBackPressed() {
        if (isShowBottom) {
            mBehavior.setState(STATE_COLLAPSED);
            isShowBottom = false;
        } else {
            super.onBackPressed();
        }
    }
}
