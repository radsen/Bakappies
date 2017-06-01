package com.udacity.bakappies.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.bakappies.activity.BaseActivity;

/**
 * Created by radsen on 5/8/17.
 */

public class BaseFragment extends Fragment {

    private BaseActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof BaseActivity){
            activity = (BaseActivity) context;
        } else {
            throw new ClassCastException("The activity does not extends " +
                    BaseActivity.class.getSimpleName());
        }
    }

    public void showProgress(){
        activity.showProgress();
    }

    public void hideProgress(){
        activity.hideProgress();
    }

}
