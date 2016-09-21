package com.appstore.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appstore.R;

/**
 * Created by 张艳琴 on 2016/9/19.
 */
public class RankFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_rank, null);
        findView(contentView);
        return contentView;
    }

    private void findView(View contentView) {
//        TagView tv = (TagView) getView().findViewById(R.id.tags_view);
//        TagView.Tag[] tags = {
//                new TagView.Tag("Sample tag", Color.parseColor("#0099CC")),
//                new TagView.Tag("Another one", Color.parseColor("#9933CC"))
//        };
//        tv.setTags(tags, " ");
    }
}