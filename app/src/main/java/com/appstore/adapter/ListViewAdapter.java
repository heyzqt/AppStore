package com.appstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.appstore.R;
import com.appstore.fragment.MainFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by XFT on 2016/9/20.
 */
public class ListViewAdapter extends BaseAdapter implements View.OnClickListener{
    private LayoutInflater inflater;
    private  Context context;
    private ImageLoader mImageLoader;
    public ListViewAdapter(Context context)
    {
        this.context=context;
        this.inflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        MainFragment mf=new MainFragment();
        return mf.getListData().size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder=new ViewHolder();
        if(convertView==null)
        {
            convertView=inflater.inflate(R.layout.fg_listitem,null);
            holder=new ViewHolder();
            holder.img_download=(ImageView)convertView.findViewById(R.id.mfg_img_download);
            holder.img_appimg=(ImageView)convertView.findViewById(R.id.mfg_img_app);
            holder.tv_appname=(TextView) convertView.findViewById(R.id.mfg_tv_appname);
            holder.tv_appsize=(TextView) convertView.findViewById(R.id.mfg_tv_appsize);
            holder.tv_appintro=(TextView) convertView.findViewById(R.id.mfg_tv_intro);
            holder.rb_apprank=(RatingBar) convertView.findViewById(R.id.mfg_rb_ratingBar);
            convertView.setTag(holder);
        }
        else
        {
/*            mImageLoader = ImageLoader.getInstance();*/
            MainFragment mf=new MainFragment();
/*            ImgLoader il=new ImgLoader();
            il.initImageLoader(context);
            holder=(ViewHolder)convertView.getTag();
            mImageLoader.displayImage(mf.getListData().get(position).get("iconUrl").toString()
                    ,holder.img_appimg,il.getOption());
            holder.img_appimg.setScaleType(ImageView.ScaleType.FIT_XY);*/
/*            holder.img_appimg.setImageResource((int)mf.getData().get(position).get("appimg"));
            holder.rb_apprank.setProgress(mf.getData().get(position).get("stars"));*/
            holder.tv_appname.setText(mf.getListData().get(position).get("name").toString());
            holder.tv_appintro.setText(mf.getListData().get(position).get("intro").toString());
            holder.tv_appsize.setText(mf.getListData().get(position).get("size").toString());
            holder.rb_apprank.setProgress(Integer.parseInt(mf.getListData().get(position).get("stars").toString()));
        }
        return convertView;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.mfg_img_download:
                //downApk();
                break;
        }
    }
}
