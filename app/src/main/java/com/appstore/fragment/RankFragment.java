package com.appstore.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appstore.R;
import com.appstore.tagview.OnTagClickListener;
import com.appstore.tagview.Tag;
import com.appstore.tagview.TagView;

import java.util.Random;

/**
 * Created by 张艳琴 on 2016/9/19.
 */
public class RankFragment extends Fragment {

    private TagView tagView;
    private Random random;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_rank, null);
        findView(contentView);
        return contentView;
    }

    private void findView(View v) {
        tagView= (TagView) v.findViewById(R.id.tagview);
        tagView.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(int position, Tag tag) {//每个Tag的点击事件
                Toast.makeText(getActivity(), "click tag text = " + tag.text + " position = " + position, Toast.LENGTH_SHORT).show();
            }
        });
        //ADD TAG
        random = new Random();
        String[] colors = this.getResources().getStringArray(R.array.colors);
        String[] contents=this.getResources().getStringArray(R.array.continents);
        for (int i =0; i < contents.length; i++) {
            Tag tag = new Tag(contents[i]);
            tag.radius = 20f;
            tag.layoutColor = Color.parseColor(colors[i%colors.length]);
            tagView.addTag(tag);
        }
    }
}