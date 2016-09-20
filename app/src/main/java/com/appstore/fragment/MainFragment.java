package com.appstore.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.appstore.R;
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

    private int index=1;
    String appname[]={"黑马程序员","有缘网","酷狗音乐","酷狗音乐","酷狗音乐","酷狗音乐","酷狗音乐"};
    String appsize[]={"89.6KB","3.69MB","8.38M","8.38M","8.38M","8.38M","8.38M"};
    String appintro[]={"google市场app测试","有缘是时下最受单身男女青睐的交友APP","全新改版震撼发布","全新改版震撼发布","全新改版震撼发布","全新改版震撼发布","全新改版震撼发布"};
    int  star[]={3,4,3,3,3,3,3};
    int appimg[]={R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher};
    int appdown[]={R.mipmap.down,R.mipmap.down,R.mipmap.down,R.mipmap.down,R.mipmap.down,R.mipmap.down,R.mipmap.down};
/*    private Handler handle=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle=msg.getData();
            int code=bundle.getInt("changecode");
            if(code==1)
            {
                ListViewAdapter adapter=new ListViewAdapter(getActivity());
                mfg_listview.setAdapter(adapter);
                setListViewHeightBasedOnChildren(mfg_listview);
            }
        }
    };*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_main, null);

        initView(contentView);
        getData();
        SimpleAdapter adapter=new SimpleAdapter(getActivity(),listdata,R.layout.fg_listitem
        ,new String[]{"appimg","name","size","intro","appdown"}
        ,new int[]{R.id.mfg_img_app,R.id.mfg_tv_appname,R.id.mfg_tv_appsize,R.id.mfg_tv_intro,R.id.mfg_img_download});
        mfg_listview.setAdapter(adapter);
        setListViewHeightBasedOnChildren(mfg_listview);
/*        ListViewAdapter adapter=new ListViewAdapter(getActivity());
        mfg_listview.setAdapter(adapter);*/

      /*  new Thread(new loadHttpData()).start();*/
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
    }
    public ArrayList<HashMap<String,Object>> getData()
    {
        for(int i=0;i<appname.length;i++)
        {

            HashMap<String,Object> map=new HashMap<String,Object>();
            map.put("name",appname[i]);
            map.put("size",appsize[i]);
            map.put("intro",appintro[i]);
            map.put("appimg",appimg[i]);
            map.put("appdown",appdown[i]);
            map.put("stars",star[i]);
            listdata.add(map);
        }
        return listdata;
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
            params.put("index",String.valueOf(index));
            client.get(String.valueOf(R.string.app_mfg_http),params,new request());
        };
    }

    class request extends JsonHttpResponseHandler {


        @Override
        public void onSuccess(JSONObject jsonObject) {
            super.onSuccess(jsonObject);
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
                    map.put("sizi",object.get("size").toString());
                    map.put("downloadUrl",object.get("downloadUrl"));
                    map.put("intro",object.get("des"));
                    listdata.add(map);

                }
                Message msg=new Message();
                Bundle bundle=new Bundle();
                bundle.putInt("changcode",1);
                msg.setData(bundle);
                sendMessage(msg);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onFailure(Throwable throwable, JSONArray jsonArray) {
            super.onFailure(throwable, jsonArray);
        }
    }


}
