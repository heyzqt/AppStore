package com.appstore.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appstore.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * Created by heyzqt on 2016/9/19.
 */
public class CollectActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar mProgressbar;

    private TextView mTextView;

    private Button mDownBtn;

    private Button mPauseBtn;

    private Button mStartBtn;

    private String path;
    //private String url0 = "http://gdown.baidu.com/data/wisegame/cc57d8778b42e558/shoujitaobao_141.apk";
    private String url0 = "http://localhost:8090/download?name=app/com.youyuan.yyhl/com.youyuan.yyhl.apk";
    //private String url2 = "/com.youyuan.yyhl/com.youyuan.yyhl.apk&&range=";
    private String url3 = "http://localhost:8090/download?name=app/com.youyuan.yyhl/com.youyuan.yyhl.apk&&range=";
    private String url_second = "http://localhost:8090/download?name=app/cn.goapk.market/cn.goapk.market.apk&&range=";
    private boolean isDownloading = false;
    private HttpHandler<File> handler;
    private long mRange = 0;
    private static final int DOWN_START = 0;
    private static final int DOWN_LOADING = 1;
    private static final int DOWN_FINISHED = 3;
    private static int mDownStatus = DOWN_START;

    private static final String TAG = "hello";
    HttpUtils httpUtils;
    EditText text;

    HttpURLConnection mConn = null;
    private String filename = "youyuan.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        initView();
    }

    private void initView() {
        mProgressbar = (ProgressBar) findViewById(R.id.progressBar);
        mTextView = (TextView) findViewById(R.id.tv_progress);
        mDownBtn = (Button) findViewById(R.id.button_down);
        mPauseBtn = (Button) findViewById(R.id.button_pause);
        mStartBtn = (Button) findViewById(R.id.button_start);

        isDownloading = false;

        mDownBtn.setOnClickListener(this);
        mPauseBtn.setOnClickListener(this);
        mStartBtn.setOnClickListener(this);
        path = Environment.getExternalStorageDirectory().getPath() + "/AppStore/";

        text = (EditText) findViewById(R.id.tv_mytext);
        mProgressbar.setMax(100);
        httpUtils = new HttpUtils();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_down:
                if (isDownloading) {
                    isDownloading = false;
                    mDownBtn.setText("开始");
                } else {
                    isDownloading = true;
                    mDownBtn.setText("暂停");
                }
                final File file = new File(path, filename);
                int i = 0;
                if (file.exists()) {
                    final long filesize = file.length();
                    Log.i(TAG, "已有文件大小为:" + filesize);
                    if (filesize > 0) {
                        Log.i(TAG, "文件续传");
                        downloadAPP(url_second + filesize, filesize, path + filename);
                    } else {
                        Log.i(TAG, "文件大小为0");
                        downloadAPP(url_second, 0, path + filename);
                    }
                } else {
                    try {
                        Log.i(TAG, "文件不存在，下载");
                        file.createNewFile();
                        downloadAPP(url_second, 0, path + filename);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void downloadAPP(final String url, final long pos, final String savePathAndFile) {

        MyRunnable runnable = new MyRunnable("zqt", url, pos, savePathAndFile);
        new Thread(runnable).start();
    }

    class MyRunnable implements Runnable {

        String url;
        long pos;
        String savePathAndFile;
        String name;

        public MyRunnable(String name, final String url, final long pos, final String savePathAndFile) {
            this.name = name;
            this.url = url;
            this.pos = pos;
            this.savePathAndFile = savePathAndFile;
        }

        @Override
        public void run() {
            Log.i(TAG, "当前线程: " + name);
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                int contentLength = conn.getContentLength();
                InputStream input = conn.getInputStream();
                RandomAccessFile inFile = new RandomAccessFile(savePathAndFile, "rw");
                //定位到pos位置
                inFile.seek(pos);
                Log.i(TAG, "pos===" + pos);
                int read;
                byte[] b = new byte[1024];
                //从输入流中读出字节流,再写入文件
                while ((read = input.read(b, 0, 1024)) > 0) {
                    if (!isDownloading)
                        return;
                    inFile.write(b, 0, read);
                    Log.i(TAG, "正在下载 文件大小 read:" + read);
                    Log.i(TAG, "正在下载 文件大小 filesize:" + inFile.length());
                    Message msg = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putLong("current", inFile.length());
                    bundle.putLong("total", contentLength);
                    msg.what = 1;
                    msg.setData(bundle);
                    mHandler.sendMessageDelayed(msg,200);
                }
                conn.disconnect();
                Log.i(TAG, "downloadAPP: 下载完成");
            } catch (IOException e) {
                conn.disconnect();
                Log.i(TAG, "downloadAPP: 下载error");
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
                mTextView.setText(i + "%");
                mProgressbar.setProgress(i);
            }
        }
    };
}