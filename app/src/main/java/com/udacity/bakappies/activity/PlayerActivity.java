package com.udacity.bakappies.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.udacity.bakappies.R;

/**
 * Created by radsen on 5/27/17.
 */

public class PlayerActivity extends BaseActivity implements ExoPlayer.EventListener {

    private static final String TAG = PlayerActivity.class.getSimpleName();

    private SimpleExoPlayer mExoPlayer;
    private int resumeWindow;
    private long resumePosition;
    private Handler mediaHandler;

    private static BandwidthMeter sBandwithMeter = new DefaultBandwidthMeter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaHandler = new Handler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || mExoPlayer == null)) {
            initializePlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    public void initializePlayer(){
        if(mExoPlayer == null){

            // Create an instance of the ExoPlayer.
            TrackSelection.Factory videoStreamTrackSelector =
                    new AdaptiveTrackSelection.Factory(sBandwithMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoStreamTrackSelector);

            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

            mExoPlayer.addListener(this);
        }
    }

    public void releasePlayer(){
        if(mExoPlayer == null){
            return;
        }

        mExoPlayer.stop();

        resumeWindow = mExoPlayer.getCurrentWindowIndex();
        resumePosition = mExoPlayer.isCurrentWindowSeekable() ? Math.max(0,
                mExoPlayer.getCurrentPosition())
                : C.TIME_UNSET;

        mExoPlayer.release();
        mExoPlayer = null;
    }

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
        switch (playbackState){
            case ExoPlayer.STATE_READY:
                Log.d(TAG, "Ready");
                break;
            case ExoPlayer.STATE_BUFFERING:
                Log.d(TAG, "Buffering");
                break;
            case ExoPlayer.STATE_IDLE:
                Log.d(TAG, "Idle");
                break;
            case ExoPlayer.STATE_ENDED:
                Log.d(TAG, "Ended");
                mExoPlayer.seekTo(0);
                mExoPlayer.setPlayWhenReady(false);
                break;
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.d(TAG, "onPlayerError");
    }

    @Override
    public void onPositionDiscontinuity() {
        Log.d(TAG, "onPositionDiscontinuity");
    }

    public void preparePlayer(Uri uri){
        Log.d(TAG, uri.toString());
        DefaultDataSourceFactory dataSource = new DefaultDataSourceFactory(this,
                getString(R.string.app_name));
        ExtractorsFactory extractorFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(uri, dataSource, extractorFactory,
                mediaHandler, null);

        boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
        if (haveResumePosition) {
            mExoPlayer.seekTo(resumeWindow, resumePosition);
        }
        mExoPlayer.prepare(mediaSource, !haveResumePosition, false);

    }

    public SimpleExoPlayer getExoPlayer() {
        return mExoPlayer;
    }
}
