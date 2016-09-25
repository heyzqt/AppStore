package com.appstore.downloadutils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by heyzqt on 2016/9/25.
 */
public class DownloadService extends Service {

    public static final int DOWN_UNLOAD = 0x1;      //未下载
    public static final int DOWN_LOADING = 0x2;     //正在下载
    public static final int DOWN_PAUSE = 0x3;       //暂停下载
    public static final int DOWN_FINISHED = 0x4;    //完成下载
    public static final int DOWN_FAILURE = 0x5;     //下载失败
    public static final int DOWN_STOPED = 0x6;      //停止下载

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    class MyBinder extends Binder {
        public DownloadService getService(){
            return DownloadService.this;
        }
    }


}
