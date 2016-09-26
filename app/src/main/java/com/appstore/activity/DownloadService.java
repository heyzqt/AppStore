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

import com.appstore.StoreApplication;
import com.appstore.dbutils.DBHelper;
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
    public List<DownLoadInfo> mDownLoadInfos = new ArrayList<>();

    /**
     * 当前正在下载的对象
     */
    public DownLoadInfo mDownLoadInfo;

    /**
     * 当前下载的进度
     */
    private int mCurrentPos;

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

    private static final String TAG = "hello";

    /**
     * Single线程池
     */
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private StoreApplication mStoreAPP;

    public static final int DOWN_UNLOAD = 0;      //未下载
    public static final int DOWN_LOADING = 0x2;     //正在下载
    public static final int DOWN_PAUSE = 0x3;       //暂停下载
    public static final int DOWN_FINISHED = 0x4;    //完成下载
    public static final int DOWN_WAITTING = 0x5;    //等待下载
    public static final int DOWN_FAILURE = 0x6;     //下载失败
    public static final int DOWN_STOPED = 0x7;      //停止下载

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

    DownloadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mStoreAPP = (StoreApplication) getApplication();
        mPath = Environment.getExternalStorageDirectory().getPath() + "/AppStore/";
        //获取当前下载列表
        try {
            mDownLoadInfos = DBHelper.getInstance(mStoreAPP).findAll(DownLoadInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            mDownLoadInfos = null;
        }
    }

    //更新下载状态接口
    public interface DownloadUpdateListener {
        public void onPublish(int progress);    //更新进度条位置

        public void onChange(AppInfo appInfo);         //更新下载按钮状态
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
        if (isDownloading) {
            isDownloading = false;
        } else {
            isDownloading = true;
        }

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
        MyRunnable runnable = new MyRunnable(url, pos, savePathAndFile);
        mExecutor.execute(runnable);
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
                        mDownLoadInfo.setStatus(DOWN_PAUSE);
                        return;
                    }
                    inFile.write(b, 0, read);
                    Message msg = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putLong("current", inFile.length());
                    bundle.putLong("total", contentLength);
                    msg.what = 1;
                    msg.setData(bundle);
                    mHandler.sendMessageDelayed(msg, 200);
                }
                conn.disconnect();
                mDownLoadInfo.setStatus(DOWN_FINISHED);
                mStoreAPP.dbHelper.saveOrUpdate(mDownLoadInfo);
            } catch (IOException e) {
                conn.disconnect();
                mDownLoadInfo.setStatus(DOWN_FINISHED);
                try {
                    mStoreAPP.dbHelper.saveOrUpdate(mDownLoadInfo);
                } catch (DbException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Bundle bundle = msg.getData();
                long current = bundle.getLong("current");
                long total = bundle.getLong("total");
                String result = new DecimalFormat("0.00").format((double) current / total);
                double d = Double.parseDouble(result);
                int i = (int) (d * 100);
                mDownloadUpdateListener.onPublish(i);
            }
        }
    };

    /**
     * 获取正在等待下载的APP数
     *
     * @return
     */
    public int getDownWaitting() {
        int result = 0;
        try {
            List<DownLoadInfo> waittingInfos = mStoreAPP.dbHelper.findAll(Selector.from(DownLoadInfo.class).where("status", "=", DOWN_WAITTING));
            if (waittingInfos != null && waittingInfos.size() > 0) {
                return waittingInfos.size();
            } else {
                return result;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getCurrentPos() {
        return mCurrentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.mCurrentPos = currentPos;
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
}
