package com.appstore.widget;

/**
 * Created by 张艳琴 on 2016/9/23.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mChenys on 2015/12/5.
 */
public class MyFlowLayout extends ViewGroup {
    public static final int DEFAULT_SPACING = 20;
    //水平间距和垂直间距
    private int mHorizontalSpacing = DEFAULT_SPACING, mVertivalSpacing = DEFAULT_SPACING;
    private Line mLine = null;  //当前行
    private int mUsedWidth = 0;//当前行已使用的宽度
    private int maxLine = 100; //最大行数
    private List<Line> mLineList = new ArrayList<>();

    public MyFlowLayout(Context context) {
        this(context, null);
    }

    public MyFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int lines = mLineList.size();
        //确定行之间的垂直位置
        for (int i = 0; i < lines; i++) {
            Line oneLine = mLineList.get(i);
            oneLine.layoutView(left, top);
            //更新每一行的top坐标
            top += oneLine.mHeight + mVertivalSpacing;
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //子控件的最大宽高
        int maxChildWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int maxChildHeight = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        //父控件的宽高模式
        int parentWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int parentHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        restoreLine();//每次进来onMeasure都需要重置一下数据

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() == View.GONE) {
                continue;
            }
            //确定子控件的宽高模式
            int childWidthSpec = MeasureSpec.makeMeasureSpec(maxChildWidth,
                    parentWidthMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : parentWidthMode);
            int childHeightSpec = MeasureSpec.makeMeasureSpec(maxChildHeight,
                    parentHeightMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : parentHeightMode);
            //测量子控件
            childView.measure(childWidthSpec, childHeightSpec);
            if (mLine == null) {
                mLine = new Line();
            }
            int width = childView.getMeasuredWidth();
            mUsedWidth += width;
            if (mUsedWidth <= maxChildWidth) {
                //当前使用的宽度没有超过最大子控件的宽度,那么可以添加到行中
                mLine.addView(childView);
                mUsedWidth += mHorizontalSpacing;//加上水平间距
                if (mUsedWidth >= maxChildWidth) {
                    //加上水平间距后大于了最大子控件宽度,那么需要换行
                    if (!newLine()) {
                        break;
                    }
                }
            } else {
                //2种情况
                //1.当前行没有控件,要添加的控件宽度大于最大宽度,强制添加,再换行
                if (mLine.getViewCount() == 0) {
                    mLine.addView(childView);
                    if (!newLine()) {
                        break;
                    }
                } else {
                    //2.当前行剩余空间不够存放,需要先换行,再添加
                    if (!newLine()) {
                        break;
                    }
                    mLine.addView(childView);
                    mUsedWidth += childView.getMeasuredWidth() + mHorizontalSpacing;//更新当前行使用的宽度
                }
            }
        }
        //确保最后一行有剩余宽度的情况,也要把这一行添加到集合
        if (null != mLine && mLine.getViewCount() > 0 && !mLineList.contains(mLine)) {
            mLineList.add(mLine);
        }
        //确定父控件宽高
        int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int totalHeight = 0;
        int lines = mLineList.size();
        for (int i = 0; i < lines; i++) {
            Line oneLine = mLineList.get(i);
            totalHeight += oneLine.mHeight;
        }
        totalHeight += mVertivalSpacing * (lines - 1) + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(totalWidth, totalHeight);
    }

    /**
     * 创建新行
     * @return
     */
    private boolean newLine() {
        //保存当前行
        mLineList.add(mLine);
        if (mLineList.size() > maxLine) {
            return false;
        }
        //重置变量
        mLine = new Line();
        mUsedWidth = 0;
        return true;
    }

    /**
     * 还原所有数据
     */
    private void restoreLine() {
        mLineList.clear();
        mLine = new Line();
        mUsedWidth = 0;
    }

    /**
     * 代表着一行，封装了一行所占高度，该行子View的集合，以及所有View的宽度总和
     */
    private class Line {
        private int mHeight;//行高
        private int mWidth; //当前行子View的总宽度
        private List<View> childViews = new ArrayList<>();
        //添加子控件到行
        public void addView(View view) {
            childViews.add(view);
            mHeight = mHeight > view.getMeasuredHeight() ? mHeight : view.getMeasuredHeight();
            mWidth += view.getMeasuredWidth();
        }
        //获取当前行的子控件个数
        public int getViewCount() {
            return childViews.size();
        }

        public void layoutView(int left, int top) {
            //1.有剩余宽度的情况
            int viewCount = getViewCount();
            int surplusWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() -
                    mWidth - mHorizontalSpacing * (viewCount - 1);
            if (surplusWidth >= 0) {
                //需要平均分配
                int splitWidth = (int) (surplusWidth / viewCount + 0.5f);
                for (int i = 0; i < viewCount; i++) {
                    View childView = childViews.get(i);
                    int width = childView.getMeasuredWidth();
                    int height = childView.getMeasuredHeight();
                    if (splitWidth > 0) {
                        width += splitWidth;
                        //重新测量控件
                        int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
                        int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
                        childView.measure(widthSpec, heightSpec);
                    }
                    //高度低的要垂直居中显示
                    int topOffset = (int) ((mHeight - height) / 2 + 0.5f);
                    if (topOffset < 0) {
                        topOffset = 0;
                    }
                    //更新top值
                    top += topOffset;
                    //确定当前行子控件的位置
                    childView.layout(left, top, left + width, top + height);
                    //更新下一子控件的left坐标
                    left += width + mHorizontalSpacing;
                }
            } else {
                //2.超出子控件的最大宽度,强制添加后的位置确定
                if (getViewCount() == 1) {
                    View childView = childViews.get(0);
                    childView.layout(left, top, left + childView.getMeasuredWidth(), top + childView.getMeasuredHeight());
                }
            }
        }
    }
}