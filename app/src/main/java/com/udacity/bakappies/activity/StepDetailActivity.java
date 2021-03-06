package com.udacity.bakappies.activity;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.udacity.bakappies.R;
import com.udacity.bakappies.adapter.StepPagerAdapter;
import com.udacity.bakappies.common.BakappiesConstants;
import com.udacity.bakappies.data.BakappiesContract;
import com.udacity.bakappies.model.Step;
import com.udacity.bakappies.util.StepLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by radsen on 5/12/17.
 */

public class StepDetailActivity extends PlayerActivity implements
        LoaderManager.LoaderCallbacks<List<Step>>, View.OnClickListener,
        ViewPager.OnPageChangeListener {

    private static final String TAG = StepDetailActivity.class.getSimpleName();

    @Nullable @BindView(R.id.vp_steps)
    ViewPager vpSteps;

    @Nullable @BindView(R.id.btn_prev)
    ImageView btnPrev;

    @Nullable @BindView(R.id.btn_next)
    ImageView btnNext;

    private StepPagerAdapter mStepPagerAdapter;
    private int recipeId;
    private int stepNumber;
    private String recipeName;
    private boolean isLandscape;

    @Nullable @BindView(R.id.exo_player)
    SimpleExoPlayerView playerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        switch (getResources().getConfiguration().orientation){
            case Configuration.ORIENTATION_LANDSCAPE:
                isLandscape = true;
                Log.d(TAG, "Landscape!!!");
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                isLandscape = false;
                Log.d(TAG, "Portrait!!!");
                break;
            default:
                Log.d(TAG, "WTF!!!");
        }

        if(isLandscape){
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_steps);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            recipeName = bundle.getString(BakappiesConstants.RECIPE_NAME_KEY);
            getSupportActionBar().setTitle(recipeName);
            recipeId = bundle.getInt(BakappiesConstants.RECIPE_ID_KEY);
            stepNumber = bundle.getInt(BakappiesConstants.STEP_NUMBER_KEY, 0);
        }

        if(savedInstanceState != null){
            stepNumber = savedInstanceState.getInt(BakappiesConstants.STEP_NUMBER_KEY, 0);
        }

        if(!isLandscape){
            mStepPagerAdapter = new StepPagerAdapter(getSupportFragmentManager(),  null);
            vpSteps.setAdapter(mStepPagerAdapter);
            vpSteps.addOnPageChangeListener(this);
            setNavVisibility(stepNumber);

            btnPrev.setOnClickListener(this);
            btnNext.setOnClickListener(this);

        }

        getSupportLoaderManager()
                .restartLoader(StepLoader.STEP_LOADER, null, this)
                .forceLoad();
    }

    @Override
    public Loader<List<Step>> onCreateLoader(int id, Bundle args) {
        showProgress();

        StepLoader loader = null;
        switch (id){
            case StepLoader.STEP_LOADER:
                loader = new StepLoader(this, BakappiesContract.buildUriStep(recipeId));
                break;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Step>> loader, List<Step> data) {
        if(data == null || data.size() == 0){
            return;
        }

        if(!isLandscape){
            mStepPagerAdapter.swap(data);
            vpSteps.setCurrentItem(stepNumber, false);
        } else {
            Uri videoUri = Uri.parse(data.get(stepNumber).getVideoURL());
            playerView.setPlayer(getExoPlayer());
            preparePlayer(videoUri);
        }

        hideProgress();
    }

    @Override
    public void onLoaderReset(Loader<List<Step>> loader) {
        if(!isLandscape){
            mStepPagerAdapter.swap(null);
        }

        hideProgress();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BakappiesConstants.STEP_NUMBER_KEY, stepNumber);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_prev:
                stepNumber = vpSteps.getCurrentItem() - 1;
                break;
            case R.id.btn_next:
                stepNumber = vpSteps.getCurrentItem() + 1;
                break;
        }

        vpSteps.setCurrentItem(stepNumber, true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        setNavVisibility(position);
    }

    private void setNavVisibility(int position) {
        if(position == 0){
            btnPrev.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
        } else if (position == vpSteps.getAdapter().getCount() - 1) {
            btnPrev.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
        } else {
            btnPrev.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}