package com.udacity.bakappies.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.android.exoplayer2.source.TrackGroup;
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
import com.udacity.bakappies.R;
import com.udacity.bakappies.common.BakappiesConstants;
import com.udacity.bakappies.model.Step;
import com.udacity.bakappies.util.BindingUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by radsen on 5/8/17.
 */

public class FragmentStepDetail extends BaseFragment implements ExoPlayer.EventListener {

    private static final String TAG = FragmentStepDetail.class.getSimpleName();

    @Nullable @BindView(R.id.iv_thumbnail)
    ImageView ivThumbnail;

    @Nullable @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.exo_player)
    SimpleExoPlayerView player;

    @Nullable @BindView(R.id.tv_full_description)
    TextView tvFullDesc;

    private Unbinder unbinder;
    private SimpleExoPlayer mExoPlayer;
    private Step step;

    public static FragmentStepDetail newInstance(Step step) {
        FragmentStepDetail stepDetail = new FragmentStepDetail();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BakappiesConstants.STEP_KEY, step);
        stepDetail.setArguments(bundle);
        return stepDetail;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        step = getArguments().getParcelable(BakappiesConstants.STEP_KEY);
        if(step != null){
            tvTitle.setText(step.getShortDescription());
            initializePlayer();
            BindingUtils.loadImage(ivThumbnail, step.getThumbnailURL(), R.drawable.ic_oven);
            tvFullDesc.setText(step.getDescription());
        }

    }

    private void initializePlayer() {
        if(mExoPlayer == null){

            // Create an instance of the ExoPlayer.
            BandwidthMeter bandWithMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoStreamTrackSelector =
                    new AdaptiveTrackSelection.Factory(bandWithMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoStreamTrackSelector);

            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            player.setPlayer(mExoPlayer);

            mExoPlayer.addListener(this);

            // Prepare the media source
            Uri uri = Uri.parse(step.getVideoURL());
            if(!TextUtils.isEmpty(uri.toString())){
                Log.d(TAG, uri.toString());
                DefaultDataSourceFactory dataSource = new DefaultDataSourceFactory(getContext(),
                        getString(R.string.app_name));
                ExtractorsFactory extractorFactory = new DefaultExtractorsFactory();
                MediaSource mediaSource = new ExtractorMediaSource(uri, dataSource, extractorFactory, null, null);
                mExoPlayer.prepare(mediaSource);
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume " + step.getId());
        initializePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause " + step.getId());
        releasePlayer();
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }
}
