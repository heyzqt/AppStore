package com.appstore.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appstore.R;
import com.appstore.StoreApplication;
import com.appstore.adapter.TypeGridViewAdapter;
import com.appstore.adapter.TypeListViewAdapter;
import com.appstore.entity.Type;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 张艳琴 on 2016/9/19.
 */
public class TypeFragment extends Fragment {

    private ListView mListView;

    private TextView mTypeName;

    private GridView mGridView;

    private ImageView mTypeImg;

    private TextView mSubTypeName;

    private TypeListViewAdapter mLvAdapter;

    private TypeGridViewAdapter mGvAdapter;

    private List<Map<String, List<Type>>> mAppLists = new ArrayList<>();

    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_type, null);
        mContext = getActivity();
        initView(contentView);
        return contentView;
    }

    private void initView(View v) {
        //从服务器获取数据
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("index", "1");
        String ip = StoreApplication.IP_ADDRESS + "category";
        client.post(mContext, ip, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(Throwable throwable, String s) {
                Toast.makeText(mContext, "网络连接失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, String s) {
                try {

                    Map<String, List<Type>> map;
                    String key;
                    List<Type> lists;
                    Type type;

                    //处理分类的数据
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject object;
                    JSONArray infoArray;
                    for (int j = 0; j < jsonArray.length(); j++) {
                        object = jsonArray.getJSONObject(j);
                        key = (String) object.get("title");
                        infoArray = object.getJSONArray("infos");
                        map = new HashMap<String, List<Type>>();
                        lists = new ArrayList<Type>();
                        for (int k = 0; k < infoArray.length(); k++) {
                            for (int t = 1; t < 4; t++) {
                                type = new Type();
                                String name = infoArray.getJSONObject(k).getString("name" + t);
                                String url = infoArray.getJSONObject(k).getString("url" + t);
                                type.setName(name);
                                type.setImageUrl(url);
                                lists.add(type);
                            }
                        }
                        map.put(key, lists);
                        mAppLists.add(map);
                    }
                    Message msg = Message.obtain();
                    msg.what = 1;
                    handler.sendEmptyMessage(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mListView = (ListView) v.findViewById(R.id.listview);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (mAppLists != null) {
                    mLvAdapter = new TypeListViewAdapter(mContext, mAppLists, R.layout.item_type_listview);
                    mListView.setAdapter(mLvAdapter);
                }
            }
        }
    };
}