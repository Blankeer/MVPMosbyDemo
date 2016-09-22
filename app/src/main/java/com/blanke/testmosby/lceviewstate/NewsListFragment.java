package com.blanke.testmosby.lceviewstate;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blanke.testmosby.R;
import com.blanke.testmosby.bean.News;
import com.blanke.testmosby.lceviewstate.persenter.NewsListPersenter;
import com.blanke.testmosby.lceviewstate.view.NewsListView;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateFragment;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;
import com.orhanobut.logger.Logger;

import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.internal.SuperViewHolder;

import java.util.List;

/**
 * Created by asus on 2016/9/20.
 */
public class NewsListFragment extends MvpLceViewStateFragment<RecyclerView, List<News>, NewsListView, NewsListPersenter>
        implements NewsListView {
    private RecyclerView fragmentRecyclerview;
    private SuperAdapter<News> mAdapter;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentRecyclerview = (RecyclerView) view.findViewById(R.id.contentView);
        fragmentRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new SuperAdapter<News>(getActivity(), null, android.R.layout.simple_list_item_1) {
            @Override
            public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, News item) {
                holder.setText(android.R.id.text1, item.getTitle() + "\n" + item.getContent());
            }
        };
        fragmentRecyclerview.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logger.d("onActivityCreated");
    }

    @Override
    public LceViewState<List<News>, NewsListView> createViewState() {
        return new RetainingLceViewState<>();
    }

    @Override
    public List<News> getData() {
        return mAdapter.getData();
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return e.toString();
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
    public void loadData(boolean pullToRefresh) {
        Logger.d("loadData# pullToRefresh=" + pullToRefresh);
        showLoading(pullToRefresh);
        getPresenter().getNews(0, 0, 0);
    }
}
