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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.appstore.R;
import com.appstore.activity.AppDetailsActvity;
import com.appstore.adapter.ImgLoaders;
import com.appstore.adapter.ListViewAdapter;
import com.appstore.utils.ImgUtils;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by 张艳琴 on 2016/9/19.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemClickListener{

    private ListView mfg_listview;

    public Context context;
    private ImageLoader mImageLoader;
    ArrayList<HashMap<String,Object>> listdata=new ArrayList<HashMap<String,Object>>();

    ArrayList<HashMap<String,Object>> listurl=new ArrayList<HashMap<String,Object>>();
    private ProgressBar mf_pb;
    private int index=0;

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
                setPagerView();
                setListViewHeightBasedOnChildren(mfg_listview);

            }

        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_main, null);
        initView(contentView);

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
        this.context=getActivity();
        mfg_listview= (ListView)v.findViewById(R.id.mf_mainlistview);
        mf_pb=(ProgressBar)v.findViewById(R.id.mf_progressbar);
        rollpager=(RollPagerView) v.findViewById(R.id.viewpager);
        mfg_listview.setVisibility(View.GONE);
        mf_pb.setVisibility(View.VISIBLE);
        mfg_listview.setOnItemClickListener(this);
    }

    public void setPagerView()
    {

        //设置播放时间间隔
        rollpager.setPlayDelay(2000);
        //设置透明度
        rollpager.setAnimationDurtion(500);
        //设置适配器
        new Thread(new loadImage()).start();


        //设置指示器（顺序依次）
        //自定义指示器图片
        //设置圆点指示器颜色
        //设置文字指示器
        //隐藏指示器
        //mRollViewPager.setHintView(new IconHintView(this,R.drawable.point_focus,R.drawable.point_normal));
        rollpager.setHintView(new ColorPointHintView(getActivity(),Color.YELLOW,Color.WHITE));
        //mRollViewPager.setHintView(new TextHintView(this));
        //mRollViewPager.setHintView(null);
    }


    private class TestNormalAdapter extends StaticPagerAdapter
    {

        public Context t_context;
        public ImgLoaders loader;
        private Bitmap bit[]=new Bitmap[listurl.size()];
        public  TestNormalAdapter(Context context)
        {

            t_context=context;
            loader=new ImgLoaders();
            loader.initImageLoader(t_context);
            mImageLoader=ImageLoader.getInstance();


            for(int i=0;i<listurl.size();i++)
            {

                bit[i]=mImageLoader.loadImageSync("http://localhost:8090/image?name="+listurl.get(i).get("pagerimageurl"),loader.getOption());

            }
        }

        @Override
        public View getView(ViewGroup container, int position) {

            ImageView view=new ImageView(container.getContext());
            view.setImageBitmap(bit[position]);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }

        @Override
        public int getCount() {
            return listurl.size();
        }
    }


    //异步访问数据，加载轮播数据
    private class loadImage implements Runnable
    {

        @Override
        public void run() {
            rollpager.setAdapter(new TestNormalAdapter(context));
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
            Looper.prepare();
            AsyncHttpClient client=new AsyncHttpClient();
            RequestParams params=new RequestParams();
            params.put("index",String.valueOf(index));
            client.get("http://localhost:8090/home",params,new request());

            Looper.loop();
        }
    }

    class request extends JsonHttpResponseHandler {


        @Override
        public void onSuccess(JSONObject jsonObject) {
            super.onSuccess(jsonObject);

            try {
                Log.i("127","jsonArray1");
                JSONArray jsonArray1 = jsonObject.getJSONArray("picture");
                Log.i("127",jsonArray1.toString());
                for (int i = 0;i<jsonArray1.length();i++)
                {
                    HashMap<String,Object> map=new HashMap<String,Object>();
                    map.put("pagerimageurl",jsonArray1.get(i).toString());
                    listurl.add(map);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            try {
                JSONArray jsonArray=jsonObject.getJSONArray("list");
                for(int i=0;i<jsonArray.length();i++)
                {
                    HashMap<String,Object> map=new HashMap<String,Object>();

                    JSONObject object=jsonArray.getJSONObject(i);

                    map.put("id",object.get("id").toString());
                    map.put("name",object.get("name").toString());
                    //获取包名
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

    //跳转到详情界面
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(),"跳转",Toast.LENGTH_SHORT).show();
       Intent intent=new Intent(getActivity(), AppDetailsActvity.class);
        Bundle bundle=new Bundle();
        bundle.putString("comname",listdata.get(position).get("packagename").toString());
        intent.putExtras(bundle);
        startActivity(intent);



    }

}