package com.appstore.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.appstore.StoreApplication;
import com.appstore.entity.AppInfo;

/**
 * Created by heyzqt on 2016/9/25.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected StoreApplication mApp;
    protected DownloadService mService;
    private boolean isBound = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (StoreApplication) getApplication();
    }

    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownloadService.MyBinder downBinder = (DownloadService.MyBinder) service;
            mService = downBinder.getService();

            mService.setDownloadUpdateListener(mDownloadUpdateListener);
            mDownloadUpdateListener.onChange(mService.getAppInfo());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    /**
     * 绑定服务
     */
    public void bindService() {
        if (!isBound) {
            Intent intent = new Intent(this, DownloadService.class);
            bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);
            isBound = true;
        }
    }

    /**
     * 解绑服务
     */
    public void unbindService(){
        if(isBound){
            unbindService(mServiceConn);
            isBound = false;
        }
    }

    private DownloadService.DownloadUpdateListener mDownloadUpdateListener = new DownloadService.DownloadUpdateListener() {
        @Override
        public void onPublish(int progress) {
            publish(progress);
        }

        @Override
        public void onChange(AppInfo appInfo) {
            change(appInfo);
        }


    };

    public abstract void publish(int progress);

    public abstract void change(AppInfo appInfo);
}
