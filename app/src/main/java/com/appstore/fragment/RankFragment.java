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
import android.widget.AdapterView;
import android.widget.Toast;

import com.appstore.R;
import com.appstore.adapter.SubjectAdapter;
import com.appstore.tagview.OnTagClickListener;
import com.appstore.tagview.Tag;
import com.appstore.tagview.TagView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Random;

/**
 * Created by 张艳琴 on 2016/9/19.
 */
public class RankFragment extends Fragment {

    private TagView tagView;
    private Random random;
    String[] contet = null;
    View contentView;
    String str;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView1 = inflater.inflate(R.layout.fragment_rank, null);
        contentView = contentView1;
        findView(contentView1);
        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void findView(View v) {
        tagView = (TagView) v.findViewById(R.id.tagview);
        tagView.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(int position, Tag tag) {//每个Tag的点击事件
                Toast.makeText(getActivity(), "click tag text = " + tag.text + " position = " + position, Toast.LENGTH_SHORT).show();
            }
        });
        //ADD TAG
        random = new Random();
        //=this.getResources().getStringArray(R.array.continents);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String url = getResources().getString(R.string.ip_address) + "hot";
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String s) {
                str = s;
                Message msg = Message.obtain();
                msg.what = 1;
                handler.sendEmptyMessage(1);
                super.onSuccess(s);
            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                super.onFailure(throwable, s);
            }
        });

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (str != null) {
                    final String[] colors = getActivity().getResources().getStringArray(R.array.colors);
                    final String[] contents = null;
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(str);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Tag tag = new Tag(jsonArray.optString(i));
                        tag.radius = 20f;
                        tag.layoutColor = Color.parseColor(colors[i % colors.length]);
                        tagView.addTag(tag);
                    }
                }
            }
        }
    };
}