package com.udacity.bakappies.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.udacity.bakappies.R;
import com.udacity.bakappies.util.VideoPlayer;
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
    private Step step;
    private VideoPlayer videoPlayer;

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
        videoPlayer = new VideoPlayer(getContext(), player, this);
        videoPlayer.initializePlayer();

        if(getArguments() != null){
            step = getArguments().getParcelable(BakappiesConstants.STEP_KEY);
            load(step);
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
        Log.d(TAG, "onResume");
        videoPlayer.initializePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        videoPlayer.releasePlayer();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putLong(VideoPlayer.POSITION_KEY, videoPlayer.getPosition());
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
        Log.d(TAG, "onLoadingChanged");
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

    public void load(Step step) {
        if(step != null){
            tvTitle.setText(step.getShortDescription());
            BindingUtils.loadImage(ivThumbnail, step.getThumbnailURL(), R.drawable.ic_oven);
            tvFullDesc.setText(step.getDescription());

            Uri videoUri = Uri.parse(step.getVideoURL());
            videoPlayer.preparePlayer(videoUri);
        }
    }
}
