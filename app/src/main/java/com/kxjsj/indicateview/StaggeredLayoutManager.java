package com.kxjsj.indicateview;

import android.graphics.Rect;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by vange on 2017/11/14.
 */

public class StaggeredLayoutManager extends RecyclerView.LayoutManager {

    private int count;
    int[] offsets;
    int scrolls;
    int maxHeight;

    SparseArray<Integer> sizeArray = new SparseArray<>(5);
    SparseArray<Boolean> hasadds = new SparseArray<>();

    SparseArray<Rect> layouts = new SparseArray<>();
    private OrientationHelper helper;
    private OrientationHelper helper2;
    private Rect screenRect;

    public StaggeredLayoutManager setCount(int count) {
        this.count = count;
        offsets = new int[count];
        return this;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if(state.getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
        }
        if (getChildCount()== 0 &&state.isPreLayout()) {
            return;
        }

        if (helper == null) {
            helper = OrientationHelper.createHorizontalHelper(this);
            helper2 = OrientationHelper.createVerticalHelper(this);
        }

        detachAndScrapAttachedViews(recycler);


        /**
         * 预计算位置
         */
        Saveposition(recycler, state);

        layout(recycler, state, 0);

    }

    private void Saveposition(RecyclerView.Recycler recycler, RecyclerView.State state) {
        offsets = new int[count];
        layouts.clear();
        sizeArray.clear();
        hasadds.clear();
        int eachWidth = helper.getTotalSpace() / count;
        for (int i = 0; i < state.getItemCount(); i++) {
            View scrap = recycler.getViewForPosition(i);
            int itemViewType = getItemViewType(scrap);
            /**
             * 之测量不同type的大小 计算位置
             */
            if(sizeArray.get(itemViewType)==null){
                addView(scrap);
                measureChildWithMargins(scrap, 0, 0);
                int decoratedMeasuredHeight = getDecoratedMeasuredHeight(scrap);
                detachAndScrapView(scrap, recycler);
                sizeArray.put(itemViewType,decoratedMeasuredHeight);
                hasadds.put(i,true);
            }
            int rowNumber = getMinIndex();
            Rect rect=new Rect(rowNumber * eachWidth, offsets[rowNumber], (rowNumber + 1) * eachWidth, offsets[rowNumber] + sizeArray.get(itemViewType));
            layouts.put(i,rect);
            offsets[rowNumber] = offsets[rowNumber] + rect.height();
        }
        maxHeight = getMaxHeight();

    }

    /**
     * 获取最小的指针位置
     *
     * @return
     */
    private int getMinIndex() {
        int min = 0;
        int minnum = offsets[0];
        for (int i = 1; i < offsets.length; i++) {
            if (minnum > offsets[i]) {
                minnum = offsets[i];
                min = i;
            }
        }
        return min;
    }

    /**
     * 获取最大的高度
     *
     * @return
     */
    private int getMaxHeight() {
        int max = offsets[0];
        for (int i = 1; i < offsets.length; i++) {
            if (offsets[i] > max) {
                max = offsets[i];
            }
        }
        return max;
    }

    public Rect getRect(RecyclerView.Recycler recycler,int position){
        Rect rect=layouts.get(position);
        if (hasadds.get(position)==null) {
            View scrap= recycler.getViewForPosition(position);
            addView(scrap);
            measureChildWithMargins(scrap, 0, 0);
            detachAndScrapView(scrap,recycler);
            hasadds.put(position,true);
        }
        return rect;
    }

    /**
     * 获取第一个可见在adapter中位置
     *
     * @return
     */
    private int getfristAdapterPosition() {
        int position = 0;
        if (getChildCount() != 0) {
            View firstView = getChildAt(0);
            position = getPosition(firstView);
        }
        return position;
    }

    /**
     * 获取第最后个可见在adapter中位置
     *
     * @return
     */
    private int getlastAdapterPosition() {
        int position = 0;
        if (getChildCount() != 0) {
            View firstView = getChildAt(getChildCount() - 1);
            position = getPosition(firstView);
        }
        return position;
    }

