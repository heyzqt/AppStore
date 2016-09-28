package com.appstore;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.appstore.activity.DownloadService;
import com.appstore.dbutils.DBHelper;
import com.lidroid.xutils.DbUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;

/**
 * Created by heyzqt on 2016/9/21.
 */
public class StoreApplication extends Application {

    /**
     * 服务器IP地址
     */
    public static String IP_ADDRESS;

    private final String IMAGE_CACHE_PATH="AppStore/Cache";

    /**
     * SharedPreferences对象
     */
    public SharedPreferences sp;

    public SharedPreferences.Editor editor;

    /**
     * 数据库操作对象
     */
    public DbUtils dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        IP_ADDRESS = getResources().getString(R.string.ip_address);
        initImageLoader();

        sp = getSharedPreferences("AppStore", Context.MODE_PRIVATE);
        editor = sp.edit();

        dbHelper = DBHelper.getInstance(this);

        //启动服务
        startService(new Intent(this, DownloadService.class));
    };

    private void initImageLoader() {
        File cacheDir = com.nostra13.universalimageloader.utils.StorageUtils
                .getOwnCacheDirectory(getApplicationContext(),
                        IMAGE_CACHE_PATH);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new LruMemoryCache(12 * 1024 * 1024))
                .memoryCacheSize(12 * 1024 * 1024)
                .discCacheSize(32 * 1024 * 1024).discCacheFileCount(100)
                .discCache(new UnlimitedDiscCache(cacheDir))
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();

        ImageLoader.getInstance().init(config);
    }
}
