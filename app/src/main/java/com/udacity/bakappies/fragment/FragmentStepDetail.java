package com.udacity.bakappies.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.udacity.bakappies.R;
import com.udacity.bakappies.activity.PlayerActivity;
import com.udacity.bakappies.common.BakappiesConstants;
import com.udacity.bakappies.model.Step;
import com.udacity.bakappies.util.BindingUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by radsen on 5/8/17.
 */

public class FragmentStepDetail extends BaseFragment {

    public static final String TAG = FragmentStepDetail.class.getSimpleName();

    @Nullable @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.exo_player)
    SimpleExoPlayerView playerView;

    @BindView(R.id.iv_no_content)
    ImageView ivNoContent;

    @Nullable @BindView(R.id.tv_full_description)
    TextView tvFullDesc;

    private Unbinder unbinder;
    private Step step;

    private PlayerActivity activity;

    public static FragmentStepDetail newInstance(Step step) {
        FragmentStepDetail stepDetail = new FragmentStepDetail();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BakappiesConstants.STEP_KEY, step);
        stepDetail.setArguments(bundle);
        return stepDetail;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            step = getArguments().getParcelable(BakappiesConstants.STEP_KEY);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof PlayerActivity){
            activity = (PlayerActivity) context;
        } else {
            throw new ClassCastException("The context must extend PlayerActivity");
        }
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getUserVisibleHint()){
            load(step);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void load(Step step) {
        Log.d(TAG, "load");
        if(step != null){
            tvTitle.setText(step.getShortDescription());
            tvFullDesc.setText(step.getDescription());

            if(!TextUtils.isEmpty(step.getVideoURL())){
                playerView.setPlayer(activity.getExoPlayer());
                playerView.setVisibility(View.VISIBLE);
                ivNoContent.setVisibility(View.GONE);
                activity.preparePlayer(Uri.parse(step.getVideoURL()));
            } else {
                playerView.setVisibility(View.GONE);
                playerView.setPlayer(null);
                ivNoContent.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser && step != null){
            load(step);
        }
    }
}
