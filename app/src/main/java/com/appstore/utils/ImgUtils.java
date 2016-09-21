package com.appstore.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.appstore.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by heyzqt on 2016/9/21.
 * <p/>
 * 图片操作类
 */
public class ImgUtils {

    private static DisplayImageOptions options;

    private static ImageLoader mImageLoader;

    /**
     * 加载网络图片
     *
     * @param view
     * @param imgurl
     * @param imgId
     */
    public static void setInterImg(View view, String imgurl, int imgId) {
        mImageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY).build();
        addDynamicView(view, imgurl, imgId);
    }

    private static void addDynamicView(View view, String imgurl, int imgId) {
        ImageView img = (ImageView) view.findViewById(imgId);
        mImageLoader.displayImage(imgurl, img,
                options);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }
}
