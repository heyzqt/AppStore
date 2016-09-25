package com.appstore.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appstore.R;
import com.appstore.fragment.MainFragment;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by XFT on 2016/9/20.
 */
public class ListViewAdapter extends BaseAdapter implements View.OnClickListener{
    private LayoutInflater inflater;
    private  Context context;
    private ImageLoader mImageLoader;
    public static String IMAGE_CACHE_PATH = "imageloader/Cache";
    private ArrayList<HashMap<String,Object>> listdata;
    public ListViewAdapter(Context context, ArrayList<HashMap<String,Object>> listdata)
    {
        Log.i("125"," ListViewAdapter");
        this.context=context;
        this.listdata=listdata;
        this.inflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        MainFragment mf=new MainFragment();
        Log.i("125","getcount:"+String.valueOf(listdata.size()));
        return listdata.size();
    }

    @Override
    public Object getItem(int position) {
        Log.i("125"," getItem");
        return null;
    }

    @Override
    public long getItemId(int position) {
        Log.i("125"," getItemId");
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder=new ViewHolder();
        if(convertView==null)
        {
            convertView=inflater.inflate(R.layout.fg_listitem,null);
            holder=new ViewHolder();
            holder.img_download=(ImageView)convertView.findViewById(R.id.fg_img_download);
            holder.img_appimg=(ImageView)convertView.findViewById(R.id.mfg_img_app);
            holder.tv_appname=(TextView) convertView.findViewById(R.id.mfg_tv_appname);
            holder.tv_appsize=(TextView) convertView.findViewById(R.id.mfg_tv_appsize);
            holder.tv_appintro=(TextView) convertView.findViewById(R.id.mfg_tv_intro);
            holder.rb_apprank=(RatingBar) convertView.findViewById(R.id.mfg_rb_ratingBar);

            holder.img_download.setOnClickListener(this);



            imgLoader loader=new imgLoader();
            loader.initImageLoader(context);
 //           initImageLoader(context);
            mImageLoader = ImageLoader.getInstance();
            holder.img_appimg.setScaleType(ImageView.ScaleType.FIT_XY);
            mImageLoader.displayImage("http://localhost:8090/image?name="+listdata.get(position).get("iconUrl").toString(),holder.img_appimg,loader.getOption());

           // Log.i("127","imgurl:"+listdata.get(position).get("iconUrl").toString());
            holder.rb_apprank.setRating(Float.parseFloat(listdata.get(position).get("stars").toString()));

            holder.tv_appname.setText(listdata.get(position).get("name").toString());
           // Log.i("127","appname:"+listdata.get(position).get("name").toString());
            holder.tv_appintro.setText(listdata.get(position).get("intro").toString());
           // Log.i("127","appintro:"+listdata.get(position).get("intro").toString());
            DecimalFormat df=new DecimalFormat("#.##");
            holder.tv_appsize.setText(String.valueOf(df.format((Float.parseFloat(listdata.get(position).get("size").toString())/(1024*1024))))+"MB");
           // String.valueOf((Float.parseFloat(listdata.get(position).get("size").toString())))+"MB"
            convertView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)convertView.getTag();
            holder.img_appimg.setScaleType(ImageView.ScaleType.FIT_XY);
            imgLoader loader=new imgLoader();
            loader.initImageLoader(context);
            mImageLoader = ImageLoader.getInstance();
            mImageLoader.displayImage("http://localhost:8090/image?name="+listdata.get(position).get("iconUrl").toString(),holder.img_appimg,loader.getOption()
            );

            holder.rb_apprank.setRating(Float.parseFloat(listdata.get(position).get("stars").toString()));

            holder.tv_appname.setText(listdata.get(position).get("name").toString());
            holder.tv_appintro.setText(listdata.get(position).get("intro").toString());
            DecimalFormat df=new DecimalFormat("#.##");
            holder.tv_appsize.setText(String.valueOf(df.format((Float.parseFloat(listdata.get(position).get("size").toString())/(1024*1024))))+"MB");

            holder.img_download.setOnClickListener(this);
        }
        return convertView;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.fg_img_download:
                Toast.makeText(context,"开始下载",Toast.LENGTH_SHORT).show();
                //downApk();
                break;
        }
    }

    public void initImageLoader(Context context) {
        File cacheDir = com.nostra13.universalimageloader.utils.StorageUtils
                .getOwnCacheDirectory(context,
                        IMAGE_CACHE_PATH);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new LruMemoryCache(12 * 1024 * 1024))
                .memoryCacheSize(12 * 1024 * 1024)
                .discCacheSize(32 * 1024 * 1024).discCacheFileCount(100)
                .discCache(new UnlimitedDiscCache(cacheDir))
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    public DisplayImageOptions getOption()
    {
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY).build();
        return  options;
    }
}
