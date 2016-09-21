package com.appstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appstore.R;
import com.appstore.entity.Type;

import java.util.List;

/**
 * Created by heyzqt on 2016/9/20.
 */
public class TypeGridViewAdapter extends BaseAdapter {

    private Context mContext;

    private List<Type> mDatas;

    private int mLayoutId;

    private int mPosition;

    public TypeGridViewAdapter(Context context, List<Type> lists, int layoutId, int position) {
        this.mContext = context;
        this.mDatas = lists;
        this.mLayoutId = layoutId;
        this.mPosition = position;
    }

    @Override
    public int getCount() {
        if (mDatas == null)
            return 0;
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_type_gridview, null);
            vh.mImageView = (ImageView) convertView.findViewById(R.id.img_app);
            vh.mTvName = (TextView) convertView.findViewById(R.id.tv_apptype);
            vh.mBackground = (RelativeLayout) convertView.findViewById(R.id.rl_gridview);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        if (mDatas.get(position).getImageUrl() != null &&!mDatas.get(position).getImageUrl().equals("")) {
            vh.mImageView.setImageResource(R.mipmap.category_game_4);
            vh.mTvName.setText(mDatas.get(position).getName());
        }else{
            vh.mBackground.setBackgroundColor(mContext.getResources().getColor(R.color.color_transparent));
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView mImageView;
        TextView mTvName;
        RelativeLayout mBackground;
    }
}