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
import com.appstore.utils.ImgUtils;

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
//        if (mDatas.get(position).getImageUrl() != null &&!mDatas.get(position).getImageUrl().equals("")) {
//            if (position==0){
//                String url = "http://img5.imgtn.bdimg.com/it/u=642692494,1199448469&fm=21&gp=0.jpg";
//                ImgUtils.setInterImg(convertView,url,vh.mImageView);
//            }else{
//                vh.mImageView.setImageResource(R.mipmap.category_game_4);
//            }
//            vh.mTvName.setText(mDatas.get(position).getName());
//        }else{
//            vh.mBackground.setBackgroundColor(mContext.getResources().getColor(R.color.color_transparent));
//        }
        if (position==0){
            String url = "http://img5.imgtn.bdimg.com/it/u=642692494,1199448469&fm=21&gp=0.jpg";
            ImgUtils.setInterImg(url,vh.mImageView);
        }else if(position==1){
            String url = "http://img4.imgtn.bdimg.com/it/u=2459295303,3320131763&fm=21&gp=0.jpg";
            ImgUtils.setInterImg(url,vh.mImageView);
        }
        else{
            vh.mImageView.setImageResource(R.mipmap.category_game_4);
        }
        vh.mTvName.setText(mDatas.get(position).getName());
        return convertView;
    }

    static class ViewHolder {
        ImageView mImageView;
        TextView mTvName;
        RelativeLayout mBackground;
    }
}