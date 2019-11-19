package com.telecomyt.videolibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.bean.VideoParticipantBean;

import java.util.List;

/**
 * @author lbx
 * @date 2018/1/24.
 */

public class VideoControlAdapter extends RecyclerView.Adapter<VideoControlAdapter.ViewHolder> {

    private List<VideoParticipantBean> mList;
    private LayoutInflater mInflater;

    public VideoControlAdapter(Context context, List<VideoParticipantBean> list) {
        this.mList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_video_control, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mOnVideoControlClickListener == null) {
            mOnVideoControlClickListener = OnVideoControlClickListener.DEFAULT_LISTENER;
        }
        final VideoParticipantBean participantBean = mList.get(position);
        holder.nameTextView.setText(participantBean.getDisplayName());
        holder.heardImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnVideoControlClickListener.onHeardViewClick(participantBean, (ImageView) v);
            }
        });
        holder.cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnVideoControlClickListener.onCameraViewClick(participantBean, (ImageView) v);
            }
        });
        holder.micImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnVideoControlClickListener.onMicViewClick(participantBean, (ImageView) v);
            }
        });
        holder.finishImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnVideoControlClickListener.onFinishViewClick(participantBean, (ImageView) v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private ImageView  heardImageView, cameraImageView, micImageView, finishImageView;

        private ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.tv_participant_name);
            heardImageView = (ImageView) itemView.findViewById(R.id.tv_participant_heard);
            cameraImageView = (ImageView) itemView.findViewById(R.id.tv_participant_camera);
            micImageView = (ImageView) itemView.findViewById(R.id.tv_participant_mic);
            finishImageView = (ImageView) itemView.findViewById(R.id.tv_participant_finish);
        }
    }

    private OnVideoControlClickListener mOnVideoControlClickListener;

    public interface OnVideoControlClickListener {

        void onHeardViewClick(VideoParticipantBean bean, ImageView view);

        void onCameraViewClick(VideoParticipantBean bean, ImageView view);

        void onMicViewClick(VideoParticipantBean bean, ImageView view);

        void onFinishViewClick(VideoParticipantBean bean, ImageView view);

        OnVideoControlClickListener DEFAULT_LISTENER = new OnVideoControlClickListener() {

            @Override
            public void onHeardViewClick(VideoParticipantBean bean, ImageView view) {

            }

            @Override
            public void onCameraViewClick(VideoParticipantBean bean, ImageView view) {

            }

            @Override
            public void onMicViewClick(VideoParticipantBean bean, ImageView view) {

            }

            @Override
            public void onFinishViewClick(VideoParticipantBean bean, ImageView view) {

            }
        };
    }

    public void setOnVideoControlClickListener(OnVideoControlClickListener onVideoControlClickListener) {
        this.mOnVideoControlClickListener = onVideoControlClickListener;
    }
}
