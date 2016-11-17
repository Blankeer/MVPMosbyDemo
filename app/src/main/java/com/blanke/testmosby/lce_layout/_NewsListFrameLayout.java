package com.blanke.testmosby.lce_layout;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.blanke.testmosby.mosby_class.MvpLceViewStateFrameLayout;
import com.blanke.testmosby.R;
import com.blanke.testmosby.bean.News;
import com.blanke.testmosby.lceviewstate.persenter.NewsListPersenter;
import com.blanke.testmosby.lceviewstate.view.NewsListView;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.CastedArrayListLceViewState;
import com.orhanobut.logger.Logger;

import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.internal.SuperViewHolder;

import java.util.List;

/**
 *
 */
public class _NewsListFrameLayout extends MvpLceViewStateFrameLayout<SwipeRefreshLayout, List<News>, NewsListView, NewsListPersenter>
        implements NewsListView {
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SuperAdapter<News> mAdapter;

    public _NewsListFrameLayout(Context context) {
        super(context);
    }

    public _NewsListFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public _NewsListFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public _NewsListFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mSwipeRefreshLayout = contentView;
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(true);
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new SuperAdapter<News>(getContext(), null, android.R.layout.simple_list_item_1) {
            @Override
            public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, News item) {
                holder.setText(android.R.id.text1, item.getTitle() + "\n" + item.getContent());
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }
    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return e == null ? "error" : e.getMessage();
    }

    @Override
    public LceViewState<List<News>, NewsListView> createViewState() {
        return new CastedArrayListLceViewState<>();
    }

    @Override
    public void onNewViewStateInstance() {
        loadData(false);
    }

    @Override
    public List<News> getData() {
        return mAdapter.getData();
    }

    @Override
    public NewsListPersenter createPresenter() {
        return new NewsListPersenter();
    }

    @Override
    public void setData(List<News> data) {
        Logger.d(data);
        mAdapter.replaceAll(data);
    }

    @Override
    public void showContent() {
        super.showContent();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        super.showError(e, pullToRefresh);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        Logger.d(pullToRefresh);
        showLoading(pullToRefresh);
        getPresenter().getNews(0, 0, 0);
    }
}
