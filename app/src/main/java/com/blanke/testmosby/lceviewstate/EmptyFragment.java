package com.blanke.testmosby.lceviewstate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blanke.testmosby.R;
import com.orhanobut.logger.Logger;

/**
 * Created by asus on 2016/9/20.
 */
public class EmptyFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        Logger.d("EmptyFragment#onCreateView " + this);

        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logger.d("EmptyFragment#onActivityCreated " + this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("EmptyFragment#onDestroy " + this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.d("EmptyFragment#onDestroyView " + this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logger.d("EmptyFragment#onDetach " + this);
    }
}
