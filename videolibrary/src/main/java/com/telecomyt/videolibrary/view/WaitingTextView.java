package com.telecomyt.videolibrary.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import com.telecomyt.videolibrary.R;

import static com.telecomyt.videolibrary.utils.UIUtils.getString;

/**
 * @author lbx
 * @date 2018/1/19.
 */

public class WaitingTextView extends android.support.v7.widget.AppCompatTextView {

    private static final int REFRESH_TEXT = 0x010;
    private int time = 30;
    private String string = getString(R.string.waiting_answer);

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_TEXT:
                    if (onTimeRefreshListener != null) {
                        onTimeRefreshListener.onTimeRefresh(time--);
                    }
                    if (time < 0) {
                        return;
                    }
                    String point = ".";
                    String split = " ";
                    int max = 3;
                    int num = Math.abs(time % max - 2);
                    String s = string + point;
                    for (int i = 0; i < max; i++) {
                        if (i >= num) {
                            s += split;
                        } else {
                            s += point;
                        }
                    }
                    setText(s);
                    handler.sendEmptyMessageDelayed(REFRESH_TEXT, 1000);
                    break;
                default:
                    break;
            }
        }
    };

    public WaitingTextView(Context context) {
        this(context, null);
    }

    public WaitingTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaitingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setText(getString(R.string.waiting_answer));
    }

    public void start() {
        handler.sendEmptyMessageDelayed(REFRESH_TEXT, 1000);
    }

    public void cancel() {
        handler.removeMessages(REFRESH_TEXT);
    }

    private OnTimeRefreshListener onTimeRefreshListener;

    public interface OnTimeRefreshListener {
        void onTimeRefresh(int time);
    }

    public void setOnTimeRefreshListener(OnTimeRefreshListener onTimeRefreshListener) {
        this.onTimeRefreshListener = onTimeRefreshListener;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }
}
