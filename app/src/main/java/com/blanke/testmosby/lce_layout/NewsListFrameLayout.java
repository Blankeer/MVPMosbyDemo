package com.blanke.testmosby.lce_layout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.blanke.testmosby.R;
import com.blanke.testmosby.bean.News;
import com.blanke.testmosby.lceviewstate.persenter.NewsListPersenter;
import com.blanke.testmosby.lceviewstate.view.NewsListView;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.layout.MvpViewStateFrameLayout;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.CastedArrayListLceViewState;
import com.orhanobut.logger.Logger;

import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.internal.SuperViewHolder;

import java.util.List;

/**
 */
public class NewsListFrameLayout extends MvpViewStateFrameLayout<NewsListView, NewsListPersenter>
        implements NewsListView {
    private View viewLoading, viewError, viewContent;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SuperAdapter<News> mAdapter;

    public NewsListFrameLayout(Context context) {
        super(context);
    }

    public NewsListFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewsListFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NewsListFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        viewLoading = findViewById(R.id.loadingView);
        viewError = findViewById(R.id.errorView);
        viewContent = findViewById(R.id.contentView);
        if (viewContent instanceof SwipeRefreshLayout) {
            mSwipeRefreshLayout = (SwipeRefreshLayout) viewContent;
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_light, android.R.color.holo_red_light);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData(true);
                }
            });
        }
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
        viewError.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(false);
            }
        });
        viewContent.setVisibility(GONE);
        viewLoading.setVisibility(GONE);
        viewError.setVisibility(GONE);
    }

    @NonNull
    @Override
    public ViewState<NewsListView> createViewState() {
        return new CastedArrayListLceViewState<List<News>, NewsListView>();
    }

    @Override
    public CastedArrayListLceViewState getViewState() {
        return (CastedArrayListLceViewState) super.getViewState();
    }

    @Override
    public void onNewViewStateInstance() {
        Logger.d("onNewViewStateInstance");
        loadData(false);
    }

    @Override
    public NewsListPersenter createPresenter() {
        return new NewsListPersenter();
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        viewContent.setVisibility(GONE);
        viewLoading.setVisibility(pullToRefresh ? GONE : VISIBLE);
        viewError.setVisibility(GONE);
    }

    @Override
    public void showContent() {
        viewContent.setVisibility(VISIBLE);
        viewLoading.setVisibility(GONE);
        viewError.setVisibility(GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        viewContent.setVisibility(GONE);
        viewLoading.setVisibility(GONE);
        viewError.setVisibility(VISIBLE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void setData(List<News> data) {
        Logger.d(data);
        getViewState().setStateShowContent(data);
        mAdapter.replaceAll(data);
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        Logger.d(pullToRefresh);
        showLoading(pullToRefresh);
        getPresenter().getNews(0, 0, 0);
    }
}
