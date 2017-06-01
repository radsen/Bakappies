package com.udacity.bakappies.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.udacity.bakappies.R;

/**
 * Created by radsen on 5/10/17.
 */

public class BaseActivity extends AppCompatActivity {

    private LinearLayout pbLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pbLoading = (LinearLayout) findViewById(R.id.pb_loader);
    }

    public void showProgress() {
        if(pbLoading.getVisibility() != View.VISIBLE){
            pbLoading.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress() {
        pbLoading.setVisibility(View.GONE);
    }
}
