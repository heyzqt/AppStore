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
import android.widget.TextView;
import android.widget.Toast;

import com.appstore.R;
import com.appstore.widget.KeywordsFlow;
import com.appstore.widget.KeywordsFlowView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by 张艳琴 on 2016/9/19.
 * <p>
 * 推荐界面
 */
public class RecommentFragment extends Fragment {

    private KeywordsFlowView mKeywordsView;

    private String str;

    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_recomment, null);
        mContext = getActivity();
        initView(contentView);
        return contentView;
    }

    private void initView(View v) {
        //获取数据
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

        mKeywordsView = (KeywordsFlowView) v.findViewById(R.id.keywordsFlowView);
        mKeywordsView.setTextShowSize(15);
        mKeywordsView.shouldScroolFlow(true);
        mKeywordsView.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, ((TextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
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
                        //放入String数组中
                        int n = jsonArray.length();
                        String[] contentStr = new String[n];
                        for (int i = 0; i < n; i++) {
                            contentStr[i] = jsonArray.optString(i);
                        }
                        //开始展示
                        mKeywordsView.show(contentStr, KeywordsFlow.ANIMATION_IN);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
}