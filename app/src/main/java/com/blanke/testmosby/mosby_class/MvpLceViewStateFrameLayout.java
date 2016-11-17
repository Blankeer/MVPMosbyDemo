package com.blanke.testmosby.mosby_class;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.delegate.MvpViewStateViewGroupDelegateCallback;
import com.hannesdorfmann.mosby.mvp.delegate.ViewGroupMvpDelegate;
import com.hannesdorfmann.mosby.mvp.delegate.ViewGroupMvpViewStateDelegateImpl;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;

/**
 * 官方未提供该类，根据#MvpLceFragment写了此类
 */
public abstract class MvpLceViewStateFrameLayout<CV extends View, M, V extends MvpLceView<M>, P extends MvpPresenter<V>>
        extends MvpLceFrameLayout<CV, M, V, P> implements MvpLceView<M>,
        MvpViewStateViewGroupDelegateCallback<V, P> {

    /**
     * The viewstate will be instantiated by calling {@link #createViewState()} in {@link
     * #onFinishInflate()}. Don't instantiate it by hand.
     */
    protected LceViewState<M, V> viewState;

    /**
     * A flag that indicates if the viewstate tires to restore the view right now.
     */
    private boolean restoringViewState = false;


    public MvpLceViewStateFrameLayout(Context context) {
        super(context);
    }

    public MvpLceViewStateFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MvpLceViewStateFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MvpLceViewStateFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Create the view state object of this class
     */
    public abstract LceViewState<M, V> createViewState();

    @Override
    protected ViewGroupMvpDelegate<V, P> getMvpDelegate() {
        if (mvpDelegate == null) {
            mvpDelegate = new ViewGroupMvpViewStateDelegateImpl<V, P>(this);
        }
        return mvpDelegate;
    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected Parcelable onSaveInstanceState() {
        return ((ViewGroupMvpViewStateDelegateImpl) getMvpDelegate()).onSaveInstanceState();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        ((ViewGroupMvpViewStateDelegateImpl) getMvpDelegate()).onRestoreInstanceState(state);
    }

    @Override
    public LceViewState getViewState() {
        return viewState;
    }

    @Override
    public void setViewState(ViewState<V> viewState) {
        this.viewState = (LceViewState<M, V>) viewState;
    }

    @Override
    public void setRestoringViewState(boolean retstoringViewState) {
        this.restoringViewState = retstoringViewState;
    }

    @Override
    public boolean isRestoringViewState() {
        return restoringViewState;
    }

    @Override
    public void onViewStateInstanceRestored(boolean instanceStateRetained) {
        // can be overridden in subclass
    }

    @Override
    public Parcelable superOnSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    public void superOnRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    public void showContent() {
        super.showContent();
        viewState.setStateShowContent(getData());
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        super.showError(e, pullToRefresh);
        viewState.setStateShowError(e, pullToRefresh);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        super.showLoading(pullToRefresh);
        viewState.setStateShowLoading(pullToRefresh);
    }

    @Override
    public void onNewViewStateInstance() {
        loadData(false);
    }

    @Override
    protected void showLightError(String msg) {
        if (isRestoringViewState()) {
            return; // Do not display toast again while restoring viewstate
        }
        super.showLightError(msg);
    }

    /**
     * Get the data that has been set before in {@link #setData(Object)}
     * <p>
     * <b>It's necessary to return the same data as set before to ensure that {@link ViewState} works
     * correctly</b>
     * </p>
     *
     * @return The data
     */
    public abstract M getData();
}
