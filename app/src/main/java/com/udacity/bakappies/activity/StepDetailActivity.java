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

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.udacity.bakappies.R;
import com.udacity.bakappies.util.VideoPlayer;
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

public class StepDetailActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<List<Step>>, View.OnClickListener,
        ViewPager.OnPageChangeListener, ExoPlayer.EventListener {

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
    private VideoPlayer videoPlayer;

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

        videoPlayer = new VideoPlayer(this, playerView, this);

        if(savedInstanceState != null){
            long position = savedInstanceState.getLong(VideoPlayer.POSITION_KEY);
            videoPlayer.setPosition(position);
        }

        setContentView(R.layout.activity_steps);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            recipeName = bundle.getString(BakappiesConstants.RECIPE_NAME_KEY);
            getSupportActionBar().setTitle(recipeName);
            recipeId = bundle.getInt(BakappiesConstants.RECIPE_ID_KEY);
            stepNumber = bundle.getInt(BakappiesConstants.STEP_NUMBER_KEY);
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
    protected void onResume() {
        super.onResume();
        if(isLandscape){
            videoPlayer.initializePlayer(playerView);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isLandscape){
            videoPlayer.releasePlayer();
        }
    }

    @Override
    public Loader<List<Step>> onCreateLoader(int id, Bundle args) {
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
            videoPlayer.preparePlayer(videoUri);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Step>> loader) {
        if(!isLandscape){
            mStepPagerAdapter.swap(null);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_prev:
                vpSteps.setCurrentItem(vpSteps.getCurrentItem() - 1, true);
                break;
            case R.id.btn_next:
                vpSteps.setCurrentItem(vpSteps.getCurrentItem() + 1, true);
                break;
        }
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

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        Log.d(TAG, "onTimelineChanged");
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Log.d(TAG, "onTracksChanged");
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        Log.d(TAG, "onLoadingChanged " + isLoading);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.d(TAG, "onPlayerStateChanged");
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.d(TAG, "onPlayerError");
    }

    @Override
    public void onPositionDiscontinuity() {
        Log.d(TAG, "onPositionDiscontinuity");
    }
}
