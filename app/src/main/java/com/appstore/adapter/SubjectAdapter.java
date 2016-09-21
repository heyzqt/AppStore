package com.appstore.adapter;

/**
 * Created by skysoft on 2016/8/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appstore.R;
import com.appstore.StoreApplication;
import com.appstore.entity.Subject;
import com.appstore.utils.ImgUtils;

import java.util.List;

public class SubjectAdapter extends BaseAdapter {
    List<Subject> subjectList = null;
    Context mContext;
    LayoutInflater inflater;

    public SubjectAdapter(Context context, List<Subject> subjectList) {
        this.subjectList = subjectList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return subjectList.size();
    }

    @Override
    public Object getItem(int position) {
        return subjectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            inflater = LayoutInflater.from(mContext);
            // 按当前所需的样式，确定new的布局
            convertView = inflater.inflate(R.layout.subject_list_item, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.subject_item_text);
            holder.bg = (ImageView) convertView.findViewById(R.id.subject_item_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(subjectList.get(position).getDes());
        ImgUtils.setInterImg(StoreApplication.IP_ADDRESS+"image?name="+subjectList.get(position).getUrl(),holder.bg);
        return convertView;
    }

    public class ViewHolder {
        TextView title;
        ImageView bg;
    }

}