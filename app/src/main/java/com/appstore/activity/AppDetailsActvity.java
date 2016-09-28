package com.appstore.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appstore.R;
import com.appstore.StoreApplication;
import com.appstore.adapter.SafeAdapter;
import com.appstore.dbutils.DBHelper;
import com.appstore.entity.AppInfo;
import com.appstore.entity.DownLoadInfo;
import com.appstore.entity.Safe;
import com.appstore.utils.DataUtils;
import com.appstore.utils.ImgUtils;
import com.appstore.widget.NoScrollListView;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.appstore.R.id.details_collect;

/**
 * Created by 张艳琴 on 2016/9/19.
 */
public class AppDetailsActvity extends BaseActivity implements View.OnClickListener {
    NoScrollListView lv;
    ImageView Ivback;
    ImageView Ivicon;
    TextView Tvname;
    RatingBar Rbstars;
    TextView Tvdownloadcount;
    TextView Tvversion;
    TextView Tvdate;
    TextView Tvsize;
    ImageView[] Ivsafe = new ImageView[3];
    ImageView Ivdown;
    ImageView Ivdown1;
    TextView Tvdes;
    TextView Tvaurth;
    Button Btcollect;
    Button Btshare;
    AppInfo appinfo;
    private ViewPager viewPager;
    private ImageView[] mImageViews;
    private ProgressBar mProgressbar;
    private TextView mTvDownload;

    SafeAdapter safeAdapter;
    List<Safe> safeList = null;
    DownLoadInfo mDownloadInfo;

    private static final String TAG = "hello";

    private static final int UPDATE_INFO = 1234;

