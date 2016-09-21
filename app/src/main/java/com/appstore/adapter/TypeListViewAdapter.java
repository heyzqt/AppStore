package com.appstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.appstore.R;
import com.appstore.entity.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by heyzqt on 2016/9/20.
 */
public class TypeListViewAdapter extends BaseAdapter {

    private List<Map<String, List<Type>>> mDatas;

    private int mLayoutId;

    private Context mContext;

    private List<String> mTypeLists = new ArrayList<>();

    public TypeListViewAdapter(Context context, List<Map<String, List<Type>>> datas, int layoutId) {
        this.mDatas = datas;
        this.mLayoutId = layoutId;
        this.mContext = context;

        //取出Map的key值
        for (int i = 0; i < mDatas.size(); i++) {
            Map.Entry<String, List<Type>> entry = mDatas.get(i).entrySet().iterator().next();
            mTypeLists.add(entry.getKey().toString());
        }
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
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(mLayoutId, null);
            vh.mTvTypeName = (TextView) convertView.findViewById(R.id.tv_listview);
            vh.mGridView = (GridView) convertView.findViewById(R.id.gridview);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.mTvTypeName.setText(mTypeLists.get(position));
        TypeGridViewAdapter typeGridViewAdapter = new TypeGridViewAdapter(mContext
                , mDatas.get(position).get(mTypeLists.get(position)), R.layout.item_type_gridview, position);
        vh.mGridView.setAdapter(typeGridViewAdapter);

        final int listPosition = position;
        vh.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, mTypeLists.get(listPosition) +
                                "的" + mDatas.get(listPosition).get(mTypeLists
                                .get(listPosition)).get(position).getName().toString()
                        , Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView mTvTypeName;
        GridView mGridView;
    }
}
