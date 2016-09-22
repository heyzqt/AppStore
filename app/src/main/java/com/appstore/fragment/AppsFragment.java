package com.appstore.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.appstore.R;
import com.appstore.activity.AppDetailsActvity;
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
public class AppsFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ListView ap_listview;
    private ProgressBar af_pb;
    private int index=1;
    ArrayList<HashMap<String,Object>> listdata=new ArrayList<HashMap<String,Object>>();
    private Handler handle=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle=msg.getData();
            int code=bundle.getInt("changcode");
            if(code==1)
            {


                ap_listview.setVisibility(View.VISIBLE);
                af_pb.setVisibility(View.GONE);
                ListViewAdapter adapter1=new ListViewAdapter(getActivity(),listdata);
                ap_listview.setAdapter(adapter1);

            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_apps, null);
        initView(contentView);
        new Thread(new loadHttpData()).start();
        return contentView;
    }
    public void initView(View v)
    {
        ap_listview=(ListView)v.findViewById(R.id.ap_listview);
        af_pb=(ProgressBar)v.findViewById(R.id.ap_progressBar);
        ap_listview.setVisibility(View.GONE);
        af_pb.setVisibility(View.VISIBLE);
        ap_listview.setOnItemClickListener(this);
    }
    public ArrayList<HashMap<String,Object>> getListData()
    {
        return listdata;
    }
    private class loadHttpData implements  Runnable
    {

        @Override
        public void run() {
            Looper.prepare();
            AsyncHttpClient client=new AsyncHttpClient();
            RequestParams params=new RequestParams();
            params.put("index",String.valueOf(index));
            client.get("http://localhost:8090/app",params,new request());
            Looper.loop();
        }
    }

    class request extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(JSONArray jsonArray) {
            super.onSuccess(jsonArray);
            try {
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
                Message msg=new Message();
                Bundle bundle=new Bundle();
                bundle.putInt("changcode",1);
                msg.setData(bundle);
                handle.sendMessage(msg);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
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