    private int mCurrentPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);
        initView();
    }

    private void initView() {
        Ivback = (ImageView) findViewById(R.id.back);
        Ivicon = (ImageView) findViewById(R.id.details_icon);
        Tvname = (TextView) findViewById(R.id.details_name);
        Rbstars = (RatingBar) findViewById(R.id.details_rate);
        Tvdownloadcount = (TextView) findViewById(R.id.details_downloadNum);
        Tvversion = (TextView) findViewById(R.id.details_version);
        Tvdate = (TextView) findViewById(R.id.details_date);
        Tvsize = (TextView) findViewById(R.id.details_size);
        Ivsafe[0] = (ImageView) findViewById(R.id.details_safe0);
        Ivsafe[1] = (ImageView) findViewById(R.id.details_safe1);
        Ivsafe[2] = (ImageView) findViewById(R.id.details_safe2);
        Ivdown = (ImageView) findViewById(R.id.detals_down);
        Ivdown1 = (ImageView) findViewById(R.id.detals_down1);
        Tvdes = (TextView) findViewById(R.id.details_des);
        Tvaurth = (TextView) findViewById(R.id.details_aurth);
        Btcollect = (Button) findViewById(details_collect);
        Btshare = (Button) findViewById(R.id.details_share);
        lv = (NoScrollListView) findViewById(R.id.safe_listview);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        mProgressbar = (ProgressBar) findViewById(R.id.progressBar);
        mTvDownload = (TextView) findViewById(R.id.tv_progress);

        mProgressbar.setMax(100);
        viewPager.setPageMargin(20);
        viewPager.setVisibility(View.GONE);
        appinfo = new AppInfo();
        initData();
        Ivdown1.setTag("off");
        Ivdown.setTag("off");
        mTvDownload.setOnClickListener(this);
        Ivdown.setOnClickListener(this);
        Ivdown1.setOnClickListener(this);
        Ivback.setOnClickListener(this);
        Btcollect.setOnClickListener(this);
        Btshare.setOnClickListener(this);

        //Log.e(TAG, "initView: update update download UI");

        //更新下载UI
        String packagename = getIntent().getStringExtra("comname");
        //从DownLoad表中搜索是否有这个下载对象
        try {
            mDownloadInfo = DBHelper.getInstance(mApp).findFirst(Selector.from(DownLoadInfo.class).where("packagename", "=", packagename));
            if (mDownloadInfo == null) {
                mTvDownload.setText("下载");
            } else {
                if (mDownloadInfo.getStatus() == DownloadService.DOWN_UNLOAD) {
                    mTvDownload.setText("下载");
                } else if (mDownloadInfo.getStatus() == DownloadService.DOWN_PAUSE) {
                    mTvDownload.setText("继续下载");
                    mProgressbar.setProgress(mDownloadInfo.getPos());
                } else if (mDownloadInfo.getStatus() == DownloadService.DOWN_WAITTING) {
                    mTvDownload.setText("等待下载");
                } else if (mDownloadInfo.getStatus() == DownloadService.DOWN_FINISHED) {
                    mTvDownload.setText("下载完成");
                    //mProgressbar.setBackgroundColor(getResources().getColor(R.color.color_main));
                }


                //        if (mDownloadInfo != null && mDownloadInfo.getStatus() == DownloadService.DOWN_LOADING) {
//            mService.isDownloading = true;
//            mProgressbar.setProgress(mDownloadInfo.getPos());
//            mTvDownload.setText(mDownloadInfo.getPos() + "%");
//            mService.downloadAPP(appinfo);
//        }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

       // Log.e(TAG, "oncreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindDownloadService();
        //Log.e(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mService.mDownLoadInfo != null) {
                mApp.dbHelper.saveOrUpdate(mService.mDownLoadInfo);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        unbindDownloadService();
        //Log.e(TAG, "onPause: ");
    }

    @Override
    public void publish(int progress) {

        if(mDownloadInfo!=null){
            Log.i(TAG, "publish: mdownloadInfo==="+mDownloadInfo.getAppId());
        }

        if(mService.mDownLoadInfo!=null){
            Log.i(TAG, "publish: service mdownloadInfo==="+mService.mDownLoadInfo.getAppId());
        }

        if (mDownloadInfo != null && mDownloadInfo.getAppId().equals(mService.mDownLoadInfo.getAppId())) {
            Log.i(TAG, "publish: progress=" + progress);
            mProgressbar.setProgress(progress);
            mCurrentPos = progress;
            Message msg = Message.obtain();
            msg.what = UPDATE_INFO;
            msg.arg1 = progress;
            mHandler.sendMessageDelayed(msg, 200);
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE_INFO) {
                int progress = msg.arg1;

                switch (mDownloadInfo.getStatus()) {
                    //APP未被下载
                    case DownloadService.DOWN_UNLOAD:
                        mTvDownload.setText("下载");
                        break;
                    //APP正在下载
                    case DownloadService.DOWN_LOADING:
                        mTvDownload.setText(progress + "%");
                        if (progress == 100) {
                            mTvDownload.setText("下载完成");
                            mDownloadInfo.setStatus(DownloadService.DOWN_FINISHED);
                        }
                        break;
                    //APP暂停下载
                    case DownloadService.DOWN_PAUSE:
                        mTvDownload.setText("继续下载");
                        break;
                    //APP下载完成
                    case DownloadService.DOWN_FINISHED:
                        mTvDownload.setText("下载完成");
                        break;
                }
            }
        }
    };

    @Override
    public void change(DownLoadInfo downLoadInfo) {
//        Log.i(TAG, "change: ");
//        if(downLoadInfo!=null){
//            Log.e(TAG, "change: downLoadInfo==="+downLoadInfo.toString());
//        }
//        if(mDownloadInfo!=null){
//            Log.e(TAG, "change: mDownloadInfo==="+mDownloadInfo.toString());
//        }
        Log.e(TAG, "change: ");
        if (mDownloadInfo != null && mDownloadInfo.getStatus() == DownloadService.DOWN_LOADING) {
            mProgressbar.setProgress(mDownloadInfo.getPos());
            mTvDownload.setText(mDownloadInfo.getPos() + "%");
            if (mService.getAppInfo() != null) {
                Log.e(TAG, "change更新ui:" + mService.getAppInfo().toString());
                mDownloadInfo.setStatus(DownloadService.DOWN_LOADING);
                mService.isDownloading = true;
                mService.downloadAPP(mService.getAppInfo());
                Log.e(TAG, "change: "+mService.mDownloadUpdateListener.toString());
            }
        }
    }

    private void initData() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("packageName", getIntent().getStringExtra("comname"));
        String url = getResources().getString(R.string.ip_address) + "detail";
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int ins, String s) {
                try {
                    safeList = new ArrayList<Safe>();
                    JSONObject jsonObject;
                    jsonObject = new JSONObject(s);
                    appinfo.setId(jsonObject.getInt("id"));
                    appinfo.setName(jsonObject.getString("name"));
                    appinfo.setPackageName(jsonObject.getString("packageName"));
                    appinfo.setIconUrl(jsonObject.getString("iconUrl"));
                    appinfo.setStars(jsonObject.getDouble("stars") + "");
                    appinfo.setDownLoadNum(jsonObject.getString("downloadNum"));
                    appinfo.setVesion(jsonObject.getString("version"));
                    appinfo.setDate(jsonObject.getString("date"));
                    appinfo.setSize(jsonObject.getString("size"));
                    appinfo.setDownloadUrl(jsonObject.getString("downloadUrl"));
                    appinfo.setDes(jsonObject.getString("des"));
                    appinfo.setAuthor(jsonObject.getString("author"));
                    appinfo.setScreen(jsonObject.getString("screen"));
                    JSONObject root = null;
                    root = new JSONObject(s.toString());
                    JSONArray arry = root.getJSONArray("safe");
                    for (int i = 0; i < arry.length(); i++) {
                        Safe safe = new Safe();
                        safe.setAppid(jsonObject.getString("packageName"));
                        JSONObject lo = arry.getJSONObject(i);
                        safe.setSafeUrl(lo.getString("safeUrl"));
                        safe.setSafeDesUrl(lo.getString("safeDesUrl"));
                        safe.setSafeDes(lo.getString("safeDes"));
                        safe.setSafeDesColor(lo.getString("safeDesColor"));
                        safeList.add(safe);
                    }
                    Message msg = Message.obtain();
                    msg.what = 1;
                    handler.sendEmptyMessage(1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                Toast.makeText(AppDetailsActvity.this, "网络连接失败", Toast.LENGTH_LONG).show();
                mDownloadInfo = null;
                super.onFailure(throwable, s);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                break;
            //下载按钮
            case R.id.tv_progress:
                if (mDownloadInfo == null) {
                    int num = mService.getDownWaitting();
                    if (num == 4) {
                        Toast.makeText(AppDetailsActvity.this, "已有5个应用在下载队列,请等待", Toast.LENGTH_SHORT).show();
                    } else {
                        mDownloadInfo = DataUtils.convertAppInfoToDownloadInfo(appinfo, DownloadService.DOWN_UNLOAD);
                        if (mService.isAPPLoading()) {
                            try {
                                mDownloadInfo.setStatus(DownloadService.DOWN_WAITTING);
                                mService.mWaittingInfos.add(mDownloadInfo);
                                mService.mAppInfos.add(appinfo);
                                mApp.dbHelper.saveOrUpdate(mDownloadInfo);
                                mTvDownload.setText("等待下载..");
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        } else {
                            mDownloadInfo.setStatus(DownloadService.DOWN_LOADING);
                            mService.setDownLoadInfo(mDownloadInfo);
                            mService.setAppInfo(appinfo);
                            mService.isDownloading = true;
                            mService.downloadAPP(appinfo);
                        }
                    }
                } else {
                    switch (mDownloadInfo.getStatus()) {
                        //APP未被下载
                        case DownloadService.DOWN_UNLOAD:
                            mDownloadInfo.setStatus(DownloadService.DOWN_LOADING);
                            mService.setDownLoadInfo(mDownloadInfo);
                            mService.setAppInfo(appinfo);
                            mService.isDownloading = true;
                            mService.downloadAPP(appinfo);
                            break;
                        //APP正在下载
                        case DownloadService.DOWN_LOADING:
                            mDownloadInfo.setStatus(DownloadService.DOWN_PAUSE);
                            mService.setDownLoadInfo(mDownloadInfo);
                            mService.isDownloading = false;
                            mService.downloadAPP(appinfo);
                            break;
                        //APP等待下载
                        case DownloadService.DOWN_WAITTING:
                            Toast.makeText(AppDetailsActvity.this, "等待下载", Toast.LENGTH_SHORT).show();
                            break;
                        //APP暂停下载
                        case DownloadService.DOWN_PAUSE:
                            mDownloadInfo.setStatus(DownloadService.DOWN_LOADING);
                            mService.setDownLoadInfo(mDownloadInfo);
                            mService.isDownloading = true;
                            mService.downloadAPP(appinfo);
                            break;
                        //APP下载完成
                        case DownloadService.DOWN_FINISHED:
                            mService.isDownloading = false;
                            Toast.makeText(AppDetailsActvity.this, "APP已下载完成", Toast.LENGTH_SHORT).show();
                            mTvDownload.setText("下载完成");
                            break;
                    }
                }
                break;
            case R.id.detals_down:      //展开显示listView
                if (Ivdown.getTag().equals("off")) {
                    Ivdown.setImageResource(R.mipmap.arrow_up);
                    lv.setVisibility(View.VISIBLE);
                    Ivdown.setTag("on");
                } else {
                    Ivdown.setImageResource(R.mipmap.arrow_down);
                    lv.setVisibility(View.GONE);
                    Ivdown.setTag("off");
                }
                break;
            case R.id.detals_down1://展开详细描述
                if (Ivdown1.getTag().equals("off")) {
                    Ivdown1.setImageResource(R.mipmap.arrow_up);
                    Tvdes.setMaxLines(500);
                    Ivdown1.setTag("on");
                } else {
                    Ivdown1.setImageResource(R.mipmap.arrow_down);
                    Tvdes.setMaxLines(5);
                    Ivdown1.setTag("off");
                }
                break;
            case R.id.details_collect:
                Toast.makeText(this, "收藏成功！", Toast.LENGTH_LONG).show();
                break;
            case R.id.details_share:
                Toast.makeText(this, "分享成功！", Toast.LENGTH_LONG).show();
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (appinfo != null) {
                    Tvname.setText(appinfo.getName());
                    Rbstars.setRating(Float.parseFloat(appinfo.getStars()));
                    Tvdownloadcount.setText("下载量:" + appinfo.getDownLoadNum());
                    Tvversion.setText("版本:" + appinfo.getVesion());
                    Tvdate.setText("时间:" + appinfo.getDate());
                    Tvsize.setText("大小：" + new java.text.DecimalFormat("#.00").
                            format((Double.parseDouble(appinfo.getSize()) / (1024 * 1024))) + "MB");
                    Tvdes.setText(appinfo.getDes());
                    Tvaurth.setText(appinfo.getAuthor());
                    ImgUtils.setInterImg1(StoreApplication.IP_ADDRESS + "image?name=" + appinfo.getIconUrl(), Ivicon, R.mipmap.safedesurl0);
                    //将图片装载到数组中

                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(appinfo.getScreen());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mImageViews = new ImageView[jsonArray.length()];
                    for (int i = 0; i < mImageViews.length; i++) {
                        ImageView imageView = new ImageView(AppDetailsActvity.this);
                        mImageViews[i] = imageView;
                        ImgUtils.setInterImg1(StoreApplication.IP_ADDRESS + "image?name=" + jsonArray.optString(i), imageView, R.mipmap.screen0);
                    }
                    viewPager.setAdapter(new ScreenAdapter());
                    viewPager.setCurrentItem((mImageViews.length) * 100);
                    viewPager.setVisibility(View.VISIBLE);
                    findViewById(R.id.ly).setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return viewPager.dispatchTouchEvent(event);
                        }
                    });

                    if (safeList != null) {
                        switch (safeList.size()) {
                            case 0:
                                Ivsafe[0].setVisibility(View.GONE);
                                Ivsafe[1].setVisibility(View.GONE);
                                Ivsafe[2].setVisibility(View.GONE);
                                break;
                            case 1:
                                ImgUtils.setInterImg1(StoreApplication.IP_ADDRESS + "image?name=" + safeList.get(0).getSafeUrl(),
                                        Ivsafe[0], R.mipmap.safeicon0);
                                Ivsafe[0].setVisibility(View.VISIBLE);
                                Ivsafe[1].setVisibility(View.GONE);
                                Ivsafe[2].setVisibility(View.GONE);
                                break;
                            case 2:
                                ImgUtils.setInterImg1(StoreApplication.IP_ADDRESS + "image?name=" + safeList.get(0).getSafeUrl(),
                                        Ivsafe[0], R.mipmap.safeicon0);
                                ImgUtils.setInterImg1(StoreApplication.IP_ADDRESS + "image?name=" + safeList.get(1).getSafeUrl(),
                                        Ivsafe[1], R.mipmap.safeicon1);
                                Ivsafe[0].setVisibility(View.VISIBLE);
                                Ivsafe[1].setVisibility(View.VISIBLE);
                                Ivsafe[2].setVisibility(View.GONE);
                                break;
                            case 3:
                                ImgUtils.setInterImg1(StoreApplication.IP_ADDRESS + "image?name=" + safeList.get(0).getSafeUrl(),
                                        Ivsafe[0], R.mipmap.safeicon0);
                                ImgUtils.setInterImg1(StoreApplication.IP_ADDRESS + "image?name=" + safeList.get(1).getSafeUrl(),
                                        Ivsafe[1], R.mipmap.safeicon1);
                                ImgUtils.setInterImg1(StoreApplication.IP_ADDRESS + "image?name=" + safeList.get(2).getSafeUrl(),
                                        Ivsafe[2], R.mipmap.safeicon2);
                                Ivsafe[0].setVisibility(View.VISIBLE);
                                Ivsafe[1].setVisibility(View.VISIBLE);
                                Ivsafe[2].setVisibility(View.VISIBLE);
                                break;
                        }
                        safeAdapter = new SafeAdapter(getApplicationContext(), safeList);
                        lv.setAdapter(safeAdapter);
                        lv.setVisibility(View.GONE);
                    }
                }
            }
        }
    };


    public class ScreenAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(View container, int position) {
            try {
                ImageView views = mImageViews[position % mImageViews.length];
                ((ViewPager) container).addView(views, 0);
            } catch (Exception e) {
            }
            return mImageViews[position % mImageViews.length];
        }

    }
}