package com.appstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.appstore.R;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by heyzqt on 2016/9/20.
 */
public class TypeListViewAdapter extends BaseAdapter {

    private List<Map<String, List<View>>> mDatas;

    private int mLayoutId;

    private Context mContext;

    private List<String> mTypeLists;

    public TypeListViewAdapter(Context context, List<Map<String, List<View>>> datas, int layoutId) {
        mDatas = datas;
        this.mLayoutId = layoutId;
        this.mContext = context;

        //取出Map值
        Iterator<Map<String, List<View>>> iterator = datas.iterator();
        while(iterator.hasNext()){
            Map<String, List<View>> map = iterator.next();
            Map.Entry entry = (Map.Entry) map.entrySet();
            mTypeLists.add((String) entry.getKey());
        }
    }

    @Override
    public int getCount() {
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
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(mLayoutId, null);
            vh.mTvTypeName = (TextView) convertView.findViewById(R.id.tv_apptype);
            vh.mGridView = (GridView) convertView.findViewById(R.id.gridview);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.mTvTypeName.setText(mTypeLists.get(position));
        TypeGridViewAdapter typeGridViewAdapter = new TypeGridViewAdapter();
        return convertView;
    }

    static class ViewHolder {
        TextView mTvTypeName;
        GridView mGridView;
    }
}
