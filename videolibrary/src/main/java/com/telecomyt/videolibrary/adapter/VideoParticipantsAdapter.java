package com.telecomyt.videolibrary.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.bean.VideoParticipantBean;

import java.util.List;

/**
 * @author lbx
 * @date 2018/1/30.
 */

public class VideoParticipantsAdapter extends BaseAdapter {

    private List<VideoParticipantBean> mList;
    private Context mContext;

    public VideoParticipantsAdapter(Context context, List<VideoParticipantBean> mList) {
        this.mContext = context;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public VideoParticipantBean getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_video_participant, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        VideoParticipantBean bean = mList.get(position);
        holder.nameView.setText(bean.getDisplayName());
        return convertView;
    }

    private static final class ViewHolder {

        private TextView nameView;

        private ViewHolder(View convertView) {
            nameView = (TextView) convertView.findViewById(R.id.tv_video_participant);
        }
    }
}

