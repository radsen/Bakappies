package com.udacity.bakappies.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.udacity.bakappies.R;
import com.udacity.bakappies.common.BakappiesConstants;
import com.udacity.bakappies.fragment.FragmentRecipe;
import com.udacity.bakappies.model.Recipe;

/**
 * Created by radsen on 5/10/17.
 */

public class RecipeActivity extends BaseActivity {

    private FragmentRecipe fragmentRecipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        Recipe recipe = null;
        if(bundle != null){
            recipe = bundle.getParcelable(BakappiesConstants.RECIPE_KEY);
        }

        getSupportActionBar().setTitle(recipe.getName());

        fragmentRecipe = FragmentRecipe.newInstance(recipe);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragmentRecipe)
                .commit();
    }
}
