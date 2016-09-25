package com.appstore.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.appstore.R;
import com.appstore.adapter.ListViewAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 张艳琴 on 2016/9/19.
 */
public class GameFragment extends Fragment implements AdapterView.OnItemClickListener,XListView2.IXListViewListener{
    private XListView2 mlistview;

    ListViewAdapter adapter1;
    private ProgressBar gf_pb;
    private int index=1;
    ArrayList<HashMap<String,Object>> listdata=new ArrayList<HashMap<String,Object>>();
    private Handler handle=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle=msg.getData();
            int code=bundle.getInt("changcode");
            String j=bundle.getString("Json");

            try {
                  JSONArray jsonArray=new JSONArray(j);
                Log.i("128","leng:"+String.valueOf(jsonArray.length()));
                for (int i = 0; i <jsonArray.length();i++) {
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
            }
                catch (JSONException e)
                {

                    e.printStackTrace();
                }
            if(code==0) {
                mlistview.setVisibility(View.VISIBLE);
                gf_pb.setVisibility(View.GONE);
                adapter1 = new ListViewAdapter(getActivity(),listdata);
                mlistview.setAdapter(adapter1);
            }
            else
            {
                adapter1.notifyDataSetChanged();

                onLoad();
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_game, null);

        initView(contentView);
        new Thread(new loadLocalData(0)).start();
        return contentView;
    }

    public void initView(View v)
    {
        mlistview=(XListView2)v.findViewById(R.id.gf1_listView);

        mlistview.setPullLoadEnable(true);
        mlistview.setPullRefreshEnable(true);
        mlistview.setXListViewListener(this);
        gf_pb=(ProgressBar)v.findViewById(R.id.gf1_progressBar);
        mlistview.setVisibility(View.GONE);
        gf_pb.setVisibility(View.VISIBLE);
        mlistview.setOnItemClickListener(this);
    }

    private void onLoad() {
        mlistview.stopRefresh();
        mlistview.stopLoadMore();
        mlistview.setRefreshTime("刚刚");
    }
    public ArrayList<HashMap<String,Object>> getListData()
    {
        return listdata;
    }

    @Override
    public void onRefresh() {

        Toast.makeText(getActivity(),"已经是最新的了",Toast.LENGTH_SHORT).show();
        onLoad();
    }

    @Override
    public void onLoadMore() {

        index=index+1;
        Log.i("127"," onLoadMore");
        new Thread(new loadLocalData(1)).start();
    }


    private class loadLocalData implements  Runnable
    {

        private int code;
        public loadLocalData(int code)
        {
            this.code=code;
        }
        @Override
        public void run() {
            AsyncHttpClient client=new AsyncHttpClient();
            RequestParams params=new RequestParams();
            params.put("index",String.valueOf(index));
            client.get("http://localhost:8090/game",params,new request(code));
        }
    }
    private class request extends JsonHttpResponseHandler
    {
        private int code;
        public  request(int code)
        {
            this.code=code;
        }
        @Override
        public void onSuccess(JSONArray jsonArray) {
            super.onSuccess(jsonArray);

                Message msg=new Message();
                Bundle bundle=new Bundle();
                bundle.putInt("changcode",code);
                bundle.putString("Json",jsonArray.toString());
                msg.setData(bundle);
                Log.i("127","gf:sendMessage");
                handle.sendMessage(msg);




        }
    }

    //跳转到详情界面
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(),"跳转",Toast.LENGTH_SHORT).show();
/*        Intent intent=new Intent(getActivity(), AppDetailsActvity.class);
        Bundle bundle=new Bundle();
        bundle.putString("comname",listdata.get(position).get("packagename").toString());
        intent.putExtras(bundle);
        startActivity(intent);*/
    }
}
