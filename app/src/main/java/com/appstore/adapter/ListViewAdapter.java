package com.appstore.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appstore.R;
import com.appstore.activity.DownloadService;
import com.appstore.entity.DownLoadInfo;
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
import java.util.List;

/**
 * Created by XFT on 2016/9/20.
 */
public class ListViewAdapter extends BaseAdapter implements View.OnClickListener {
    private LayoutInflater inflater;
    private Context context;
    private ImageLoader mImageLoader;
    public static String IMAGE_CACHE_PATH = "imageloader/Cache";
    private ArrayList<HashMap<String, Object>> listdata;
    private List<DownLoadInfo> mWaittings;
    private DownLoadInfo mDownloadInfo;
    private DownloadService mService;
    private int progress;

    public ListViewAdapter(Context context, ArrayList<HashMap<String, Object>> listdata) {
        this.context = context;
        this.listdata = listdata;
        this.inflater = LayoutInflater.from(context);
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public List<DownLoadInfo> getWaittings() {
        return mWaittings;
    }

    public void setWaittings(List<DownLoadInfo> mWaittings) {
        this.mWaittings = mWaittings;
    }

    public DownLoadInfo getDownloadInfo() {
        return mDownloadInfo;
    }

    public void setDownloadInfo(DownLoadInfo mDownloadInfo) {
        this.mDownloadInfo = mDownloadInfo;
    }

    public DownloadService getService() {
        return mService;
    }

    public void setService(DownloadService mService) {
        this.mService = mService;
    }

    @Override
    public int getCount() {
        MainFragment mf = new MainFragment();
        return listdata.size();
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

        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fg_listitem, null);
            holder = new ViewHolder();
            holder.img_download = (ImageView) convertView.findViewById(R.id.fg_img_download);
            holder.img_appimg = (ImageView) convertView.findViewById(R.id.mfg_img_app);
            holder.tv_appname = (TextView) convertView.findViewById(R.id.mfg_tv_appname);
            holder.tv_appsize = (TextView) convertView.findViewById(R.id.mfg_tv_appsize);
            holder.tv_appintro = (TextView) convertView.findViewById(R.id.mfg_tv_intro);
            holder.rb_apprank = (RatingBar) convertView.findViewById(R.id.mfg_rb_ratingBar);
            holder.mTvPos = (TextView) convertView.findViewById(R.id.tv_downpos);

            holder.img_download.setOnClickListener(this);


            ImgLoaders loader = new ImgLoaders();
            loader.initImageLoader(context);
            mImageLoader = ImageLoader.getInstance();
            holder.img_appimg.setScaleType(ImageView.ScaleType.FIT_XY);
            mImageLoader.displayImage("http://localhost:8090/image?name=" + listdata.get(position).get("iconUrl").toString(), holder.img_appimg, loader.getOption());

            holder.rb_apprank.setRating(Float.parseFloat(listdata.get(position).get("stars").toString()));

            holder.tv_appname.setText(listdata.get(position).get("name").toString());
            holder.tv_appintro.setText(listdata.get(position).get("intro").toString());
            DecimalFormat df = new DecimalFormat("#.##");
            holder.tv_appsize.setText(String.valueOf(df.format((Float.parseFloat(listdata.get(position).get("size").toString()) / (1024 * 1024)))) + "MB");
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.img_appimg.setScaleType(ImageView.ScaleType.FIT_XY);
            ImgLoaders loader = new ImgLoaders();
            loader.initImageLoader(context);
            mImageLoader = ImageLoader.getInstance();
            mImageLoader.displayImage("http://localhost:8090/image?name=" + listdata.get(position).get("iconUrl").toString(), holder.img_appimg, loader.getOption()
            );

            holder.rb_apprank.setRating(Float.parseFloat(listdata.get(position).get("stars").toString()));

            holder.tv_appname.setText(listdata.get(position).get("name").toString());
            holder.tv_appintro.setText(listdata.get(position).get("intro").toString());
            DecimalFormat df = new DecimalFormat("#.##");
            holder.tv_appsize.setText(String.valueOf(df.format((Float.parseFloat(listdata.get(position).get("size").toString()) / (1024 * 1024)))) + "MB");

            holder.img_download.setOnClickListener(this);

            //mProgressbar.setProgress(mService.mDownLoadInfo.getPos());
            if (mService != null && mService.mDownLoadInfo != null &&
                    listdata.get(position).get("packagename")
                            .equals(mService.mDownLoadInfo.getPackagename())
                    && mService.mDownLoadInfo.getStatus() == DownloadService.DOWN_LOADING) {
                holder.mTvPos.setText(mService.mDownLoadInfo.getPos() + "%");
                mService.mDownLoadInfo.setStatus(DownloadService.DOWN_LOADING);
                mService.isDownloading = true;
                mService.downloadAPP(mService.getAppInfo());
            }
        }


        return convertView;
    }

//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 1) {
//                int progress = msg.arg1;
//                //ViewHolder holder = msg.getData();
//                switch (mDownloadInfo.getStatus()) {
//                    //APP未被下载
//                    case DownloadService.DOWN_UNLOAD:
//                        .setText("下载");
//                        break;
//                    //APP正在下载
//                    case DownloadService.DOWN_LOADING:
//                        mTvDownload.setText(progress + "%");
//                        if (progress == 100) {
//                            mTvDownload.setText("下载完成");
//                            mDownloadInfo.setStatus(DownloadService.DOWN_FINISHED);
//                        }
//                        break;
//                    //APP暂停下载
//                    case DownloadService.DOWN_PAUSE:
//                        mTvDownload.setText("继续下载");
//                        break;
//                    //APP下载完成
//                    case DownloadService.DOWN_FINISHED:
//                        mTvDownload.setText("点击下载");
//                        break;
//                }
//            }
//        }
//    };


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fg_img_download:
                Toast.makeText(context, "开始下载", Toast.LENGTH_SHORT).show();
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

    public DisplayImageOptions getOption() {
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY).build();
        return options;
    }
}
