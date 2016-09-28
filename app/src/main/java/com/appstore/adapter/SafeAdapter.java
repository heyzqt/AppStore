package com.appstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appstore.R;
import com.appstore.StoreApplication;
import com.appstore.entity.Safe;
import com.appstore.entity.Subject;
import com.appstore.utils.ImgUtils;

import java.util.List;

/**
 * Created by 张艳琴 on 2016/9/22.
 */
public class SafeAdapter extends BaseAdapter {
    List<Safe> safeList = null;
    Context mContext;
    LayoutInflater inflater;

    public SafeAdapter(Context context, List<Safe> safeList) {
        this.safeList = safeList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return safeList.size();
    }

    @Override
    public Object getItem(int position) {
        return safeList.get(position);
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
            convertView = inflater.inflate(R.layout.item_safe, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.safe_item_text);
            holder.icon= (ImageView) convertView.findViewById(R.id.safe_item_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(safeList.get(position).getSafeDes());
        ImgUtils.setInterImg1(StoreApplication.IP_ADDRESS+"image?name="+safeList.get(position).getSafeDesUrl(),holder.icon,R.mipmap.safedesurl0);
       // holder.icon.setImageResource(R.mipmap.safedesurl0);
        return convertView;
    }

    public class ViewHolder {
        TextView title;
        ImageView icon;
    }

}