    /**
     * dy 1 上滑 -1 下滑 0出初始
     *
     * @param recycler
     * @param state
     */
    private void layout(RecyclerView.Recycler recycler, RecyclerView.State state, int dy) {
        Rect layoutRange = new Rect(getPaddingLeft(), getPaddingTop()+scrolls, helper.getTotalSpace() + getPaddingRight(), helper2.getTotalSpace() + getPaddingTop()+scrolls);
        int itemCount = state.getItemCount();
//        getOffsets(dy);
        if (dy == 0) {
            int first = getfristAdapterPosition();
            dolayoutAndRecycler(recycler, layoutRange, itemCount, first);
        } else if (dy == 1) {
            int last = getlastAdapterPosition();
            dolayoutAndRecycler(recycler, layoutRange, itemCount, last + 1);
        } else {
            int first = getfristAdapterPosition();
            dolayoutAndRecyclerDown(recycler, layoutRange, itemCount, first - 1);
        }


    }

    private void recyclerViews(RecyclerView.Recycler recycler) {
        int childCount = getChildCount();
        if(childCount==0){
            return;
        }
        if(screenRect==null) {
            screenRect = new Rect(getPaddingLeft(), getPaddingTop(), helper.getTotalSpace() + getPaddingLeft(), helper2.getTotalSpace() + getPaddingTop());
        }
        List<View> toBeRemove=new ArrayList<>();
        for (int i = 0; i < childCount-1; i++) {
            View view = getChildAt(i);
            Rect rect=new Rect() ;
            getDecoratedBoundsWithMargins(view,rect);

            if(!rect.intersect(screenRect)){
                toBeRemove.add(view);
            }
        }
        for (View view : toBeRemove) {
          removeAndRecycleView(view,recycler);
        }
    }


    private void dolayoutAndRecyclerDown(RecyclerView.Recycler recycler, Rect layoutRange, int itemCount, int first) {

        recyclerViews(recycler);

        boolean layoutStart = false;
        boolean layoutend = false;
        for (int i = first; i > 0; i--) {
            /**
             * 可见位置遍历结束，跳出循环
             */
            if (layoutend) {
                break;
            }
            Rect layout=getRect(recycler,i);
            if (layout.intersect(layoutRange)) {
                View viewForPosition = recycler.getViewForPosition(i);
                addView(viewForPosition);
                layoutStart = true;
                layoutDecorated(viewForPosition, layout.left, layout.top -scrolls, layout.right, layout.bottom - scrolls);
            } else {
                if (layoutStart) {
                    layoutend = true;
                }
            }
        }
    }

    /**
     * 出初始layout
     *
     * @param recycler
     * @param layoutRange
     * @param itemCount
     * @param first
     */
    private void dolayoutAndRecycler(RecyclerView.Recycler recycler, Rect layoutRange, int itemCount, int first) {

        boolean layoutStart = false;
        boolean layoutend = false;
        recyclerViews(recycler);
        for (int i = first; i < itemCount; i++) {
            /**
             * 可见位置遍历结束，跳出循环
             */
            if (layoutend) {
                break;
            }
            Rect layout=getRect(recycler,i);

            if (layout.intersect(layoutRange)) {
                View viewForPosition = recycler.getViewForPosition(i);
                addView(viewForPosition);
                layoutStart = true;
                layoutDecorated(viewForPosition, layout.left, layout.top -scrolls, layout.right, layout.bottom -scrolls);
            } else {
                if (layoutStart) {
                    layoutend = true;
                }
            }
        }
    }


    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public boolean canScrollHorizontally() {
        //返回true表示可以横向滑动
        return false;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (maxHeight < helper2.getTotalSpace()) {
            return 0;
        }
        if (scrolls + dy > maxHeight - helper2.getTotalSpace()) {
            dy = maxHeight - helper2.getTotalSpace() - scrolls;
        }
        if (scrolls + dy < 0) {
            dy = -scrolls;
        }
        scrolls += dy;
        offsetChildrenVertical(-dy);
        if (dy > 0) {
            layout(recycler, state, 1);
        } else if (dy < 0) {
            layout(recycler, state, -1);
        }
        return dy;
    }
}
