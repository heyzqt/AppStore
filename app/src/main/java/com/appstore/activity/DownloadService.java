package com.appstore.activity;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.appstore.StoreApplication;
import com.appstore.entity.AppInfo;
import com.appstore.entity.DownLoadInfo;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by heyzqt on 2016/9/25.
 */
public class DownloadService extends Service {

    /**
     * 下载对象列表
     */
    public List<DownLoadInfo> mWaittingInfos = new ArrayList<>();

    public List<AppInfo> mAppInfos = new ArrayList<>();

    /**
     * 当前正在下载的对象
     */
    public DownLoadInfo mDownLoadInfo;
//
//    /**
//     * 当前下载的进度
//     */
//    private int mCurrentPos;

    /**
     * 当前下载APP对象
     */
    private AppInfo mAppInfo;

    /**
     * 当前对象是否正在被下载
     */
    public boolean isDownloading = false;

    /**
     * 当前正在下载和等待的APP的总数
     */
    public int mCurrentDownApp = 0;

    /**
     * 下载状态更新接口
     */
    public DownloadUpdateListener mDownloadUpdateListener;

    /**
     * 文件路径
     */
    private String mPath;

    /**
     * APP下载线程池
     */
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    /**
     * APP下载状态更新
     */
    private ExecutorService mUpdateExec = Executors.newSingleThreadExecutor();

    private MyRunnable mRunnable = null;

    private StoreApplication mStoreAPP;

    private static final String TAG = "hello";

