package com.appstore.fragment;

import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.appstore.R;
import com.appstore.widget.MyFlowLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Random;

/**
 * Created by 张艳琴 on 516/9/19.
 */
public class RankFragment extends Fragment {

    View contentView;
   ScrollView scrollView;
    String str;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView1 = inflater.inflate(R.layout.fragment_rank, null);
        contentView = contentView1;
        findView(contentView1);
        return contentView;
    }

    private void findView(View v) {

        scrollView = (ScrollView) v.findViewById(R.id.scroll_views);
      //  mFlowLayout = (FlowLayout) v.findViewById(R.id.id_flowlayout);
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
                    JSONArray jsonArray = null;

                    int padding = (int) (getActivity().getResources().getDisplayMetrics().density * 10 + 0.5f);
                    int padding1 = (int) (getActivity().getResources().getDisplayMetrics().density * 5 + 0.5f);
                    scrollView.setPadding(padding, padding, padding, padding);
                    MyFlowLayout flowLayout = new MyFlowLayout(getActivity());
                    Random random = new Random();
                    try {
                        jsonArray = new JSONArray(str);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length()-18; i++) {
                        final String str = jsonArray.optString(i);
                        TextView textView = new TextView(getActivity());
                        int left, top, right, bottom;
                        left = top = right = bottom =0;
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(left, top, right, bottom);
                        textView.setLayoutParams(params);
                        textView.setTextSize(14);
                        textView.setText(str);
                        textView.setGravity(Gravity.CENTER);
                        textView.setPadding(padding1, padding1, padding1, padding1);
                        textView.setTextColor(Color.WHITE);
                        //随机创建颜色值
//                        int r = 30 + random.nextInt(210);
//                        int g = 30 + random.nextInt(210);
//                        int b = 30 + random.nextInt(210);
                        //按下后的偏白的背景色
                   //     int defaultColor = Color.rgb(r, g, b);
                        ShapeDrawable backgroundDrawable = new ShapeDrawable(new RoundRectShape(new float[] {5, 5, 5, 5, 5, 5, 5, 5},
                                null, null));
                    //    backgroundDrawable.getPaint().setColor( defaultColor);
                        backgroundDrawable.getPaint().setColor(  Color.parseColor(colors[i % colors.length]));
                        textView.setBackgroundDrawable(backgroundDrawable);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                            }
                        });
                        LinearLayout ll = new LinearLayout(getActivity()); // + 增加行
                        ll.setOrientation(LinearLayout.VERTICAL); // + 增加行
                        ll.addView(textView); // + 增加行
                        flowLayout.addView(ll);
                    }
                   scrollView.addView(flowLayout);
                }
            }
        }
    };
}