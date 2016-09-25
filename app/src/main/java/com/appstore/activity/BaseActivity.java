package com.appstore.activity;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.appstore.StoreApplication;
import com.appstore.downloadutils.DownloadService;

/**
 * Created by heyzqt on 2016/9/25.
 */
public class BaseActivity extends AppCompatActivity{

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

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