    public static final int DOWN_UNLOAD = 0x0;      //未下载
    public static final int DOWN_LOADING = 0x2;     //正在下载
    public static final int DOWN_PAUSE = 0x3;       //暂停下载
    public static final int DOWN_FINISHED = 0x4;    //完成下载
    public static final int DOWN_WAITTING = 0x5;    //等待下载
    public static final int DOWN_FAILURE = 0x6;     //下载失败
    public static final int DOWN_STOPED = 0x7;      //停止下载
    public static final int DOWN_INSTALL = 0x8;     //程序已安装

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    class MyBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    public DownloadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mStoreAPP = (StoreApplication) getApplication();
        mPath = Environment.getExternalStorageDirectory().getPath() + "/AppStore/";
        mUpdateExec.execute(updateStatusRunnable);
//        //获取当前下载列表
//        try {
//            mDownLoadInfos = DBHelper.getInstance(mStoreAPP).findAll(DownLoadInfo.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//            mDownLoadInfos = null;
//        }
    }

    //更新下载状态接口
    public interface DownloadUpdateListener {
        public void onPublish(int progress);    //更新进度条位置

        public void onChange(DownLoadInfo downLoadInfo);         //更新下载按钮状态
    }

    public void setDownloadUpdateListener(DownloadUpdateListener downloadUpdateListener) {
        this.mDownloadUpdateListener = downloadUpdateListener;
    }

    /**
     * 下载APP
     *
     * @param appInfo
     */
    public void downloadAPP(AppInfo appInfo) {

        String url = StoreApplication.IP_ADDRESS + "download?name=" + appInfo.getDownloadUrl() + "&&range=";
        String filename = appInfo.getPackageName() + ".apk";

        setAppInfo(appInfo);
        final File file = new File(mPath, filename);
        if (file.exists()) {
            final long filesize = file.length();
            if (filesize > 0) {
                down(url + filesize, filesize, mPath + filename);
            } else {
                down(url, 0, mPath + filename);
            }
        } else {
            try {
                file.createNewFile();
                down(url, 0, mPath + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载APP
     *
     * @param url
     * @param pos
     * @param savePathAndFile
     */
    private void down(final String url, final long pos, final String savePathAndFile) {
        mRunnable = new MyRunnable(url, pos, savePathAndFile);
        mExecutor.execute(mRunnable);
    }

    /**
     * 更新进度条线程
     */
    class MyRunnable implements Runnable {

        String url;
        long pos;
        String savePathAndFile;

        public MyRunnable(final String url, final long pos, final String savePathAndFile) {
            this.url = url;
            this.pos = pos;
            this.savePathAndFile = savePathAndFile;
        }

        public void setPos(long pos) {
            this.pos = pos;
        }

        @Override
        public void run() {

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                int contentLength = conn.getContentLength();
                InputStream input = conn.getInputStream();
                RandomAccessFile inFile = new RandomAccessFile(savePathAndFile, "rw");
                //定位到pos位置
                inFile.seek(pos);
                int read;
                byte[] b = new byte[1024];
                //从输入流中读出字节流,再写入文件
                while ((read = input.read(b, 0, 1024)) > 0) {
                    if (!isDownloading) {
                        return;
                    }
                    inFile.write(b, 0, read);
                    Message msg = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putLong("current", inFile.length());
                    bundle.putLong("total", contentLength);
                    msg.what = 1;
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
                conn.disconnect();
            } catch (IOException e) {
                Log.e(TAG,"IOException!!!!!!");
                e.printStackTrace();
            } finally {
                mHandler.sendEmptyMessage(2);
            }
        }
    }

    // 创建Runnable线程，实时更新下载的进度条
    Runnable updateStatusRunnable = new Runnable() {

        @Override
        public void run() {
            while (true) {
                if (mDownloadUpdateListener != null && mDownLoadInfo != null) {
                    mDownloadUpdateListener.onPublish(mDownLoadInfo.getPos());
                    //Log.i(TAG, "updateStatusRunnable线程  pos====" + mDownLoadInfo.getPos());
                    //Log.e(TAG, "run: mDownloadUpdateListener==="+mDownloadUpdateListener.toString());
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO: handle exception
                }
            }
        }
    };

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    long current = bundle.getLong("current");
                    long total = bundle.getLong("total");
                    String result = new DecimalFormat("0.00").format((double) current / total);
                    double d = Double.parseDouble(result);
                    int i = (int) (d * 100);
                    mDownLoadInfo.setPos(i);

                   //Log.e(TAG, "handleMessage: msg==1,pos==="+i);
                    Log.e(TAG, "handleMessage: msg==1:"+mDownLoadInfo.getPackagename()+",current="+current+",pos==="+i);
                    if (i == 100) {
                        mDownLoadInfo.setStatus(DOWN_FINISHED);
                    }
//                    if (mDownloadUpdateListener != null && mDownLoadInfo != null && isDownloading == true) {
//                        mDownloadUpdateListener.onPublish(i);
//                        Log.e(TAG, "handleMessage: "+mDownloadUpdateListener.toString());
//                    }
                    break;
                case 2:
                    try {
                        if(mDownLoadInfo.getStatus()== DownloadService.DOWN_FINISHED){
                           Log.e(TAG, "handleMessage: msg===2,mDownLoadInfo===="+mDownLoadInfo.getPackagename() );
                            mStoreAPP.dbHelper.saveOrUpdate(mDownLoadInfo);
                            //从等待队列中取出第一个APP来下载
                            if (mWaittingInfos != null && mWaittingInfos.size() > 0) {
                                DownLoadInfo waitInfo = mWaittingInfos.get(0);
                                if (waitInfo != null) {
                                    AppInfo appInfo = mAppInfos.get(0);
                                    mWaittingInfos.remove(0);
                                    mAppInfos.remove(0);
                                    waitInfo.setStatus(DOWN_LOADING);
                                    isDownloading = true;
                                    mDownLoadInfo = waitInfo;
                                    Log.e(TAG, "handleMessage: download before");
                                    downloadAPP(appInfo);
                                    Log.e(TAG, "handleMessage: download after");
                                }
                            }
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    /**
     * 是否有APP正在下载
     *
     * @return
     */
    public boolean isAPPLoading() {
        try {
            DownLoadInfo downLoadingInfo = mStoreAPP.dbHelper.findFirst(Selector.from(DownLoadInfo.class).where("status", "=", DOWN_LOADING));
            DownLoadInfo downPausedInfo = mStoreAPP.dbHelper.findFirst(Selector.from(DownLoadInfo.class).where("status", "=", DOWN_PAUSE));
            if (downLoadingInfo != null || downPausedInfo != null) {
                if (downLoadingInfo != null) {
                    Log.i(TAG, "isAPPLoading: " + downLoadingInfo.toString());
                }
                if (downPausedInfo != null) {
                    Log.i(TAG, "isAPPLoading: " + downPausedInfo.toString());
                }
                return true;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取正在等待下载的APP数
     *
     * @return
     */
    public int getDownWaitting() {
        try {
            List<DownLoadInfo> waittingInfos = mStoreAPP.dbHelper.findAll(Selector.from(DownLoadInfo.class).where("status", "=", DOWN_WAITTING));
            if (waittingInfos != null && waittingInfos.size() > 0) {
                return waittingInfos.size();
            } else {
                return 0;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public AppInfo getAppInfo() {
        return mAppInfo;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.mAppInfo = appInfo;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public int getCurrentDownApp() {
        return mCurrentDownApp;
    }

    public void setCurrentDownApp(int currentDownApp) {
        this.mCurrentDownApp = currentDownApp;
    }

    public DownLoadInfo getDownLoadInfo() {
        return mDownLoadInfo;
    }

    public void setDownLoadInfo(DownLoadInfo downLoadInfo) {
        this.mDownLoadInfo = downLoadInfo;
        try {
            mStoreAPP.dbHelper.saveOrUpdate(mDownLoadInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public List<DownLoadInfo> getmWaittingInfos() {
        return mWaittingInfos;
    }

    public void setmWaittingInfos(List<DownLoadInfo> mWaittingInfos) {
        this.mWaittingInfos = mWaittingInfos;
    }
}
