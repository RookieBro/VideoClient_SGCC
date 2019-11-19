package com.telecomyt.videolibrary.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.bean.DefaultVideoRoom;
import com.telecomyt.videolibrary.bean.VideoDefaultMeeting;
import com.telecomyt.videolibrary.view.CircleTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lbx
 * @date 2017/10/13.
 */

public class DefaultRoomAdapter<T extends DefaultVideoRoom, M extends VideoDefaultMeeting> extends BaseExpandableListAdapter {

    private List<T> mListDefault;
    private Context mContext;

    public DefaultRoomAdapter(List<T> mListDefault, Context mContext) {
        setList(mListDefault);
        this.mContext = mContext;
    }

    public void setList(List<T> mListDefault) {
        List<T> list = new ArrayList<>();
        for (T t : mListDefault) {
            if (!t.isEmpty()) {
                list.add(t);
            }
        }
        this.mListDefault = list;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mListDefault.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mListDefault.get(i).getList().size();
    }

    @Override
    public T getGroup(int i) {
        return mListDefault.get(i);
    }

    @Override
    public M getChild(int i, int i1) {
        return (M) mListDefault.get(i).getList().get(i1);
    }

    public List<T> getListDefault() {
        return mListDefault;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        view = View.inflate(mContext, R.layout.item_default_room_group, null);
        TextView tv_defaultRoom = (TextView) view.findViewById(R.id.tv_defaultRoom);
        ImageView iv_stateIcon = (ImageView) view.findViewById(R.id.iv_stateIcon);

        T t = mListDefault.get(i);

        tv_defaultRoom.setText(t.getName());

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildViewHolder holder;
        if (view == null) {
            view = View.inflate(mContext, R.layout.item_default_room_child, null);
            holder = new ChildViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ChildViewHolder) view.getTag();
        }

        M data = getChild(i, i1);

        holder.tv_bigTitle.setText(data.meetingName);
        holder.ctv_head.setTextWithColorAndSize("会议室", R.color.white_main, 12);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private static class ChildViewHolder {
        private TextView tv_bigTitle;
        private CircleTextView ctv_head;

        private ChildViewHolder(View view) {
            tv_bigTitle = (TextView) view.findViewById(R.id.tv_bigTitle);
            ctv_head = (CircleTextView) view.findViewById(R.id.ctv_head);
        }
    }
}
