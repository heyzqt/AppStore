package com.appstore.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.appstore.R;
import com.appstore.StoreApplication;
import com.appstore.adapter.ListViewAdapter;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by 张艳琴 on 2016/9/19.
 */
public class MainFragment extends Fragment {

    private ListView mfg_listview;

    ArrayList<HashMap<String,Object>> listdata=new ArrayList<HashMap<String,Object>>();

    private ProgressBar mf_pb;
    private int index=1;

    private RollPagerView rollpager;
    private Handler handle=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle=msg.getData();
            int code=bundle.getInt("changcode");
            if(code==1)
            {

                mfg_listview.setVisibility(View.VISIBLE);
                mf_pb.setVisibility(View.GONE);
                ListViewAdapter adapter=new ListViewAdapter(getActivity(),listdata);
                mfg_listview.setAdapter(adapter);
                setListViewHeightBasedOnChildren(mfg_listview);


            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_main, null);
        initView(contentView);
        setPagerView();
        new Thread(new loadHttpData()).start();
        return contentView;
    }
    //计算listview的高度
    private void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null,listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = (totalHeight+ (listView.getHeight() * (listAdapter.getCount())));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
    public  void initView(View v)
    {
        mfg_listview= (ListView)v.findViewById(R.id.mf_mainlistview);
        mf_pb=(ProgressBar)v.findViewById(R.id.mf_progressbar);
        rollpager=(RollPagerView) v.findViewById(R.id.viewpager);
        mfg_listview.setVisibility(View.GONE);
        mf_pb.setVisibility(View.VISIBLE);
    }

    public void setPagerView()
    {
        //设置播放时间间隔
        rollpager.setPlayDelay(2000);
       //设置透明度
        rollpager.setAnimationDurtion(500);
        //设置适配器
        rollpager.setAdapter(new TestNormalAdapter());

        //设置指示器（顺序依次）
       //自定义指示器图片
        //设置圆点指示器颜色
       //设置文字指示器
        //隐藏指示器
       //mRollViewPager.setHintView(new IconHintView(this,R.drawable.point_focus,R.drawable.point_normal));
       rollpager.setHintView(new ColorPointHintView(getActivity(),Color.YELLOW,Color.WHITE));
        //mRollViewPager.setHintView(new TextHintView(this));
        //mRollViewPager.setHintView(null);
    }


    private class TestNormalAdapter extends StaticPagerAdapter
    {

        private int imgs[]={R.mipmap.ic_1
        ,R.mipmap.ic_2
        ,R.mipmap.ic_3
        ,R.mipmap.ic_4};
        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view=new ImageView(container.getContext());
            view.setImageResource(imgs[position]);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
           return view;
        }

        @Override
        public int getCount() {
            return imgs.length;
        }
    }


    public ArrayList<HashMap<String,Object>> getListData()
    {
        return listdata;
    }

   private class loadHttpData implements  Runnable
    {

        @Override
        public void run() {
            AsyncHttpClient client=new AsyncHttpClient();
            RequestParams params=new RequestParams();
            Log.i("123","run");
            params.put("index",String.valueOf(2));
            client.get(StoreApplication.IP_ADDRESS+"home",params,new request());

        };
    }

    class request extends JsonHttpResponseHandler {


        @Override
        public void onSuccess(JSONObject jsonObject) {
            super.onSuccess(jsonObject);
            Log.i("123","onSuccess");
            try {
                JSONArray jsonArray=jsonObject.getJSONArray("list");
                for(int i=0;i<jsonArray.length();i++)
                {
                    HashMap<String,Object> map=new HashMap<String,Object>();
                    JSONObject object=jsonArray.getJSONObject(i);
                    map.put("id",object.get("id").toString());
                    map.put("name",object.get("name").toString());
                    map.put("packagename",object.get("packageName").toString());
                    map.put("iconUrl",object.get("iconUrl"));
                    map.put("stars",object.get("stars").toString());
                    map.put("size",object.get("size").toString());
                    map.put("downloadUrl",object.get("downloadUrl"));
                    map.put("intro",object.get("des"));
                    listdata.add(map);

                }
                Message msg=new Message();
                Bundle bundle=new Bundle();
                bundle.putInt("changcode",1);
                msg.setData(bundle);
                Log.i("124","sendMessage");
                handle.sendMessage(msg);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onFailure(Throwable throwable, JSONArray jsonArray) {
            super.onFailure(throwable, jsonArray);
            Log.i("123","onFailure");
        }
    }


}
