package com.udacity.bakappies.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.udacity.bakappies.R;
import com.udacity.bakappies.common.BakappiesConstants;
import com.udacity.bakappies.fragment.FragmentRecipe;
import com.udacity.bakappies.fragment.FragmentStepDetail;
import com.udacity.bakappies.model.Recipe;
import com.udacity.bakappies.model.Step;

/**
 * Created by radsen on 5/10/17.
 */

public class RecipeActivity extends PlayerActivity implements FragmentRecipe.OnRecipeListener {

    private static final String TAG = RecipeActivity.class.getSimpleName();

    private FragmentRecipe fragmentRecipe;
    private FragmentStepDetail fragmentStep;
    private Recipe recipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Bundle bundle = getIntent().getExtras();
        recipe = null;
        if(bundle != null){
            Log.d(TAG, "bundle not null");
            recipe = bundle.getParcelable(BakappiesConstants.RECIPE_KEY);
        } else {
            Log.d(TAG, "bundle null");
        }

        getSupportActionBar().setTitle(recipe.getName());

        if(getResources().getBoolean(R.bool.isTablet)){
            fragmentRecipe = FragmentRecipe.newInstance(recipe);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragmentRecipe)
                    .commit();

            fragmentStep = new FragmentStepDetail();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail, fragmentStep)
                    .commit();

        } else {
            fragmentRecipe = FragmentRecipe.newInstance(recipe);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragmentRecipe)
                    .commit();
        }

    }

    @Override
    public void onRecipeSelected(Step step) {
        if(fragmentStep != null){
            fragmentStep.load(step);
        }
    }
}
