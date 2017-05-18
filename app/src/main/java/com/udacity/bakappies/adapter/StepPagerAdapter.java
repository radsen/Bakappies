package com.udacity.bakappies.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.udacity.bakappies.fragment.FragmentStepDetail;
import com.udacity.bakappies.model.Step;

import java.util.List;

/**
 * Created by radsen on 5/13/17.
 */

public class StepPagerAdapter extends FragmentStatePagerAdapter {
    private List<Step> steps;

    public StepPagerAdapter(FragmentManager fragmentManager, List<Step> steps) {
        super(fragmentManager);
        this.steps = steps;
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentStepDetail.newInstance(steps.get(position));
    }

    @Override
    public int getCount() {
        if(steps == null){
            return 0;
        }

        return steps.size();
    }

    public void swap(List<Step> data) {
        if(data != null){
            steps = data;
        }

        notifyDataSetChanged();
    }
}
