package com.telecomyt.videolibrary.ui.activity;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.Domain;
import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.VideoClient;
import com.telecomyt.videolibrary.VideoState;
import com.telecomyt.videolibrary.base.BaseActivity;
import com.telecomyt.videolibrary.bean.ChangeCamera;
import com.telecomyt.videolibrary.bean.VideoParticipantBean;
import com.telecomyt.videolibrary.callback.HttpPostCallback;
import com.telecomyt.videolibrary.event.VideoEvent;
import com.telecomyt.videolibrary.gson.CreateTempVideoRequest;
import com.telecomyt.videolibrary.gson.DirectCallTimeOutHelper;
import com.telecomyt.videolibrary.gson.GetVideoRoomRequest;
import com.telecomyt.videolibrary.gson.GzbVideoRoom;
import com.telecomyt.videolibrary.http.VideoHttpConfig;
import com.telecomyt.videolibrary.manager.ManagerAudio;
import com.telecomyt.videolibrary.utils.HttpUtil;
import com.telecomyt.videolibrary.utils.LogUtils;
import com.telecomyt.videolibrary.utils.SoftInputUtil;
import com.telecomyt.videolibrary.utils.UIUtils;
import com.telecomyt.videolibrary.view.WaitingTextView;
import com.vidyo.LmiDeviceManager.LmiDeviceManagerView;
import com.vidyo.LmiDeviceManager.LmiVideoCapturer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.view.View.inflate;
import static com.vidyo.LmiDeviceManager.LmiVideoCapturerInternal.ORIENTATION_DOWN;
import static com.vidyo.LmiDeviceManager.LmiVideoCapturerInternal.ORIENTATION_LEFT;
import static com.vidyo.LmiDeviceManager.LmiVideoCapturerInternal.ORIENTATION_RIGHT;
import static com.vidyo.LmiDeviceManager.LmiVideoCapturerInternal.ORIENTATION_UP;

/**
 * @author lbx
 */
public class VideoActivity extends BaseActivity implements LmiDeviceManagerView.Callback, SensorEventListener, View.OnTouchListener {

    public FrameLayout mVideoLayout;
    private ImageView mMicView, mListenerView, mRecordView, mCameraView, mMoreView, mNextView;
    private ViewGroup mMenuLayout;

    private int mScreenOrientation;
    /**
     * 会议控件
     */
    private LmiDeviceManagerView mBcView;
    /**
     * 是否允许渲染
     */
    private boolean doRender;
    /**
     * 会议室信息  工作宝的会议  不可加减人员
     */
    private GzbVideoRoom mRoomInfo;
    /**
     * 普通的会议  可以加减人员
     */
    private GetVideoRoomRequest.Result.PersonalMeeting mRoom;
    /**
     * 临时会议 用于第三方
     */
    private CreateTempVideoRequest mTempRoom;
    /**
     * 屏幕方向
     */
    private int currentOrientation;
    /**
     * 方向传感器（地磁+加速度）
     */
    private SensorManager mSensorManager;
    /**
     * 传感器相关
     */
    private float[] mGData = new float[3];
    private float[] mMData = new float[3];
    private float[] mR = new float[16];
    private float[] mI = new float[16];
    private float[] mOrientation = new float[3];
    final float degreePerRadian = (float) (180.0f / Math.PI);
    /**
     * 麦克风是否可用
     */
    private boolean micCanUse = true;
    private boolean listenerCanUse = true;
    /**
     * 页面是否可以强制结束
     */
    private boolean canFinish = true;
    /**
     * 是否可以录像（只有创建者可以录制 可用此字段判断是否为其创建者,临时会议除外）
     */
    private boolean canRecord;
    /**
     * 前后摄像头前换
     */
    public static int usedCamera = 1;
    /**
     * 视频会议是否开始
     */
    public static boolean isStart = false;
    public static boolean activityStart;
    private boolean canChangeCamera = true;
    /**
     * 当前是否预览自己画面
     */
    private boolean previewModeON = true;
    /**
     * 当前是mute了相机
     */
    private boolean muteCamera = false;
    private Toast toast;

    private PowerManager mPowerManager;
    private PowerManager.WakeLock wakeLock;
    private String mRoomkey;
    private String mSubject;
    /**
     * 描述
     */
    private String mMeetingMessage = "";
    /**
     * 是否暂停
     */
    private boolean mIsOnPause;
    /**
     * 点呼的超时时间
     */
    private static final int DIRECT_CALL_TIME_OUT = 30;

