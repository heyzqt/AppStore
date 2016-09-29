package com.appstore.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.appstore.R;
import com.appstore.activity.AppDetailsActvity;
import com.appstore.activity.MainActivity;
import com.appstore.adapter.ImgLoaders;
import com.appstore.adapter.ListViewAdapter;
import com.appstore.entity.AppInfo;
import com.appstore.entity.DownLoadInfo;
import com.appstore.widget.XListView;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 张艳琴 on 2016/9/19.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemClickListener, XListView.IXListViewListener {

    private ListViewAdapter adapter;
    private XListView mListView;
    public Context context;
    private ImageLoader mImageLoader;
    ArrayList<HashMap<String, Object>> listdata = new ArrayList<HashMap<String, Object>>();

    ArrayList<HashMap<String, Object>> listurl = new ArrayList<HashMap<String, Object>>();
    private ProgressBar mf_pb;
    private int index = 0;

    private RollPagerView rollpager;
    private Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String j = bundle.getString("Json");
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(j);
                if (index == 0) {

                    JSONArray jsonArray1 = jsonObject.getJSONArray("picture");
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("pagerimageurl", jsonArray1.get(i).toString());
                        listurl.add(map);
                    }
                }

                JSONArray jsonArray = jsonObject.getJSONArray("list");
                for (int i = 0; i < jsonArray.length(); i++) {
                    HashMap<String, Object> map = new HashMap<String, Object>();

                    JSONObject object = jsonArray.getJSONObject(i);

                    map.put("id", object.get("id").toString());
                    map.put("name", object.get("name").toString());
                    //获取包名
                    map.put("packagename", object.get("packageName").toString());
                    map.put("iconUrl", object.get("iconUrl"));
                    map.put("stars", object.get("stars").toString());
                    map.put("size", object.get("size").toString());
                    map.put("downloadUrl", object.get("downloadUrl"));
                    map.put("intro", object.get("des"));
                    listdata.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (index == 0) {
                mListView.setVisibility(View.VISIBLE);
                mf_pb.setVisibility(View.GONE);
                adapter = new ListViewAdapter(getActivity(), listdata);
                mListView.setAdapter(adapter);
                setPagerView();
            } else {
                adapter.notifyDataSetChanged();
                onLoad();
            }
        }

    };

    private MainActivity mainActivity;
    private DownLoadInfo mDownloadInfo;
    private List<DownLoadInfo> mWaittings;
    private AppInfo mAppInfo = null;
    boolean flag = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_main, null);
        initView(contentView);

        new Thread(new loadHttpData()).start();
        return contentView;
    }

    public void initView(View v) {
        this.context = getActivity();
        mListView = (XListView) v.findViewById(R.id.mf_mainlistview);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mf_pb = (ProgressBar) v.findViewById(R.id.mf_progressbar);
        rollpager = (RollPagerView) v.findViewById(R.id.viewpager);
        mListView.setVisibility(View.GONE);
        mf_pb.setVisibility(View.VISIBLE);
        mListView.setOnItemClickListener(this);
    }


    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime("刚刚");
    }

    //设置轮播
    public void setPagerView() {

        //设置播放时间间隔
        rollpager.setPlayDelay(2000);
        //设置透明度
        rollpager.setAnimationDurtion(500);
        //设置适配器
        //rollpager.setAdapter(new TestNormalAdapter(context));

        new Thread(new A()).start();
        rollpager.setHintView(new ColorPointHintView(getActivity(), Color.YELLOW, Color.WHITE));

    }

    //刷新
    @Override
    public void onRefresh() {
        Toast.makeText(getActivity(), "已经是最新的了", Toast.LENGTH_SHORT).show();
        onLoad();

    }

    //加载更多
    @Override
    public void onLoadMore() {
        index = index + 1;
        new Thread(new loadHttpData()).start();
    }


    private class A implements Runnable {

        @Override
        public void run() {
            try {
                rollpager.setAdapter(new TestNormalAdapter(context));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class TestNormalAdapter extends StaticPagerAdapter {

        public Context t_context;
        public ImgLoaders loader;
        private Bitmap bit[] = new Bitmap[listurl.size()];

        public TestNormalAdapter(Context context) {

            t_context = context;
            loader = new ImgLoaders();
            loader.initImageLoader(t_context);
            mImageLoader = ImageLoader.getInstance();


            for (int i = 0; i < listurl.size(); i++) {

                bit[i] = mImageLoader.loadImageSync("http://localhost:8090/image?name=" + listurl.get(i).get("pagerimageurl"), loader.getOption());

            }
        }

        @Override
        public View getView(ViewGroup container, int position) {

            ImageView view = new ImageView(container.getContext());
            view.setImageBitmap(bit[position]);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }

        @Override
        public int getCount() {
            return listurl.size();
        }
    }


    public ArrayList<HashMap<String, Object>> getListData() {
        return listdata;
    }

    //加载网络数据
    private class loadHttpData implements Runnable {

        @Override
        public void run() {
            Looper.prepare();
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("index", String.valueOf(index));
            client.get("http://localhost:8090/home", params, new request());

            Looper.loop();
        }
    }

    class request extends JsonHttpResponseHandler {


        @Override
        public void onSuccess(JSONObject jsonObject) {
            super.onSuccess(jsonObject);
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt("changcode", 1);
            bundle.putString("Json", jsonObject.toString());
            msg.setData(bundle);
            handle.sendMessage(msg);
        }

        @Override
        public void onFailure(Throwable throwable, JSONArray jsonArray) {
            super.onFailure(throwable, jsonArray);
        }
    }

    //跳转到详情界面
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), AppDetailsActvity.class);
        Bundle bundle = new Bundle();
        bundle.putString("comname", listdata.get(position - 1).get("packagename").toString());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        // TODO Auto-generated method stub
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.bindDownloadService();
    }

    @Override
    public void onPause() {
        super.onResume();
        mainActivity.unbindDownloadService();
    }

    //更新进度条UI
    public void publishUI(int progress) {

        if (flag) {
            adapter.setProgress(progress);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 更新在下载表中的对象UI
     */
    public void changeUI() {

        //当前正在下载数据是否在当前列表中
        if (mainActivity.mService.mDownLoadInfo != null) {
            String packagename = mainActivity.mService.getPackageName();
            mAppInfo = new AppInfo();
            //listdata中找是否这个对象
            for (int i = 0; i < listdata.size(); i++) {
                if (listdata.get(i).get("packagename").equals(packagename)) {
                    flag = true;
                    Map<String, Object> map = new HashMap<String, Object>();
                    map = listdata.get(i);
                    mAppInfo.setId((Integer) map.get("id"));
                    mAppInfo.setName((String) map.get("name"));
                    mAppInfo.setPackageName((String) map.get("packagename"));
                    mAppInfo.setIconUrl((String) map.get("iconUrl"));
                    mAppInfo.setDownloadUrl((String) map.get("downloadUrl"));
                    mAppInfo.setSize((String) map.get("size"));
                    mAppInfo.setStars((String) map.get("stars"));
                    mAppInfo.setDate((String) map.get("intro"));
                    break;
                }
            }
        }

        if (flag) {
            adapter.setDownloadInfo(mainActivity.mService.mDownLoadInfo);
            adapter.setService(mainActivity.mService);
            adapter.notifyDataSetChanged();
        }
    }
}