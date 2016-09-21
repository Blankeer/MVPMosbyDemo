package com.blanke.testmosby.lce_layout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.blanke.testmosby.bean.News;
import com.blanke.testmosby.lceviewstate.persenter.NewsListPersenter;
import com.blanke.testmosby.lceviewstate.view.NewsListView;
import com.hannesdorfmann.mosby.mvp.viewstate.RestorableParcelableViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.layout.MvpViewStateFrameLayout;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.CastedArrayListLceViewState;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by asus on 2016/9/20.
 */
public class NewsListFrameLayout extends MvpViewStateFrameLayout<NewsListView, NewsListPersenter>
        implements NewsListView {

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

    @NonNull
    @Override
    public ViewState<NewsListView> createViewState() {
        return new CastedArrayListLceViewState<List<News>, NewsListView>();
    }

    @Override
    public RestorableParcelableViewState getViewState() {
        Logger.d("getViewState");
        return super.getViewState();
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

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {

    }

    @Override
    public void setData(List<News> data) {
        Logger.d(data);
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        Logger.d(pullToRefresh);
        getPresenter().getNews(0, 0, 0);
    }
}