    private WaitingTextView mWaitingView;
    private Handler handler = new Handler();

    /**
     * 直接传roomKey的会议  只有创建的会议才会
     */
    public static void start(Context c, String roomkey, String subject, boolean showMoreBtn) {
        Intent intent = new Intent(c, VideoActivity.class);
        intent.putExtra("roomkey", roomkey);
        intent.putExtra("subject", subject);
        intent.putExtra("showMoreBtn", showMoreBtn);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!activityStart) {
            c.startActivity(intent);
        }
    }

    /**
     * IM会议
     */
    public static void start(Context c, GzbVideoRoom roomInfo) {
        Intent intent = new Intent(c, VideoActivity.class);
        intent.putExtra("roomInfo", roomInfo);
        if (!activityStart) {
            c.startActivity(intent);
        }
    }

    /**
     * IM会议 with flags
     */
    public static void start(Context c, GzbVideoRoom roomInfo, int... intentFlag) {
        Intent intent = new Intent(c, VideoActivity.class);
        intent.putExtra("roomInfo", roomInfo);
        for (int i : intentFlag) {
            if (i != -1) {
                intent.addFlags(i);
            }
        }
        if (!activityStart) {
            c.startActivity(intent);
        }
    }

    /**
     * 普通会议
     */
    public static void start(Context c, GetVideoRoomRequest.Result.PersonalMeeting room) {
        Intent intent = new Intent(c, VideoActivity.class);
        intent.putExtra("room", room);
        if (!activityStart) {
            c.startActivity(intent);
        }
    }

    /**
     * 临时会议 用于第三方
     */
    public static void start(Context c, CreateTempVideoRequest tempRoom) {
        Intent intent = new Intent(c, VideoActivity.class);
        intent.putExtra("tempRoom", tempRoom);
        if (!activityStart) {
            c.startActivity(intent);
        }
    }

    @Override
    public int getLayoutID() {
        activityStart = true;
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        if (intent != null) {
            mRoomInfo = (GzbVideoRoom) intent.getSerializableExtra("roomInfo");
            mRoom = (GetVideoRoomRequest.Result.PersonalMeeting) intent.getSerializableExtra("room");
            mTempRoom = (CreateTempVideoRequest) intent.getSerializableExtra("tempRoom");
            mRoomkey = intent.getStringExtra("roomkey");
            mSubject = intent.getStringExtra("subject");
        }
        mBcView = new LmiDeviceManagerView(this, this);
        if (!Config.VIDEO_LOGIN) {
            Toast.makeText(this, "会议已结束", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
        //必要的初始化 告诉video在当前activity渲染
        VideoClient.getInstance().initialize(this);
        VideoClient.getInstance().SimpleConference();
        //这个方法是点击通知栏的时候，进入点呼的接听逻辑
        isStart = true;

        if (mRoomInfo != null) {
            mRoomkey = mRoomInfo.getRoomKey();
            mSubject = mRoomInfo.gzbChatRoomName;
        } else if (mRoom != null) {
            mRoomkey = mRoom.roomKey;
            mSubject = mRoom.meetingName;
            String message = mRoom.meetingMessage;
            mMeetingMessage = TextUtils.isEmpty(message) ? "" : message;
        } else if (mTempRoom != null) {
            mRoomkey = mTempRoom.result.roomKey;
            mSubject = "临时会议";
        }
        if (!TextUtils.isEmpty(mRoomkey)) {
            VideoClient.getInstance().joinRoomConference(Config.getVideoUrl(), mRoomkey, mSubject, true, false, false,"");
        } else {
            VideoClient.getInstance().MuteCamera(true);
            LogUtils.e("roomKey may be null");
        }
        LogUtils.e("roomKey = " + mRoomkey);
        if (TextUtils.isEmpty(mRoomkey)) {
            VideoClient.getInstance().Answer();
        }
        VideoClient.getInstance().HideToolBar(true);
        return R.layout.activity_video;
    }

    @Override
    public void initView(View view) {
        mVideoLayout = findView(R.id.fl_video);
        mMicView = findView(R.id.iv_mic);
        mListenerView = findView(R.id.iv_listener);
        mRecordView = findView(R.id.iv_record);
        mCameraView = findView(R.id.iv_camera_change);
        mNextView = findView(R.id.iv_next);
        mMoreView = findView(R.id.iv_more);
        mWaitingView = findView(R.id.tv_waiting_view);
        mMenuLayout = findView(R.id.ll_menu);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }

        if (TextUtils.isEmpty(mRoomkey)) {
            //single
            mMoreView.setVisibility(View.GONE);
        } else if (mRoomInfo != null) {
            //gzb
            mMoreView.setVisibility(View.GONE);
        } else if (mTempRoom != null) {
            //三方app的临时会议
            mMoreView.setVisibility(View.GONE);
        } else {
            //normal//普通会议并且不是默认会议室
            //直接传roomkey
            if (mRoom != null
                    || (!TextUtils.isEmpty(mRoomkey))
                    ) {
                mMoreView.setVisibility(View.VISIBLE);
            }
        }
        //关闭progressActivity
        EventBus.getDefault().post(VideoHttpConfig.FINISH_PROGRESS);
        mVideoLayout.addView(mBcView, 0, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        doRender = true;
        initSensor();
        //  隐藏默认会议的更多按钮
        if (!TextUtils.isEmpty(mRoomkey) && getIntent() != null) {
            boolean showMoreBtn = getIntent().getBooleanExtra("showMoreBtn", false);
            mMoreView.setVisibility(showMoreBtn || mRoom != null ? View.VISIBLE : View.GONE);
        }
        LogUtils.e("会议描述：" + mMeetingMessage);
    }

    @Override
    public int addWaterMarkView() {
        return R.id.fl_water_video;
    }

    @Override
    public void initData() {
        //设置音量
        setupAudioMax();
        VideoClient.getInstance().EnableAllVideoStreams();
        mPowerManager = (PowerManager) this.getSystemService(Service.POWER_SERVICE);
        wakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Lock");
        if (isDirectCallWaiting()) {
            mWaitingView.setVisibility(View.VISIBLE);
            mWaitingView.setTime(DIRECT_CALL_TIME_OUT);
            mWaitingView.start();
            mWaitingView.setOnTimeRefreshListener(new WaitingTextView.OnTimeRefreshListener() {
                @Override
                public void onTimeRefresh(int time) {
                    if (time == 0) {
                        VideoClient.getInstance().stopConference();
                    }
                }
            });
        }
    }

    @Override
    public void initListener() {
        mBcView.setOnTouchListener(this);
    }

    /**
     * 判断是否是点呼的等待界面
     */
    private boolean isDirectCallWaiting() {
        return !TextUtils.isEmpty(mSubject) && mSubject.contains(Domain.DIRECT_CALL);
    }

    /**
     * 初始化传感器
     */
    private void initSensor() {
        currentOrientation = -1;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor gSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, gSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void videoInit() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //设置相机  camera:选择前(1)/后(0)摄像头
                LogUtils.writeLogFileByDate("video videoInit");
                VideoClient.getInstance().SetCameraDevice(1, 1);
                //设置预览模式启动 使得渲染界面可以看到自己
                VideoClient.getInstance().SetPreviewModeON(true);
                //禁用共享事件
                VideoClient.getInstance().DisableShareEvents();
                VideoClient.getInstance().MuteCamera(false);
                VideoClient.getInstance().micEnable();
                VideoClient.getInstance().CameraEnable();
                VideoClient.getInstance().listenerEnable();

                VideoClient.getInstance().HideToolBar(true);
                ManagerAudio.OpenSpeaker(UIUtils.getContext());
//                VideoClient.getInstance().SetOrientation(ORIENTATION_RIGHT);
            }
        }, 200);
    }

    @Override
    public void LmiDeviceManagerViewRender() {
        //渲染
        if (doRender) {
            VideoClient.getInstance().Render();
        }
    }

    @Override
    public void LmiDeviceManagerViewResize(int width, int height) {
        //窗口大小
        VideoClient.getInstance().Resize(width, height);
    }

    @Override
    public void LmiDeviceManagerViewRenderRelease() {
        //释放渲染占用的资源
        VideoClient.getInstance().RenderRelease();
    }

    @Override
    public void LmiDeviceManagerViewTouchEvent(int id, int type, int x, int y) {
        //渲染窗口的触摸事件
        VideoClient.getInstance().TouchEvent(id, type, x, y);
    }

    /**
     * 设置为最大音量
     */
    public static void setupAudioMax() {
        int set_Volume = 65535;
        VideoClient.getInstance().SetSpeakerVolume(set_Volume);
    }

    /**
     * 会议结束
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false, priority = 0)
    public void videoEnded(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
        if (VideoState.VIDEO_ENDED.equals(s)) {
            finish();
        } else if (Domain.INIT_VIDEO_ACTIVITY.equals(s)) {
            videoInit();
        } else if (Config.MEETING_MENU.equals(s)) {

        }
    }

    @Override
    public void onWindowFocusChanged(final boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && mIsOnPause) {
            resumeCall();
            VideoClient.getInstance().EnableAllVideoStreams();
        }
        SoftInputUtil.hintSoftInput(this);
    }

    private void resumeCall() {
        if (mBcView != null) {
            mBcView.onResume();
        }
    }

    private void pauseCall() {
        if (mBcView != null) {
            mBcView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsOnPause = false;
        if (Config.VIDEO_ACTIVITY_PAUSE) {
            resumeCall();
        }
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //必须调用 否则onRestart周期后会停止渲染会议画面
        mIsOnPause = true;
        LmiVideoCapturer.onActivityPause();
        if (Config.VIDEO_ACTIVITY_PAUSE) {
            pauseCall();
        }
        //禁止所有视频
//        VideoClient.getInstance().DisableAllVideoStreams();
    }

    @Override
    protected void onStop() {
        super.onStop();
        wakeLock.release();
    }

    @Override
    protected void onDestroy() {
        //如果是点呼的等待界面  告诉服务器超时
        if (isDirectCallWaiting()) {
            sendDirectCallTimeOut();
        }
        VideoClient.getInstance().SetPreviewModeON(true);
        //停止渲染
        VideoClient.getInstance().RenderRelease();
        VideoClient.getInstance().stopConference();
        mBcView = null;
        EventBus.getDefault().unregister(this);
        activityStart = false;
        LogUtils.writeLogFileByDate("video onDestroy");
        super.onDestroy();
    }

    /**
     * 告诉服务器超时
     */
    private void sendDirectCallTimeOut() {
        String callGzbId = mSubject.replace(Domain.DIRECT_CALL, "");
        HttpUtil.getInstance().post(new DirectCallTimeOutHelper(callGzbId), new HttpPostCallback<String>() {
            @Override
            public void onSuccess(String msg) {

            }

            @Override
            public void onFailure(String err) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        //离开会议
        VideoClient.getInstance().stopConference();
        if (!Config.VIDEO_START) {
            super.onBackPressed();
        }
    }

    /**
     * 传感器回调
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        //第一次为-1
        int newOrientation = currentOrientation;
        //获取sensor类型
        int type = event.sensor.getType();
        float[] data;
        //加速度传感器
        if (type == Sensor.TYPE_ACCELEROMETER) {
            /* get accelerometer data pointer */
            data = mGData;
        } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            //磁传感器
            data = mMData;
        } else {
            return;
        }
        //将数据复制到适当的数组
        System.arraycopy(event.values, 0, data, 0, 3);

		/*
         * magnetic data 根据两个传感器 计算出新值
		 */
        Boolean ret = SensorManager.getRotationMatrix(mR, mI, mGData, mMData);
        if (!ret) {
            return;
        }

        SensorManager.getOrientation(mR, mOrientation);
        //获取硬件
        Configuration config = getResources().getConfiguration();
        //物理键盘
        boolean hardKeyboardOrientFix = (config.hardKeyboardHidden
                == Configuration.HARDKEYBOARDHIDDEN_NO);
        //弧度
        int pitch = (int) (mOrientation[1] * degreePerRadian);
        int roll = (int) (mOrientation[2] * degreePerRadian);

        if (pitch < -45) {
            //物理键盘
            if (hardKeyboardOrientFix) {
                //旋转方向
                newOrientation = ORIENTATION_LEFT;
            } else {
                newOrientation = ORIENTATION_UP;
            }
        } else if (pitch > 45) {
            if (hardKeyboardOrientFix) {
                newOrientation = ORIENTATION_RIGHT;
            } else {
                newOrientation = ORIENTATION_DOWN;
            }
        } else if (roll < -45 && roll > -135) {
            if (hardKeyboardOrientFix) {
                newOrientation = ORIENTATION_UP;
            } else {
                newOrientation = ORIENTATION_RIGHT;
            }
        } else if (roll > 45 && roll < 135) {
            if (hardKeyboardOrientFix) {
                newOrientation = ORIENTATION_DOWN;
            } else {
                newOrientation = ORIENTATION_LEFT;
            }
        }

        if (newOrientation != currentOrientation) {
            currentOrientation = newOrientation;
            //  start
//            if (mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//                if (newOrientation == ORIENTATION_DOWN) {
//                    newOrientation = ORIENTATION_LEFT;
//                } else if (newOrientation == ORIENTATION_UP) {
//                    newOrientation = ORIENTATION_RIGHT;
//                }
//            }
//            LogUtils.e("newOrientation:" + newOrientation);
            //end
            VideoClient.getInstance().SetOrientation(newOrientation);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * @param view 录制视频会议
     */
    public void recordConference(View view) {

    }

    /**
     * 麦克按钮点击事件
     */
    public void micUseChange(View view) {
        if (doRender) {
            if (micCanUse) {
                micCanUse = false;
                mMicView.setImageResource(R.drawable.mic_none);
                VideoClient.getInstance().micDisenable();
            } else {
                micCanUse = true;
                VideoClient.getInstance().micEnable();
                mMicView.setImageResource(R.drawable.mic_normal);
            }
        } else {
            UIUtils.showToast(getString(R.string.please_operate_later));
        }
    }

    /**
     * 扬声器按钮点击事件
     */
    public void micListenerChange(View view) {
        if (doRender) {
            if (listenerCanUse) {
                mListenerView.setImageResource(R.drawable.voice_none);
                VideoClient.getInstance().ListenerDisenable();
            } else {
                VideoClient.getInstance().listenerEnable();
                mListenerView.setImageResource(R.drawable.voice_normal);
            }
            listenerCanUse = !listenerCanUse;
        } else {
            UIUtils.showToast(getString(R.string.please_operate_later));
        }
    }

    /**
     * 摄像头按钮点击事件
     */
    public void changeCamera(View view) {
//        changeCamera();
        selectCamera();
    }

    /**
     * 手动选择
     */
    private void selectCamera() {
        final Dialog dialog = new Dialog(this, R.style.Theme_MyDialog);
        View view = inflate(this, R.layout.dialog_camera_select, null);
        dialog.setContentView(view);
        view.findViewById(R.id.ll_camera_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mCameraView.setImageResource(R.drawable.camera_f);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        VideoClient.getInstance().SetCameraDevice(1, 1);
                        LogUtils.e("SetCameraDevice:" + 1);
                        if (!previewModeON) {
                            VideoClient.getInstance().SetPreviewModeON(true);
                            LogUtils.e("SetPreviewModeON:" + true);
                            previewModeON = true;
                        }
                        if (muteCamera) {
                            VideoClient.getInstance().MuteCamera(false);
                            muteCamera = false;
                            LogUtils.e("MuteCamera:" + false);
                        }
                    }
                };
                threadStart(runnable);
            }
        });
        view.findViewById(R.id.ll_camera_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mCameraView.setImageResource(R.drawable.camera_b);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        VideoClient.getInstance().SetCameraDevice(0, 1);
                        LogUtils.e("SetCameraDevice:" + 0);
                        if (!previewModeON) {
                            VideoClient.getInstance().SetPreviewModeON(true);
                            previewModeON = true;
                            LogUtils.e("SetPreviewModeON:" + true);
                        }
                        if (muteCamera) {
                            VideoClient.getInstance().MuteCamera(false);
                            muteCamera = false;
                            LogUtils.e("MuteCamera:" + false);
                        }
                    }
                };
                threadStart(runnable);
            }
        });
        view.findViewById(R.id.ll_camera_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mCameraView.setImageResource(R.drawable.camera_n);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (previewModeON) {
                            VideoClient.getInstance().SetPreviewModeON(false);
                            previewModeON = false;
                            LogUtils.e("SetPreviewModeON:" + false);
                        }
                        if (!muteCamera) {
                            VideoClient.getInstance().MuteCamera(true);
                            muteCamera = true;
                            LogUtils.e("MuteCamera:" + true);
                        }
                    }
                };
                threadStart(runnable);
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    private void threadStart(Runnable runnable) {
        new Thread(runnable).start();
    }

    /**
     * 前后关轮着来
     */
    private void changeCamera() {
        if (canChangeCamera) {
            canChangeCamera = false;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    canChangeCamera = true;
                }
            }, 3000);
            VideoClient.getInstance().changeCamera();
        } else {
            if (toast != null) {
                toast.cancel();
            }
            toast = UIUtils.showToast(getString(R.string.please_operate_later));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeCamera(ChangeCamera changeCamera) {
        usedCamera = changeCamera.getUsedCamera();
        if (usedCamera == 1) {
            mCameraView.setImageResource(R.drawable.camera_f);
        } else if (usedCamera == 0) {
            mCameraView.setImageResource(R.drawable.camera_b);
        } else if (usedCamera == -1) {
            mCameraView.setImageResource(R.drawable.camera_n);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void meetingCount(VideoEvent.VideoEventParticipantsChange entity) {
        if (entity.getParticipantsNum() >= 2) {
            mWaitingView.setVisibility(View.GONE);
            mWaitingView.cancel();
        }
    }

    public void more(View view) {
        Intent intent = new Intent();
        intent.setAction("com.telecomyt.video.sdk.action.VIDEO_SETTING");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * 退出按钮点击事件
     */
    public void end(View view) {
        safeFinish();
    }

    /**
     * 点击参会人员的会议列表item回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoParticipantItemClick(VideoEvent.VideoParticipantItemClick event) {
        VideoParticipantBean bean = event.getVideoParticipantBean();
        if (bean != null) {
            //会议激励功能...
            LogUtils.e("会议激励功能 bean:" + bean.toString());
            String uri = VideoClient.getInstance().getUri(event.getItemPos());
            if (!TextUtils.isEmpty(uri)) {
                uri = uri.replace("scip:", "")
//                        .replace("CsAPI", "")
                        .replace(";transport=TCP", "")
                        .replace(" ", "");
            }
            LogUtils.e("uri:" + uri);
            boolean setVideoTop = VideoClient.getInstance().setVideoTop(uri);
            LogUtils.e("setVideoTop:" + setVideoTop);
        }
    }

    /**
     * 显示已经在会议里的人
     */
    public void showVideoParticipants(View view) {
        if (!TextUtils.isEmpty(mRoomkey)) {
            UIUtils.showProgressDialog(this);
            int orientation = getResources().getConfiguration().orientation;
            int o = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                o = 1;
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                o = 0;
            }
            VideoParticipantListActivity.start(this, mRoomkey, o);
            overridePendingTransition(0, 0);
        }
    }

    /**
     * 旋转屏幕点击事件
     */
    public void orientationChange(View view) {
        ImageView imageView = (ImageView) view;
        int orientation = getResources().getConfiguration().orientation;
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mMenuLayout.getLayoutParams();
        int screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            setRequestedOrientation(screenOrientation);
            params.setMarginEnd(UIUtils.getDimen(R.dimen.video_menu_end_margin));
            imageView.setImageResource(R.drawable.video_orientation_land);
            VideoClient.getInstance().SetOrientation(ORIENTATION_LEFT);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            params.setMarginEnd(1);
            imageView.setImageResource(R.drawable.video_orientation_port);
        }
        setRequestedOrientation(screenOrientation);
        mScreenOrientation = screenOrientation;
        mMenuLayout.setLayoutParams(params);
    }

    private void safeFinish() {
        LogUtils.writeLogFileByDate("safeFinish : " + "  isStart:" + isStart + "   canFinish:" + canFinish);
//        if (isStart) {
//            if (canFinish) {
//                canFinish = false;
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        canFinish = true;
//                    }
//                }, 2000);
        VideoClient.getInstance().micEnable();
        VideoClient.getInstance().stopConference();
//            } else {
//                UIUtils.showToast(getString(R.string.please_operate_later));
//            }
//        } else {
//            if (canFinish) {
//                canFinish = false;
//                VideoActivity.this.finish();
//            }
//        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mMenuLayout.getVisibility() == View.INVISIBLE) {
                mMenuLayout.setVisibility(View.VISIBLE);
                mNextView.setVisibility(View.VISIBLE);
            } else {
                mMenuLayout.setVisibility(View.INVISIBLE);
                mNextView.setVisibility(View.INVISIBLE);
            }
        }
        return false;
    }
}
