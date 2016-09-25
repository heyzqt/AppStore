package com.appstore.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.appstore.R;
import com.appstore.adapter.SubjectAdapter;
import com.appstore.entity.Subject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 张艳琴 on 2016/9/19.
 */
public class SubjectFragment extends Fragment {
    ListView listView;
    SubjectAdapter subjectAdapter;
    List<Subject> subjectList=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_subject, null);
        findView(contentView);
        return contentView;
    }

    private void findView(View v) {
        listView = (ListView) v.findViewById(R.id.subject_listview);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("index", "0");
        String url = getResources().getString(R.string.ip_address) + "subject";
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int ins, String s) {
                try {
                    subjectList=new ArrayList<Subject>();
                    JSONArray jsonArrays = new JSONArray(s);
                    for (int i = 0; i < jsonArrays.length(); i++) {
                        JSONObject jsonObject = jsonArrays.optJSONObject(i);
                        Subject subject = new Subject();
                        String des = jsonObject.optString("des");
                        String urls = jsonObject.optString("url");
                        subject.setDes(des);
                        subject.setUrl(urls);
                        subjectList.add(subject);
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
                super.onFailure(throwable, s);
            }
        });

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (subjectList != null) {
                    subjectAdapter = new SubjectAdapter(getActivity(), subjectList);
                    listView.setAdapter(subjectAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(getActivity(),subjectList.get(position).getDes(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }
    };
}
