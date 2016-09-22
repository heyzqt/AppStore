package com.appstore.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appstore.R;
import com.appstore.StoreApplication;
import com.appstore.adapter.SafeAdapter;
import com.appstore.adapter.SubjectAdapter;
import com.appstore.entity.AppInfo;
import com.appstore.entity.Safe;
import com.appstore.entity.Subject;
import com.appstore.utils.ImgUtils;
import com.appstore.widget.NoScrollListView;
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
public class AppDetailsActvity extends AppCompatActivity implements View.OnClickListener {
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
    Button Btdownload;
    Button Btshare;
    AppInfo appinfo;

    SafeAdapter safeAdapter;
    List<Safe> safeList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);
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
        Btdownload = (Button) findViewById(R.id.details_download);
        Btshare = (Button) findViewById(R.id.details_share);
        lv = (NoScrollListView) findViewById(R.id.safe_listview);
        appinfo = new AppInfo();
        initData();
        Ivdown1.setTag("off");
        Ivdown.setTag("off");
        Ivdown.setOnClickListener(this);
        Ivdown1.setOnClickListener(this);
        Ivback.setOnClickListener(this);
        Btcollect.setOnClickListener(this);
        Btshare.setOnClickListener(this);
        Btdownload.setOnClickListener(this);


    }

    private void initData() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("packageName", "com.mogujie");
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
                    Log.e("zyq---", s);
                } catch (JSONException e) {
                    Log.e("zyq---Jsonjiaxi", e + "");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                Log.e("zyq---网络连接失败", s);
                Toast.makeText(AppDetailsActvity.this, "网络连接失败", Toast.LENGTH_LONG).show();
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
            case R.id.detals_down:      //展开显示listView
                if (Ivdown.getTag().equals("off")) {
                    Ivdown.setImageResource(R.mipmap.arrow_up);
                    lv.setVisibility(View.VISIBLE);
                    Ivdown.setTag("on");
                }else{
                    Ivdown.setImageResource(R.mipmap.arrow_down);
                    lv.setVisibility(View.GONE);
                    Ivdown.setTag("off");
                }
                break;
            case R.id.detals_down1://展开详细描述
                if (Ivdown1.getTag().equals("off")) {
                    Ivdown1.setImageResource(R.mipmap.arrow_up);
                    Tvdes.setMaxLines(50);
                    Ivdown1.setTag("on");
                }else{
                    Ivdown1.setImageResource(R.mipmap.arrow_down);
                    Tvdes.setMaxLines(5);
                    Ivdown1.setTag("off");
                }
                break;

            case R.id.details_collect:
                Toast.makeText(this, "收藏成功！", Toast.LENGTH_LONG).show();
                break;
            case R.id.details_download:
                //开始下载

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
                    Tvsize.setText("大小：" + new java.text.DecimalFormat("#.00").format((Double.parseDouble(appinfo.getSize()) / (1024 * 1024))) + "MB");
                    Tvdes.setText(appinfo.getDes());
                    Tvaurth.setText(appinfo.getAuthor());
                    ImgUtils.setInterImg(StoreApplication.IP_ADDRESS + "image?name=" + appinfo.getIconUrl(), Ivicon);
                    if (safeList != null) {
                        switch (safeList.size()) {
                            case 0:
                                Ivsafe[0].setVisibility(View.GONE);
                                Ivsafe[1].setVisibility(View.GONE);
                                Ivsafe[2].setVisibility(View.GONE);
                                break;
                            case 1:
                                Ivsafe[0].setVisibility(View.VISIBLE);
                                Ivsafe[1].setVisibility(View.GONE);
                                Ivsafe[2].setVisibility(View.GONE);
                                break;
                            case 2:
                                Ivsafe[0].setVisibility(View.VISIBLE);
                                Ivsafe[1].setVisibility(View.VISIBLE);
                                Ivsafe[2].setVisibility(View.GONE);
                                break;
                            case 3:
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
}