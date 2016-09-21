package com.blanke.testmosby.lceviewstate.persenter;

import com.blanke.testmosby.bean.News;
import com.blanke.testmosby.lceviewstate.view.NewsListView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 */
public class NewsListPersenter extends MvpBasePresenter<NewsListView> {

    public void getNews(int page, int pageSize, int type) {
        Logger.d("getnews");
//        getView().showLoading(false);
        Observable.timer(3000, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, List<News>>() {
                    @Override
                    public List<News> call(Long aLong) {
                        return getNews();
                    }
                }).filter(new Func1<List<News>, Boolean>() {
            @Override
            public Boolean call(List<News> newses) {
                return getView() != null;
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<News>>() {
                    @Override
                    public void call(List<News> newses) {
                        getView().setData(newses);
                        getView().showContent();
                    }
                });
    }

    private List<News> getNews() {
        List<News> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add(new News("news content" + i, "news title" + i));
        }
        return list;
    }
}
