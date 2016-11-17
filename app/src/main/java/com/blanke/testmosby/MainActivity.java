package com.blanke.testmosby;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.blanke.lib.ProgressButton;
import com.blanke.testmosby.lce_layout.NewsListFrameLayout;
import com.blanke.testmosby.lceviewstate.NewsListFragment;
import com.orhanobut.logger.Logger;


public class MainActivity extends AppCompatActivity {
    private FrameLayout mainFramelayout;
    private NewsListFrameLayout newslistlayout;

    private ProgressButton progressbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.init().methodCount(10);
        Logger.d("MainActivity#onCreate");
        mainFramelayout = (FrameLayout) findViewById(R.id.main_framelayout);
        FragmentManager frgmentManager = getSupportFragmentManager();
        Fragment f = frgmentManager.findFragmentByTag("tag_fragment");
        if (f == null) {
//            f = new EmptyFragment();
            f = new NewsListFragment();
        }
//        frgmentManager.beginTransaction()
//                .replace(R.id.main_framelayout, f, "tag_fragment")
//                .commit();
//        if (savedInstanceState != null) {
//            Parcelable data = savedInstanceState.getParcelable("data");
//            if (data != null) {
//                newslistlayout.onRestoreInstanceState(data);
//            }
//        }

        progressbutton = (ProgressButton) findViewById(R.id.progressbutton);
        progressbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressbutton.setLoading(false);
                    }
                }, 3000);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Logger.d("main#onSaveInstanceState");
        super.onSaveInstanceState(outState);
//        Parcelable data = newslistlayout.onSaveInstanceState();
//        outState.putParcelable("data", data);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Logger.d("main#onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
//        if (savedInstanceState != null) {
//            Parcelable data = savedInstanceState.getParcelable("data");
//            if (data != null) {
//                newslistlayout.onRestoreInstanceState(data);
//            }
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d("MainActivity#onDestroy");
    }
}
