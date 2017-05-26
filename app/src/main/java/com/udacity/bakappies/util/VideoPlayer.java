package com.udacity.bakappies.util;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.udacity.bakappies.R;

/**
 * Created by radsen on 5/23/17.
 */

public class VideoPlayer {

    private static final String TAG = VideoPlayer.class.getSimpleName();
    public static final String POSITION_KEY = "com.bakappies.video.player.key";

    private final Context mContext;
    private final ExoPlayer.EventListener mEventListener;
    private SimpleExoPlayerView player;
    private SimpleExoPlayer mExoPlayer;
    private long position;

    public VideoPlayer(Context context, SimpleExoPlayerView view,
                       ExoPlayer.EventListener eventListener){
        this.mContext = context;
        this.player = view;
        this.mEventListener = eventListener;
    }

    public void initializePlayer() {
        if(mExoPlayer == null){

            // Create an instance of the ExoPlayer.
            BandwidthMeter bandWithMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoStreamTrackSelector =
                    new AdaptiveTrackSelection.Factory(bandWithMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoStreamTrackSelector);

            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
            player.setPlayer(mExoPlayer);

            mExoPlayer.addListener(mEventListener);
        }
    }

    public void initializePlayer(SimpleExoPlayerView player) {
        if(this.player == null){
            this.player = player;
        }

        initializePlayer();
    }

    public void preparePlayer(Uri uri) {
        // Prepare the media source
        if(!TextUtils.isEmpty(uri.toString())){
            Log.d(TAG, uri.toString());
            DefaultDataSourceFactory dataSource = new DefaultDataSourceFactory(mContext,
                    mContext.getString(R.string.app_name));
            ExtractorsFactory extractorFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(uri, dataSource, extractorFactory, null, null);
            mExoPlayer.prepare(mediaSource);
        }
    }

    public long getPosition(){
        return mExoPlayer.getCurrentPosition();
    }

    public void releasePlayer(){
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    public void setPosition(long position) {
        mExoPlayer.seekTo(position);
    }
}
